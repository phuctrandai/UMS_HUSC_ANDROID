package com.practice.phuc.ums_husc.ViewModel;

public class VLyLichCaNhan {
    private VThongTinChung ThongTinChung;
    private VThongTinLienHe ThongTinLienHe;
    private VThuongTru ThuongTru;
    private VQueQuan QueQuan;
    private VDacDiemBanThan DacDiemBanThan;
    private VLichSuBanThan LichSuBanThan;

    public VThongTinChung getThongTinChung() {
        return ThongTinChung;
    }

    public void setThongTinChung(VThongTinChung thongTinChung) {
        ThongTinChung = thongTinChung;
    }

    public VThongTinLienHe getThongTinLienHe() {
        return ThongTinLienHe;
    }

    public void setThongTinLienHe(VThongTinLienHe thongTinLienHe) {
        ThongTinLienHe = thongTinLienHe;
    }

    public VThuongTru getThuongTru() {
        return ThuongTru;
    }

    public void setThuongTru(VThuongTru thuongTru) {
        ThuongTru = thuongTru;
    }

    public VQueQuan getQueQuan() {
        return QueQuan;
    }

    public void setQueQuan(VQueQuan queQuan) {
        QueQuan = queQuan;
    }

    public VDacDiemBanThan getDacDiemBanThan() {
        return DacDiemBanThan;
    }

    public void setDacDiemBanThan(VDacDiemBanThan dacDiemBanThan) {
        DacDiemBanThan = dacDiemBanThan;
    }

    public VLichSuBanThan getLichSuBanThan() {
        return LichSuBanThan;
    }

    public void setLichSuBanThan(VLichSuBanThan lichSuBanThan) {
        LichSuBanThan = lichSuBanThan;
    }

    public VLyLichCaNhan(VThongTinChung thongTinChung, VThongTinLienHe thongTinLienHe,
                         VThuongTru thuongTru, VQueQuan queQuan, VDacDiemBanThan dacDiemBanThan,
                         VLichSuBanThan lichSuBanThan) {
        ThongTinChung = thongTinChung;
        ThongTinLienHe = thongTinLienHe;
        ThuongTru = thuongTru;
        QueQuan = queQuan;
        DacDiemBanThan = dacDiemBanThan;
        LichSuBanThan = lichSuBanThan;
    }

    public VLyLichCaNhan() {
    }
}
