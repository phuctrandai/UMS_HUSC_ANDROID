package com.practice.phuc.ums_husc.ScheduleModule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.practice.phuc.ums_husc.Helper.DBHelper;
import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG", "Daily receiver alarm !!!");

        DBHelper mDBHelper = new DBHelper(context);
        Date now = DateHelper.getCalendar().getTime();
        int dayOfMonth = DateHelper.getDayOfMonth(now);
        int month = DateHelper.getMonth(now);
        int year = DateHelper.getYear(now);
        String dayOfMonthStr = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
        String monthStr = month < 10 ? "0" + month : month + "";
        String dateStr = year + "-" + monthStr + "-" + dayOfMonthStr;
        List<ThoiKhoaBieu> todayClasses = mDBHelper.getSchedule(dateStr);

        if (todayClasses.size() > 0) {

            // Alarm
            int code = 0;
            for (ThoiKhoaBieu item : todayClasses) {
                ScheduleDailyNotification.setReminder(context, code, ScheduleReceiver.class,
                        -1, 7, 10 + code);
                code++;
            }

            // Push notification
            ScheduleDailyNotification.riseNotification(
                    context, ((int) (Calendar.getInstance().getTimeInMillis() / 1000)),
                    ScheduleDailyNotification.createScheduleNotification(context, todayClasses));
        }
    }
}
