package com.practice.phuc.ums_husc.MessageModule;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
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
            instance.mAttempRestoreSentMessage = new ArrayList<>();
            instance.mAttempRestoreReceivedMessage = new ArrayList<>();
        }
        return instance;
    }

    public void destroy() {
        mAttempRestoreSentMessage.clear();
        mAttempRestoreReceivedMessage.clear();
        mAttempDeleteMessage.clear();
        instance = null;
    }

    private List<TINNHAN> mAttempDeleteMessage;
    private List<TINNHAN> mAttempRestoreSentMessage;
    private List<TINNHAN> mAttempRestoreReceivedMessage;

    List<TINNHAN> getAttempDeletemessage() {
        return mAttempDeleteMessage;
    }
    List<TINNHAN> getAttempRestoreSentMessage() {
        return mAttempRestoreSentMessage;
    }
    List<TINNHAN> getAttempRestoreReceivedMessage() {
        return mAttempRestoreReceivedMessage;
    }

    void insertAttempDeleteMessage(TINNHAN message) {
        mAttempDeleteMessage.add(message);
    }

    void removeAttempDeleteMessage(TINNHAN message) {
        for (int i = 0; i < mAttempDeleteMessage.size(); i++) {
            if (mAttempDeleteMessage.get(i).MaTinNhan.equals(message.MaTinNhan)) {
                mAttempDeleteMessage.remove(i);
                break;
            }
        }
    }

    private void removeAttempDeleteMessage(String messageId) {
        for (int i = 0; i < mAttempDeleteMessage.size(); i++) {
            if (mAttempDeleteMessage.get(i).MaTinNhan.equals(messageId)) {
                mAttempDeleteMessage.remove(i);
                break;
            }
        }
    }

    void insertAttempRestoreSentMessage(TINNHAN message) {
        mAttempRestoreSentMessage.add(message);
    }

    private void removeAttempRestoreSentMessage(String messageId) {
        for (int i = 0; i < mAttempRestoreSentMessage.size(); i++) {
            if (mAttempRestoreSentMessage.get(i).MaTinNhan.equals(messageId)) {
                mAttempRestoreSentMessage.remove(i);
                break;
            }
        }
    }

    void insertAttempRestoreReceivedMessage(TINNHAN message) {
        mAttempRestoreReceivedMessage.add(message);
    }

    private void removeAttempRestoreReceivedMessage(String messageId) {
        for (int i = 0; i < mAttempRestoreReceivedMessage.size(); i++) {
            if (mAttempRestoreReceivedMessage.get(i).MaTinNhan.equals(messageId)) {
                mAttempRestoreReceivedMessage.remove(i);
                break;
            }
        }
    }

    void updateSeenTime(int messageId, String maSinhVien, String matKhau) {
        String url = Reference.getUpdateThoiDiemXemTinNhanApiUrl(maSinhVien, matKhau, String.valueOf(messageId));

        new Task(Task.DO_UPDATE_SEEN_TIME).execute(url);
    }

    void attempDelete(String messageId, String maSinhVien, String matKhau) {
        int count = MessageTaskHelper.getInstance().mAttempDeleteMessage.size();

        if (count > 0) {
            String url = Reference.getAttempDeleteTinNhanApiUrl(maSinhVien, matKhau, messageId);
            Log.e("DEBUG", "Request to server: " + url);
            new Task(Task.DO_ATTEMP_DELETE).execute(url, messageId);
        } else {
            Log.e("DEBUG", "Nothing to request");
        }
    }

    void foreverDelete(String messageId, String maSinhVien, String matKhau) {
        String url = Reference.getForeverDeleteTinNhanApiUrl(maSinhVien, matKhau, messageId);
        Log.e("DEBUG", "Request to server: " + url);

        new Task(Task.DO_FOREVER_DELETE).execute(url);
    }

    void restore(String messageId, String maSinhVien, String matKhau) {
        String url = Reference.getRestoreDeletedTinNhanApiUrl(maSinhVien, matKhau, messageId);
        Log.e("DEBUG", "Request to server: " + url);

        new Task(Task.DO_RESTORE).execute(url, messageId);
    }

    @SuppressLint("StaticFieldLeak")
    class Task extends AsyncTask<String, Void, Boolean> {
        private static final int DO_UPDATE_SEEN_TIME = -1;
        private static final int DO_ATTEMP_DELETE = 0;
        private static final int DO_FOREVER_DELETE = 1;
        private static final int DO_RESTORE = 2;

        private int order;
        private String messageId;

        Task(int order) {
            this.order = order;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                if (order == DO_ATTEMP_DELETE || order == DO_RESTORE)
                    messageId = strings[1];

                String url = strings[0];

                Response response = NetworkUtil.makeRequest(url, false, null);

                if (response == null) {
                    Log.e("DEBUG", "Máy chủ không phản hồi");
                    return false;
                }

                if (response.code() == NetworkUtil.OK) {
                    Log.e("DEBUG", "Progress Ok !!!");
                    return true;

                } else if (response.code() == NetworkUtil.BAD_REQUEST) {
                    Log.e("DEBUG", "Progress Not Ok: " +
                            (response.body() != null ? response.body().string() : ""));
                    return false;

                } else {
                    Log.e("DEBUG", "Không tìm thấy máy chủ: "
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
            if (success) {
                switch (order) {
                    case DO_ATTEMP_DELETE:
                        MessageTaskHelper.getInstance().removeAttempDeleteMessage(messageId);
                        break;

                    case DO_RESTORE:
                        MessageTaskHelper.getInstance().removeAttempRestoreSentMessage(messageId);
                        MessageTaskHelper.getInstance().removeAttempRestoreReceivedMessage(messageId);
                        break;
                }
            }
            super.onPostExecute(success);
        }
    }
}
