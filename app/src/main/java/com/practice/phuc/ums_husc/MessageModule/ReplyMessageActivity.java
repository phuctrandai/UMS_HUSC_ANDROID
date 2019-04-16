package com.practice.phuc.ums_husc.MessageModule;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.NGUOINHAN;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReplyMessageActivity extends AppCompatActivity {

    private ViewGroup layoutRoot;
    private TextView tvNguoiNhan;
    private TextView tvNguoiGui;
    private EditText etTieuDe;
    private EditText etNoiDung;

    private TINNHAN mTinNhan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        layoutRoot = findViewById(R.id.layout_root_reply_message);
        etTieuDe = findViewById(R.id.tv_tieuDe);
        tvNguoiNhan = findViewById(R.id.tv_nguoiNhan);
        tvNguoiGui = findViewById(R.id.tv_nguoiGui);
        etNoiDung = findViewById(R.id.et_noiDung);
        etNoiDung.requestFocus();
        Button btnGui = findViewById(R.id.btn_gui);
        btnGui.setOnClickListener(sendMessageClickListener);

        String json = getIntent().getStringExtra(Reference.BUNDLE_EXTRA_MESSAGE);
        mTinNhan = TINNHAN.fromJson(json);
        Log.d("DEBUG", "Reply tin nhan: " + json);

        setUpData(mTinNhan);
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
                ReplyMessageActivity.super.onBackPressed();
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

    private void setUpData(TINNHAN tinNhan) {
        if (tinNhan == null) return;

        String tieuDe = tinNhan.getTieuDe();
        String hoTenNguoiNhan = tinNhan.getHoTenNguoiGui();
        String hoTenNguoiGui = Reference.getAccountName(this);

        String tieuDeReply = tieuDe.contains("Re: ") ? tieuDe : "Re: " + tieuDe;
        etTieuDe.setText(tieuDeReply);
        tvNguoiNhan.setText(hoTenNguoiNhan);
        tvNguoiGui.setText(hoTenNguoiGui);
    }

    View.OnClickListener sendMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            attempSendMessage();
        }
    };

    private void attempSendMessage() {
        if (etTieuDe.getText() == null || etTieuDe.getText().toString().equals("")) {
            etTieuDe.setError("Tiêu đề không được trống");
            etTieuDe.requestFocus();
            return;
        }

        if (etNoiDung.getText() == null || etNoiDung.getText().toString().equals("")) {
            etNoiDung.setError("Nội dung không được trống");
            etNoiDung.requestFocus();
            return;
        }

        if (NetworkUtil.getConnectivityStatus(this) == NetworkUtil.TYPE_NOT_CONNECTED) {
            Snackbar.make(layoutRoot, getString(R.string.error_network_disconected), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ReplyMessageActivity.this);
        builder.setTitle("Thông báo");
        builder.setMessage("Gửi tin nhắn đến " + tvNguoiNhan.getText());
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
        String maNguoiGui = Reference.getAccountId(this);
        String hoTenNguoiGui = Reference.getAccountName(this);
        String maNguoiNhan = mTinNhan.getMaNguoiGui();
        String hoTenNguoiNhan = mTinNhan.getHoTenNguoiGui();

        NGUOINHAN nguoiNhan = new NGUOINHAN();
        nguoiNhan.setMaNguoiNhan(maNguoiNhan);
        nguoiNhan.setHoTenNguoiNhan(hoTenNguoiNhan);
        nguoiNhan.setThoiDiemXem("");

        TINNHAN tinNhan = new TINNHAN();
        tinNhan.setTieuDe(title);
        tinNhan.setNoiDung(content);
        tinNhan.setThoiDiemGui("");
        tinNhan.setMaNguoiGui(maNguoiGui);
        tinNhan.setHoTenNguoiGui(hoTenNguoiGui);
        tinNhan.setNGUOINHANs(new NGUOINHAN[]{ nguoiNhan });

        SendMessageTask sendMessageTask = new SendMessageTask(tinNhan);
        sendMessageTask.execute((String) null);
        this.finish();
    }

    @SuppressLint("StaticFieldLeak")
    class SendMessageTask extends AsyncTask<String, Void, Boolean> {
        Response mResponse;
        TINNHAN tinNhan;

        SendMessageTask(TINNHAN tinNhan) {
            this.tinNhan = tinNhan;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String maSinhVien = Reference.getAccountId(ReplyMessageActivity.this);
            String matKhau = Reference.getAccountPassword(ReplyMessageActivity.this);
            String url = Reference.getReplyTinNhanApiUrl(maSinhVien, matKhau);

            String json = TINNHAN.toJson(tinNhan);
            Log.d("DEBUG", "Tin nhan gui di: " + json);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

            mResponse = NetworkUtil.makeRequest(url, true, requestBody);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (mResponse == null) {
                pushNotification("Không thể kết nối đến máy chủ", 0);
                return;
            }

            if (mResponse.code() == NetworkUtil.BAD_REQUEST) {
                Log.d("DEBUG", "Send message: " + mResponse.code());
                pushNotification("Thông tin người gửi không đúng", 0);
                return;
            }

            if (mResponse.code() == NetworkUtil.OK) {
                Log.d("DEBUG", "Send message: " + mResponse.code());
                pushNotification("Đã gửi tin nhắn", 5000);
                return;
            }
            try {
                assert mResponse.body() != null;
                Log.d("DEBUG", "Send message: " + mResponse.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }

            pushNotification("Có lỗi xảy ra khi gửi tin nhắn", 0);
        }
    }

    private void pushNotification(final String title, long timeOut) {
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
        if (timeOut > 0) {
            mBuilder.setAutoCancel(true);
            mBuilder.setTimeoutAfter(timeOut);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        int norificationId = Integer.parseInt(String.valueOf(new Date().getTime() / 1000));
        notificationManager.notify(norificationId, mBuilder.build());
    }

}
