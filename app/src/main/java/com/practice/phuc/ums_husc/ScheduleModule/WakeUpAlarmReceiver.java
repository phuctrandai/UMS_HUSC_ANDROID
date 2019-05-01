package com.practice.phuc.ums_husc.ScheduleModule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class WakeUpAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            ScheduleDailyNotification.setReminder(context, 1501, ScheduleReceiver.class, 0, 0);
        }
    }
}
