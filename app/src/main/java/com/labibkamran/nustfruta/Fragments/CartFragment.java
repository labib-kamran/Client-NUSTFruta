package com.labibkamran.nustfruta.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labibkamran.nustfruta.Model.CartItems;
import com.labibkamran.nustfruta.PayOutActivity;
import com.labibkamran.nustfruta.adapter.cartAdapter;
import com.labibkamran.nustfruta.databinding.FragmentCartBinding;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    FragmentCartBinding binding;
    String userId;
    private Context context;
    private ArrayList<String> cartItems;
    private ArrayList<String> cartItemPrice;
    private ArrayList<String> cartImage;
    private ArrayList<String> cartDescription;
    private FirebaseAuth auth;
    private Integer[] itemQuantities;
    private FirebaseDatabase database;
    DatabaseReference foodReference;

    public CartFragment() {
        cartItems = new ArrayList<>();
        cartItemPrice = new ArrayList<>();
        cartImage = new ArrayList<>();
        cartDescription = new ArrayList<>();
        itemQuantities = new Integer[10];
    }

    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        context = getActivity();
        retrieveCartItems();

        binding.proceedCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get order item details before proceeding
                getorderItemDetail();

            }
        });
        return view;
    }

    private void getorderItemDetail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            DatabaseReference orderIdreference = database.getReference().child("user").child(userId).child("CartItems");
            ArrayList<String> foodName = new ArrayList<>();
            ArrayList<String> foodPrice = new ArrayList<>();
            ArrayList<String> foodImage = new ArrayList<>();
            ArrayList<String> foodDescription = new ArrayList<>();
            // no of items
            ArrayList<Integer> foodQuantities = cartAdapter.getUpdatedItemQuantites();
            orderIdreference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot foodsnapshot : snapshot.getChildren()) {
                        // get the cart item to the respeditve list
                        CartItems orderItems = foodsnapshot.getValue(CartItems.class);
                        if (orderItems != null) {
                            // add items to the list
                            foodName.add(orderItems.getFoodname());
                            foodPrice.add(orderItems.getFoodprice());
                            foodDescription.add(orderItems.getFooddescription());
                            foodImage.add(orderItems.getFoodImage());
                        }
                    }
                    ordernow(foodName, foodPrice, foodDescription, foodImage, foodQuantities);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Order making failed. Please try again", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void ordernow(ArrayList<String> foodName,
                          ArrayList<String> foodPrice,
                          ArrayList<String> foodDescription,
                          ArrayList<String> foodImage,
                          ArrayList<Integer> foodQuantities) {
       // if (isAdded()) {
            Intent intent = new Intent(getContext(), PayOutActivity.class);
            intent.putStringArrayListExtra("foodItemName", foodName);
            intent.putStringArrayListExtra("foodItemPrice", foodPrice);
            intent.putStringArrayListExtra("foodItemDescription", foodDescription);
            intent.putStringArrayListExtra("foodItemImage", foodImage);
            intent.putIntegerArrayListExtra("foodItemQuantity", foodQuantities);
            startActivity(intent);

       // }
    }

    private void retrieveCartItems() {
        database = FirebaseDatabase.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            foodReference = database.getReference().child("user").child(userId).child("CartItems");
            foodReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int position = 0;
                    for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                        CartItems cartItems1 = foodSnapshot.getValue(CartItems.class);
                        if (cartItems1 != null) {
                            String foodName = cartItems1.getFoodname();
                            if (foodName != null) {
                                cartItems.add(foodName);
                            }
                            String foodPrice = cartItems1.getFoodprice();
                            if (foodPrice != null) {
                                cartItemPrice.add(foodPrice);
                            }
                            String foodDescription = cartItems1.getFooddescription();
                            if (foodDescription != null) {
                                cartDescription.add(foodDescription);
                            }
                            String foodImage = cartItems1.getFoodImage();
                            if (foodImage != null) {
                                cartImage.add(foodImage);
                            }
                            Integer foodQuantity = cartItems1.getFoodquantity();
                            if (foodQuantity != null) {
                                itemQuantities[position] = foodQuantity;
                            }
                            position++;
                        }
                    }
                    setAdapter();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Data fetching failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setAdapter() {
        cartAdapter adapter = new cartAdapter(getContext(), cartItems, cartItemPrice, cartDescription, cartImage, itemQuantities);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.cartRecyclerView.setLayoutManager(layoutManager);
        binding.cartRecyclerView.setAdapter(adapter);
    }
}
