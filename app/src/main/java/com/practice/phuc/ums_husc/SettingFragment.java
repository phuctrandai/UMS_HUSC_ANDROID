package com.practice.phuc.ums_husc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.View;

import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.ScheduleModule.DailyReceiver;
import com.practice.phuc.ums_husc.ScheduleModule.ScheduleDailyNotification;
import com.practice.phuc.ums_husc.ScheduleModule.SelectingSemesterActivity;

public class SettingFragment extends PreferenceFragmentCompat {
    public static String SHARED_SETTING = "setting";
    public static String SHARED_PRE_NEWS_NOTI = "sp_notification";
    public static String SHARED_PRE_MESSAGE_NOTI = "sp_message";
    public static String SHARED_PRE_SEND_MESSAGE_NOTI = "sp_send_message";
    public static String SHARED_PRE_TIMETABLE_ALARM = "sp_alarm_timetabel";
    public static String SHARED_PRE_TIMETABLE_ALARM_TIME = "sp_alarm_timetable_time";

    private Context mContext;
    private Preference mPSemester;
    private SwitchPreferenceCompat mSpNews;
    private SwitchPreferenceCompat mSpMessage;
    private SwitchPreferenceCompat mSpSendMessage;
    private SwitchPreferenceCompat mSpTimeTable;
    private ListPreference mSpTimeTableTime;

    public static SettingFragment newInstance(Context context) {
        SettingFragment settingFragment = new SettingFragment();
        settingFragment.mContext = context;
        return settingFragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String s) {
        setPreferencesFromResource(R.xml.setting, s);
        bindUI();
        initData();
        // Set Events
        mSpNews.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mSpNews.setDefaultValue(newValue);
                SharedPreferenceHelper.getInstance()
                        .setSharedPref(mContext, SHARED_SETTING, SHARED_PRE_NEWS_NOTI, (boolean) newValue);
                return true;
            }
        });
        mSpMessage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mSpMessage.setDefaultValue(newValue);
                SharedPreferenceHelper.getInstance()
                        .setSharedPref(mContext, SHARED_SETTING, SHARED_PRE_MESSAGE_NOTI, (boolean) newValue);
                return true;
            }
        });
        mSpSendMessage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mSpSendMessage.setDefaultValue(newValue);
                SharedPreferenceHelper.getInstance()
                        .setSharedPref(mContext, SHARED_SETTING, SHARED_PRE_SEND_MESSAGE_NOTI, (boolean) newValue);
                return true;
            }
        });
        mSpTimeTable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mSpTimeTableTime.setEnabled((boolean) newValue);
                mSpTimeTable.setDefaultValue(newValue);
                SharedPreferenceHelper.getInstance()
                        .setSharedPref(mContext, SHARED_SETTING, SHARED_PRE_TIMETABLE_ALARM, (boolean) newValue);
                if ((boolean) newValue) {
                    ScheduleDailyNotification.setDailyReminder(mContext, 1501, DailyReceiver.class,0, 0);

                } else {
                    ScheduleDailyNotification.cancelReminder(mContext, 1501, DailyReceiver.class);
                }
                return true;
            }
        });
        mSpTimeTableTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String title = "Nhắc tôi trước giờ học " + newValue + " phút";
                mSpTimeTableTime.setDefaultValue(newValue);
                mSpTimeTableTime.setValue(newValue + "");
                mSpTimeTableTime.setTitle(title);
                SharedPreferenceHelper.getInstance()
                        .setSharedPref(mContext, SHARED_SETTING, SHARED_PRE_TIMETABLE_ALARM_TIME, Integer.parseInt(newValue + ""));
                return true;
            }
        });
        mPSemester.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(mContext, SelectingSemesterActivity.class);
                startActivityForResult(intent, 0);
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String mSemesterString = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(mContext, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER_STR, "");
        mPSemester.setTitle(mSemesterString);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String hocKyStr = SharedPreferenceHelper.getInstance()
                    .getSharedPrefStr(mContext,
                            SharedPreferenceHelper.ACCOUNT_SP,
                            SharedPreferenceHelper.STUDENT_SEMSTER_STR, "");
            mPSemester.setTitle(hocKyStr);
        }
    }

    private void bindUI() {
        mPSemester = findPreference("pre_key_semester");
        mSpNews = (SwitchPreferenceCompat) findPreference("pre_key_news");
        mSpMessage = (SwitchPreferenceCompat) findPreference("pre_key_receive_message");
        mSpSendMessage = (SwitchPreferenceCompat) findPreference("pre_key_send_message");
        mSpTimeTable = (SwitchPreferenceCompat) findPreference("pre_key_alarm_timetable");
        mSpTimeTableTime = (ListPreference) findPreference("pre_key_alarm_timetable_time");
    }

    private void initData() {
        // Get save value
        String mSemesterString = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(mContext, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER_STR, "");
        boolean mSpNewsChecked = SharedPreferenceHelper.getInstance()
                .getSharedPrefBool(mContext, SHARED_SETTING, SHARED_PRE_NEWS_NOTI, true);
        boolean mSpMessageChecked = SharedPreferenceHelper.getInstance()
                .getSharedPrefBool(mContext, SHARED_SETTING, SHARED_PRE_MESSAGE_NOTI, true);
        boolean mSpSendMessageChecked = SharedPreferenceHelper.getInstance()
                .getSharedPrefBool(mContext, SHARED_SETTING, SHARED_PRE_SEND_MESSAGE_NOTI, true);
        boolean mSpTimeTableChecked = SharedPreferenceHelper.getInstance()
                .getSharedPrefBool(mContext, SHARED_SETTING, SHARED_PRE_TIMETABLE_ALARM, true);
        int mSpTimeTableTimeValue = SharedPreferenceHelper.getInstance()
                .getSharedPrefInt(mContext, SHARED_SETTING, SHARED_PRE_TIMETABLE_ALARM_TIME, 30);
        // Set value from saved value
        mPSemester.setTitle(mSemesterString);
        mSpNews.setChecked(mSpNewsChecked);
        mSpMessage.setChecked(mSpMessageChecked);
        mSpSendMessage.setChecked(mSpSendMessageChecked);
        mSpTimeTable.setChecked(mSpTimeTableChecked);
        mSpTimeTableTime.setValue(mSpTimeTableTimeValue + "");
        mSpTimeTableTime.setEnabled(mSpTimeTableChecked);
        String title = "Nhắc tôi trước giờ học " + mSpTimeTableTimeValue + " phút";
        mSpTimeTableTime.setTitle(title);
    }
}
