package com.practice.phuc.ums_husc.ScheduleModule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.practice.phuc.ums_husc.Helper.DBHelper;
import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;
import com.practice.phuc.ums_husc.ViewModel.VHocKy;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.Response;

import static com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper.ACCOUNT_PASSWORD;
import static com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper.ACCOUNT_SP;
import static com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper.STUDENT_ID;

public class SelectingSemesterActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mLvSemester;
    private Button mBtnTacNghiep;
    private ProgressBar mPBLoading;

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
        mPBLoading = findViewById(R.id.pb_loading);
        mSwipeRefreshLayout = findViewById(R.id.layout_swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        fetchData();
        setUpButton();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (mPBLoading.getVisibility() == View.GONE) {
            super.onBackPressed();
        }
    }

    private void fetchData() {
        if (NetworkUtil.getConnectivityStatus(this) == NetworkUtil.TYPE_NOT_CONNECTED) {
            Toasty.custom(SelectingSemesterActivity.this, getString(R.string.error_network_disconected),
                    getResources().getDrawable(R.drawable.ic_signal_wifi_off_white_24),
                    getResources().getColor(R.color.colorRed),
                    getResources().getColor(android.R.color.white),
                    Toasty.LENGTH_SHORT, true, true)
                    .show();
        } else {
            new FetchSemesterTask().execute((String) null);
        }
    }

    private void setUpListView(final List<VHocKy> semesters) {
        ArrayAdapter<VHocKy> semesterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice, semesters);
        mLvSemester.setAdapter(semesterAdapter);

        String semesterId = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(this, ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER, null);
        int index = Integer.parseInt(semesterId != null ? semesterId : "0");
        for (int i = 0; i < semesters.size(); i++) {
            if (semesters.get(i).MaHocKy.equals(semesterId)) {
                index = i;
                break;
            }
        }
        mCurrentSemesterId = semesterId;
        mSelectedPos = index;
        mSelectedItem = semesters.get(index);
        mLvSemester.setItemChecked(index, true);
        mLvSemester.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedItem = semesters.get(position);
                mSelectedPos = position;
            }
        });
        mLvSemester.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLvSemester.getChildAt(0) != null) {
                    mSwipeRefreshLayout.setEnabled(mLvSemester.getFirstVisiblePosition() == 0
                            && mLvSemester.getChildAt(0).getTop() == 0);
                }
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

                if (mSelectedItem != null) {
                    new TacNghiepTask(mSelectedItem.MaHocKy).execute();
                }
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
            mPBLoading.setVisibility(View.VISIBLE);
            mBtnTacNghiep.setText("");
            mBtnTacNghiep.refreshDrawableState();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            if (mCurrentSemesterId.equals(mSelectedItem.MaHocKy)) {
                responseMessage = "Tác nghiệp thành công";
                return true;
            }

            String url = Reference.HOST + "api/sinhvien/hocky/tacnghiep" +
                    "?masinhvien=" + SharedPreferenceHelper.getInstance()
                    .getSharedPrefStr(SelectingSemesterActivity.this, ACCOUNT_SP, STUDENT_ID, "") +
                    "&matkhau=" + SharedPreferenceHelper.getInstance()
                    .getSharedPrefStr(SelectingSemesterActivity.this, ACCOUNT_SP, ACCOUNT_PASSWORD, "") +
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
        protected void onPostExecute(final Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                mCurrentSemesterId = maHocKy;
                SharedPreferenceHelper.getInstance().setSharedPref(SelectingSemesterActivity.this, ACCOUNT_SP,
                        SharedPreferenceHelper.STUDENT_SEMSTER, maHocKy);
                SharedPreferenceHelper.getInstance().setSharedPref(SelectingSemesterActivity.this, ACCOUNT_SP,
                        SharedPreferenceHelper.STUDENT_SEMSTER_STR, mSelectedItem.toString());

                Intent intent = new Intent();
                intent.putExtra("semesterStr", mSelectedItem.toString());
                SelectingSemesterActivity.this.setResult(Activity.RESULT_OK, intent);

                List<ThoiKhoaBieu> thoiKhoaBieus = ThoiKhoaBieu.fromJsonToList(json);
                DBHelper dbHelper = new DBHelper(SelectingSemesterActivity.this);
                dbHelper.deleteAllRecord(DBHelper.SCHEDULE);
                ScheduleFragment.mIsLastSemester = mSelectedPos == 0;
                ScheduleFragment.mIsChangeSemester = true;

                if (thoiKhoaBieus != null && thoiKhoaBieus.size() > 0) {
                    dbHelper.insertSchedule(thoiKhoaBieus);
                    Date now = DateHelper.getCalendar().getTime();
                    int dayOfMonth = DateHelper.getDayOfMonth(now);
                    int month = DateHelper.getMonth(now);
                    int year = DateHelper.getYear(now);
                    String dayOfMonthStr = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
                    String monthStr = month < 10 ? "0" + month : month + "";
                    String dateStr = year + "-" + monthStr + "-" + dayOfMonthStr;
                    List<ThoiKhoaBieu> todayClasses = dbHelper.getSchedule(dateStr);
                    ScheduleTaskHelper.getInstance().setTodayReminder(SelectingSemesterActivity.this, todayClasses);
                }
            }

            new Handler().postDelayed(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    mPBLoading.setVisibility(View.GONE);
                    mBtnTacNghiep.setText("Tác Nghiệp");
                    mBtnTacNghiep.refreshDrawableState();

                    if (aBoolean)
                        Toasty.success(SelectingSemesterActivity.this, responseMessage, Toasty.LENGTH_SHORT).show();
                    else
                        Toasty.error(SelectingSemesterActivity.this, responseMessage, Toasty.LENGTH_SHORT).show();
                }
            }, 1000);
        }
    }

    @Override
    public void onRefresh() {
        fetchData();
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchSemesterTask extends AsyncTask<String, Void, Boolean> {
        private String mJson;
        private String mResponseMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String url = Reference.HOST + "/api/sinhvien/hocky/danhsach" +
                    "?masinhvien=" + SharedPreferenceHelper.getInstance()
                    .getSharedPrefStr(SelectingSemesterActivity.this, ACCOUNT_SP, STUDENT_ID, "")
                    + "&matkhau=" + SharedPreferenceHelper.getInstance()
                    .getSharedPrefStr(SelectingSemesterActivity.this, ACCOUNT_SP, ACCOUNT_PASSWORD, "");
            Response response = NetworkUtil.makeRequest(url, false, null);

            if (response == null) {
                mResponseMessage = "Máy chủ không phản hồi";
                return false;
            }

            if (response.code() == NetworkUtil.OK) {
                try {
                    mJson = response.body() != null ? response.body().string() : "";
                    return true;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mResponseMessage = "Có sự cố khi tải danh sách học kì";
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mSwipeRefreshLayout.setRefreshing(false);

            if (aBoolean) {
                List<VHocKy> list = VHocKy.fromJsonToList(mJson);

                if (list != null && list.size() > 0) {
                    setUpListView(list);
                }
            } else {
                Toasty.error(SelectingSemesterActivity.this, mResponseMessage, Toasty.LENGTH_SHORT).show();
            }
        }
    }
}
