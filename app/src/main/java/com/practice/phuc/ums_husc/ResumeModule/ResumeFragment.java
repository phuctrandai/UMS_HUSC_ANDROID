package com.practice.phuc.ums_husc.ResumeModule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Adapter.ResumePagerAdapter;
import com.practice.phuc.ums_husc.Helper.CustomSnackbar;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.VLyLichCaNhan;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ResumeFragment extends Fragment {
    public ResumeFragment() {
    }

    public static ResumeFragment newInstance(Context context) {
        ResumeFragment rf = new ResumeFragment();
        rf.mContext = context;
        return rf;
    }

    private Context mContext;
    private LoadResumeTask mLoadResumeTask;
    private ResumePagerAdapter mResumePagerAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewGroup mLayoutLoading;
    private ViewGroup mLayoutData;
    private RelativeLayout mRootLayout;
    private JsonAdapter<VLyLichCaNhan> mJsonAdapter;
    private String mErrorMessage;
    private Snackbar mErrorSnackbar;
    private Snackbar mNotNetworkSnackbar;
    private boolean mIsViewDestroyed;
    private int mStatus;

    private final int STATUS_INIT = 0;
    private final int STATUS_SHOW_ERROR = 1;
    private final int STATUS_SHOW_DATA = 2;
    private final int STATUS_NOT_NETWORK = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mResumePagerAdapter = new ResumePagerAdapter(getChildFragmentManager());
        mIsViewDestroyed = false;
        mStatus = STATUS_INIT;
        Moshi mMoshi = new Moshi.Builder().build();
        Type mUsersType = Types.newParameterizedType(VLyLichCaNhan.class);
        mJsonAdapter = mMoshi.adapter(mUsersType);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resume, container, false);
        mLayoutData = view.findViewById(R.id.layout_resume);
        mLayoutLoading = view.findViewById(R.id.layout_loading);
        mRootLayout = view.findViewById(R.id.layout_root_resume);
        mTabLayout = view.findViewById(R.id.tabs);
        mViewPager = view.findViewById(R.id.vp_resume);
        mViewPager.setOffscreenPageLimit(mResumePagerAdapter.getCount());
        mIsViewDestroyed = false;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        displayPersonalData(view);

        switch (mStatus) {
            case STATUS_INIT:
                attempGetData();
                break;
            case STATUS_NOT_NETWORK:
                showNetworkErrorSnackbar(true);
                break;
            case STATUS_SHOW_ERROR:
                showErrorSnackbar(true, mErrorMessage);
                break;
            case STATUS_SHOW_DATA:
                mViewPager.setAdapter(mResumePagerAdapter);
                mTabLayout.setupWithViewPager(mViewPager);
                mLayoutLoading.setVisibility(View.GONE);
                mLayoutData.setVisibility(View.VISIBLE);
                break;
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        showErrorSnackbar(false, "");
        showNetworkErrorSnackbar(false);
        mIsViewDestroyed = true;

        super.onPause();
    }

    @Override
    public void onDestroy() {
        mIsViewDestroyed = true;
        if (mLoadResumeTask != null) {
            mLoadResumeTask.cancel(true);
            mLoadResumeTask = null;
        }
        super.onDestroy();
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadResumeTask extends AsyncTask<String, Void, Boolean> {
        private Response mRespose = null;

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadResumeTask == null) return false;

            try {
                mRespose = fetchData();
                if (mRespose == null) {
                    mErrorMessage = getString(R.string.error_time_out);
                    return false;
                } else {
                    if (mRespose.code() == NetworkUtil.OK) {
                        try {
                            String json = (mRespose.body() != null) ? mRespose.body().string() : "";
                            VLyLichCaNhan lyLichCaNhan = mJsonAdapter.fromJson(json);
                            mResumePagerAdapter.setThongTin(lyLichCaNhan.getThongTinChung(),
                                    lyLichCaNhan.getThongTinLienHe(),
                                    lyLichCaNhan.getQueQuan(),
                                    lyLichCaNhan.getThuongTru(),
                                    lyLichCaNhan.getDacDiemBanThan(),
                                    lyLichCaNhan.getLichSuBanThan());
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    } else if (mRespose.code() == NetworkUtil.BAD_REQUEST) {
                        mErrorMessage = mRespose.body() != null ? mRespose.body().string() : "";
                    } else {
                        mErrorMessage = getString(R.string.error_time_out);
                    }
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (mLoadResumeTask == null) return;
            if (success) {
                mStatus = STATUS_SHOW_DATA;
                mViewPager.setAdapter(mResumePagerAdapter);
                mTabLayout.setupWithViewPager(mViewPager);

                mLayoutData.setVisibility(View.VISIBLE);
                mLayoutData.setAlpha(0f);
                mLayoutData.animate().alpha(1f).setDuration(500).start();
                mLayoutLoading.setVisibility(View.GONE);
            } else {
                showErrorSnackbar(true, mErrorMessage);
            }
        }
    }

    private void attempGetData() {
        if (NetworkUtil.getConnectivityStatus(mContext) == NetworkUtil.TYPE_NOT_CONNECTED) {
            showNetworkErrorSnackbar(true);
        } else {
            mLoadResumeTask = new LoadResumeTask();
            mLoadResumeTask.execute((String) null);
        }
    }

    private Response fetchData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        String maSinhVien = sharedPreferences.getString(getString(R.string.pre_key_student_id), null);
        String matKhau = sharedPreferences.getString(getString(R.string.pre_key_password), null);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/xml; charset=utf-8"), ""
        );
        return NetworkUtil.makeRequest(Reference.getLoadLyLichApiUrl(maSinhVien, matKhau),
                true, requestBody);
    }

    private void displayPersonalData(View view) {
        TextView tvMaSinhVien = view.findViewById(R.id.tv_maSinhVien);
        TextView tvHoTen = view.findViewById(R.id.tv_hoTen);
        TextView tvKhoaHocNganhHoc = view.findViewById(R.id.tv_khoaHocNganhHoc);

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        String maSinhVien = sharedPreferences.getString(getString(R.string.pre_key_student_id), "");
        String hoTen = sharedPreferences.getString(getString(R.string.pre_key_student_name), "");
        String khoaHoc = sharedPreferences.getString(getString(R.string.pre_key_course), "");
        String nganhHoc = sharedPreferences.getString(getString(R.string.pre_key_majors), "");

        tvMaSinhVien.setText(maSinhVien);
        tvHoTen.setText(hoTen);
        String khoaNganh = khoaHoc + " - " + nganhHoc;
        tvKhoaHocNganhHoc.setText(khoaNganh);
    }

    private void showNetworkErrorSnackbar(boolean show) {
        if (mIsViewDestroyed) return;

        if (show) {
            mStatus = STATUS_NOT_NETWORK;
            mNotNetworkSnackbar = CustomSnackbar.createTwoButtonSnackbar(mContext, mRootLayout
                    , getString(R.string.network_not_available)
                    , null
                    , new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNotNetworkSnackbar.dismiss();
                            attempGetData();
                        }
                    });
            mNotNetworkSnackbar.show();
        } else if (mNotNetworkSnackbar != null) {
            mNotNetworkSnackbar.dismiss();
        }
    }

    private void showErrorSnackbar(boolean show, String message) {
        if (mIsViewDestroyed) return;

        if (show) {
            mStatus = STATUS_SHOW_ERROR;
            mErrorSnackbar = CustomSnackbar.createTwoButtonSnackbar(mContext, mRootLayout
                    , message
                    , null
                    , new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mErrorSnackbar.dismiss();
                            attempGetData();
                        }
                    });
            mErrorSnackbar.show();
        } else if (mErrorSnackbar != null) {
            mErrorSnackbar.dismiss();
        }
    }
}
