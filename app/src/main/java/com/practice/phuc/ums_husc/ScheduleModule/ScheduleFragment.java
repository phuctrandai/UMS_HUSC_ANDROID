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
import com.practice.phuc.ums_husc.R;

public class ScheduleFragment extends Fragment{

    private Context mContext;
    private WeeksTabAdapter mFragmentsTabAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

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
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        mViewPager = view.findViewById(R.id.vp_schedule);
        mTabLayout = view.findViewById(R.id.tabs_schedule);

        setUpFragments();
        return view;
    }

    @Override
    public void onDestroyView() {
        mFragmentsTabAdapter.clearFragment();
        super.onDestroyView();
    }

    private void setUpFragments() {
        // CO bao nhieu tuan hoc = co bay nhieu page
        mFragmentsTabAdapter.addFragment(WeekFragment.newInstance(mContext, "1"), "Tuần 1");
        mFragmentsTabAdapter.addFragment(WeekFragment.newInstance(mContext, "2"), "Tuần 2");
        mFragmentsTabAdapter.addFragment(WeekFragment.newInstance(mContext, "3"), "Tuần 3");
        mFragmentsTabAdapter.addFragment(WeekFragment.newInstance(mContext, "4"), "Tuần 4");
        mFragmentsTabAdapter.addFragment(WeekFragment.newInstance(mContext, "5"), "Tuần 5");
        mFragmentsTabAdapter.addFragment(WeekFragment.newInstance(mContext, "6"), "Tuần 6");
        mFragmentsTabAdapter.addFragment(WeekFragment.newInstance(mContext, "7"), "Tuần 7");
        mViewPager.setAdapter(mFragmentsTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
