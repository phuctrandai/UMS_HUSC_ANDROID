package com.practice.phuc.ums_husc.ViewModel;

public class VDacDiemBanThan {
    private String MaSinhVien;
    private String TinhTrangHonNhan;
    private int ChieuCao;
    private String NhomMau;
    private String ThanhPhanXuatThan;
    private String DienUuTienGiaDinh;
    private String TenDienUuTienBanThan;
    private String NgayVaoDoan;
    private String NoiKetNapDoan;
    private String NgayVaoDang;
    private String NoiKetNapDang;
    private String NgayChinhThucVaoDang;

    public String getMaSinhVien() {
        return MaSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        MaSinhVien = maSinhVien;
    }

    public String getTinhTrangHonNhan() {
        return TinhTrangHonNhan;
    }

    public void setTinhTrangHonNhan(String tinhTrangHonNhan) {
        TinhTrangHonNhan = tinhTrangHonNhan;
    }

    public int getChieuCao() {
        return ChieuCao;
    }

    public void setChieuCao(int chieuCao) {
        ChieuCao = chieuCao;
    }

    public String getNhomMau() {
        return NhomMau;
    }

    public void setNhomMau(String nhomMau) {
        NhomMau = nhomMau;
    }

    public String getThanhPhanXuatThan() {
        return ThanhPhanXuatThan;
    }

    public void setThanhPhanXuatThan(String thanhPhanXuatThan) {
        ThanhPhanXuatThan = thanhPhanXuatThan;
    }

    public String getDienUuTienGiaDinh() {
        return DienUuTienGiaDinh;
    }

    public void setDienUuTienGiaDinh(String dienUuTienGiaDinh) {
        DienUuTienGiaDinh = dienUuTienGiaDinh;
    }

    public String getTenDienUuTienBanThan() {
        return TenDienUuTienBanThan;
    }

    public void setTenDienUuTienBanThan(String tenDienUuTienBanThan) {
        TenDienUuTienBanThan = tenDienUuTienBanThan;
    }

    public String getNgayVaoDoan() {
        return NgayVaoDoan;
    }

    public void setNgayVaoDoan(String ngayVaoDoan) {
        NgayVaoDoan = ngayVaoDoan;
    }

    public String getNoiKetNapDoan() {
        return NoiKetNapDoan;
    }

    public void setNoiKetNapDoan(String noiKetNapDoan) {
        NoiKetNapDoan = noiKetNapDoan;
    }

    public String getNgayVaoDang() {
        return NgayVaoDang;
    }

    public void setNgayVaoDang(String ngayVaoDang) {
        NgayVaoDang = ngayVaoDang;
    }

    public String getNoiKetNapDang() {
        return NoiKetNapDang;
    }

    public void setNoiKetNapDang(String noiKetNapDang) {
        NoiKetNapDang = noiKetNapDang;
    }

    public String getNgayChinhThucVaoDang() {
        return NgayChinhThucVaoDang;
    }

    public void setNgayChinhThucVaoDang(String ngayChinhThucVaoDang) {
        NgayChinhThucVaoDang = ngayChinhThucVaoDang;
    }

    public VDacDiemBanThan(String maSinhVien, String tinhTrangHonNhan, int chieuCao, String nhomMau,
                           String thanhPhanXuatThan, String dienUuTienGiaDinh,
                           String tenDienUuTienBanThan, String ngayVaoDoan, String noiKetNapDoan,
                           String ngayVaoDang, String noiKetNapDang, String ngayChinhThucVaoDang) {
        MaSinhVien = maSinhVien;
        TinhTrangHonNhan = tinhTrangHonNhan;
        ChieuCao = chieuCao;
        NhomMau = nhomMau;
        ThanhPhanXuatThan = thanhPhanXuatThan;
        DienUuTienGiaDinh = dienUuTienGiaDinh;
        TenDienUuTienBanThan = tenDienUuTienBanThan;
        NgayVaoDoan = ngayVaoDoan;
        NoiKetNapDoan = noiKetNapDoan;
        NgayVaoDang = ngayVaoDang;
        NoiKetNapDang = noiKetNapDang;
        NgayChinhThucVaoDang = ngayChinhThucVaoDang;
    }

    public VDacDiemBanThan() {
    }
}
