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
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.VLichSuBanThan;
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

public class HistoryFragment extends Fragment {

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(Context context, VLichSuBanThan lichSuBanThan) {
        HistoryFragment f = new HistoryFragment();
        f.mLichSuBanThan = lichSuBanThan;
        f.mContext = context;
        return f;
    }

    private VLichSuBanThan mLichSuBanThan;
    private Context mContext;

    public void setThongTin(VLichSuBanThan lichSuBanThan) {
        this.mLichSuBanThan = lichSuBanThan;
    }

    private TextView mNamTotNghiepTHPT, mXepLoaiTotNghiepTHPT, mNoiTotNghiepTHPT, mXepLoaiHocTapLop10,
            mXepLoaiHocTapLop11, mXepLoaiHocTapLop12, mXepLoaiHanhKiemLop10, mXepLoaiHanhKiemLop11,
            mXepLoaiHanhKiemLop12, mTenLVT, mNgayBatDauLLVT, mNgayKetThucLLVT, mQuanHamLLVT, mNoiCongTacLLVT;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private Spinner mSpNamTotNghiepTHPT, mSpLoaiTotNghiepTHPT,
            mSpXepLoaiHocTap10, mSpXepLoaiHocTap11, mSpXepLoaiHocTap12,
            mSpXepLoaiHanhKiem10, mSpXepLoaiHanhKiem11, mSpXepLoaiHanhKiem12,
            mSpNgayNhapNgu, mSpThangNhapNgu, mSpNamNhapNgu, mSpNgayXuatNgu, mSpThangXuatNgu, mSpNamXuatNgu;
    private EditText mEtNoiTotNghiepTHPT, mEtLucLuongVuTrang, mEtQuanHamCaoNhat, mEtNoiCongTac;
    private Button mBtnSave, mBtnCloseSlideBtm;
    private ImageButton mBtnCloseSlide, mBtnOpenSlide;
    private ProgressBar mPbLoading;

    private ArrayAdapter<String> mNamTotNghiepTHPTAdt, mLoaiTotNghiepTHPTAdt,
            mXepLoaiHocTap10Adt, mXepLoaiHocTap11Adt, mXepLoaiHocTap12Adt,
            mXepLoaiHanhKiem10Adt, mXepLoaiHanhKiem11Adt, mXepLoaiHanhKiem12Adt,
            mNgayNhapNguAdt, mThangNhapNguAdt, mNamNhapNguAdt, mNgayXuatNguAdt, mThangXuatNguAdt, mNamXuatNguAdt;
    private List<String> mNgayNhapNguData;
    private List<String> mNgayXuatNguData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> mNamTotNghiepTHPTData = new ArrayList<>();
        mNgayNhapNguData = new ArrayList<>();
        List<String> mThangNhapNguData = new ArrayList<>();
        List<String> mNamNhapNguData = new ArrayList<>();
        mNgayXuatNguData = new ArrayList<>();
        List<String> mThangXuatNguData = new ArrayList<>();
        List<String> mNamXuatNguData = new ArrayList<>();

        for (int i = 0; i <= 31; i++) {
            mNgayNhapNguData.add(i + "");
            mNgayXuatNguData.add(i + "");
            if (i <= 12) {
                mThangNhapNguData.add(i + "");
                mThangXuatNguData.add(i + "");
            }
        }
        mNamNhapNguData.add(0 + "");
        mNamXuatNguData.add(0 + "");
        mNamTotNghiepTHPTData.add(0 + "");
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1990; i <= year; i++) {
            mNamNhapNguData.add(i + "");
            mNamXuatNguData.add(i + "");
            mNamTotNghiepTHPTData.add(i + "");
        }
        mNamTotNghiepTHPTAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNamTotNghiepTHPTData);
        mNamTotNghiepTHPTAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mLoaiTotNghiepTHPTAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.learning_ranking));
        mLoaiTotNghiepTHPTAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mXepLoaiHocTap10Adt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.learning_ranking));
        mXepLoaiHocTap10Adt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mXepLoaiHocTap11Adt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.learning_ranking));
        mXepLoaiHocTap11Adt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mXepLoaiHocTap12Adt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.learning_ranking));
        mXepLoaiHocTap12Adt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mXepLoaiHanhKiem10Adt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.practice_ranking));
        mXepLoaiHanhKiem10Adt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mXepLoaiHanhKiem11Adt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.practice_ranking));
        mXepLoaiHanhKiem11Adt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mXepLoaiHanhKiem12Adt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.practice_ranking));
        mXepLoaiHanhKiem12Adt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNgayNhapNguAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNgayNhapNguData);
        mNgayNhapNguAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mThangNhapNguAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mThangNhapNguData);
        mThangNhapNguAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNamNhapNguAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNamNhapNguData);
        mNamNhapNguAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNgayXuatNguAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNgayXuatNguData);
        mNgayXuatNguAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mThangXuatNguAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mThangXuatNguData);
        mThangXuatNguAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNamXuatNguAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNamXuatNguData);
        mNamXuatNguAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_self_history, container, false);
        bindUI(view);
        setUpEvent();
        setUpMainPanel(mLichSuBanThan);

        return view;
    }

    private void attempUpdate() {
        VLichSuBanThan lichSuBanThan = new VLichSuBanThan();
        lichSuBanThan.setMaSinhVien(Reference.getInstance().getStudentId(mContext));
        lichSuBanThan.setNamTotNghiepTHPT(mSpNamTotNghiepTHPT.getSelectedItem().toString());
        lichSuBanThan.setXepLoaiTotNghiepTHPT(mSpLoaiTotNghiepTHPT.getSelectedItemPosition() + "");
        lichSuBanThan.setNoiTotNghiepTHPT(mEtNoiTotNghiepTHPT.getText().toString());
        lichSuBanThan.setXepLoaiHocTap10(mSpXepLoaiHocTap10.getSelectedItemPosition() + "");
        lichSuBanThan.setXepLoaiHocTap11(mSpXepLoaiHocTap11.getSelectedItemPosition() + "");
        lichSuBanThan.setXepLoaiHocTap12(mSpXepLoaiHocTap12.getSelectedItemPosition() + "");
        lichSuBanThan.setXepLoaiHanhKiem10(mSpXepLoaiHanhKiem10.getSelectedItemPosition() + "");
        lichSuBanThan.setXepLoaiHanhKiem11(mSpXepLoaiHanhKiem11.getSelectedItemPosition() + "");
        lichSuBanThan.setXepLoaiHanhKiem12(mSpXepLoaiHanhKiem12.getSelectedItemPosition() + "");
        lichSuBanThan.setNoiCongTacLLVT(mEtNoiCongTac.getText().toString());
        lichSuBanThan.setQuanHamLLVT(mEtQuanHamCaoNhat.getText().toString());
        lichSuBanThan.setTenLucLuongVuTrang(mEtLucLuongVuTrang.getText().toString());
        int ngay = Integer.parseInt(mSpNgayNhapNgu.getSelectedItem().toString());
        int thang = Integer.parseInt(mSpThangNhapNgu.getSelectedItem().toString());
        int nam = Integer.parseInt(mSpNamNhapNgu.getSelectedItem().toString());
        lichSuBanThan.setNgayBatDauLLVT(thang + "/" + ngay + "/" + nam);
        ngay = Integer.parseInt(mSpNgayXuatNgu.getSelectedItem().toString());
        thang = Integer.parseInt(mSpThangXuatNgu.getSelectedItem().toString());
        nam = Integer.parseInt(mSpNamXuatNgu.getSelectedItem().toString());
        lichSuBanThan.setNgayKetThucLLVT(thang + "/" + ngay + "/" + nam);

        new UpdateTask().execute(VLichSuBanThan.toJson(lichSuBanThan));
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateTask extends AsyncTask<String, Void, Boolean> {
        Response mResponse;
        String mJson = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String data = strings[0];
            String url = Reference.getInstance().getHost(mContext) +
                    "api/SinhVien/UpdateLichSuBanThan/" +
                    "?masinhvien=" + Reference.getInstance().getStudentId(mContext) +
                    "&matkhau=" + Reference.getInstance().getAccountPassword(mContext);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data);
            mResponse = NetworkUtil.makeRequest(url, true, body);

            if (mResponse == null) {
                return false;

            } else {
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
            mPbLoading.setVisibility(View.GONE);

            if (aBoolean) {
                if (mResponse.code() == NetworkUtil.OK) {
                    VLichSuBanThan lichSuBanThan = VLichSuBanThan.fromJson(mJson);

                    if (lichSuBanThan != null) {
                        mLichSuBanThan = lichSuBanThan;
                        setUpMainPanel(lichSuBanThan);
                        setUpSlidePanel(lichSuBanThan);
                        Toasty.success(mContext, "Đã cập nhật !", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (mResponse == null) {
                    Toasty.error(mContext, getString(R.string.error_server_not_response), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUpMainPanel(VLichSuBanThan lichSuBanThan) {
        mNamTotNghiepTHPT.setText(getValueOrEmpty(lichSuBanThan.getNamTotNghiepTHPT()));
        String[] learning_ranking = getResources().getStringArray(R.array.learning_ranking);
        String[] practice_ranking = getResources().getStringArray(R.array.practice_ranking);
        int selectedIndex = isNullOrEmpty(lichSuBanThan.getXepLoaiTotNghiepTHPT()) ?
                0 : Integer.parseInt(lichSuBanThan.getXepLoaiTotNghiepTHPT());
        mXepLoaiTotNghiepTHPT.setText(learning_ranking[selectedIndex]);
        mNoiTotNghiepTHPT.setText(getValueOrEmpty(lichSuBanThan.getNoiTotNghiepTHPT()));
        selectedIndex = isNullOrEmpty(lichSuBanThan.getXepLoaiHocTap10()) ?
                0 : Integer.parseInt(lichSuBanThan.getXepLoaiHocTap10());
        mXepLoaiHocTapLop10.setText(learning_ranking[selectedIndex]);
        selectedIndex = isNullOrEmpty(lichSuBanThan.getXepLoaiHocTap11()) ?
                0 : Integer.parseInt(lichSuBanThan.getXepLoaiHocTap11());
        mXepLoaiHocTapLop11.setText(learning_ranking[selectedIndex]);
        selectedIndex = isNullOrEmpty(lichSuBanThan.getXepLoaiHocTap12()) ?
                0 : Integer.parseInt(lichSuBanThan.getXepLoaiHocTap12());
        mXepLoaiHocTapLop12.setText(learning_ranking[selectedIndex]);
        selectedIndex = isNullOrEmpty(lichSuBanThan.getXepLoaiHanhKiem10()) ?
                0 : Integer.parseInt(lichSuBanThan.getXepLoaiHanhKiem10());
        mXepLoaiHanhKiemLop10.setText(practice_ranking[selectedIndex]);
        selectedIndex = isNullOrEmpty(lichSuBanThan.getXepLoaiHanhKiem11()) ?
                0 : Integer.parseInt(lichSuBanThan.getXepLoaiHanhKiem11());
        mXepLoaiHanhKiemLop11.setText(practice_ranking[selectedIndex]);
        selectedIndex = isNullOrEmpty(lichSuBanThan.getXepLoaiHanhKiem12()) ?
                0 : Integer.parseInt(lichSuBanThan.getXepLoaiHanhKiem12());
        mXepLoaiHanhKiemLop12.setText(practice_ranking[selectedIndex]);

        String ngayBdLLVT = isNullOrEmpty(lichSuBanThan.getNgayBatDauLLVT()) ? "..." :
                DateHelper.formatYMDToDMY(lichSuBanThan.getNgayBatDauLLVT().substring(0, 10));
        mNgayBatDauLLVT.setText(ngayBdLLVT);

        String ngayKtLLVT = isNullOrEmpty(lichSuBanThan.getNgayKetThucLLVT()) ? "..." :
                DateHelper.formatYMDToDMY(lichSuBanThan.getNgayKetThucLLVT().substring(0, 10));
        mNgayKetThucLLVT.setText(ngayKtLLVT);
        mTenLVT.setText(getValueOrEmpty(lichSuBanThan.getTenLucLuongVuTrang()));
        mQuanHamLLVT.setText(getValueOrEmpty(lichSuBanThan.getQuanHamLLVT()));
        mNoiCongTacLLVT.setText(getValueOrEmpty(lichSuBanThan.getNoiCongTacLLVT()));
    }

    private void setUpSlidePanel(VLichSuBanThan lichSuBanThan) {
        int indexNgay = 0, indexThang = 0, indexNam;
        // Nam tot nghiep THPT
        indexNam = isNullOrEmpty(lichSuBanThan.getNamTotNghiepTHPT()) ? 0 : (Integer.parseInt(lichSuBanThan.getNamTotNghiepTHPT()) - 1989);
        mSpNamTotNghiepTHPT.setSelection(indexNam);
        // Loai tot nghiep THPT
        if (isNullOrEmpty(lichSuBanThan.getXepLoaiTotNghiepTHPT()))
            mSpLoaiTotNghiepTHPT.setSelection(0);
        else
            mSpLoaiTotNghiepTHPT.setSelection(Integer.parseInt(lichSuBanThan.getXepLoaiTotNghiepTHPT()));
        // Xep loai hoc tap lop 10
        if (isNullOrEmpty(lichSuBanThan.getXepLoaiHocTap10()))
            mSpXepLoaiHocTap10.setSelection(0);
        else
            mSpXepLoaiHocTap10.setSelection(Integer.parseInt(lichSuBanThan.getXepLoaiHocTap10()));
        // Xep loai hoc tap lop 11
        if (isNullOrEmpty(lichSuBanThan.getXepLoaiHocTap11()))
            mSpXepLoaiHocTap11.setSelection(0);
        else
            mSpXepLoaiHocTap11.setSelection(Integer.parseInt(lichSuBanThan.getXepLoaiHocTap11()));
        // Xep loai hoc tap lop 12
        if (isNullOrEmpty(lichSuBanThan.getXepLoaiHocTap12()))
            mSpXepLoaiHocTap12.setSelection(0);
        else
            mSpXepLoaiHocTap12.setSelection(Integer.parseInt(lichSuBanThan.getXepLoaiHocTap12()));
        // Xep loai hanh kiem lop 10
        if (isNullOrEmpty(lichSuBanThan.getXepLoaiHanhKiem10()))
            mSpXepLoaiHanhKiem10.setSelection(0);
        else
            mSpXepLoaiHanhKiem10.setSelection(Integer.parseInt(lichSuBanThan.getXepLoaiHanhKiem10()));
        // Xep loai hanh kiem lop 11
        if (isNullOrEmpty(lichSuBanThan.getXepLoaiHanhKiem11()))
            mSpXepLoaiHanhKiem11.setSelection(0);
        else
            mSpXepLoaiHanhKiem11.setSelection(Integer.parseInt(lichSuBanThan.getXepLoaiHanhKiem11()));
        // Xep loai hanh kiem lop 12
        if (isNullOrEmpty(lichSuBanThan.getXepLoaiHanhKiem12()))
            mSpXepLoaiHanhKiem12.setSelection(0);
        else
            mSpXepLoaiHanhKiem12.setSelection(Integer.parseInt(lichSuBanThan.getXepLoaiHanhKiem12()));
        // Noi tot nghiep THPT
        String noiTotNghiepTHPT = isNullOrEmpty(lichSuBanThan.getNoiTotNghiepTHPT()) ? "" : lichSuBanThan.getNoiTotNghiepTHPT();
        mEtNoiTotNghiepTHPT.setText("");
        mEtNoiTotNghiepTHPT.append(noiTotNghiepTHPT);
        // Luc luong vu trang
        String lucLuongVuTrang = isNullOrEmpty(lichSuBanThan.getTenLucLuongVuTrang()) ? "" : lichSuBanThan.getTenLucLuongVuTrang();
        mEtLucLuongVuTrang.setText("");
        mEtLucLuongVuTrang.append(lucLuongVuTrang);
        // Quan ham cao nhat
        String quanHamCaoNhat = isNullOrEmpty(lichSuBanThan.getQuanHamLLVT()) ? "" : lichSuBanThan.getQuanHamLLVT();
        mEtQuanHamCaoNhat.setText("");
        mEtQuanHamCaoNhat.append(quanHamCaoNhat);
        // Noi cong tac luc luong vu trang
        String noiCongTac = isNullOrEmpty(lichSuBanThan.getNoiCongTacLLVT()) ? "" : lichSuBanThan.getNoiCongTacLLVT();
        mEtNoiCongTac.setText("");
        mEtNoiCongTac.append(noiCongTac);
        // Ngay nhap ngu
        String ngayNhapNgu;
        indexNam = 0;
        if (!isNullOrEmpty(lichSuBanThan.getNgayBatDauLLVT())) {
            ngayNhapNgu = DateHelper.formatYMDToDMY(lichSuBanThan.getNgayBatDauLLVT().substring(0, 10));
            Date d = DateHelper.stringToDate(ngayNhapNgu, "dd/MM/yyyy");
            indexNgay = DateHelper.getDayOfMonth(d);
            indexThang = DateHelper.getMonth(d);
            indexNam = DateHelper.getYear(d) - 1989;
        }
        mSpNgayNhapNgu.setSelection(indexNgay);
        mSpThangNhapNgu.setSelection(indexThang);
        mSpNamNhapNgu.setSelection(indexNam);
        // Ngay xuat ngu
        String ngayXuatNgu;
        indexNgay = 0;
        indexThang = 0;
        indexNam = 0;
        if (!isNullOrEmpty(lichSuBanThan.getNgayKetThucLLVT())) {
            ngayXuatNgu = DateHelper.formatYMDToDMY(lichSuBanThan.getNgayKetThucLLVT().substring(0, 10));
            Date d = DateHelper.stringToDate(ngayXuatNgu, "dd/MM/yyyy");
            indexNgay = DateHelper.getDayOfMonth(d);
            indexThang = DateHelper.getMonth(d);
            indexNam = DateHelper.getYear(d) - 1989;
        }
        mSpNgayXuatNgu.setSelection(indexNgay);
        mSpThangXuatNgu.setSelection(indexThang);
        mSpNamXuatNgu.setSelection(indexNam);
    }

    private void setUpEvent() {
        mBtnOpenSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                mSlidingUpPanelLayout.setTouchEnabled(false);
                setUpSlidePanel(mLichSuBanThan);
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
                    attempUpdate();
                }
            }
        });
        mSpThangNhapNgu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayNhapNguData, mNgayNhapNguAdt, position, mSpNamNhapNgu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpThangNhapNgu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayNhapNguData, mNgayNhapNguAdt, mSpThangNhapNgu.getSelectedItemPosition(), mSpNamNhapNgu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpThangXuatNgu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayXuatNguData, mNgayXuatNguAdt, position, mSpNamXuatNgu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpNamXuatNgu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayXuatNguData, mNgayXuatNguAdt, mSpThangXuatNgu.getSelectedItemPosition(), mSpNamXuatNgu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindUI(View view) {
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

        mSlidingUpPanelLayout = view.findViewById(R.id.sliding_layout);
        mSpNamTotNghiepTHPT = view.findViewById(R.id.sp_namTotNghiepTHPT);
        mSpLoaiTotNghiepTHPT = view.findViewById(R.id.sp_loaiTotNghiepTHPT);
        mSpXepLoaiHocTap10 = view.findViewById(R.id.sp_xepLoaiHocTap10);
        mSpXepLoaiHocTap11 = view.findViewById(R.id.sp_xepLoaiHocTap11);
        mSpXepLoaiHocTap12 = view.findViewById(R.id.sp_xepLoaiHocTap12);
        mSpXepLoaiHanhKiem10 = view.findViewById(R.id.sp_xepLoaiHanhKiem10);
        mSpXepLoaiHanhKiem11 = view.findViewById(R.id.sp_xepLoaiHanhKiem11);
        mSpXepLoaiHanhKiem12 = view.findViewById(R.id.sp_xepLoaiHanhKiem12);
        mSpNgayNhapNgu = view.findViewById(R.id.sp_ngayNhapNgu);
        mSpThangNhapNgu = view.findViewById(R.id.sp_thangNhapNgu);
        mSpNamNhapNgu = view.findViewById(R.id.sp_namNhapNgu);
        mSpNgayXuatNgu = view.findViewById(R.id.sp_ngayXuatNgu);
        mSpThangXuatNgu = view.findViewById(R.id.sp_thangXuatNgu);
        mSpNamXuatNgu = view.findViewById(R.id.sp_namXuatNgu);
        mEtNoiTotNghiepTHPT = view.findViewById(R.id.et_noiTotNghiepTHPT);
        mEtLucLuongVuTrang = view.findViewById(R.id.et_lucLuongVuTrang);
        mEtQuanHamCaoNhat = view.findViewById(R.id.et_quanHamCaoNhat);
        mEtNoiCongTac = view.findViewById(R.id.et_noiCongTac);

        mBtnOpenSlide = view.findViewById(R.id.btn_suaLichSuBanThan);
        mBtnCloseSlide = view.findViewById(R.id.btn_closeSlidePanel);
        mBtnCloseSlideBtm = view.findViewById(R.id.btn_closeSlidePanelBtm);
        mBtnSave = view.findViewById(R.id.btn_save);
        mPbLoading = view.findViewById(R.id.pb_updateLoading);

        mSpNamTotNghiepTHPT.setAdapter(mNamTotNghiepTHPTAdt);
        mSpLoaiTotNghiepTHPT.setAdapter(mLoaiTotNghiepTHPTAdt);
        mSpXepLoaiHocTap10.setAdapter(mXepLoaiHocTap10Adt);
        mSpXepLoaiHocTap11.setAdapter(mXepLoaiHocTap11Adt);
        mSpXepLoaiHocTap12.setAdapter(mXepLoaiHocTap12Adt);
        mSpXepLoaiHanhKiem10.setAdapter(mXepLoaiHanhKiem10Adt);
        mSpXepLoaiHanhKiem11.setAdapter(mXepLoaiHanhKiem11Adt);
        mSpXepLoaiHanhKiem12.setAdapter(mXepLoaiHanhKiem12Adt);
        mSpNgayNhapNgu.setAdapter(mNgayNhapNguAdt);
        mSpThangNhapNgu.setAdapter(mThangNhapNguAdt);
        mSpNamNhapNgu.setAdapter(mNamNhapNguAdt);
        mSpNgayXuatNgu.setAdapter(mNgayXuatNguAdt);
        mSpThangXuatNgu.setAdapter(mThangXuatNguAdt);
        mSpNamXuatNgu.setAdapter(mNamXuatNguAdt);
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
