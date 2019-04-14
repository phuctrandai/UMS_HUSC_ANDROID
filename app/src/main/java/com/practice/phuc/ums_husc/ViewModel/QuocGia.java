package com.practice.phuc.ums_husc.ViewModel;

import android.support.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class QuocGia {
    public int MaQuocGia;
    public String TenQuocGia;

    @NonNull
    @Override
    public String toString() {
        return this.TenQuocGia;
    }

    public static List<QuocGia> fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, QuocGia.class);
        JsonAdapter<List<QuocGia>> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
