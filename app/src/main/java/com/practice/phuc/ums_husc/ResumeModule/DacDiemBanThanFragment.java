package com.practice.phuc.ums_husc.ResumeModule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.VDacDiemBanThan;

public class DacDiemBanThanFragment extends Fragment {
    private static VDacDiemBanThan dacDiemBanThan;

    private TextView mXuatThan;
    private TextView mUuTienBanThan;
    private TextView mUuTienGiaDinh;
    private TextView mTinhTrangHonNhan;
    private TextView mChieuCao;
    private TextView mNhomMau;
    private TextView mNgayVaoDoan;
    private TextView mNoiKetNapDoan;
    private TextView mNgayVaoDang;
    private TextView mNoiKetNapDang;
    private TextView mNgayChinhThucVaoDang;

    private RelativeLayout mLayoutXuatThan;
    private RelativeLayout mLayoutUuTienGiaDinh;
    private RelativeLayout mLayoutUuTienBanThan;
    private RelativeLayout mLayoutTinhTrangHonNhan;
    private RelativeLayout mLayoutChieuCao;
    private RelativeLayout mLayoutNhomMau;
    private RelativeLayout mLayoutNgayVaoDoan;
    private RelativeLayout mLayoutNoiKetNapDoan;
    private RelativeLayout mLayoutNgayVaoDang;
    private RelativeLayout mLayoutNoiKetNapDang;
    private RelativeLayout mLayoutNgayChinhThuc;


    public DacDiemBanThanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dac_diem_ban_than, container, false);

        mXuatThan = view.findViewById(R.id.tv_thanhPhanXuatThan);
        mUuTienBanThan = view.findViewById(R.id.tv_dienUuTienBanThan);
        mUuTienGiaDinh = view.findViewById(R.id.tv_dienUuTienGiaDinh);
        mTinhTrangHonNhan = view.findViewById(R.id.tv_tinhTrangHonNhan);
        mChieuCao = view.findViewById(R.id.tv_chieuCao);
        mNhomMau = view.findViewById(R.id.tv_nhomMau);
        mNgayVaoDoan = view.findViewById(R.id.tv_ngayVaoDoan);
        mNoiKetNapDoan = view.findViewById(R.id.tv_noiVaoDoan);
        mNgayVaoDang = view.findViewById(R.id.tv_ngayVaoDang);
        mNoiKetNapDang = view.findViewById(R.id.tv_noiKetNapDang);
        mNgayChinhThucVaoDang = view.findViewById(R.id.tv_ngayChinhThuc);

        mLayoutXuatThan = view.findViewById(R.id.layout_thanhPhanXuatThan);
        mLayoutUuTienGiaDinh = view.findViewById(R.id.layout_dienUuTienGiaDinh);
        mLayoutUuTienBanThan = view.findViewById(R.id.layout_dienUuTienBanThan);
        mLayoutTinhTrangHonNhan = view.findViewById(R.id.layout_tinhTrangHonNhan);
        mLayoutChieuCao = view.findViewById(R.id.layout_chieuCao);
        mLayoutNhomMau = view.findViewById(R.id.layout_nhomMau);
        mLayoutNgayVaoDoan = view.findViewById(R.id.layout_ngayVaoDoan);
        mLayoutNoiKetNapDoan = view.findViewById(R.id.layout_noiVaoDoan);
        mLayoutNgayVaoDang = view.findViewById(R.id.layout_ngayVaoDang);
        mLayoutNoiKetNapDang = view.findViewById(R.id.layout_noiKetNapDang);
        mLayoutNgayChinhThuc = view.findViewById(R.id.layout_ngayChinhThuc);

        displayThongTin();

        return view;
    }

    public void setThongTin(VDacDiemBanThan dacDiemBanThan) {
        DacDiemBanThanFragment.dacDiemBanThan = dacDiemBanThan;
    }

    public void displayThongTin() {
        if (dacDiemBanThan != null) {
            String ngayVaoDoan = dacDiemBanThan.getNgayVaoDoan() == null ? "" :
                    dacDiemBanThan.getNgayVaoDoan().substring(0, 10);
            String chieuCao = dacDiemBanThan.getChieuCao() + " cm";
            String ngayVaoDang = dacDiemBanThan.getNgayVaoDang() == null ? "" :
                    dacDiemBanThan.getNgayVaoDang().substring(0, 10);
            String ngayChinhThuc = dacDiemBanThan.getNgayChinhThucVaoDang() == null ? "" :
                    dacDiemBanThan.getNgayChinhThucVaoDang().substring(0, 10);

            mXuatThan.setText(dacDiemBanThan.getThanhPhanXuatThan());
            mUuTienBanThan.setText(dacDiemBanThan.getTenDienUuTienBanThan());
            mUuTienGiaDinh.setText(dacDiemBanThan.getDienUuTienGiaDinh());
            mTinhTrangHonNhan.setText(dacDiemBanThan.getTinhTrangHonNhan());
            mChieuCao.setText(chieuCao);
            mNhomMau.setText(dacDiemBanThan.getNhomMau());
            mNgayVaoDoan.setText(ngayVaoDoan);
            mNoiKetNapDoan.setText(dacDiemBanThan.getNoiKetNapDoan());
            mNgayVaoDang.setText(ngayVaoDang);
            mNoiKetNapDang.setText(dacDiemBanThan.getNoiKetNapDang());
            mNgayChinhThucVaoDang.setText(ngayChinhThuc);

            if (mXuatThan.getText() == "") mLayoutXuatThan.setVisibility(View.GONE);
            if (mUuTienBanThan.getText() == "") mLayoutUuTienBanThan.setVisibility(View.GONE);
            if (mUuTienGiaDinh.getText() == "") mLayoutUuTienGiaDinh.setVisibility(View.GONE);
            if (mTinhTrangHonNhan.getText() == "") mLayoutTinhTrangHonNhan.setVisibility(View.GONE);
            if (mChieuCao.getText() == "") mLayoutChieuCao.setVisibility(View.GONE);
            if (mNhomMau.getText() == "") mLayoutNhomMau.setVisibility(View.GONE);
            if (mNgayVaoDoan.getText() == "") mLayoutNgayVaoDoan.setVisibility(View.GONE);
            if (mNoiKetNapDoan.getText() == "") mLayoutNoiKetNapDoan.setVisibility(View.GONE);
            if (mNgayVaoDang.getText() == "") mLayoutNgayVaoDang.setVisibility(View.GONE);
            if (mNoiKetNapDang.getText() == "") mLayoutNoiKetNapDang.setVisibility(View.GONE);
            if (mNgayChinhThucVaoDang.getText() == "") mLayoutNgayChinhThuc.setVisibility(View.GONE);
        }
    }
}
