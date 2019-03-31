package com.practice.phuc.ums_husc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

public class SettingFragment extends PreferenceFragment {
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private SwitchPreference mSpNews;
    private SwitchPreference mSpMessage;
    private SwitchPreference mSpTimeTable;
    private ListPreference mSpTimeTableTime;

    public static SettingFragment newInstance(Context context) {
        SettingFragment settingFragment = new SettingFragment();
        settingFragment.mContext = context;
        return settingFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = mContext.getSharedPreferences(getString(R.string.share_pre_key_setting), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        addPreferencesFromResource(R.xml.setting);
        // bind UI
        mSpNews = (SwitchPreference) findPreference(getString(R.string.pre_key_news));
        mSpMessage = (SwitchPreference) findPreference(getString(R.string.pre_key_message));
        mSpTimeTable = (SwitchPreference) findPreference(getString(R.string.pre_key_alarm_timetable));
        mSpTimeTableTime = (ListPreference) findPreference(getString(R.string.pre_key_alarm_timetable_time));
        // Get save value
        boolean mSpNewsChecked = mSharedPreferences.getBoolean(getString(R.string.share_pre_key_news), true);
        boolean mSpMessageChecked = mSharedPreferences.getBoolean(getString(R.string.share_pre_key_message), true);
        boolean mSpTimeTableChecked = mSharedPreferences.getBoolean(getString(R.string.share_pre_key_alarm_timetable), true);
        String mSpTimeTableTimeValue = mSharedPreferences.getString(getString(R.string.share_pre_key_alarm_timetable_time), "30");
        // Set value from saved value
        mSpNews.setChecked(mSpNewsChecked);
        mSpMessage.setChecked(mSpMessageChecked);
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
                mEditor.putBoolean(getString(R.string.share_pre_key_message), (boolean) newValue);
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
