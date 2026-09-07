package com.example.stu;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationAlarmScheduler {

    private final Context context;

    public NotificationAlarmScheduler(Context context) {
        this.context = context;
    }

    public void schedule(ReminderItem item) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", item.title);
        intent.putExtra("category", item.category);

        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                item.id, // unique per task
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, item.timeMillis, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, item.timeMillis, pi);
        }
    }
}
