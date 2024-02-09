package com.example.todolist;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
    private BroadcastReceiver screenReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve user preference for dark mode or use the system default
        int savedNightMode = getPreferences(Context.MODE_PRIVATE).getInt("night_mode",
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedNightMode);

        setContentView(R.layout.activity_main);
        onInit();

        // Create and show a notification when the app starts
        Notification notification = createNotification();
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        ToDoNotifications toDoNotifications = new ToDoNotifications(this);
        toDoNotifications.startThread(notification);

        // Initialize the UI components
        items = new ArrayList<>();
        itemAdapter = new CustomAdapter();
        list.setAdapter(itemAdapter);
        loadItemsFromSharedPreferences();

        // Play the default notification sound when the app is opened
        playDefaultNotificationSound();

        // Register the BroadcastReceiver to listen for screen on/off events
        registerScreenReceiver();

        // Set the onClickListener for the "Add" button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    // Create a notification with a default configuration
    Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(this, ToDoNotifications.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icn)
                .setContentTitle("Danios's App")
                .setContentText("Du har nodi min bror ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver when the activity is destroyed
        unregisterReceiver(screenReceiver);
    }

    // Register a BroadcastReceiver to listen for screen on/off events
    private void registerScreenReceiver() {
        screenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                    // Screen is turned on, play the default notification sound
                    playDefaultNotificationSound();
                } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                    // Screen is turned off, play the default notification sound
                    playDefaultNotificationSound();
                }
            }
        };

        // Register the BroadcastReceiver to listen for screen on/off events
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);
    }

    // Play the default notification sound
    private void playDefaultNotificationSound() {
        try {
            // Get the default notification sound URI
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // Create a Ringtone object and play the notification sound
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Initialize UI components
    private void onInit() {
        list = findViewById(R.id.lv_todo);
        button = findViewById(R.id.btn_add);
        name_Input = findViewById(R.id.edit_text);
        desc_input = findViewById(R.id.desc_txt);
        type_input = findViewById(R.id.type_txt);
    }

    // Add a new item to the list
    private void addItem() {
        String itemName = name_Input.getText().toString();
        String itemDesc = desc_input.getText().toString();
        String itemType = type_input.getText().toString();

        if (!itemName.isEmpty()) {
            // Add item to the list and update the UI
            items.add(itemName);
            itemAdapter.notifyDataSetChanged();
            name_Input.setText("");

            // Create an Item object and add it to the itemList
            Item item = new Item();
            item.Todo = itemName;
            item.ItemDescription = itemDesc;
            item.Type = itemType;
            item.CreatedDate = new java.util.Date(); // Set creation date
            item.IconResourceId = R.drawable.time_clock; // Set the icon resource ID
            itemList.add(item);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter text...", Toast.LENGTH_SHORT).show();
        }
        // Save the updated list to SharedPreferences
        saveItemsToSharedPreferences();
    }

    // CustomAdapter for displaying items in the ListView
    private class CustomAdapter extends ArrayAdapter<String> {
        public CustomAdapter() {
            super(MainActivity.this, R.layout.list_item_layout, items);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout,
                        parent, false);
            }

            TextView todoTextView = convertView.findViewById(R.id.todo_text);
            TextView descriptionTextView = convertView.findViewById(R.id.description_text);
            TextView typeTextView = convertView.findViewById(R.id.type_text);
            TextView createdDateTextView = convertView.findViewById(R.id.created_date);
            TextView expireTimeTextView = convertView.findViewById(R.id.expire_time);
            ImageView iconImageView = convertView.findViewById(R.id.icon_image);

            // Check if itemList is not empty and position is within bounds
            if (!itemList.isEmpty() && position < itemList.size()) {
                Item currentItem = itemList.get(position);

                todoTextView.setText(currentItem.Todo);
                descriptionTextView.setText("Description: " + currentItem.ItemDescription);
                typeTextView.setText("Type: " + currentItem.Type);
                createdDateTextView.setText("Created: " + currentItem.CreatedDate.toString());

                // Load and display the icon
                iconImageView.setImageResource(currentItem.IconResourceId);

                // Calculate expiration time
                long expirationTimeMillis = currentItem.CreatedDate
                        .getTime() + (2 * 60 * 60 * 1000); // 2 hours in milliseconds
                long currentTimeMillis = System.currentTimeMillis();
                long timeRemainingMillis = expirationTimeMillis - currentTimeMillis;

                // Check if the item is still valid
                if (timeRemainingMillis > 0) {
                    long minutesRemaining = (timeRemainingMillis / (1000 * 60)) % 60;
                    long hoursRemaining = (timeRemainingMillis / (1000 * 60 * 60)) % 24;

                    expireTimeTextView.setText("Planlagt om: " + hoursRemaining + " hours "
                            + minutesRemaining + " minutes");
                } else {
                    expireTimeTextView.setText("Udgået: ");
                }
            } else {
                todoTextView.setText(""); // Set an empty string or handle accordingly
                descriptionTextView.setText("");
                typeTextView.setText("");
                createdDateTextView.setText("");
                expireTimeTextView.setText("");
                iconImageView.setImageResource(R.drawable.time_clock); // Set a default icon if needed
            }

            // Set onClickListener for the "Edit" button
            Button editButton = convertView.findViewById(R.id.edit_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog(position);
                }
            });

            // Set onClickListener for the "Delete" button
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

    // Show a dialog for editing an item
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
                    // Update item in the list and UI
                    items.set(position, editedText);
                    itemList.get(position).Todo = editedText;
                    itemAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter text...",
                            Toast.LENGTH_SHORT).show();
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
        // Save the updated list to SharedPreferences
        saveItemsToSharedPreferences();
    }

    // Save the items list to SharedPreferences
    private void saveItemsToSharedPreferences() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> serializedItems = new HashSet<>();

        for (Item item : itemList) {
            // Serialize each item to a string
            String serializedItem = item.Todo + "|" + item.ItemDescription + "|" + item.Type + "|" +
                    item.CreatedDate.toString() + "|" + item.IconResourceId;
            serializedItems.add(serializedItem);
        }

        editor.putStringSet("todo_items", serializedItems);
        editor.apply();
    }

    // Load items from SharedPreferences and update the UI
    private void loadItemsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        Set<String> savedItems = sharedPreferences.getStringSet("todo_items", new HashSet<>());
        items.clear();
        itemList.clear();

        // Deserialize each string back to an Item and add to the list
        for (String serializedItem : savedItems) {
            String[] parts = serializedItem.split("\\|");
            if (parts.length == 5) { // Check for the correct number of parts
                Item item = new Item();
                item.Todo = parts[0];
                item.ItemDescription = parts[1];
                item.Type = parts[2];
                item.CreatedDate = new java.util.Date(parts[3]);
                item.IconResourceId = Integer.parseInt(parts[4]); // Parse the icon resource ID
                itemList.add(item);
                items.add(item.Todo); // Only add the Todo part to the displayed list if needed
            }
        }

        // Update the UI
        itemAdapter.notifyDataSetChanged();
    }

    private void toggleDarkMode() {
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        int newNightMode = (nightMode == AppCompatDelegate.MODE_NIGHT_YES) ?
                AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES;

        // Save the new night mode preference
        getPreferences(Context.MODE_PRIVATE).edit().putInt("night_mode", newNightMode).apply();

        // Set the new night mode
        AppCompatDelegate.setDefaultNightMode(newNightMode);
        recreate();
    }
}