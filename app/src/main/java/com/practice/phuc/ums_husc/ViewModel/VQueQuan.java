package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;

public class VQueQuan {
    public String MaSinhVien;
    public String TenQuocGia;
    public String TenThanhPho;
    public String TenQuanHuyen;
    public String TenPhuongXa;
    public String DiaChi;

    public String getMaSinhVien() {
        return MaSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        MaSinhVien = maSinhVien;
    }

    public String getTenQuocGia() {
        return TenQuocGia;
    }

    public String getTenThanhPho() {
        return TenThanhPho;
    }

    public String getTenQuanHuyen() {
        return TenQuanHuyen;
    }

    public String getTenPhuongXa() {
        return TenPhuongXa;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public VQueQuan() {
    }

    public static String toJson(VQueQuan model) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VQueQuan.class);
        JsonAdapter<VQueQuan> adapter = moshi.adapter(type);
        return adapter.toJson(model);
    }
}
