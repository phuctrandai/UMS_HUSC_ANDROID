package com.practice.phuc.ums_husc.MessageModule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.phuc.ums_husc.R;

public class ReceivedMessageFragment extends Fragment {

    public ReceivedMessageFragment() {
        // Required empty public constructor
    }

    public static ReceivedMessageFragment newInstance(Context context) {
        ReceivedMessageFragment rmf = new ReceivedMessageFragment();
        rmf.mContext = context;
        return rmf;
    }

    private Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_received_message, container, false);
        return view;
    }

}
