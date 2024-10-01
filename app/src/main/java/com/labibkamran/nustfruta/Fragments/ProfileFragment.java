package com.labibkamran.nustfruta.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labibkamran.nustfruta.ActivityLogin;
import com.labibkamran.nustfruta.Model.UserModel;
import com.labibkamran.nustfruta.databinding.FragmentProfileBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FragmentProfileBinding binding;
    ArrayList<String> location;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        location = new ArrayList<>(Arrays.asList(
                "Ammar hostel", "Amna Hostel", "Ayesha hostel", "Attar Hostel",
                "Beruni Hostel", "Fatima hostel", "Ghazali hostel", "Johar hostel",
                "Khadija hostel", "Liaquat Hostel", "Rahmat Hostel", "Razi hostel",
                "Rumi Hostel", "Zakriya Hostel", "Zainab hostel"
        ));
        Collections.sort(location);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, location);
        binding.address.setAdapter(adapter);

        binding.address.setOnItemClickListener((parent, view, position, id) -> {
            binding.address.setEnabled(false);
        });

        setuserdata();

        binding.saveintobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });

        binding.saveintobutton.setEnabled(false);
        binding.name.setEnabled(false);
        binding.email.setEnabled(false);
        binding.address.setEnabled(false);
        binding.ProfilePhone.setEnabled(false);

        binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEditable = !binding.name.isEnabled();
                binding.name.setEnabled(isEditable);
                binding.email.setEnabled(isEditable);
                binding.address.setEnabled(isEditable);
                binding.ProfilePhone.setEnabled(isEditable);
                binding.saveintobutton.setEnabled(isEditable);
            }
        });
        binding.signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getActivity(), ActivityLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return binding.getRoot();
    }

    private void updateUserData(String name, String email, String address, String phone) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRefrence = database.getReference("user").child(userId);
        HashMap<String, String> userData = new HashMap<>();

        if (userId != null) {
            userData.put("name", name);
            userData.put("address", address);
            userData.put("email", email);
            userData.put("phone", phone);

            userRefrence.setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Profile update failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setuserdata() {
        String userId = auth.getCurrentUser().getUid();
        if (userId != null) {
            DatabaseReference userReference = database.getReference().child("user").child(userId);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserModel userProfile = snapshot.getValue(UserModel.class);
                        if (userProfile != null) {
                            binding.name.setText(userProfile.getName());
                            binding.address.setText(userProfile.getAddress());
                            binding.email.setText(userProfile.getEmail());
                            binding.ProfilePhone.setText(userProfile.getPhone());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors.
                }
            });
        }
    }
    private void checkCredentials() {
        String name = binding.name.getText().toString();
        String email = binding.email.getText().toString();
        String address = binding.address.getText().toString();
        String phone = binding.ProfilePhone.getText().toString();
        if (name.isEmpty())
            showError(binding.name, "please fill this field");
        else if (address.isEmpty())
            showError(binding.address, "please fill this field");
        else if (!(location.contains(address)))
            showError(binding.address, "please select address from list");
        else if (phone.isEmpty())
            showError(binding.ProfilePhone, "please fill this field");
        else if (!(phone.matches("^03\\d{9}$")))
            showError(binding.ProfilePhone, "enter a valid phone number");

        else {
            updateUserData(name, email, address, phone);
        }
    }

    private void showError(EditText input, String error) {
        input.setError(error);
        input.requestFocus();
    }
}
