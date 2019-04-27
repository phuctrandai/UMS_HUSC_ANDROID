package com.practice.phuc.ums_husc.Model;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TINNHAN {
    public NGUOINHAN[] NguoiNhans;
    public String MaTinNhan;
    public String TieuDe;
    public String NoiDung;
    public String HoTenNguoiGui;
    public String MaNguoiGui;
    public String ThoiDiemGui;

    public TINNHAN() {
    }

    public TINNHAN(String tieuDe, String hoTenNguoiGui) {
        TieuDe = tieuDe;
        HoTenNguoiGui = hoTenNguoiGui;
    }



    /*##### Helper method #####*/

    public NGUOINHAN getNguoiNhanTrongDanhSach(String maNguoiNhan) {
        for (NGUOINHAN item : NguoiNhans) {
            if (item.MaNguoiNhan.equals(maNguoiNhan))
                return item;
        }
        return null;
    }

    public String[] getTenNguoiNhanArray() {
        int soNguoiNhan = this.NguoiNhans.length;

        String[] ds = new String[soNguoiNhan];
        for (int j = 0; j < soNguoiNhan; j++) {
            ds[j] = NguoiNhans[j].HoTenNguoiNhan;
        }
        return ds;
    }

    public String getTenNguoiNhanCollapse() {
        int soNguoiNhan = this.NguoiNhans.length;
        String temp = "";
        if (soNguoiNhan > 0)
            temp = NguoiNhans[(0)].HoTenNguoiNhan;
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
