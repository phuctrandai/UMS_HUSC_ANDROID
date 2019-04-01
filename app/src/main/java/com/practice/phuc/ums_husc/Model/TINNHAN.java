package com.practice.phuc.ums_husc.Model;

public class TINNHAN {
    private int maTinNhan;
    private String tieuDe;
    private String nguoiGui;
    private String thoiDiemGui;
    private String noiDung;
    private String nguoiNhan;
    private String thoiDiemXem;

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

    public String getNguoiNhan() {
        return nguoiNhan;
    }

    public void setNguoiNhan(String nguoiNhan) {
        this.nguoiNhan = nguoiNhan;
    }

    public String getThoiDiemXem() {
        return thoiDiemXem;
    }

    public void setThoiDiemXem(String thoiDiemXem) {
        this.thoiDiemXem = thoiDiemXem;
    }

    public TINNHAN(int maTinNhan, String tieuDe, String nguoiGui, String thoiDiemGui, String noiDung, String nguoiNhan, String thoiDiemXem) {
        this.maTinNhan = maTinNhan;
        this.tieuDe = tieuDe;
        this.nguoiGui = nguoiGui;
        this.thoiDiemGui = thoiDiemGui;
        this.noiDung = noiDung;
        this.nguoiNhan = nguoiNhan;
        this.thoiDiemXem = thoiDiemXem;
    }

    public TINNHAN() {
    }

    public int getMaTinNhan() {
        return maTinNhan;
    }

    public void setMaTinNhan(int maTinNhan) {
        this.maTinNhan = maTinNhan;
    }
}
