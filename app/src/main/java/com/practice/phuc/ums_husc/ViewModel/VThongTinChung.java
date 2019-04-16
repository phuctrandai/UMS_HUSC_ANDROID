package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;

public class VThongTinChung {
    public String HoTen;
    public boolean GioiTinh;
    public String NgaySinh;
    public VNoiSinh NoiSinh;
    public String TenQuocGia;
    public String TenDanToc;
    public String SoCMND;
    public String NoiCap;
    public String NgayCap;
    public String TenTonGiao;
    public String AnhDaiDien;
    public String MaSinhVien;
    public String QuocTich;
    public String DanToc;
    public String TonGiao;

    public VThongTinChung() {
    }

    public static VThongTinChung fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VThongTinChung.class);
        JsonAdapter<VThongTinChung> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toJson(VThongTinChung model) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VThongTinChung.class);
        JsonAdapter<VThongTinChung> adapter = moshi.adapter(type);
        return adapter.toJson(model);
    }
}
