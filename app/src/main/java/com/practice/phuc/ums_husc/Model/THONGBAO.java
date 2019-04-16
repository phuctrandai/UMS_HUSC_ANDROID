package com.practice.phuc.ums_husc.Model;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class THONGBAO {
    private int MaThongBao;
    private String TieuDe;
    private String NoiDung;
    private String ThoiGianDang;

    public int getMaThongBao() {
        return MaThongBao;
    }

    public void setMaThongBao(int maThongBao) {
        MaThongBao = maThongBao;
    }

    public String getTieuDe() {
        return TieuDe;
    }

    public void setTieuDe(String tieuDe) {
        TieuDe = tieuDe;
    }

    public String getNoiDung() {
        return NoiDung;
    }

    public void setNoiDung(String noiDung) {
        NoiDung = noiDung;
    }

    public String getThoiGianDang() {
        return ThoiGianDang;
    }

    public void setThoiGianDang(String thoiGianDang) {
        ThoiGianDang = thoiGianDang;
    }

    public THONGBAO() {
    }

    public static String toJson(THONGBAO model) {
        Moshi mMoshi = new Moshi.Builder().build();
        Type mUsersType = Types.newParameterizedType(THONGBAO.class);
        JsonAdapter<THONGBAO> mJsonAdapter = mMoshi.adapter(mUsersType);
        return mJsonAdapter.toJson(model);
    }

    public static THONGBAO fromJson(String json) {
        Moshi mMoshi = new Moshi.Builder().build();
        Type mUsersType = Types.newParameterizedType(THONGBAO.class);
        JsonAdapter<THONGBAO> mJsonAdapter = mMoshi.adapter(mUsersType);
        try {
            return mJsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<THONGBAO> fromJsonToList(String json) {
        Moshi mMoshi = new Moshi.Builder().build();
        Type mUsersType = Types.newParameterizedType(List.class, THONGBAO.class);
        JsonAdapter<List<THONGBAO>> mJsonAdapter = mMoshi.adapter(mUsersType);

        try {
            return mJsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
