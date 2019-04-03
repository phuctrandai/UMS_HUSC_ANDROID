package com.practice.phuc.ums_husc.MessageModule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.phuc.ums_husc.Helper.JustifyTextInTextView;
import com.practice.phuc.ums_husc.Helper.MyFireBaseMessagingService;
import com.practice.phuc.ums_husc.R;

public class DetailMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);
//        Log.d("DEBUG", "ON CREATE Detail message activity");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Chi tiết tin nhắn");

        hienThiTinNhan();
    }

    @Override
    protected void onResume() {
        Log.d("DEBUG", "ON RESUME Detail news activity");
        MyFireBaseMessagingService.mContext = this;
        super.onResume();
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
        switch (id){
            case R.id.item_traLoi:
                Toast.makeText(this, "Tra loi tin nhan", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_xoa:
                Toast.makeText(this,"Xoa tin nhan", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hienThiTinNhan() {
        TextView tvTieuDe = findViewById(R.id.tv_tieuDe);
        TextView tvThoiDiemGui = findViewById(R.id.tv_thoiDiemGui);
        TextView tvNguoiGui = findViewById(R.id.tv_nguoiGui);
        TextView tvNguoiNhan = findViewById(R.id.tv_nguoiNhan);
        WebView tvNoiDung = findViewById(R.id.tv_noiDung);

        Bundle bundle = getIntent().getBundleExtra("news");
        if (bundle != null) {
            tvTieuDe.setText(bundle.getString("title"));
            tvNguoiGui.setText(bundle.getString("sender"));
            tvThoiDiemGui.setText(bundle.getString("postTime"));
            tvNguoiNhan.setText(bundle.getString("receiver"));
            tvNoiDung.loadData(bundle.getString("body"), "text/html; charset=UTF-8", null);

            JustifyTextInTextView.justify(tvTieuDe);
        }
    }
}
