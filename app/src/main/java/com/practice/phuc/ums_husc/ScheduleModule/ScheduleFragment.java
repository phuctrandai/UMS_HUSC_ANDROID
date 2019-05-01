package com.practice.phuc.ums_husc.ScheduleModule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.practice.phuc.ums_husc.Adapter.WeeksTabAdapter;
import com.practice.phuc.ums_husc.Helper.DBHelper;
import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.Helper.StringHelper;
import com.practice.phuc.ums_husc.MainActivity;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.Response;

public class ScheduleFragment extends Fragment {
    public static boolean mIsChangeSemester;
    public static boolean mIsLastSemester;

    private Context mContext;
    private WeeksTabAdapter mWeeksTabAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewGroup mLoadingLayout;

    private boolean mIsFragmentsCreated;
    private int mCurrentWeekPos;
    private List<ThoiKhoaBieu> mClassList;
    private DBHelper mDBHelper;

    public ScheduleFragment() {
    }

    public static ScheduleFragment newInstance(Context context) {
        ScheduleFragment fragment = new ScheduleFragment();
        fragment.mContext = context;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mIsChangeSemester = false;
        mIsLastSemester = true;
        mIsFragmentsCreated = false;
        mWeeksTabAdapter = new WeeksTabAdapter(getChildFragmentManager());
        mDBHelper = new DBHelper(mContext);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        setHasOptionsMenu(true);
        mTabLayout = view.findViewById(R.id.tabs_schedule);
        mViewPager = view.findViewById(R.id.vp_schedule);
        mLoadingLayout = view.findViewById(R.id.layout_loading);
        mLoadingLayout.bringToFront();

        if (!mIsFragmentsCreated) initFragments();

        if (mIsChangeSemester) notifySemesterChanged(mDBHelper.getSchedule());

        setUpViewPagerTabLayout();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_goToToday:
                if (!mIsLastSemester) {
                    Toasty.info(mContext, "Học kì này hiện đã kết thúc", Toasty.LENGTH_SHORT).show();
                    return true;
                }

                if (mTabLayout != null) {
                    TabLayout.Tab tab = mTabLayout.getTabAt(mCurrentWeekPos);
                    if (mTabLayout.getSelectedTabPosition() != mCurrentWeekPos && tab != null)
                        tab.select();
                }
                return true;

            case R.id.action_refreshSchedule:
                if (NetworkUtil.getConnectivityStatus(mContext) == NetworkUtil.TYPE_NOT_CONNECTED)
                    Toasty.custom(mContext, getString(R.string.error_network_disconected),
                            getResources().getDrawable(R.drawable.ic_signal_wifi_off_white_24),
                            getResources().getColor(R.color.colorRed),
                            getResources().getColor(android.R.color.white),
                            Toasty.LENGTH_SHORT, true, true)
                            .show();
                else {
                    String semesterId = SharedPreferenceHelper.getInstance()
                            .getSharedPrefStr(mContext, SharedPreferenceHelper.ACCOUNT_SP,
                                    SharedPreferenceHelper.STUDENT_SEMSTER, "");
                    if (!StringHelper.isNullOrEmpty(semesterId)) {
                        String studentId = SharedPreferenceHelper.getInstance()
                                .getSharedPrefStr(mContext, SharedPreferenceHelper.ACCOUNT_SP,
                                        SharedPreferenceHelper.STUDENT_ID, "");
                        String password = SharedPreferenceHelper.getInstance()
                                .getSharedPrefStr(mContext, SharedPreferenceHelper.ACCOUNT_SP,
                                        SharedPreferenceHelper.ACCOUNT_PASSWORD, "");
                        new RefreshScheduleTask(studentId, password, semesterId).execute((String) null);
                    }
                }
                return true;

            case R.id.action_selectSemester:
                Intent intent = new Intent(mContext, SelectingSemesterActivity.class);
                startActivityForResult(intent, 0);

                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String semesterStr = data.getStringExtra("semesterStr");

            MainActivity main = (MainActivity) getActivity();
            if (main != null) main.updateSemester(semesterStr);
            notifySemesterChanged(mDBHelper.getSchedule());
            setUpViewPagerTabLayout();

            if (!mIsLastSemester)
                Toasty.info(mContext, "Học kì này hiện đã kết thúc", Toasty.LENGTH_SHORT).show();
        }
    }

    private void initFragments() {
        mIsFragmentsCreated = true;
        mClassList = mDBHelper.getSchedule();
        setUpFragments(mClassList);
    }

    private void notifySemesterChanged(List<ThoiKhoaBieu> classList) {
        mClassList.clear();
        mClassList.addAll(classList);
        mWeeksTabAdapter.clearFragment();
        mWeeksTabAdapter = new WeeksTabAdapter(getChildFragmentManager());
        setUpFragments(mClassList);
    }

    private void setUpViewPagerTabLayout() {
        int totalWeek = countTotalWeek(mClassList);
        mViewPager.setAdapter(mWeeksTabAdapter);
        mViewPager.setCurrentItem(mCurrentWeekPos);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabGravity(totalWeek <= 5 ? TabLayout.GRAVITY_CENTER : TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(totalWeek <= 5 ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE);

        if (mIsChangeSemester || !mIsFragmentsCreated) {
            mIsChangeSemester = false;
            mViewPager.post(new Runnable() {
                @Override
                public void run() {
                    mViewPager.setCurrentItem(mCurrentWeekPos, false);
                }
            });
        }
    }

    private void setUpFragments(List<ThoiKhoaBieu> classList) {
        Date startDateOfWeek = findMinStartDate(classList);
        Date endDateOfWeek = DateHelper.plusDay(startDateOfWeek, 6);

        mCurrentWeekPos = 0;
        int totalWeek = countTotalWeek(classList);
        int lassAccess = 0;
        boolean isBetween = false;
        Date now = DateHelper.getCalendar().getTime();

        for (int i = 0; i < totalWeek; i++) {
            String weekTitle = "Tuần " + (i + 1);

            List<ThoiKhoaBieu> list = getClassesOfWeek(startDateOfWeek, endDateOfWeek, classList, lassAccess);
            mWeeksTabAdapter.addFragment(WeekFragment.newInstance(
                    mContext, startDateOfWeek, endDateOfWeek, list), weekTitle
            );

            if (!isBetween) { // Luu lai tab/pag cua tuan hien tai
                isBetween = DateHelper.isBetweenTwoDate(startDateOfWeek, endDateOfWeek, now);
                if (isBetween) mCurrentWeekPos = i;
            }

            startDateOfWeek = DateHelper.plusDay(endDateOfWeek, 1);
            endDateOfWeek = DateHelper.plusDay(startDateOfWeek, 6);
        }
    }

    private int countTotalWeek(List<ThoiKhoaBieu> classList) {
        Date minStartDate = findMinStartDate(classList);
        Date maxEndDate = findMaxEndDate(classList);

        int numberOfDate = DateHelper.daysBetween(minStartDate, maxEndDate) + 1;
        return numberOfDate % 7 == 0 ? numberOfDate / 7 : numberOfDate / 7 + 1;
    }

    private Date findMinStartDate(List<ThoiKhoaBieu> classList) {
        if (classList == null || classList.size() == 0)
            return DateHelper.getTheFirstDateOfWeek(DateHelper.getCalendar().getTime());

        String ngayBatDauStr = classList.get(0).NgayHoc;

        if (ngayBatDauStr.length() > 10) {
            ngayBatDauStr = ngayBatDauStr.substring(0, 10);
        }

        ngayBatDauStr = DateHelper.formatYMDToDMY(ngayBatDauStr);
        Date date = DateHelper.stringToDate(ngayBatDauStr, "dd/MM/yyyy");

        date = DateHelper.getTheFirstDateOfWeek(date);
        return date;
    }

    private Date findMaxEndDate(List<ThoiKhoaBieu> classList) {
        if (classList == null || classList.size() == 0)
            return DateHelper.getCalendar().getTime();

        String ngayKetThucStr = classList.get(classList.size() - 1).NgayHoc;

        if (ngayKetThucStr.length() > 10) {
            ngayKetThucStr = ngayKetThucStr.substring(0, 10);
        }

        ngayKetThucStr = DateHelper.formatYMDToDMY(ngayKetThucStr);

        return DateHelper.stringToDate(ngayKetThucStr, "dd/MM/yyyy");
    }

    private List<ThoiKhoaBieu> getClassesOfWeek(Date startDateOfWeek, Date endDateOfWeek, List<ThoiKhoaBieu> source, int lastAccess) {
        List<ThoiKhoaBieu> result = new ArrayList<>();

        for (int i = lastAccess; i < source.size(); i++, lastAccess++) {
            String ngayHocStr = source.get(i).NgayHoc.substring(0, 10);
            ngayHocStr = DateHelper.formatYMDToDMY(ngayHocStr);
            Date ngayHoc = DateHelper.stringToDate(ngayHocStr, "dd/MM/yyyy");
            boolean isBetween = DateHelper.isBetweenTwoDate(startDateOfWeek, endDateOfWeek, ngayHoc);

            if (isBetween) result.add(source.get(i));
        }

        return result;
    }

    @SuppressLint("StaticFieldLeak")
    private class RefreshScheduleTask extends AsyncTask<String, Void, Boolean> {
        private String mStudentId;
        private String mPassword;
        private String mSemesterId;
        private String mResponseMessage;
        private String mJson;

        private RefreshScheduleTask(String studentId, String password, String semesterId) {
            mPassword = password;
            mStudentId = studentId;
            mSemesterId = semesterId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String url = Reference.HOST + "api/sinhvien/thoikhoabieu" +
                    "?masinhvien=" + mStudentId +
                    "&matkhau=" + mPassword +
                    "&mahocky=" + mSemesterId;

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

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                final List<ThoiKhoaBieu> list = ThoiKhoaBieu.fromJsonToList(mJson);
                if (list != null) {
                    mDBHelper.deleteAllRecord(DBHelper.SCHEDULE);
                    mDBHelper.insertSchedule(list);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifySemesterChanged(list);
                            setUpViewPagerTabLayout();
                            mLoadingLayout.setVisibility(View.GONE);
                        }
                    }, 1500);
                }

            } else {
                Toasty.info(mContext, mResponseMessage, Toasty.LENGTH_SHORT).show();
            }
        }
    }
}
