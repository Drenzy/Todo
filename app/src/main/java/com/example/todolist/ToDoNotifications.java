// ToDoNotifications class for managing and posting notifications in a separate thread.

package com.example.todolist;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class ToDoNotifications extends Thread {

    // Constant for the notification channel ID
    public static final String CHANNEL_ID = "1912";

    // Reference to the activity for context and resource access
    Activity act;

    // NotificationManager for handling notifications
    NotificationManager notificationManager;

    // Constructor for initializing the ToDoNotifications object
    public ToDoNotifications(Activity activity) {
        act = activity;
        createNotificationChannel();
    }

    // Start a new thread for posting a notification
    void startThread(final Notification notification) {
        Thread t = new Thread(() -> postNotification(notification));
        t.start();
    }

    // Create a notification channel for devices running Android Oreo (API 26) or higher
    private void createNotificationChannel() {
        // Check if the Android version is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = act.getString(R.string.channel_name);
            String description = act.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            notificationManager = act.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Post a notification using the provided Notification object
    void postNotification(Notification notification) {
        // Check if the app has the necessary notification permission
        if (ActivityCompat.checkSelfPermission(act, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Notify the NotificationManager to display the notification
        notificationManager.notify(0, notification);
    }
}
