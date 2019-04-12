package com.practice.phuc.ums_husc.MessageModule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.practice.phuc.ums_husc.R;

public class MessageFragment extends Fragment {

    public MessageFragment() {
    }

    public static MessageFragment newInstance(Context context) {
        MessageFragment fragment = new MessageFragment();
        fragment.mContext = context;
        return fragment;
    }

    private Context mContext;
    private boolean mIsCreatedView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mIsCreatedView = false;
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        bindUI(view);

        if (!mIsCreatedView) {
            loadFragment(ReceivedMessageFragment.newInstance(mContext));
            mIsCreatedView = true;
        }

        return view;
    }

    private void bindUI(View view) {
        BottomNavigationView navigation = view.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_received:
                    loadFragment(ReceivedMessageFragment.newInstance(mContext));
                    return true;
                case R.id.navigation_sent:
                    loadFragment(SentMessageFragment.newInstance(mContext));
                    return true;
                case R.id.navigation_delete:
                    loadFragment(DeletedMessageFragment.newInstance(mContext));
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        String fragmentTag = fragment.getClass().getName();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        Fragment temp = getChildFragmentManager().findFragmentByTag(fragmentTag);
        if (temp != null) {
            ft.replace(R.id.frame_message_container, temp, fragmentTag);
        } else {
            ft.replace(R.id.frame_message_container, fragment, fragmentTag);
            ft.addToBackStack(fragmentTag);
        }
        ft.commit();
    }
}
