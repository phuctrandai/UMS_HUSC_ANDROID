package com.practice.phuc.ums_husc.Model;

public class NGUOINHAN {
    public int getMaTinNhan() {
        return MaTinNhan;
    }

    public void setMaTinNhan(int maTinNhan) {
        MaTinNhan = maTinNhan;
    }

    public String getMaNguoiNhan() {
        return MaNguoiNhan;
    }

    public void setMaNguoiNhan(String maNguoiNhan) {
        MaNguoiNhan = maNguoiNhan;
    }

    public String getHoTenNguoiNhan() {
        return HoTen;
    }

    public void setHoTenNguoiNhan(String hoTenNguoiNhan) {
        HoTen = hoTenNguoiNhan;
    }

    public String getThoiDiemXem() {
        return ThoiDiemXem;
    }

    public void setThoiDiemXem(String thoiDiemXem) {
        ThoiDiemXem = thoiDiemXem;
    }

    public boolean isDaXoa() {
        return DaXoa;
    }

    public void setDaXoa(boolean daXoa) {
        DaXoa = daXoa;
    }

    private int MaTinNhan;
    private String MaNguoiNhan;
    private String HoTen;
    private String ThoiDiemXem;
    private boolean DaXoa;

    public NGUOINHAN() {

    }

    public NGUOINHAN(int maTinNhan, String maNguoiNhan, String hoTenNguoiNhan, String thoiDiemXem, boolean daXoa) {
        MaTinNhan = maTinNhan;
        MaNguoiNhan = maNguoiNhan;
        HoTen = hoTenNguoiNhan;
        ThoiDiemXem = thoiDiemXem;
        DaXoa = daXoa;
    }
}
