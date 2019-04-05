package com.practice.phuc.ums_husc.ScheduleModule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.phuc.ums_husc.Adapter.WeeksTabAdapter;
import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Model.LOPHOCPHAN;
import com.practice.phuc.ums_husc.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private Context mContext;
    private WeeksTabAdapter mFragmentsTabAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private List<LOPHOCPHAN> mClassList;
    private Date mMinStartDate;
    private Date mMaxEndDate;

    public ScheduleFragment() {
    }

    public static ScheduleFragment newInstance(Context context) {
        ScheduleFragment fragment = new ScheduleFragment();
        fragment.mContext = context;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mFragmentsTabAdapter = new WeeksTabAdapter(getChildFragmentManager());
        mClassList = new ArrayList<>();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        mViewPager = view.findViewById(R.id.vp_schedule);
        mTabLayout = view.findViewById(R.id.tabs_schedule);

        setData();
        setUpFragments();
        return view;
    }

    @Override
    public void onDestroyView() {
        mFragmentsTabAdapter.clearFragment();
        super.onDestroyView();
    }

    private void setUpFragments() {
        int currentWeekPos = 0;
        int plusDay = 6;
        int totalWeek = countTotalWeek();

        Date startDateOfWeek = mMinStartDate;
        Date endDateOfWeek = DateHelper.plusDay(startDateOfWeek, plusDay);

        Date now = DateHelper.getCalendar().getTime();
        boolean isBetween;

        for (int i = 0; i < totalWeek; i++) {
            String weekTitle = "Tuần " + (i + 1);
            // Ngay hien tai co thuoc tuan nay khong
            isBetween = DateHelper.isBetweenTwoDate(startDateOfWeek, endDateOfWeek, now);
            if (isBetween) currentWeekPos = i;  // Neu thuoc, luu lai vi tri cua tab/page

            mFragmentsTabAdapter.addFragment(
                    WeekFragment.newInstance(mContext, startDateOfWeek, endDateOfWeek, /*Debug:*/ weekTitle)
                    , weekTitle
            );
            startDateOfWeek = DateHelper.plusDay(endDateOfWeek,1);
            endDateOfWeek = DateHelper.plusDay(startDateOfWeek, plusDay);
        }

        mViewPager.setAdapter(mFragmentsTabAdapter);
        mViewPager.setCurrentItem(currentWeekPos);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private int countTotalWeek() {
        Date[] dateArr = new Date[mClassList.size()];
        findMinStartDate(dateArr);
        findMaxEndDate(dateArr);

        int numberOfDate = DateHelper.daysBetween(mMinStartDate, mMaxEndDate) + 1;
        return numberOfDate % 7 == 0 ? numberOfDate / 7 : numberOfDate / 7 + 1;
    }

    private void findMinStartDate(Date[] dateArr) {
        for (int i = 0; i < mClassList.size(); i++) {
            dateArr[i] = DateHelper.stringToDate(mClassList.get(i).getNgayBatDau(), "dd/MM/yyyy");
        }
        mMinStartDate = DateHelper.minDateInArr(dateArr);
        mMinStartDate = DateHelper.getTheFirstDateOfWeek(mMinStartDate);
    }

    private void findMaxEndDate(Date[] dateArr) {
        for (int i = 0; i < mClassList.size(); i++) {
            dateArr[i] = DateHelper.stringToDate(mClassList.get(i).getNgayKetThuc(), "dd/MM/yyyy");
        }
        mMaxEndDate = DateHelper.maxDateInArr(dateArr);
    }

    private void setData() {
        LOPHOCPHAN l = new LOPHOCPHAN(
                "2018-2019.1.TIN4063.001", "Phần mềm mã nguồn mở - Nhóm 1",
                "Lab 5_CNTT", 5, 7, "Nguyễn Dũng",
                "20/01/2019", "10/05/2019", DateHelper.MONDAY
        );
        mClassList.add(l);

        l = new LOPHOCPHAN(
                "2018-2019.1.TIN4133.001", "Quản trị dự án phần mềm - Nhóm 1",
                "Lab 2_CNTT", 2, 4, "Nguyễn Mậu Hân",
                "20/01/2019", "03/05/2019", DateHelper.MONDAY
        );
        mClassList.add(l);

        l = new LOPHOCPHAN(
                "2018-2019.1.TIN4173.001", "Java và xử lý phân tán - Nhóm 1",
                "Lab 4_CNTT", 1, 3, "Nguyễn Hoàng Hà",
                "21/01/2019", "27/04/2019", DateHelper.TUESDAY
        );
        mClassList.add(l);

        l = new LOPHOCPHAN(
                "2018-2019.1.TIN4183.001", "Kiểm định phần mềm - Nhóm 1",
                "E404", 1, 3, "Lê Văn Tường Lân",
                "30/01/2019", "06/05/2019", DateHelper.THURSDAY
        );
        mClassList.add(l);

        l = new LOPHOCPHAN(
                "2018-2019.1.TIN4203.002", "Quy trình phát triển phần mềm RUP - Nhóm 2",
                "Lab 5_CNTT", 1, 3, "Lê Văn Tường Lân",
                "29/01/2019", "06/05/2019", DateHelper.WEDNESDAY
        );
        mClassList.add(l);

        l = new LOPHOCPHAN(
                "2018-2019.1.TIN4403.003", "Lập trình ứng dụng cho các thiết bị di động - Nhóm 3",
                "Lab 4_CNTT", 1, 3, "Nguyễn Dũng",
                "24/01/2019", "10/05/2019", DateHelper.WEDNESDAY
        );
        mClassList.add(l);

        l = new LOPHOCPHAN(
                "2018-2019.1.TIN4483.002", "Xây dựng ứng dụng với .NET FrameWork - Nhóm 2",
                "Lab 2_CNTT", 9, 11, "Nguyễn Hoàng Hà",
                "21/01/2019", "27/04/2019", DateHelper.WEDNESDAY
        );
        mClassList.add(l);
    }
}
