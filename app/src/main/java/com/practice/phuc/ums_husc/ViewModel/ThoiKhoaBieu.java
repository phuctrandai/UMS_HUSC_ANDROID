package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ThoiKhoaBieu {
    public String MaLopHocPhan;
    public String TenLopHocPhan;
    public String TenPhong;
    public String TietHocBatDau;
    public String TietHocKetThuc;
    public String PhongHoc;
    public String HoVaTen;
    public String NgayHoc;
    public String MaSinhVien;
    public String HocKy;
    public String NgayTrongTuan;

    public static List<ThoiKhoaBieu> fromJsonToList(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, ThoiKhoaBieu.class);
        JsonAdapter<List<ThoiKhoaBieu>> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
