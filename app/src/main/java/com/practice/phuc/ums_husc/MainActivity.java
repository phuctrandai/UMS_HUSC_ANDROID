package com.practice.phuc.ums_husc;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.DBHelper;
import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.FireBaseIDTask;
import com.practice.phuc.ums_husc.Helper.MessageTaskHelper;
import com.practice.phuc.ums_husc.Helper.MyFireBaseMessagingService;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.Helper.StringHelper;
import com.practice.phuc.ums_husc.MessageModule.DetailMessageActivity;
import com.practice.phuc.ums_husc.MessageModule.MessageFragment;
import com.practice.phuc.ums_husc.NewsModule.DetailNewsActivity;
import com.practice.phuc.ums_husc.NewsModule.MainFragment;
import com.practice.phuc.ums_husc.ResumeModule.ResumeFragment;
import com.practice.phuc.ums_husc.ScheduleModule.ScheduleFragment;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private FragmentManager fragmentManager;
    private int currentNavItem;
    private String currentFragment;
    private String mPrevFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean mIsLogined = localLogin();
        if (!mIsLogined) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            this.finish();

        } else {
            initAll();
            showAccountInfo();
            fragmentManager = getSupportFragmentManager();

            boolean mIsLaunchFromNewsNoti = getIntent().getBooleanExtra(Reference.BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, false);
            boolean mIsLaunchFromMessageNoti = getIntent().getBooleanExtra(Reference.BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI, false);

            if (mIsLaunchFromMessageNoti) {
                initFragment(MessageFragment.newInstance(this));

                String messageJson = getIntent().getStringExtra(Reference.BUNDLE_EXTRA_MESSAGE);
                Intent intent = new Intent(this, DetailMessageActivity.class);
                intent.putExtra(Reference.BUNDLE_EXTRA_MESSAGE, messageJson);
                intent.putExtra(Reference.BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI, true);
                startActivity(intent);

            } else if (mIsLaunchFromNewsNoti) {
                initFragment(MainFragment.newInstance(this));

                String newsJson = getIntent().getStringExtra(Reference.BUNDLE_EXTRA_NEWS);
                Intent intent = new Intent(this, DetailNewsActivity.class);
                intent.putExtra(Reference.BUNDLE_EXTRA_NEWS, newsJson);
                intent.putExtra(Reference.BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, true);
                startActivity(intent);

            } else {
                initFragment(MainFragment.newInstance(this));

            }
        }
    }

    @Override
    protected void onResume() {
        MyFireBaseMessagingService.mContext = this;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        MyFireBaseMessagingService.mContext = null;
        MessageTaskHelper.getInstance().destroy();
        Reference.clearListNewSentMessage();
        Reference.clearListNewReceivedMessage();
        Reference.clearListNewThongBao();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (currentFragment.equals(MainFragment.class.getName())) {
            MainFragment mainFragment = (MainFragment) fragmentManager.findFragmentByTag(currentFragment);
            if (mainFragment != null && mainFragment.findFirstVisibleItemPosition() > 0) {
                mainFragment.smoothScrollToTop();

            } else {
                confirmExit();

            }
        } else if (currentFragment.equals(SettingFragment.class.getName())
                || currentFragment.equals(ResumeFragment.class.getName())
                || currentFragment.equals(ChangePasswordFragment.class.getName())) {

            if (currentFragment.equals(SettingFragment.class.getName())) {
                String semesterStr = SharedPreferenceHelper.getInstance()
                        .getSharedPrefStr(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER_STR, "");
                updateSemester(semesterStr);
            }

            if (mPrevFragment.equals(MainFragment.class.getName()))
                replaceFragment(MainFragment.newInstance(this));

            else if (mPrevFragment.equals(MessageFragment.class.getName()))
                replaceFragment(MessageFragment.newInstance(this));

            else if (mPrevFragment.equals(ScheduleFragment.class.getName()))
                replaceFragment(ScheduleFragment.newInstance(this));

        } else {
            replaceFragment(MainFragment.newInstance(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        boolean isScheduleFrag = currentFragment.equals(ScheduleFragment.class.getName());
        boolean isResumeFrag = currentFragment.equals(ResumeFragment.class.getName());
        boolean isMessageFrag = currentFragment.equals(MessageFragment.class.getName());

        menu.findItem(R.id.action_goToToday).setVisible(isScheduleFrag);
        menu.findItem(R.id.action_refreshSchedule).setVisible(isScheduleFrag);
        menu.findItem(R.id.action_selectSemester).setVisible(isScheduleFrag);
        menu.findItem(R.id.action_refreshResume).setVisible(isResumeFrag);
        menu.findItem(R.id.action_newMessage).setVisible(isMessageFrag);
        menu.findItem(R.id.action_searchMessage).setVisible(isMessageFrag);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        if (currentNavItem != id) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (id) {
                        case R.id.nav_news:
                            replaceFragment(MainFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_timetable:
                            replaceFragment(ScheduleFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_message:
                            replaceFragment(MessageFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_setting:
                            replaceFragment(SettingFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_resume:
                            replaceFragment(ResumeFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_change_password:
                            replaceFragment(ChangePasswordFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_sign_out:
                            logOut();
                            break;
                        case R.id.nav_app_info:

                            break;
                        default:
                            break;
                    }
                }
            }, 400);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void replaceFragment(Fragment fragment) {
        String fragmentTag = fragment.getClass().getName();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        updateByFragmentTag(fragmentTag);

        Fragment temp = fragmentManager.findFragmentByTag(fragmentTag);
        if (temp != null) {
            ft.replace(R.id.frame_layout, temp, fragmentTag);
        } else {
            ft.replace(R.id.frame_layout, fragment, fragmentTag);
            ft.addToBackStack(fragmentTag);
        }
        ft.commit();
    }

    private void updateByFragmentTag(String fragmentTag) {
        mPrevFragment = currentFragment;
        currentFragment = fragmentTag;
        invalidateOptionsMenu();

        if (fragmentTag.equals(MainFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_news));
            navigationView.setCheckedItem(R.id.nav_news);
            currentNavItem = R.id.nav_news;
            showDrawerButton(true);

        } else if (fragmentTag.equals(MessageFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_message));
            navigationView.setCheckedItem(R.id.nav_message);
            currentNavItem = R.id.nav_message;
            showDrawerButton(true);

        } else if (fragmentTag.equals(ScheduleFragment.class.getName())) {
            String hocKyStr = SharedPreferenceHelper.getInstance()
                    .getSharedPrefStr(this,
                            SharedPreferenceHelper.ACCOUNT_SP,
                            SharedPreferenceHelper.STUDENT_SEMSTER_STR, "");
            setTitle(hocKyStr);
            navigationView.setCheckedItem(R.id.nav_timetable);
            currentNavItem = R.id.nav_timetable;
            showDrawerButton(true);

        } else if (fragmentTag.equals(ResumeFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_resume));
            showDrawerButton(false);
            currentNavItem = R.id.nav_resume;

        } else if (fragmentTag.equals(SettingFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_setting));
            showDrawerButton(false);
            currentNavItem = R.id.nav_setting;

        } else if (fragmentTag.equals(ChangePasswordFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_change_password));
            showDrawerButton(false);
            currentNavItem = R.id.nav_change_password;
        }
    }

    private void initAll() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.title_nav_news));

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initFragment(Fragment fragment) {
        currentNavItem = R.id.nav_news;
        currentFragment = fragment.getClass().getName();
        mPrevFragment = null;
        navigationView.setCheckedItem(R.id.nav_news);
        replaceFragment(fragment);
    }

    private void showAccountInfo() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvMaSinhVien = headerView.findViewById(R.id.tv_maSinhVien);
        TextView tvHoTen = headerView.findViewById(R.id.tv_hoTen);
        TextView tvKhoaHoc = headerView.findViewById(R.id.tv_khoaHoc);
        TextView tvNganhHoc = headerView.findViewById(R.id.tv_nganhHoc);
        TextView tvHocKiNamHoc = headerView.findViewById(R.id.tv_hocKiNamHoc);

        String maSinhVien = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_ID, "");
        String hoTen = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.ACCOUNT_NAME, "");
        String khoaHoc = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_COURSE, "");
        String nganhHoc = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_MAJORS, "");
        String hocKi = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_SEMSTER_STR, "");

        tvMaSinhVien.setText(maSinhVien);
        tvHoTen.setText(hoTen);
        tvKhoaHoc.setText(khoaHoc);
        tvNganhHoc.setText(nganhHoc);
        tvHocKiNamHoc.setText(hocKi);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        String message = "Xin chào " + hoTen;
        Snackbar snackbar = Snackbar.make(drawerLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean localLogin() {
        String maSinhVien = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_ID, "");
        String matKhau = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.ACCOUNT_PASSWORD, "");

        return (!StringHelper.isNullOrEmpty(maSinhVien)) && (!StringHelper.isNullOrEmpty(matKhau));
    }

    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn chắc chắn muốn đăng xuất ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete firbase token
                String maSinhVien = SharedPreferenceHelper.getInstance()
                        .getSharedPrefStr(MainActivity.this, SharedPreferenceHelper.ACCOUNT_SP, SharedPreferenceHelper.STUDENT_ID, "");
                String token = SharedPreferenceHelper.getInstance()
                        .getSharedPrefStr(MainActivity.this, "FIREBASE", "TOKEN", "");
                FireBaseIDTask.deleteTokenFromAccount(maSinhVien, token);
                // Clear all notification
                NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
                nm.cancelAll();
                // Delete account info
                getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE).edit().clear().apply();
                // Delete schedule
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                dbHelper.deleteAllRecord(DBHelper.SCHEDULE);
                dbHelper.deleteAllRecord(DBHelper.NEWS);
                // Start login screen
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void confirmExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Thoát ứng dụng ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showDrawerButton(boolean show) {
        if (show) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
            toggle.syncState();
        } else {
            toggle.setDrawerIndicatorEnabled(false);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void updateSemester(String semesterStr) {
        setTitle(semesterStr);
        View headerView = navigationView.getHeaderView(0);
        TextView tvHocKiNamHoc = headerView.findViewById(R.id.tv_hocKiNamHoc);
        tvHocKiNamHoc.setText(semesterStr);
        tvHocKiNamHoc.refreshDrawableState();
    }
}
