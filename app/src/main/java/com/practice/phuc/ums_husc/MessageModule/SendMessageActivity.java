package com.practice.phuc.ums_husc.MessageModule;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Adapter.ReceiverAdapter;
import com.practice.phuc.ums_husc.Adapter.SearchAccountAdapter;
import com.practice.phuc.ums_husc.Helper.DBHelper;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.NGUOINHAN;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.TaiKhoan;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendMessageActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private TextView tvNguoiGui;
    private EditText etTieuDe;
    private EditText etNoiDung;
    private RecyclerView rvSearchResult;
    private ProgressBar pbLoading;
    private GridView gvReceiver;

    private DBHelper mDBHelper;
    private SearchAccountAdapter mSearchAdapter;
    private ReceiverAdapter mReceiverAdapter;
    private TINNHAN mTinNhan;
    private int mCurrentPage;
    private int ITEM_PER_PAGE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        etTieuDe = findViewById(R.id.tv_tieuDe);
        etNoiDung = findViewById(R.id.et_noiDung);
        pbLoading = findViewById(R.id.pb_loading);
        tvNguoiGui = findViewById(R.id.tv_nguoiGui);
        gvReceiver = findViewById(R.id.gv_receiver);
        rvSearchResult = findViewById(R.id.rv_searchResult);
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        ImageButton btnOpenSlidingPanel = findViewById(R.id.btn_addReceiver);
        ImageButton btnCloseSlidingPanel = findViewById(R.id.btn_closeSlidePanel);
        SearchView svSearch = findViewById(R.id.sv_query);
        svSearch.onActionViewExpanded();
        svSearch.setOnQueryTextListener(this);
        svSearch.setActivated(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mCurrentPage = 1;
        ITEM_PER_PAGE = 15;
        mDBHelper = new DBHelper(this);

        boolean isNew = getIntent().getBooleanExtra(Reference.BUNDLE_EXTRA_MESSAGE_NEW, false);
        boolean isReply = getIntent().getBooleanExtra(Reference.BUNDLE_EXTRA_MESSAGE_REPLY, false);
        boolean isForward = getIntent().getBooleanExtra(Reference.BUNDLE_EXTRA_MESSAGE_FORWARD, false);

        int MODE;
        if (isNew) {
            MODE = 0;
            getSupportActionBar().setTitle("Gửi tin mới");
            etTieuDe.requestFocus();

        } else if (isReply) {
            MODE = 1;
            getSupportActionBar().setTitle("Trả lời");
            etNoiDung.requestFocus();

        } else if (isForward) {
            MODE = 2;
            getSupportActionBar().setTitle("Chuyển tiếp");
            etNoiDung.requestFocus();

        } else
            MODE = 0;

        setUpGridView();

        setUpRecyclerView();

        mTinNhan = setUpData(MODE);

        btnOpenSlidingPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
        btnCloseSlidingPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reply_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send_message) {
            attempSendMessage();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (etNoiDung.getText() == null || etNoiDung.getText().toString().equals("")) {
            super.onBackPressed();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Hủy tin nhắn này ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendMessageActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Thôi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mSearchAdapter.onFilter(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        new SearchAccountTask().execute(query);
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    class SearchAccountTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;
        private String mMessage;
        private String mResponseBody;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
            rvSearchResult.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String url = Reference.getSearchTaiKhoan(strings[0], mCurrentPage, ITEM_PER_PAGE);

            mResponse = NetworkUtil.makeRequest(url, false, null);

            if (mResponse == null) {
                mMessage = getString(R.string.error_server_not_response);
                Log.e("DEBUG", "Search account RESPONSE NULL");
                return false;

            } else {

                if (mResponse.code() == NetworkUtil.OK) {
                    try {
                        mResponseBody = mResponse.body() != null ? mResponse.body().string() : "";
                        Log.e("DEBUG", mResponseBody);
                        return true;

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("DEBUG", "Có lỗi bất ngờ khi tìm kiếm tài khoản");
                        return false;
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            pbLoading.setVisibility(View.GONE);
            rvSearchResult.setVisibility(View.VISIBLE);

            if (success) {
                List<TaiKhoan> taiKhoans = TaiKhoan.fromJson(mResponseBody);

                if (taiKhoans != null && taiKhoans.size() > 0) {
                    mSearchAdapter.onNotifySearchResultChanged(taiKhoans);
                }
            } else {
                Log.e("DEBUG", mMessage);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SendMessageTask extends AsyncTask<String, Void, Boolean> {

        Response mResponse;
        String mMessage;
        TINNHAN tinNhan;

        SendMessageTask(TINNHAN tinNhan) {
            this.tinNhan = tinNhan;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String maSinhVien = Reference.getStudentId(SendMessageActivity.this);
            String matKhau = Reference.getAccountPassword(SendMessageActivity.this);
            String url = Reference.getReplyTinNhanApiUrl(maSinhVien, matKhau);

            String json = TINNHAN.toJson(tinNhan);
            Log.e("DEBUG", "Tin nhan gui di: " + json);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            mResponse = NetworkUtil.makeRequest(url, true, requestBody);

            if (mResponse == null) {
                mMessage = "Không thể kết nối đến máy chủ";
                return false;
            }

            if (mResponse.code() == NetworkUtil.BAD_REQUEST) {
                Log.e("DEBUG", "Send message: " + mResponse.code());
                mMessage = "Thông tin người gửi không đúng";
                return false;
            }

            if (mResponse.code() == NetworkUtil.OK) {
                Log.e("DEBUG", "Send message: " + mResponse.code());
                mMessage = "Đã gửi tin nhắn";
                return true;
            }
            try {
                assert mResponse.body() != null;
                Log.e("DEBUG", "Send message: " + mResponse.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMessage = "Có lỗi xảy ra khi gửi tin nhắn";
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            pushNotification(mMessage);
        }

    }

    private TINNHAN setUpData(int mode) {
        String json = getIntent().getStringExtra(Reference.BUNDLE_EXTRA_MESSAGE);
        TINNHAN current = json != null ? TINNHAN.fromJson(json) : null;
        TINNHAN toSent = new TINNHAN();

        String tieuDe = "";
        String maNguoiGui = Reference.getAccountId(this);
        String hoTenNguoiGui = Reference.getStudentName(this);

        if (current != null && current.MaNguoiGui.equals(maNguoiGui) && mode != 2)
            mode = 4;

        if (mode == 1 && current != null) { // Reply
            tieuDe = current.TieuDe.contains("Re: ") ? current.TieuDe : "Re: " + current.TieuDe;
            mReceiverAdapter.updateReceiverList(new TaiKhoan(/*Ma nguoi nhan:*/ current.MaNguoiGui, current.HoTenNguoiGui));

        } else if (mode == 2 && current != null) { // Forward
            tieuDe = current.TieuDe.contains("Fwd: ") ? current.TieuDe : "Fwd: " + current.TieuDe;

        } else if (mode == 4 && current != null && current.NguoiNhans != null) { // Reply sent message
            tieuDe = current.TieuDe.contains("Re: ") ? current.TieuDe : "Re: " + current.TieuDe;

            for (int i = 0; i < current.NguoiNhans.length; i++) {
                mReceiverAdapter.insertReceiver(new TaiKhoan(
                        current.NguoiNhans[i].MaNguoiNhan,
                        current.NguoiNhans[i].HoTenNguoiNhan
                ));
            }
            mReceiverAdapter.notifyDataSetChanged();
        }

        etTieuDe.setText(tieuDe);
        tvNguoiGui.setText(hoTenNguoiGui);

        toSent.TieuDe = tieuDe;
        toSent.MaNguoiGui = maNguoiGui;
        toSent.HoTenNguoiGui = hoTenNguoiGui;
        toSent.ThoiDiemGui = "";

        return toSent;
    }

    private void attempSendMessage() {
        if (mReceiverAdapter.getCount() == 0) {
            Toasty.error(this, "Phải có ít nhất một người nhận", Toasty.LENGTH_SHORT, true).show();
            return;
        }

        if (etTieuDe.getText() == null || etTieuDe.getText().toString().equals("")) {
            Toasty.error(this,"Tiêu đề không được trống", Toasty.LENGTH_LONG, true).show();
            etTieuDe.requestFocus();
            return;
        }

        if (etNoiDung.getText() == null || etNoiDung.getText().toString().equals("")) {
            Toasty.error(this,"Nội dung không được trống", Toasty.LENGTH_LONG, true).show();
            etNoiDung.requestFocus();
            return;
        }

        if (NetworkUtil.getConnectivityStatus(this) == NetworkUtil.TYPE_NOT_CONNECTED) {
            Toasty.custom(this, getString(R.string.error_network_disconected),
                    getResources().getDrawable(R.drawable.ic_signal_wifi_off_white_24),
                    getResources().getColor(R.color.colorRed),
                    getResources().getColor(android.R.color.white),
                    Toasty.LENGTH_LONG, true, true)
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
        builder.setTitle("Thông báo");
        builder.setMessage("Gửi tin nhắn ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMessage(etTieuDe.getText().toString(), etNoiDung.getText().toString());
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void sendMessage(String title, String content) {
        mTinNhan.TieuDe = title;
        mTinNhan.NoiDung = content;

        mTinNhan.NguoiNhans = new NGUOINHAN[mReceiverAdapter.getCount()];
        int i = 0;
        for (TaiKhoan item : mReceiverAdapter.getReceiverList()) {
            NGUOINHAN n = new NGUOINHAN(item.MaTaiKhoan, item.HoTen, "");
            mTinNhan.NguoiNhans[i] = n;
            i++;
        }
        Reference.mHasNewSentMessage = true;
        Reference.getListNewSentMessage().add(mTinNhan);
        SendMessageTask sendMessageTask = new SendMessageTask(mTinNhan);
        sendMessageTask.execute((String) null);
        this.finish();
    }

    private void setUpGridView() {
        mReceiverAdapter = new ReceiverAdapter(this);
        gvReceiver.setAdapter(mReceiverAdapter);
    }

    private void setUpRecyclerView() {
        List<TaiKhoan> temp = mDBHelper.getAllAccount();
        mSearchAdapter = new SearchAccountAdapter(this, temp, mReceiverAdapter);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResult.setHasFixedSize(true);
        rvSearchResult.setAdapter(mSearchAdapter);
    }

    private void pushNotification(final String title) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.share_pre_key_setting), MODE_PRIVATE);
        boolean allow = sp.getBoolean(getString(R.string.share_pre_key_send_message), true);
        if (!allow) return;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.chanel_id));
        mBuilder.setContentTitle(title);
        mBuilder.setSmallIcon(R.mipmap.noti_icon);
        mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setAutoCancel(true);
        mBuilder.setGroupSummary(true);
        mBuilder.setContentIntent(null);
        mBuilder.setSound(Uri.EMPTY);
        mBuilder.setGroup(Reference.SEND_MESSAGE_NOTIFICATION);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        int norificationId = Integer.parseInt(String.valueOf(new Date().getTime() / 1000));
        notificationManager.notify(norificationId, mBuilder.build());
    }

}
