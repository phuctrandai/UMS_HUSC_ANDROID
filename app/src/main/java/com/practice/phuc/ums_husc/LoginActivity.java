package com.practice.phuc.ums_husc;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.common.GoogleApiAvailability;
import com.practice.phuc.ums_husc.Helper.FireBaseIDTask;
import com.practice.phuc.ums_husc.Helper.MyFireBaseMessagingService;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.ViewModel.VThongTinCaNhan;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Response mResponseLogin = null;
    private SharedPreferences mSharedPreferences = null;
    private UserLoginTask mAuthTask;
    private JsonAdapter<VThongTinCaNhan> mJsonAdapter;
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
        Type usersType = Types.newParameterizedType(VThongTinCaNhan.class);
        mJsonAdapter = moshi.adapter(usersType);
        mSharedPreferences = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
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
        } else if (!isMaSinhVienValid(maSinhVien)) {
            mTxtStudentId.setError(getString(R.string.error_ma_sinh_vien_khong_hop_le));
            focusView = mTxtStudentId;
            cancel = true;
        } else if (TextUtils.isEmpty(matKhau)) {
            mTxtPassword.setError(getString(R.string.error_truong_bat_buoc));
            focusView = mTxtPassword;
            cancel = true;
        } else if (!isPasswordValid(matKhau)) {
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

    private boolean isMaSinhVienValid(String maSinhVien) {
        return maSinhVien.contains("T") || maSinhVien.contains("t");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    @SuppressLint("StaticFieldLeak")
    public class UserLoginTask extends AsyncTask<String, Void, Boolean> {

        private final String mMaSinhVien;
        private final String mMatKhau;

        UserLoginTask(String email, String password) {
            mMaSinhVien = email;
            mMatKhau = password;
        }

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

            } catch (Exception e) {
                mResponseLogin = null;
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (mAuthTask == null || mIsViewDestroyed) return;

            if (mResponseLogin == null) {
                Snackbar.make(mRootLayout, R.string.error_server_not_response
                        , Snackbar.LENGTH_LONG).show();
                return;
            }

            if (success) {
                if (mResponseLogin.code() == NetworkUtil.OK) {
                    try {
                        if (mResponseLogin.body() != null) {
                            String thongTinCaNhanJson = mResponseLogin.body().string();
                            VThongTinCaNhan vThongTinCaNhan = mJsonAdapter.fromJson(thongTinCaNhanJson);

                            saveAccountInfo(vThongTinCaNhan, mMaSinhVien, mMatKhau);
                            saveTokenForAccount();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }
                        return;
                    } catch (IOException e) {
                        Snackbar.make(mRootLayout, R.string.error_not_handle,
                                Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                        return;
                    }

                } else if (mResponseLogin.code() == NetworkUtil.NOT_FOUND) {
                    Snackbar.make(mRootLayout, R.string.error_time_out,
                            Snackbar.LENGTH_LONG).show();

                } else if (mResponseLogin.code() == NetworkUtil.BAD_REQUEST) {
                    Snackbar.make(mRootLayout, R.string.error_login_failed,
                            Snackbar.LENGTH_LONG).show();
                    mTxtPassword.requestFocus();

                }
                Log.d("DEBUG", "Login: " + mResponseLogin.code());
                try {
                    assert mResponseLogin.body() != null;
                    Log.d("DEBUG", "Login: " + mResponseLogin.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar.make(mRootLayout, R.string.error_not_handle,
                        Snackbar.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

    private void saveAccountInfo(VThongTinCaNhan vThongTinCaNhan, String maSinhVien, String matKhau) {
        if (maSinhVien.contains("t"))
            maSinhVien = maSinhVien.replace('t', 'T');
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(getString(R.string.pre_key_account_id), vThongTinCaNhan.MaTaiKhoan);
        editor.putString(getString(R.string.pre_key_student_name), vThongTinCaNhan.HoTen);
        editor.putString(getString(R.string.pre_key_majors), vThongTinCaNhan.TenNganh);
        editor.putString(getString(R.string.pre_key_course), vThongTinCaNhan.KhoaHoc);
        editor.putString(getString(R.string.pre_key_student_id), maSinhVien);
        editor.putString(getString(R.string.pre_key_password), matKhau);
        editor.apply();
    }

    private void saveTokenForAccount() {
        final String maSinhVien = Reference.getStudentId(this);
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

