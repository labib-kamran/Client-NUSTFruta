package com.labibkamran.nustfruta;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.labibkamran.nustfruta.Model.CartItems;
import com.labibkamran.nustfruta.databinding.ActivityDetailsAcitvityBinding;

public class DetailsAcitvity extends AppCompatActivity {

    private String foodname = null;
    private String foodprice = null;
    private String fooddescription = null;
    private String foodImage = null;
    FirebaseAuth auth = null;
    ActivityDetailsAcitvityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailsAcitvityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        foodname = getIntent().getStringExtra("MenuItemName");
        fooddescription = getIntent().getStringExtra("MenuItemDescription");
        foodprice = getIntent().getStringExtra("MenuItemPrice");
        fooddescription = getIntent().getStringExtra("MenuItemDescription");
        foodImage = getIntent().getStringExtra("MenuItemImage");

        binding.DetailFoodName.setText(foodname);
        binding.DetailDescription.setText(fooddescription);
        Glide.with(this).load(Uri.parse(foodImage)).into(binding.DetailFoodImage);
        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a cart item
                addItemToCart();
            }
        });
    }

    private void addItemToCart() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String userId = auth.getCurrentUser().getUid();
        CartItems cartItem = new CartItems(foodname,foodprice,fooddescription,foodImage,1);
    // save data to cartitem on firebase
        if (auth.getCurrentUser() != null) {
            // User is signed in.
            database.child("user").child(userId).child("CartItems").push().setValue(cartItem)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Display a success toast
                            Toast.makeText(getApplicationContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Display a failure toast
                            Toast.makeText(getApplicationContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            // No user is signed in.
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
        }



    }
}