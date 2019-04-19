package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;

public class VQueQuan {
    public String MaSinhVien;
    public String MaQuocGia;
    public String TenQuocGia;
    public String MaThanhPho;
    public String TenThanhPho;
    public String MaQuanHuyen;
    public String TenQuanHuyen;
    public String MaPhuongXa;
    public String TenPhuongXa;
    public String DiaChi;

    public VQueQuan() {
    }

    public static String toJson(VQueQuan model) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VQueQuan.class);
        JsonAdapter<VQueQuan> adapter = moshi.adapter(type);
        return adapter.toJson(model);
    }
}
