package com.practice.phuc.ums_husc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.JustifyTextInTextView;
import com.practice.phuc.ums_husc.Helper.MyFireBaseMessagingService;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Response;

public class DetailNewsActivity extends AppCompatActivity {

    private boolean launchFromNotification;
    private LoadNewsContentTask mLoadTask;

    // UI
    private TextView tvTieuDe;
    private TextView tvThoiGianDang;
    private WebView tvNoiDung;
    private ProgressBar progressBar;

    // Cast json to model
    Moshi moshi;
    Type usersType;
    JsonAdapter<THONGBAO> jsonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);
        Log.d("DEBUG", "ON CREATE Detail news activity");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvTieuDe = findViewById(R.id.tv_tieuDe);
        tvThoiGianDang = findViewById(R.id.tv_thoiGianDang);
        tvNoiDung = findViewById(R.id.tv_noiDung);
        progressBar = findViewById(R.id.progressBar);

        // animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Neu mo tu thong bao, thi khi tro ve se tro ve main activity
        launchFromNotification = getIntent().getBundleExtra("news").getBoolean("fromNotification");

        // hien thi chi tiet bai dang
        hienThiBaiDang();
    }

    @Override
    protected void onResume() {
//        Log.d("DEBUG", "ON RESUME Detail news activity");
        MyFireBaseMessagingService.context = this;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
//        if (launchFromNotification) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finishAfterTransition();
//        } else {
//            super.onBackPressed();
//        }
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void hienThiBaiDang() {
        Bundle bundle = getIntent().getBundleExtra("news");
        tvTieuDe.setText(bundle.getString("title"));
        tvThoiGianDang.setText(bundle.getString("postTime"));
        JustifyTextInTextView.justify(tvTieuDe);

        if (launchFromNotification) {
            Reference.mHasNewNews = true;
            mLoadTask = new LoadNewsContentTask();
            mLoadTask.execute((String) null);
        } else {
            progressBar.setVisibility(View.GONE);
            String content = bundle.getString("body");
            String htmlContent = "<div style='text-align: justify'>" + content + "</div>";
            tvNoiDung.loadData(htmlContent, "text/html; charset=UTF-8",null);
        }
    }

    private class LoadNewsContentTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;
        private String mErrorMessage;
        private String json;

        @Override
        protected void onPreExecute() {
            tvNoiDung.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadTask == null) return false;

            try {
                mResponse = fetchData();
                if (mResponse == null) {
                    Log.d("DEBUG", "Get noi dung thong bao Response null");
                    mErrorMessage = getString(R.string.error_time_out);
                    return false;

                } else {
                    Log.d("DEBUG", "Get noi dung thong bao Response code: " + mResponse.code());
                    if (mResponse.code() == NetworkUtil.OK) {
                        json = mResponse.body() != null ? mResponse.body().string() : "";
                        return true;

                    } else if (mResponse.code() == NetworkUtil.NOT_FOUND) {
                        Log.d("DEBUG", "Get noi dung thong bao Response Mess: " + mResponse.body().string());
                        mErrorMessage = getString(R.string.error_server_not_response);
                    } else {
                        mErrorMessage = getString(R.string.error_server_not_response);
                    }
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (mLoadTask != null) {
                if (success) {
                    setData(json);
                    progressBar.setVisibility(View.GONE);
                    tvNoiDung.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private Response fetchData() {
        String maSinhVien = getSharedPreferences("sinhVien", MODE_PRIVATE)
                .getString("maSinhVien", null);
        String matKhau = getSharedPreferences("sinhVien", MODE_PRIVATE)
                .getString("matKhau", null);
        String id = getIntent().getBundleExtra("news").getString("id");
        String url = Reference.getLoadNoiDungThongBaoApiUrl(maSinhVien, matKhau, id);
//        Log.d("DEBUG", "Get noi dung thong bao URL: " + url);

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void setData(String json) {
        moshi = new Moshi.Builder().build();
        usersType = Types.newParameterizedType(THONGBAO.class);
        jsonAdapter = moshi.adapter(usersType);

        try {
            THONGBAO thongbao = jsonAdapter.fromJson(json);
            String htmlContent = "<div style='text-align: justify'>" + thongbao.getNoiDung() + "</div>";
            tvNoiDung.loadData(htmlContent, "text/html; charset=UTF-8",null);
            Reference.mNewThongBao = thongbao;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
