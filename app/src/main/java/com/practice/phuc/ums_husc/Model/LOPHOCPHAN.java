package com.practice.phuc.ums_husc.Model;

public class LOPHOCPHAN {
    private String MaLopHocPhan;
    private String TenLopHocPhan;
    private String PhongHoc;
    private int TietBatDau;
    private int TietKetThuc;
    private String GiaoVien;
    private String NgayBatDau;
    private String NgayKetThuc;
    private int NgayTrongTuan;

    public LOPHOCPHAN(String maLopHocPhan, String tenLopHocPhan, String phongHoc,
                      int tietBatDau, int tietKetThuc, String giaoVien,
                      String ngayBatDau, String ngayKetThuc, int ngayTrongTuan) {
        MaLopHocPhan = maLopHocPhan;
        TenLopHocPhan = tenLopHocPhan;
        PhongHoc = phongHoc;
        TietBatDau = tietBatDau;
        TietKetThuc = tietKetThuc;
        GiaoVien = giaoVien;
        NgayBatDau = ngayBatDau;
        NgayKetThuc = ngayKetThuc;
        NgayTrongTuan = ngayTrongTuan;
    }

    public LOPHOCPHAN() {
    }

    public String getMaLopHocPhan() {
        return MaLopHocPhan;
    }

    public void setMaLopHocPhan(String maLopHocPhan) {
        MaLopHocPhan = maLopHocPhan;
    }

    public String getTenLopHocPhan() {
        return TenLopHocPhan;
    }

    public void setTenLopHocPhan(String tenLopHocPhan) {
        TenLopHocPhan = tenLopHocPhan;
    }

    public String getPhongHoc() {
        return PhongHoc;
    }

    public void setPhongHoc(String phongHoc) {
        PhongHoc = phongHoc;
    }

    public int getTietBatDau() {
        return TietBatDau;
    }

    public void setTietBatDau(int tietBatDau) {
        TietBatDau = tietBatDau;
    }

    public int getTietKetThuc() {
        return TietKetThuc;
    }

    public void setTietKetThuc(int tietKetThuc) {
        TietKetThuc = tietKetThuc;
    }

    public String getGiaoVien() {
        return GiaoVien;
    }

    public void setGiaoVien(String giaoVien) {
        GiaoVien = giaoVien;
    }

    public String getNgayKetThuc() {
        return NgayKetThuc;
    }

    public void setNgayKetThuc(String ngayKetThuc) {
        NgayKetThuc = ngayKetThuc;
    }

    public String getNgayBatDau() {
        return NgayBatDau;
    }

    public void setNgayBatDau(String ngayBatDau) {
        NgayBatDau = ngayBatDau;
    }

    public int getNgayTrongTuan() {
        return NgayTrongTuan;
    }

    public void setNgayTrongTuan(int ngayTrongTuan) {
        NgayTrongTuan = ngayTrongTuan;
    }
}
