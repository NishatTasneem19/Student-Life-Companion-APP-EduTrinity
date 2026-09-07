package com.example.stu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.os.Build;
import android.app.Notification;

public class TaskNotifier extends Notifier {

    private Context context;
    private String title;
    private String category;

    public TaskNotifier(NotificationManager nm, Context context, String title, String category) {
        super(nm);
        this.context = context;
        this.title = title;
        this.category = category;
    }

    @Override
    public void showNotification() {
        String channelId = "task_channel_id";
        String channelName = "Task Reminders";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = createNotificationChannel(channelId, channelName);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, ToDoList.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Task Reminder")
                .setContentText(title + " [" + category + "]")
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
