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
import com.practice.phuc.ums_husc.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayOfWeekAdapter extends Adapter<DayOfWeekAdapter.DataViewHolder> {

    private Context mContext;
    private SessionOfDayAdapter mMorningAdapter;
    private SessionOfDayAdapter mAfternoonAdapter;
    private SessionOfDayAdapter mEveningAdapter;
    private boolean mNowIsInThisWeek;
    private Date mStartDateOfWeek;

    private List<String> mClassesOfDay; // Danh sach cac lop hoc trong ngay hom nay

    public DayOfWeekAdapter(Context context, List<String> classesOfDay, Date startDateOfWeek) {
        mContext = context;
        mClassesOfDay = classesOfDay;
        mStartDateOfWeek = startDateOfWeek;

        mMorningAdapter = new SessionOfDayAdapter(mContext, null);
        mAfternoonAdapter = new SessionOfDayAdapter(mContext, null);
        mEveningAdapter = new SessionOfDayAdapter(mContext, null);
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
            String toDate = DateHelper.toShortDateString(mStartDateOfWeek);
            viewHolder.tvDayOfMonth.setText(toDate);
        }
        if (i == 1) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.tuesday));
            String toDate = DateHelper.toShortDateString(DateHelper.plusDay(mStartDateOfWeek, 1));
            viewHolder.tvDayOfMonth.setText(toDate);
        }
        if (i == 2) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.wednesday));
            String toDate = DateHelper.toShortDateString(DateHelper.plusDay(mStartDateOfWeek, 2));
            viewHolder.tvDayOfMonth.setText(toDate);
        }
        if (i == 3) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.thursday));
            String toDate = DateHelper.toShortDateString(DateHelper.plusDay(mStartDateOfWeek, 3));
            viewHolder.tvDayOfMonth.setText(toDate);
        }
        if (i == 4) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.friday));
            String toDate = DateHelper.toShortDateString(DateHelper.plusDay(mStartDateOfWeek, 4));
            viewHolder.tvDayOfMonth.setText(toDate);
        }
        if (i == 5) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.saturday));
            String toDate = DateHelper.toShortDateString(DateHelper.plusDay(mStartDateOfWeek, 5));
            viewHolder.tvDayOfMonth.setText(toDate);
        }
        if (i == 6) {
            viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.sunday));
            String toDate = DateHelper.toShortDateString(DateHelper.plusDay(mStartDateOfWeek, 6));
            viewHolder.tvDayOfMonth.setText(toDate);
        }

        // Morning
        viewHolder.rvSessionMorning.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        );
        viewHolder.rvSessionMorning.setHasFixedSize(true);
        viewHolder.rvSessionMorning.setAdapter(mMorningAdapter);
        // Afternoon
        viewHolder.rvSessionAfternoon.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        );
        viewHolder.rvSessionAfternoon.setHasFixedSize(true);
        viewHolder.rvSessionAfternoon.setAdapter(mAfternoonAdapter);
        // Evening
        viewHolder.rvSessionEvening.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        );
        viewHolder.rvSessionEvening.setHasFixedSize(true);
        viewHolder.rvSessionEvening.setAdapter(mEveningAdapter);

        if (mNowIsInThisWeek) { // Neu hom nay la ngay nay, lam sang ngay nay len
            int toDayOfWeek = DateHelper.getCalendar().get(Calendar.DAY_OF_WEEK);
            if ((toDayOfWeek == 1 && i == 6) || toDayOfWeek - 2 == i) {
                viewHolder.tvDayOfWeek.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                viewHolder.tvDayOfWeek.setTypeface(null, Typeface.BOLD);
                viewHolder.tvDayOfMonth.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                viewHolder.tvDayOfMonth.setTypeface(null, Typeface.BOLD);
                viewHolder.layoutTitle.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    @Override // Co bay ngay trong tuan
    public int getItemCount() {
        return 7;
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
