package com.practice.phuc.ums_husc.NewsModule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.CustomSnackbar;
import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.JustifyTextInTextView;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.MainActivity;
import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.Service.MyFireBaseMessagingService;

import java.util.Objects;

import okhttp3.Response;

public class DetailNewsActivity extends AppCompatActivity {
    private View rootLayout;
    private TextView tvTieuDe;
    private TextView tvThoiGianDang;
    private WebView tvNoiDung;
    private ProgressBar progressBar;

    private boolean mAutoAddNewNews;
    private boolean mIsViewDestroyed;
    private LoadNewsContentTask mLoadTask;
    private Snackbar mNotNetworkSnackbar;
    private Snackbar mErrorSnackbar;
    private THONGBAO mThongBao;

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

        String json = getIntent().getStringExtra(Reference.getInstance().BUNDLE_EXTRA_NEWS);
        mThongBao = THONGBAO.fromJson(json);
        showData(Objects.requireNonNull(mThongBao));
        mAutoAddNewNews = getIntent().getBooleanExtra("auto_add_new_news", true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String json = getIntent().getStringExtra(Reference.getInstance().BUNDLE_EXTRA_NEWS);
        mThongBao = THONGBAO.fromJson(json);
        if (mThongBao != null) {
            showData(mThongBao);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyFireBaseMessagingService.mContext = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
            mLoadTask = null;
        }
        mIsViewDestroyed = true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showData(THONGBAO thongBao) {
        boolean launchFromNotification = getIntent()
                .getBooleanExtra(Reference.getInstance().BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, false);
        String thoiGianDang = thongBao.getThoiGianDang();
        String ngayDang = DateHelper.formatYMDToDMY(thoiGianDang.substring(0, 10));
        String gioDang = thoiGianDang.substring(11, 16);
        String thoiGianDangStr = ngayDang + " " + gioDang;

        tvThoiGianDang.setText(thoiGianDangStr);
        tvTieuDe.setText(thongBao.getTieuDe());
        JustifyTextInTextView.justify(tvTieuDe);

        if (launchFromNotification) {
            mLoadTask = new LoadNewsContentTask(thongBao.getMaThongBao() + "");
            mLoadTask.execute((String) null);

        } else {
            progressBar.setVisibility(View.GONE);
            showBodyNews(thongBao);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadNewsContentTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;
        private String mErrorMessage;
        private String json;
        private String newsId;

        LoadNewsContentTask(String newsId) {
            this.newsId = newsId;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            if (NetworkUtil.getConnectivityStatus(DetailNewsActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED) {
                showNetworkErrorSnackbar(true);
                mLoadTask.cancel(true);
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadTask == null) return false;

            try {
                mResponse = fetchData(newsId);

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
                    mThongBao = THONGBAO.fromJson(json);
                    showBodyNews(Objects.requireNonNull(mThongBao));

                    // Luu lai cac thong bao moi, de them vao o Main fragment
                    if (mAutoAddNewNews && MainActivity.mIsCreated) {
                        Reference.getInstance().mHasNewNews = true;
                        Reference.getInstance().getmListNewThongBao().add(mThongBao);
                    }

                    progressBar.setVisibility(View.GONE);
                    showNetworkErrorSnackbar(false);
                    showErrorSnackbar(false, "");
                } else {
                    Log.d("DEBUG", mErrorMessage);
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

    private Response fetchData(String newsId) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        String maSinhVien = sp.getString(getString(R.string.pre_key_student_id), null);
        String matKhau = sp.getString(getString(R.string.pre_key_password), null);
        String url = Reference.getInstance()
                . getHost(this) + "api/ThongBao/ChiTiet/"
                + "?masinhvien=" + maSinhVien
                + "&matkhau=" + matKhau
                + "&id=" + newsId;

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void showBodyNews(THONGBAO thongBao) {
        String htmlContent = "<div style='text-align: justify'> " + thongBao.getNoiDung() + " </div>";
        tvNoiDung.setAlpha(0.0f);
        tvNoiDung.loadData(htmlContent, "text/html; charset=UTF-8", null);
        tvNoiDung.refreshDrawableState();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvNoiDung.setAlpha(1.0f);
            }
        }, 1000);
    }

    private void showNetworkErrorSnackbar(final boolean show) {
        if (mIsViewDestroyed) return;

        if (show) {
            if (mNotNetworkSnackbar != null && mNotNetworkSnackbar.isShown()) return;

            mNotNetworkSnackbar = CustomSnackbar.createTwoButtonSnackbar(this, rootLayout,
                    getString(R.string.error_network_disconected), Snackbar.LENGTH_INDEFINITE,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNotNetworkSnackbar.dismiss();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mLoadTask = new DetailNewsActivity.LoadNewsContentTask(mThongBao.getMaThongBao() + "");
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
                    message, Snackbar.LENGTH_INDEFINITE,
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
                            mLoadTask = new DetailNewsActivity.LoadNewsContentTask(mThongBao.getMaThongBao() + "");
                            mLoadTask.execute((String) null);
                        }
                    });
            mErrorSnackbar.show();
        } else if (mErrorSnackbar != null) {
            mErrorSnackbar.dismiss();
        }
    }
}
