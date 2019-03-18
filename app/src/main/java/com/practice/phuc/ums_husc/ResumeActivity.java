package com.practice.phuc.ums_husc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import android.widget.Toast;

import com.practice.phuc.ums_husc.Adapter.SectionsPagerAdapter;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResumeActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private ViewGroup mProgressViewLayout;
    private View mLoadingView;
    private View mLyLichLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);


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

        attempLoadLyLich();
    }

    private void attempLoadLyLich() {
        showProgress(true);
        if (NetworkUtil.getConnectivityStatus(ResumeActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED) {
            Toast.makeText(this, getString(R.string.network_not_available), Toast.LENGTH_LONG).show();
        } else {
            new LoadLyLichTastk().execute((String) null);
        }
    }

    // Thuc hien lay thong tin ly lich ca nhan
    public class LoadLyLichTastk extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            try {
                mSectionsPagerAdapter.setThongTin(null, null,
                        null, null,null, null);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                tabLayout.setupWithViewPager(mViewPager);
                Thread.sleep(1500);
            } catch (Exception e) {

                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                //ThongTinChungFragment.displayThongTinChung();
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    private String loadLyLich(String maSinhVien) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        // Tao request
        Request request = new Request.Builder()
                .url(Reference.HOST + Reference.LOAD_LY_LICH_API + "?masinhvien=" + maSinhVien)
                .get().build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response != null) {
                Log.d("UMS_HUSC", response.code() + response.body().string());
                if (response.code() == Reference.OK)
                    return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Hien thi hinh anh dang tai du lieu
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
