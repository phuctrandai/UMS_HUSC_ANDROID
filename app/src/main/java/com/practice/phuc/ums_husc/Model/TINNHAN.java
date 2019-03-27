package com.practice.phuc.ums_husc.Model;

public class TINNHAN {
    private String tieuDe;
    private String nguoiGui;
    private String thoiDiemGui;
    private String noiDung;

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNguoiGui() {
        return nguoiGui;
    }

    public void setNguoiGui(String nguoiGui) {
        this.nguoiGui = nguoiGui;
    }

    public String getThoiDiemGui() {
        return thoiDiemGui;
    }

    public void setThoiDiemGui(String thoiDiemGui) {
        this.thoiDiemGui = thoiDiemGui;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public TINNHAN(String tieuDe, String nguoiGui, String thoiDiemGui, String noiDung) {
        this.tieuDe = tieuDe;
        this.nguoiGui = nguoiGui;
        this.thoiDiemGui = thoiDiemGui;
        this.noiDung = noiDung;
    }

    public TINNHAN() {
    }
}
