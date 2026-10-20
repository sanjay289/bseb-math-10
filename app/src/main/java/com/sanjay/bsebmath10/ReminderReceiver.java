package com.sanjay.bsebmath10;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent intent) {
        String action = intent.getAction();
        // Boot and app update clear alarms; just reschedule. Otherwise it's our daily alarm.
        if (!Intent.ACTION_BOOT_COMPLETED.equals(action) && !Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            Reminder.show(c);
        }
        Reminder.schedule(c);
    }
}
