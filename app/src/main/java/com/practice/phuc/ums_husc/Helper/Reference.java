package com.practice.phuc.ums_husc.Helper;

import com.practice.phuc.ums_husc.Model.THONGBAO;

import java.util.ArrayList;
import java.util.List;

public final class Reference {

    private static final String HOST = "http://192.168.1.106:8082/";

    private static final String LOGIN_API = "api/sinhvien/dangnhap/";

    private static final String LOAD_LY_LICH_API = "api/SinhVien/LyLichCaNhan/";

    private static final String LOAD_THONG_BAO_API = "api/ThongBao/TatCa/";

    private static final String LOAD_NOI_DUNG_THONG_BAO_API = "api/ThongBao/NoiDung/";

    private static final String LOAD_TIN_NHAN_DEN_API = "api/SinhVien/tinnhandanhan/";

    private static final String LOAD_TIN_NHAN_DA_GUI_API = "api/SinhVien/tinnhandagui/";

    private static final String SAVE_TOKEN_API = "api/fcm/save/";

    private static final String DELETE_TOKEN_API = "api/fcm/delete/";

    public static String getLoginApiUrl(String maSinhVien, String matKhau) {
        return Reference.HOST + Reference.LOGIN_API
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
        return Reference.HOST + Reference.LOAD_TIN_NHAN_DA_GUI_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau
                + "&sotrang=" + currentPage
                + "&sodongmoitrang=" + itemPerPage;
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
}
