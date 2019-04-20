package com.practice.phuc.ums_husc.MessageModule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.phuc.ums_husc.Helper.CustomSnackbar;
import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.JustifyTextInTextView;
import com.practice.phuc.ums_husc.Helper.MyFireBaseMessagingService;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.StringHelper;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Response;

public class DetailMessageActivity extends AppCompatActivity {
    private LoadMessageContentTask mLoadTask;
    private ProgressBar mProgressBar;
    private Snackbar mNotNetworkSnackbar;
    private Snackbar mErrorSnackbar;
    private boolean mIsViewDestroyed;
    private TINNHAN mTinNhan;

    private View rootLayout;
    private TextView tvTieuDe;
    private TextView tvThoiDiemGui;
    private TextView tvNguoiGui;
    private TextView tvNguoiGuiLabel;
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
        tvNguoiGuiLabel = findViewById(R.id.tv_nguoiGuiLabel);
        tvNguoiNhan = findViewById(R.id.tv_nguoiNhan);
        tvNoiDung = findViewById(R.id.tv_noiDung);

        String json = getIntent().getStringExtra(Reference.BUNDLE_EXTRA_MESSAGE);
        mTinNhan = TINNHAN.fromJson(json);
        showData(mTinNhan);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String json = intent.getStringExtra(Reference.BUNDLE_EXTRA_MESSAGE);
        TINNHAN tinNhan = TINNHAN.fromJson(json);
        showData(tinNhan);
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
                Intent intent = new Intent(this, SendMessageActivity.class);
                intent.putExtra(Reference.BUNDLE_EXTRA_MESSAGE, TINNHAN.toJson(mTinNhan));
                intent.putExtra(Reference.BUNDLE_EXTRA_MESSAGE_REPLY, true);
                startActivity(intent);
                break;

            case R.id.item_chuyenTiep:
                Intent intentCT = new Intent(this, SendMessageActivity.class);
                intentCT.putExtra(Reference.BUNDLE_EXTRA_MESSAGE, TINNHAN.toJson(mTinNhan));
                intentCT.putExtra(Reference.BUNDLE_EXTRA_MESSAGE_FORWARD, true);
                startActivity(intentCT);
                break;

            case R.id.item_xoa:
                Toast.makeText(this, "Xoa tin nhan", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void showData(TINNHAN tinNhan) {

        boolean launchFromNotification = getIntent().getBooleanExtra(Reference.BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI, false);

        if (tinNhan == null) return;

        String ngayDang = DateHelper.formatYMDToDMY(tinNhan.ThoiDiemGui.substring(0, 10));
        String gioDang = tinNhan.ThoiDiemGui.substring(11, 16);
        final String thoiDiemGui = ngayDang + " " + gioDang;

        tvTieuDe.setText(tinNhan.TieuDe);
        tvNguoiGui.setText(tinNhan.HoTenNguoiGui);
        tvThoiDiemGui.setText(thoiDiemGui);
        tvNguoiGuiLabel.setText(StringHelper.getFirstCharToCap(tinNhan.HoTenNguoiGui));
        JustifyTextInTextView.justify(tvTieuDe);

        if (!launchFromNotification) {
            showMessageBody(tinNhan);
            mProgressBar.setVisibility(View.GONE);

        } else {
            Log.e("DEBUG", tinNhan.MaTinNhan);
            mLoadTask = new LoadMessageContentTask(tinNhan.MaTinNhan);
            mLoadTask.execute((String) null);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadMessageContentTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;
        private String mErrorMessage;
        private String json, messageId;

        LoadMessageContentTask(String messageId) {
            this.messageId = messageId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (NetworkUtil.getConnectivityStatus(DetailMessageActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED) {
                showNetworkErrorSnackbar(true);
                mLoadTask.cancel(true);
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadTask == null) return false;

            try {
                mResponse = fetchData(messageId);

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
                    Log.e("DEBUG", "Lay tin nhan theo id: " + json);
                    mTinNhan = TINNHAN.fromJson(json);
                    showMessageBody(Objects.requireNonNull(mTinNhan));

                    mProgressBar.setVisibility(View.GONE);

                    showErrorSnackbar(false, "");
                    showNetworkErrorSnackbar(false);
                } else {
                    Log.e("DEBUG", mErrorMessage);
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

    private Response fetchData(String messageId) {
        String maSinhVien = Reference.getStudentId(this);
        String matKhau = Reference.getAccountPassword(this);
        String url = Reference.getLoadNoiDungTinNhanApiUrl(maSinhVien, matKhau, messageId);

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void showMessageBody(final TINNHAN tinNhan) {

        String htmlContent = "<div style='text-align: justify'>" + tinNhan.NoiDung + "</div>";
        tvNoiDung.loadData(htmlContent, "text/html; charset=UTF-8", null);

        String temp = tinNhan.getTenNguoiNhanCollapse();
        tvNguoiNhan.setText(temp);

        final String[] dsTenNguoiNhan = tinNhan.getTenNguoiNhanArray();
        setUpTvNguoiNhan(dsTenNguoiNhan);

        // Luu lai cac tin nhan moi, de them vao o Received fragment
//            Reference.getmListNewThongBao().add(tinnhan);
    }

    private void setUpTvNguoiNhan(final String[] receiverNameList) {
        tvNguoiNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailMessageActivity.this);
                builder.setTitle("Danh sách này có " + receiverNameList.length + " người");
                builder.setItems(receiverNameList, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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
                            mLoadTask = new LoadMessageContentTask(mTinNhan.MaTinNhan);
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
            mErrorSnackbar = CustomSnackbar.createTwoButtonSnackbar(getApplicationContext(), rootLayout,
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
                            mLoadTask = new LoadMessageContentTask(mTinNhan.MaTinNhan);
                            mLoadTask.execute((String) null);
                        }
                    });
            mErrorSnackbar.show();
        } else if (mErrorSnackbar != null) {
            mErrorSnackbar.dismiss();
        }
    }
}
