package com.example.todolist;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class ToDoNotifications extends Thread {
    public static final String CHANNEL_ID = "1912";
    Activity act;
    NotificationManager notificationManager;
    public ToDoNotifications(Activity activity){
        act = activity;
        createNotificationChannel();

    }

    void startThread(final Notification notification){
        Thread t = new Thread(() -> postNotification(notification));
        t.start();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
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


    void postNotification(Notification notification){
        if (ActivityCompat.checkSelfPermission(act,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        notificationManager.notify(0, notification);
    }
}
