package com.labibkamran.nustfruta.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.labibkamran.nustfruta.MainActivity;
import com.labibkamran.nustfruta.R;
import com.labibkamran.nustfruta.databinding.FragmentCongratsBinding;


public class CongratsFragment extends BottomSheetDialogFragment {


    private FragmentCongratsBinding binding;
    public CongratsFragment() {
        // Required empty public constructor
    }


    public static CongratsFragment newInstance(String param1, String param2) {
        CongratsFragment fragment = new CongratsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCongratsBinding.inflate(inflater, container, false);
        binding.goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        return binding.getRoot();
    }
}