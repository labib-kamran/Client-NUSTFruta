package com.labibkamran.nustfruta.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labibkamran.nustfruta.Model.MenuItems;
import com.labibkamran.nustfruta.R;
import com.labibkamran.nustfruta.adapter.menuAdapter;
import com.labibkamran.nustfruta.databinding.FragmentBottomMenuSeedBinding;

import java.util.ArrayList;

public class BottomMenuSeedFragment extends BottomSheetDialogFragment {
    private FragmentBottomMenuSeedBinding binding;
    FirebaseDatabase database;
    private ArrayList<MenuItems> menuItems;

    public BottomMenuSeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBottomMenuSeedBinding.inflate(inflater, container, false);
        binding.menuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        retrieveMenuItems();
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
        binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.menuRecyclerView.setAdapter(adapter);
    }
}
