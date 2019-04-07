package com.practice.phuc.ums_husc.Helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.practice.phuc.ums_husc.Model.LOPHOCPHAN;

import java.util.Calendar;

public class ScheduleDailyNotification {
    public static Calendar getScheduleTime(LOPHOCPHAN myClass) {
        Calendar calendarSet = DateHelper.getCalendar();
        Calendar now = (Calendar) calendarSet.clone();

        calendarSet.set(Calendar.DAY_OF_WEEK, now.get(Calendar.DAY_OF_WEEK));
        calendarSet.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
        calendarSet.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + 1);
        calendarSet.set(Calendar.SECOND, 0);
        calendarSet.set(Calendar.MILLISECOND, 0);

        Log.d("DEBUG", "Alarm time: " + calendarSet.getTime().toString());
        Log.d("DEBUG", "Now: " + now.getTime().toString());

        if (calendarSet.compareTo(now) <= 0) {
            Log.d("DEBUG", "Alarm will schedule for next day!");
            calendarSet.add(Calendar.DAY_OF_YEAR, 1);
        }
        else{
            Log.d("DEBUG", "Alarm will schedule for today!");
        }
        return calendarSet;
    }

    public static int setUpScheduleAlarm(Context context, Calendar time) {
        int alarmId = Integer.parseInt(String.valueOf(time.getTimeInMillis() / 1000));

        AlarmManager alarmManager =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ScheduleReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        return alarmId;
    }
}
