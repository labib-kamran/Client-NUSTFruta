package com.labibkamran.nustfruta.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.labibkamran.nustfruta.DetailsAcitvity;
import com.labibkamran.nustfruta.databinding.PopularItemsBinding;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder> {

    private ArrayList<String> items;
    private ArrayList<Integer> images;
    private ArrayList<String> prices;
    private Context context;

    public PopularAdapter(Context context, ArrayList<String> items, ArrayList<Integer> images, ArrayList<String> prices) {
        this.context = context;
        this.items = items;
        this.images = images;
        this.prices = prices;
    }

    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PopularItemsBinding binding = PopularItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PopularViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PopularViewHolder extends RecyclerView.ViewHolder {
        private PopularItemsBinding binding;

        public PopularViewHolder(PopularItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, DetailsAcitvity.class);
                        intent.putExtra("MenuItemName", items.get(position));
                        intent.putExtra("menuImage", images.get(position));
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void bind(int position) {
            String itemName = items.get(position);
            String price = prices.get(position);
            int imageResource = images.get(position);

            binding.foodNamePopular.setText(itemName);
            binding.PricePopular.setText(price);
            binding.popularImage.setImageResource(imageResource);
        }
    }
}
