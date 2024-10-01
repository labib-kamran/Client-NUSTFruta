package com.labibkamran.nustfruta.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labibkamran.nustfruta.databinding.CartItemBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.cartViewHolder> {
    CartItemBinding binding;
    private Context context;
    private ArrayList<String> cartItems;
    private ArrayList<String> cartItemPrice;
    private ArrayList<String> cartImage;
    private ArrayList<String> cartDescription;
    private FirebaseAuth auth;
    private static Integer[] itemQuantities;
    private FirebaseDatabase database;
    DatabaseReference cartItemsReference;

    public cartAdapter(Context context, ArrayList<String> cartItems, ArrayList<String> cartItemPrice,ArrayList<String> cartDescription ,ArrayList<String> cartImage,Integer[] itemQuantities) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartItemPrice = cartItemPrice;
        this.cartDescription = cartDescription;
        this.cartImage = cartImage;
        this.itemQuantities = itemQuantities;
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        String userId = auth.getCurrentUser().getUid();
        cartItemsReference = database.getReference("user").child(userId).child("CartItems");
    }

    public static ArrayList<Integer> getUpdatedItemQuantites() {

        // get updated quantities
        ArrayList<Integer> itemQuantity = new ArrayList<Integer>();
        itemQuantity.addAll(Arrays.asList(itemQuantities));
        return itemQuantity;
    }


    @NonNull
    @Override
    public cartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = CartItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new cartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull cartViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class cartViewHolder extends RecyclerView.ViewHolder {
        private CartItemBinding binding;

        public cartViewHolder(CartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            int quantity = itemQuantities[position];
            binding.cartItems.setText(cartItems.get(position));
            binding.cartItemPrice.setText(cartItemPrice.get(position));
            Uri uri = Uri.parse(cartImage.get(position));
            Glide.with(context).load(uri).into(binding.cartImage);
            binding.catItemQuantity.setText(Integer.toString(quantity));

            binding.minusButton.setOnClickListener(v -> decreaseQuantity(position));
            binding.plusButton.setOnClickListener(v -> increaseQuantity(position));
            binding.deleteButton.setOnClickListener(v -> {
                int itemPosition = getAdapterPosition();
                if (itemPosition != RecyclerView.NO_POSITION) {
                    deleteItem(itemPosition);
                }
            });
        }

        public void decreaseQuantity(int position) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--;
                binding.catItemQuantity.setText(Integer.toString(itemQuantities[position]));
                updateQuantityInFirebase(position, itemQuantities[position]);
            }
        }

        public void increaseQuantity(int position) {
            if (itemQuantities[position] < 9) {
                itemQuantities[position]++;
                binding.catItemQuantity.setText(Integer.toString(itemQuantities[position]));
                updateQuantityInFirebase(position, itemQuantities[position]);
            }
        }

        private void updateQuantityInFirebase(int position, int newQuantity) {
            getUniquekeyAtPosition(position, uniqueKey -> {
                if (uniqueKey != null) {
                    cartItemsReference.child(uniqueKey).child("foodquantity").setValue(newQuantity);
                }
            });
        }


        public void deleteItem(int position) {
            getUniquekeyAtPosition(position, uniqueKey -> {
                if (uniqueKey != null) {
                    removeItemFromFirebase(uniqueKey);
                    removeItemFromAdapter(position);
                }
            });
        }

        private void getUniquekeyAtPosition(int position, OnCompleteListener onComplete) {
            cartItemsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String uniqueKey = null;
                    int index = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (index == position) {
                            uniqueKey = dataSnapshot.getKey();
                            break;
                        }
                        index++;
                    }
                    onComplete.onComplete(uniqueKey);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error here
                }
            });
        }

        private void removeItemFromFirebase(String uniqueKey) {
            cartItemsReference.child(uniqueKey).removeValue();
        }

        private void removeItemFromAdapter(int position) {
            cartItems.remove(position);
            cartItemPrice.remove(position);
            cartImage.remove(position);
            notifyItemRemoved(position);
        }
    }

    public interface OnCompleteListener {
        void onComplete(String uniqueKey);
    }
}
