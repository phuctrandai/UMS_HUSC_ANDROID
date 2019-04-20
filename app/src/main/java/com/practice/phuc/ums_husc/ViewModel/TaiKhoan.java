package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TaiKhoan {
    public String MaTaiKhoan;
    public String HoTen;
    public String MaSinhVien;

    public TaiKhoan(String maTaiKhoan, String hoTen) {
        MaTaiKhoan = maTaiKhoan;
        HoTen = hoTen;
    }

    public TaiKhoan(String maTaiKhoan, String hoTen, String maSinhVien) {
        MaTaiKhoan = maTaiKhoan;
        HoTen = hoTen;
        MaSinhVien = maSinhVien;
    }

    public static List<TaiKhoan> fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, TaiKhoan.class);
        JsonAdapter<List<TaiKhoan>> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
