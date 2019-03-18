package com.practice.phuc.ums_husc;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.phuc.ums_husc.ViewModel.VQueQuan;
import com.practice.phuc.ums_husc.ViewModel.VThongTinLienHe;
import com.practice.phuc.ums_husc.ViewModel.VThuongTru;

public class LienHeCuTruFragment extends Fragment {

    private static VThongTinLienHe thongTinLienHe = null;
    private static VThuongTru thuongTru = null;
    private static VQueQuan queQuan = null;

    public LienHeCuTruFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        displayThongTin();

        return inflater.inflate(R.layout.fragment_lien_he_cu_tru, container, false);
    }

    public void setThongTin(VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        LienHeCuTruFragment.thongTinLienHe = thongTinLienHe;
        LienHeCuTruFragment.thuongTru = thuongTru;
        LienHeCuTruFragment.queQuan = queQuan;
    }

    public void displayThongTin() {

    }
}
