package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Model.LOPHOCPHAN;
import com.practice.phuc.ums_husc.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.practice.phuc.ums_husc.Helper.DateHelper.AFTERNOON;
import static com.practice.phuc.ums_husc.Helper.DateHelper.EVENING;
import static com.practice.phuc.ums_husc.Helper.DateHelper.FRIDAY;
import static com.practice.phuc.ums_husc.Helper.DateHelper.MONDAY;
import static com.practice.phuc.ums_husc.Helper.DateHelper.MORNING;
import static com.practice.phuc.ums_husc.Helper.DateHelper.SATURDAY;
import static com.practice.phuc.ums_husc.Helper.DateHelper.SUNDAY;
import static com.practice.phuc.ums_husc.Helper.DateHelper.THURSDAY;
import static com.practice.phuc.ums_husc.Helper.DateHelper.TUESDAY;
import static com.practice.phuc.ums_husc.Helper.DateHelper.WEDNESDAY;
import static com.practice.phuc.ums_husc.Helper.DateHelper.getDate;
import static com.practice.phuc.ums_husc.Helper.DateHelper.isBetweenTwoDate;
import static com.practice.phuc.ums_husc.Helper.DateHelper.stringToDate;
import static com.practice.phuc.ums_husc.Helper.DateHelper.toShortDateString;

public class DayOfWeekAdapter extends Adapter<DayOfWeekAdapter.DataViewHolder> {

    private Context mContext;
    private SessionOfDayAdapter mMorningAdapter;
    private SessionOfDayAdapter mAfternoonAdapter;
    private SessionOfDayAdapter mEveningAdapter;
    private boolean mNowIsInThisWeek;
    private Date mStartDateOfWeek;
    private Date mEndDateOfWeek;
    private Date mTodate;

    private List<LOPHOCPHAN> mClassList; // Danh sach cac lop hoc
    private List<LOPHOCPHAN> mMondayClassList;
    private List<LOPHOCPHAN> mTuesdayClassList;
    private List<LOPHOCPHAN> mWednesdayClassList;
    private List<LOPHOCPHAN> mThursdayClassList;
    private List<LOPHOCPHAN> mFridayClassList;
    private List<LOPHOCPHAN> mSaturdayClassList;
    private List<LOPHOCPHAN> mSundayClassList;

    public DayOfWeekAdapter(Context context, List<LOPHOCPHAN> classList, Date startDateOfWeek, Date endDateOfWeek) {
        mContext = context;
        mStartDateOfWeek = startDateOfWeek;
        mEndDateOfWeek = endDateOfWeek;
        mClassList = classList;
        mMondayClassList = getClassesOnThisDay(MONDAY);
        mTuesdayClassList = getClassesOnThisDay(TUESDAY);
        mWednesdayClassList = getClassesOnThisDay(WEDNESDAY);
        mThursdayClassList = getClassesOnThisDay(THURSDAY);
        mFridayClassList = getClassesOnThisDay(FRIDAY);
        mSaturdayClassList = getClassesOnThisDay(SATURDAY);
        mSundayClassList = getClassesOnThisDay(SUNDAY);

        mMorningAdapter = new SessionOfDayAdapter(mContext);
        mAfternoonAdapter = new SessionOfDayAdapter(mContext);
        mEveningAdapter = new SessionOfDayAdapter(mContext);
    }

    public void setNowIsInThisWeek(boolean value) {
        mNowIsInThisWeek = value;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_day_of_week, viewGroup, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder viewHolder, int i) {
        if (i == 0) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.monday));
            setUpSessionAdapter(mMondayClassList, MONDAY);
        }
        if (i == 1) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.tuesday));
            setUpSessionAdapter(mTuesdayClassList, TUESDAY);
        }
        if (i == 2) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.wednesday));
            setUpSessionAdapter(mWednesdayClassList, WEDNESDAY);
        }
        if (i == 3) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.thursday));
            setUpSessionAdapter(mThursdayClassList, THURSDAY);
        }
        if (i == 4) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.friday));
            setUpSessionAdapter(mFridayClassList, FRIDAY);
        }
        if (i == 5) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.saturday));
            setUpSessionAdapter(mSaturdayClassList, SATURDAY);
        }
        if (i == 6) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.sunday));
            setUpSessionAdapter(mSundayClassList, SUNDAY);
        }
        /* Phai goi sau ham setUpSessionAdapter */
        viewHolder.tvDayOfMonth.setText(toShortDateString(mTodate));

        setUpSessionRecyclerView(viewHolder.rvSessionMorning, mMorningAdapter);
        setUpSessionRecyclerView(viewHolder.rvSessionAfternoon, mAfternoonAdapter);
        setUpSessionRecyclerView(viewHolder.rvSessionEvening, mEveningAdapter);
        // Neu hom nay la ngay nay, lam sang ngay nay len
        setUpToday(viewHolder, i);
    }

    private void setUpToday(DataViewHolder viewHolder, int i) {
        if (mNowIsInThisWeek) {
            int toDayOfWeek = DateHelper.getCalendar().get(Calendar.DAY_OF_WEEK);
            if ((toDayOfWeek == 1 && i == 6) || toDayOfWeek - 2 == i) {
                viewHolder.tvDayOfWeek.append(" ( Hôm nay )");
                viewHolder.tvDayOfWeek.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                viewHolder.tvDayOfWeek.setTypeface(null, Typeface.BOLD);
                viewHolder.tvDayOfMonth.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                viewHolder.tvDayOfMonth.setTypeface(null, Typeface.BOLD);
                viewHolder.layoutTitle.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    private void setUpSessionAdapter(List<LOPHOCPHAN> classList, int dateOfWeek) {
        mTodate = DateHelper.getDate(mStartDateOfWeek, dateOfWeek);
        mMorningAdapter.setClassesOfSession(getClassesOnThisSession(classList, MORNING));
        mAfternoonAdapter.setClassesOfSession(getClassesOnThisSession(classList, AFTERNOON));
        mEveningAdapter.setClassesOfSession(getClassesOnThisSession(classList, EVENING));
    }

    private void setUpSessionRecyclerView(RecyclerView recyclerView, SessionOfDayAdapter adapter) {
        recyclerView.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override // Co bay ngay trong tuan
    public int getItemCount() {
        return 7;
    }

    private List<LOPHOCPHAN> getClassesOnThisDay(int dayOfWeek) {
        List<LOPHOCPHAN> l = new ArrayList<>();
        Date startDate, endDate;

        for (LOPHOCPHAN item : mClassList) {
            startDate = stringToDate(item.getNgayBatDau(), "dd/MM/yyyy");
            endDate = stringToDate(item.getNgayKetThuc(), "dd/MM/yyyy");
            if ((item.getNgayTrongTuan() == dayOfWeek)
            && isBetweenTwoDate(startDate, endDate, getDate(mStartDateOfWeek, dayOfWeek))) {
                l.add(item);
            }
        }
        return l;
    }

    private List<LOPHOCPHAN> getClassesOnThisSession(List<LOPHOCPHAN> classes, int sessionOfDay) {
        List<LOPHOCPHAN> l = new ArrayList<>();
        switch (sessionOfDay) {
            case MORNING:
                for (LOPHOCPHAN item : classes) {
                    if (1 <= item.getTietBatDau() && item.getTietBatDau() <= 4)
                        l.add(item);
                }
                break;
            case AFTERNOON:
                for (LOPHOCPHAN item : classes) {
                    if (5 <= item.getTietBatDau() && item.getTietBatDau() <= 8)
                        l.add(item);
                }
                break;
            case EVENING:
                for (LOPHOCPHAN item : classes) {
                    if (9 <= item.getTietBatDau() && item.getTietBatDau() <= 12)
                        l.add(item);
                }
                break;
        }
        return l;
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        private ViewGroup layoutTitle;
        private TextView tvDayOfWeek;
        private TextView tvDayOfMonth;
        private RecyclerView rvSessionMorning;
        private RecyclerView rvSessionAfternoon;
        private RecyclerView rvSessionEvening;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutTitle = itemView.findViewById(R.id.layout_title);
            tvDayOfWeek = itemView.findViewById(R.id.tv_dayOfWeek);
            tvDayOfMonth = itemView.findViewById(R.id.tv_dayOfMonth);
            rvSessionMorning = itemView.findViewById(R.id.rv_sessionMorning);
            rvSessionAfternoon = itemView.findViewById(R.id.rv_sessionAfternoon);
            rvSessionEvening = itemView.findViewById(R.id.rv_sessionEvening);
        }
    }
}
