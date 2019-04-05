package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.phuc.ums_husc.R;

import java.util.List;

public class SessionOfDayAdapter extends RecyclerView.Adapter<SessionOfDayAdapter.DataViewHolder> {

    private Context mContext;
    private List<String> mClassesOfSession; // Danh sach lop hoc co tiet vao buoi nay

    public SessionOfDayAdapter(Context context, List<String> classesOfSession) {
        mContext = context;
        mClassesOfSession = classesOfSession;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_session_of_day, viewGroup, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
//        return mClassesOfSession != null ? mClassesOfSession.size() : 0;
        return 1;
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {
        DataViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
