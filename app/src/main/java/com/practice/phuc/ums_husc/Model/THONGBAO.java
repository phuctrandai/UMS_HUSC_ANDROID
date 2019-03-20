package com.practice.phuc.ums_husc.Model;

import java.util.List;

public class THONGBAO {
    private int MaThongBao;
    private String TieuDe;
    private String NoiDung;
    private String ThoiGianDang;
    private List<LIENKET> LIENKETs;

    public int getMaThongBao() {
        return MaThongBao;
    }

    public void setMaThongBao(int maThongBao) {
        MaThongBao = maThongBao;
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

    public String getThoiGianDang() {
        return ThoiGianDang;
    }

    public void setThoiGianDang(String thoiGianDang) {
        ThoiGianDang = thoiGianDang;
    }

    public List<LIENKET> getLIENKETs() {
        return LIENKETs;
    }

    public void setLIENKETs(List<LIENKET> LIENKETs) {
        this.LIENKETs = LIENKETs;
    }

    public THONGBAO(int maThongBao, String tieuDe, String noiDung, String thoiGianDang, List<LIENKET> LIENKETs) {
        MaThongBao = maThongBao;
        TieuDe = tieuDe;
        NoiDung = noiDung;
        ThoiGianDang = thoiGianDang;
        this.LIENKETs = LIENKETs;
    }

    public THONGBAO() {
    }
}
