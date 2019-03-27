package com.practice.phuc.ums_husc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.JustifyTextInTextView;
import com.practice.phuc.ums_husc.Helper.MyFireBaseMessagingService;

public class DetailNewsActivity extends AppCompatActivity {

    private boolean launchFromNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);
        Log.d("DEBUG", "ON CREATE Detail news activity");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Neu mo tu thong bao, thi khi tro ve se tro ve main activity
        launchFromNotification = getIntent().getBundleExtra("news").getBoolean("fromNotification");

        // hien thi chi tiet bai dang
        hienThiBaiDang();
    }

    @Override
    protected void onResume() {
        Log.d("DEBUG", "ON RESUME Detail news activity");
        MyFireBaseMessagingService.context = this;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (launchFromNotification) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void hienThiBaiDang() {
        TextView tvTieuDe = findViewById(R.id.tv_tieuDe);
        TextView tvThoiGianDang = findViewById(R.id.tv_thoiGianDang);
        WebView tvNoiDung = findViewById(R.id.tv_noiDung);

        Bundle bundle = getIntent().getBundleExtra("news");
        tvTieuDe.setText(bundle.getString("title"));
        tvThoiGianDang.setText(bundle.getString("postTime"));
        tvNoiDung.loadData(bundle.getString("body"), "text/html; charset=UTF-8", null);

        JustifyTextInTextView.justify(tvTieuDe);
    }
}
