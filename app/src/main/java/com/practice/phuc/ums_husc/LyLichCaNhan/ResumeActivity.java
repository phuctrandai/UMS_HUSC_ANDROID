package com.practice.phuc.ums_husc.LyLichCaNhan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private ViewGroup mProgressViewLayout;
    private View mLoadingView;
    private View mNetworkError;
    private View mLyLichLayout;
    private TextView mThongBaoLoi;

    Moshi moshi;
    Type usersType;
    JsonAdapter<VLyLichCaNhan> jsonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResumeActivity.this.onBackPressed();
            }
        });

        mProgressViewLayout = findViewById(R.id.loading_progress_layout);
        mLyLichLayout = findViewById(R.id.layout_lyLich);
        mLoadingView = findViewById(R.id.loading_progress);
        mNetworkError = findViewById(R.id.layout_thongBaoKhongCoMang);
        mThongBaoLoi = findViewById(R.id.tv_thongBaoLoi);

        moshi = new Moshi.Builder().build();
        usersType = Types.newParameterizedType(VLyLichCaNhan.class);
        jsonAdapter = moshi.adapter(usersType);

        attempLoadLyLich();
    }

    // Kiem tra truoc khi lay thong tin ly lich
    private void attempLoadLyLich() {
        hienThiThongTinCaNhan();
        if (NetworkUtil.getConnectivityStatus(ResumeActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED) {
            showError(true);
        } else {
            showProgress(true);
            new LoadLyLichTastk().execute((String) null);
        }
    }

    // Thuc hien lay thong tin ly lich ca nhan
    public class LoadLyLichTastk extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            try {
                Thread.sleep(500);

                String json = loadLyLich("15T1021129");
                Log.d("UMS_json:", json);
                VLyLichCaNhan lyLichCaNhan = jsonAdapter.fromJson(json);

                mSectionsPagerAdapter.setThongTin(lyLichCaNhan.getThongTinChung(),
                        lyLichCaNhan.getThongTinLienHe(),
                        lyLichCaNhan.getQueQuan(),
                        lyLichCaNhan.getThuongTru(),
                        lyLichCaNhan.getDacDiemBanThan(),
                        lyLichCaNhan.getLichSuBanThan());
            } catch (Exception e) {
                Log.d("UMS_HUSC_doInBackground", e.getMessage());
                e.printStackTrace();
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mViewPager.setAdapter(mSectionsPagerAdapter);

            if (success) {
                Log.d("UMS_onPostExecute:", success + "");
            } else {
                mThongBaoLoi.setText(getString(R.string.error_time_out));
                showError(true);
                Log.d("UMS_onPostExecute:", success + "");
            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    // Lay thong tin ly lich tu may chu ve
    private String loadLyLich(String maSinhVien) {
        String result = "";
        final OkHttpClient okHttpClient = new OkHttpClient();
        // Tao request
        Request request = new Request.Builder()
                .url(Reference.HOST + Reference.LOAD_LY_LICH_API + "?maSinhVien=" + maSinhVien)
                .get().build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response != null) {
                if (response.code() == Reference.OK)
                    result = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Hien thi hinh anh dang tai du lieu
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mLyLichLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressViewLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoadingView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressViewLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    // Hien thi thong bao loi len man hinh
    private void showError(final boolean show) {
        mLyLichLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        mNetworkError.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    // Hien thi thong tin ca nhan
    private void hienThiThongTinCaNhan() {
        TextView tvMaSinhVien = findViewById(R.id.tv_maSinhVien);
        TextView tvHoTen = findViewById(R.id.tv_hoTen);
        TextView tvKhoaHocNganhHoc = findViewById(R.id.tv_khoaHocNganhHoc);

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
