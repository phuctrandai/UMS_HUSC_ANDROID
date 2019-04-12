package com.practice.phuc.ums_husc.MessageModule;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

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
        btnGui.setOnClickListener(sendMessage);

        setUpData();
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
        builder.setMessage("Trở lại và hủy tin nhắn đang soạn ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Đúng rồi", new DialogInterface.OnClickListener() {
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

    private void setUpData() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        Bundle bundle = getIntent().getBundleExtra(Reference.BUNDLE_EXTRA_MESSAGE);
        String tieuDe = bundle.getString(Reference.BUNDLE_KEY_MESSAGE_TITLE, "");
        String nguoiNhan = bundle.getString(Reference.BUNDLE_KEY_MESSAGE_SENDER_NAME, "");
        String hoTenNguoiGui = sp.getString(getString(R.string.pre_key_student_name), null);

        String tieuDeReply = "Re: " + tieuDe;
        etTieuDe.setText(tieuDeReply);
        tvNguoiNhan.setText(nguoiNhan);
        tvNguoiGui.setText(hoTenNguoiGui);
    }

    View.OnClickListener sendMessage = new View.OnClickListener() {
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
            Snackbar.make(layoutRoot, getString(R.string.network_not_available), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ReplyMessageActivity.this);
        builder.setTitle("Thông báo");
        builder.setMessage("Gửi tin nhắn đến " + tvNguoiNhan.getText());
        builder.setCancelable(false);
        builder.setPositiveButton("Gửi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMessage();
            }
        });
        builder.setNegativeButton("Chưa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void sendMessage() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        Bundle bundle = getIntent().getBundleExtra(Reference.BUNDLE_EXTRA_MESSAGE);
        String tieuDe = etTieuDe.getText().toString();
        String noiDung = etNoiDung.getText().toString();
        String maNguoiGui = sp.getString(getString(R.string.pre_key_student_id), "");
        String maNguoiNhan = bundle.getString(Reference.BUNDLE_KEY_MESSAGE_SENDER_ID, "");
        String hoTenNguoiNhan = bundle.getString(Reference.BUNDLE_KEY_MESSAGE_SENDER_NAME, "");
        String hoTenNguoiGui = sp.getString(getString(R.string.pre_key_student_name), "");

        NGUOINHAN nguoiNhan = new NGUOINHAN();
        nguoiNhan.setMaNguoiNhan(maNguoiNhan);
        nguoiNhan.setHoTenNguoiNhan(hoTenNguoiNhan);
        nguoiNhan.setThoiDiemXem("");

        TINNHAN tinNhan = new TINNHAN();
        tinNhan.setTieuDe(tieuDe);
        tinNhan.setNoiDung(noiDung);
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
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            SharedPreferences sp = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
            String maSinhVien = sp.getString(getString(R.string.pre_key_student_id), "");
            String matKhau = sp.getString(getString(R.string.pre_key_password), "");
            String url = Reference.getReplyTinNhanApiUrl(maSinhVien, matKhau);

            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<TINNHAN> jsonAdapter = moshi.adapter(TINNHAN.class);
            String json = jsonAdapter.toJson(tinNhan);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

            mResponse = NetworkUtil.makeRequest(url, true, requestBody);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (mResponse == null) {
                pushNotification("Không thể kết nối đến máy chủ");
                return;
            }

            if (mResponse.code() == NetworkUtil.BAD_REQUEST) {
                Log.d("DEBUG", "Send message: " + mResponse.code());
                pushNotification("Có lỗi xảy ra khi gửi tin nhắn");
                return;
            }

            if (mResponse.code() == NetworkUtil.OK) {
                Log.d("DEBUG", "Send message: " + mResponse.code());
                pushNotification("Đã gửi tin nhắn");
                return;
            }
            try {
                assert mResponse.body() != null;
                Log.d("DEBUG", "Send message: " + mResponse.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }

            pushNotification("Có lỗi xảy ra khi gửi tin nhắn");
        }
    }

    private void pushNotification(final String title) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.share_pre_key_setting), MODE_PRIVATE);
        boolean allow = sp.getBoolean(getString(R.string.share_pre_key_send_message), true);
        if (!allow) return;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.chanel_id));
        mBuilder.setSmallIcon(R.mipmap.noti_icon);
        mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setAutoCancel(true);
        mBuilder.setGroupSummary(true);
        mBuilder.setContentTitle(title);
        mBuilder.setContentIntent(null);
        mBuilder.setGroup(Reference.SEND_MESSAGE_NOTIFICATION);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        int norificationId = Integer.parseInt(String.valueOf(new Date().getTime() / 1000));
        notificationManager.notify(norificationId, mBuilder.build());
    }

}
