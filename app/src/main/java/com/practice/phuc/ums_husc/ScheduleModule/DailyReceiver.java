package com.practice.phuc.ums_husc.ScheduleModule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.SettingFragment;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.util.Calendar;
import java.util.List;

public class DailyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG", "Daily receiver alarm !!!");

        boolean isAllow = SharedPreferenceHelper.getInstance()
                .getSharedPrefBool(context, SettingFragment.SHARED_SETTING, SettingFragment.SHARED_PRE_TIMETABLE_ALARM, true);


        List<ThoiKhoaBieu> todayClasses = ScheduleTaskHelper.getInstance().setTodayReminder(context);

        // Push notification
        if (todayClasses != null && isAllow) {
            ScheduleDailyNotification.riseNotification(
                    context, ((int) (Calendar.getInstance().getTimeInMillis() / 1000)),
                    ScheduleDailyNotification.createScheduleNotification(context, todayClasses));
        }
    }
}
