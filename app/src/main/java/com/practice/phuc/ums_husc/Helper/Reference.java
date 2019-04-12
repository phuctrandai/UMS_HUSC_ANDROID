package com.practice.phuc.ums_husc.Helper;

import com.practice.phuc.ums_husc.Model.THONGBAO;

import java.util.ArrayList;
import java.util.List;

public final class Reference {

    private static final String HOST = "http://192.168.1.106:8082/";

    private static final String LOGIN_API = "api/SinhVien/DangNhap/";

    private static final String CHANGE_PASS_API = "api/SinhVien/DoiMatKhau";

    private static final String LOAD_LY_LICH_API = "api/SinhVien/LyLichCaNhan/";

    private static final String LOAD_THONG_BAO_API = "api/ThongBao/";

    private static final String LOAD_NOI_DUNG_THONG_BAO_API = "api/ThongBao/NoiDung/";

    private static final String LOAD_TIN_NHAN_DEN_API = "api/SinhVien/TinNhanDaNhan/";

    private static final String LOAD_TIN_NHAN_DA_GUI_API = "api/SinhVien/TinNhanDaGui/";

    private static final String LOAD_TIN_NHAN_DA_XOA_API = "api/SinhVien/TinNhanDaXoa/";

    private static final String LOAD_NOI_DUNG_TIN_NHAN_API = "api/SinhVien/NoiDungTinNhan/";

    private static final String REPLY_TIN_NHAN_API = "api/SinhVien/";

    private static final String SAVE_TOKEN_API = "api/fcm/save/";

    private static final String DELETE_TOKEN_API = "api/fcm/delete/";

    public static String getLoginApiUrl(String maSinhVien, String matKhau) {
        return Reference.HOST + Reference.LOGIN_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau;
    }

    public static String getChangePassApiUrl(String maSinhVien, String matKhau) {
        return Reference.HOST + Reference.CHANGE_PASS_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau;
    }

    public static String getLoadThongBaoApiUrl(String maSinhVien, String matKhau, long currentPage, int itemPerPage) {
        return Reference.HOST + Reference.LOAD_THONG_BAO_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau
                + "&sotrang=" + currentPage
                + "&sodongmoitrang=" + itemPerPage;
    }

    public static String getLoadNoiDungThongBaoApiUrl(String maSinhVien, String matKhau, String id) {
        return Reference.HOST + Reference.LOAD_NOI_DUNG_THONG_BAO_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau
                + "&id=" + id;
    }

    public static String getLoadTinNhanDenApiUrl(String maSinhVien, String matKhau, long currentPage, int itemPerPage) {
        return Reference.HOST + Reference.LOAD_TIN_NHAN_DEN_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau
                + "&sotrang=" + currentPage
                + "&sodongmoitrang=" + itemPerPage;
    }

    public static String getLoadTinNhanDaGuiApiUrl(String maSinhVien, String matKhau, long currentPage, int itemPerPage) {
        return HOST + LOAD_TIN_NHAN_DA_GUI_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau
                + "&sotrang=" + currentPage
                + "&sodongmoitrang=" + itemPerPage;
    }

    public static String getLoadTinNhanDaXoaApiUrl(String maSinhVien, String matKhau, long currentPage, int itemPerPage) {
        return HOST + LOAD_TIN_NHAN_DA_XOA_API +
                "?masinhvien=" + maSinhVien +
                "&matkhau=" + matKhau +
                "&sotrang=" + currentPage +
                "&sodongmoitrang=" + itemPerPage;
    }

    public static String getLoadNoiDungTinNhanApiUrl(String maSinhVien, String matKhau, String id) {
        return Reference.HOST + Reference.LOAD_NOI_DUNG_TIN_NHAN_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau
                + "&id=" + id;
    }

    public static String getReplyTinNhanApiUrl(String maSinhVien, String matKhau) {
        return HOST + REPLY_TIN_NHAN_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau;
    }

    public static String getLoadLyLichApiUrl(String maSinhVien, String matKhau) {
        return Reference.HOST + Reference.LOAD_LY_LICH_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau;
    }

    static String getSaveTokenApiUrl(String maSinhVien, String token) {
        return Reference.HOST + Reference.SAVE_TOKEN_API
                + "?masinhvien=" + maSinhVien
                + "&token=" + token;
    }

    static String getDeleteTokenApiUrl(String maSinhVien, String token) {
        return Reference.HOST + Reference.DELETE_TOKEN_API
                + "?masinhvien=" + maSinhVien
                + "&token=" + token;
    }

    static String MESSAGE_NOTIFICATION = "message_notification";
    static String NEWS_NOTIFICATION = "news_notification";
    static String SCHEDULE_NOTIFICATION = "schedule_notification";
    public static String SEND_MESSAGE_NOTIFICATION = "send_message_notification";

    public static boolean mHasNewNews = false;
    private static List<THONGBAO> mListNewThongBao = null;
    public static List<THONGBAO> getmListNewThongBao() {
        if (mListNewThongBao == null) {
            mListNewThongBao = new ArrayList<>();
        }
        return mListNewThongBao;
    }

    public static String BUNDLE_EXTRA_NEWS = "news";
    public static String BUNDLE_KEY_NEWS_TITLE = "news_title";
    public static String BUNDLE_KEY_NEWS_BODY = "news_body";
    public static String BUNDLE_KEY_NEWS_POST_TIME = "news_post_time";
    public static String BUNDLE_KEY_NEWS_ID = "news_id";
    public static String BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI = "launch_from_noti";

    public static String BUNDLE_EXTRA_MESSAGE = "message";
    public static String BUNDLE_KEY_MESSAGE_TITLE = "message_title";
    public static String BUNDLE_KEY_MESSAGE_BODY = "message_body";
    public static String BUNDLE_KEY_MESSAGE_SEND_TIME = "message_send_time";
    public static String BUNDLE_KEY_MESSAGE_ID = "message_id";
    public static String BUNDLE_KEY_MESSAGE_RECEIVERS = "message_receivers";
    public static String BUNDLE_KEY_MESSAGE_RECEIVER_NAMES = "message_receiver_names";
    public static String BUNDLE_KEY_MESSAGE_SENDER_NAME = "message_sender";
    public static String BUNDLE_KEY_MESSAGE_SENDER_ID = "message_sender_id";
    public static String BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI = "launch_from_noti";

}
