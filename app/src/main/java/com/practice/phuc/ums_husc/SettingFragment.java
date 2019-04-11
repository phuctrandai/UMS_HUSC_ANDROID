package com.practice.phuc.ums_husc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.practice.phuc.ums_husc.Helper.ScheduleDailyNotification;
import com.practice.phuc.ums_husc.Helper.ScheduleReceiver;

public class SettingFragment extends PreferenceFragmentCompat {
    private Context mContext;
    private SharedPreferences.Editor mEditor;
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
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(getString(R.string.share_pre_key_setting), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        // bind UI
        mSpNews = (SwitchPreferenceCompat) findPreference(getString(R.string.pre_key_news));
        mSpMessage = (SwitchPreferenceCompat) findPreference(getString(R.string.pre_key_receive_message));
        mSpSendMessage = (SwitchPreferenceCompat) findPreference(getString(R.string.pre_key_send_message));
        mSpTimeTable = (SwitchPreferenceCompat) findPreference(getString(R.string.pre_key_alarm_timetable));
        mSpTimeTableTime = (ListPreference) findPreference(getString(R.string.pre_key_alarm_timetable_time));
        // Get save value
        boolean mSpNewsChecked = mSharedPreferences.getBoolean(getString(R.string.share_pre_key_news), true);
        boolean mSpMessageChecked = mSharedPreferences.getBoolean(getString(R.string.share_pre_key_receive_message), true);
        boolean mSpSendMessageChecked = mSharedPreferences.getBoolean(getString(R.string.share_pre_key_send_message), true);
        boolean mSpTimeTableChecked = mSharedPreferences.getBoolean(getString(R.string.share_pre_key_alarm_timetable), true);
        String mSpTimeTableTimeValue = mSharedPreferences.getString(getString(R.string.share_pre_key_alarm_timetable_time), "30");
        // Set value from saved value
        mSpNews.setChecked(mSpNewsChecked);
        mSpMessage.setChecked(mSpMessageChecked);
        mSpSendMessage.setChecked(mSpSendMessageChecked);
        mSpTimeTable.setChecked(mSpTimeTableChecked);
        mSpTimeTableTime.setValue(mSpTimeTableTimeValue);
        mSpTimeTableTime.setEnabled(mSpTimeTableChecked);
        String title = getString(R.string.pre_title_alarm_timetable_time) + " " + mSpTimeTableTimeValue + " phút";
        mSpTimeTableTime.setTitle(title);
        // Set Events
        mSpNews.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mSpNews.setDefaultValue(newValue);
                mEditor.putBoolean(getString(R.string.share_pre_key_news),(boolean) newValue);
                mEditor.apply();
                return true;
            }
        });
        mSpMessage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mSpMessage.setDefaultValue(newValue);
                mEditor.putBoolean(getString(R.string.share_pre_key_receive_message), (boolean) newValue);
                mEditor.apply();
                return true;
            }
        });
        mSpSendMessage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mSpSendMessage.setDefaultValue(newValue);
                mEditor.putBoolean(getString(R.string.share_pre_key_send_message), (boolean) newValue);
                mEditor.apply();
                return true;
            }
        });
        mSpTimeTable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mSpTimeTableTime.setEnabled((boolean) newValue);
                mSpTimeTable.setDefaultValue(newValue);
                mEditor.putBoolean(getString(R.string.share_pre_key_alarm_timetable), (boolean) newValue);
                mEditor.apply();
                if ((boolean) newValue) {
                    ScheduleDailyNotification.setUpScheduleAlarm(mContext, ScheduleDailyNotification.getScheduleTime(null));
                } else {
                    AlarmManager alarmManager =(AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(mContext, ScheduleReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                }
                return true;
            }
        });
        mSpTimeTableTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String title = getString(R.string.pre_title_alarm_timetable_time) + " " + newValue + " phút";
                mSpTimeTableTime.setDefaultValue(newValue);
                mSpTimeTableTime.setValue(newValue + "");
                mSpTimeTableTime.setTitle(title);
                mEditor.putString(getString(R.string.share_pre_key_alarm_timetable_time), newValue + "");
                mEditor.apply();
                return true;
            }
        });
    }
}
