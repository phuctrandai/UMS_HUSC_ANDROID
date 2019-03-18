package com.practice.phuc.ums_husc.LyLichCaNhan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
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

    private TableRow mLayoutXuatThan;
    private TableRow mLayoutUuTienGiaDinh;
    private TableRow mLayoutUuTienBanThan;
    private TableRow mLayoutTinhTrangHonNhan;
    private LinearLayout mLayoutChieuCao;
    private LinearLayout mLayoutNhomMau;

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

        mLayoutXuatThan = view.findViewById(R.id.layout_thanhPhanXuatThan);
        mLayoutUuTienGiaDinh = view.findViewById(R.id.layout_dienUuTienGiaDinh);
        mLayoutUuTienBanThan = view.findViewById(R.id.layout_dienUuTienBanThan);
        mLayoutTinhTrangHonNhan = view.findViewById(R.id.layout_tinhTrangHonNhan);
        mLayoutChieuCao = view.findViewById(R.id.layout_chieuCao);
        mLayoutNhomMau = view.findViewById(R.id.layout_nhomMau);

        displayThongTin();

        return view;
    }

    public void setThongTin(VDacDiemBanThan dacDiemBanThan) {
        DacDiemBanThanFragment.dacDiemBanThan = dacDiemBanThan;
    }

    public void displayThongTin() {
        if (dacDiemBanThan != null) {
            mXuatThan.setText(dacDiemBanThan.getThanhPhanXuatThan());
            mUuTienBanThan.setText(dacDiemBanThan.getTenDienUuTienBanThan());
            mUuTienGiaDinh.setText(dacDiemBanThan.getDienUuTienGiaDinh());
            mTinhTrangHonNhan.setText(dacDiemBanThan.getTinhTrangHonNhan());
            mChieuCao.setText(dacDiemBanThan.getChieuCao() + " cm");
            mNhomMau.setText(dacDiemBanThan.getNhomMau());

            if (mXuatThan.getText() == "") mLayoutXuatThan.setVisibility(View.GONE);
            if (mUuTienBanThan.getText() == "") mLayoutUuTienBanThan.setVisibility(View.GONE);
            if (mUuTienGiaDinh.getText() == "") mLayoutUuTienGiaDinh.setVisibility(View.GONE);
            if (mTinhTrangHonNhan.getText() == "") mLayoutTinhTrangHonNhan.setVisibility(View.GONE);
            if (mChieuCao.getText() == "") mLayoutChieuCao.setVisibility(View.GONE);
            if (mNhomMau.getText() == "") mLayoutNhomMau.setVisibility(View.GONE);
        }
    }
}
