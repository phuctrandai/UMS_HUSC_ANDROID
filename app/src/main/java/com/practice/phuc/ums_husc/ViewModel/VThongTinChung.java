package com.practice.phuc.ums_husc.ViewModel;

public class VThongTinChung {
    private String HoTen;
    private boolean GioiTinh;
    private String NgaySinh;
    private String NoiSinh;
    private String TenQuocGia;
    private String TenDanToc;
    private String SoCMND;
    private String NoiCap;
    private String NgayCap;
    private String TenTonGiao;
    private String AnhDaiDien;
    private String MaSinhVien;

    public String getHoTen() {
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        HoTen = hoTen;
    }

    public boolean getGioiTinh() {
        return GioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        GioiTinh = gioiTinh;
    }

    public String getNgaySinh() {
        return NgaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        NgaySinh = ngaySinh;
    }

    public String getNoiSinh() {
        return NoiSinh;
    }

    public void setNoiSinh(String noiSinh) {
        NoiSinh = noiSinh;
    }

    public String getTenQuocGia() {
        return TenQuocGia;
    }

    public void setTenQuocGia(String tenQuocGia) {
        TenQuocGia = tenQuocGia;
    }

    public String getTenDanToc() {
        return TenDanToc;
    }

    public void setTenDanToc(String tenDanToc) {
        TenDanToc = tenDanToc;
    }

    public String getSoCMND() {
        return SoCMND;
    }

    public void setSoCMND(String soCMND) {
        SoCMND = soCMND;
    }

    public String getNoiCap() {
        return NoiCap;
    }

    public void setNoiCap(String noiCap) {
        NoiCap = noiCap;
    }

    public String getNgayCap() {
        return NgayCap;
    }

    public void setNgayCap(String ngayCap) {
        NgayCap = ngayCap;
    }

    public String getTenTonGiao() {
        return TenTonGiao;
    }

    public void setTenTonGiao(String tenTonGiao) {
        TenTonGiao = tenTonGiao;
    }

    public String getAnhDaiDien() {
        return AnhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        AnhDaiDien = anhDaiDien;
    }

    public String getMaSinhVien() {
        return MaSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        MaSinhVien = maSinhVien;
    }

    public VThongTinChung(String hoTen, boolean gioiTinh, String ngaySinh, String noiSinh,
                          String tenQuocGia, String tenDanToc, String soCMND,
                          String noiCap, String ngayCap,
                          String tenTonGiao, String anhDaiDien, String maSinhVien) {
        HoTen = hoTen;
        GioiTinh = gioiTinh;
        NgaySinh = ngaySinh;
        NoiSinh = noiSinh;
        TenQuocGia = tenQuocGia;
        TenDanToc = tenDanToc;
        SoCMND = soCMND;
        NoiCap = noiCap;
        NgayCap = ngayCap;
        TenTonGiao = tenTonGiao;
        AnhDaiDien = anhDaiDien;
        MaSinhVien = maSinhVien;
    }

    public VThongTinChung() {
    }
}
