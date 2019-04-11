package com.practice.phuc.ums_husc.MessageModule;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.R;

import java.util.Objects;

public class ReplyMessageActivity extends AppCompatActivity {

    private TextView tvTieuDe;
    private TextView tvNguoiNhan;
    private TextView tvNguoiGui;
    private EditText etNoiDung;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvTieuDe = findViewById(R.id.tv_tieuDe);
        tvNguoiNhan = findViewById(R.id.tv_nguoiNhan);
        tvNguoiGui = findViewById(R.id.tv_nguoiGui);
        etNoiDung = findViewById(R.id.et_noiDung);
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
        tvTieuDe.setText(tieuDeReply);
        tvNguoiNhan.setText(nguoiNhan);
        tvNguoiGui.setText(hoTenNguoiGui);
    }

    View.OnClickListener sendMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReplyMessageActivity.this);
            builder.setTitle("Thông báo");
            builder.setMessage("Gửi tin nhắn đến " + tvNguoiNhan.getText());
            builder.setCancelable(false);
            builder.setPositiveButton("Gửi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String noiDung = etNoiDung.getText().toString();
                    Log.d("DEBUG", noiDung);
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
    };
}
