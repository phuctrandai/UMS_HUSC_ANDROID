package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.practice.phuc.ums_husc.ResumeModule.CharacteristicsFragment;
import com.practice.phuc.ums_husc.ResumeModule.LichSuBanThanFragment;
import com.practice.phuc.ums_husc.ResumeModule.ContactResidentFragment;
import com.practice.phuc.ums_husc.ResumeModule.GeneralInfoFragment;
import com.practice.phuc.ums_husc.ViewModel.VDacDiemBanThan;
import com.practice.phuc.ums_husc.ViewModel.VLichSuBanThan;
import com.practice.phuc.ums_husc.ViewModel.VQueQuan;
import com.practice.phuc.ums_husc.ViewModel.VThongTinChung;
import com.practice.phuc.ums_husc.ViewModel.VThongTinLienHe;
import com.practice.phuc.ums_husc.ViewModel.VThuongTru;

public class ResumePagerAdapter extends FragmentPagerAdapter {

    private VThongTinChung thongTinChung;
    private VThongTinLienHe thongTinLienHe;
    private VQueQuan queQuan;
    private VThuongTru thuongTru;
    private VDacDiemBanThan dacDiemBanThan;
    private VLichSuBanThan lichSuBanThan;

    private GeneralInfoFragment generalInfoFragment;
    private ContactResidentFragment contactResidentFragment;
    private CharacteristicsFragment characteristicsFragment;
    private LichSuBanThanFragment lichSuBanThanFragment;

    public ResumePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        generalInfoFragment = GeneralInfoFragment.newInstance(context, thongTinChung);
        contactResidentFragment = ContactResidentFragment.newInstance(context, thongTinLienHe, thuongTru, queQuan);
        characteristicsFragment = CharacteristicsFragment.newInstance(context, dacDiemBanThan);
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
        switch (position) {
            case 0:
                generalInfoFragment.setThongTin(thongTinChung);
                return generalInfoFragment;
            case 1:
                contactResidentFragment.setThongTin(thongTinLienHe, thuongTru, queQuan);
                return contactResidentFragment;
            case 2:
                characteristicsFragment.setThongTin(dacDiemBanThan);
                return characteristicsFragment;
            default:
                lichSuBanThanFragment.setThongTin(lichSuBanThan);
                return lichSuBanThanFragment;
        }
    }

    @Override
    public void notifyDataSetChanged() {
        generalInfoFragment.setThongTin(thongTinChung);
        contactResidentFragment.setThongTin(thongTinLienHe, thuongTru, queQuan);
        characteristicsFragment.setThongTin(dacDiemBanThan);
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
