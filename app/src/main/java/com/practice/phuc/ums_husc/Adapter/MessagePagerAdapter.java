package com.practice.phuc.ums_husc.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.practice.phuc.ums_husc.MessageModule.DeletedMessageFragment;
import com.practice.phuc.ums_husc.MessageModule.ReceivedMessageFragment;
import com.practice.phuc.ums_husc.MessageModule.SentMessageFragment;

public class MessagePagerAdapter extends FragmentPagerAdapter {

    public MessagePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                ReceivedMessageFragment r = new ReceivedMessageFragment();
                return r;
            case 1:
                SentMessageFragment s = new SentMessageFragment();
                return s;
            default:
                DeletedMessageFragment d = new DeletedMessageFragment();
                return d;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Đã nhận";
            case 1:
                return "Đã gửi";
            case 2:
                return "Đã xóa";
        }
        return null;
    }
}
