package com.labibkamran.nustfruta;

import static java.security.AccessController.getContext;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labibkamran.nustfruta.Fragments.CongratsFragment;
import com.labibkamran.nustfruta.Model.NotificationModel;
import com.labibkamran.nustfruta.Model.OrderDetails;
import com.labibkamran.nustfruta.databinding.ActivityPayOutBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class PayOutActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String name;
    private String address;
    private String phone;
    private String totalAmount;
    private ArrayList<String> foodItemName;
    private ArrayList<String> foodItemPrice;
    private ArrayList<String> foodItemDescription;
    private ArrayList<String> foodItemImage;
    private ArrayList<Integer> foodItemQuantity;

    private DatabaseReference databaseReference;
    private String userId;
    ActivityPayOutBinding binding;
    private ArrayList<String> location;

    public PayOutActivity(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPayOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        location = new ArrayList<>(Arrays.asList(
                "Ammar hostel", "Amna Hostel", "Ayesha hostel", "Attar Hostel",
                "Beruni Hostel", "Fatima hostel", "Ghazali hostel", "Johar hostel",
                "Khadija hostel", "Liaquat Hostel", "Rahmat Hostel", "Razi hostel",
                "Rumi Hostel", "Zakriya Hostel", "Zainab hostel"
        ));
        Collections.sort(location);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, location);
        binding.address.setAdapter(adapter);
        
        //set user data

        Intent intent = getIntent();
        foodItemName = intent.getStringArrayListExtra("foodItemName");
        foodItemPrice = intent.getStringArrayListExtra("foodItemPrice");
        foodItemDescription = intent.getStringArrayListExtra("foodItemDescription");
        foodItemImage = intent.getStringArrayListExtra("foodItemImage");
        foodItemQuantity = intent.getIntegerArrayListExtra("foodItemQuantity");
        setUserData();
        totalAmount = String.valueOf(calculateTotalAmount());
        binding.amount.setEnabled(false);
        binding.amount.setText("Rs " + totalAmount);
        binding.placeMyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               
                if(checkCredentials()){
                    placeorder();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    addNotification(userId, "Your order has been placed", R.drawable.congrats);
                }

            }
        });
        binding.backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void placeorder() {
        userId = auth.getCurrentUser().getUid();
        Long time = System.currentTimeMillis();
        String itemPushKey = databaseReference.child("OrderDetails").push().getKey();
        OrderDetails orderDetails = new OrderDetails(
                userId, // userUid
                name, // userName
                foodItemName, // foodNames
                foodItemImage, // foodImages
                foodItemPrice, // foodPrices
                foodItemQuantity, // foodQuantities
                address, // address
                totalAmount, // totalPrice
                phone, // phoneNumber
                false, // orderAccepted
                false, // paymentReceived
                itemPushKey, // itemPushKey
                time // currentTime
        );
        assert itemPushKey != null;
        DatabaseReference orderReference = databaseReference.child("OrderDetails").child(itemPushKey);
        orderReference.setValue(orderDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                CongratsFragment congratsFragment = new CongratsFragment();
                congratsFragment.show(getSupportFragmentManager(), "CongratsFragment");
                // removing item from cart
                removeItemFromCart();
                // adding item to history
                addOrderToHistory(orderDetails);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PayOutActivity.this, "Order Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOrderToHistory(OrderDetails orderDetails) {
        databaseReference.child("user").child(userId).child("BuyHistory").child(orderDetails.getItemPushKey()).setValue(orderDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });


    }

    private void removeItemFromCart() {
        DatabaseReference cartItemsReference = databaseReference.child("user").child(userId).child("CartItems");
        cartItemsReference.removeValue();
    }

    private int calculateTotalAmount() {
        int totalAmount = 0;
        for (int i = 0; i < foodItemPrice.size(); i++) {
            String price = foodItemPrice.get(i);
            char lastChar = price.charAt(price.length() - 1);
            int priceIntValue;
            if (lastChar == '$') {
                priceIntValue = Integer.parseInt(price.substring(0, price.length() - 1));
            } else {
                priceIntValue = Integer.parseInt(price);
            }
            int quantity = foodItemQuantity.get(i);
            totalAmount += priceIntValue * quantity;
        }
        return totalAmount;
    }


    private void setUserData() {
        FirebaseUser user= auth.getCurrentUser();
        if(user !=null){
            userId = user.getUid();
            DatabaseReference userReference = databaseReference.child("user").child(userId);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String names = snapshot.child("name").getValue(String.class);
                        if (names == null) {
                            names = "";
                        }

                        String addresses = snapshot.child("address").getValue(String.class);
                        if (addresses == null) {
                            addresses = "";
                        }

                        String phones = snapshot.child("phone").getValue(String.class);
                        if (phones == null) {
                            phones = "";
                        }

                        binding.name.setText(names);
                        binding.address.setText(addresses);
                        binding.phone.setText(phones);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
    private boolean checkCredentials() {
        name = String.valueOf(binding.name.getText());
        phone = String.valueOf(binding.phone.getText());
        address = String.valueOf(binding.address.getText());
        if (name.isEmpty())
            showError(binding.name, "please fill this field");
        else if (address.isEmpty())
            showError(binding.address, "please fill this field");
        else if (!(location.contains(address)))
            showError(binding.address, "please select address from list");
        else if (phone.isEmpty())
            showError(binding.phone, "please fill this field");
        else if (!(phone.matches("^03\\d{9}$")))
            showError(binding.phone, "enter a valid phone number");
        
        else {
            return true;
        }
        return false;
    }

    private void showError(EditText input, String error) {
        input.setError(error);
        input.requestFocus();
    }
    private void addNotification(String userId, String message, int imageResourceId) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("notifications");
        String notificationId = notificationRef.push().getKey();
        NotificationModel notification = new NotificationModel(message, imageResourceId);
        notification.setUserId(userId); // Set the user ID
        notificationRef.child(notificationId).setValue(notification);
    }
}