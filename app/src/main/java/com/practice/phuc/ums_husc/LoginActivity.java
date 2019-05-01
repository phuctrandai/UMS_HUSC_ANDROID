package com.practice.phuc.ums_husc;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.common.GoogleApiAvailability;
import com.practice.phuc.ums_husc.Service.FireBaseIDTask;
import com.practice.phuc.ums_husc.Service.MyFireBaseMessagingService;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.ScheduleModule.ScheduleTaskHelper;
import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.ViewModel.ThongTinCaNhan;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private UserLoginTask mAuthTask;
    private JsonAdapter<ThongTinCaNhan> mJsonAdapter;
    private boolean mIsViewDestroyed;

    private LinearLayout mRootLayout;
    private EditText mTxtStudentId;
    private EditText mTxtPassword;
    private Button mBtnLogin;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mIsViewDestroyed = false;

        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

        initAll();

        createNotificationChannel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
    }

    @Override
    protected void onDestroy() {
        mIsViewDestroyed = true;
        if (mAuthTask != null) {
            mAuthTask.cancel(true);
            mAuthTask = null;
        }
        super.onDestroy();
    }

    private void initAll() {
        bindUI();
        initLoginForm();

        Moshi moshi = new Moshi.Builder().build();
        Type usersType = Types.newParameterizedType(ThongTinCaNhan.class);
        mJsonAdapter = moshi.adapter(usersType);
    }

    private void bindUI() {
        mRootLayout = findViewById(R.id.linearLayout);
        mProgressView = findViewById(R.id.login_progress);
        mTxtStudentId = findViewById(R.id.txt_ma_sinh_vien);
        mTxtPassword = findViewById(R.id.txt_mat_khau);
        mBtnLogin = findViewById(R.id.btn_login);
    }

    private void initLoginForm() {
        mBtnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.getConnectivityStatus(LoginActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED && !mIsViewDestroyed) {
                    Snackbar snackbar = Snackbar.make(mRootLayout,
                            getString(R.string.error_network_disconected), Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    attemptLogin();
                }
            }
        });
    }

    private void attemptLogin() {
        mTxtStudentId.setError(null);
        mTxtPassword.setError(null);

        String maSinhVien = mTxtStudentId.getText().toString();
        String matKhau = mTxtPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(maSinhVien)) {
            mTxtStudentId.setError(getString(R.string.error_truong_bat_buoc));
            focusView = mTxtStudentId;
            cancel = true;
        } else if (!(maSinhVien.contains("T") || maSinhVien.contains("t"))) {
            mTxtStudentId.setError(getString(R.string.error_ma_sinh_vien_khong_hop_le));
            focusView = mTxtStudentId;
            cancel = true;
        } else if (TextUtils.isEmpty(matKhau)) {
            mTxtPassword.setError(getString(R.string.error_truong_bat_buoc));
            focusView = mTxtPassword;
            cancel = true;
        } else if (!(matKhau.length() >= 6)) {
            mTxtPassword.setError(getString(R.string.error_mat_khau_qua_ngan));
            focusView = mTxtPassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {
            mAuthTask = new UserLoginTask(maSinhVien, matKhau);
            mAuthTask.execute((String) null);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class UserLoginTask extends AsyncTask<String, Void, Boolean> {

        private final String mMaSinhVien;
        private final String mMatKhau;

        UserLoginTask(String email, String password) {
            mMaSinhVien = email;
            mMatKhau = password;
        }

        private ThongTinCaNhan thongTinCaNhan;
        private Response mResponseLogin;
        private String mErrorMessage;
        private String mJson;

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (mAuthTask == null) return false;

            try {
                Thread.sleep(1000);
                mResponseLogin = onlineLogin(mMaSinhVien, mMatKhau);

                if (mResponseLogin == null) {
                    mErrorMessage = getString(R.string.error_server_not_response);
                    return false;
                }

                if (mResponseLogin.code() == NetworkUtil.OK) {
                    mJson = mResponseLogin.body() != null ? mResponseLogin.body().string() : "";
                    thongTinCaNhan = mJsonAdapter.fromJson(mJson);

                    if (thongTinCaNhan == null) {
                        mErrorMessage = "Có lỗi xảy ra, thử lại sau";
                        return false;

                    } else {
                        ScheduleTaskHelper.getInstance().fetchSchedule(LoginActivity.this, mMaSinhVien, mMatKhau,
                                Integer.parseInt(thongTinCaNhan.HocKyTacNghiep.MaHocKy));
                        return true;
                    }
                }

                if (mResponseLogin.code() == NetworkUtil.NOT_FOUND) {
                    mErrorMessage = getString(R.string.error_time_out);
                    return false;
                }

                if (mResponseLogin.code() == NetworkUtil.BAD_REQUEST) {
                    mErrorMessage = getString(R.string.error_login_failed);
                    return false;
                }

            } catch (Exception e) {
                mResponseLogin = null;
                mErrorMessage = "Có lỗi bất ngờ xảy ra, thử lại sau";
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (mAuthTask == null || mIsViewDestroyed) return;

            if (success) {
                saveAccountInfo(thongTinCaNhan, mMaSinhVien, mMatKhau);
                saveTokenForAccount();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();

            } else {
                Snackbar.make(mRootLayout, mErrorMessage, Snackbar.LENGTH_LONG).show();
            }

            showProgress(false);
        }
    }

    private void saveAccountInfo(ThongTinCaNhan thongTinCaNhan, String maSinhVien, String matKhau) {
        if (maSinhVien.contains("t"))
            maSinhVien = maSinhVien.replace('t', 'T');
        SharedPreferenceHelper instance = SharedPreferenceHelper.getInstance();
        instance.setSharedPref(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.ACCOUNT_ID, thongTinCaNhan.MaTaiKhoan);
        instance.setSharedPref(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.ACCOUNT_NAME, thongTinCaNhan.HoTen);
        instance.setSharedPref(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_MAJORS, thongTinCaNhan.TenNganh);
        instance.setSharedPref(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_COURSE, thongTinCaNhan.KhoaHoc);
        instance.setSharedPref(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.ACCOUNT_PASSWORD, matKhau);
        instance.setSharedPref(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_ID, maSinhVien);
        instance.setSharedPref(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER, thongTinCaNhan.HocKyTacNghiep.MaHocKy);
        instance.setSharedPref(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER_STR, thongTinCaNhan.HocKyTacNghiep.toString());
    }

    private void saveTokenForAccount() {
        final String maSinhVien = SharedPreferenceHelper.getInstance().getSharedPrefStr(
                this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_ID, ""
        );
        String token = MyFireBaseMessagingService.getToken(this);
        FireBaseIDTask.saveTokenForAccount(maSinhVien, token);
    }

    private Response onlineLogin(String maSinhVien, String matKhau) {
        return NetworkUtil.makeRequest(Reference.getLoginApiUrl(maSinhVien, matKhau),
                false, null);
    }

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mBtnLogin.setVisibility(show ? View.GONE : View.VISIBLE);
        mTxtStudentId.setEnabled(!show);
        mTxtPassword.setEnabled(!show);
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(getString(R.string.chanel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

