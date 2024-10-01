package com.labibkamran.nustfruta;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import java.util.ArrayList;
import java.util.Arrays;


public class setdelivery extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setdelivery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ArrayList<String> location = new ArrayList<String>(Arrays.asList("New York", "London", "Tokyo", "Paris"));
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,location);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.listoflocation);
        autoCompleteTextView.setAdapter(adapter);
    }
}