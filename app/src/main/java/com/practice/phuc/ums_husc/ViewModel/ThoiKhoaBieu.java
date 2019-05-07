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
    public int TietHocBatDau;
    public int TietHocKetThuc;
    public String PhongHoc;
    public String HoVaTen;
    public String NgayHoc;
    public String MaSinhVien;
    public String HocKy;
    public int NgayTrongTuan;

    public static List<ThoiKhoaBieu> fromJsonToList(String json) {
        if (json == null) return null;

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

    public static String toJson(ThoiKhoaBieu obj) {
        if (obj == null) return null;

        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(ThoiKhoaBieu.class);
        JsonAdapter<ThoiKhoaBieu> adapter = moshi.adapter(type);
        return adapter.toJson(obj);
    }

    public static ThoiKhoaBieu fromJson(String json) {
        if (json == null) return null;

        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(ThoiKhoaBieu.class);
        JsonAdapter<ThoiKhoaBieu> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getLessionStartHour() {
        switch (this.TietHocBatDau) {
            case 1:
                return 7;
            case 2:
                return 8;
            case 3:
                return 9;
            case 4:
                return 10;
            case 5:
                return 13;
            case 6:
                return 14;
            case 7:
                return 15;
            case 8:
                return 16;
            case 9:
                return 17;
            case 10:
                return 18;
            case 11:
                return 19;
            case 12:
                return 20;
            default:
                return 0;
        }
    }

    public int getLessionStartMinute() {
        switch (this.TietHocBatDau) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return 0;
            case 9:
                return 30;
            case 10:
                return 25;
            case 11:
                return 20;
            case 12:
                return 15;
            default:
                return 0;
        }
    }

    public String getLessionStartTimeStr() {
        switch (this.TietHocBatDau) {
            case 1:
                return "7:00";
            case 2:
                return "8:00";
            case 3:
                return "9:00";
            case 4:
                return "10:00";
            case 5:
                return "13:00";
            case 6:
                return "14:00";
            case 7:
                return "15:00";
            case 8:
                return "16:00";
            case 9:
                return "17:30";
            case 10:
                return "18:25";
            case 11:
                return "19:20";
            case 12:
                return "20:15";
            default:
                return null;
        }
    }

    public String getLessionEndTimeStr() {
        switch (this.TietHocKetThuc) {
            case 1:
                return "7:50";
            case 2:
                return "8:50";
            case 3:
                return "9:50";
            case 4:
                return "10:50";
            case 5:
                return "13:50";
            case 6:
                return "14:50";
            case 7:
                return "15:50";
            case 8:
                return "16:50";
            case 9:
                return "18:20";
            case 10:
                return "19:15";
            case 11:
                return "20:10";
            case 12:
                return "21:00";
            default:
                return null;
        }
    }
}
