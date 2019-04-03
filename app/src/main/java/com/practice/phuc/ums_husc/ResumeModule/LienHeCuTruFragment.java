package com.practice.phuc.ums_husc.ResumeModule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.VQueQuan;
import com.practice.phuc.ums_husc.ViewModel.VThongTinLienHe;
import com.practice.phuc.ums_husc.ViewModel.VThuongTru;

public class LienHeCuTruFragment extends Fragment {

    private static VThongTinLienHe thongTinLienHe = null;
    private static VThuongTru thuongTru = null;
    private static VQueQuan queQuan = null;

    private TextView mDiDong;
    private TextView mCoDinh;
    private TextView mEmail;
    private TextView mHinhThucCuTru;
    private TextView mNgayCuTru;
    private TextView mDiaChi;
    private TextView mQueQuan;
    private TextView mHoKhau;

    private LinearLayout mLayouCoDinh;
    private LinearLayout mLayoutNgayCuTru;
    private LinearLayout mLayoutDiDong;
    private LinearLayout mLayoutEmail;
    private LinearLayout mLayoutHinhThucCuTru;
    private LinearLayout mLayoutDiaChiCuTru;
    private LinearLayout mLayoutHoKhau;
    private LinearLayout mLayoutQueQuan;

    public LienHeCuTruFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lien_he_cu_tru, container, false);

        mCoDinh = view.findViewById(R.id.tv_coDinh);
        mDiDong = view.findViewById(R.id.tv_diDong);
        mEmail = view.findViewById(R.id.tv_email);
        mHinhThucCuTru = view.findViewById(R.id.tv_hinhThucCuTru);
        mNgayCuTru = view.findViewById(R.id.tv_ngayBatDauCuTru);
        mDiaChi = view.findViewById(R.id.tv_diaChiCuTru);
        mQueQuan = view.findViewById(R.id.tv_queQuan);
        mHoKhau = view.findViewById(R.id.tv_hoKhauThuongTru);

        mLayouCoDinh = view.findViewById(R.id.layout_coDinh);
        mLayoutDiDong = view.findViewById(R.id.layout_diDong);
        mLayoutNgayCuTru = view.findViewById(R.id.layout_ngayBatDauCuTru);
        mLayoutEmail = view.findViewById(R.id.layout_email);
        mLayoutHinhThucCuTru = view.findViewById(R.id.layout_hinhThucCuTru);
        mLayoutDiaChiCuTru = view.findViewById(R.id.layout_diaChiCuTru);
        mLayoutHoKhau = view.findViewById(R.id.layout_hoKhau);
        mLayoutQueQuan = view.findViewById(R.id.layout_queQuan);

        displayThongTin();

        return view;
    }

    public void setThongTin(VThongTinLienHe thongTinLienHe,
                            VThuongTru thuongTru,
                            VQueQuan queQuan) {
        LienHeCuTruFragment.thongTinLienHe = thongTinLienHe;
        LienHeCuTruFragment.thuongTru = thuongTru;
        LienHeCuTruFragment.queQuan = queQuan;
    }

    public void displayThongTin() {
        if (thuongTru != null) {
            String temp = "";
            if (thuongTru.getDiaChi() != null) temp += thuongTru.getDiaChi();
            if (thuongTru.getTenPhuongXa() != null) temp += ", " + thuongTru.getTenPhuongXa();
            if (thuongTru.getTenQuanHuyen() != null) temp += ", " + thuongTru.getTenQuanHuyen();
            if (thuongTru.getTenThanhPho() != null) temp += ", " + thuongTru.getTenQuocGia();
            if (thuongTru.getTenQuocGia() != null) temp += ", " + thuongTru.getTenQuocGia();
            if (temp != "") {
                mHoKhau.setText(temp);
            } else {
                mLayoutHoKhau.setVisibility(View.GONE);
            }
        }
        if (thongTinLienHe != null) {
            mDiDong.setText(thongTinLienHe.getDiDong());
            mCoDinh.setText(thongTinLienHe.getDienThoai());
            mEmail.setText(thongTinLienHe.getEmail());
            mNgayCuTru.setText(thongTinLienHe.getNgayBatDauCuTru());
            if (thongTinLienHe.getHinhThucCuTru() == 3) {
                mDiaChi.setText(mHoKhau.getText());
                mHinhThucCuTru.setText("Theo hộ khẩu thường trú");
            } else {
                mLayoutHinhThucCuTru.setVisibility(View.GONE);
            }

            if (mDiaChi.getText() == "") mLayoutDiDong.setVisibility(View.GONE);
            if (mCoDinh.getText() == "") mLayouCoDinh.setVisibility(View.GONE);
            if (mEmail.getText() == "") mLayoutEmail.setVisibility(View.GONE);
            if (mNgayCuTru.getText() == "") mLayoutNgayCuTru.setVisibility(View.GONE);
        }
        if (queQuan != null) {
            String temp = "";
            if (queQuan.getDiaChi() != null) temp += queQuan.getDiaChi();
            if (queQuan.getTenPhuongXa() != null) temp += ", " + queQuan.getTenPhuongXa();
            if (queQuan.getTenQuanHuyen() != null) temp += ", " + queQuan.getTenQuanHuyen();
            if (queQuan.getTenThanhPho() != null) temp += ", " + queQuan.getTenQuocGia();
            if (queQuan.getTenQuocGia() != null) temp += ", " + queQuan.getTenQuocGia();
            if (temp != "") {
                mQueQuan.setText(temp);
            } else {
                mLayoutQueQuan.setVisibility(View.GONE);
            }
        }
    }
}
