package com.practice.phuc.ums_husc.Model;

import android.support.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class KYTUCXA {
    public int MaKyTucXa;
    public String TenKyTucXa;

    public KYTUCXA() {

    }

    public KYTUCXA(int maKyTucXa, String tenKyTucXa) {
        MaKyTucXa = maKyTucXa;
        TenKyTucXa = tenKyTucXa;
    }


    @NonNull
    @Override
    public String toString() {
        return TenKyTucXa;
    }

    public static List<KYTUCXA> fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, KYTUCXA.class);
        JsonAdapter<List<KYTUCXA>> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
