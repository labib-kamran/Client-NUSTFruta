package com.labibkamran.nustfruta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.labibkamran.nustfruta.Fragments.CartFragment;
import com.labibkamran.nustfruta.Fragments.HistoryFragment;
import com.labibkamran.nustfruta.Fragments.HomeFragment;
import com.labibkamran.nustfruta.Fragments.NotificationBottomFragment;
import com.labibkamran.nustfruta.Fragments.ProfileFragment;
import com.labibkamran.nustfruta.Fragments.SearchFragment;
import com.labibkamran.nustfruta.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragment(new HomeFragment());
       bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
           if(menuItem.getItemId()==R.id.homeFragment3){
               replaceFragment(new HomeFragment());
           }

           else if(menuItem.getItemId()==R.id.cartFragment3){
                replaceFragment(new CartFragment());
           }
           else if(menuItem.getItemId()==R.id.searchFragment2){
                replaceFragment(new SearchFragment());
           }
           else if(menuItem.getItemId()==R.id.historyFragment3){
                replaceFragment(new HistoryFragment());
           }
           else if(menuItem.getItemId()==R.id.profileFragment2){
                replaceFragment(new ProfileFragment());
           }
           return true;
       });
        binding.notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationBottomFragment notificationBottomFragment = new NotificationBottomFragment();
                notificationBottomFragment.show(getSupportFragmentManager(),"");
            }
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}

