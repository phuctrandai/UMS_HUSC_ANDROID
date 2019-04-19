package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;

public class VLyLichCaNhan {
    public VThongTinChung ThongTinChung;
    public VThongTinLienHe ThongTinLienHe;
    public VThuongTru ThuongTru;
    public VQueQuan QueQuan;
    public VDacDiemBanThan DacDiemBanThan;
    public VLichSuBanThan LichSuBanThan;

    public VThongTinChung getThongTinChung() {
        return ThongTinChung;
    }

    public VThongTinLienHe getThongTinLienHe() {
        return ThongTinLienHe;
    }

    public VThuongTru getThuongTru() {
        return ThuongTru;
    }

    public VQueQuan getQueQuan() {
        return QueQuan;
    }

    public VDacDiemBanThan getDacDiemBanThan() {
        return DacDiemBanThan;
    }

    public VLichSuBanThan getLichSuBanThan() {
        return LichSuBanThan;
    }

    public VLyLichCaNhan() {
    }

    public static VLyLichCaNhan fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VLyLichCaNhan.class);
        JsonAdapter<VLyLichCaNhan> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toJson() {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VLyLichCaNhan.class);
        JsonAdapter<VLyLichCaNhan> adapter = moshi.adapter(type);
        return adapter.toJson(this);
    }
}
