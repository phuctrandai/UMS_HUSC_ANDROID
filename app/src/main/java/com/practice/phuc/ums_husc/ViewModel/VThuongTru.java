package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;

public class VThuongTru {
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

    public VThuongTru() {
    }

    public static String toJson(VThuongTru model) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VThuongTru.class);
        JsonAdapter<VThuongTru> adapter = moshi.adapter(type);
        return adapter.toJson(model);
    }
}
