package com.practice.phuc.ums_husc.ViewModel;

public class VThongTinLienHe {
    private String MaSinhVient;
    private String DienThoai;
    private String DiDong;
    private String Email;
    private String HinhThucCuTru;
    private String TenKyTucXa;
    private String TenQuocGia;
    private String TenThanhPho;
    private String TenQuanHuyen;
    private String TenPhuongXa;
    private String DiaChi;
    private String NgayBatDauCuTru;

    public String getMaSinhVient() {
        return MaSinhVient;
    }

    public void setMaSinhVient(String maSinhVient) {
        MaSinhVient = maSinhVient;
    }

    public String getDienThoai() {
        return DienThoai;
    }

    public void setDienThoai(String dienThoai) {
        DienThoai = dienThoai;
    }

    public String getDiDong() {
        return DiDong;
    }

    public void setDiDong(String diDong) {
        DiDong = diDong;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getHinhThucCuTru() {
        return HinhThucCuTru;
    }

    public void setHinhThucCuTru(String hinhThucCuTru) {
        HinhThucCuTru = hinhThucCuTru;
    }

    public String getTenKyTucXa() {
        return TenKyTucXa;
    }

    public void setTenKyTucXa(String tenKyTucXa) {
        TenKyTucXa = tenKyTucXa;
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

    public String getNgayBatDauCuTru() {
        return NgayBatDauCuTru;
    }

    public void setNgayBatDauCuTru(String ngayBatDauCuTru) {
        NgayBatDauCuTru = ngayBatDauCuTru;
    }

    public VThongTinLienHe(String maSinhVient, String dienThoai, String diDong, String email,
                           String hinhThucCuTru, String tenKyTucXa, String tenQuocGia,
                           String tenThanhPho, String tenQuanHuyen, String tenPhuongXa,
                           String diaChi, String ngayBatDauCuTru) {
        MaSinhVient = maSinhVient;
        DienThoai = dienThoai;
        DiDong = diDong;
        Email = email;
        HinhThucCuTru = hinhThucCuTru;
        TenKyTucXa = tenKyTucXa;
        TenQuocGia = tenQuocGia;
        TenThanhPho = tenThanhPho;
        TenQuanHuyen = tenQuanHuyen;
        TenPhuongXa = tenPhuongXa;
        DiaChi = diaChi;
        NgayBatDauCuTru = ngayBatDauCuTru;
    }

    public VThongTinLienHe() {
    }
}
