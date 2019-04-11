package com.practice.phuc.ums_husc.ResumeModule;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.VThongTinChung;

import static com.practice.phuc.ums_husc.Helper.StringHelper.isNullOrEmpty;

public class ThongTinChungFragment extends Fragment {

    public ThongTinChungFragment() {
        // Required empty public constructor
    }

    public static ThongTinChungFragment newInstance(VThongTinChung thongTinChung) {
        Log.d("DEBUG", "New instance thong tin chung fragment");
        ThongTinChungFragment f = new ThongTinChungFragment();
        f.thongTinChung = thongTinChung;
        return f;
    }

    public void setThongTin(VThongTinChung thongTinChung) {
        this.thongTinChung = thongTinChung;
    }

    private VThongTinChung thongTinChung;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        return view;
    }

    public void displayThongTin() {
        if (thongTinChung != null) {
            String empty = "...";
            String gioiTinh = thongTinChung.getGioiTinh() ? "Nam" : "Nữ";
            String noiSinh = empty;
            if (thongTinChung.getNoiSinh() != null) {
                String thanhPhoSinh = !isNullOrEmpty(thongTinChung.getNoiSinh().getTenQuocGia()) ?
                        thongTinChung.getNoiSinh().getTenThanhPho() : empty;
                String quocGiaSinh = !isNullOrEmpty(thongTinChung.getNoiSinh().getTenQuocGia()) ?
                        thongTinChung.getNoiSinh().getTenQuocGia() : empty;
                noiSinh = thanhPhoSinh + ", " + quocGiaSinh;
            }
            String ngaySinh = !isNullOrEmpty(thongTinChung.getNgaySinh()) ? thongTinChung.getNgaySinh().substring(0, 10) : empty;
            String quocTich = !isNullOrEmpty(thongTinChung.getTenQuocGia()) ? thongTinChung.getTenQuocGia() : empty;
            String danToc = !isNullOrEmpty(thongTinChung.getTenDanToc()) ? thongTinChung.getTenDanToc() : empty;
            String tonGiao = !isNullOrEmpty(thongTinChung.getTenTonGiao()) ? thongTinChung.getTenTonGiao() : empty;
            String soCMND = !isNullOrEmpty(thongTinChung.getSoCMND()) ? thongTinChung.getSoCMND() : empty;
            String ngayCapCMND = !isNullOrEmpty(thongTinChung.getNgayCap()) ? thongTinChung.getNgayCap().substring(0, 10) : empty;
            String noiCapCMND = !isNullOrEmpty(thongTinChung.getNoiCap()) ? thongTinChung.getNoiCap() : empty;

            mGioiTinh.setText(gioiTinh);
            mNgaySinh.setText(ngaySinh);
            mNoiSinh.setText(noiSinh);
            mQuocTich.setText(quocTich);
            mDanToc.setText(danToc);
            mTonGiao.setText(tonGiao);
            mSoCMND.setText(soCMND);
            mNgayCapCMND.setText(ngayCapCMND);
            mNoiCapCMND.setText(noiCapCMND);
        }
    }
}
