package com.practice.phuc.ums_husc.ResumeModule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.KYTUCXA;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.PhuongXa;
import com.practice.phuc.ums_husc.ViewModel.QuanHuyen;
import com.practice.phuc.ums_husc.ViewModel.QuocGia;
import com.practice.phuc.ums_husc.ViewModel.ThanhPho;
import com.practice.phuc.ums_husc.ViewModel.VLyLichCaNhan;
import com.practice.phuc.ums_husc.ViewModel.VQueQuan;
import com.practice.phuc.ums_husc.ViewModel.VThongTinLienHe;
import com.practice.phuc.ums_husc.ViewModel.VThuongTru;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.practice.phuc.ums_husc.Helper.StringHelper.getValueOrEmpty;
import static com.practice.phuc.ums_husc.Helper.StringHelper.isNullOrEmpty;

public class ContactResidentFragment extends Fragment {
    private Context mContext;
    private boolean mIsCreated;

    private final String GET_NATIONS = "nations";
    private final String GET_CITIES_BY_NATION = "cites";
    private final String GET_DISTRICT_BY_CITY = "districts";
    private final String GET_WARDS_BY_DISTRICT = "wards";
    private final String GET_DORMITORY = "dormitory";
    private final String HOME_UPDATE = "home";
    private final String RESIDENT_UPDATE = "resident";
    private final String DO_UPDATE = "update";

    public ContactResidentFragment() {
    }

    public static ContactResidentFragment newInstance(Context context, VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        ContactResidentFragment f = new ContactResidentFragment();
        f.mContext = context;
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
    private TextView mDiDong, mCoDinh, mEmail, mHinhThucCuTru, mKyTucXa, mNgayCuTru, mDiaChi, mQueQuan, mHoKhau;
    private ViewGroup mDiaChiCTLayout, mKyTucXaLayout;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private EditText mEtDiDong, mEtCoDinh, mEtEmail, mEtDiaChiCT, mEtDiaChiQQ, mEtDiaChiTR;
    private Spinner mSpHinhThucCT, mSpNgayCT, mSpThangCT, mSpNamCT, mSpKyTucXa;
    private Spinner mSpQuocGiaQQ, mSpTinhThanhQQ, mSpQuanHuyenQQ, mSpPhuongXaQQ;
    private Spinner mSpQuocGiaTR, mSpTinhThanhTR, mSpQuanHuyenTR, mSpPhuongXaTR;
    private Button mBtnSave, mBtnCloseSlideBtm;
    private ImageButton mBtnCloseSlide, mBtnOpenSlide;
    private ProgressBar mPbLoading;

    private ArrayAdapter<String> mHinhThucCTAdt, mNgayCTAdt, mThangCTAdt, mNamCTAdt;
    private ArrayAdapter<QuocGia> mQuocGiaQQAdt, mQuocGiaTRAdt;
    private ArrayAdapter<ThanhPho> mTinhThanhQQAdt, mTinhThanhTRAdt;
    private ArrayAdapter<QuanHuyen> mQuanHuyenQQAdt, mQuanHuyenTRAdt;
    private ArrayAdapter<PhuongXa> mPhuongXaQQAdt, mPhuongXaTRAdt;
    private ArrayAdapter<KYTUCXA> mKyTucXaAdt;

    private List<String> mNgayCTData, mThangCTData, mNamCTData;
    private List<QuocGia> mQuocGiaQQData, mQuocGiaTRData;
    private List<ThanhPho> mTinhThanhQQData, mTinhThanhTRData;
    private List<QuanHuyen> mQuanHuyenQQData, mQuanHuyenTRData;
    private List<PhuongXa> mPhuongXaQQData, mPhuongXaTRData;
    private List<KYTUCXA> mKyTucXaData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsCreated = false;

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
        mKyTucXaData = new ArrayList<>();

        for (int i = 0; i <= 31; i++) {
            mNgayCTData.add(i + "");
            if (i <= 12) mThangCTData.add(i + "");
        }
        mNamCTData.add(0 + "");
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1990; i <= year; i++) {
            mNamCTData.add(i + "");
        }

        mKyTucXaAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mKyTucXaData);
        mKyTucXaAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mHinhThucCTAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, getResources().getStringArray(R.array.resident_type_name));
        mHinhThucCTAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNgayCTAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNgayCTData);
        mNgayCTAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mThangCTAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mThangCTData);
        mThangCTAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNamCTAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNamCTData);
        mNamCTAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mQuocGiaQQAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mQuocGiaQQData);
        mQuocGiaQQAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mTinhThanhQQAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mTinhThanhQQData);
        mTinhThanhQQAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mQuanHuyenQQAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mQuanHuyenQQData);
        mQuanHuyenQQAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mPhuongXaQQAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mPhuongXaQQData);
        mPhuongXaQQAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mQuocGiaTRAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mQuocGiaTRData);
        mQuocGiaTRAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mTinhThanhTRAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mTinhThanhTRData);
        mTinhThanhTRAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mQuanHuyenTRAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mQuanHuyenTRData);
        mQuanHuyenTRAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mPhuongXaTRAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mPhuongXaTRData);
        mPhuongXaTRAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);
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

            new Task().execute(GET_NATIONS, "");
            new Task().execute(GET_DORMITORY, "");
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
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
                setUpSlidePanel(thongTinLienHe, thuongTru, queQuan);
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
        mSpHinhThucCT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 3: // Theo ho khau
                        mEtDiaChiCT.setEnabled(false);
                        mSpKyTucXa.setEnabled(false);
                        break;
                    case 2: // Ngoai tru
                        mEtDiaChiCT.setEnabled(true);
                        mEtDiaChiCT.requestFocus();
                        mSpKyTucXa.setEnabled(false);
                        break;
                    case 1: // Noi tru
                        mEtDiaChiCT.setEnabled(false);
                        mSpKyTucXa.setEnabled(true);
                        break;
                    default:
                        mEtDiaChiCT.setEnabled(false);
                        mSpKyTucXa.setEnabled(false);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                new Task().execute(GET_CITIES_BY_NATION, maQuocGia + "", HOME_UPDATE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpTinhThanhQQ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ThanhPho thanhPho = (ThanhPho) mSpTinhThanhQQ.getSelectedItem();
                new Task().execute(GET_DISTRICT_BY_CITY, thanhPho.MaThanhPho + "", HOME_UPDATE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpQuanHuyenQQ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QuanHuyen quanHuyen = (QuanHuyen) mSpQuanHuyenQQ.getSelectedItem();
                new Task().execute(GET_WARDS_BY_DISTRICT, quanHuyen.MaQuanHuyen + "", HOME_UPDATE);
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
                new Task().execute(GET_CITIES_BY_NATION, maQuocGia + "", RESIDENT_UPDATE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpTinhThanhTR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ThanhPho thanhPho = (ThanhPho) mSpTinhThanhTR.getSelectedItem();
                new Task().execute(GET_DISTRICT_BY_CITY, thanhPho.MaThanhPho + "", RESIDENT_UPDATE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpQuanHuyenTR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QuanHuyen quanHuyen = (QuanHuyen) mSpQuanHuyenTR.getSelectedItem();
                new Task().execute(GET_WARDS_BY_DISTRICT, quanHuyen.MaQuanHuyen + "", RESIDENT_UPDATE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.getConnectivityStatus(mContext) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    Toasty.custom(mContext, getString(R.string.error_network_disconected),
                            getResources().getDrawable(R.drawable.ic_signal_wifi_off_white_24),
                            getResources().getColor(R.color.colorRed),
                            getResources().getColor(android.R.color.white),
                            Toasty.LENGTH_SHORT, true, true)
                            .show();

                } else {
                    VThongTinLienHe thongTinLienHe = new VThongTinLienHe();
                    VThuongTru thuongTru = new VThuongTru();
                    VQueQuan queQuan = new VQueQuan();

                    thongTinLienHe.DiDong = mEtDiDong.getText().toString();
                    thongTinLienHe.DienThoai = mEtCoDinh.getText().toString();
                    thongTinLienHe.Email = mEtEmail.getText().toString();
                    int ngayCT = Integer.parseInt(mSpNgayCT.getSelectedItem().toString());
                    int thangCT = Integer.parseInt(mSpThangCT.getSelectedItem().toString());
                    int namCT = Integer.parseInt(mSpNamCT.getSelectedItem().toString());
                    thongTinLienHe.NgayBatDauCuTru = thangCT + "/" + ngayCT + "/" + namCT;
                    thongTinLienHe.HinhThucCuTru = mSpHinhThucCT.getSelectedItemPosition() + "";
                    thongTinLienHe.MaKyTucXa = ((KYTUCXA)mSpKyTucXa.getSelectedItem()).MaKyTucXa + "";
                    thongTinLienHe.DiaChi = mEtDiaChiCT.getText().toString();

                    thuongTru.MaQuocGia = ((QuocGia) mSpQuocGiaTR.getSelectedItem()).MaQuocGia + "";
                    thuongTru.MaThanhPho = ((ThanhPho) mSpTinhThanhTR.getSelectedItem()).MaThanhPho + "";
                    thuongTru.MaQuanHuyen = ((QuanHuyen) mSpQuanHuyenTR.getSelectedItem()).MaQuanHuyen + "";
                    thuongTru.MaPhuongXa = ((PhuongXa) mSpPhuongXaTR.getSelectedItem()).MaPhuongXa + "";
                    thuongTru.DiaChi = mEtDiaChiTR.getText().toString();

                    queQuan.MaQuocGia = ((QuocGia) mSpQuocGiaQQ.getSelectedItem()).MaQuocGia + "";
                    queQuan.MaThanhPho = ((ThanhPho) mSpTinhThanhQQ.getSelectedItem()).MaThanhPho + "";
                    queQuan.MaQuanHuyen = ((QuanHuyen) mSpQuanHuyenQQ.getSelectedItem()).MaQuanHuyen + "";
                    queQuan.MaPhuongXa = ((PhuongXa) mSpPhuongXaQQ.getSelectedItem()).MaPhuongXa + "";
                    queQuan.DiaChi = mEtDiaChiQQ.getText().toString();

                    attempUpdate(thongTinLienHe, thuongTru, queQuan);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setUpMainPanel(VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        if (thuongTru != null) {
            String temp = "";
            if (!isNullOrEmpty(thuongTru.DiaChi)) temp += thuongTru.DiaChi;
            if (!isNullOrEmpty(thuongTru.TenPhuongXa))
                temp += ", " + thuongTru.TenPhuongXa;
            if (!isNullOrEmpty(thuongTru.TenQuanHuyen))
                temp += ", " + thuongTru.TenQuanHuyen;
            if (!isNullOrEmpty(thuongTru.TenThanhPho))
                temp += ", " + thuongTru.TenThanhPho;
            if (!isNullOrEmpty(thuongTru.TenQuocGia)) temp += ", " + thuongTru.TenQuocGia;
            String diaChiHoKhau = getValueOrEmpty(temp);
            mHoKhau.setText(diaChiHoKhau);
        }
        if (thongTinLienHe != null) {
            mDiDong.setText(getValueOrEmpty(thongTinLienHe.DiDong));
            mCoDinh.setText(getValueOrEmpty(thongTinLienHe.DienThoai));
            mEmail.setText(getValueOrEmpty(thongTinLienHe.Email));
            String ngayCuTru = isNullOrEmpty(thongTinLienHe.NgayBatDauCuTru) ? "..." :
                    DateHelper.formatYMDToDMY(thongTinLienHe.NgayBatDauCuTru.substring(0, 10));
            mNgayCuTru.setText(ngayCuTru);

            if (thongTinLienHe.HinhThucCuTru != null) {
                switch (thongTinLienHe.HinhThucCuTru) {
                    case "3":
                        mHinhThucCuTru.setText("Theo hộ khẩu thường trú");
                        mKyTucXaLayout.setVisibility(View.GONE);
                        mDiaChiCTLayout.setVisibility(View.GONE);
                        break;
                    case "2":
                        mHinhThucCuTru.setText("Ở ngoại trú");
                        mDiaChiCTLayout.setVisibility(View.VISIBLE);
                        mKyTucXaLayout.setVisibility(View.GONE);
                        String diaChi = isNullOrEmpty(thongTinLienHe.DiaChi) ? "..." : thongTinLienHe.DiaChi;
                        mDiaChi.setText(diaChi);
                        break;
                    case "1":
                        mHinhThucCuTru.setText("Ở nội trú");
                        mDiaChiCTLayout.setVisibility(View.GONE);
                        mKyTucXaLayout.setVisibility(View.VISIBLE);
                        mKyTucXa.setText(thongTinLienHe.TenKyTucXa);
                        break;
                    default:
                        mHinhThucCuTru.setText("...");
                        mDiaChiCTLayout.setVisibility(View.GONE);
                        mKyTucXaLayout.setVisibility(View.GONE);
                        break;
                }
            } else {
                mHinhThucCuTru.setText("...");
                mDiaChiCTLayout.setVisibility(View.GONE);
                mKyTucXaLayout.setVisibility(View.GONE);
            }
        }
        if (queQuan != null) {
            String temp = "";
            if (!isNullOrEmpty(queQuan.DiaChi)) temp += queQuan.DiaChi;
            if (!isNullOrEmpty(queQuan.TenPhuongXa)) temp += ", " + queQuan.TenPhuongXa;
            if (!isNullOrEmpty(queQuan.TenQuanHuyen)) temp += ", " + queQuan.TenQuanHuyen;
            if (!isNullOrEmpty(queQuan.TenThanhPho)) temp += ", " + queQuan.TenThanhPho;
            if (!isNullOrEmpty(queQuan.TenQuocGia)) temp += ", " + queQuan.TenQuocGia;
            String queQuanStr = getValueOrEmpty(temp);
            mQueQuan.setText(queQuanStr);
        }
    }

    private void setUpSlidePanel(VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        // Di dong
        String diDong = !isNullOrEmpty(thongTinLienHe.DiDong) ? thongTinLienHe.DiDong : "";
        mEtDiDong.setText("");
        mEtDiDong.append(diDong);
        // Co dinh
        String coDinh = !isNullOrEmpty(thongTinLienHe.DienThoai) ? thongTinLienHe.DienThoai : "";
        mEtCoDinh.setText("");
        mEtCoDinh.append(coDinh);
        // Email
        String email = !isNullOrEmpty(thongTinLienHe.Email) ? thongTinLienHe.Email : "";
        mEtEmail.setText("");
        mEtEmail.append(email);
        // Hinh thuc cu tru
        int indexHinhThucCT = 0;
        if (thongTinLienHe.HinhThucCuTru != null) {
            switch (thongTinLienHe.HinhThucCuTru) {
                case "3": // Theo ho khau thuong tr
                    indexHinhThucCT = 3;
                    break;
                case "2": // O ngoai tru
                    indexHinhThucCT = 2;
                    break;
                case "1": // O noi tru
                    indexHinhThucCT = 1;
                    break;
                default:
                    indexHinhThucCT = 0;
                    break;
            }
        }
        mSpHinhThucCT.setSelection(indexHinhThucCT);
        // Ky tuc xa
        updateSpKyTucXa(mKyTucXaData);
        // Ngay cu tru
        String ngayCT;
        int indexNgay = 0, indexThang = 0, indexNam = 0;
        if (!isNullOrEmpty(thongTinLienHe.NgayBatDauCuTru)) {
            ngayCT = DateHelper.formatYMDToDMY(thongTinLienHe.NgayBatDauCuTru.substring(0, 10));
            Date d = DateHelper.stringToDate(ngayCT, "dd/MM/yyyy");
            indexNgay = DateHelper.getDayOfMonth(d);
            indexThang = DateHelper.getMonth(d);
            indexNam = DateHelper.getYear(d) - 1989;
        }
        mSpNgayCT.setSelection(indexNgay);
        mSpThangCT.setSelection(indexThang);
        mSpNamCT.setSelection(indexNam);
        // Dia chi cu tru
        String diaChiCT = !isNullOrEmpty(thongTinLienHe.DiaChi) ? thongTinLienHe.DiaChi : "";
        mEtDiaChiCT.setText("");
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
        mEtDiaChiQQ.setText("");
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
        mEtDiaChiTR.setText("");
        mEtDiaChiTR.append(diaChiTR);
    }

    private void attempUpdate(VThongTinLienHe thongTinLienHe, VThuongTru thuongTru, VQueQuan queQuan) {
        VLyLichCaNhan lyLichCaNhan = new VLyLichCaNhan();
        lyLichCaNhan.ThongTinLienHe = thongTinLienHe;
        lyLichCaNhan.ThuongTru = thuongTru;
        lyLichCaNhan.QueQuan = queQuan;

        new Task().execute(DO_UPDATE, lyLichCaNhan.toJson());
        mPbLoading.setVisibility(View.VISIBLE);
    }

    @SuppressLint("StaticFieldLeak")
    public class Task extends AsyncTask<String, Void, Boolean> {
        Response mResponse;
        String mJson = "";
        String ORDER = "";
        String HOME_OR_RESIDENT = "";

        @Override
        protected Boolean doInBackground(String... strings) {
            String order = strings[0];

            if (strings.length == 3) HOME_OR_RESIDENT = strings[2];

            switch (order) {
                case GET_NATIONS:
                    String url = Reference.HOST + "api/DungChung/Get/QuocGia/";
                    mResponse = NetworkUtil.makeRequest(url, false, null);
                    ORDER = GET_NATIONS;
                    break;

                case GET_CITIES_BY_NATION:
                    url = Reference.HOST + "api/DungChung/Get/ThanhPho/?refId=" + strings[1];
                    mResponse = NetworkUtil.makeRequest(url, false, null);
                    ORDER = GET_CITIES_BY_NATION;
                    break;

                case GET_DISTRICT_BY_CITY:
                    url = Reference.HOST + "api/DungChung/Get/QuanHuyen/?refId=" + strings[1];
                    mResponse = NetworkUtil.makeRequest(url, false, null);
                    ORDER = GET_DISTRICT_BY_CITY;
                    break;

                case GET_WARDS_BY_DISTRICT:
                    url = Reference.HOST + "api/DungChung/Get/PhuongXa/?refId=" + strings[1];
                    mResponse = NetworkUtil.makeRequest(url, false, null);
                    ORDER = GET_WARDS_BY_DISTRICT;
                    break;

                case GET_DORMITORY:
                    url = Reference.HOST + "api/DungChung/Get/KyTucXa/";
                    mResponse = NetworkUtil.makeRequest(url, false, null);
                    ORDER = GET_DORMITORY;
                    break;

                case DO_UPDATE:
                    url = Reference.HOST + "api/SinhVien/UpdateThongTinLienHe/" +
                            "?masinhvien=" + Reference.getStudentId(mContext) +
                            "&matkhau=" + Reference.getAccountPassword(mContext);
                    String data = strings[1];
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data);
                    mResponse = NetworkUtil.makeRequest(url, true, body);
                    ORDER = DO_UPDATE;
                    break;
            }
            if (mResponse == null)
                return false;
            else {
                try {
                    mJson = mResponse.body() != null ? mResponse.body().string() : "";
                    return true;

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                if (mResponse == null) {
                    Toasty.error(mContext, getString(R.string.error_server_not_response), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mResponse.code() == NetworkUtil.OK) {
                    switch (ORDER) {
                        case GET_NATIONS:
                            List<QuocGia> quocGias = QuocGia.fromJson(mJson);
                            if (quocGias != null) {
                                mQuocGiaQQData.clear();
                                mQuocGiaQQData.add(new QuocGia(0, "----"));
                                mQuocGiaQQData.addAll(quocGias);
                                mQuocGiaQQAdt.notifyDataSetChanged();
                                updateSpQuocGiaQQ(mQuocGiaQQData);

                                mQuocGiaTRData.clear();
                                mQuocGiaTRData.add(new QuocGia(0, "----"));
                                mQuocGiaTRData.addAll(quocGias);
                                mQuocGiaTRAdt.notifyDataSetChanged();
                                updateSpQuocGiaTR(mQuocGiaTRData);
                            }
                            break;

                        case GET_CITIES_BY_NATION:
                            List<ThanhPho> thanhPhos = ThanhPho.fromJson(mJson);
                            if (thanhPhos != null) {
                                if (!isNullOrEmpty(HOME_OR_RESIDENT)) {
                                    if (HOME_OR_RESIDENT.equals(HOME_UPDATE)) {
                                        mTinhThanhQQData.clear();
                                        mTinhThanhQQData.add(new ThanhPho(0, "----"));
                                        mTinhThanhQQAdt.addAll(thanhPhos);
                                        mTinhThanhQQAdt.notifyDataSetChanged();
                                        updateSpTinhThanhQQ(mTinhThanhQQData);
                                    } else {
                                        mTinhThanhTRData.clear();
                                        mTinhThanhTRData.add(new ThanhPho(0, "----"));
                                        mTinhThanhTRAdt.addAll(thanhPhos);
                                        mTinhThanhTRAdt.notifyDataSetChanged();
                                        updateSpTinhThanhTR(mTinhThanhTRData);
                                    }
                                }
                            }
                            break;

                        case GET_DISTRICT_BY_CITY:
                            List<QuanHuyen> quanHuyens = QuanHuyen.fromJson(mJson);
                            if (quanHuyens != null) {
                                if (!isNullOrEmpty(HOME_OR_RESIDENT)) {
                                    if (HOME_OR_RESIDENT.equals(HOME_UPDATE)) {
                                        mQuanHuyenQQData.clear();
                                        mQuanHuyenQQData.add(new QuanHuyen(0, "----"));
                                        mQuanHuyenQQData.addAll(quanHuyens);
                                        mQuanHuyenQQAdt.notifyDataSetChanged();
                                        updateSpQuanHuyenQQ(mQuanHuyenQQData);
                                    } else if (HOME_OR_RESIDENT.equals(RESIDENT_UPDATE)) {
                                        mQuanHuyenTRData.clear();
                                        mQuanHuyenTRData.add(new QuanHuyen(0, "----"));
                                        mQuanHuyenTRData.addAll(quanHuyens);
                                        mQuanHuyenTRAdt.notifyDataSetChanged();
                                        updateSpQuanHuyenTR(mQuanHuyenTRData);
                                    }
                                }
                            }
                            break;

                        case GET_WARDS_BY_DISTRICT:
                            List<PhuongXa> phuongXas = PhuongXa.fromJson(mJson);
                            if (phuongXas != null) {
                                if (!isNullOrEmpty(HOME_OR_RESIDENT)) {
                                    if (HOME_OR_RESIDENT.equals(HOME_UPDATE)) {
                                        mPhuongXaQQData.clear();
                                        mPhuongXaQQData.add(new PhuongXa(0, "----"));
                                        mPhuongXaQQData.addAll(phuongXas);
                                        mPhuongXaQQAdt.notifyDataSetChanged();
                                        updateSpPhuongXaQQ(mPhuongXaQQData);
                                    } else if (HOME_OR_RESIDENT.equals(RESIDENT_UPDATE)) {
                                        mPhuongXaTRData.clear();
                                        mPhuongXaTRData.add(new PhuongXa(0, "----"));
                                        mPhuongXaTRData.addAll(phuongXas);
                                        mPhuongXaTRAdt.notifyDataSetChanged();
                                        updateSpPhuongXaTR(mPhuongXaTRData);
                                    }
                                }
                            }
                            break;

                        case GET_DORMITORY:
                            List<KYTUCXA> kytucxas = KYTUCXA.fromJson(mJson);
                            if (kytucxas != null) {
                                mKyTucXaData.clear();
                                mKyTucXaData.add(new KYTUCXA(0, "----"));
                                mKyTucXaData.addAll(kytucxas);
                                mKyTucXaAdt.notifyDataSetChanged();
                                updateSpKyTucXa(mKyTucXaData);
                            }
                            break;

                        case DO_UPDATE:
                            mPbLoading.setVisibility(View.GONE);
                            VLyLichCaNhan lyLichCaNhan = VLyLichCaNhan.fromJson(mJson);

                            if (lyLichCaNhan != null) {
                                thongTinLienHe = lyLichCaNhan.getThongTinLienHe();
                                thuongTru = lyLichCaNhan.getThuongTru();
                                queQuan = lyLichCaNhan.getQueQuan();
                                setUpSlidePanel(thongTinLienHe, thuongTru, queQuan);
                                setUpMainPanel(thongTinLienHe, thuongTru, queQuan);
                                Toasty.success(mContext, "Đã cập nhật !", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }

                } else {
                    Log.e("DEBUG", mJson);
                }
            }
        }
    }

    /*##### UPDATE DATA ON VIEW #####*/

    private void bindUI(@NonNull View view) {
        mCoDinh = view.findViewById(R.id.tv_coDinh);
        mDiDong = view.findViewById(R.id.tv_diDong);
        mEmail = view.findViewById(R.id.tv_email);
        mHinhThucCuTru = view.findViewById(R.id.tv_hinhThucCuTru);
        mKyTucXa = view.findViewById(R.id.tv_kyTucXa);
        mNgayCuTru = view.findViewById(R.id.tv_ngayBatDauCuTru);
        mDiaChi = view.findViewById(R.id.tv_diaChiCuTru);
        mQueQuan = view.findViewById(R.id.tv_queQuan);
        mHoKhau = view.findViewById(R.id.tv_hoKhauThuongTru);
        mDiaChiCTLayout = view.findViewById(R.id.layout_diaChiCuTru);
        mKyTucXaLayout = view.findViewById(R.id.layout_kyTucXa);

        mSlidingUpPanelLayout = view.findViewById(R.id.sliding_layout);
        mEtDiDong = view.findViewById(R.id.et_diDong);
        mEtCoDinh = view.findViewById(R.id.et_coDinh);
        mEtEmail = view.findViewById(R.id.et_email);
        mSpHinhThucCT = view.findViewById(R.id.sp_hinhThucCuTru);
        mSpKyTucXa = view.findViewById(R.id.sp_kyTucXa);
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

        mSpHinhThucCT.setAdapter(mHinhThucCTAdt);
        mSpNgayCT.setAdapter(mNgayCTAdt);
        mSpThangCT.setAdapter(mThangCTAdt);
        mSpNamCT.setAdapter(mNamCTAdt);
        mSpQuocGiaQQ.setAdapter(mQuocGiaQQAdt);
        mSpTinhThanhQQ.setAdapter(mTinhThanhQQAdt);
        mSpQuanHuyenQQ.setAdapter(mQuanHuyenQQAdt);
        mSpPhuongXaQQ.setAdapter(mPhuongXaQQAdt);
        mSpQuocGiaTR.setAdapter(mQuocGiaTRAdt);
        mSpTinhThanhTR.setAdapter(mTinhThanhTRAdt);
        mSpQuanHuyenTR.setAdapter(mQuanHuyenTRAdt);
        mSpPhuongXaTR.setAdapter(mPhuongXaTRAdt);
        mSpKyTucXa.setAdapter(mKyTucXaAdt);
    }

    private void updateSpQuocGiaQQ(List<QuocGia> quocGias) {
        if (quocGias == null) return;
        int index = 0;

        if (quocGias.size() > 0 && queQuan != null && queQuan.MaQuocGia != null) {
            int maHienTai = Integer.parseInt(queQuan.MaQuocGia);
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

        if (thanhPhos.size() > 0 && queQuan != null && queQuan.MaThanhPho != null) {
            int maHienTai = Integer.parseInt(queQuan.MaThanhPho);
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

        if (quanHuyens.size() > 0 && queQuan != null && queQuan.MaQuanHuyen != null) {
            int maHienTai = Integer.parseInt(queQuan.MaQuanHuyen);
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

        if (phuongXas.size() > 0 && queQuan != null && queQuan.MaPhuongXa != null) {
            int maHienTai = Integer.parseInt(queQuan.MaPhuongXa);
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

        if (quocGias.size() > 0 && thuongTru != null && thuongTru.MaQuocGia != null) {
            int maHienTai = Integer.parseInt(thuongTru.MaQuocGia);
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

        if (thanhPhos.size() > 0 && thuongTru != null && thuongTru.MaThanhPho != null) {
            int maHienTai = Integer.parseInt(thuongTru.MaThanhPho);
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

        if (quanHuyens.size() > 0 && thuongTru != null && thuongTru.MaQuanHuyen != null) {
            int maHienTai = Integer.parseInt(thuongTru.MaQuanHuyen);
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

        if (phuongXas.size() > 0 && thuongTru != null && thuongTru.MaPhuongXa != null) {
            int maHienTai = Integer.parseInt(thuongTru.MaPhuongXa);
            for (int i = 0; i < phuongXas.size(); i++)
                if (phuongXas.get(i).MaPhuongXa == maHienTai) {
                    index = i;
                    break;
                }
        }
        mSpPhuongXaTR.setSelection(index);
    }

    private void updateSpKyTucXa(List<KYTUCXA> kytucxas) {
        if (kytucxas == null) return;
        int index = 0;

        if (kytucxas.size() > 0 && thongTinLienHe.MaKyTucXa != null) {
            String maHienTai = thongTinLienHe.MaKyTucXa;
            for (int i = 0; i < kytucxas.size(); i++)
                if (kytucxas.get(i).MaKyTucXa == Integer.parseInt(maHienTai)) {
                    index = i;
                    break;
                }
        }
        mSpKyTucXa.setSelection(index);
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
