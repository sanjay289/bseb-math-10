package com.sanjay.bsebmath10;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Calendar;

// Daily practice reminder: settings live in SharedPreferences so the boot receiver can reschedule.
// One alarm at a time; each firing schedules the next day's.
final class Reminder {
    private static final String CHANNEL = "daily";

    static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences("reminder", Context.MODE_PRIVATE);
    }

    private static int immutable() {
        return Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_IMMUTABLE : 0;
    }

    private static PendingIntent alarmIntent(Context c) {
        return PendingIntent.getBroadcast(c, 0, new Intent(c, ReminderReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT | immutable());
    }

    static void schedule(Context c) {
        SharedPreferences p = prefs(c);
        if (!p.getBoolean("on", false)) return;
        Calendar at = Calendar.getInstance();
        at.set(Calendar.HOUR_OF_DAY, p.getInt("hour", 19));
        at.set(Calendar.MINUTE, p.getInt("minute", 0));
        at.set(Calendar.SECOND, 0);
        at.set(Calendar.MILLISECOND, 0);
        if (at.getTimeInMillis() <= System.currentTimeMillis()) at.add(Calendar.DAY_OF_MONTH, 1);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at.getTimeInMillis(), alarmIntent(c));
        } else {
            am.set(AlarmManager.RTC_WAKEUP, at.getTimeInMillis(), alarmIntent(c));
        }
    }

    static void cancel(Context c) {
        ((AlarmManager) c.getSystemService(Context.ALARM_SERVICE)).cancel(alarmIntent(c));
    }

    static void show(Context c) {
        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(new NotificationChannel(CHANNEL,
                    "दैनिक अभ्यास / Daily practice", NotificationManager.IMPORTANCE_DEFAULT));
            b = new Notification.Builder(c, CHANNEL);
        } else {
            b = new Notification.Builder(c);
        }
        String text = "आज का गणित अभ्यास करें! 📚\nTime for today's maths practice!";
        PendingIntent open = PendingIntent.getActivity(c, 0, new Intent(c, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | immutable());
        b.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(c.getString(R.string.app_name))
                .setContentText("आज का गणित अभ्यास करें! 📚")
                .setStyle(new Notification.BigTextStyle().bigText(text))
                .setContentIntent(open)
                .setAutoCancel(true);
        nm.notify(1, b.build());
    }
}
