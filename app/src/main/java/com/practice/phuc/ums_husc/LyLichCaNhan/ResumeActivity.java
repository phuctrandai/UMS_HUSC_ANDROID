package com.practice.phuc.ums_husc.LyLichCaNhan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.phuc.ums_husc.Adapter.SectionsPagerAdapter;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.VLyLichCaNhan;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResumeActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    // UI
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private ViewGroup mProgressViewLayout;
    private View mNetworkError;
    private View mLyLichLayout;
    private TextView mThongBaoLoi;
    private TextView tvMaSinhVien;
    private TextView tvHoTen;
    private TextView tvKhoaHocNganhHoc;

    Moshi moshi;
    Type usersType;
    JsonAdapter<VLyLichCaNhan> jsonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        // Animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bind UI
        mProgressViewLayout = findViewById(R.id.loading_progress_layout);
        mLyLichLayout = findViewById(R.id.layout_lyLich);
        mNetworkError = findViewById(R.id.layout_thongBaoKhongCoMang);
        mThongBaoLoi = findViewById(R.id.tv_thongBaoLoi);
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tvMaSinhVien = findViewById(R.id.tv_maSinhVien);
        tvHoTen = findViewById(R.id.tv_hoTen);
        tvKhoaHocNganhHoc = findViewById(R.id.tv_khoaHocNganhHoc);
        Button btnBack = findViewById(R.id.btn_back);
        Button btnTryAgain = findViewById(R.id.btn_thuLai);

        // Cast json to model
        moshi = new Moshi.Builder().build();
        usersType = Types.newParameterizedType(VLyLichCaNhan.class);
        jsonAdapter = moshi.adapter(usersType);

        // Set up view pager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());

        // Set up back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResumeActivity.this.onBackPressed();
            }
        });

        // Set up try again button
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showError(false);
                attempLoadLyLich();
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Load thong tin ly lich
        attempLoadLyLich();
    }

    // Kiem tra truoc khi lay thong tin ly lich
    private void attempLoadLyLich() {
        hienThiThongTinCaNhan();
        if (NetworkUtil.getConnectivityStatus(ResumeActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED) {
            mThongBaoLoi.setText(getString(R.string.network_not_available));
            showError(true);
        } else {
            new LoadLyLichTastk().execute((String) null);
        }
    }

    // Thuc hien lay thong tin ly lich ca nhan
    public class LoadLyLichTastk extends AsyncTask<String, Void, Boolean> {
        private Response mRespose = null;
        private String mErrorMessage = "";

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("sinhVien", MODE_PRIVATE);
                String maSinhVien = sharedPreferences.getString("maSinhVien", "");

                mRespose = loadLyLich(maSinhVien);
                if (mRespose == null) {
                    mErrorMessage = getString(R.string.error_time_out);
                    return false;
                } else {
                    if (mRespose.code() == Reference.OK) {
                        String json = null;
                        try {
                            json = mRespose.body().string();
                            VLyLichCaNhan lyLichCaNhan = jsonAdapter.fromJson(json);
                            mSectionsPagerAdapter.setThongTin(lyLichCaNhan.getThongTinChung(),
                                    lyLichCaNhan.getThongTinLienHe(),
                                    lyLichCaNhan.getQueQuan(),
                                    lyLichCaNhan.getThuongTru(),
                                    lyLichCaNhan.getDacDiemBanThan(),
                                    lyLichCaNhan.getLichSuBanThan());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else if (mRespose.code() == Reference.NOT_FOUND) {
                        mErrorMessage = getString(R.string.error_time_out);
                    } else {
                        mErrorMessage = getString(R.string.error_time_out);
                    }
                    return false;
                }
            } catch (Exception e) {
                Log.d("DEBUG", e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                mViewPager.setAdapter(mSectionsPagerAdapter);
                tabLayout.setupWithViewPager(mViewPager);
                showProgress(false);
            } else {
                setError(mErrorMessage);
                showError(true);
            }
            Log.d("DEBUG", success + " - ResumeActivity - On post excute");
        }
    }

    // Lay thong tin ly lich tu may chu ve
    private Response loadLyLich(String maSinhVien) {
        String url = Reference.HOST + Reference.LOAD_LY_LICH_API + "?maSinhVien=" + maSinhVien;

        return NetworkUtil.makeRequest(url, null);
    }

    // Hien thi hinh anh dang tai du lieu
    private void showProgress(final boolean show) {
        mLyLichLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressViewLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mNetworkError.setVisibility(View.GONE);
    }

    // Set tin nhan loi
    private void setError(String error) {
        if (mThongBaoLoi != null) {
            mThongBaoLoi.setText(getString(R.string.error_time_out));
        }
    }

    // Hien thi thong bao loi len man hinh
    private void showError(final boolean show) {
        mLyLichLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressViewLayout.setVisibility(View.GONE);
        mNetworkError.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    // Hien thi thong tin ca nhan
    private void hienThiThongTinCaNhan() {
        SharedPreferences sharedPreferences = getSharedPreferences("sinhVien", MODE_PRIVATE);
        String maSinhVien = sharedPreferences.getString("maSinhVien", "");
        String hoTen = sharedPreferences.getString("hoTen", "");
        String khoaHoc = sharedPreferences.getString("khoaHoc", "");
        String nganhHoc = sharedPreferences.getString("nganhHoc", "");

        tvMaSinhVien.setText(maSinhVien);
        tvHoTen.setText(hoTen);
        tvKhoaHocNganhHoc.setText(khoaHoc + " - " + nganhHoc);
    }
}
