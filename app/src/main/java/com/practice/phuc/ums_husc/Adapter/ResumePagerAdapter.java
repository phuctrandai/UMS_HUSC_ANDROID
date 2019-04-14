package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.practice.phuc.ums_husc.ResumeModule.DacDiemBanThanFragment;
import com.practice.phuc.ums_husc.ResumeModule.LichSuBanThanFragment;
import com.practice.phuc.ums_husc.ResumeModule.LienHeCuTruFragment;
import com.practice.phuc.ums_husc.ResumeModule.ThongTinChungFragment;
import com.practice.phuc.ums_husc.ViewModel.VDacDiemBanThan;
import com.practice.phuc.ums_husc.ViewModel.VLichSuBanThan;
import com.practice.phuc.ums_husc.ViewModel.VQueQuan;
import com.practice.phuc.ums_husc.ViewModel.VThongTinChung;
import com.practice.phuc.ums_husc.ViewModel.VThongTinLienHe;
import com.practice.phuc.ums_husc.ViewModel.VThuongTru;

public class ResumePagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    private VThongTinChung thongTinChung;
    private VThongTinLienHe thongTinLienHe;
    private VQueQuan queQuan;
    private VThuongTru thuongTru;
    private VDacDiemBanThan dacDiemBanThan;
    private VLichSuBanThan lichSuBanThan;

    private ThongTinChungFragment thongTinChungFragment;
    private LienHeCuTruFragment lienHeCuTruFragment;
    private DacDiemBanThanFragment dacDiemBanThanFragment;
    private LichSuBanThanFragment lichSuBanThanFragment;

    public ResumePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        thongTinChungFragment = ThongTinChungFragment.newInstance(mContext, thongTinChung);
        lienHeCuTruFragment = LienHeCuTruFragment.newInstance(thongTinLienHe, thuongTru, queQuan);
        dacDiemBanThanFragment = DacDiemBanThanFragment.newInstance(dacDiemBanThan);
        lichSuBanThanFragment = LichSuBanThanFragment.newInstance(lichSuBanThan);
    }

    public void setThongTin(VThongTinChung thongTinChung,
                                VThongTinLienHe thongTinLienHe,
                                VQueQuan queQuan, VThuongTru thuongTru,
                                VDacDiemBanThan dacDiemBanThan, VLichSuBanThan lichSuBanThan) {
        this.thongTinChung = thongTinChung;
        this.thongTinLienHe = thongTinLienHe;
        this.queQuan = queQuan;
        this.thuongTru = thuongTru;
        this.dacDiemBanThan = dacDiemBanThan;
        this.lichSuBanThan = lichSuBanThan;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("DEBUG", "Get item " + position);
        switch (position) {
            case 0:
                thongTinChungFragment.setThongTin(thongTinChung);
                return thongTinChungFragment;
            case 1:
                lienHeCuTruFragment.setThongTin(thongTinLienHe, thuongTru, queQuan);
                return lienHeCuTruFragment;
            case 2:
                dacDiemBanThanFragment.setThongTin(dacDiemBanThan);
                return dacDiemBanThanFragment;
            default:
                lichSuBanThanFragment.setThongTin(lichSuBanThan);
                return lichSuBanThanFragment;
        }
    }

    @Override
    public void notifyDataSetChanged() {
        thongTinChungFragment.setThongTin(thongTinChung);
        lienHeCuTruFragment.setThongTin(thongTinLienHe, thuongTru, queQuan);
        dacDiemBanThanFragment.setThongTin(dacDiemBanThan);
        lichSuBanThanFragment.setThongTin(lichSuBanThan);
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chung";
            case 1:
                return "Liên hệ";
            case 2:
                return "Đặc điểm";
            case 3:
                return "Lịch sử";
        }
        return null;
    }
}
