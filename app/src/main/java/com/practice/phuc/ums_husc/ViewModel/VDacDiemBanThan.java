package com.practice.phuc.ums_husc.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;

public class VDacDiemBanThan {
    private String MaSinhVien;
    private String TinhTrangHonNhan;
    private String ChieuCao;
    private String NhomMau;
    private String ThanhPhanXuatThan;
    private String DienUuTienGiaDinh;
    private String DienUuTienBanThan;
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

    public String getChieuCao() {
        return ChieuCao;
    }

    public void setChieuCao(String chieuCao) {
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

    public String getDienUuTienBanThan() {
        return DienUuTienBanThan;
    }

    public void setDienUuTienBanThan(String dienUuTienBanThan) {
        DienUuTienBanThan = dienUuTienBanThan;
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

    public VDacDiemBanThan() {
    }

    public static String toJson(VDacDiemBanThan obj) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VDacDiemBanThan.class);
        JsonAdapter<VDacDiemBanThan> adapter = moshi.adapter(type);
        return adapter.toJson(obj);
    }

    public static VDacDiemBanThan fromJson(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(VDacDiemBanThan.class);
        JsonAdapter<VDacDiemBanThan> adapter = moshi.adapter(type);
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
