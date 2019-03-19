package com.practice.phuc.ums_husc.ViewModel;

public class VThongTinCaNhan {
    public String MaSinhVien;
    public String TenNganh;
    public String KhoaHoc;
    public String HoTen;
    public String AnhDaiDien;

    public String getMaSinhVien() {
        return MaSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        MaSinhVien = maSinhVien;
    }

    public String getTenNganh() {
        return TenNganh;
    }

    public void setTenNganh(String tenNganh) {
        TenNganh = tenNganh;
    }

    public String getKhoaHoc() {
        return KhoaHoc;
    }

    public void setKhoaHoc(String khoaHoc) {
        KhoaHoc = khoaHoc;
    }

    public String getHoTen() {
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        HoTen = hoTen;
    }

    public String getAnhDaiDien() {
        return AnhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        AnhDaiDien = anhDaiDien;
    }

    public VThongTinCaNhan(String maSinhVien, String tenNganh, String khoaHoc, String hoTen, String anhDaiDien) {
        MaSinhVien = maSinhVien;
        TenNganh = tenNganh;
        KhoaHoc = khoaHoc;
        HoTen = hoTen;
        AnhDaiDien = anhDaiDien;
    }

    public VThongTinCaNhan() {
    }
}
