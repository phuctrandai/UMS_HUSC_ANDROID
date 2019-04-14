package com.practice.phuc.ums_husc.ResumeModule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.VThongTinChung;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.practice.phuc.ums_husc.Helper.StringHelper.isNullOrEmpty;

public class ThongTinChungFragment extends Fragment {
    private Context mContext;

    public ThongTinChungFragment() {
    }

    public static ThongTinChungFragment newInstance(Context context, VThongTinChung thongTinChung) {
        ThongTinChungFragment f = new ThongTinChungFragment();
        f.mContext = context;
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
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private Spinner spNgaySinh, spThangSinh, spNamSinh;
    private Spinner spQuocGiaSinh, spTinhThanhSinh;
    private Spinner spQuocTich, spTonGiao, spDanToc;
    private EditText etSoCMND;
    private Spinner spNgayCapCMND, spThangCapCMND, spNamCapCMND;
    private EditText etNoiCapCMND;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thong_tin_chung, container, false);
        bindUI(view);
        setUpEvent();

        setUpSpNgay();
        setUpSpThang();
        setUpSpNam();

        setUpMainPanel(thongTinChung);

        return view;
    }

    /*###### EVENTS #####*/
    private void setUpEvent() {
        spNgaySinh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spThangSinh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(ngaySinhData, adapterNgaySinh, position, spNamSinh);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spNamSinh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(ngaySinhData, adapterNgaySinh, spThangSinh.getSelectedItemPosition(), spNamSinh);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spNgayCapCMND.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spThangCapCMND.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(ngayCapCMNDData, adapterNgayCap, position, spNamCapCMND);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spNamCapCMND.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(ngayCapCMNDData, adapterNgayCap, spThangCapCMND.getSelectedItemPosition(), spNamCapCMND);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*##### DATA #####*/
    private void setUpMainPanel(VThongTinChung thongTinChung){
        if (thongTinChung != null) {
            String gioiTinh = thongTinChung.getGioiTinh() ? "Nam" : "Nữ";
            mGioiTinh.setText(gioiTinh);

            // Ngay sinh
            String ngaySinh = isNullOrEmpty(thongTinChung.getNgaySinh()) ? "..."
                    : DateHelper.formatYMDToDMY(thongTinChung.getNgaySinh().substring(0, 10));
            mNgaySinh.setText(ngaySinh);
            // Noi sinh
            String thanhPhoSinh = !isNullOrEmpty(thongTinChung.getNoiSinh().getTenQuocGia()) ?
                    thongTinChung.getNoiSinh().getTenThanhPho() : "...";
            String quocGiaSinh = !isNullOrEmpty(thongTinChung.getNoiSinh().getTenQuocGia()) ?
                    thongTinChung.getNoiSinh().getTenQuocGia() : "...";
            String noiSinh = thanhPhoSinh + ", " + quocGiaSinh;
            mNoiSinh.setText(noiSinh);
            // Quoc tich
            String quocTich = !isNullOrEmpty(thongTinChung.getTenQuocGia()) ? thongTinChung.getTenQuocGia() : "...";
            mQuocTich.setText(quocTich);
            // Ton giao
            String tonGiao = !isNullOrEmpty(thongTinChung.getTenTonGiao()) ? thongTinChung.getTenTonGiao() : "...";
            mTonGiao.setText(tonGiao);
            // Dan toc
            String danToc = !isNullOrEmpty(thongTinChung.getTenDanToc()) ? thongTinChung.getTenDanToc() : "...";
            mDanToc.setText(danToc);
            // So CMND
            String soCMND = !isNullOrEmpty(thongTinChung.getSoCMND()) ? thongTinChung.getSoCMND() : "...";
            mSoCMND.setText(soCMND);
            // Ngay cap CMND
            String ngayCapCMND = isNullOrEmpty(thongTinChung.getNgaySinh()) ? "..."
                    : DateHelper.formatYMDToDMY(thongTinChung.getNgayCap().substring(0, 10));
            mNgayCapCMND.setText(ngayCapCMND);
            // Noi cap CMND
            String noiCapCMND = !isNullOrEmpty(thongTinChung.getNoiCap()) ? thongTinChung.getNoiCap() : "...";
            mNoiCapCMND.setText(noiCapCMND);
        }
    }

    private void setUpSlidePanel(VThongTinChung thongTinChung) {
        // Ngay sinh
        String ngaySinh = "...";
        if (!isNullOrEmpty(thongTinChung.getNgaySinh())) {
            ngaySinh = DateHelper.formatYMDToDMY(thongTinChung.getNgaySinh().substring(0, 10));
            Date d = DateHelper.stringToDate(ngaySinh, "dd/MM/yyyy");
            spNgaySinh.setSelection(DateHelper.getDayOfMonth(d) - 1);
            spThangSinh.setSelection(DateHelper.getMonth(d) - 1);
            spNamSinh.setSelection(DateHelper.getYear(d) - 1990);
        }
        // Noi sinh

        // So CMND
        String soCMND = !isNullOrEmpty(thongTinChung.getSoCMND()) ? thongTinChung.getSoCMND() : "...";
        etSoCMND.setText(soCMND);
        // Ngay cap CMND
        String ngayCapCMND = "...";
        if (!isNullOrEmpty(thongTinChung.getNgaySinh())) {
            ngayCapCMND = DateHelper.formatYMDToDMY(thongTinChung.getNgayCap().substring(0, 10));
            Date d = DateHelper.stringToDate(ngayCapCMND, "dd/MM/yyyy");
            spNgayCapCMND.setSelection(DateHelper.getDayOfMonth(d) - 1);
            spThangCapCMND.setSelection(DateHelper.getMonth(d) - 1);
            spNamCapCMND.setSelection(DateHelper.getYear(d) - 1990);
        }
        // Noi cap CMND
        String noiCapCMND = !isNullOrEmpty(thongTinChung.getNoiCap()) ? thongTinChung.getNoiCap() : "...";
        etNoiCapCMND.setText(noiCapCMND);
    }

    /*####################*/

    private void bindUI(View view) {
        mSlidingUpPanelLayout = view.findViewById(R.id.sliding_layout);
        mGioiTinh = view.findViewById(R.id.tv_gioiTinh);
        mNgaySinh = view.findViewById(R.id.tv_ngaySinh);
        mNoiSinh = view.findViewById(R.id.tv_noiSinh);
        mQuocTich = view.findViewById(R.id.tv_quocTich);
        mDanToc = view.findViewById(R.id.tv_danToc);
        mTonGiao = view.findViewById(R.id.tv_tonGiao);
        mSoCMND = view.findViewById(R.id.tv_soCMND);
        mNgayCapCMND = view.findViewById(R.id.tv_ngayCapCMND);
        mNoiCapCMND = view.findViewById(R.id.tv_noiCapCMND);
        ImageButton mChinhSua = view.findViewById(R.id.btn_suaThongTinChung);
        ImageButton mCloseSlidePanel = view.findViewById(R.id.btn_closeSlidePanel);
        mChinhSua.setOnClickListener(onModify);
        mCloseSlidePanel.setOnClickListener(onClose);

        spNgaySinh = view.findViewById(R.id.sp_ngaySinh);
        spThangSinh = view.findViewById(R.id.sp_thangSinh);
        spNamSinh = view.findViewById(R.id.sp_namSinh);
        spQuocGiaSinh = view.findViewById(R.id.sp_quocGiaSinh);
        spTinhThanhSinh = view.findViewById(R.id.sp_tinhThanhSinh);
        spQuocTich = view.findViewById(R.id.sp_quocTich);
        spTonGiao = view.findViewById(R.id.sp_tonGiao);
        spDanToc = view.findViewById(R.id.sp_danToc);
        etSoCMND = view.findViewById(R.id.et_soCMND);
        spNgayCapCMND = view.findViewById(R.id.sp_ngayCap);
        spThangCapCMND = view.findViewById(R.id.sp_thangCap);
        spNamCapCMND = view.findViewById(R.id.sp_namCap);
        etNoiCapCMND = view.findViewById(R.id.et_noiCapCMND);
    }

    View.OnClickListener onModify = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            mSlidingUpPanelLayout.setTouchEnabled(false);
            setUpSlidePanel(thongTinChung);
        }
    };

    View.OnClickListener onClose = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    };

    ArrayAdapter<String> adapterNgaySinh;
    private List<String> ngaySinhData;
    private List<String> ngayCapCMNDData;
    ArrayAdapter<String> adapterNgayCap;

    private void setUpSpNgay() {
        ngaySinhData = new ArrayList<>();
        ngayCapCMNDData = new ArrayList<>();

        for (int i = 1; i <= 31; i++) {
            ngaySinhData.add(i + "");
            ngayCapCMNDData.add(i + "");
        }

        adapterNgaySinh = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, ngaySinhData);
        adapterNgaySinh.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spNgaySinh.setAdapter(adapterNgaySinh);

        adapterNgayCap = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, ngayCapCMNDData);
        adapterNgayCap.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spNgayCapCMND.setAdapter(adapterNgayCap);
    }

    private void setUpSpThang() {
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(i + "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spThangSinh.setAdapter(adapter);
        spThangCapCMND.setAdapter(adapter);
    }

    private void setUpSpNam() {
        List<String> years = new ArrayList<>();
        for (int i = 1990; i <= 2019; i++) {
            years.add(i + "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spNamSinh.setAdapter(adapter);
        spNamCapCMND.setAdapter(adapter);
    }

    private void updateNamNhuan(List<String> data, ArrayAdapter adapter, int position, Spinner spNam) {
        switch (position) {
            case 1: // Thang 2
                int namSinh = Integer.valueOf(spNam.getSelectedItem().toString());

                if ((namSinh % 400 == 0) || ((namSinh % 100 != 0) && (namSinh % 4 == 0))) {// nam nhuan
                    if (data.size() == 31) { // Hien co 31 ngay
                        data.remove(30); // remove 31
                        data.remove(29); // remove 30
                    } else if (data.size() == 30) {
                        data.remove(29); // remove 30
                    } else if (data.size() == 28) {
                        data.add(28, "29");
                    }

                } else { // Nam khong nhuan
                    if (data.size() == 31) {
                        data.remove(30);
                        data.remove(29);
                        data.remove(28);
                    } else if (data.size() == 30) {
                        data.remove(29);
                        data.remove(28);
                    } else if (data.size() == 29) {
                        data.remove(28);
                    }
                }
                break;
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11: // Nhung thang 31 ngay
                if (data.size() == 30)
                    data.add(30, "31");
                else if (data.size() == 29) { // Hien co 29 ngay
                    data.add(29, "30");
                    data.add(30, "31");
                } else if (data.size() == 28) { // Hien co 28 ngay
                    data.add(28, "29");
                    data.add(29, "30");
                    data.add(30, "31");
                }
                break;
            default: // Nhung thang co 30 ngay
                if (data.size() == 31)
                    data.remove(30);
                else if (data.size() == 28) {
                    data.add(28, "29");
                    data.add(29, "30");
                } else if (data.size() == 29) {
                    data.add(29, "30");
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }
}
