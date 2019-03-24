package com.practice.phuc.ums_husc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.ViewModel.VThongTinCaNhan;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Response responseLogin = null;

    // Keep login information
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor = null;

    // Cast json to model
    Moshi moshi;
    Type usersType;
    JsonAdapter<VThongTinCaNhan> jsonAdapter;

    // UI references.
    private LinearLayout linearLayout;
    private EditText txtMaSinhVien;
    private EditText txtMatKhau;
    private Button btnLogin;
    private ViewGroup mProgressViewLayout;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

        initAll();

        createNotificationChannel();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        if (localLogin()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
    }

    private void initAll() {
        bindUI();
        initLoginForm();

        moshi = new Moshi.Builder().build();
        usersType = Types.newParameterizedType(VThongTinCaNhan.class);
        jsonAdapter = moshi.adapter(usersType);
        // Lay thong tin dang nhap tu truoc
        sharedPreferences = getSharedPreferences("sinhVien", MODE_PRIVATE);
    }

    private void bindUI(){
        linearLayout = findViewById(R.id.linearLayout);
        mProgressViewLayout = findViewById(R.id.login_progress_layout);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        txtMaSinhVien = findViewById(R.id.txt_ma_sinh_vien);
        txtMatKhau = findViewById(R.id.txt_mat_khau);
        btnLogin = (Button) findViewById(R.id.btn_login);
    }

    private void initLoginForm() {
        // Set up the login form.
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
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.getConnectivityStatus(LoginActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    Snackbar snackbar = Snackbar.make(linearLayout,
                            getString(R.string.network_not_available), Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    attemptLogin();
                }
            }
        });
    }

    // Thuc hien validate thong tin dang nhap
    // Neu hop le, tien hanh dang nhap
    private void attemptLogin() {
        // Reset errors.
        txtMaSinhVien.setError(null);
        txtMatKhau.setError(null);

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
        } else {

            UserLoginTask mAuthTask = new UserLoginTask(maSinhVien, matKhau);
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

    // Thuc hien kiem tra thong tin dang nhap
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
            try {
                Thread.sleep(500);
                responseLogin = onlineLogin(mMaSinhVien, mMatKhau);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d("DEBUG", success + " - do login");

            if (success && (responseLogin != null)) {
                Log.d("DEBUG", "Login response code: " + responseLogin.code());
                if (responseLogin.code() == NetworkUtil.OK) {
                    try {
                        String thongTinCaNhanJson = responseLogin.body().string();
                        VThongTinCaNhan vThongTinCaNhan = jsonAdapter.fromJson(thongTinCaNhanJson);

                        // Luu thong tin dang nhap, se tu dong dang nhap cho lan sau
                        editor = sharedPreferences.edit();
                        editor.putString("hoTen", vThongTinCaNhan.getHoTen());
                        editor.putString("nganhHoc", vThongTinCaNhan.getTenNganh());
                        editor.putString("khoaHoc", vThongTinCaNhan.getKhoaHoc());
                        editor.putString("maSinhVien", mMaSinhVien);
                        editor.putString("matKhau", mMatKhau);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (responseLogin.code() == NetworkUtil.NOT_FOUND) {
                    Snackbar.make(linearLayout,
                            getString(R.string.error_dang_nhap_that_bai),
                            Snackbar.LENGTH_LONG).show();
                    txtMatKhau.requestFocus();
                } else {
                    Snackbar.make(linearLayout,
                            getString(R.string.error_server_not_response),
                            Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(linearLayout,
                        getString(R.string.error_server_not_response),
                        Snackbar.LENGTH_LONG).show();
            }
            showProgress(false);
        }
    }

    // Dang nhap voi thong tin da luu tren may
    private boolean localLogin() {
        // Kiem tra da dang nhap truoc do chua
        SharedPreferences sp = LoginActivity.this.getSharedPreferences("sinhVien", MODE_PRIVATE);
        String maSinhVien = sp.getString("maSinhVien", null);
        String matKhau = sp.getString("matKhau", null);

        if ((maSinhVien != null) && (matKhau != null))
            return true;

        return false;
    }

    // Dang nhap voi thong tin nhap vao
    private Response onlineLogin(String maSinhVien, String matKhau) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/xml; charset=utf-8"), ""
        );
        // Lay response
        return NetworkUtil.makeRequest(Reference.getLoginApiUrl(maSinhVien, matKhau),
                true,
                requestBody);
    }

    // Hien thi hinh anh dang tai du lieu
    private void showProgress(final boolean show) {
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressViewLayout.setVisibility(show ? View.VISIBLE : View.GONE);
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

