package com.practice.phuc.ums_husc.MessageModule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.phuc.ums_husc.Helper.CustomSnackbar;
import com.practice.phuc.ums_husc.Helper.JustifyTextInTextView;
import com.practice.phuc.ums_husc.Helper.MyFireBaseMessagingService;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

import okhttp3.Response;

public class DetailMessageActivity extends AppCompatActivity {

    private LoadMessageContentTask mLoadTask;
    private ProgressBar mProgressBar;
    private Snackbar mNotNetworkSnackbar;
    private Snackbar mErrorSnackbar;
    private boolean mIsViewDestroyed;

    private View rootLayout;
    private TextView tvTieuDe;
    private TextView tvThoiDiemGui;
    private TextView tvNguoiGui;
    private TextView tvNguoiNhan;
    private WebView tvNoiDung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rootLayout = findViewById(R.id.layout_detail_message_root);
        mProgressBar = findViewById(R.id.progressBar);
        tvTieuDe = findViewById(R.id.tv_tieuDe);
        tvThoiDiemGui = findViewById(R.id.tv_thoiDiemGui);
        tvNguoiGui = findViewById(R.id.tv_nguoiGui);
        tvNguoiNhan = findViewById(R.id.tv_nguoiNhan);
        tvNoiDung = findViewById(R.id.tv_noiDung);

        showData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        showData();
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        MyFireBaseMessagingService.mContext = this;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mIsViewDestroyed = true;
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
            mLoadTask = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item_traLoi:
                replyMessage();
                break;
            case R.id.item_xoa:
                Toast.makeText(this, "Xoa tin nhan", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showData() {
        Bundle bundle = getIntent().getBundleExtra(Reference.BUNDLE_EXTRA_MESSAGE);
        boolean launchFromNotification = bundle.getBoolean(Reference.BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI, false);
        tvTieuDe.setText(bundle.getString(Reference.BUNDLE_KEY_MESSAGE_TITLE));
        tvNguoiGui.setText(bundle.getString(Reference.BUNDLE_KEY_MESSAGE_SENDER_NAME));
        tvThoiDiemGui.setText(bundle.getString(Reference.BUNDLE_KEY_MESSAGE_SEND_TIME));
        JustifyTextInTextView.justify(tvTieuDe);

        if (launchFromNotification) {
            mLoadTask = new LoadMessageContentTask();
            mLoadTask.execute((String) null);
        } else {
            String htmlContent = "<div style='text-align: justify'>"
                    + bundle.getString(Reference.BUNDLE_KEY_MESSAGE_BODY) + "</div>";
            tvNoiDung.loadData(htmlContent,"text/html; charset=UTF-8", null);
            tvNguoiNhan.setText(bundle.getString(Reference.BUNDLE_KEY_MESSAGE_RECEIVERS));
            String[] dsTenNguoiNhan = bundle.getStringArray(Reference.BUNDLE_KEY_MESSAGE_RECEIVER_NAMES);
            setUpTvNguoiNhan(dsTenNguoiNhan);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void setUpTvNguoiNhan(final String[] receiverNameList) {
        tvNguoiNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailMessageActivity.this);
                builder.setTitle("Có " + receiverNameList.length + " người");
                builder.setItems(receiverNameList, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadMessageContentTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;
        private String mErrorMessage;
        private String json;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvNoiDung.setVisibility(View.GONE);
            if (NetworkUtil.getConnectivityStatus(DetailMessageActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED) {
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
                    mErrorMessage = getString(R.string.error_server_not_response);
                    return false;
                } else {
                    if (mResponse.code() == NetworkUtil.OK) {
                        json = mResponse.body() != null ? mResponse.body().string() : "";
                        return true;

                    } else if (mResponse.code() == NetworkUtil.BAD_REQUEST) {
                        mErrorMessage = mResponse.body() != null ? mResponse.body().string() : "";
                    } else {
                        mErrorMessage = getString(R.string.error_time_out);
                    }
                    return false;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (mLoadTask != null) {
                if (success) {
                    setData(json);
                    mProgressBar.setVisibility(View.GONE);
                    tvNoiDung.setVisibility(View.VISIBLE);
                    showErrorSnackbar(false, "");
                    showNetworkErrorSnackbar(false);
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
        String id = getIntent().getBundleExtra(Reference.BUNDLE_EXTRA_MESSAGE).getString(Reference.BUNDLE_KEY_MESSAGE_ID);
        String url = Reference.getLoadNoiDungTinNhanApiUrl(maSinhVien, matKhau, id);

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void setData(final String json) {
        Moshi mMoshi = new Moshi.Builder().build();
        Type mUsersType = Types.newParameterizedType(TINNHAN.class);
        JsonAdapter<TINNHAN> mJsonAdapter = mMoshi.adapter(mUsersType);

        try {
            TINNHAN tinnhan = mJsonAdapter.fromJson(json);
            String htmlContent = "<div style='text-align: justify'>" + tinnhan.getNoiDung() + "</div>";
            tvNoiDung.loadData(htmlContent, "text/html; charset=UTF-8", null);

            if (tinnhan.getNGUOINHANs() != null) {
                String temp = "";
                if (tinnhan.getNGUOINHANs().length > 0)
                    temp = tinnhan.getNGUOINHANs()[(0)].getHoTenNguoiNhan();
                if (tinnhan.getNGUOINHANs().length > 1)
                    temp += " và " + (tinnhan.getNGUOINHANs().length - 1) + " người khác (xem thêm)";
                tvNguoiNhan.setText(temp);
                final String[] dsTenNguoiNhan = new String[tinnhan.getNGUOINHANs().length];
                for (int j = 0; j < tinnhan.getNGUOINHANs().length; j++) {
                    dsTenNguoiNhan[j] = tinnhan.getNGUOINHANs()[j].getHoTenNguoiNhan();
                }
                setUpTvNguoiNhan(dsTenNguoiNhan);
            }

            // Luu lai cac tin nhan moi, de them vao o Received fragment
//            Reference.getmListNewThongBao().add(tinnhan);
        } catch (IOException | ClassCastException e) {
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
                            mLoadTask = new LoadMessageContentTask();
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
                            mLoadTask = new LoadMessageContentTask();
                            mLoadTask.execute((String) null);
                        }
                    });
            mErrorSnackbar.show();
        } else if (mErrorSnackbar != null) {
            mErrorSnackbar.dismiss();
        }
    }

    private void replyMessage() {
        Bundle bundle = getIntent().getBundleExtra(Reference.BUNDLE_EXTRA_MESSAGE);

        Intent intent = new Intent(this, ReplyMessageActivity.class);
        intent.putExtra(Reference.BUNDLE_EXTRA_MESSAGE, bundle);
        startActivity(intent);
    }
}
