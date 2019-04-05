package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;

import java.util.List;

public class DayOfWeekAdapter extends Adapter<DayOfWeekAdapter.DataViewHolder> {

    private Context mContext;
    private SessionOfDayAdapter mMorningAdapter;
    private SessionOfDayAdapter mAfternoonAdapter;
    private SessionOfDayAdapter mEveningAdapter;

    private List<String> mClassesOfDay; // Danh sach cac lop hoc trong ngay hom nay

    public DayOfWeekAdapter(Context context, List<String> classesOfDay) {
        mContext = context;
        mClassesOfDay = classesOfDay;
        mMorningAdapter = new SessionOfDayAdapter(mContext, null);
        mAfternoonAdapter = new SessionOfDayAdapter(mContext, null);
        mEveningAdapter = new SessionOfDayAdapter(mContext, null);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_day_of_week, viewGroup, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder viewHolder, int i) {
        if (i == 0) viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.monday));
        if (i == 1) viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.tuesday));
        if (i == 2) viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.wednesday));
        if (i == 3) viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.thursday));
        if (i == 4) viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.friday));
        if (i == 5) viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.saturday));
        if (i == 6) viewHolder.tvDayOfWeek.setText(mContext.getString(R.string.sunday));

        viewHolder.rvSessionMorning.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        );
        viewHolder.rvSessionMorning.setHasFixedSize(true);
        viewHolder.rvSessionMorning.setAdapter(mMorningAdapter);

        viewHolder.rvSessionAfternoon.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        );
        viewHolder.rvSessionAfternoon.setHasFixedSize(true);
        viewHolder.rvSessionAfternoon.setAdapter(mAfternoonAdapter);

        viewHolder.rvSessionEvening.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        );
        viewHolder.rvSessionEvening.setHasFixedSize(true);
        viewHolder.rvSessionEvening.setAdapter(mEveningAdapter);
    }

    @Override // Co bay ngay trong tuan
    public int getItemCount() {
        return 7;
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDayOfWeek;
        private RecyclerView rvSessionMorning;
        private RecyclerView rvSessionAfternoon;
        private RecyclerView rvSessionEvening;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tv_dayOfWeek);
            rvSessionMorning = itemView.findViewById(R.id.rv_sessionMorning);
            rvSessionAfternoon = itemView.findViewById(R.id.rv_sessionAfternoon);
            rvSessionEvening = itemView.findViewById(R.id.rv_sessionEvening);
        }
    }
}
