package com.labibkamran.nustfruta.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.labibkamran.nustfruta.databinding.BuyAgainItemBinding;

import java.util.ArrayList;

public class BuyAgainAdapter extends RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>{
    BuyAgainItemBinding binding;
    private ArrayList<String> buyAgainFoodName;
    private ArrayList<String> buyAgainFoodPrice;
    private ArrayList<String> buyAgainFoodImage;
    private Context context;

    public BuyAgainAdapter(ArrayList<String> buyAgainFoodName, ArrayList<String> buyAgainFoodPrice, ArrayList<String> buyAgainFoodImage, Context context) {
        this.buyAgainFoodName = buyAgainFoodName;
        this.buyAgainFoodPrice = buyAgainFoodPrice;
        this.buyAgainFoodImage = buyAgainFoodImage;
        this.context = context;
    }
    @NonNull
    @Override
    public BuyAgainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BuyAgainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyAgainViewHolder holder, int position) {
        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return buyAgainFoodName.size();
    }

    public class BuyAgainViewHolder extends RecyclerView.ViewHolder {
        private final BuyAgainItemBinding binding;

        public BuyAgainViewHolder(BuyAgainItemBinding binding) {

            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            String foodName = buyAgainFoodName.get(position);
            String price = buyAgainFoodPrice.get(position);
            String imageResource = buyAgainFoodImage.get(position);

            binding.buyAgainFoodName.setText(foodName);
            binding.againFoodPrice.setText(price);
            Uri uri = Uri.parse(imageResource);
            Glide.with(context).load(uri).into(binding.buyAgainFoodImage);          //  binding.buyAgainFoodImage.setImageResource(imageResource);

        }
    }
}
