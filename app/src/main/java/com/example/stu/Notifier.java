package com.example.stu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.app.Notification;

public abstract class Notifier {

    protected NotificationManager notificationManager;

    public Notifier(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public abstract void showNotification();

    @RequiresApi(Build.VERSION_CODES.O)
    protected NotificationChannel createNotificationChannel(String channelId, String channelName) {
        return new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
    }
}
