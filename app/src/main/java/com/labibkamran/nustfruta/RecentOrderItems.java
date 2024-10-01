package com.labibkamran.nustfruta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.labibkamran.nustfruta.Model.NotificationModel;
import com.labibkamran.nustfruta.Model.OrderDetails;
import com.labibkamran.nustfruta.adapter.RecentBuyAdapter;
import com.labibkamran.nustfruta.databinding.ActivityRecentOrderItemsBinding;
import java.util.ArrayList;

public class RecentOrderItems extends AppCompatActivity {

    ActivityRecentOrderItemsBinding binding;
    private DatabaseReference database;
    private OrderDetails recentOrderItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRecentOrderItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance().getReference();

        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        OrderDetails recentOrderItems = intent.getParcelableExtra("RecentBuyOrderItem");
        if (recentOrderItems != null) {
            recentOrderItem = recentOrderItems;
            ArrayList<String> allFoodNames = recentOrderItem.getFoodNames();
            ArrayList<String> allFoodImages = recentOrderItem.getFoodImages();
            ArrayList<String> allFoodPrices =  recentOrderItem.getFoodPrices();
            ArrayList<Integer> allFoodQuantities = recentOrderItem.getFoodQuantities();
            setAdapter(allFoodNames, allFoodImages, allFoodPrices, allFoodQuantities);
        }

        binding.cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfOrderAcceptedAndCancel();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                addNotification(userId,"Your order has been cancelled successfully", R.drawable.sademoji);
            }
        });
    }

    private void setAdapter(ArrayList<String> allFoodNames, ArrayList<String> allFoodImages, ArrayList<String> allFoodPrices, ArrayList<Integer> allFoodQuantities) {
        RecyclerView rv = binding.recentRecyclerView;
        rv.setLayoutManager(new LinearLayoutManager(this));
        RecentBuyAdapter adapter = new RecentBuyAdapter(allFoodNames, allFoodImages, allFoodPrices, allFoodQuantities, this);
        rv.setAdapter(adapter);
    }

    private void checkIfOrderAcceptedAndCancel() {
        if (recentOrderItem != null) {
            String userIdOfClickedItem = recentOrderItem.getUserUid();
            String pushKeyOfClickedItem = recentOrderItem.getItemPushKey();

            // Reference to the specific order in the user's BuyHistory
            DatabaseReference userBuyHistoryRef = database.child("user").child(userIdOfClickedItem).child("BuyHistory").child(pushKeyOfClickedItem);

            // Check if the order has been accepted
            userBuyHistoryRef.child("orderAccepted").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Boolean orderAccepted = snapshot.getValue(Boolean.class);
                    if (orderAccepted != null && orderAccepted) {
                        // Order is accepted, do not allow cancellation
                        Toast.makeText(RecentOrderItems.this, "Order has been accepted and cannot be canceled.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Order is not accepted, proceed with cancellation
                        moveOrderToCanceledOrdersAndRemove(userBuyHistoryRef, pushKeyOfClickedItem);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle possible errors.
                    Toast.makeText(RecentOrderItems.this, "Failed to check order status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void moveOrderToCanceledOrdersAndRemove(DatabaseReference userBuyHistoryRef, String pushKeyOfClickedItem) {
        // Move the order data to the CanceledOrders node
        DatabaseReference canceledOrdersRef = database.child("CanceledOrders").child(pushKeyOfClickedItem);
        canceledOrdersRef.setValue(recentOrderItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove the order data from the user's BuyHistory
                userBuyHistoryRef.removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        // Remove the order data from the CompletedOrders if it exists
                        DatabaseReference completedOrdersRef = database.child("CompletedOrders").child(pushKeyOfClickedItem);
                        completedOrdersRef.removeValue().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                // Update UI or notify user
                                binding.cancelOrder.setText("Order Canceled");
                                binding.cancelOrder.setEnabled(false);

                                Toast.makeText(RecentOrderItems.this, "Order canceled successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RecentOrderItems.this, "Failed to remove order from completed orders.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(RecentOrderItems.this, "Failed to remove order from buy history.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(RecentOrderItems.this, "Failed to move order to canceled orders.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addNotification(String userId, String message, int imageResourceId) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("notifications");
        String notificationId = notificationRef.push().getKey();
        NotificationModel notification = new NotificationModel(message, imageResourceId);
        notification.setUserId(userId); // Set the user ID
        notificationRef.child(notificationId).setValue(notification);
    }

    // Example usage



}
