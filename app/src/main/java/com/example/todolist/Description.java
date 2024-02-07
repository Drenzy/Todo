package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Description extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        // Retrieve the selected item string from the intent
        String selectedItem = getIntent().getStringExtra("selectedItem");

        // Use the selected item string as needed (for example, display it in a TextView)
        TextView itemTextView = findViewById(R.id.itemTextView);
        itemTextView.setText(selectedItem);
    }
}