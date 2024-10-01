package com.labibkamran.nustfruta.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labibkamran.nustfruta.Model.MenuItems;
import com.labibkamran.nustfruta.adapter.menuAdapter;
import com.labibkamran.nustfruta.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private  ArrayList<MenuItems> menuItems;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
//        binding.popularRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        binding.popularRecyclerView.setAdapter(adapter);
        binding.viewAllMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomMenuSeedFragment bottomMenuSeedFragment = new BottomMenuSeedFragment();
                bottomMenuSeedFragment.show(getParentFragmentManager(),"");
            }
        });
        // display and retrieve popular item view
        retrieveAndDisplayPopularItems();
        return view;
    }

    private void retrieveAndDisplayPopularItems() {
        // reference to the data base
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference foodRef = database.getReference().child("menu");
        menuItems = new ArrayList<MenuItems>();
        // retriieve menu items from database
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot foodSnapshot : snapshot.getChildren()){
                    MenuItems menuItem = foodSnapshot.getValue(MenuItems.class);
                    if (menuItem != null) {
                        menuItems.add(menuItem);
                    }
                }
                randomPopularItems();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void randomPopularItems() {
        // Create a shuffled list of menu items
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < menuItems.size(); i++) {
            index.add(i);
        }
        Collections.shuffle(index);

        int numItemsToShow = 4;
        ArrayList<MenuItems> subsetMenuItems = new ArrayList<>();
        for (int i = 0; i < numItemsToShow; i++) {
            subsetMenuItems.add(menuItems.get(index.get(i)));
        }

        setPopularItemsAdapter(subsetMenuItems);
    }

    private void setPopularItemsAdapter(ArrayList<MenuItems> subsetMenuItems) {
        menuAdapter adapter = new menuAdapter(getContext(),  subsetMenuItems);
        binding.popularRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.popularRecyclerView.setAdapter(adapter);
    }

}