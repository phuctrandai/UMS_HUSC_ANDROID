package com.practice.phuc.ums_husc.Model;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TINNHAN {
    private NGUOINHAN[] NguoiNhans;
    private int MaTinNhan;
    private String TieuDe;
    private String NoiDung;
    private String HoTenNguoiGui;
    private String MaNguoiGui;
    private String ThoiDiemGui;

    public int getMaTinNhan() {
        return MaTinNhan;
    }

    public void setMaTinNhan(int maTinNhan) {
        MaTinNhan = maTinNhan;
    }

    public String getTieuDe() {
        return TieuDe;
    }

    public void setTieuDe(String tieuDe) {
        TieuDe = tieuDe;
    }

    public String getNoiDung() {
        return NoiDung;
    }

    public void setNoiDung(String noiDung) {
        NoiDung = noiDung;
    }

    public String getHoTenNguoiGui() {
        return HoTenNguoiGui;
    }

    public void setHoTenNguoiGui(String hoTenNguoiGui) {
        HoTenNguoiGui = hoTenNguoiGui;
    }

    public String getMaNguoiGui() {
        return MaNguoiGui;
    }

    public void setMaNguoiGui(String maNguoiGui) {
        MaNguoiGui = maNguoiGui;
    }

    public String getThoiDiemGui() {
        return ThoiDiemGui;
    }

    public void setThoiDiemGui(String thoiDiemGui) {
        ThoiDiemGui = thoiDiemGui;
    }

    public NGUOINHAN[] getNGUOINHANs() {
        return NguoiNhans;
    }

    public void setNGUOINHANs(NGUOINHAN[] NGUOINHANs) {
        this.NguoiNhans = NGUOINHANs;
    }

    public TINNHAN() {
    }

    public String[] getTenNguoiNhanArray() {
        int soNguoiNhan = this.NguoiNhans.length;

        String[] ds = new String[soNguoiNhan];
        for (int j = 0; j < soNguoiNhan; j++) {
            ds[j] = NguoiNhans[j].getHoTenNguoiNhan();
        }
        return ds;
    }

    public String getTenNguoiNhanCollapse() {
        int soNguoiNhan = this.NguoiNhans.length;
        String temp = "";
        if (soNguoiNhan > 0)
            temp = NguoiNhans[(0)].getHoTenNguoiNhan();
        if (soNguoiNhan > 1)
            temp += " và " + (soNguoiNhan - 1) + " người khác";
        return temp;
    }

    public static String toJson(TINNHAN model) {
        Moshi mMoshi = new Moshi.Builder().build();
        Type mUsersType = Types.newParameterizedType(TINNHAN.class);
        JsonAdapter<TINNHAN> mJsonAdapter = mMoshi.adapter(mUsersType);
        return mJsonAdapter.toJson(model);
    }

    public static TINNHAN fromJson(String json) {
        Moshi mMoshi = new Moshi.Builder().build();
        Type mUsersType = Types.newParameterizedType(TINNHAN.class);
        JsonAdapter<TINNHAN> mJsonAdapter = mMoshi.adapter(mUsersType);
        try {
            return mJsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<TINNHAN> fromJsonToList(String json) {
        Moshi mMoshi = new Moshi.Builder().build();
        Type mUsersType = Types.newParameterizedType(List.class, TINNHAN.class);
        JsonAdapter<List<TINNHAN>> mJsonAdapter = mMoshi.adapter(mUsersType);

        try {
            return mJsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
