package com.practice.phuc.ums_husc.Helper;

import android.os.AsyncTask;

import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public class ScheduleTaskHelper {

    private ScheduleTaskHelper() {
    }

    private static ScheduleTaskHelper instance;

    public static ScheduleTaskHelper getInstance() {
        instance = instance == null ? new ScheduleTaskHelper() : instance;
        return instance;
    }

    public void fetchSchedule(String maSinhVien, String matKhau, int maHocKy){
        new ScheduleTask(maSinhVien, matKhau, maHocKy).execute((String) null);
    }


    private static class ScheduleTask extends AsyncTask<String, Void, Boolean> {
        private int maHocKy;
        private String maSinhVien;
        private String matKhau;

        private ScheduleTask(String maSinhVien, String matKhau, int maHocKy) {
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

                }
            }
        }
    }
}
