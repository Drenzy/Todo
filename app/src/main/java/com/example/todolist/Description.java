package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Description extends AppCompatActivity {
    TextView todoTextView;
    TextView descTextView;
    TextView typeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        // Retrieve the selected Item object from the intent
        Item selectedItem = (Item) getIntent().getSerializableExtra("selectedItem");

        // Use the selected Item object's properties as needed
        todoTextView = findViewById(R.id.todoTextView);
        descTextView = findViewById(R.id.descTextView);
        typeTextView = findViewById(R.id.typeTextView);

        if (selectedItem != null) {
            todoTextView.setText(selectedItem.Todo);
            descTextView.setText(selectedItem.ItemDescription);
            typeTextView.setText(selectedItem.Type);
            // Add additional TextViews and set their text based on other properties of Item class
        }
    }
}