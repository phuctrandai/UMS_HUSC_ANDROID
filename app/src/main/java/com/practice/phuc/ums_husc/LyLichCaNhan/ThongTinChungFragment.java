package com.practice.phuc.ums_husc.LyLichCaNhan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.VThongTinChung;

public class ThongTinChungFragment extends Fragment {

    public ThongTinChungFragment() {
        // Required empty public constructor
    }

    private static VThongTinChung thongTinChung = null;
    private TextView mGioiTinh;
    private TextView mNgaySinh;
    private TextView mNoiSinh;
    private TextView mQuocTich;
    private TextView mDanToc;
    private TextView mTonGiao;
    private TextView mSoCMND;
    private TextView mNgayCapCMND;
    private TextView mNoiCapCMND;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thong_tin_chung, container, false);

        mGioiTinh = view.findViewById(R.id.tv_gioiTinh);
        mNgaySinh = view.findViewById(R.id.tv_ngaySinh);
        mNoiSinh = view.findViewById(R.id.tv_noiSinh);
        mQuocTich = view.findViewById(R.id.tv_quocTich);
        mDanToc = view.findViewById(R.id.tv_danToc);
        mTonGiao = view.findViewById(R.id.tv_tonGiao);
        mSoCMND = view.findViewById(R.id.tv_soCMND);
        mNgayCapCMND = view.findViewById(R.id.tv_ngayCapCMND);
        mNoiCapCMND = view.findViewById(R.id.tv_noiCapCMND);

        displayThongTin();

        Log.d("UMS", "Create thong tin chung !!!");
        return view;
    }

    public void setThongTin(VThongTinChung thongTinChung) {
        this.thongTinChung = thongTinChung;
    }

    public void displayThongTin() {
        if (thongTinChung != null) {
            mGioiTinh.setText(thongTinChung.getGioiTinh() == true ? "Nam" : "Nữ");
            mNgaySinh.setText(thongTinChung.getNgaySinh().substring(0, 10));
            mNoiSinh.setText(thongTinChung.getNoiSinh());
            mQuocTich.setText(thongTinChung.getTenQuocGia());
            mDanToc.setText(thongTinChung.getTenDanToc());
            mTonGiao.setText(thongTinChung.getTenTonGiao());
            mSoCMND.setText(thongTinChung.getSoCMND());
            mNgayCapCMND.setText(thongTinChung.getNgayCap().substring(0, 10));
            mNoiCapCMND.setText(thongTinChung.getNoiCap());
        }
    }
}
