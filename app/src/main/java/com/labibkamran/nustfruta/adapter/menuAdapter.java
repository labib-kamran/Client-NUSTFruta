package com.labibkamran.nustfruta.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.labibkamran.nustfruta.DetailsAcitvity;
import com.labibkamran.nustfruta.Model.MenuItems;
import com.labibkamran.nustfruta.databinding.MenuItemBinding;

import java.util.ArrayList;

public class menuAdapter extends RecyclerView.Adapter<menuAdapter.MenuViewHolder> {

  private ArrayList<MenuItems> menuItems;
    private Context context;

    public menuAdapter(Context context, ArrayList<MenuItems> menuItem) {
        this.context = context;
        this.menuItems = menuItem;
    }

    public menuAdapter(Context context, ArrayList<String> foodName, ArrayList<String> price, ArrayList<Integer> images) {
    }


    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MenuItemBinding binding = MenuItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MenuViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private MenuItemBinding binding;

        public MenuViewHolder(MenuItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        openDetailsActivity(position);
                    }
                }
            });
        }

        private void openDetailsActivity(int position) {
            MenuItems menuItem = menuItems.get(position);
            // intent to open Details Activity and put data
            Intent intent = new Intent(context, DetailsAcitvity.class);
            intent.putExtra("MenuItemName", menuItem.getFoodName());
            intent.putExtra("MenuItemImage", menuItem.getFoodImage());
            intent.putExtra("MenuItemPrice", menuItem.getFoodPrice());
            intent.putExtra("MenuItemDescription", menuItem.getFoodDescription());

            context.startActivity(intent);
        }

        public void bind(int position) {
            MenuItems menuItem = menuItems.get(position);
            String foodName = menuItem.getFoodName();
            String price = menuItem.getFoodPrice();
            Uri uri = Uri.parse(menuItem.getFoodImage());
            Glide.with(context).load(uri).into(binding.menuImage);

            binding.menuFoodName.setText(foodName);
            binding.menuPrice.setText(price);

        }
    }
}
