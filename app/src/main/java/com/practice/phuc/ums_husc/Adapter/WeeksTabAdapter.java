package com.practice.phuc.ums_husc.Adapter;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class WeeksTabAdapter extends FragmentStatePagerAdapter {
    private final FragmentManager mManager;
    private final List<Fragment> mFragmentList;
    private final List<String> mFragmentTitleList;

    public WeeksTabAdapter(FragmentManager fm) {
        super(fm);
        mManager = fm;
        mFragmentList = new ArrayList<>();
        mFragmentTitleList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList != null ? mFragmentList.get(i) : null;
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList != null ? mFragmentTitleList.get(position) : null;
    }

    @Override
    public int getCount() {
        return mFragmentList != null ? mFragmentList.size() : 0;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {

    }

    public void clearFragment() {
        FragmentTransaction ft = mManager.beginTransaction();

        for(Fragment item : mFragmentList){
            if (item != null) {
                ft.remove(item);
            }
        }
        ft.commit();
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }
}
