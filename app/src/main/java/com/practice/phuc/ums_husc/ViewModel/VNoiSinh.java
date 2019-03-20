package com.practice.phuc.ums_husc.ViewModel;

public class VNoiSinh {
    private String MaSinhVien;
    private String TenQuocGia;
    private String TenThanhPho;

    public String getMaSinhVien() {
        return MaSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        MaSinhVien = maSinhVien;
    }

    public String getTenQuocGia() {
        return TenQuocGia;
    }

    public void setTenQuocGia(String tenQuocGia) {
        TenQuocGia = tenQuocGia;
    }

    public String getTenThanhPho() {
        return TenThanhPho;
    }

    public void setTenThanhPho(String tenThanhPho) {
        TenThanhPho = tenThanhPho;
    }

    public VNoiSinh(String maSinhVien, String tenQuocGia, String tenThanhPho) {
        MaSinhVien = maSinhVien;
        TenQuocGia = tenQuocGia;
        TenThanhPho = tenThanhPho;
    }

    public VNoiSinh() {
    }
}
