package com.practice.phuc.ums_husc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

//import com.practice.phuc.ums_husc.Adapter.LinkListViewAdapter;
import com.practice.phuc.ums_husc.Adapter.LinkListViewAdapter;
import com.practice.phuc.ums_husc.Helper.JustifyTextInTextView;

import java.util.ArrayList;
import java.util.List;

public class DetailNewsActivity extends AppCompatActivity {

    // UI
    private RecyclerView rvListLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        rvListLink = findViewById(R.id.lv_lienKet);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvListLink.setLayoutManager(layoutManager);
        rvListLink.setHasFixedSize(true);

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
        JustifyTextInTextView.justify(tvNoiDung);

        List<String> links = new ArrayList<String>();
        links.add("www.google.com.vn");
        links.add("www.youtube.com.vn");

        rvListLink.setAdapter(new LinkListViewAdapter(this, links));
    }
}
