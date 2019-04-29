package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
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

    private Context mContext;
    private List<ThoiKhoaBieu> mClassesOfSession; // Danh sach lop hoc co tiet vao buoi nay

    SessionOfDayAdapter(Context context) {
        mContext = context;
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
                getLessionStartTimeStr(currentClass.TietHocBatDau) + " - " +
                getLessionEndTimeStr(currentClass.TietHocKetThuc) + " )";
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

    private String getLessionStartTimeStr(int lession) {
        switch (lession) {
            case 1:
                return "7:00";
            case 2:
                return "8:00";
            case 3:
                return "9:00";
            case 4:
                return "10:00";
            case 5:
                return "13:00";
            case 6:
                return "14:00";
            case 7:
                return "15:00";
            case 8:
                return "16:00";
            case 9:
                return "17:30";
            case 10:
                return "18:25";
            case 11:
                return "19:20";
            case 12:
                return "20:15";
            default:
                return null;
        }
    }

    private String getLessionEndTimeStr(int lession) {
        switch (lession) {
            case 1:
                return "7:50";
            case 2:
                return "8:50";
            case 3:
                return "9:50";
            case 4:
                return "10:50";
            case 5:
                return "13:50";
            case 6:
                return "14:50";
            case 7:
                return "15:50";
            case 8:
                return "16:50";
            case 9:
                return "18:20";
            case 10:
                return "19:15";
            case 11:
                return "20:10";
            case 12:
                return "21:00";
            default:
                return null;
        }
    }
}
