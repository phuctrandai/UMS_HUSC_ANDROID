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
import com.practice.phuc.ums_husc.ViewModel.VQueQuan;
import com.practice.phuc.ums_husc.ViewModel.VThongTinLienHe;
import com.practice.phuc.ums_husc.ViewModel.VThuongTru;

import static com.practice.phuc.ums_husc.Helper.StringHelper.getValueOrEmpty;
import static com.practice.phuc.ums_husc.Helper.StringHelper.isNullOrEmpty;

public class LienHeCuTruFragment extends Fragment {

    public static LienHeCuTruFragment newInstance(VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        LienHeCuTruFragment f = new LienHeCuTruFragment();
        f.thongTinLienHe = thongTinLienHe;
        f.thuongTru = thuongTru;
        f.queQuan = queQuan;
        return f;
    }

    private VThongTinLienHe thongTinLienHe;
    private VThuongTru thuongTru;
    private VQueQuan queQuan;

    private TextView mDiDong;
    private TextView mCoDinh;
    private TextView mEmail;
    private TextView mHinhThucCuTru;
    private TextView mNgayCuTru;
    private TextView mDiaChi;
    private TextView mQueQuan;
    private TextView mHoKhau;

//    private LinearLayout mLayouCoDinh;
//    private LinearLayout mLayoutNgayCuTru;
//    private LinearLayout mLayoutDiDong;
//    private LinearLayout mLayoutEmail;
//    private LinearLayout mLayoutHinhThucCuTru;
//    private LinearLayout mLayoutDiaChiCuTru;
//    private LinearLayout mLayoutHoKhau;
//    private LinearLayout mLayoutQueQuan;

    public LienHeCuTruFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

//        mLayouCoDinh = view.findViewById(R.id.layout_coDinh);
//        mLayoutDiDong = view.findViewById(R.id.layout_diDong);
//        mLayoutNgayCuTru = view.findViewById(R.id.layout_ngayBatDauCuTru);
//        mLayoutEmail = view.findViewById(R.id.layout_email);
//        mLayoutHinhThucCuTru = view.findViewById(R.id.layout_hinhThucCuTru);
//        mLayoutDiaChiCuTru = view.findViewById(R.id.layout_diaChiCuTru);
//        mLayoutHoKhau = view.findViewById(R.id.layout_hoKhau);
//        mLayoutQueQuan = view.findViewById(R.id.layout_queQuan);

        displayThongTin();

        return view;
    }

    @SuppressLint("SetTextI18n")
    public void displayThongTin() {
        if (thuongTru != null) {
            String temp = "";
            if (!isNullOrEmpty(thuongTru.getDiaChi())) temp += thuongTru.getDiaChi();
            if (!isNullOrEmpty(thuongTru.getTenPhuongXa())) temp += ", " + thuongTru.getTenPhuongXa();
            if (!isNullOrEmpty(thuongTru.getTenQuanHuyen())) temp += ", " + thuongTru.getTenQuanHuyen();
            if (!isNullOrEmpty(thuongTru.getTenThanhPho())) temp += ", " + thuongTru.getTenQuocGia();
            if (!isNullOrEmpty(thuongTru.getTenQuocGia())) temp += ", " + thuongTru.getTenQuocGia();
            String diaChiHoKhau = getValueOrEmpty(temp);
            mHoKhau.setText(diaChiHoKhau);
        }
        if (thongTinLienHe != null) {
            mDiDong.setText(getValueOrEmpty(thongTinLienHe.getDiDong()));
            mCoDinh.setText(getValueOrEmpty(thongTinLienHe.getDienThoai()));
            mEmail.setText(getValueOrEmpty(thongTinLienHe.getEmail()));
            String ngayCuTru = isNullOrEmpty(thongTinLienHe.getNgayBatDauCuTru()) ? "..." :
                    thongTinLienHe.getNgayBatDauCuTru().substring(0, 10);
            mNgayCuTru.setText(ngayCuTru);

            if (thongTinLienHe.getHinhThucCuTru() != null) {
                if (thongTinLienHe.getHinhThucCuTru().equals("3")) {
                    mDiaChi.setText(mHoKhau.getText());
                    mHinhThucCuTru.setText("Theo hộ khẩu thường trú");
                } else {
                    mDiaChi.setText("...");
                }
            } else {
                mHinhThucCuTru.setText("...");
                mDiaChi.setText("...");
            }

//            if (mDiaChi.getText() == "") mLayoutDiDong.setVisibility(View.GONE);
//            if (mCoDinh.getText() == "") mLayouCoDinh.setVisibility(View.GONE);
//            if (mEmail.getText() == "") mLayoutEmail.setVisibility(View.GONE);
//            if (mNgayCuTru.getText() == "") mLayoutNgayCuTru.setVisibility(View.GONE);
        }
        if (queQuan != null) {
            String temp = "";
            if (!isNullOrEmpty(queQuan.getDiaChi())) temp += queQuan.getDiaChi();
            if (!isNullOrEmpty(queQuan.getTenPhuongXa())) temp += ", " + queQuan.getTenPhuongXa();
            if (!isNullOrEmpty(queQuan.getTenQuanHuyen())) temp += ", " + queQuan.getTenQuanHuyen();
            if (!isNullOrEmpty(queQuan.getTenThanhPho())) temp += ", " + queQuan.getTenQuocGia();
            if (!isNullOrEmpty(queQuan.getTenQuocGia())) temp += ", " + queQuan.getTenQuocGia();
            String queQuan = getValueOrEmpty(temp);
            mQueQuan.setText(queQuan);
        }
    }

    public void setThongTin(VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        this.thongTinLienHe = thongTinLienHe;
        this.thuongTru = thuongTru;
        this.queQuan = queQuan;
    }
}
