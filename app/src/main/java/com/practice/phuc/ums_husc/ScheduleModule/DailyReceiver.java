package com.practice.phuc.ums_husc.ScheduleModule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.practice.phuc.ums_husc.Helper.DBHelper;
import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.SettingFragment;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG", "Daily receiver alarm !!!");

        boolean isAllow = SharedPreferenceHelper.getInstance()
                .getSharedPrefBool(context, SettingFragment.SHARED_SETTING, SettingFragment.SHARED_PRE_TIMETABLE_ALARM, true);

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

            ScheduleTaskHelper.getInstance().setTodayReminder(context, todayClasses);

            // Push notification
            if (isAllow) {
                ScheduleDailyNotification.riseNotification(
                        context, ((int) (Calendar.getInstance().getTimeInMillis() / 1000)),
                        ScheduleDailyNotification.createScheduleNotification(context, todayClasses));
            }
        }
    }
}
