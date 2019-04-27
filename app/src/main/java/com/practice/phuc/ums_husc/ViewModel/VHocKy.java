package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class VHocKy {
    public String MaHocKy;
    public String TenHocKy;
    public String MaNamHoc;
    public String NamBatDau;
    public String NamKetThuc;

    public List<VHocKy> fromJsonToList(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, VHocKy.class);
        JsonAdapter<List<VHocKy>> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
