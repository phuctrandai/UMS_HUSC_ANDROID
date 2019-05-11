package com.practice.phuc.ums_husc.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public final class Reference {
    private Reference() {
    }

    private static Reference instance;

    public static Reference getInstance() {
        if (instance == null) instance = new Reference();
        return instance;
    }

    /* SERVER */
    private final String SERVER_SP = "server";
    private final String ADDRESS_SP_KEY = "address";
    private final String PORT_SP_KEY = "port";

    private String DEFAULT_ADDRESS = "192.168.1.106";

    private String DEFAULT_PORT = "8082";

    private String DEFAULT_HOST = "http://" + DEFAULT_ADDRESS + ":" + DEFAULT_PORT + "/";

    public String getServerAddress(Context context) {
        if (context == null)
            return DEFAULT_ADDRESS;
        else {
            return SharedPreferenceHelper.getInstance()
                    .getSharedPrefStr(context, SERVER_SP, ADDRESS_SP_KEY, DEFAULT_ADDRESS);
        }
    }

    public String getServerPort(Context context) {
        if (context == null)
            return DEFAULT_PORT;
        else {
            return SharedPreferenceHelper.getInstance()
                    .getSharedPrefStr(context, SERVER_SP, PORT_SP_KEY, DEFAULT_PORT);
        }
    }

    public String getHost(Context context) {
        if (context == null)
            return DEFAULT_HOST;
        else {
            return "http://" + getServerAddress(context) + ":" + getServerPort(context) + "/";
        }
    }

    public void setServerAddress(Context context, String value) {
        SharedPreferenceHelper.getInstance()
                .setSharedPref(context, SERVER_SP, ADDRESS_SP_KEY, value);
    }

    public void setServerPort(Context context, String value) {
        SharedPreferenceHelper.getInstance()
                .setSharedPref(context, SERVER_SP, PORT_SP_KEY, value);
    }

    /* NEWS */

    public boolean mHasNewNews = false;
    private List<THONGBAO> mListNewThongBao = null;

    public List<THONGBAO> getmListNewThongBao() {
        if (mListNewThongBao == null) mListNewThongBao = new ArrayList<>();
        return mListNewThongBao;
    }

    public void clearListNewThongBao() {
        if (mListNewThongBao != null) mListNewThongBao.clear();
    }

    /* MESSAGE */

    public boolean mHasNewReceivedMessage = false;
    private List<TINNHAN> mListNewReceivedMessage = null;

    public List<TINNHAN> getListNewReceivedMessage() {
        if (mListNewReceivedMessage == null)
            mListNewReceivedMessage = new ArrayList<>();
        return mListNewReceivedMessage;
    }

    public void clearListNewReceivedMessage() {
        if (mListNewReceivedMessage != null) mListNewReceivedMessage.clear();
    }

    public boolean mHasNewSentMessage = false;
    private List<TINNHAN> mListNewSentMessage = null;

    public List<TINNHAN> getListNewSentMessage() {
        if (mListNewSentMessage == null)
            mListNewSentMessage = new ArrayList<>();
        return mListNewSentMessage;
    }

    public void clearListNewSentMessage() {
        if (mListNewSentMessage != null)
            mListNewSentMessage.clear();
    }

    /* FIRE BASE */

    public String getSaveTokenApiUrl(Context context, String maSinhVien, String token) {
        String SAVE_TOKEN_API = "api/FCM/Excute/Save/";
        return getHost(context) + SAVE_TOKEN_API
                + "?masinhvien=" + maSinhVien
                + "&token=" + token;
    }

    public String getDeleteTokenApiUrl(Context context, String maSinhVien, String token) {
        String DELETE_TOKEN_API = "api/FCM/Excute/Delete/";
        return getHost(context) + DELETE_TOKEN_API
                + "?masinhvien=" + maSinhVien
                + "&token=" + token;
    }

    /* NOTIFICATION */

    public final String MESSAGE_NOTIFICATION = "message_notification";
    public final String NEWS_NOTIFICATION = "news_notification";
    public final String SCHEDULE_NOTIFICATION = "schedule_notification";
    public final String ADD_SCHEDULE_NOTIFICATION = "add_schedule_notification";
    public final String SEND_MESSAGE_NOTIFICATION = "send_message_notification";

    /* BUNDLE KEY */

    public final String BUNDLE_EXTRA_NEWS = "news";
    public final String BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI = "launch_from_news_noti";
    public final String BUNDLE_EXTRA_MESSAGE = "message";
    public final String BUNDLE_EXTRA_MESSAGE_REPLY = "reply";
    public final String BUNDLE_EXTRA_MESSAGE_FORWARD = "forward";
    public final String BUNDLE_EXTRA_MESSAGE_NEW = "new";
    public final String BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI = "launch_from_message_noti";

    public String getStudentId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        return sp.getString(context.getString(R.string.pre_key_student_id), "");
    }

    public String getAccountPassword(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        return sp.getString(context.getString(R.string.pre_key_password), "");
    }
}
