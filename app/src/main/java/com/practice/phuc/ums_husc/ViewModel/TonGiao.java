package com.practice.phuc.ums_husc.ViewModel;

import android.support.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TonGiao {
    public int MaTonGiao;
    public String TenTonGiao;

    @NonNull
    @Override
    public String toString() {
        return this.TenTonGiao;
    }

    public static List<TonGiao> fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, TonGiao.class);
        JsonAdapter<List<TonGiao>> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
