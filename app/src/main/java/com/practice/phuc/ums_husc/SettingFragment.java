package com.practice.phuc.ums_husc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.ScheduleDailyNotification;
import com.practice.phuc.ums_husc.Helper.ScheduleReceiver;
import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.ViewModel.VHocKy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.Response;

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
                    ScheduleDailyNotification.setReminder(mContext, 1, ScheduleReceiver.class, 10, 0);
                    ScheduleDailyNotification.setReminder(mContext, 2, ScheduleReceiver.class, 11, 0);
                    ScheduleDailyNotification.setReminder(mContext, 3, ScheduleReceiver.class, 11, 20);
                    ScheduleDailyNotification.setReminder(mContext, 4, ScheduleReceiver.class, 11, 30);
                    ScheduleDailyNotification.setReminder(mContext, 5, ScheduleReceiver.class, 13, 0);
                    ScheduleDailyNotification.setReminder(mContext, 6, ScheduleReceiver.class, 14, 0);
                    ScheduleDailyNotification.setReminder(mContext, 7, ScheduleReceiver.class, 15, 0);

                } else {

                    for (int i = 0; i < 10; i++) {
                        ScheduleDailyNotification.cancelReminder(mContext, i, ScheduleReceiver.class);
                    }

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
                        .setSharedPref(mContext, SHARED_SETTING, SHARED_PRE_TIMETABLE_ALARM_TIME, newValue + "");
                return true;
            }
        });
        initSemsterDialog();
        mPSemester.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showSelectSemesterDialog();
                return true;
            }
        });
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
                .getSharedPrefStr(mContext, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER_STR, null);
        boolean mSpNewsChecked = SharedPreferenceHelper.getInstance()
                .getSharedPrefBool(mContext, SHARED_SETTING, SHARED_PRE_NEWS_NOTI, true);
        boolean mSpMessageChecked = SharedPreferenceHelper.getInstance()
                .getSharedPrefBool(mContext, SHARED_SETTING, SHARED_PRE_MESSAGE_NOTI, true);
        boolean mSpSendMessageChecked = SharedPreferenceHelper.getInstance()
                .getSharedPrefBool(mContext, SHARED_SETTING, SHARED_PRE_SEND_MESSAGE_NOTI, true);
        boolean mSpTimeTableChecked = SharedPreferenceHelper.getInstance()
                .getSharedPrefBool(mContext, SHARED_SETTING, SHARED_PRE_TIMETABLE_ALARM, true);
        String mSpTimeTableTimeValue = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(mContext, SHARED_SETTING, SHARED_PRE_TIMETABLE_ALARM_TIME, "30");
        // Set value from saved value
        mPSemester.setTitle(mSemesterString);
        mSpNews.setChecked(mSpNewsChecked);
        mSpMessage.setChecked(mSpMessageChecked);
        mSpSendMessage.setChecked(mSpSendMessageChecked);
        mSpTimeTable.setChecked(mSpTimeTableChecked);
        mSpTimeTableTime.setValue(mSpTimeTableTimeValue);
        mSpTimeTableTime.setEnabled(mSpTimeTableChecked);
        String title = "Nhắc tôi trước giờ học " + mSpTimeTableTimeValue + " phút";
        mSpTimeTableTime.setTitle(title);
    }

    private ArrayAdapter<VHocKy> semesterAdapter;
    private int selectedSemesterIndex;
    private int attempSelectedSemesterIndex;

    private void initSemsterDialog() {
        String semesterId = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(mContext, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER, null);
        int index = Integer.parseInt(semesterId != null ? semesterId : "0");

        List<VHocKy> semesters = new ArrayList<>();
        semesters.add(new VHocKy("12", "2", "5", "2018", "2019"));
        semesters.add(new VHocKy("11", "1", "5", "2018", "2019"));
        semesters.add(new VHocKy("10", "3", "4", "2017", "2018"));
        semesters.add(new VHocKy("9", "2", "4", "2017", "2018"));
        semesters.add(new VHocKy("8", "1", "4", "2017", "2018"));
        semesters.add(new VHocKy("7", "3", "3", "2016", "2017"));
        semesters.add(new VHocKy("6", "2", "3", "2016", "2017"));
        semesters.add(new VHocKy("5", "1", "3", "2016", "2017"));
        semesters.add(new VHocKy("3", "3", "1", "2015", "2016"));
        semesters.add(new VHocKy("2", "2", "1", "2015", "2016"));
        semesters.add(new VHocKy("1", "1", "1", "2015", "2016"));
        semesterAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_single_choice, semesters);

        for (int i = 0; i < semesters.size(); i++) {
            if (semesters.get(i).MaHocKy.equals(semesterId)) {
                index = i;
                break;
            }
        }
        selectedSemesterIndex = index;
    }

    private void showSelectSemesterDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Chọn học kì tác nghiệp");
        builder.setSingleChoiceItems(semesterAdapter, selectedSemesterIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                attempSelectedSemesterIndex = index;
            }
        });
        builder.setPositiveButton("Tác nghiệp", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (NetworkUtil.getConnectivityStatus(mContext) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    Toasty.custom(mContext, getString(R.string.error_network_disconected),
                            getResources().getDrawable(R.drawable.ic_signal_wifi_off_white_24),
                            getResources().getColor(R.color.colorRed),
                            getResources().getColor(android.R.color.white),
                            Toasty.LENGTH_SHORT, true, true)
                            .show();
                    return;
                }
                String hocKy = Objects.requireNonNull(semesterAdapter.getItem(attempSelectedSemesterIndex)).toString();
                mPSemester.setTitle(hocKy);
                SharedPreferenceHelper.getInstance()
                        .setSharedPref(mContext, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER_STR, hocKy);
                String maHocKyStr = Objects.requireNonNull(semesterAdapter.getItem(attempSelectedSemesterIndex)).MaHocKy;
                new TacNghiepTask(maHocKyStr).execute();

            }
        });
        builder.setNegativeButton("Thôi", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class TacNghiepTask extends AsyncTask<String, Void, Boolean> {
        String responseMessage;
        String maHocKy;

        TacNghiepTask(String maHocKy) {
            this.maHocKy = maHocKy;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String url = Reference.HOST + "api/sinhvien/hocky/tacnghiep" +
                    "?masinhvien=" + Reference.getStudentId(mContext) +
                    "&matkhau=" + Reference.getAccountPassword(mContext) +
                    "&mahocky=" + maHocKy;
            Response response = NetworkUtil.makeRequest(url, false, null);
            Log.d("DEBUG", url);
            if (response == null) {
                responseMessage = "Không tìm thấy máy chủ";
                return false;
            }

            if (response.code() == NetworkUtil.OK) {
                responseMessage = "Tác nghiệp thành công";
                return true;
            }

            responseMessage = "Có lỗi xảy ra, thử lại sau";
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                Toasty.success(mContext, responseMessage, Toasty.LENGTH_SHORT).show();
                SharedPreferenceHelper.getInstance()
                        .setSharedPref(mContext, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER, maHocKy);
                selectedSemesterIndex = attempSelectedSemesterIndex;

            } else {
                Toasty.error(mContext, responseMessage, Toasty.LENGTH_SHORT).show();
            }
        }
    }
}
