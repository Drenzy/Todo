package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //private ArrayList<String> items;
    private Button button;
    private ListView list;
    private EditText name_Input;
    private EditText desc_imput;
    private EditText type_input;

    private ArrayAdapter<Item> itemAdapter;

    private List<Item> itemList = new ArrayList<>();
    // Use a custom adapter to handle custom list item layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onInit();

        itemList = new ArrayList<>();
        itemAdapter = new CustomAdapter(this, itemList); // Initialize custom adapter
        list.setAdapter(itemAdapter);
        loadItemsFromSharedPreferences();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });


    }

    private void onInit(){
        list = findViewById(R.id.lv_todo);
        button = findViewById(R.id.btn_add);
        name_Input = findViewById(R.id.edit_text);
        desc_imput = findViewById(R.id.desc_txt);
        type_input = findViewById(R.id.type_txt);
    }

    private void addItem() {
        String nameText = name_Input.getText().toString();
        String descText = desc_imput.getText().toString();
        String typeText = type_input.getText().toString();

        if (!nameText.isEmpty()) {
            //item.Todo.add(nameText); // Add item to the list
            itemAdapter.notifyDataSetChanged(); // Notify adapter that data has changed
            name_Input.setText(""); // Clear task input
            desc_imput.setText(""); // Clear description input
            type_input.setText(""); // Clear type input


            // Create a new Item object and set its properties
            Item newItem = new Item();
            newItem.Todo = nameText;
            newItem.ItemDescription = descText;
            newItem.Type = typeText;
            itemList.add(newItem);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter a task...", Toast.LENGTH_SHORT).show();
        }
        saveItemsToSharedPreferences();
    }

    // Custom adapter class to handle custom list item layout and functionality
    private class CustomAdapter extends ArrayAdapter<Item> {

        public CustomAdapter(Context context, List<Item> items) {
            super(MainActivity.this, R.layout.list_item_layout, items);
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Inflating custom list item layout if convertView is null
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
            }

            // Setting text for to-do item
            TextView textView = convertView.findViewById(R.id.todo_text);
            textView.setText(itemList.get(position).Todo);

            // Setting click listener for list item
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start a new activity with the selected item data
                    startNewItemActivity(itemList.get(position));
                }
            });


            // Setting click listener for edit button
            Button editButton = convertView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add your edit functionality here
                    showEditDialog(position);

                    Toast.makeText(MainActivity.this, "Edit clicked for item at position " + position, Toast.LENGTH_SHORT).show();
                }
            });

            // Setting click listener for delete button
            Button deleteButton = convertView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove item from the list and notify adapter
                    itemList.remove(position);
                    saveItemsToSharedPreferences();
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
        private void showEditDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); // Use MainActivity.this as the context
            builder.setTitle("Edit Item");

            final EditText input = new EditText(MainActivity.this);
            input.setText(itemList.get(position));
            builder.setView(input);

            // Set positive button for saving changes
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Retrieve edited text and update the list
                    String editedText = input.getText().toString();
                    if (!editedText.isEmpty()) {
                        items.set(position, editedText);
                        itemAdapter.notifyDataSetChanged();
                    } else {
                        // Show a toast if the edited text is empty
                        Toast.makeText(getApplicationContext(), "Please enter text...", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Set negative button for canceling the edit
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Show the edit dialog
            builder.show();
            saveItemsToSharedPreferences();
        }

    }
    private void saveItemsToSharedPreferences() {
        // Use SharedPreferences to save the list of items
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("todo_items", new HashSet<>(items));
        editor.apply();
    }

    private void loadItemsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        Set<String> savedItems = sharedPreferences.getStringSet("todo_items", new HashSet<>());
        items.addAll(savedItems);
        itemAdapter.notifyDataSetChanged();
    }

    private void startNewItemActivity(Item selectedItem) {
        // Create an Intent to start the new activity
        Intent intent = new Intent(MainActivity.this, Description.class);
        // Pass the selected item data to the new activity
        intent.putExtra("selectedItem", selectedItem);
        // Pass the itemList to the new activity
        intent.putExtra("itemList", new ArrayList<>(itemList));
        startActivity(intent);
    }
}