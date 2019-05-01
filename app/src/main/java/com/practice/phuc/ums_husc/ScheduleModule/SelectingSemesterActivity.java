package com.practice.phuc.ums_husc.ScheduleModule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.practice.phuc.ums_husc.Helper.DBHelper;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;
import com.practice.phuc.ums_husc.ViewModel.VHocKy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.Response;

public class SelectingSemesterActivity extends AppCompatActivity {
    private ListView mLvSemester;
    private Button mBtnTacNghiep;
    private ViewGroup mLoadingLayout;

    private List<VHocKy> mSemesters;
    private VHocKy mSelectedItem;
    private String mCurrentSemesterId;
    private int mSelectedPos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecting_semester);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLvSemester = findViewById(R.id.lv_semester);
        mBtnTacNghiep = findViewById(R.id.btn_tacNghiep);
        mLoadingLayout = findViewById(R.id.layout_loading);
        fetchData();
        setUpListView();
        setUpButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSemesters.clear();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (mLoadingLayout.getVisibility() == View.GONE) {
            super.onBackPressed();
        }
    }

    private void fetchData() {
        mSemesters = new ArrayList<>();
        mSemesters.add(new VHocKy("12", "2", "5", "2018", "2019"));
        mSemesters.add(new VHocKy("11", "1", "5", "2018", "2019"));
        mSemesters.add(new VHocKy("10", "3", "4", "2017", "2018"));
        mSemesters.add(new VHocKy("9", "2", "4", "2017", "2018"));
        mSemesters.add(new VHocKy("8", "1", "4", "2017", "2018"));
        mSemesters.add(new VHocKy("7", "3", "3", "2016", "2017"));
        mSemesters.add(new VHocKy("6", "2", "3", "2016", "2017"));
        mSemesters.add(new VHocKy("5", "1", "3", "2016", "2017"));
        mSemesters.add(new VHocKy("3", "3", "1", "2015", "2016"));
        mSemesters.add(new VHocKy("2", "2", "1", "2015", "2016"));
        mSemesters.add(new VHocKy("1", "1", "1", "2015", "2016"));
    }

    private void setUpListView() {
        ArrayAdapter<VHocKy> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, mSemesters);
        mLvSemester.setAdapter(semesterAdapter);

        String semesterId = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER, null);
        int index = Integer.parseInt(semesterId != null ? semesterId : "0");
        for (int i = 0; i < mSemesters.size(); i++) {
            if (mSemesters.get(i).MaHocKy.equals(semesterId)) {
                index = i;
                break;
            }
        }
        mCurrentSemesterId = semesterId;
        mSelectedPos = index;
        mSelectedItem = mSemesters.get(index);
        mLvSemester.setItemChecked(index, true);
        mLvSemester.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedItem = mSemesters.get(position);
                mSelectedPos = position;
            }
        });
    }

    private void setUpButton() {
        mBtnTacNghiep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.getConnectivityStatus(SelectingSemesterActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    Toasty.custom(SelectingSemesterActivity.this, getString(R.string.error_network_disconected),
                            getResources().getDrawable(R.drawable.ic_signal_wifi_off_white_24),
                            getResources().getColor(R.color.colorRed),
                            getResources().getColor(android.R.color.white),
                            Toasty.LENGTH_SHORT, true, true)
                            .show();
                    return;
                }
                new TacNghiepTask(mSelectedItem.MaHocKy).execute();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class TacNghiepTask extends AsyncTask<String, Void, Boolean> {
        String responseMessage;
        String maHocKy;
        String json;

        TacNghiepTask(String maHocKy) {
            this.maHocKy = maHocKy;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            if (mCurrentSemesterId.equals(mSelectedItem.MaHocKy)) {
                responseMessage = "Tác nghiệp thành công";
                return true;
            }

            String url = Reference.HOST + "api/sinhvien/hocky/tacnghiep" +
                    "?masinhvien=" + Reference.getStudentId(SelectingSemesterActivity.this) +
                    "&matkhau=" + Reference.getAccountPassword(SelectingSemesterActivity.this) +
                    "&mahocky=" + maHocKy;
            Response response = NetworkUtil.makeRequest(url, false, null);

            if (response == null) {
                responseMessage = "Không tìm thấy máy chủ";
                return false;
            }

            if (response.code() == NetworkUtil.OK) {
                responseMessage = "Tác nghiệp thành công";
                try {
                    json = response.body() != null ? response.body().string() : "";
                    return true;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            responseMessage = "Có lỗi xảy ra, thử lại sau";
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                mCurrentSemesterId = maHocKy;
                Toasty.success(SelectingSemesterActivity.this, responseMessage, Toasty.LENGTH_SHORT).show();
                SharedPreferenceHelper.getInstance()
                        .setSharedPref(SelectingSemesterActivity.this,
                                SharedPreferenceHelper.ACCOUNT_SP,
                                SharedPreferenceHelper.STUDENT_SEMSTER, maHocKy);
                SharedPreferenceHelper.getInstance()
                        .setSharedPref(SelectingSemesterActivity.this,
                                SharedPreferenceHelper.ACCOUNT_SP,
                                SharedPreferenceHelper.STUDENT_SEMSTER_STR, mSelectedItem.toString());

                Intent intent = new Intent();
                intent.putExtra("isSemesterFinished", mSelectedPos != 0);
                intent.putExtra("semesterStr", mSelectedItem.toString());
                SelectingSemesterActivity.this.setResult(Activity.RESULT_OK, intent);

                List<ThoiKhoaBieu> thoiKhoaBieus = ThoiKhoaBieu.fromJsonToList(json);
                DBHelper dbHelper = new DBHelper(SelectingSemesterActivity.this);
                dbHelper.deleteAllRecord(DBHelper.SCHEDULE);
                ScheduleFragment.mIsChangeSemester = true;

                if (thoiKhoaBieus != null && thoiKhoaBieus.size() > 0) {
                    dbHelper.insertSchedule(thoiKhoaBieus);
                }
            } else {
                Toasty.error(SelectingSemesterActivity.this, responseMessage, Toasty.LENGTH_SHORT).show();
            }

            mLoadingLayout.setVisibility(View.GONE);
        }
    }
}
