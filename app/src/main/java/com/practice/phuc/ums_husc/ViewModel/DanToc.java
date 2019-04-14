package com.practice.phuc.ums_husc.ViewModel;

import android.support.annotation.NonNull;

import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class DanToc {
    public int MaDanToc;
    public String TenDanToc;

    @NonNull
    @Override
    public String toString() {
        return this.TenDanToc;
    }

    public static List<DanToc> fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, DanToc.class);
        JsonAdapter<List<DanToc>> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
