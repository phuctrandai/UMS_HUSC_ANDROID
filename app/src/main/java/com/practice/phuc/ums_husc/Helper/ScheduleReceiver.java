package com.practice.phuc.ums_husc.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG", "ALARM !!!!!!!!!!!");

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
            ScheduleDailyNotification.riseNotification(
                    context, ((int) (Calendar.getInstance().getTimeInMillis() / 1000)),
                    ScheduleDailyNotification.createScheduleNotification(context, todayClasses));
        }


    }
}