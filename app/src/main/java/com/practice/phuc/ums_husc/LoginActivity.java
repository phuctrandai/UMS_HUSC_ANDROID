package com.practice.phuc.ums_husc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.SINHVIEN;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private UserLoginTask mAuthTask = null;

    // Keep login information
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor = null;

    // UI references.
    private AutoCompleteTextView txtMaSinhVien;
    private EditText txtMatKhau;
    private ViewGroup mProgressViewLayout;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        txtMaSinhVien = (AutoCompleteTextView) findViewById(R.id.txt_ma_sinh_vien);
        txtMatKhau = (EditText) findViewById(R.id.txt_mat_khau);
        txtMatKhau.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mProgressViewLayout = findViewById(R.id.login_progress_layout);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Lay thong tin dang nhap tu truoc
        sharedPreferences = getSharedPreferences("sinhVien", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (localLogin()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    }


    // Thuc hien validate thong tin dang nhap
    // Neu hop le, tien hanh dang nhap
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        txtMaSinhVien.setError(null);
        txtMatKhau.setError(null);

        // Store values at the time of the login attempt.
        String maSinhVien = txtMaSinhVien.getText().toString();
        String matKhau = txtMatKhau.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Validate ma sinh vien
        if (TextUtils.isEmpty(maSinhVien)) {
            txtMaSinhVien.setError(getString(R.string.error_truong_bat_buoc));
            focusView = txtMaSinhVien;
            cancel = true;
        } else if (!isMaSinhVienValid(maSinhVien)) {
            txtMaSinhVien.setError(getString(R.string.error_ma_sinh_vien_khong_hop_le));
            focusView = txtMaSinhVien;
            cancel = true;
        }
        // Validate mat khau
        else if (TextUtils.isEmpty(matKhau)) {
            txtMatKhau.setError(getString(R.string.error_truong_bat_buoc));
            focusView = txtMatKhau;
            cancel = true;
        } else if (!isPasswordValid(matKhau)) {
            txtMatKhau.setError(getString(R.string.error_mat_khau_qua_ngan));
            focusView = txtMatKhau;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else if (NetworkUtil.getConnectivityStatus(LoginActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED) {
            Toast.makeText(this, getString(R.string.network_not_available), Toast.LENGTH_SHORT).show();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(maSinhVien, matKhau);
            mAuthTask.execute((String) null);
        }
    }

    // Validate ma sinh vien
    private boolean isMaSinhVienValid(String maSinhVien) {
        return maSinhVien.contains("T");
    }

    // Validate mat khau
    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    // Hien thi hinh anh dang tai du lieu
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressViewLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressViewLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    // Thuc hien kiem tra thong tin dang nhap
    public class UserLoginTask extends AsyncTask<String, Void, Boolean> {

        private final String mMaSinhVien;
        private final String mMatKhau;

        UserLoginTask(String email, String password) {
            mMaSinhVien = email;
            mMatKhau = password;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result;
            try {
                Thread.sleep(1500);

                if (mMaSinhVien == null && mMatKhau == null) {
                    result = localLogin();
                } else
                    result = postLogin(mMaSinhVien, mMatKhau);
            } catch (Exception e) {
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            } else {
                showProgress(false);
                txtMatKhau.setError(getString(R.string.error_dang_nhap_that_bai));
                txtMatKhau.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    // Dang nhap voi thong tin da luu tren may
    private boolean localLogin() {
        // Kiem tra da dang nhap truoc do chua
        SharedPreferences sp = LoginActivity.this.getSharedPreferences("sinhVien", MODE_PRIVATE);
        String maSinhVien = sp.getString("maSinhVien", null);
        String matKhau = sp.getString("matKhau", null);

        if (maSinhVien != null && matKhau != null)
            return true;

        return false;
    }

    // Dang nhap voi thong tin nhap vao
    public boolean postLogin(String maSinhVien, String matKhau) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        boolean isSuccess = false;

        // Tao doi tuong sinh vien dang json
        String sinhVienJson = new SINHVIEN(maSinhVien, matKhau).toJSON();
        // Dua vao request body
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), sinhVienJson);
        // Tao request
        Request request = new Request.Builder()
                .url(Reference.HOST + Reference.LOGIN_API).post(body).build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response != null) {
                Log.d("UMS_HUSC", response.code() + response.body().string());
                isSuccess = response.code() == Reference.OK;
            }
        } catch (IOException e) {
            e.printStackTrace();
            isSuccess = false;
        }
        // Luu thong tin dang nhap, se tu dong dang nhap cho lan sau
        editor.putString("maSinhVien", maSinhVien);
        editor.putString("matKhau", matKhau);
        editor.commit();

        return isSuccess;
    }
}

