package com.practice.phuc.ums_husc.Model;

public class TINNHAN {
    private NGUOINHAN[] NGUOINHANs;
    private int MaTinNhan;
    private String TieuDe;
    private String NoiDung;
    private String HoTenNguoiGui;
    private String MaNguoiGui;
    private String ThoiDiemGui;

    public int getMaTinNhan() {
        return MaTinNhan;
    }

    public void setMaTinNhan(int maTinNhan) {
        MaTinNhan = maTinNhan;
    }

    public String getTieuDe() {
        return TieuDe;
    }

    public void setTieuDe(String tieuDe) {
        TieuDe = tieuDe;
    }

    public String getNoiDung() {
        return NoiDung;
    }

    public void setNoiDung(String noiDung) {
        NoiDung = noiDung;
    }

    public String getHoTenNguoiGui() {
        return HoTenNguoiGui;
    }

    public void setHoTenNguoiGui(String hoTenNguoiGui) {
        HoTenNguoiGui = hoTenNguoiGui;
    }

    public String getMaNguoiGui() {
        return MaNguoiGui;
    }

    public void setMaNguoiGui(String maNguoiGui) {
        MaNguoiGui = maNguoiGui;
    }

    public String getThoiDiemGui() {
        return ThoiDiemGui;
    }

    public void setThoiDiemGui(String thoiDiemGui) {
        ThoiDiemGui = thoiDiemGui;
    }

    public NGUOINHAN[] getNGUOINHANs() {
        return NGUOINHANs;
    }

    public void setNGUOINHANs(NGUOINHAN[] NGUOINHANs) {
        this.NGUOINHANs = NGUOINHANs;
    }

    public TINNHAN(int maTinNhan, String tieuDe, String noiDung, String hoTenNguoiGui, String maNguoiGui, String thoiDiemGui, NGUOINHAN[] NGUOINHANs) {
        MaTinNhan = maTinNhan;
        TieuDe = tieuDe;
        NoiDung = noiDung;
        HoTenNguoiGui = hoTenNguoiGui;
        MaNguoiGui = maNguoiGui;
        ThoiDiemGui = thoiDiemGui;
        this.NGUOINHANs = NGUOINHANs;
    }

    public TINNHAN() {
    }
}
