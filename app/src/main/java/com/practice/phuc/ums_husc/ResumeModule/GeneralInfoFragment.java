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
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.DanToc;
import com.practice.phuc.ums_husc.ViewModel.QuocGia;
import com.practice.phuc.ums_husc.ViewModel.ThanhPho;
import com.practice.phuc.ums_husc.ViewModel.TonGiao;
import com.practice.phuc.ums_husc.ViewModel.VNoiSinh;
import com.practice.phuc.ums_husc.ViewModel.VThongTinChung;
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

import static com.practice.phuc.ums_husc.Helper.StringHelper.isNullOrEmpty;

public class GeneralInfoFragment extends Fragment {
    private Context mContext;
    private boolean mIsCreated;

    private final String GET_NATIONS = "nations";
    private final String GET_CITIES_BY_NATION = "cites";
    private final String GET_PEOPLE = "people";
    private final String GET_RELIGION = "religion";
    private final String DO_UPDATE = "update";

    public GeneralInfoFragment() {
    }

    public static GeneralInfoFragment newInstance(Context context, VThongTinChung thongTinChung) {
        GeneralInfoFragment f = new GeneralInfoFragment();
        f.mContext = context;
        f.mThongTinChung = thongTinChung;
        return f;
    }

    public void setThongTin(VThongTinChung thongTinChung) {
        this.mThongTinChung = thongTinChung;
    }

    private VThongTinChung mThongTinChung;
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
    private ImageButton mOpenSlidePanel, mCloseSlidePanel;
    private Button mBtnSave, mCloseLidePanelBtm;
    private ProgressBar pbUpdateLoading;

    ArrayAdapter<String> mNgaySinhAdapter;
    ArrayAdapter<String> mThangSinhAdapter;
    ArrayAdapter<String> mNamSinhAdapter;
    ArrayAdapter<String> mNgayCapCMNDAdapter;
    ArrayAdapter<String> mThangCapCMNDAdapter;
    ArrayAdapter<String> mNamCapCMNDAdapter;
    ArrayAdapter<QuocGia> mQuocGiaAdapter;
    ArrayAdapter<QuocGia> mQuocTichAdapter;
    ArrayAdapter<ThanhPho> mTinhThanhAdapter;
    ArrayAdapter<DanToc> mDanTocAdapter;
    ArrayAdapter<TonGiao> mTonGiaoAdapter;

    private List<String> mNgaySinhDs;
    private List<String> mNgayCapCMNDDs;
    private List<QuocGia> mQuocGiaDs;
    private List<ThanhPho> mTinhThanhDs;
    private List<DanToc> mDanTocDs;
    private List<TonGiao> mTonGiaoDs;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsCreated = false;

        List<String> mNamDs = new ArrayList<>();
        List<String> mThangDs = new ArrayList<>();
        mNgaySinhDs = new ArrayList<>();
        mNgayCapCMNDDs = new ArrayList<>();
        mQuocGiaDs = new ArrayList<>();
        mTinhThanhDs = new ArrayList<>();
        mDanTocDs = new ArrayList<>();
        mTonGiaoDs = new ArrayList<>();

        for (int i = 0; i <= 31; i++) {
            mNgaySinhDs.add(i + "");
            mNgayCapCMNDDs.add(i + "");

            if (i <= 12) mThangDs.add(i + "");
        }
        mNamDs.add("0");
        int year = DateHelper.getCalendar().get(Calendar.YEAR);
        for (int i = 1990; i <= year; i++) {
            mNamDs.add(i + "");
        }

        mNgaySinhAdapter = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNgaySinhDs);
        mNgaySinhAdapter.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mThangSinhAdapter = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mThangDs);
        mThangSinhAdapter.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNamSinhAdapter = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNamDs);
        mNamSinhAdapter.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNgayCapCMNDAdapter = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNgayCapCMNDDs);
        mNgayCapCMNDAdapter.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mThangCapCMNDAdapter = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mThangDs);
        mThangCapCMNDAdapter.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mNamCapCMNDAdapter = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mNamDs);
        mNamCapCMNDAdapter.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mQuocGiaAdapter = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mQuocGiaDs);
        mQuocGiaAdapter.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mTinhThanhAdapter = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mTinhThanhDs);
        mTinhThanhAdapter.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mQuocTichAdapter = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mQuocGiaDs);
        mQuocTichAdapter.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mDanTocAdapter = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mDanTocDs);
        mDanTocAdapter.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);

        mTonGiaoAdapter = new ArrayAdapter<>(mContext, R.layout.custom_simple_spinner_item, mTonGiaoDs);
        mTonGiaoAdapter.setDropDownViewResource(R.layout.custom_simple_list_item_single_choice);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_info, container, false);
        bindUI(view);

        setUpMainPanel(mThongTinChung);

        setUpEvent();

        if (!mIsCreated) {
            mIsCreated = true;
            new Task().execute(GET_NATIONS);
            new Task().execute(GET_PEOPLE);
            new Task().execute(GET_RELIGION);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsCreated = false;
        mNgaySinhDs.clear();
        mNgayCapCMNDDs.clear();
        mQuocGiaDs.clear();
        mTinhThanhDs.clear();
        mDanTocDs.clear();
        mTonGiaoDs.clear();
    }

    /*###### EVENTS #####*/
    private void setUpEvent() {
        mOpenSlidePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                mSlidingUpPanelLayout.setTouchEnabled(false);
                setUpSlidePanel(mThongTinChung);
            }
        });
        mCloseSlidePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        mCloseLidePanelBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        spThangSinh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgaySinhDs, mNgaySinhAdapter, position, spNamSinh);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spNamSinh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgaySinhDs, mNgaySinhAdapter, spThangSinh.getSelectedItemPosition(), spNamSinh);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spThangCapCMND.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayCapCMNDDs, mNgayCapCMNDAdapter, position, spNamCapCMND);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spNamCapCMND.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateNamNhuan(mNgayCapCMNDDs, mNgayCapCMNDAdapter, spThangCapCMND.getSelectedItemPosition(), spNamCapCMND);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spQuocGiaSinh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                QuocGia quocGia = (QuocGia) spQuocGiaSinh.getSelectedItem();
                int maQuocGia = quocGia.MaQuocGia;
                new Task().execute(GET_CITIES_BY_NATION, maQuocGia + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ngaySinh = Integer.parseInt(spNgaySinh.getSelectedItem().toString());
                int thangSinh = Integer.parseInt(spThangSinh.getSelectedItem().toString());
                int namSinh = Integer.parseInt(spNamSinh.getSelectedItem().toString());
                int quocGiaSinh = ((QuocGia) spQuocGiaSinh.getSelectedItem()).MaQuocGia;
                int tinhThanhSinh = ((ThanhPho) spTinhThanhSinh.getSelectedItem()).MaThanhPho;
                int quocTich = ((QuocGia) spQuocTich.getSelectedItem()).MaQuocGia;
                int tonGiao = ((TonGiao) spTonGiao.getSelectedItem()).MaTonGiao;
                int danToc = ((DanToc) spDanToc.getSelectedItem()).MaDanToc;
                String soCMND = etSoCMND.getText().toString();
                int ngayCapCMND = Integer.parseInt(spNgayCapCMND.getSelectedItem().toString());
                int thangCapCMND = Integer.parseInt(spThangCapCMND.getSelectedItem().toString());
                int namCapCMND = Integer.parseInt(spNamCapCMND.getSelectedItem().toString());
                String noiCapCMND = etNoiCapCMND.getText().toString();

                VThongTinChung newThongTin = new VThongTinChung();
                newThongTin.NoiSinh = new VNoiSinh();
                newThongTin.NgaySinh = thangSinh + "/" + ngaySinh + "/" + namSinh;
                newThongTin.NoiSinh.QuocGia = quocGiaSinh + "";
                newThongTin.NoiSinh.ThanhPho = tinhThanhSinh + "";
                newThongTin.QuocTich = quocTich + "";
                newThongTin.TonGiao = tonGiao + "";
                newThongTin.DanToc = danToc + "";
                newThongTin.SoCMND = soCMND;
                newThongTin.NgayCap = thangCapCMND + "/" + ngayCapCMND + "/" + namCapCMND;
                newThongTin.NoiCap = noiCapCMND;
                newThongTin.MaSinhVien = Reference.getInstance().getStudentId(mContext);
                newThongTin.TenDanToc = "";
                newThongTin.TenQuocGia = "";
                newThongTin.TenTonGiao = "";
                newThongTin.AnhDaiDien = "";
                newThongTin.HoTen = "";
                newThongTin.GioiTinh = false;

                attempUpdate(newThongTin);
            }
        });
    }

    /*##### DATA #####*/
    private void setUpMainPanel(VThongTinChung thongTinChung) {
        if (thongTinChung != null) {
            String gioiTinh = thongTinChung.GioiTinh ? "Nam" : "Nữ";
            mGioiTinh.setText(gioiTinh);

            // Ngay sinh
            String ngaySinh = isNullOrEmpty(thongTinChung.NgaySinh) ? "..."
                    : DateHelper.formatYMDToDMY(thongTinChung.NgaySinh.substring(0, 10));
            mNgaySinh.setText(ngaySinh);
            // Noi sinh
            String thanhPhoSinh = !isNullOrEmpty(thongTinChung.NoiSinh.TenQuocGia) ?
                    thongTinChung.NoiSinh.TenThanhPho : "...";
            String quocGiaSinh = !isNullOrEmpty(thongTinChung.NoiSinh.TenQuocGia) ?
                    thongTinChung.NoiSinh.TenQuocGia : "...";
            String noiSinh = thanhPhoSinh + ", " + quocGiaSinh;
            mNoiSinh.setText(noiSinh);
            // Quoc tich
            String quocTich = !isNullOrEmpty(thongTinChung.TenQuocGia) ? thongTinChung.TenQuocGia : "...";
            mQuocTich.setText(quocTich);
            // Ton giao
            String tonGiao = !isNullOrEmpty(thongTinChung.TenTonGiao) ? thongTinChung.TenTonGiao : "...";
            mTonGiao.setText(tonGiao);
            // Dan toc
            String danToc = !isNullOrEmpty(thongTinChung.TenDanToc) ? thongTinChung.TenDanToc : "...";
            mDanToc.setText(danToc);
            // So CMND
            String soCMND = !isNullOrEmpty(thongTinChung.SoCMND) ? thongTinChung.SoCMND : "...";
            mSoCMND.setText(soCMND);
            // Ngay cap CMND
            String ngayCapCMND = isNullOrEmpty(thongTinChung.NgaySinh) ? "..."
                    : DateHelper.formatYMDToDMY(thongTinChung.NgayCap.substring(0, 10));
            mNgayCapCMND.setText(ngayCapCMND);
            // Noi cap CMND
            String noiCapCMND = !isNullOrEmpty(thongTinChung.NoiCap) ? thongTinChung.NoiCap : "...";
            mNoiCapCMND.setText(noiCapCMND);
        }
    }

    private void setUpSlidePanel(@NonNull VThongTinChung thongTinChung) {
        // Ngay sinh
        String ngaySinh;
        int indexNgay = 0, indexThang = 0, indexNam = 0;
        if (!isNullOrEmpty(thongTinChung.NgaySinh)) {
            ngaySinh = DateHelper.formatYMDToDMY(thongTinChung.NgaySinh.substring(0, 10));
            Date d = DateHelper.stringToDate(ngaySinh, "dd/MM/yyyy");
            indexNgay = DateHelper.getDayOfMonth(d);
            indexThang = DateHelper.getMonth(d);
            indexNam = DateHelper.getYear(d) - 1989;
        }
        spNgaySinh.setSelection(indexNgay);
        spThangSinh.setSelection(indexThang);
        spNamSinh.setSelection(indexNam);
        // Noi sinh
        updateSpQuocGiaSinh(mQuocGiaDs);
        updateSpTinhThanhSinh(mTinhThanhDs);
        // So CMND
        String soCMND = !isNullOrEmpty(thongTinChung.SoCMND) ? thongTinChung.SoCMND : "";
        etSoCMND.setText(soCMND);
        // Ngay cap CMND
        String ngayCapCMND;
        indexNgay = 0;
        indexThang = 0;
        indexNam = 0;
        if (!isNullOrEmpty(thongTinChung.NgaySinh)) {
            ngayCapCMND = DateHelper.formatYMDToDMY(thongTinChung.NgayCap.substring(0, 10));
            Date d = DateHelper.stringToDate(ngayCapCMND, "dd/MM/yyyy");
            indexNgay = DateHelper.getDayOfMonth(d);
            indexThang = DateHelper.getMonth(d);
            indexNam = DateHelper.getYear(d) - 1989;
        }
        spNgayCapCMND.setSelection(indexNgay);
        spThangCapCMND.setSelection(indexThang);
        spNamCapCMND.setSelection(indexNam);
        // Noi cap CMND
        String noiCapCMND = !isNullOrEmpty(thongTinChung.NoiCap) ? thongTinChung.NoiCap : "";
        etNoiCapCMND.setText(noiCapCMND);
        // Quoc tich
        updateSpQuocTich(mQuocGiaDs);
        // Dan toc
        updateSpDanToc(mDanTocDs);
        // Ton giao
        updateSpTonGiao(mTonGiaoDs);
    }

    private void attempUpdate(VThongTinChung thongTinChung) {
        if (NetworkUtil.getConnectivityStatus(mContext) == NetworkUtil.TYPE_NOT_CONNECTED) {
            Toasty.custom(mContext, getString(R.string.error_network_disconected),
                    getResources().getDrawable(R.drawable.ic_signal_wifi_off_white_24),
                    getResources().getColor(R.color.colorRed),
                    getResources().getColor(android.R.color.white),
                    Toasty.LENGTH_SHORT, true, true)
                    .show();

        } else {
            String json = VThongTinChung.toJson(thongTinChung);
            new Task().execute(DO_UPDATE, json);
            pbUpdateLoading.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class Task extends AsyncTask<String, Void, Boolean> {
        Response mResponse;
        String mJson = "";
        String ORDER = "";

        @Override
        protected Boolean doInBackground(String... strings) {
            String order = strings[0];
            String host = Reference.getInstance().getHost(mContext);

            switch (order) {
                case GET_NATIONS:
                    String url = host + "api/DungChung/Get/QuocGia/";
                    mResponse = NetworkUtil.makeRequest(url, false, null);
                    ORDER = GET_NATIONS;
                    return true;

                case GET_CITIES_BY_NATION:
                    url = host + "api/DungChung/Get/ThanhPho/?refId=" + strings[1];
                    mResponse = NetworkUtil.makeRequest(url, false, null);
                    ORDER = GET_CITIES_BY_NATION;
                    return true;

                case GET_RELIGION:
                    url = host + "api/DungChung/Get/TonGiao/";
                    mResponse = NetworkUtil.makeRequest(url, false, null);
                    ORDER = GET_RELIGION;
                    return true;

                case GET_PEOPLE:
                    url = host + "api/DungChung/Get/DanToc";
                    mResponse = NetworkUtil.makeRequest(url, false, null);
                    ORDER = GET_PEOPLE;
                    return true;

                case DO_UPDATE:
                    url = host + "api/SinhVien/UpdateThongTinChung/" +
                            "?masinhvien=" + Reference.getInstance().getStudentId(mContext) +
                            "&matkhau=" + Reference.getInstance().getAccountPassword(mContext);
                    String data = strings[1];

                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data);
                    mResponse = NetworkUtil.makeRequest(url, true, body);
                    ORDER = DO_UPDATE;
                    return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                if (mResponse == null) {
                    Toasty.error(mContext, getString(R.string.error_server_not_response), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mResponse.code() == NetworkUtil.OK) {
                    try {
                        mJson = mResponse.body() != null ? mResponse.body().string() : "";
                        switch (ORDER) {
                            case GET_NATIONS:
                                List<QuocGia> quocGias = QuocGia.fromJson(mJson);
                                if (quocGias != null) {
                                    mQuocGiaDs.clear();
                                    mQuocGiaDs.add(new QuocGia(0, "----"));
                                    mQuocGiaDs.addAll(quocGias);
                                    mQuocGiaAdapter.notifyDataSetChanged();
                                    mQuocTichAdapter.notifyDataSetChanged();
                                    updateSpQuocGiaSinh(mQuocGiaDs);
                                    updateSpQuocTich(mQuocGiaDs);
                                }
                                break;

                            case GET_CITIES_BY_NATION:
                                List<ThanhPho> thanhPhos = ThanhPho.fromJson(mJson);
                                if (thanhPhos != null) {
                                    mTinhThanhDs.clear();
                                    mTinhThanhDs.add(new ThanhPho(0, "----"));
                                    mTinhThanhDs.addAll(thanhPhos);
                                    mTinhThanhAdapter.notifyDataSetChanged();
                                    updateSpTinhThanhSinh(mTinhThanhDs);
                                }
                                break;

                            case GET_RELIGION:
                                List<TonGiao> tonGiaos = TonGiao.fromJson(mJson);
                                if (tonGiaos != null) {
                                    mTonGiaoDs.clear();
                                    mTonGiaoDs.add(new TonGiao(0, "----"));
                                    mTonGiaoDs.addAll(tonGiaos);
                                    mTonGiaoAdapter.notifyDataSetChanged();
                                    updateSpTonGiao(mTonGiaoDs);
                                }
                                break;

                            case GET_PEOPLE:
                                List<DanToc> danTocs = DanToc.fromJson(mJson);
                                if (danTocs != null) {
                                    mDanTocDs.clear();
                                    mDanTocDs.add(new DanToc(0, "----"));
                                    mDanTocDs.addAll(danTocs);
                                    mDanTocAdapter.notifyDataSetChanged();
                                    updateSpDanToc(mDanTocDs);
                                }
                                break;

                            case DO_UPDATE:
                                pbUpdateLoading.setVisibility(View.GONE);
                                VThongTinChung thongTinChung = VThongTinChung.fromJson(mJson);
                                if (thongTinChung != null) {
                                    mThongTinChung = thongTinChung;
                                    setUpSlidePanel(thongTinChung);
                                    setUpMainPanel(thongTinChung);
                                    Toasty.success(mContext, "Đã cập nhật !", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.d("DEBUG", mResponse.body() != null ? mResponse.body().string() : "NULL");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /*##### UPDATE DATA ON VIEW #####*/

    private void bindUI(@NonNull View view) {
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
        mOpenSlidePanel = view.findViewById(R.id.btn_suaThongTinChung);
        mCloseSlidePanel = view.findViewById(R.id.btn_closeSlidePanel);
        mBtnSave = view.findViewById(R.id.btn_luuThongTinChung);
        mCloseLidePanelBtm = view.findViewById(R.id.btn_closeSlidePanelBtm);
        pbUpdateLoading = view.findViewById(R.id.pb_updateLoading);

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

        spNgaySinh.setAdapter(mNgaySinhAdapter);
        spThangSinh.setAdapter(mThangSinhAdapter);
        spNamSinh.setAdapter(mNamSinhAdapter);
        spNgayCapCMND.setAdapter(mNgayCapCMNDAdapter);
        spThangCapCMND.setAdapter(mThangCapCMNDAdapter);
        spNamCapCMND.setAdapter(mNamCapCMNDAdapter);
        spQuocGiaSinh.setAdapter(mQuocGiaAdapter);
        spQuocTich.setAdapter(mQuocTichAdapter);
        spTinhThanhSinh.setAdapter(mTinhThanhAdapter);
        spTonGiao.setAdapter(mTonGiaoAdapter);
        spDanToc.setAdapter(mDanTocAdapter);
    }

    private void updateSpQuocGiaSinh(List<QuocGia> quocGias) {
        if (quocGias == null) {
            return;
        }
        int index = 0;

        if (quocGias.size() > 0 && mThongTinChung.NoiSinh != null && mThongTinChung.NoiSinh.QuocGia != null) {
            try {
                int maHienTai = Integer.parseInt(mThongTinChung.NoiSinh.QuocGia);
                for (int i = 0; i < quocGias.size(); i++) {
                    if (quocGias.get(i).MaQuocGia == maHienTai) {
                        index = i;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        spQuocGiaSinh.setSelection(index);
    }

    private void updateSpQuocTich(List<QuocGia> quocgias) {
        if (quocgias == null) return;

        int index = 0;
        if (quocgias.size() > 0 && mThongTinChung.QuocTich != null) {
            try {
                int maHienTai = Integer.parseInt(mThongTinChung.QuocTich);
                for (int i = 0; i < quocgias.size(); i++) {
                    if (quocgias.get(i).MaQuocGia == maHienTai) {
                        index = i;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        spQuocTich.setSelection(index);
    }

    private void updateSpTinhThanhSinh(List<ThanhPho> thanhPhos) {
        if (thanhPhos == null) return;

        int index = 0;
        if (thanhPhos.size() > 0 && mThongTinChung.NoiSinh != null && mThongTinChung.NoiSinh.ThanhPho != null) {
            try {
                int maHienTai = Integer.parseInt(mThongTinChung.NoiSinh.ThanhPho);
                for (int i = 0; i < thanhPhos.size(); i++) {
                    if (thanhPhos.get(i).MaThanhPho == maHienTai) {
                        index = i;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        spTinhThanhSinh.setSelection(index);
    }

    private void updateSpTonGiao(List<TonGiao> tongiaos) {
        if (tongiaos == null) return;

        int index = 0;
        if (tongiaos.size() > 0 && mThongTinChung.TonGiao != null) {
            try {
                int maHienTai = Integer.parseInt(mThongTinChung.TonGiao);
                for (int i = 0; i < tongiaos.size(); i++) {
                    if (tongiaos.get(i).MaTonGiao == maHienTai) {
                        index = i;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        spTonGiao.setSelection(index);
    }

    private void updateSpDanToc(List<DanToc> danTocs) {
        if (danTocs == null) return;

        int index = 0;
        if (danTocs.size() > 0 && mThongTinChung.TonGiao != null) {
            try {
                int maHienTai = Integer.parseInt(mThongTinChung.DanToc);
                for (int i = 0; i < danTocs.size(); i++) {
                    if (danTocs.get(i).MaDanToc == maHienTai) {
                        index = i;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        spDanToc.setSelection(index);
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
