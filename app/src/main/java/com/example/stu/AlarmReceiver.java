package com.example.stu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Debug toast to confirm alarm triggered
        Toast.makeText(context, "⏰ Reminder triggered!", Toast.LENGTH_SHORT).show();

        String title = intent.getStringExtra("title");
        String category = intent.getStringExtra("category");

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel (for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "todo_channel",
                    "To-Do Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            nm.createNotificationChannel(channel);
        }

        // Show notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "todo_channel")
                .setSmallIcon(R.drawable.notification) // make sure you have a valid icon!
                .setContentTitle(title)
                .setContentText("Category: " + category)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        nm.notify((int) System.currentTimeMillis(), builder.build());
    }
}
