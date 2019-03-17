package com.practice.phuc.ums_husc.ViewModel;

public class VThuongTru {
    private String MaSinhVien;
    private String TenQuocGia;
    private String TenThanhPho;
    private String TenQuanHuyen;
    private String TenPhuongXa;
    private String DiaChi;

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

    public String getTenQuanHuyen() {
        return TenQuanHuyen;
    }

    public void setTenQuanHuyen(String tenQuanHuyen) {
        TenQuanHuyen = tenQuanHuyen;
    }

    public String getTenPhuongXa() {
        return TenPhuongXa;
    }

    public void setTenPhuongXa(String tenPhuongXa) {
        TenPhuongXa = tenPhuongXa;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String diaChi) {
        DiaChi = diaChi;
    }

    public VThuongTru(String maSinhVien, String tenQuocGia, String tenThanhPho,
                      String tenQuanHuyen, String tenPhuongXa, String diaChi) {
        MaSinhVien = maSinhVien;
        TenQuocGia = tenQuocGia;
        TenThanhPho = tenThanhPho;
        TenQuanHuyen = tenQuanHuyen;
        TenPhuongXa = tenPhuongXa;
        DiaChi = diaChi;
    }

    public VThuongTru() {
    }
}
