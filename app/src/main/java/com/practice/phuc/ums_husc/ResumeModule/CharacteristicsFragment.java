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
import com.practice.phuc.ums_husc.ViewModel.VDacDiemBanThan;
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

public class CharacteristicsFragment extends Fragment {

    public CharacteristicsFragment() {
        // Required empty public constructor
    }

    public static CharacteristicsFragment newInstance(Context context, VDacDiemBanThan dacDiemBanThan) {
        CharacteristicsFragment f = new CharacteristicsFragment();
        f.mDacDiemBanThan = dacDiemBanThan;
        f.mContext = context;
        return f;
    }

    public void setThongTin(VDacDiemBanThan dacDiemBanThan) {
        this.mDacDiemBanThan = dacDiemBanThan;
    }

    private VDacDiemBanThan mDacDiemBanThan;
    private Context mContext;

    private TextView mXuatThan, mUuTienBanThan, mUuTienGiaDinh, mTinhTrangHonNhan, mChieuCao,
            mNhomMau, mNgayVaoDoan, mNoiKetNapDoan, mNgayVaoDang, mNoiKetNapDang, mNgayChinhThucVaoDang;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private EditText mEtNoiKetNapDoan, mEtNoiKetNapDang, mEtChieuCao;
    private Spinner mSpXuatThan, mSpUuTienBanThan, mSpUuTienGiaDinh, mSpTinhTrangHonNhan,
            mSpNhomMau, mSpNgayVaoDoan, mSpThangVaoDoan, mSpNamVaoDoan,
            mSpNgayVaoDang, mSpThangVaoDang, mSpNamVaoDang, mSpNgayChinhThuc, mSpThangChinhThuc, mSpNamChinhThuc;
    private Button mBtnSave, mBtnCloseSlideBtm;
    private ImageButton mBtnCloseSlide, mBtnOpenSlide;
    private ProgressBar mPbLoading;

    private ArrayAdapter<String> mXuatThanAdt, mTinhTrangHonNhanAdt, mUuTienBanThanAdt, mUuTienGiaDinhAdt, mNhomMauAdt,
            mNgayVaoDoanAdt, mThangVaoDoanAdt, mNamVaoDoanAdt,
            mNgayVaoDangAdt, mThangVaoDangAdt, mNamVaoDangAdt,
            mNgayChinhThucAdt, mThangChinhThucAdt, mNamChinhThucAdt;

    private List<String> mNgayVaoDoanData;
    private List<String> mNgayVaoDangData;
    private List<String> mNgayChinhThucData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNgayVaoDoanData = new ArrayList<>();
        List<String> mThangVaoDoanData = new ArrayList<>();
        List<String> mNamVaoDoanData = new ArrayList<>();
        mNgayVaoDangData = new ArrayList<>();
        List<String> mThangVaoDangData = new ArrayList<>();
        List<String> mNamVaoDangData = new ArrayList<>();
        mNgayChinhThucData = new ArrayList<>();
        List<String> mThangChinhThucData = new ArrayList<>();
        List<String> mNamChinhThucData = new ArrayList<>();

        for (int i = 0; i <= 31; i++) {
            mNgayVaoDoanData.add(i + "");
            mNgayVaoDangData.add(i + "");
            mNgayChinhThucData.add(i + "");
            if (i <= 12) {
                mThangVaoDoanData.add(i + "");
                mThangVaoDangData.add(i + "");
                mThangChinhThucData.add(i + "");
            }
        }
        mNamVaoDoanData.add(0 + "");
        mNamVaoDangData.add(0 + "");
        mNamChinhThucData.add(0 + "");
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1990; i <= year; i++) {
            mNamVaoDoanData.add(i + "");
            mNamVaoDangData.add(i + "");
            mNamChinhThucData.add(i + "");
        }

        mNgayVaoDoanAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNgayVaoDoanData);
        mNgayVaoDoanAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mThangVaoDoanAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mThangVaoDoanData);
        mThangVaoDoanAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNamVaoDoanAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNamVaoDoanData);
        mNamVaoDoanAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNgayVaoDangAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNgayVaoDangData);
        mNgayVaoDangAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mThangVaoDangAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mThangVaoDangData);
        mThangVaoDangAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNamVaoDangAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNamVaoDangData);
        mNamVaoDangAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNgayChinhThucAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNgayChinhThucData);
        mNgayChinhThucAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mThangChinhThucAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mThangChinhThucData);
        mThangChinhThucAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNamChinhThucAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNamChinhThucData);
        mNamChinhThucAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mXuatThanAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.composition_of_origin));
        mXuatThanAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mTinhTrangHonNhanAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.marital_status));
        mTinhTrangHonNhanAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mUuTienBanThanAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.priority_for_yourself));
        mUuTienBanThanAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mUuTienGiaDinhAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.family_priority));
        mUuTienGiaDinhAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNhomMauAdt = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item,
                getResources().getStringArray(R.array.blood_group));
        mNhomMauAdt.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_characteristics, container, false);
        bindUI(view);
        setUpEvent();

        setUpMainPanel(mDacDiemBanThan);

        return view;
    }

    private void setUpEvent() {
        mBtnOpenSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                mSlidingUpPanelLayout.setTouchEnabled(false);
                setUpSlidePanel(mDacDiemBanThan);
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
        mSpThangVaoDoan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayVaoDoanData, mNgayVaoDoanAdt, position, mSpNamVaoDoan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpNamVaoDoan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayVaoDoanData, mNgayVaoDoanAdt, mSpThangVaoDoan.getSelectedItemPosition(), mSpNamVaoDoan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpThangVaoDang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayVaoDangData, mNgayVaoDangAdt, position, mSpNamVaoDang);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpNamVaoDang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayVaoDangData, mNgayVaoDangAdt, mSpThangVaoDang.getSelectedItemPosition(), mSpNamVaoDang);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpThangChinhThuc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayChinhThucData, mNgayChinhThucAdt, position, mSpNamChinhThuc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpNamChinhThuc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayChinhThucData, mNgayChinhThucAdt, mSpThangChinhThuc.getSelectedItemPosition(), mSpNamChinhThuc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void attempUpdate() {
        VDacDiemBanThan dacDiemBanThan = new VDacDiemBanThan();
        dacDiemBanThan.setMaSinhVien(Reference.getInstance().getStudentId(mContext));
        dacDiemBanThan.setChieuCao(mEtChieuCao.getText().toString());
        dacDiemBanThan.setNoiKetNapDoan(mEtNoiKetNapDoan.getText().toString());
        dacDiemBanThan.setNoiKetNapDang(mEtNoiKetNapDang.getText().toString());
        int ngay = Integer.parseInt(mSpNgayVaoDoan.getSelectedItem().toString());
        int thang = Integer.parseInt(mSpThangVaoDoan.getSelectedItem().toString());
        int nam = Integer.parseInt(mSpNamVaoDoan.getSelectedItem().toString());
        dacDiemBanThan.setNgayVaoDoan(thang + "/" + ngay + "/" + nam);
        ngay = Integer.parseInt(mSpNgayVaoDang.getSelectedItem().toString());
        thang = Integer.parseInt(mSpThangVaoDang.getSelectedItem().toString());
        nam = Integer.parseInt(mSpNamVaoDang.getSelectedItem().toString());
        dacDiemBanThan.setNgayVaoDang(thang + "/" + ngay + "/" + nam);
        ngay = Integer.parseInt(mSpNgayChinhThuc.getSelectedItem().toString());
        thang = Integer.parseInt(mSpThangChinhThuc.getSelectedItem().toString());
        nam = Integer.parseInt(mSpNamChinhThuc.getSelectedItem().toString());
        dacDiemBanThan.setNgayChinhThucVaoDang(thang + "/" + ngay + "/" + nam);
        dacDiemBanThan.setThanhPhanXuatThan(mSpXuatThan.getSelectedItemPosition() + "");
        dacDiemBanThan.setDienUuTienBanThan(mSpUuTienBanThan.getSelectedItemPosition() + "");
        dacDiemBanThan.setDienUuTienGiaDinh(mSpUuTienGiaDinh.getSelectedItemPosition() + "");
        dacDiemBanThan.setTinhTrangHonNhan(mSpTinhTrangHonNhan.getSelectedItemPosition() + "");
        dacDiemBanThan.setNhomMau(mSpNhomMau.getSelectedItemPosition() + "");

        new UpdateTask().execute(VDacDiemBanThan.toJson(dacDiemBanThan));
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
                    "api/SinhVien/UpdateDacDiemBanThan/" +
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
                    VDacDiemBanThan dacDiemBanThan = VDacDiemBanThan.fromJson(mJson);

                    if (dacDiemBanThan != null) {
                        mDacDiemBanThan = dacDiemBanThan;
                        setUpMainPanel(dacDiemBanThan);
                        setUpSlidePanel(dacDiemBanThan);
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

    /*##### UPDATE DATA ON VIEW #####*/
    private void bindUI(@NonNull View view) {
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

        mSlidingUpPanelLayout = view.findViewById(R.id.sliding_layout);
        mEtNoiKetNapDoan = view.findViewById(R.id.et_noiKetNapDoan);
        mEtNoiKetNapDang = view.findViewById(R.id.et_noiKetNapDang);
        mEtChieuCao = view.findViewById(R.id.et_chieuCao);
        mSpXuatThan = view.findViewById(R.id.sp_thanhPhanXuatThan);
        mSpUuTienBanThan = view.findViewById(R.id.sp_dienUuTienBanThan);
        mSpUuTienGiaDinh = view.findViewById(R.id.sp_dienUuTienGiaDinh);
        mSpTinhTrangHonNhan = view.findViewById(R.id.sp_tinhTrangHonNhan);
        mSpNhomMau = view.findViewById(R.id.sp_nhomMau);
        mSpNgayVaoDoan = view.findViewById(R.id.sp_ngayVaoDoan);
        mSpThangVaoDoan = view.findViewById(R.id.sp_thangVaoDoan);
        mSpNamVaoDoan = view.findViewById(R.id.sp_namVaoDoan);
        mSpNgayVaoDang = view.findViewById(R.id.sp_ngayVaoDang);
        mSpThangVaoDang = view.findViewById(R.id.sp_thangVaoDang);
        mSpNamVaoDang = view.findViewById(R.id.sp_namVaoDang);
        mSpNgayChinhThuc = view.findViewById(R.id.sp_ngayChinhThuc);
        mSpThangChinhThuc = view.findViewById(R.id.sp_thangChinhThuc);
        mSpNamChinhThuc = view.findViewById(R.id.sp_namChinhThuc);

        mBtnOpenSlide = view.findViewById(R.id.btn_suaDacDiemBanThan);
        mBtnCloseSlide = view.findViewById(R.id.btn_closeSlidePanel);
        mBtnCloseSlideBtm = view.findViewById(R.id.btn_closeSlidePanelBtm);
        mBtnSave = view.findViewById(R.id.btn_save);
        mPbLoading = view.findViewById(R.id.pb_updateLoading);

        mSpXuatThan.setAdapter(mXuatThanAdt);
        mSpUuTienBanThan.setAdapter(mUuTienBanThanAdt);
        mSpUuTienGiaDinh.setAdapter(mUuTienGiaDinhAdt);
        mSpTinhTrangHonNhan.setAdapter(mTinhTrangHonNhanAdt);
        mSpNhomMau.setAdapter(mNhomMauAdt);
        mSpNgayVaoDoan.setAdapter(mNgayVaoDoanAdt);
        mSpThangVaoDoan.setAdapter(mThangVaoDoanAdt);
        mSpNamVaoDoan.setAdapter(mNamVaoDoanAdt);
        mSpNgayVaoDang.setAdapter(mNgayVaoDangAdt);
        mSpThangVaoDang.setAdapter(mThangVaoDangAdt);
        mSpNamVaoDang.setAdapter(mNamVaoDangAdt);
        mSpNgayChinhThuc.setAdapter(mNgayChinhThucAdt);
        mSpThangChinhThuc.setAdapter(mThangChinhThucAdt);
        mSpNamChinhThuc.setAdapter(mNamChinhThucAdt);
    }

    private void setUpMainPanel(VDacDiemBanThan dacDiemBanThan) {
        if (dacDiemBanThan != null) {
            String ngayVaoDoan = isNullOrEmpty(dacDiemBanThan.getNgayVaoDoan()) ? "..." :
                    DateHelper.formatYMDToDMY(dacDiemBanThan.getNgayVaoDoan().substring(0, 10));
            String chieuCao = isNullOrEmpty(dacDiemBanThan.getChieuCao()) ? "..." :
                    dacDiemBanThan.getChieuCao() + " cm";
            String ngayVaoDang = isNullOrEmpty(dacDiemBanThan.getNgayVaoDang()) ? "..." :
                    DateHelper.formatYMDToDMY(dacDiemBanThan.getNgayVaoDang().substring(0, 10));
            String ngayChinhThuc = isNullOrEmpty(dacDiemBanThan.getNgayChinhThucVaoDang()) ? "..." :
                    DateHelper.formatYMDToDMY(dacDiemBanThan.getNgayChinhThucVaoDang().substring(0, 10));

            String[] xuatThans = getResources().getStringArray(R.array.composition_of_origin);
            int selectedIndex = isNullOrEmpty(dacDiemBanThan.getThanhPhanXuatThan()) ?
                    0 : Integer.parseInt(dacDiemBanThan.getThanhPhanXuatThan());
            mXuatThan.setText(xuatThans[selectedIndex]);

            String[] dienUuTienBanThans = getResources().getStringArray(R.array.priority_for_yourself);
            selectedIndex = isNullOrEmpty(dacDiemBanThan.getDienUuTienBanThan()) ?
                    0 : Integer.parseInt(dacDiemBanThan.getDienUuTienBanThan());
            mUuTienBanThan.setText(dienUuTienBanThans[selectedIndex]);

            String[] dienUuTienGiaDinhs = getResources().getStringArray(R.array.family_priority);
            selectedIndex = isNullOrEmpty(dacDiemBanThan.getDienUuTienGiaDinh()) ?
                    0 : Integer.parseInt(dacDiemBanThan.getDienUuTienGiaDinh());
            mUuTienGiaDinh.setText(dienUuTienGiaDinhs[selectedIndex]);

            String[] tinhTrangHonNhans = getResources().getStringArray(R.array.marital_status);
            selectedIndex = isNullOrEmpty(dacDiemBanThan.getTinhTrangHonNhan()) ?
                    0 : Integer.parseInt(dacDiemBanThan.getTinhTrangHonNhan());
            mTinhTrangHonNhan.setText(tinhTrangHonNhans[selectedIndex]);

            String[] nhomMaus = getResources().getStringArray(R.array.blood_group);
            selectedIndex = isNullOrEmpty(dacDiemBanThan.getNhomMau()) ?
                    0 : Integer.parseInt(dacDiemBanThan.getNhomMau());
            mNhomMau.setText(nhomMaus[selectedIndex]);

            mChieuCao.setText(chieuCao);
            mNgayVaoDoan.setText(ngayVaoDoan);
            mNoiKetNapDoan.setText(getValueOrEmpty(dacDiemBanThan.getNoiKetNapDoan()));
            mNgayVaoDang.setText(ngayVaoDang);
            mNoiKetNapDang.setText(getValueOrEmpty(dacDiemBanThan.getNoiKetNapDang()));
            mNgayChinhThucVaoDang.setText(ngayChinhThuc);
        }
    }

    private void setUpSlidePanel(VDacDiemBanThan dacDiemBanThan) {
        // Ngay ket nap doan
        String ngayVaoDoan = "";
        int indexNgay = 0, indexThang = 0, indexNam = 0;
        if (!isNullOrEmpty(dacDiemBanThan.getNgayVaoDoan())) {
            ngayVaoDoan = DateHelper.formatYMDToDMY(dacDiemBanThan.getNgayVaoDoan().substring(0, 10));
            Date d = DateHelper.stringToDate(ngayVaoDoan, "dd/MM/yyyy");
            indexNgay = DateHelper.getDayOfMonth(d);
            indexThang = DateHelper.getMonth(d);
            indexNam = DateHelper.getYear(d) - 1989;
        }
        mSpNgayVaoDoan.setSelection(indexNgay);
        mSpThangVaoDoan.setSelection(indexThang);
        mSpNamVaoDoan.setSelection(indexNam);
        // Noi ket nap doan
        String noiKetNapDoan = isNullOrEmpty(dacDiemBanThan.getNoiKetNapDoan()) ? "" : dacDiemBanThan.getNoiKetNapDoan();
        mEtNoiKetNapDoan.setText("");
        mEtNoiKetNapDoan.append(noiKetNapDoan);
        // Ngay ket nap dang
        String ngayVaoDang = "";
        indexNgay = 0;
        indexThang = 0;
        indexNam = 0;
        if (!isNullOrEmpty(dacDiemBanThan.getNgayVaoDang())) {
            ngayVaoDang = DateHelper.formatYMDToDMY(dacDiemBanThan.getNgayVaoDang().substring(0, 10));
            Date d = DateHelper.stringToDate(ngayVaoDang, "dd/MM/yyyy");
            indexNgay = DateHelper.getDayOfMonth(d);
            indexThang = DateHelper.getMonth(d);
            indexNam = DateHelper.getYear(d) - 1989;
        }
        mSpNgayVaoDang.setSelection(indexNgay);
        mSpThangVaoDang.setSelection(indexThang);
        mSpNamVaoDang.setSelection(indexNam);
        // Noi ket nap dang
        String noiKetNapDang = isNullOrEmpty(dacDiemBanThan.getNoiKetNapDang()) ? "" : dacDiemBanThan.getNoiKetNapDang();
        mEtNoiKetNapDang.setText("");
        mEtNoiKetNapDang.append(noiKetNapDang);
        // Ngay chinh thuc vao dang
        String ngayChinhThuc;
        indexNgay = 0;
        indexThang = 0;
        indexNam = 0;
        if (!isNullOrEmpty(dacDiemBanThan.getNgayChinhThucVaoDang())) {
            ngayChinhThuc = DateHelper.formatYMDToDMY(dacDiemBanThan.getNgayChinhThucVaoDang().substring(0, 10));
            Date d = DateHelper.stringToDate(ngayChinhThuc, "dd/MM/yyyy");
            indexNgay = DateHelper.getDayOfMonth(d);
            indexThang = DateHelper.getMonth(d);
            indexNam = DateHelper.getYear(d) - 1989;
        }
        mSpNgayChinhThuc.setSelection(indexNgay);
        mSpThangChinhThuc.setSelection(indexThang);
        mSpNamChinhThuc.setSelection(indexNam);
        // Chieu cao
        String chieuCao = isNullOrEmpty(dacDiemBanThan.getChieuCao()) ? "" : dacDiemBanThan.getChieuCao();
        mEtChieuCao.setText("");
        mEtChieuCao.append(chieuCao);
        // Nhom mau
        if (isNullOrEmpty(dacDiemBanThan.getNhomMau()))
            mSpNhomMau.setSelection(0);
        else
            mSpNhomMau.setSelection(Integer.parseInt(dacDiemBanThan.getNhomMau()));
        // Thanh phan xuat than
        if (isNullOrEmpty(dacDiemBanThan.getThanhPhanXuatThan()))
            mSpXuatThan.setSelection(0);
        else
            mSpXuatThan.setSelection(Integer.parseInt(dacDiemBanThan.getThanhPhanXuatThan()));
        // Dien uu tien ban than
        if (isNullOrEmpty(dacDiemBanThan.getDienUuTienBanThan()))
            mSpUuTienBanThan.setSelection(0);
        else
            mSpUuTienBanThan.setSelection(Integer.parseInt(dacDiemBanThan.getDienUuTienBanThan()));
        // Dien uu tien gia dinh
        if (isNullOrEmpty(dacDiemBanThan.getDienUuTienGiaDinh()))
            mSpUuTienGiaDinh.setSelection(0);
        else
            mSpUuTienGiaDinh.setSelection(Integer.parseInt(dacDiemBanThan.getDienUuTienGiaDinh()));
        // Tinh trang hon nhan
        if (isNullOrEmpty(dacDiemBanThan.getTinhTrangHonNhan()))
            mSpTinhTrangHonNhan.setSelection(0);
        else
            mSpTinhTrangHonNhan.setSelection(Integer.parseInt(dacDiemBanThan.getTinhTrangHonNhan()));
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
