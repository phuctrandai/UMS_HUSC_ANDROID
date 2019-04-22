package com.practice.phuc.ums_husc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.Response;

public class ChangePasswordFragment extends Fragment {

    public ChangePasswordFragment() {
    }

    public static ChangePasswordFragment newInstance(Context context) {
        ChangePasswordFragment f = new ChangePasswordFragment();
        f.mContext = context;
        return f;
    }

    private Context mContext;
    private ChangePasswordTask mChangePasswordTask;
    private SharedPreferences mSharedPreferences;
    private boolean mIsDestroyView;

    private EditText mTxtOldPass;
    private EditText mTxtNewPass;
    private EditText mTxtRepPass;
    private Button mBtnChangePass;
    private ProgressBar mProgressBar;
    private ScrollView mRootLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mSharedPreferences = mContext.getSharedPreferences(getString(R.string.share_pre_key_account_info), Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        mTxtOldPass = view.findViewById(R.id.txt_oldPass);
        mTxtNewPass = view.findViewById(R.id.txt_newPass);
        mTxtRepPass = view.findViewById(R.id.txt_rePass);
        mBtnChangePass = view.findViewById(R.id.btn_changePass);
        mProgressBar = view.findViewById(R.id.pb_changePassProgress);
        mRootLayout = view.findViewById(R.id.change_pass_root_layout);
        mIsDestroyView = false;

        mBtnChangePass.setOnClickListener(btnChangePasswordClickListener);
        mProgressBar.bringToFront();

        return view;
    }

    @Override
    public void onPause() {
        mIsDestroyView = true;
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mIsDestroyView = true;
        super.onDestroyView();
    }

    @SuppressLint("StaticFieldLeak")
    public class ChangePasswordTask extends AsyncTask<String, Void, Boolean> {
        Response mResponse;
        String mErrorMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mChangePasswordTask == null) return false;

            mResponse = postChangePass();
            try {
                Thread.sleep(1000);

                if (mResponse == null) {
                    mErrorMessage = getString(R.string.error_time_out);
                    return false;
                } else if (mResponse.code() == NetworkUtil.NOT_FOUND) {
                    mErrorMessage = getString(R.string.error_server_not_response);
                    return false;
                } else if (mResponse.code() == NetworkUtil.BAD_REQUEST) {
                    mErrorMessage = mResponse.body() != null ? mResponse.body().string() : "";
                    return false;
                } else if (mResponse.code() == NetworkUtil.OK) {
                    return true;
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (mChangePasswordTask == null) return;

            if (success) {
                if (!mIsDestroyView)
                    Snackbar.make(mRootLayout, R.string.changePassSuccess, Snackbar.LENGTH_SHORT).show();
                saveNewPass();

            } else {
                if (!mIsDestroyView)
                    Snackbar.make(mRootLayout, mErrorMessage, Snackbar.LENGTH_SHORT).show();

            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mChangePasswordTask = null;
            super.onCancelled();
        }
    }

    private View.OnClickListener btnChangePasswordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final boolean validData = attempChangePass();

            if (validData && !mIsDestroyView) {
                if (NetworkUtil.getConnectivityStatus(mContext) == NetworkUtil.TYPE_NOT_CONNECTED) {

                    Toasty.custom(mContext, getString(R.string.error_network_disconected),
                            getResources().getDrawable(R.drawable.ic_signal_wifi_off_white_24),
                            getResources().getColor(R.color.colorRed),
                            getResources().getColor(android.R.color.white),
                            Toasty.LENGTH_LONG, true, true)
                            .show();
                    showProgress(false);
                } else {
                    changePass();
                }
            }
        }
    };

    private boolean attempChangePass() {
        mTxtOldPass.setError(null);
        mTxtNewPass.setError(null);
        mTxtRepPass.setError(null);

        String oldPass = mTxtOldPass.getText().toString();
        String newPass = mTxtNewPass.getText().toString();
        String repPass = mTxtRepPass.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(oldPass)) {
            Toasty.error(mContext, "Phải nhập mật khẩu cũ", Toasty.LENGTH_LONG).show();
            focusView = mTxtOldPass;
            cancel = true;

        } else if (!oldPass.equals(mSharedPreferences.getString(getString(R.string.pre_key_password), ""))) {
            Toasty.error(mContext, "Mật khẩu cũ không đúng", Toasty.LENGTH_LONG).show();
            focusView = mTxtOldPass;
            cancel = true;

        } else if (TextUtils.isEmpty(newPass)) {
            Toasty.error(mContext, "Phải nhập mật khẩu mới", Toasty.LENGTH_LONG).show();
            focusView = mTxtNewPass;
            cancel = true;

        } else if (newPass.length() < 6) {
            Toasty.error(mContext, "Mật khẩu mới quá ngắn", Toasty.LENGTH_LONG).show();
            focusView = mTxtNewPass;
            cancel = true;

        } else if (TextUtils.isEmpty(repPass)) {
            Toasty.error(mContext, "Phải nhập mật khẩu xác nhận", Toasty.LENGTH_LONG).show();
            focusView = mTxtRepPass;
            cancel = true;

        } else if (!repPass.equals(newPass)) {
            Toasty.error(mContext, "Xác nhận mật khẩu không đúng", Toasty.LENGTH_LONG).show();
            focusView = mTxtRepPass;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }

        return true;
    }

    private void changePass() {
        mChangePasswordTask = new ChangePasswordTask();
        mChangePasswordTask.execute((String) null);
    }

    private Response postChangePass() {
        SharedPreferences sp = mContext.getSharedPreferences(getString(R.string.share_pre_key_account_info), Context.MODE_PRIVATE);
        String maSinhVien = sp.getString(getString(R.string.pre_key_student_id), "");
        String matKhau = sp.getString(getString(R.string.pre_key_password), "");
        String url = Reference.getChangePassApiUrl(maSinhVien, matKhau);

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void saveNewPass() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(getString(R.string.pre_key_password), mTxtNewPass.getText().toString());
        editor.apply();

        mTxtOldPass.setText("");
        mTxtNewPass.setText("");
        mTxtRepPass.setText("");
    }

    private void showProgress(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mBtnChangePass.setText(show ? "" : getString(R.string.change_pass));
        mBtnChangePass.setOnClickListener(show ? null : btnChangePasswordClickListener);
        mTxtNewPass.setEnabled(!show);
        mTxtRepPass.setEnabled(!show);
        mTxtOldPass.setEnabled(!show);
    }
}
