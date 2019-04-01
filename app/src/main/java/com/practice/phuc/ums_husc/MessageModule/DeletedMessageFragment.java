package com.practice.phuc.ums_husc.MessageModule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.phuc.ums_husc.R;

public class DeletedMessageFragment extends Fragment {

    public DeletedMessageFragment() {
    }

    public static DeletedMessageFragment newInstance(Context context) {
        DeletedMessageFragment rmf = new DeletedMessageFragment();
        rmf.mContext = context;
        return rmf;
    }

    public Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deleted_message, container, false);
        return view;
    }
}
