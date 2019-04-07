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
import android.widget.RelativeLayout;

import com.practice.phuc.ums_husc.Helper.NetworkUtil;

import java.io.IOException;

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
    private RelativeLayout mRootLayout;

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
                    mErrorMessage = getString(R.string.error_server_not_response);
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

                    Snackbar.make(mRootLayout, getString(R.string.network_not_available), Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.try_again), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    changePass();
                                }
                            })
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
            mTxtOldPass.setError(getString(R.string.error_truong_bat_buoc));
            focusView = mTxtOldPass;
            cancel = true;
        } else if (!oldPass.equals(mSharedPreferences.getString(getString(R.string.share_pre_key_account_password), ""))) {
            mTxtOldPass.setError(getString(R.string.error_old_pass_incorect));
            focusView = mTxtOldPass;
            cancel = true;
        } else if (TextUtils.isEmpty(newPass)) {
            mTxtNewPass.setError(getString(R.string.error_truong_bat_buoc));
            focusView = mTxtNewPass;
            cancel = true;
        } else if (newPass.length() < 6) {
            mTxtNewPass.setError(getString(R.string.error_mat_khau_qua_ngan));
            focusView = mTxtNewPass;
            cancel = true;
        } else if (TextUtils.isEmpty(repPass)) {
            mTxtRepPass.setError(getString(R.string.error_truong_bat_buoc));
            focusView = mTxtRepPass;
            cancel = true;
        } else if (!repPass.equals(newPass)) {
            mTxtRepPass.setError(getString(R.string.error_rep_password_incorrect));
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
        String url = "";

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void saveNewPass() {
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        editor.putString("matKhau", mTxtNewPass.getText().toString());
//        editor.apply();

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
