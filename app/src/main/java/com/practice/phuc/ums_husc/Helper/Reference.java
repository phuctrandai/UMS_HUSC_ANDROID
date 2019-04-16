package com.practice.phuc.ums_husc.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.practice.phuc.ums_husc.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public final class Reference {

    public static final String HOST = "http://192.168.1.106:8082/";

    private static final String LOGIN_API = "api/SinhVien/TaiKhoan/DangNhap/";

    private static final String CHANGE_PASS_API = "api/SinhVien/TaiKhoan/DoiMatKhau";

    private static final String LOAD_LY_LICH_API = "api/SinhVien/TaiKhoan/LyLichCaNhan/";

    private static final String LOAD_THONG_BAO_API = "api/ThongBao/DanhSach/";

    private static final String LOAD_NOI_DUNG_THONG_BAO_API = "api/ThongBao/ChiTiet/";

    private static final String LOAD_TIN_NHAN_DEN_API = "api/SinhVien/TinNhan/DaNhan/";

    private static final String LOAD_TIN_NHAN_DA_GUI_API = "api/SinhVien/TinNhan/DaGui/";

    private static final String LOAD_TIN_NHAN_DA_XOA_API = "api/SinhVien/TinNhan/DaXoa/";

    private static final String LOAD_NOI_DUNG_TIN_NHAN_API = "api/SinhVien/TinNhan/NoiDung/";

    private static final String UPDATE_THOI_DIEM_XEM_TIN_NHAN_API = "api/SinhVien/CapNhatThoiDiemXem";

    private static final String ATTEMP_DELETE_TIN_NHAN_API = "api/SinhVien/TinNhan/XoaTamThoi";

    private static final String FOREVER_DELETE_TIN_NHAN_API = "api/SinhVien/TinNhan/XoaVinhVien";

    private static final String REPLY_TIN_NHAN_API = "api/SinhVien/TraLoiTinNhan/";

    private static final String SAVE_TOKEN_API = "api/FCM/Excute/Save/";

    private static final String DELETE_TOKEN_API = "api/FCM/Excute/Delete/";

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
                + "&sodong=" + itemPerPage;
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
                + "&sodong=" + itemPerPage;
    }

    public static String getLoadTinNhanDaGuiApiUrl(String maSinhVien, String matKhau, long currentPage, int itemPerPage) {
        return HOST + LOAD_TIN_NHAN_DA_GUI_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau
                + "&sotrang=" + currentPage
                + "&sodong=" + itemPerPage;
    }

    public static String getLoadTinNhanDaXoaApiUrl(String maSinhVien, String matKhau, long currentPage, int itemPerPage) {
        return HOST + LOAD_TIN_NHAN_DA_XOA_API +
                "?masinhvien=" + maSinhVien +
                "&matkhau=" + matKhau +
                "&sotrang=" + currentPage +
                "&sodong=" + itemPerPage;
    }

    public static String getLoadNoiDungTinNhanApiUrl(String maSinhVien, String matKhau, String id) {
        return Reference.HOST + Reference.LOAD_NOI_DUNG_TIN_NHAN_API
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau
                + "&id=" + id;
    }

    public static String getUpdateThoiDiemXemTinNhanApiUrl(String maSinhVien, String matKhau, String id) {
        return HOST + UPDATE_THOI_DIEM_XEM_TIN_NHAN_API +
                "?masinhvien=" + maSinhVien +
                "&matkhau=" + matKhau +
                "&id=" + id;
    }

    public static String getAttempDeleteTinNhanApiUrl(String maSinhVien, String matKhau, String id) {
        return HOST + ATTEMP_DELETE_TIN_NHAN_API +
                "?masinhvien=" + maSinhVien +
                "&matkhau=" + matKhau +
                "&id=" + id;
    }

    public static String getForeverDeleteTinNhanApiUrl(String maSinhVien, String matKhau, String id) {
        return HOST + FOREVER_DELETE_TIN_NHAN_API +
                "?masinhvien=" + maSinhVien +
                "&matkhau=" + matKhau +
                "&id=" + id;
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
    public static String BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI = "launch_from_noti";

    public static String BUNDLE_EXTRA_MESSAGE = "message";
    public static String BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI = "launch_from_noti";

    public static String getAccountId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        return sp.getString(context.getString(R.string.pre_key_student_id), "");
    }

    public static String getAccountName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        return sp.getString(context.getString(R.string.pre_key_student_name), "");
    }

    public static String getAccountPassword(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        return sp.getString(context.getString(R.string.pre_key_password), "");
    }
}
