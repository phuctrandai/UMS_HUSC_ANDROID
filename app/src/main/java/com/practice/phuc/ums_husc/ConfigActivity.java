package com.practice.phuc.ums_husc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.fade_out_half);

        String serverAddr = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(this, "server", "address", Reference.ADDRESS);
        String serverPort = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(this, "server", "port", Reference.PORT);

        final EditText etServerAddr = findViewById(R.id.et_server_addr);
        final EditText etServerPort = findViewById(R.id.et_server_port);

        etServerAddr.append(serverAddr);
        etServerPort.append(serverPort);

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addr = etServerAddr.getText().toString();
                String port = etServerPort.getText().toString();
                String host = "http://" + addr + ":" + port + "/";

                SharedPreferenceHelper.getInstance()
                        .setSharedPref(ConfigActivity.this, "server", "address", addr);
                SharedPreferenceHelper.getInstance()
                        .setSharedPref(ConfigActivity.this, "server", "port", port);
                SharedPreferenceHelper.getInstance()
                        .setSharedPref(ConfigActivity.this, "server", "host", host);

                Reference.ADDRESS = addr;
                Reference.PORT = port;
                Reference.HOST = host;

                Toasty.success(ConfigActivity.this, "Lưu cấu hình thành công").show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
