package com.practice.phuc.ums_husc.MessageModule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.phuc.ums_husc.Adapter.MessagePagerAdapter;
import com.practice.phuc.ums_husc.R;

public class MessageFragment extends Fragment {

    public MessageFragment() {}

    public static MessageFragment newInstance(Context context) {
        MessageFragment fragment = new MessageFragment();
        fragment.mContext = context;
        return fragment;
    }

    private MessagePagerAdapter mAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "On create MessageFragment");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("DEBUG", "On create VIEW Message Fragment");
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        bindUI(view);
        initAdapter();

        return view;
    }

    private void initAdapter() {
        mAdapter = new MessagePagerAdapter(getChildFragmentManager());
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void bindUI(View view) {
        mViewPager = view.findViewById(R.id.vp_message);
        mTabLayout = view.findViewById(R.id.tabs);
    }
}
