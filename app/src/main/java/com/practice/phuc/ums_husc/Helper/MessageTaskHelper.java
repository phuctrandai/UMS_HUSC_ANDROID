package com.practice.phuc.ums_husc.Helper;

import android.os.AsyncTask;
import android.util.Log;

import com.practice.phuc.ums_husc.Model.TINNHAN;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class MessageTaskHelper {
    private MessageTaskHelper() {
    }

    private static MessageTaskHelper instance;

    public static MessageTaskHelper getInstance() {
        if (instance == null) {
            instance = new MessageTaskHelper();
            instance.mAttempDeleteMessage = new ArrayList<>();
        }
        return instance;
    }

    private List<TINNHAN> mAttempDeleteMessage;

    public List<TINNHAN> getAttempDeletemessage() {
        return mAttempDeleteMessage;
    }

    public void insertAttempDeleteMessage(TINNHAN message) {
        mAttempDeleteMessage.add(message);
    }

    public void removeAttempDeleteMessage(TINNHAN message) {
        for (int i = 0; i < mAttempDeleteMessage.size(); i++) {
            if (mAttempDeleteMessage.get(i).getMaTinNhan() == message.getMaTinNhan()) {
                mAttempDeleteMessage.remove(i);
                break;
            }
        }
    }

    public void updateSeenTime(int messageId, String maSinhVien, String matKhau) {
        String url = Reference.getUpdateThoiDiemXemTinNhanApiUrl(maSinhVien, matKhau, String.valueOf(messageId));

        new Task().execute(url);
    }

    public void attempDelete(int messageId, String maSinhVien, String matKhau) {
        int count = MessageTaskHelper.getInstance().mAttempDeleteMessage.size();
        if (count > 0) {
            String url = Reference.getAttempDeleteTinNhanApiUrl(maSinhVien, matKhau, String.valueOf(messageId));
            Log.d("DEBUG", "Request to server: " + url);
            new Task().execute(url);
        } else {
            Log.d("DEBUG", "Nothing to request");
        }
    }

    public void foreverDelete(int messageId, String maSinhVien, String matKhau) {
        String url = Reference.getForeverDeleteTinNhanApiUrl(maSinhVien, matKhau, String.valueOf(messageId));
        Log.d("DEBUG", "Request to server: " + url);

        new Task().execute(url);
    }

    static class Task extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                String url = strings[0];

                Response response = NetworkUtil.makeRequest(url, false, null);

                if (response == null) {
                    Log.d("DEBUG", "Máy chủ không phản hồi");
                    return false;
                }

                if (response.code() == NetworkUtil.OK) {
                    Log.d("DEBUG", "Progress Ok !!!");
                    return true;

                } else if (response.code() == NetworkUtil.BAD_REQUEST) {
                    Log.d("DEBUG", "Progress Not Ok: " +
                            (response.body() != null ? response.body().string() : ""));
                    return false;

                } else {
                    Log.d("DEBUG", "Không tìm thấy máy chủ: "
                    + (response.body() != null ? response.body().string() : ""));
                    return false;
                }
            } catch (Exception ex) {
                Log.e("DEBUG", "Progress fail with error:");
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success)
                MessageTaskHelper.getInstance().mAttempDeleteMessage.clear();
            super.onPostExecute(success);
        }
    }
}
