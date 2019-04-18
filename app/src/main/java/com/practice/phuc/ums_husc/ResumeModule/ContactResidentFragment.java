package com.practice.phuc.ums_husc.ResumeModule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.PhuongXa;
import com.practice.phuc.ums_husc.ViewModel.QuanHuyen;
import com.practice.phuc.ums_husc.ViewModel.QuocGia;
import com.practice.phuc.ums_husc.ViewModel.ThanhPho;
import com.practice.phuc.ums_husc.ViewModel.VQueQuan;
import com.practice.phuc.ums_husc.ViewModel.VThongTinLienHe;
import com.practice.phuc.ums_husc.ViewModel.VThuongTru;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.practice.phuc.ums_husc.Helper.StringHelper.getValueOrEmpty;
import static com.practice.phuc.ums_husc.Helper.StringHelper.isNullOrEmpty;

public class ContactResidentFragment extends Fragment {
    private Context mContext;
    private boolean mIsCreated;

    private final String GET_NATIONS = "nations";
    private final String GET_CITIES_BY_NATION = "cites";
    private final String GET_DISTRICT_BY_CITY = "districts";
    private final String GET_WARDS_BY_DISTRICT = "wards";
    private final String DO_UPDATE = "update";

    public ContactResidentFragment() {
    }

    public static ContactResidentFragment newInstance(VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        ContactResidentFragment f = new ContactResidentFragment();
        f.thongTinLienHe = thongTinLienHe;
        f.thuongTru = thuongTru;
        f.queQuan = queQuan;
        return f;
    }

    public void setThongTin(VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        this.thongTinLienHe = thongTinLienHe;
        this.thuongTru = thuongTru;
        this.queQuan = queQuan;
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
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private EditText mEtDiDong, mEtCoDinh, mEtEmail;
    private Spinner mSpHinhThucCT, mSpNgayCT, mSpThangCT, mSpNamCT;
    private EditText mEtDiaChiCT;
    private Spinner mSpQuocGiaQQ, mSpTinhThanhQQ, mSpQuanHuyenQQ, mSpPhuongXaQQ;
    private Spinner mSpQuocGiaTR, mSpTinhThanhTR, mSpQuanHuyenTR, mSpPhuongXaTR;
    private EditText mEtDiaChiQQ, mEtDiaChiTR;
    private Button mBtnSave, mBtnCloseSlideBtm;
    private ImageButton mBtnCloseSlide, mBtnOpenSlide;
    private ProgressBar mPbLoading;

    private ArrayAdapter<String> mHinhThucCTAdt, mNgayCTAdt, mThangCTAdt, mNamCTAdt;
    private ArrayAdapter<QuocGia> mQuocGiaQQAdt, mQuocGiaTRAdt;
    private ArrayAdapter<ThanhPho> mTinhThanhQQAdt, mTinhThanhTRAdt;
    private ArrayAdapter<QuanHuyen> mQuanHuyenQQAdt, mQuanHuyenTRAdt;
    private ArrayAdapter<PhuongXa> mPhuongXaQQAdt, mPhuongXaTRAdt;

    private List<String> mHinhThucCTData, mNgayCTData, mThangCTData, mNamCTData;
    private List<QuocGia> mQuocGiaQQData, mQuocGiaTRData;
    private List<ThanhPho> mTinhThanhQQData, mTinhThanhTRData;
    private List<QuanHuyen> mQuanHuyenQQData, mQuanHuyenTRData;
    private List<PhuongXa> mPhuongXaQQData, mPhuongXaTRData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsCreated = false;

        mHinhThucCTData = new ArrayList<>();
        mNgayCTData = new ArrayList<>();
        mThangCTData = new ArrayList<>();
        mNamCTData = new ArrayList<>();
        mQuocGiaQQData = new ArrayList<>();
        mTinhThanhQQData = new ArrayList<>();
        mQuanHuyenQQData = new ArrayList<>();
        mPhuongXaQQData = new ArrayList<>();
        mQuocGiaTRData = new ArrayList<>();
        mTinhThanhTRData = new ArrayList<>();
        mQuanHuyenTRData = new ArrayList<>();
        mPhuongXaTRData = new ArrayList<>();

        for (int i = 0; i <= 31; i++) {
            mNgayCTData.add(i + "");
            if (i <= 12) mThangCTData.add(i + "");
        }
        mNamCTData.add(0 + "");
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1990; i <= year; i++) {
            mNamCTData.add(i + "");
        }

        mNgayCTAdt = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mNgayCTData);
        mNgayCTAdt.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mThangCTAdt = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mThangCTData);
        mThangCTAdt.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mNamCTAdt = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mNamCTData);
        mNamCTAdt.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mQuocGiaQQAdt = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mQuocGiaQQData);
        mQuocGiaQQAdt.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mTinhThanhQQAdt = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mTinhThanhQQData);
        mTinhThanhQQAdt.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mQuanHuyenQQAdt = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mQuanHuyenQQData);
        mQuanHuyenQQAdt.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mPhuongXaQQAdt = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mPhuongXaQQData);
        mPhuongXaQQAdt.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mQuocGiaTRAdt = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mQuocGiaTRData);
        mQuocGiaTRAdt.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mTinhThanhTRAdt = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mTinhThanhTRData);
        mTinhThanhTRAdt.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mQuanHuyenTRAdt = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mQuanHuyenTRData);
        mQuanHuyenTRAdt.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mPhuongXaTRAdt = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mPhuongXaTRData);
        mPhuongXaTRAdt.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_resident, container, false);
        bindUI(view);
        setUpEvent();

        setUpMainPanel(thongTinLienHe, thuongTru, queQuan);
        setUpSlidePanel(thongTinLienHe, thuongTru, queQuan);

        if (!mIsCreated) {
            mIsCreated = true;

        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsCreated = false;
        mNgayCTData.clear();
        mThangCTData.clear();
        mNamCTData.clear();
        mQuocGiaQQData.clear();
        mTinhThanhQQData.clear();
        mQuanHuyenQQData.clear();
        mPhuongXaQQData.clear();
        mQuocGiaTRData.clear();
        mTinhThanhTRData.clear();
        mQuanHuyenTRData.clear();
        mPhuongXaTRData.clear();
    }

    /*###### EVENTS ######*/
    private void setUpEvent() {
        mBtnOpenSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                mSlidingUpPanelLayout.setTouchEnabled(false);
            }
        });
        mBtnCloseSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        mBtnCloseSlideBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        mSpThangCT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayCTData, mNgayCTAdt, position, mSpNamCT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpNamCT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayCTData, mNgayCTAdt, mSpThangCT.getSelectedItemPosition(), mSpNamCT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpQuocGiaQQ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QuocGia quocGia = (QuocGia) mSpQuocGiaQQ.getSelectedItem();
                int maQuocGia = quocGia.MaQuocGia;
                new Task().execute(GET_CITIES_BY_NATION, maQuocGia + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpTinhThanhQQ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ThanhPho thanhPho = (ThanhPho) mSpTinhThanhQQ.getSelectedItem();
                new Task().execute(GET_DISTRICT_BY_CITY, thanhPho.MaThanhPho + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpQuanHuyenQQ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QuanHuyen quanHuyen = (QuanHuyen) mSpQuanHuyenQQ.getSelectedItem();
                new Task().execute(GET_WARDS_BY_DISTRICT, quanHuyen.MaQuanHuyen + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpQuocGiaTR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QuocGia quocGia = (QuocGia) mSpQuocGiaTR.getSelectedItem();
                int maQuocGia = quocGia.MaQuocGia;
                new Task().execute(GET_CITIES_BY_NATION, maQuocGia + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpTinhThanhTR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ThanhPho thanhPho = (ThanhPho) mSpTinhThanhTR.getSelectedItem();
                new Task().execute(GET_DISTRICT_BY_CITY, thanhPho.MaThanhPho + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpQuanHuyenTR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QuanHuyen quanHuyen = (QuanHuyen) mSpQuanHuyenTR.getSelectedItem();
                new Task().execute(GET_WARDS_BY_DISTRICT, quanHuyen.MaQuanHuyen + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.getConnectivityStatus(mContext) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    Toast.makeText(mContext, getString(R.string.error_network_disconected), Toast.LENGTH_LONG).show();
                } else {
                    VThongTinLienHe thongTinLienHe = new VThongTinLienHe();
                    VThuongTru thuongTru = new VThuongTru();
                    VQueQuan queQuan = new VQueQuan();

                    attempUpdate(thongTinLienHe, thuongTru, queQuan);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setUpMainPanel(VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        if (thuongTru != null) {
            String temp = "";
            if (!isNullOrEmpty(thuongTru.getDiaChi())) temp += thuongTru.getDiaChi();
            if (!isNullOrEmpty(thuongTru.getTenPhuongXa()))
                temp += ", " + thuongTru.getTenPhuongXa();
            if (!isNullOrEmpty(thuongTru.getTenQuanHuyen()))
                temp += ", " + thuongTru.getTenQuanHuyen();
            if (!isNullOrEmpty(thuongTru.getTenThanhPho()))
                temp += ", " + thuongTru.getTenQuocGia();
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
        }
        if (queQuan != null) {
            String temp = "";
            if (!isNullOrEmpty(queQuan.getDiaChi())) temp += queQuan.getDiaChi();
            if (!isNullOrEmpty(queQuan.getTenPhuongXa())) temp += ", " + queQuan.getTenPhuongXa();
            if (!isNullOrEmpty(queQuan.getTenQuanHuyen())) temp += ", " + queQuan.getTenQuanHuyen();
            if (!isNullOrEmpty(queQuan.getTenThanhPho())) temp += ", " + queQuan.getTenQuocGia();
            if (!isNullOrEmpty(queQuan.getTenQuocGia())) temp += ", " + queQuan.getTenQuocGia();
            String queQuanStr = getValueOrEmpty(temp);
            mQueQuan.setText(queQuanStr);
        }
    }

    private void setUpSlidePanel(VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        // Di dong
        String diDong = !isNullOrEmpty(thongTinLienHe.DiDong) ? thongTinLienHe.DiDong : "";
        mEtDiDong.append(diDong);
        // Co dinh
        String coDinh = !isNullOrEmpty(thongTinLienHe.DienThoai) ? thongTinLienHe.DienThoai : "";
        mEtCoDinh.append(coDinh);
        // Email
        String email = !isNullOrEmpty(thongTinLienHe.Email) ? thongTinLienHe.Email : "";
        mEtEmail.append(email);
        // Hinh thuc cu tru

        // Ngay cu tru
        String ngayCT;
        int indexNgay = 0, indexThang = 0, indexNam = 0;
        if (!isNullOrEmpty(thongTinLienHe.NgayBatDauCuTru)) {
            ngayCT = DateHelper.formatYMDToDMY(thongTinLienHe.NgayBatDauCuTru.substring(0, 10));
            Date d = DateHelper.stringToDate(ngayCT, "dd/MM/yyyy");
            indexNgay = DateHelper.getDayOfMonth(d);
            indexThang = DateHelper.getMonth(d);
            indexNam = DateHelper.getYear(d);
        }
        mSpNgayCT.setSelection(indexNgay);
        mSpThangCT.setSelection(indexThang);
        mSpNamCT.setSelection(indexNam);
        // Dia chi cu tru
        String diaChiCT = !isNullOrEmpty(thongTinLienHe.DiaChi) ? thongTinLienHe.DiaChi : "";
        mEtDiaChiCT.append(diaChiCT);
        // Quoc gia Que Quan
        updateSpQuocGiaQQ(mQuocGiaQQData);
        // Tinh thanh QQ
        updateSpTinhThanhQQ(mTinhThanhQQData);
        // Quan huyen QQ
        updateSpQuanHuyenQQ(mQuanHuyenQQData);
        // Phuong xa QQ
        updateSpPhuongXaQQ(mPhuongXaQQData);
        // Dia chi QQ
        String diaChiQQ = !isNullOrEmpty(queQuan.DiaChi) ? queQuan.DiaChi : "";
        mEtDiaChiQQ.append(diaChiQQ);
        // Quoc gia Thuong tru
        updateSpQuocGiaTR(mQuocGiaTRData);
        // Tinh thanh TR
        updateSpTinhThanhTR(mTinhThanhTRData);
        // Quan huyen TR
        updateSpQuanHuyenTR(mQuanHuyenTRData);
        // Phuong xa TR
        updateSpPhuongXaTR(mPhuongXaTRData);
        // Dia chi TR
        String diaChiTR = !isNullOrEmpty(thuongTru.DiaChi) ? thuongTru.DiaChi : "";
        mEtDiaChiTR.append(diaChiTR);
    }

    private void attempUpdate(VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        if (NetworkUtil.getConnectivityStatus(mContext) == NetworkUtil.TYPE_NOT_CONNECTED) {
            Toast.makeText(mContext, getString(R.string.error_network_disconected), Toast.LENGTH_LONG).show();

        } else {
            String lienHeJson = VThongTinLienHe.toJson(thongTinLienHe);
            String thuongTruJson = VThuongTru.toJson(thuongTru);
            String queQuanJson = VQueQuan.toJson(queQuan);
            new Task().execute(DO_UPDATE, lienHeJson, thuongTruJson, queQuanJson);
            mPbLoading.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class Task extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }
    }

    /*##### UPDATE DATA ON VIEW #####*/

    private void bindUI(@NonNull View view) {
        mCoDinh = view.findViewById(R.id.tv_coDinh);
        mDiDong = view.findViewById(R.id.tv_diDong);
        mEmail = view.findViewById(R.id.tv_email);
        mHinhThucCuTru = view.findViewById(R.id.tv_hinhThucCuTru);
        mNgayCuTru = view.findViewById(R.id.tv_ngayBatDauCuTru);
        mDiaChi = view.findViewById(R.id.tv_diaChiCuTru);
        mQueQuan = view.findViewById(R.id.tv_queQuan);
        mHoKhau = view.findViewById(R.id.tv_hoKhauThuongTru);

        mSlidingUpPanelLayout = view.findViewById(R.id.sliding_layout);
        mEtDiDong = view.findViewById(R.id.et_diDong);
        mEtCoDinh = view.findViewById(R.id.et_coDinh);
        mEtEmail = view.findViewById(R.id.et_email);
        mSpHinhThucCT = view.findViewById(R.id.sp_hinhThucCuTru);
        mSpNgayCT = view.findViewById(R.id.sp_ngayBdCuTru);
        mSpThangCT = view.findViewById(R.id.sp_thangBdCuTru);
        mSpNamCT = view.findViewById(R.id.sp_namBdCuTru);
        mEtDiaChiCT = view.findViewById(R.id.et_diaChiCuTru);
        mSpQuocGiaQQ = view.findViewById(R.id.sp_quocGiaQQ);
        mSpTinhThanhQQ = view.findViewById(R.id.sp_tinhThanhQQ);
        mSpQuanHuyenQQ = view.findViewById(R.id.sp_quanHuyenQQ);
        mSpPhuongXaQQ = view.findViewById(R.id.sp_phuongXaQQ);
        mSpQuocGiaTR = view.findViewById(R.id.sp_quocGiaTR);
        mSpTinhThanhTR = view.findViewById(R.id.sp_tinhThanhTR);
        mSpQuanHuyenTR = view.findViewById(R.id.sp_quanHuyenTR);
        mSpPhuongXaTR = view.findViewById(R.id.sp_phuongXaTR);
        mEtDiaChiQQ = view.findViewById(R.id.et_diaChiQQ);
        mEtDiaChiTR = view.findViewById(R.id.et_diaChiTR);
        mBtnOpenSlide = view.findViewById(R.id.btn_suaThongTinLienHeCuTru);
        mBtnCloseSlide = view.findViewById(R.id.btn_closeSlidePanel);
        mBtnCloseSlideBtm = view.findViewById(R.id.btn_closeSlidePanelBtm);
        mBtnSave = view.findViewById(R.id.btn_save);
        mPbLoading = view.findViewById(R.id.pb_updateLoading);
    }

    private void updateSpQuocGiaQQ(List<QuocGia> quocGias) {
        if (quocGias == null) return;
        int index = 0;

        if (quocGias.size() > 0) {
            int maHienTai = 0;
            for (int i = 0; i < quocGias.size(); i++)
                if (quocGias.get(i).MaQuocGia == maHienTai) {
                    index = i;
                    break;
                }
        }
        mSpQuocGiaQQ.setSelection(index);
    }

    private void updateSpTinhThanhQQ(List<ThanhPho> thanhPhos) {
        if (thanhPhos == null) return;
        int index = 0;

        if (thanhPhos.size() > 0) {
            int maHienTai = 0;
            for (int i = 0; i < thanhPhos.size(); i++)
                if (thanhPhos.get(i).MaThanhPho == maHienTai) {
                    index = i;
                    break;
                }
        }
        mSpTinhThanhQQ.setSelection(index);
    }

    private void updateSpQuanHuyenQQ(List<QuanHuyen> quanHuyens) {
        if (quanHuyens == null) return;
        int index = 0;

        if (quanHuyens.size() > 0) {
            int maHienTai = 0;
            for (int i = 0; i < quanHuyens.size(); i++)
                if (quanHuyens.get(i).MaQuanHuyen == maHienTai) {
                    index = i;
                    break;
                }
        }
        mSpQuanHuyenQQ.setSelection(index);
    }

    private void updateSpPhuongXaQQ(List<PhuongXa> phuongXas) {
        if (phuongXas == null) return;
        int index = 0;

        if (phuongXas.size() > 0) {
            int maHienTai = 0;
            for (int i = 0; i < phuongXas.size(); i++)
                if (phuongXas.get(i).MaPhuongXa == maHienTai) {
                    index = i;
                    break;
                }
        }
        mSpPhuongXaQQ.setSelection(index);
    }

    private void updateSpQuocGiaTR(List<QuocGia> quocGias) {
        if (quocGias == null) return;
        int index = 0;

        if (quocGias.size() > 0) {
            int maHienTai = 0;
            for (int i = 0; i < quocGias.size(); i++)
                if (quocGias.get(i).MaQuocGia == maHienTai) {
                    index = i;
                    break;
                }
        }
        mSpQuocGiaTR.setSelection(index);
    }

    private void updateSpTinhThanhTR(List<ThanhPho> thanhPhos) {
        if (thanhPhos == null) return;
        int index = 0;

        if (thanhPhos.size() > 0) {
            int maHienTai = 0;
            for (int i = 0; i < thanhPhos.size(); i++)
                if (thanhPhos.get(i).MaThanhPho == maHienTai) {
                    index = i;
                    break;
                }
        }
        mSpTinhThanhTR.setSelection(index);
    }

    private void updateSpQuanHuyenTR(List<QuanHuyen> quanHuyens) {
        if (quanHuyens == null) return;
        int index = 0;

        if (quanHuyens.size() > 0) {
            int maHienTai = 0;
            for (int i = 0; i < quanHuyens.size(); i++)
                if (quanHuyens.get(i).MaQuanHuyen == maHienTai) {
                    index = i;
                    break;
                }
        }
        mSpQuanHuyenTR.setSelection(index);
    }

    private void updateSpPhuongXaTR(List<PhuongXa> phuongXas) {
        if (phuongXas == null) return;
        int index = 0;

        if (phuongXas.size() > 0) {
            int maHienTai = 0;
            for (int i = 0; i < phuongXas.size(); i++)
                if (phuongXas.get(i).MaPhuongXa == maHienTai) {
                    index = i;
                    break;
                }
        }
        mSpPhuongXaTR.setSelection(index);
    }

    private void updateNamNhuan(List<String> data, ArrayAdapter adapter, int position, Spinner spNam) {
        switch (position) {
            case 2: // Thang 2
                int namSinh = Integer.valueOf(spNam.getSelectedItem().toString());

                if ((namSinh % 400 == 0) || ((namSinh % 100 != 0) && (namSinh % 4 == 0))) {// nam nhuan
                    if (data.size() == 32) { // Hien co 31 ngay
                        data.remove(31); // remove 31
                        data.remove(30); // remove 30
                    } else if (data.size() == 31) { // Hien co 30 ngay
                        data.remove(30); // remove 30
                    } else if (data.size() == 29) { // Hien co 28 ngay
                        data.add(29, "29");
                    }

                } else { // Nam khong nhuan
                    if (data.size() == 32) { // Hien co 31 ngay
                        data.remove(31);
                        data.remove(30);
                        data.remove(29);
                    } else if (data.size() == 31) { // Hien co 30 ngay
                        data.remove(30);
                        data.remove(29);
                    } else if (data.size() == 30) { // Hien co 29 ngay
                        data.remove(29);
                    }
                }
                break;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12: // Nhung thang 31 ngay
                if (data.size() == 31) // Hien co 30 ngay
                    data.add(31, "31");
                else if (data.size() == 30) { // Hien co 29 ngay
                    data.add(30, "30");
                    data.add(31, "31");
                } else if (data.size() == 29) { // Hien co 28 ngay
                    data.add(29, "29");
                    data.add(30, "30");
                    data.add(31, "31");
                }
                break;
            default: // Nhung thang co 30 ngay
                if (data.size() == 32) // Hien co 31 ngay
                    data.remove(31);
                else if (data.size() == 29) { // Hien co 28 ngay
                    data.add(29, "29");
                    data.add(30, "30");
                } else if (data.size() == 30) { // Hien co 29 ngay
                    data.add(30, "30");
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }
}
