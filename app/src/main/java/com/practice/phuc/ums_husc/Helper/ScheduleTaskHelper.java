package com.practice.phuc.ums_husc.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.io.IOException;
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

    public void fetchSchedule(Context context, String maSinhVien, String matKhau, int maHocKy){
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
                    ScheduleDailyNotification.setReminder(mContext, 1, ScheduleReceiver.class, 18, 0);
                    ScheduleDailyNotification.setReminder(mContext, 2, ScheduleReceiver.class, 19, 0);
                    ScheduleDailyNotification.setReminder(mContext, 3, ScheduleReceiver.class, 20, 0);
                    ScheduleDailyNotification.setReminder(mContext, 4, ScheduleReceiver.class, 21, 0);
                    ScheduleDailyNotification.setReminder(mContext, 5, ScheduleReceiver.class, 0, 0);
                }
            }
        }
    }
}
