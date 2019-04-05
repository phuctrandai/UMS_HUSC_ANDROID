package com.practice.phuc.ums_husc.ScheduleModule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.phuc.ums_husc.Adapter.DayOfWeekAdapter;
import com.practice.phuc.ums_husc.R;

public class WeekFragment extends Fragment {

    private Context mContext;
    private DayOfWeekAdapter mDayOfWeekAdapter;
    private RecyclerView mRvDayOfWeek;
    private String mDebugTitle;

    public WeekFragment() {
    }

    public static WeekFragment newInstance(Context context, String debugTitle) {
        WeekFragment df = new WeekFragment();
        df.mContext = context;
        df.mDebugTitle = debugTitle;
        return df;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("DEBUG", "On Create Fragment Date: " + mDebugTitle);
        mDayOfWeekAdapter = new DayOfWeekAdapter(mContext, null);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("DEBUG", "On Create View Fragment Date: " + mDebugTitle);
        View view = inflater.inflate(R.layout.fragment_week, container, false);
        mRvDayOfWeek = view.findViewById(R.id.rv_dayOfWeek);

        setUpRecyclerView();
        return view;
    }

    @Override
    public void onDestroy() {
        Log.d("DEBUG", "On Destroy Fragment Date: " + mDebugTitle);
        super.onDestroy();
    }

    private void setUpRecyclerView() {
        final LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mRvDayOfWeek.setLayoutManager(manager);
        mRvDayOfWeek.setHasFixedSize(true);
        mRvDayOfWeek.setAdapter(mDayOfWeekAdapter);
    }
}
