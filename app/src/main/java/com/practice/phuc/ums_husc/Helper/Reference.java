package com.practice.phuc.ums_husc.Helper;

public final class Reference {

    public static final String HOST = "http://192.168.1.106:8082/";

    public static final String LOGIN_API = "api/sinhvien/dangnhap/";

    public static final String LOAD_LY_LICH_API = "api/SinhVien/LyLichCaNhan/";

    public static final String LOAD_THONG_BAO_API = "api/ThongBao/TatCa/";

    public static final String LOAD_TIN_NHAN_DEN_API = "api/SinhVien/TinNhanDen/";

    public static final String SAVE_TOKEN_API = "api/fcm/save/";

    public static final String DELETE_TOKEN_API = "api/fcm/delete/";

    public static String getLoginApiUrl(String maSinhVien, String matKhau) {
        return Reference.HOST + Reference.LOGIN_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau;
    }

    public static String getLoadThongBaoApiUrl(String maSinhVien, String matKhau, int currentPage, int itemPerPage) {
        return Reference.HOST + Reference.LOAD_THONG_BAO_API
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

    public static String getSaveTokenApiUrl(String maSinhVien, String token) {
        return Reference.HOST + Reference.SAVE_TOKEN_API
                + "?masinhvien=" + maSinhVien
                + "&token=" + token;
    }

    public static String getDeleteTokenApiUrl(String maSinhVien, String token) {
        return Reference.HOST + Reference.DELETE_TOKEN_API
                + "?masinhvien=" + maSinhVien
                + "&token=" + token;
    }

    public static String MESSAGE_NOTIFICATION = "message_notification";
    public static String NEWS_NOTIFICATION = "news_notification";
}
