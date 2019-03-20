package com.practice.phuc.ums_husc.Model;

public class LIENKET {
    private int MaLienKet;
    private int MaThongBao;
    private String DuongDan;

    public int getMaLienKet() {
        return MaLienKet;
    }

    public void setMaLienKet(int maLienKet) {
        MaLienKet = maLienKet;
    }

    public int getMaThongBao() {
        return MaThongBao;
    }

    public void setMaThongBao(int maThongBao) {
        MaThongBao = maThongBao;
    }

    public String getDuongDan() {
        return DuongDan;
    }

    public void setDuongDan(String duongDan) {
        DuongDan = duongDan;
    }

    public LIENKET(int maLienKet, int maThongBao, String duongDan) {
        MaLienKet = maLienKet;
        MaThongBao = maThongBao;
        DuongDan = duongDan;
    }

    public LIENKET() {
    }
}
