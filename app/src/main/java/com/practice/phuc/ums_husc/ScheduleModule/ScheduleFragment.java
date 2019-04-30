package com.practice.phuc.ums_husc.ScheduleModule;

import android.content.Context;
import android.os.Bundle;
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
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private Context mContext;
    private WeeksTabAdapter mWeeksTabAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private int mCurrentWeekPos;
    private int mTotalWeek;
    private List<ThoiKhoaBieu> mClassList;

    public ScheduleFragment() {
    }

    public static ScheduleFragment newInstance(Context context) {
        ScheduleFragment fragment = new ScheduleFragment();
        fragment.mContext = context;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        DBHelper mDBHelper = new DBHelper(mContext);
        mWeeksTabAdapter = new WeeksTabAdapter(getChildFragmentManager());
        mClassList = mDBHelper.getSchedule();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        setHasOptionsMenu(true);
        mViewPager = view.findViewById(R.id.vp_schedule);
        mTabLayout = view.findViewById(R.id.tabs_schedule);
        mTotalWeek = countTotalWeek();
        setUpFragments();
        return view;
    }

    @Override
    public void onDestroyView() {
        mWeeksTabAdapter.clearFragment();
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_goToToday:
                if (mTabLayout != null) {
                    TabLayout.Tab tab = mTabLayout.getTabAt(mCurrentWeekPos);
                    if (mTabLayout.getSelectedTabPosition() != mCurrentWeekPos && tab != null)
                        tab.select();
                }
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpFragments() {
        Date startDateOfWeek = findMinStartDate();
        Date endDateOfWeek = DateHelper.plusDay(startDateOfWeek, 6);

        int lassAccess = 0;
        mCurrentWeekPos = 0;
        boolean isBetween = false;
        Date now = DateHelper.getCalendar().getTime();

        for (int i = 0; i < mTotalWeek; i++) {
            String weekTitle = "Tuần " + (i + 1);

            List<ThoiKhoaBieu> classList = getClassesOfWeek(startDateOfWeek, endDateOfWeek, mClassList, lassAccess);
            mWeeksTabAdapter.addFragment(WeekFragment.newInstance(
                    mContext, startDateOfWeek, endDateOfWeek, classList), weekTitle
            );

            if (!isBetween) { // Luu lai tab/pag cua tuan hien tai
                isBetween = DateHelper.isBetweenTwoDate(startDateOfWeek, endDateOfWeek, now);
                if (isBetween) mCurrentWeekPos = i;
            }

            startDateOfWeek = DateHelper.plusDay(endDateOfWeek, 1);
            endDateOfWeek = DateHelper.plusDay(startDateOfWeek, 6);
        }
        mViewPager.setAdapter(mWeeksTabAdapter);
        mViewPager.setCurrentItem(mCurrentWeekPos);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private int countTotalWeek() {
        Date minStartDate = findMinStartDate();
        Date maxEndDate = findMaxEndDate();

        int numberOfDate = DateHelper.daysBetween(minStartDate, maxEndDate) + 1;
        return numberOfDate % 7 == 0 ? numberOfDate / 7 : numberOfDate / 7 + 1;
    }

    private Date findMinStartDate() {
        String ngayBatDauStr = mClassList.get(0).NgayHoc.substring(0, 10);
        ngayBatDauStr = DateHelper.formatYMDToDMY(ngayBatDauStr);
        Date date = DateHelper.stringToDate(ngayBatDauStr, "dd/MM/yyyy");
        date = DateHelper.getTheFirstDateOfWeek(date);
        return date;
    }

    private Date findMaxEndDate() {
        String ngayKetThucStr = mClassList.get(mClassList.size() - 1).NgayHoc.substring(0, 10);
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
}
