package com.practice.phuc.ums_husc.ViewModel;

import android.support.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ThanhPho {
    public int MaThanhPho;
    public String TenThanhPho;

    @NonNull
    @Override
    public String toString() {
        return this.TenThanhPho;
    }

    public static List<ThanhPho> fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, ThanhPho.class);
        JsonAdapter<List<ThanhPho>> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
