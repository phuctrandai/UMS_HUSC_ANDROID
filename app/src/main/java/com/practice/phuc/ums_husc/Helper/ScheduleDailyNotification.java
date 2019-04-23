package com.practice.phuc.ums_husc.Helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class ScheduleDailyNotification {

    public static Calendar getScheduleTime(int h) {
        Calendar calendarSet = Calendar.getInstance();
        calendarSet.set(Calendar.HOUR_OF_DAY, h);
        calendarSet.set(Calendar.MINUTE, 0);

        Log.d("DEBUG", "Alarm time: " + DateHelper.toDateTimeString(calendarSet.getTime()));

        return calendarSet;
    }

    public static void setUpScheduleAlarm(Context context, Calendar time) {
        Intent intent = new Intent(context, ScheduleReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
