package com.labibkamran.nustfruta.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.labibkamran.nustfruta.RecentOrderItems;
import com.labibkamran.nustfruta.databinding.RecentButItemBinding;

import java.util.ArrayList;

public class RecentBuyAdapter extends RecyclerView.Adapter<RecentBuyAdapter.RecentViewHolder> {

    RecentButItemBinding binding;
    private ArrayList<String> foodNameList;
    private ArrayList<String> foodImageList;
    private ArrayList<String> foodPriceList;
    private ArrayList<Integer> foodQuantityList;
    private Context context;

    public RecentBuyAdapter(ArrayList<String> allFoodNames, ArrayList<String> allFoodImages, ArrayList<String> allFoodPrices, ArrayList<Integer> allFoodQuantities, RecentOrderItems recentOrderItems){
        this.foodNameList = allFoodNames;
        this.foodImageList = allFoodImages;
        this.foodPriceList = allFoodPrices;
        this.foodQuantityList = allFoodQuantities;
        this.context = recentOrderItems;
    }

    @NonNull
    @Override
    public RecentBuyAdapter.RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RecentButItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new RecentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentBuyAdapter.RecentViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return foodNameList.size();
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder {
        RecentButItemBinding binding;
        public RecentViewHolder(@NonNull RecentButItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            binding.foodName.setText(foodNameList.get(position));
            binding.foodPrice.setText("Rs " + foodPriceList.get(position));
            binding.quantity.setText(String.valueOf(foodQuantityList.get(position)));
            String uriString = foodImageList.get(position);
            Uri uri = Uri.parse(uriString);
            Glide.with(context).load(uri).into(binding.foodImage);
        }
    }
}
