package com.practice.phuc.ums_husc.ScheduleModule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.practice.phuc.ums_husc.Helper.DBHelper;
import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.SettingFragment;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Response;

public class ScheduleTaskHelper {

    private DBHelper mDBHelper;

    private ScheduleTaskHelper() {
    }

    private static ScheduleTaskHelper instance;

    public static ScheduleTaskHelper getInstance() {
        instance = instance == null ? new ScheduleTaskHelper() : instance;
        return instance;
    }

    public void fetchSchedule(Context context, String maSinhVien, String matKhau, int maHocKy) {
        mDBHelper = new DBHelper(context);
        new ScheduleTask(context, maSinhVien, matKhau, maHocKy).execute((String) null);
    }

    @SuppressLint("StaticFieldLeak")
    private class ScheduleTask extends AsyncTask<String, Void, Boolean> {
        private Context mContext;
        private int maHocKy;
        private String maSinhVien;
        private String matKhau;

        private ScheduleTask(Context context, String maSinhVien, String matKhau, int maHocKy) {
            this.mContext = context;
            this.maSinhVien = maSinhVien;
            this.matKhau = matKhau;
            this.maHocKy = maHocKy;
        }

        private String json;

        @Override
        protected Boolean doInBackground(String... strings) {
            String url = Reference.HOST + "api/sinhvien/thoikhoabieu" +
                    "?masinhvien=" + maSinhVien +
                    "&matkhau=" + matKhau +
                    "&mahocky=" + maHocKy;
            Response response = NetworkUtil.makeRequest(url, false, null);

            if (response != null && response.code() == NetworkUtil.OK) {
                try {
                    json = response.body() != null ? response.body().string() : "";

                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                List<ThoiKhoaBieu> thoiKhoaBieus = ThoiKhoaBieu.fromJsonToList(json);

                if (thoiKhoaBieus != null && thoiKhoaBieus.size() > 0) {
                    mDBHelper.deleteAllRecord(DBHelper.SCHEDULE);
                    mDBHelper.insertSchedule(thoiKhoaBieus);
                    ScheduleDailyNotification.setDailyReminder(mContext, 1501, DailyReceiver.class, 0, 0);

                    setTodayReminder(mContext);
                }
            }
        }
    }

    List<ThoiKhoaBieu> setTodayReminder(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        Date now = DateHelper.getCalendar().getTime();
        int dayOfMonth = DateHelper.getDayOfMonth(now);
        int month = DateHelper.getMonth(now);
        int year = DateHelper.getYear(now);
        String dayOfMonthStr = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
        String monthStr = month < 10 ? "0" + month : month + "";
        String dateStr = year + "-" + monthStr + "-" + dayOfMonthStr;
        List<ThoiKhoaBieu> todayClasses = dbHelper.getSchedule(dateStr);

        if (todayClasses.size() > 0) {

            for (ThoiKhoaBieu item : todayClasses) {
                int gioBatDau = item.getLessionStartHour();
                int phutBao = item.getLessionStartMinute();
                int thoiGianBaoTruoc = SharedPreferenceHelper.getInstance()
                        .getSharedPrefInt(context, SettingFragment.SHARED_SETTING,
                                SettingFragment.SHARED_PRE_TIMETABLE_ALARM_TIME, 30);

                Calendar calendar = DateHelper.getCalendar();
                calendar.set(Calendar.HOUR_OF_DAY, gioBatDau);
                calendar.set(Calendar.MINUTE, phutBao);
                calendar.add(Calendar.MINUTE, -thoiGianBaoTruoc);

                ScheduleDailyNotification.setOneShotReminder(context, gioBatDau,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), item);
            }
            return todayClasses;
        }
        return null;
    }
}
