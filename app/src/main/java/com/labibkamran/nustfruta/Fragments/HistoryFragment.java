package com.labibkamran.nustfruta.Fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.labibkamran.nustfruta.Model.OrderDetails;
import com.labibkamran.nustfruta.R;
import com.labibkamran.nustfruta.RecentOrderItems;
import com.labibkamran.nustfruta.adapter.BuyAgainAdapter;
import com.labibkamran.nustfruta.databinding.FragmentHistoryBinding;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private BuyAgainAdapter buyAgainAdapter;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private String userId;
    private ArrayList<OrderDetails> listOfOrderItem = new ArrayList<>(); // Initialize here

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Retrieve and display user order history
        retrieveBuyHistory();

        binding.recentbuyitem.setOnClickListener(v -> seeItemRecentBuyItems());

        return view;
    }

    private void seeItemRecentBuyItems() {
        if (!listOfOrderItem.isEmpty()) {
            OrderDetails recentBuy = listOfOrderItem.get(0);
            Intent intent = new Intent(getContext(), RecentOrderItems.class);
            // You can put extra data into the intent
            intent.putExtra("RecentBuyOrderItem", recentBuy);
            // Then you can start the activity with the intent
            startActivity(intent);
        }
    }

    private void retrieveBuyHistory() {
        binding.recentbuyitem.setVisibility(View.INVISIBLE);
        userId = auth.getCurrentUser().getUid();
        DatabaseReference buyItemReference = database.child("user").child(userId).child("BuyHistory");
        Query shoringquery = buyItemReference.orderByChild("currentTime");

        shoringquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot buysnapshot : snapshot.getChildren()) {
                    OrderDetails buyHistoryItem = buysnapshot.getValue(OrderDetails.class);
                    listOfOrderItem.add(buyHistoryItem);
                }
                Collections.reverse(listOfOrderItem);
                if (!listOfOrderItem.isEmpty()) {
                    setDataInRecentBuyItem();
                    setPreviousBuyItem();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void setDataInRecentBuyItem() {
        binding.recentbuyitem.setVisibility(View.VISIBLE);
        OrderDetails recentOrderItem = listOfOrderItem.get(0);

        if (recentOrderItem != null) {
            binding.buyAgainFoodName.setText(recentOrderItem.getFoodNames().get(0));
            binding.againFoodPrice.setText(recentOrderItem.getFoodPrices().get(0));
            String image = recentOrderItem.getFoodImages().get(0);
            Uri uri = Uri.parse(image);
            Glide.with(requireContext()).load(uri).into(binding.buyAgainFoodImage);
        }

        String userIdOfClickedItem = recentOrderItem.getUserUid();
        String pushkeyofclickeditem = recentOrderItem.getItemPushKey();

        DatabaseReference userBuyHistoryRef = database.child("user").child(userIdOfClickedItem).child("BuyHistory").child(pushkeyofclickeditem);

        userBuyHistoryRef.child("orderDelivered").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean deliveryStatus = snapshot.getValue(Boolean.class);
                if (Boolean.TRUE.equals(deliveryStatus)) {
                    int color = ContextCompat.getColor(getContext(), R.color.start_color);
                    binding.orderStatus.setCardBackgroundColor(color);
                    binding.receivedButton.setText("Delivered");
                    binding.receivedButton.setBackgroundTintList(ColorStateList.valueOf(color));
                } else {
                    userBuyHistoryRef.child("orderAccepted").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Boolean acceptStatus = snapshot.getValue(Boolean.class);
                            if (Boolean.TRUE.equals(acceptStatus)) {
                                int color = ContextCompat.getColor(getContext(), R.color.blue);
                                binding.orderStatus.setCardBackgroundColor(color);
                                binding.receivedButton.setText("Accepted");
                                binding.receivedButton.setBackgroundTintList(ColorStateList.valueOf(color));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle possible errors.
                        }
                    });
                    // Check if the order was rejected
                    userBuyHistoryRef.child("orderRejected").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Boolean rejectStatus = snapshot.getValue(Boolean.class);
                            if (Boolean.TRUE.equals(rejectStatus)) {
                                int color = ContextCompat.getColor(getContext(), R.color.red);
                                binding.orderStatus.setCardBackgroundColor(color);
                                binding.receivedButton.setText("Rejected");
                                binding.receivedButton.setBackgroundTintList(ColorStateList.valueOf(color));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle possible errors.
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });

        binding.receivedButton.setOnClickListener(v -> {
            userBuyHistoryRef.child("orderDelivered").setValue(true);
            userBuyHistoryRef.child("orderAccepted").setValue(true);
            int color = ContextCompat.getColor(getContext(), R.color.start_color);
            binding.orderStatus.setCardBackgroundColor(color);
            binding.receivedButton.setText("Delivered");
            binding.receivedButton.setBackgroundTintList(ColorStateList.valueOf(color));

            DatabaseReference completedOrdersRef = database.child("CompletedOrders").child(pushkeyofclickeditem);
            completedOrdersRef.child("orderDelivered").setValue(true);
            completedOrdersRef.child("orderAccepted").setValue(true);
        });
    }

    private void setPreviousBuyItem() {
        ArrayList<String> buyAgainFoodName = new ArrayList<>();
        ArrayList<String> buyAgainFoodPrice = new ArrayList<>();
        ArrayList<String> buyAgainFoodImage = new ArrayList<>();

        for (int i = 1; i < listOfOrderItem.size(); i++) {
            OrderDetails orderItem = listOfOrderItem.get(i);
            if (orderItem.getFoodNames() != null && !orderItem.getFoodNames().isEmpty()) {
                buyAgainFoodName.add(orderItem.getFoodNames().get(0));
            }
            if (orderItem.getFoodPrices() != null && !orderItem.getFoodPrices().isEmpty()) {
                buyAgainFoodPrice.add(orderItem.getFoodPrices().get(0));
            }
            if (orderItem.getFoodImages() != null && !orderItem.getFoodImages().isEmpty()) {
                buyAgainFoodImage.add(orderItem.getFoodImages().get(0));
            }
        }

        RecyclerView rv = binding.buyAgainRecyclerView;
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        buyAgainAdapter = new BuyAgainAdapter(buyAgainFoodName, buyAgainFoodPrice, buyAgainFoodImage, requireContext());
        rv.setAdapter(buyAgainAdapter); // Set the adapter to the RecyclerView
    }
}
