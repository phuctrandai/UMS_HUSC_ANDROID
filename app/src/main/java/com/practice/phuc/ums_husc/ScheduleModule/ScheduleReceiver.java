package com.practice.phuc.ums_husc.ScheduleModule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.Helper.StringHelper;
import com.practice.phuc.ums_husc.SettingFragment;

import static com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper.ACCOUNT_SP;
import static com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper.STUDENT_ID;

public class ScheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG", "Schedule Receiver Alarm !!!");

        String studentId = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(context, ACCOUNT_SP, STUDENT_ID, "");


        boolean isAllow = SharedPreferenceHelper.getInstance()
                .getSharedPrefBool(context, SettingFragment.SHARED_SETTING,
                        SettingFragment.SHARED_PRE_TIMETABLE_ALARM, true);


        if (!StringHelper.isNullOrEmpty(studentId) && isAllow) {
            Intent alarmIntent = new Intent(context, ScheduleAlarmActivity.class);
            alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(alarmIntent);
        }
    }
}