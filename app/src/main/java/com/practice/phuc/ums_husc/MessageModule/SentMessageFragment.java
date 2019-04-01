package com.practice.phuc.ums_husc.MessageModule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.phuc.ums_husc.R;

public class SentMessageFragment extends Fragment {

    public SentMessageFragment() {
    }

    public static SentMessageFragment newInstance(Context context) {
        SentMessageFragment rmf = new SentMessageFragment();
        rmf.mContext = context;
        return rmf;
    }

    public Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "On create SENT MESSAGE FRAGMENT");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("DEBUG", "On create VIEW Sent message fragment");
        View view = inflater.inflate(R.layout.fragment_sent_message, container, false);
        return view;
    }
}
