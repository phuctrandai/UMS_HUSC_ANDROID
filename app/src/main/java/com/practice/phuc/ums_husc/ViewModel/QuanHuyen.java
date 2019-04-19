package com.practice.phuc.ums_husc.ViewModel;

import android.support.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class QuanHuyen {
    public int MaQuanHuyen;
    public String TenQuanHuyen;

    public QuanHuyen(int i, String s) {
        MaQuanHuyen = i;
        TenQuanHuyen = s;
    }

    public static List<QuanHuyen> fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, QuanHuyen.class);
        JsonAdapter<List<QuanHuyen>> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return this.TenQuanHuyen;
    }
}
