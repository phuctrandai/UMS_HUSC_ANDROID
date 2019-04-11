package com.practice.phuc.ums_husc.ResumeModule;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.VLichSuBanThan;

import static com.practice.phuc.ums_husc.Helper.StringHelper.getValueOrEmpty;
import static com.practice.phuc.ums_husc.Helper.StringHelper.isNullOrEmpty;

public class LichSuBanThanFragment extends Fragment {
    public static LichSuBanThanFragment newInstance(VLichSuBanThan lichSuBanThan) {
        LichSuBanThanFragment f = new LichSuBanThanFragment();
        f.mLichSuBanThan = lichSuBanThan;
        return f;
    }

    private VLichSuBanThan mLichSuBanThan;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        dislayThongTin();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void dislayThongTin() {
        mNamTotNghiepTHPT.setText(getValueOrEmpty(mLichSuBanThan.getNamTotNghiepTHPT()));
        mXepLoaiTotNghiepTHPT.setText(getValueOrEmpty(mLichSuBanThan.getXepLoaiTotNghiepTHPT()));
        mNoiTotNghiepTHPT.setText(getValueOrEmpty(mLichSuBanThan.getNoiTotNghiepTHPT()));
        mXepLoaiHocTapLop10.setText(getValueOrEmpty(mLichSuBanThan.getXepLoaiHocTap10()));
        mXepLoaiHocTapLop11.setText(getValueOrEmpty(mLichSuBanThan.getXepLoaiHocTap11()));
        mXepLoaiHocTapLop12.setText(getValueOrEmpty(mLichSuBanThan.getXepLoaiHocTap12()));
        mXepLoaiHanhKiemLop10.setText(getValueOrEmpty(mLichSuBanThan.getXepLoaiHanhKiem10()));
        mXepLoaiHanhKiemLop11.setText(getValueOrEmpty(mLichSuBanThan.getXepLoaiHanhKiem11()));
        mXepLoaiHanhKiemLop12.setText(getValueOrEmpty(mLichSuBanThan.getXepLoaiHanhKiem12()));
        mTenLVT.setText(getValueOrEmpty(mLichSuBanThan.getTenLucLuongVuTrang()));

        String ngayBdLLVT = isNullOrEmpty(mLichSuBanThan.getNgayBatDauLLVT()) ? "..." :
                mLichSuBanThan.getNgayBatDauLLVT().substring(0, 10);
        mNgayBatDauLLVT.setText(ngayBdLLVT);

        String ngayKtLLVT = isNullOrEmpty(mLichSuBanThan.getNgayKetThucLLVT()) ? "..." :
                mLichSuBanThan.getNgayKetThucLLVT();
        mNgayKetThucLLVT.setText(ngayKtLLVT);
        mQuanHamLLVT.setText(getValueOrEmpty(mLichSuBanThan.getQuanHamLLVT()));
        mNoiCongTacLLVT.setText(getValueOrEmpty(mLichSuBanThan.getNoiCongTacLLVT()));
    }

    public void setThongTin(VLichSuBanThan lichSuBanThan) {
        this.mLichSuBanThan = lichSuBanThan;
    }
}
