package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
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

import com.example.todolist.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> items;
    private Button button;
    private ListView list;
    private EditText name_Input;
    private EditText desc_input;
    private EditText type_input;

    private ArrayAdapter<String> itemAdapter;

    private List<Item> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onInit();

        items = new ArrayList<>();
        itemAdapter = new CustomAdapter();
        list.setAdapter(itemAdapter);
        loadItemsFromSharedPreferences();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    private void onInit() {
        list = findViewById(R.id.lv_todo);
        button = findViewById(R.id.btn_add);
        name_Input = findViewById(R.id.edit_text);
        desc_input = findViewById(R.id.desc_txt);
        type_input = findViewById(R.id.type_txt);
    }

    private void addItem() {
        String itemName = name_Input.getText().toString();
        String itemDesc = desc_input.getText().toString();
        String itemType = type_input.getText().toString();

        if (!itemName.isEmpty()) {
            items.add(itemName);
            itemAdapter.notifyDataSetChanged();
            name_Input.setText("");

            Item item = new Item();
            item.Todo = itemName;
            item.ItemDescription = itemDesc;
            item.Type = itemType;
            item.CreatedDate = new java.util.Date(); // Set creation date
            itemList.add(item);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter text...", Toast.LENGTH_SHORT).show();
        }
        saveItemsToSharedPreferences();
    }

    private class CustomAdapter extends ArrayAdapter<String> {
        public CustomAdapter() {
            super(MainActivity.this, R.layout.list_item_layout, items);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
            }

            TextView todoTextView = convertView.findViewById(R.id.todo_text);
            TextView descriptionTextView = convertView.findViewById(R.id.description_text);
            TextView typeTextView = convertView.findViewById(R.id.type_text);
            TextView createdDateTextView = convertView.findViewById(R.id.created_date);
            TextView expireTimeTextView = convertView.findViewById(R.id.expire_time);

            // Check if itemList is not empty and position is within bounds
            if (!itemList.isEmpty() && position < itemList.size()) {
                Item currentItem = itemList.get(position);

                todoTextView.setText(currentItem.Todo);
                descriptionTextView.setText("Description: " + currentItem.ItemDescription);
                typeTextView.setText("Type: " + currentItem.Type);
                createdDateTextView.setText("Created: " + currentItem.CreatedDate.toString());

                // Calculate expiration time
                long expirationTimeMillis = currentItem.CreatedDate.getTime() + (2 * 60 * 60 * 1000); // 2 hours in milliseconds
                long currentTimeMillis = System.currentTimeMillis();
                long timeRemainingMillis = expirationTimeMillis - currentTimeMillis;

                // Check if the item is still valid
                if (timeRemainingMillis > 0) {
                    long minutesRemaining = (timeRemainingMillis / (1000 * 60)) % 60;
                    long hoursRemaining = (timeRemainingMillis / (1000 * 60 * 60)) % 24;

                    expireTimeTextView.setText("Expires in: " + hoursRemaining + " hours " + minutesRemaining + " minutes");
                } else {
                    expireTimeTextView.setText("Expired");
                }
            } else {
                todoTextView.setText(""); // Set an empty string or handle accordingly
                descriptionTextView.setText("");
                typeTextView.setText("");
                createdDateTextView.setText("");
                expireTimeTextView.setText("");
            }

            Button editButton = convertView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            Button deleteButton = convertView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.remove(position);
                    if (!itemList.isEmpty() && position < itemList.size()) {
                        itemList.remove(position);
                    }
                    saveItemsToSharedPreferences();
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

    private void showEditDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Edit Item");

        final EditText input = new EditText(MainActivity.this);
        input.setText(items.get(position));
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String editedText = input.getText().toString();
                if (!editedText.isEmpty()) {
                    items.set(position, editedText);
                    itemList.get(position).Todo = editedText;
                    itemAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter text...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        saveItemsToSharedPreferences();
    }

    private void saveItemsToSharedPreferences() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> serializedItems = new HashSet<>();

        for (Item item : itemList) {
            // Serialize each item to a string (you can use Gson or another serialization method)
            // For simplicity, here's a basic implementation:
            String serializedItem = item.Todo + "|" + item.ItemDescription + "|" + item.Type + "|" +
                    item.CreatedDate.toString();
            serializedItems.add(serializedItem);
        }

        editor.putStringSet("todo_items", serializedItems);
        editor.apply();
    }

    private void loadItemsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        Set<String> savedItems = sharedPreferences.getStringSet("todo_items", new HashSet<>());
        items.clear();
        itemList.clear();

        // Deserialize each string back to an Item and add to the list
        for (String serializedItem : savedItems) {
            String[] parts = serializedItem.split("\\|");
            if (parts.length == 4) {
                Item item = new Item();
                item.Todo = parts[0];
                item.ItemDescription = parts[1];
                item.Type = parts[2];
                item.CreatedDate = new java.util.Date(parts[3]);
                itemList.add(item);
                items.add(item.Todo); // Only add the Todo part to the displayed list if needed
            }
        }

        itemAdapter.notifyDataSetChanged();
    }
}