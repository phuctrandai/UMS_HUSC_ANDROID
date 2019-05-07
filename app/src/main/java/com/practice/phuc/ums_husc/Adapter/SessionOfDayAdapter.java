package com.practice.phuc.ums_husc.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.util.List;

public class SessionOfDayAdapter extends RecyclerView.Adapter<SessionOfDayAdapter.DataViewHolder> {

    private List<ThoiKhoaBieu> mClassesOfSession; // Danh sach lop hoc co tiet vao buoi nay

    SessionOfDayAdapter() {
    }

    void setClassesOfSession(List<ThoiKhoaBieu> classes) {
        if (mClassesOfSession != null)
            mClassesOfSession.clear();

        mClassesOfSession = classes;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_session_of_day, viewGroup, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder viewHolder, int i) {
        ThoiKhoaBieu currentClass = mClassesOfSession.get(i);
        viewHolder.tvSubject.setText(currentClass.TenLopHocPhan);
        viewHolder.tvRoom.setText(currentClass.TenPhong);
        viewHolder.tvTeacher.setText(currentClass.HoVaTen);
        String timeStr = "Tiết " + currentClass.TietHocBatDau + " - " +
                currentClass.TietHocKetThuc + " ( " +
                currentClass.getLessionStartTimeStr() + " - " +
                currentClass.getLessionEndTimeStr() + " )";
        viewHolder.tvTime.setText(timeStr);
    }

    @Override
    public int getItemCount() {
        return mClassesOfSession != null ? mClassesOfSession.size() : 0;
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubject;
        private TextView tvRoom;
        private TextView tvTime;
        private TextView tvTeacher;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvRoom = itemView.findViewById(R.id.tv_room);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTeacher = itemView.findViewById(R.id.tv_teacher);
        }
    }
}
