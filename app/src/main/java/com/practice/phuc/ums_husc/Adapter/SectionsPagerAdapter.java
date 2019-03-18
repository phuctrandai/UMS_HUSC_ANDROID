package com.practice.phuc.ums_husc.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.practice.phuc.ums_husc.DacDiemBanThanFragment;
import com.practice.phuc.ums_husc.LienHeCuTruFragment;
import com.practice.phuc.ums_husc.Model.LICHSUBANTHAN;
import com.practice.phuc.ums_husc.ThongTinChungFragment;
import com.practice.phuc.ums_husc.ViewModel.VDacDiemBanThan;
import com.practice.phuc.ums_husc.ViewModel.VQueQuan;
import com.practice.phuc.ums_husc.ViewModel.VThongTinChung;
import com.practice.phuc.ums_husc.ViewModel.VThongTinLienHe;
import com.practice.phuc.ums_husc.ViewModel.VThuongTru;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private VThongTinChung thongTinChung;
    private VThongTinLienHe thongTinLienHe;
    private VQueQuan queQuan;
    private VThuongTru thuongTru;
    private VDacDiemBanThan dacDiemBanThan;
    private LICHSUBANTHAN lichSuBanThan;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setThongTin(VThongTinChung thongTinChung,
                                VThongTinLienHe thongTinLienHe,
                                VQueQuan queQuan, VThuongTru thuongTru,
                                VDacDiemBanThan dacDiemBanThan, LICHSUBANTHAN lichSuBanThan) {
        this.thongTinChung = thongTinChung;
        this.thongTinLienHe = thongTinLienHe;
        this.queQuan = queQuan;
        this.thuongTru = thuongTru;
        this.dacDiemBanThan = dacDiemBanThan;
        this.lichSuBanThan = lichSuBanThan;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ThongTinChungFragment c0 = new ThongTinChungFragment();
                c0.setThongTin(this.thongTinChung);
                c0.displayThongTin();
                return c0;
            case 1:
                LienHeCuTruFragment c1 = new LienHeCuTruFragment();

                return c1;
            case 2:
                DacDiemBanThanFragment c2 = new DacDiemBanThanFragment();
                return c2;
            default:
                ThongTinChungFragment d = new ThongTinChungFragment();
                d.setThongTin(this.thongTinChung);
                d.displayThongTin();
                return d;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Thông tin chung";
            case 1:
                return "Liên hệ - cư trú";
            case 2:
                return "Lịch sử bản thân";
        }
        return null;
    }
}
