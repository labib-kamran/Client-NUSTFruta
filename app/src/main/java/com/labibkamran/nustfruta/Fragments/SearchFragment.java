package com.labibkamran.nustfruta.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labibkamran.nustfruta.Model.MenuItems;
import com.labibkamran.nustfruta.R;
import com.labibkamran.nustfruta.adapter.menuAdapter;
import com.labibkamran.nustfruta.databinding.FragmentSearchBinding;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;
    FirebaseDatabase database;
    private ArrayList<MenuItems> menuItems;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        retrieveMenuItems();
        setupSearchView();
        return binding.getRoot();
    }

    private void retrieveMenuItems() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference foodRef = database.getReference().child("menu");
        menuItems = new ArrayList<>();
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot foodSnapshot : snapshot.getChildren()){
                    MenuItems menuItem = foodSnapshot.getValue(MenuItems.class);
                    if (menuItem != null) {
                        menuItems.add(menuItem);
                    }
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error here
            }
        });
    }

    private void setAdapter() {
        menuAdapter adapter = new menuAdapter(getContext(), menuItems);
        binding.searchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.searchRecyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterMenuItems(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMenuItems(newText);
                return false;
            }
        });
    }

    private void filterMenuItems(String query) {
        ArrayList<MenuItems> filteredMenuItems = new ArrayList<>();

        for (MenuItems menuItem : menuItems) {
            if (menuItem.getFoodName().toLowerCase().contains(query.toLowerCase())) {
                filteredMenuItems.add(menuItem);
            }
        }

        // Update your adapter with the filtered list
        menuAdapter adapter = new menuAdapter(getContext(), filteredMenuItems);
        binding.searchRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
