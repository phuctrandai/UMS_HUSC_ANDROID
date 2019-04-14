package com.practice.phuc.ums_husc.Model;

public class NGUOINHAN {
    public String getMaNguoiNhan() {
        return MaNguoiNhan;
    }

    public void setMaNguoiNhan(String maNguoiNhan) {
        MaNguoiNhan = maNguoiNhan;
    }

    public String getHoTenNguoiNhan() {
        return HoTenNguoiNhan;
    }

    public void setHoTenNguoiNhan(String hoTenNguoiNhan) {
        HoTenNguoiNhan = hoTenNguoiNhan;
    }

    public String getThoiDiemXem() {
        return ThoiDiemXem;
    }

    public void setThoiDiemXem(String thoiDiemXem) {
        ThoiDiemXem = thoiDiemXem;
    }

    private String MaNguoiNhan;
    private String HoTenNguoiNhan;
    private String ThoiDiemXem;

    public NGUOINHAN() {
    }
}
