package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;

public class VLichSuBanThan {
    private String MaSinhVien;
    private String NamTotNghiepTHPT;
    private String XepLoaiTotNghiepTHPT;
    private String NoiTotNghiepTHPT;
    private String XepLoaiHocTap10;
    private String XepLoaiHocTap11;
    private String XepLoaiHocTap12;
    private String XepLoaiHanhKiem10;
    private String XepLoaiHanhKiem11;
    private String XepLoaiHanhKiem12;
    private String TenLucLuongVuTrang;
    private String NgayBatDauLLVT;
    private String NgayKetThucLLVT;
    private String QuanHamLLVT;
    private String NoiCongTacLLVT;

    public VLichSuBanThan() {
    }

    public String getMaSinhVien() {
        return MaSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        MaSinhVien = maSinhVien;
    }

    public String getNamTotNghiepTHPT() {
        return NamTotNghiepTHPT;
    }

    public void setNamTotNghiepTHPT(String namTotNghiepTHPT) {
        NamTotNghiepTHPT = namTotNghiepTHPT;
    }

    public String getXepLoaiTotNghiepTHPT() {
        return XepLoaiTotNghiepTHPT;
    }

    public void setXepLoaiTotNghiepTHPT(String xepLoaiTotNghiepTHPT) {
        XepLoaiTotNghiepTHPT = xepLoaiTotNghiepTHPT;
    }

    public String getNoiTotNghiepTHPT() {
        return NoiTotNghiepTHPT;
    }

    public void setNoiTotNghiepTHPT(String noiTotNghiepTHPT) {
        NoiTotNghiepTHPT = noiTotNghiepTHPT;
    }

    public String getXepLoaiHocTap10() {
        return XepLoaiHocTap10;
    }

    public void setXepLoaiHocTap10(String xepLoaiHocTap10) {
        XepLoaiHocTap10 = xepLoaiHocTap10;
    }

    public String getXepLoaiHocTap11() {
        return XepLoaiHocTap11;
    }

    public void setXepLoaiHocTap11(String xepLoaiHocTap11) {
        XepLoaiHocTap11 = xepLoaiHocTap11;
    }

    public String getXepLoaiHocTap12() {
        return XepLoaiHocTap12;
    }

    public void setXepLoaiHocTap12(String xepLoaiHocTap12) {
        XepLoaiHocTap12 = xepLoaiHocTap12;
    }

    public String getXepLoaiHanhKiem10() {
        return XepLoaiHanhKiem10;
    }

    public void setXepLoaiHanhKiem10(String xepLoaiHanhKiem10) {
        XepLoaiHanhKiem10 = xepLoaiHanhKiem10;
    }

    public String getXepLoaiHanhKiem11() {
        return XepLoaiHanhKiem11;
    }

    public void setXepLoaiHanhKiem11(String xepLoaiHanhKiem11) {
        XepLoaiHanhKiem11 = xepLoaiHanhKiem11;
    }

    public String getXepLoaiHanhKiem12() {
        return XepLoaiHanhKiem12;
    }

    public void setXepLoaiHanhKiem12(String xepLoaiHanhKiem12) {
        XepLoaiHanhKiem12 = xepLoaiHanhKiem12;
    }

    public String getTenLucLuongVuTrang() {
        return TenLucLuongVuTrang;
    }

    public void setTenLucLuongVuTrang(String tenLucLuongVuTrang) {
        TenLucLuongVuTrang = tenLucLuongVuTrang;
    }

    public String getNgayBatDauLLVT() {
        return NgayBatDauLLVT;
    }

    public void setNgayBatDauLLVT(String ngayBatDauLLVT) {
        NgayBatDauLLVT = ngayBatDauLLVT;
    }

    public String getNgayKetThucLLVT() {
        return NgayKetThucLLVT;
    }

    public void setNgayKetThucLLVT(String ngayKetThucLLVT) {
        NgayKetThucLLVT = ngayKetThucLLVT;
    }

    public String getQuanHamLLVT() {
        return QuanHamLLVT;
    }

    public void setQuanHamLLVT(String quanHamLLVT) {
        QuanHamLLVT = quanHamLLVT;
    }

    public String getNoiCongTacLLVT() {
        return NoiCongTacLLVT;
    }

    public void setNoiCongTacLLVT(String noiCongTacLLVT) {
        NoiCongTacLLVT = noiCongTacLLVT;
    }

    public static VLichSuBanThan fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VLichSuBanThan.class);
        JsonAdapter<VLichSuBanThan> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toJson(VLichSuBanThan obj) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VLichSuBanThan.class);
        JsonAdapter<VLichSuBanThan> adapter = moshi.adapter(type);
        return adapter.toJson(obj);
    }
}
