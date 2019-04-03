package com.practice.phuc.ums_husc.ResumeModule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Model.LICHSUBANTHAN;
import com.practice.phuc.ums_husc.R;

public class LichSuBanThanFragment extends Fragment {
    private static LICHSUBANTHAN mLichSuBanThan;

    private TextView mNamTotNghiepTHPT;
    private TextView mXepLoaiTotNghiepTHPT;
    private TextView mNoiTotNghiepTHPT;
    private TextView mXepLoaiHocTapLop10;
    private TextView mXepLoaiHocTapLop11;
    private TextView mXepLoaiHocTapLop12;
    private TextView mXepLoaiHanhKiemLop10;
    private TextView mXepLoaiHanhKiemLop11;
    private TextView mXepLoaiHanhKiemLop12;
    private TextView mTenLVT;
    private TextView mNgayBatDauLLVT;
    private TextView mNgayKetThucLLVT;
    private TextView mQuanHamLLVT;
    private TextView mNoiCongTacLLVT;

    public LichSuBanThanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lich_su_ban_than, container, false);

        mNamTotNghiepTHPT = view.findViewById(R.id.tv_namTotNghiepTHPT);
        mXepLoaiTotNghiepTHPT = view.findViewById(R.id.tv_loaiTotNghiepTHPT);
        mNoiTotNghiepTHPT = view.findViewById(R.id.tv_noiTotNghiepTHPT);
        mXepLoaiHocTapLop10 = view.findViewById(R.id.tv_xepLoaiHocTapLop10);
        mXepLoaiHocTapLop11 = view.findViewById(R.id.tv_xepLoaiHocTapLop11);
        mXepLoaiHocTapLop12 = view.findViewById(R.id.tv_xepLoaiHocTapLop12);
        mXepLoaiHanhKiemLop10 = view.findViewById(R.id.tv_xepLoaiHanhKiemLop10);
        mXepLoaiHanhKiemLop11 = view.findViewById(R.id.tv_xepLoaiHanhKiemLop11);
        mXepLoaiHanhKiemLop12 = view.findViewById(R.id.tv_xepLoaiHanhKiemLop12);
        mTenLVT = view.findViewById(R.id.tv_lucLuongVuTrang);
        mNgayBatDauLLVT = view.findViewById(R.id.tv_ngayNhapNgu);
        mNgayKetThucLLVT = view.findViewById(R.id.tv_ngayXuatNgu);
        mQuanHamLLVT = view.findViewById(R.id.tv_quanHam);
        mNoiCongTacLLVT = view.findViewById(R.id.tv_noiCongTac);

        return view;
    }

    public void setThongTin(LICHSUBANTHAN lichsubanthan) {
        mLichSuBanThan = lichsubanthan;
    }

    private void dislayThongTin() {
        mNamTotNghiepTHPT.setText(mLichSuBanThan.getNamTotNghiepTHPT() + "");
        mXepLoaiTotNghiepTHPT.setText(mLichSuBanThan.getXepLoaiTotNghiepTHPT());
        mNoiTotNghiepTHPT.setText(mLichSuBanThan.getNoiTotNghiepTHPT());
        mXepLoaiHocTapLop10.setText(mLichSuBanThan.getXepLoaiHocTap10());
        mXepLoaiHocTapLop11.setText(mLichSuBanThan.getXepLoaiHocTap11());
        mXepLoaiHocTapLop12.setText(mLichSuBanThan.getXepLoaiHocTap12());
        mXepLoaiHanhKiemLop10.setText(mLichSuBanThan.getXepLoaiHanhKiem10());
        mXepLoaiHanhKiemLop11.setText(mLichSuBanThan.getXepLoaiHanhKiem11());
        mXepLoaiHanhKiemLop12.setText(mLichSuBanThan.getXepLoaiHanhKiem12());
        mTenLVT.setText(mLichSuBanThan.getTenLucLuongVuTrang());
        mNgayBatDauLLVT.setText(mLichSuBanThan.getNgayBatDauLLVT());
        mNgayKetThucLLVT.setText(mLichSuBanThan.getNgayKetThucLLVT());
        mQuanHamLLVT.setText(mLichSuBanThan.getQuanHamLLVT());
        mNoiCongTacLLVT.setText(mLichSuBanThan.getNoiCongTacLLVT());
    }
}
