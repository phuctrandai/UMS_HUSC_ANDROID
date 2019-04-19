package com.practice.phuc.ums_husc.ViewModel;

import android.support.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class PhuongXa {
    public int MaPhuongXa;
    public String TenPhuongXa;

    public PhuongXa(int i, String s) {
        MaPhuongXa = i;
        TenPhuongXa = s;
    }

    public static List<PhuongXa> fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, PhuongXa.class);
        JsonAdapter<List<PhuongXa>> adapter = moshi.adapter(type);
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
        return this.TenPhuongXa;
    }
}
