package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;

public class VThuongTru {
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

    public VThuongTru() {
    }

    public static String toJson(VThuongTru model) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VThuongTru.class);
        JsonAdapter<VThuongTru> adapter = moshi.adapter(type);
        return adapter.toJson(model);
    }
}
