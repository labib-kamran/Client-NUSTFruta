package com.labibkamran.nustfruta.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labibkamran.nustfruta.R;
import com.labibkamran.nustfruta.adapter.NotificationAdapter;
import com.labibkamran.nustfruta.databinding.FragmentNotificationBottomBinding;
import com.labibkamran.nustfruta.Model.NotificationModel;
import java.util.ArrayList;
import java.util.List;

public class NotificationBottomFragment extends BottomSheetDialogFragment {

    private FragmentNotificationBottomBinding binding;
    private List<NotificationModel> notifications;
    private NotificationAdapter adapter;
    private DatabaseReference notificationRef;

    public NotificationBottomFragment() {
        // Required empty public constructor
    }

    public static NotificationBottomFragment newInstance() {
        return new NotificationBottomFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationRef = FirebaseDatabase.getInstance().getReference("notifications");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationBottomBinding.inflate(inflater, container, false);
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications);
        binding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.notificationRecyclerView.setAdapter(adapter);

        loadNotifications();

        return binding.getRoot();
    }

    private void loadNotifications() {
        notificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NotificationModel notification = dataSnapshot.getValue(NotificationModel.class);
                    notifications.add(notification);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }
}
