package com.practice.phuc.ums_husc.NewsModule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.CustomSnackbar;
import com.practice.phuc.ums_husc.Helper.JustifyTextInTextView;
import com.practice.phuc.ums_husc.Helper.MyFireBaseMessagingService;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.practice.phuc.ums_husc.R;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

import okhttp3.Response;

public class DetailNewsActivity extends AppCompatActivity {

    private View rootLayout;
    private TextView tvTieuDe;
    private TextView tvThoiGianDang;
    private WebView tvNoiDung;
    private ProgressBar progressBar;

    private LoadNewsContentTask mLoadTask;
    private boolean mIsViewDestroyed;
    private Snackbar mNotNetworkSnackbar;
    private Snackbar mErrorSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rootLayout = findViewById(R.id.layout_detail_news_root);
        tvTieuDe = findViewById(R.id.tv_tieuDe);
        tvThoiGianDang = findViewById(R.id.tv_thoiGianDang);
        tvNoiDung = findViewById(R.id.tv_noiDung);
        progressBar = findViewById(R.id.progressBar);
        mIsViewDestroyed = false;

        // hien thi chi tiet bai dang
        showData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showData();
    }

    @Override
    protected void onResume() {
        MyFireBaseMessagingService.mContext = this;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
            mLoadTask = null;
        }
        mIsViewDestroyed = true;
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showData() {
        Bundle bundle = getIntent().getBundleExtra(Reference.BUNDLE_EXTRA_NEWS);
        boolean launchFromNotification = bundle.getBoolean(Reference.BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, false);
        tvTieuDe.setText(bundle.getString(Reference.BUNDLE_KEY_NEWS_TITLE, ""));
        tvThoiGianDang.setText(bundle.getString(Reference.BUNDLE_KEY_NEWS_POST_TIME, ""));
        JustifyTextInTextView.justify(tvTieuDe);

        if (launchFromNotification) {
            Reference.mHasNewNews = true;
            mLoadTask = new LoadNewsContentTask();
            mLoadTask.execute((String) null);
        } else {
            progressBar.setVisibility(View.GONE);
            String content = bundle.getString(Reference.BUNDLE_KEY_NEWS_BODY);
            String htmlContent = "<div style='text-align: justify'>" + content + "</div>";
            tvNoiDung.loadData(htmlContent, "text/html; charset=UTF-8",null);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadNewsContentTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;
        private String mErrorMessage;
        private String json;

        @Override
        protected void onPreExecute() {
            tvNoiDung.setVisibility(View.GONE);

            if (NetworkUtil.getConnectivityStatus(DetailNewsActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED) {
                showNetworkErrorSnackbar(true);
                mLoadTask.cancel(true);
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadTask == null) return false;

            try {
                mResponse = fetchData();
                if (mResponse == null) {
                    mErrorMessage = getString(R.string.error_time_out);
                    return false;

                } else {
                    if (mResponse.code() == NetworkUtil.OK) {
                        json = mResponse.body() != null ? mResponse.body().string() : "";
                        return true;

                    } else if (mResponse.code() == NetworkUtil.BAD_REQUEST) {
                        mErrorMessage = mResponse.body() != null ? mResponse.body().string() : "";
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
                    showNetworkErrorSnackbar(false);
                    showErrorSnackbar(false, "");
                } else {
                    showErrorSnackbar(true, mErrorMessage);
                }
            }
            super.onPostExecute(success);
        }

        @Override
        protected void onCancelled() {
            mLoadTask = null;
            super.onCancelled();
        }
    }

    private Response fetchData() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        String maSinhVien = sp.getString(getString(R.string.pre_key_student_id), null);
        String matKhau = sp.getString(getString(R.string.pre_key_password), null);
        String id = getIntent().getBundleExtra(Reference.BUNDLE_EXTRA_NEWS).getString(Reference.BUNDLE_KEY_NEWS_ID);
        String url = Reference.getLoadNoiDungThongBaoApiUrl(maSinhVien, matKhau, id);

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void setData(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type usersType = Types.newParameterizedType(THONGBAO.class);
        JsonAdapter<THONGBAO> jsonAdapter = moshi.adapter(usersType);

        try {
            THONGBAO thongbao = jsonAdapter.fromJson(json);
            String htmlContent = "<div style='text-align: justify'>" + thongbao.getNoiDung() + "</div>";
            tvNoiDung.loadData(htmlContent, "text/html; charset=UTF-8",null);

            // Luu lai cac thong bao moi, de them vao o Main fragment
            Reference.getmListNewThongBao().add(thongbao);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNetworkErrorSnackbar(final boolean show) {
        if (mIsViewDestroyed) return;

        if (show) {
            if (mNotNetworkSnackbar != null && mNotNetworkSnackbar.isShown()) return;

            mNotNetworkSnackbar = CustomSnackbar.createTwoButtonSnackbar(this, rootLayout,
                    getString(R.string.network_not_available),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNotNetworkSnackbar.dismiss();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mLoadTask = new DetailNewsActivity.LoadNewsContentTask();
                            mLoadTask.execute((String) null);
                            mNotNetworkSnackbar.dismiss();
                        }
                    });
            mNotNetworkSnackbar.show();
        } else if (mNotNetworkSnackbar != null) {
            mNotNetworkSnackbar.dismiss();
        }
    }

    private void showErrorSnackbar(final boolean show, final String message) {
        if (mIsViewDestroyed) return;

        if (show) {
            mErrorSnackbar = CustomSnackbar.createTwoButtonSnackbar(this, rootLayout,
                    message,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mErrorSnackbar.dismiss();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mErrorSnackbar.dismiss();
                            mLoadTask = new DetailNewsActivity.LoadNewsContentTask();
                            mLoadTask.execute((String) null);
                        }
                    });
            mErrorSnackbar.show();
        } else if (mErrorSnackbar != null) {
            mErrorSnackbar.dismiss();
        }
    }
}
