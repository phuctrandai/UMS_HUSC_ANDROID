package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;

public class VThongTinLienHe {
    public String MaSinhVient;
    public String DienThoai;
    public String DiDong;
    public String Email;
    public String HinhThucCuTru;
    public String TenKyTucXa;
    public String TenQuocGia;
    public String TenThanhPho;
    public String TenQuanHuyen;
    public String TenPhuongXa;
    public String DiaChi;
    public String NgayBatDauCuTru;

    public String getDienThoai() {
        return DienThoai;
    }

    public String getDiDong() {
        return DiDong;
    }

    public String getEmail() {
        return Email;
    }

    public String getHinhThucCuTru() {
        return HinhThucCuTru;
    }

    public String getNgayBatDauCuTru() {
        return NgayBatDauCuTru;
    }

    public VThongTinLienHe() {
    }

    public static String toJson(VThongTinLienHe model) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VThongTinLienHe.class);
        JsonAdapter<VThongTinLienHe> adapter = moshi.adapter(type);
        return adapter.toJson(model);
    }
}
