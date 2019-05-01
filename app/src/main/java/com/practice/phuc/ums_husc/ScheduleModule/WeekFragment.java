package com.practice.phuc.ums_husc.ScheduleModule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Adapter.DayOfWeekAdapter;
import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeekFragment extends Fragment {

    private Context mContext;
    private DayOfWeekAdapter mDayOfWeekAdapter;
    private RecyclerView mRvDayOfWeek;
    private TextView mTvStartDateOfWeek;
    private TextView mTvEndDateOfWeek;
    private Date mStartDateOfWeek;
    private Date mEndDateOfWeek;
    private List<ThoiKhoaBieu> mClassList; // Danh sach lop hoc trong tuan nay

    public WeekFragment() {
    }

    public static WeekFragment newInstance(Context context, Date startDateOfWeek, Date endDateOfWeek, List<ThoiKhoaBieu> classList) {
        WeekFragment df = new WeekFragment();
        df.mContext = context;
        df.mClassList = classList;
        df.mStartDateOfWeek = startDateOfWeek;
        df.mEndDateOfWeek = endDateOfWeek;
        return df;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mDayOfWeekAdapter = new DayOfWeekAdapter(mContext, mClassList, mStartDateOfWeek);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week, container, false);
        mRvDayOfWeek = view.findViewById(R.id.rv_dayOfWeek);
        mTvStartDateOfWeek = view.findViewById(R.id.tv_startDateOfWeeek);
        mTvEndDateOfWeek = view.findViewById(R.id.tv_endDateOfWeek);

        setUpStartEndDate();
        setUpRecyclerView();

        return view;
    }

    private void setUpStartEndDate() {
        String startDayTitle = "Từ " + DateHelper.toShortDateString(mStartDateOfWeek);
        mTvStartDateOfWeek.setText(startDayTitle);

        String endDayTitle = "Đến " + DateHelper.toShortDateString(mEndDateOfWeek);
        mTvEndDateOfWeek.setText(endDayTitle);
    }

    private void setUpRecyclerView() {
        final LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mRvDayOfWeek.setLayoutManager(manager);
        mRvDayOfWeek.setHasFixedSize(true);

        Date now = DateHelper.getCalendar().getTime(); // Kiem tra ngay hien tai co thuoc tuan nay khong
        boolean isBetween = DateHelper.isBetweenTwoDate(mStartDateOfWeek, mEndDateOfWeek, now);

        int nowIndex = 0;
        if (isBetween) { // Neu thuoc tuan nay, scoll xuong ngay hien tai
            int toDayOfWeek = DateHelper.getCalendar().get(Calendar.DAY_OF_WEEK);
            if (toDayOfWeek == 1)
                nowIndex = 6;
            else
                nowIndex = toDayOfWeek - 2;
            mDayOfWeekAdapter.setCurrentDayOfWeek(nowIndex);
        }
        final int finalNowIndex = nowIndex;
        mRvDayOfWeek.setAdapter(mDayOfWeekAdapter);
        mRvDayOfWeek.post(new Runnable() {
            @Override
            public void run() {
                mRvDayOfWeek.scrollToPosition(finalNowIndex);
            }
        });
    }
}
