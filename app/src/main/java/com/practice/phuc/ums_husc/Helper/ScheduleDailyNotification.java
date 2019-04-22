package com.practice.phuc.ums_husc.Helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class ScheduleDailyNotification {

    public static Calendar getScheduleTime(int h) {
        Calendar now = DateHelper.getCalendar();
        Calendar calendarSet = DateHelper.getCalendar();
        calendarSet.set(Calendar.HOUR_OF_DAY, h);
        calendarSet.set(Calendar.MINUTE, 1);
        calendarSet.set(Calendar.SECOND, 0);
        calendarSet.set(Calendar.MILLISECOND, 0);

        Log.d("DEBUG", "Alarm time: " + DateHelper.toDateTimeString(calendarSet.getTime()));

        if (calendarSet.compareTo(now) <= 0) {
            Log.d("DEBUG", "Alarm will schedule for next day!");
            calendarSet.add(Calendar.DAY_OF_YEAR, 1);
        }
        return calendarSet;
    }

    public static void setUpScheduleAlarm(Context context, Calendar time) {
        Intent intent = new Intent(context, ScheduleReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
