package com.practice.phuc.ums_husc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class DetailNewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        hienThiBaiDang();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void hienThiBaiDang() {
        TextView tvTieuDe = findViewById(R.id.tv_tieuDe);
        TextView tvThoiGianDang = findViewById(R.id.tv_thoiGianDang);
        TextView tvNoiDung = findViewById(R.id.tv_noiDung);

        Bundle bundle = this.getIntent().getBundleExtra("baiDang");
        tvTieuDe.setText(bundle.getString("tieuDe"));
        tvThoiGianDang.setText(bundle.getString("thoiGianDang"));
        tvNoiDung.setText(bundle.getString("noiDung"));
    }
}
