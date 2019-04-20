package com.practice.phuc.ums_husc;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.phuc.ums_husc.Helper.FireBaseIDTask;
import com.practice.phuc.ums_husc.Helper.MyFireBaseMessagingService;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.MessageModule.DetailMessageActivity;
import com.practice.phuc.ums_husc.MessageModule.MessageFragment;
import com.practice.phuc.ums_husc.MessageModule.SendMessageActivity;
import com.practice.phuc.ums_husc.NewsModule.DetailNewsActivity;
import com.practice.phuc.ums_husc.NewsModule.MainFragment;
import com.practice.phuc.ums_husc.ResumeModule.ResumeFragment;
import com.practice.phuc.ums_husc.ScheduleModule.ScheduleFragment;

import java.util.Objects;

import static com.practice.phuc.ums_husc.Helper.ScheduleDailyNotification.getScheduleTime;
import static com.practice.phuc.ums_husc.Helper.ScheduleDailyNotification.setUpScheduleAlarm;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private String currentFragment;
    private FragmentManager fragmentManager;
    private int currentNavItem;
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
            initFragmentManager();
            showAccountInfo();

            SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.share_pre_key_setting), Context.MODE_PRIVATE);
            boolean mSpTimeTableChecked = mSharedPreferences.getBoolean(getString(R.string.share_pre_key_alarm_timetable), true);
            if (mSpTimeTableChecked)
                setUpScheduleAlarm(this, getScheduleTime(null));

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
        menu.findItem(R.id.action_refreshResume).setVisible(isResumeFrag);
        menu.findItem(R.id.action_newMessage).setVisible(isMessageFrag);
        menu.findItem(R.id.action_selectSemester).setVisible(!(isMessageFrag || isResumeFrag));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_goToToday:
                ScheduleFragment scheduleFragment = (ScheduleFragment) fragmentManager.findFragmentByTag(ScheduleFragment.class.getName());
                if (scheduleFragment != null) {
                    scheduleFragment.goToToday();
                }
                return true;

            case R.id.action_refreshResume:
                ResumeFragment resumeFragment = (ResumeFragment) fragmentManager.findFragmentByTag(ResumeFragment.class.getName());
                if (resumeFragment != null) {
                    resumeFragment.onRefresh();
                }
                return true;

            case R.id.action_selectSemester:
                showSelectSemesterDialog();
                return true;

            case R.id.action_newMessage:
                Intent intent = new Intent(this, SendMessageActivity.class);
                intent.putExtra(Reference.BUNDLE_EXTRA_MESSAGE_NEW, true);
                startActivity(intent);
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
            setTitle(getString(R.string.title_nav_timetable));
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

    private void initFragmentManager() {
        fragmentManager = getSupportFragmentManager();
//        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                Fragment fragment = fragmentManager.findFragmentById(R.id.frame_layout);
//                if (fragment != null) {
//                    updateByFragmentTag(fragment.getTag());
//                }
//            }
//        });
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

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        String maSinhVien = sharedPreferences.getString(getString(R.string.pre_key_student_id), "");
        String hoTen = sharedPreferences.getString(getString(R.string.pre_key_student_name), "");
        String khoaHoc = sharedPreferences.getString(getString(R.string.pre_key_course), "");
        String nganhHoc = sharedPreferences.getString(getString(R.string.pre_key_majors), "");
        int index = sharedPreferences.getInt(getString(R.string.pre_key_semester), 0);
        String hocKi = semesters[index].toString();

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
        String maSinhVien = Reference.getStudentId(this);
        String matKhau = Reference.getAccountPassword(this);

        return (maSinhVien != null) && (matKhau != null);
    }

    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn chắc chắn muốn đăng xuất ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteTokenForAccount();

                clearAllNotification();

                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE).edit();
                editor.remove(getString(R.string.pre_key_account_id));
                editor.remove(getString(R.string.pre_key_student_id));
                editor.remove(getString(R.string.pre_key_password));
                editor.remove(getString(R.string.pre_key_student_name));
                editor.remove(getString(R.string.pre_key_majors));
                editor.remove(getString(R.string.pre_key_course));
                editor.remove(getString(R.string.pre_key_semester));
                editor.apply();

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

    private void clearAllNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    private void deleteTokenForAccount() {
        String maSinhVien = Reference.getStudentId(this);
        String token = getSharedPreferences(getString(R.string.share_pre_key_firebase), MODE_PRIVATE)
                .getString(getString(R.string.pre_key_token), null);
        FireBaseIDTask.deleteTokenFromAccount(maSinhVien, token);
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

    private void showSelectSemesterDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        int selectedSemesterIndex = sharedPreferences.getInt(getString(R.string.pre_key_semester), 0);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.action_selectSemester));
        builder.setSingleChoiceItems(semesters, selectedSemesterIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                saveSelectedSemester(index);
                ((TextView) findViewById(R.id.tv_hocKiNamHoc)).setText(semesters[index]);
                findViewById(R.id.tv_hocKiNamHoc).refreshDrawableState();
            }
        });
        builder.setPositiveButton("Tác nghiệp", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this,
                        "Tác nghiệp thành công !", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Thôi", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveSelectedSemester(int id) {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.pre_key_semester), id);
        editor.apply();
    }

    final CharSequence[] semesters = {
            "Học kì 2 (2018-2019)",
            "Học kì 1 (2018-2019)",
            "Học kì hè (2017-2018)",
            "Học kì 2 (2017-2018)",
            "Học kì 1 (2017-2018)",
            "Học kì hè (2016-2017)",
            "Học kì 2 (2016-2017)",
            "Học kì 1 (2016-2017)",
            "Học kì hè (2015-2016)",
            "Học kì 2 (2015-2016)",
            "Học kì 1 (2015-2016)"
    };
}
