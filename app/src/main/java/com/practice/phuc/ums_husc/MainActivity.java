package com.practice.phuc.ums_husc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.practice.phuc.ums_husc.Helper.FireBaseIDTask;
import com.practice.phuc.ums_husc.Helper.MyFireBaseMessagingService;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.MessageModule.DetailMessageActivity;
import com.practice.phuc.ums_husc.ResumeModule.ResumeFragment;
import com.practice.phuc.ums_husc.MessageModule.MessageFragment;
import com.practice.phuc.ums_husc.NewsModule.DetailNewsActivity;
import com.practice.phuc.ums_husc.NewsModule.MainFragment;
import com.practice.phuc.ums_husc.ScheduleModule.ScheduleFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private String currentFragment;
    private FragmentManager fragmentManager;
    private int currentNavItem;
    private boolean mIsLogined;
    private boolean mIsLaunchFromNewsNoti;
    private boolean mIsLaunchFromMessageNoti;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBundle = getIntent().getExtras();
        mIsLogined = localLogin();
        if (mIsLogined) {
            initAll();
            initFragmentManager();
            showAccountInfo();

            if (mBundle != null) {
                mIsLaunchFromNewsNoti = mBundle.getBoolean(Reference.BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, false);
                mIsLaunchFromMessageNoti = mBundle.getBoolean(Reference.BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI, false);

                if (mIsLaunchFromMessageNoti) {
                    initFragment(MessageFragment.newInstance(this));

                } else {
                    initFragment(MainFragment.newInstance(this));
                }

            } else {
                initFragment(MainFragment.newInstance(this));
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        if (!mIsLogined) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            this.finish();

        } else {
            // xem tin tu thong bao o thanh trang thai
            if (mBundle != null && mIsLaunchFromNewsNoti) {
                String title = mBundle.getString(Reference.BUNDLE_KEY_NEWS_TITLE, "");
                String postTime = mBundle.getString(Reference.BUNDLE_KEY_NEWS_POST_TIME, "");
                String id = mBundle.getString(Reference.BUNDLE_KEY_NEWS_ID, "");

                Bundle b = new Bundle();
                b.putString(Reference.BUNDLE_KEY_NEWS_TITLE, title);
                b.putString(Reference.BUNDLE_KEY_NEWS_POST_TIME, postTime);
                b.putString(Reference.BUNDLE_KEY_NEWS_ID, id);
                b.putBoolean(Reference.BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, true);
                Intent intent = new Intent(this, DetailNewsActivity.class);
                intent.putExtra(Reference.BUNDLE_EXTRA_NEWS, b);
                startActivity(intent);

            } else if (mBundle != null && mIsLaunchFromMessageNoti) {
                String title = mBundle.getString(Reference.BUNDLE_KEY_MESSAGE_TITLE, "");
                String sendTime = mBundle.getString(Reference.BUNDLE_KEY_MESSAGE_SEND_TIME, "");
                String sender = mBundle.getString(Reference.BUNDLE_KEY_MESSAGE_SENDER, "");
                String id = mBundle.getString(Reference.BUNDLE_KEY_MESSAGE_ID, "");

                Bundle b = new Bundle();
                b.putString(Reference.BUNDLE_KEY_MESSAGE_ID, id);
                b.putString(Reference.BUNDLE_KEY_MESSAGE_TITLE, title);
                b.putString(Reference.BUNDLE_KEY_MESSAGE_SENDER, sender);
                b.putString(Reference.BUNDLE_KEY_MESSAGE_SEND_TIME, sendTime);
                b.putBoolean(Reference.BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI, true);
                Intent intent = new Intent(this, DetailMessageActivity.class);
                intent.putExtra(Reference.BUNDLE_EXTRA_MESSAGE, b);
                startActivity(intent);
            }
        }
        super.onPostCreate(savedInstanceState);
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
        } else {
            replaceFragment(MainFragment.newInstance(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                            currentNavItem = R.id.nav_news;
                            setTitle(getString(R.string.title_nav_news));
                            replaceFragment(MainFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_timetable:
                            currentNavItem = R.id.nav_timetable;
                            setTitle(getString(R.string.title_nav_timetable));
                            replaceFragment(ScheduleFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_message:
                            currentNavItem = R.id.nav_message;
                            setTitle(getString(R.string.title_nav_message));
                            replaceFragment(MessageFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_setting:
                            currentNavItem = R.id.nav_setting;
                            setTitle(getString(R.string.title_nav_setting));
                            replaceFragment(SettingFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_resume:
                            setTitle(getString(R.string.title_nav_resume));
                            replaceFragment(ResumeFragment.newInstance(MainActivity.this));
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
        currentFragment = fragmentTag;
        if (fragmentTag.equals(MainFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_news));
            navigationView.setCheckedItem(R.id.nav_news);
            currentNavItem = R.id.nav_news;
            showDrawerButton(true);

        } else if (fragmentTag.equals(ResumeFragment.class.getName())) {
            showDrawerButton(false);
            currentNavItem = R.id.nav_resume;
        } else if (fragmentTag.equals(SettingFragment.class.getName())) {
            showDrawerButton(false);
            currentNavItem = R.id.nav_setting;
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
        currentFragment = MainFragment.class.getName();
        navigationView.setCheckedItem(R.id.nav_news);
        replaceFragment(fragment);
    }

    private void showAccountInfo() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvMaSinhVien = headerView.findViewById(R.id.tv_maSinhVien);
        TextView tvHoTen = headerView.findViewById(R.id.tv_hoTen);
        TextView tvKhoaHoc = headerView.findViewById(R.id.tv_khoaHoc);
        TextView tvNganhHoc = headerView.findViewById(R.id.tv_nganhHoc);

        SharedPreferences sharedPreferences = getSharedPreferences("sinhVien", MODE_PRIVATE);
        String maSinhVien = sharedPreferences.getString("maSinhVien", "");
        String hoTen = sharedPreferences.getString("hoTen", "");
        String khoaHoc = sharedPreferences.getString("khoaHoc", "");
        String nganhHoc = sharedPreferences.getString("nganhHoc", "");

        tvMaSinhVien.setText(maSinhVien);
        tvHoTen.setText(hoTen);
        tvKhoaHoc.setText(khoaHoc);
        tvNganhHoc.setText(nganhHoc);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        String message = "Xin chào " + hoTen;
        Snackbar snackbar = Snackbar.make(drawerLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void deleteTokenForAccount() {
        String maSinhVien = getSharedPreferences("sinhVien", MODE_PRIVATE)
                .getString("maSinhVien", null);
        String token = getSharedPreferences("FIREBASE", MODE_PRIVATE)
                .getString("TOKEN", null);
        FireBaseIDTask.deleteTokenFromAccount(maSinhVien, token);
    }

    private boolean localLogin() {
        // Kiem tra da dang nhap truoc do chua
        SharedPreferences sp = getSharedPreferences("sinhVien", MODE_PRIVATE);
        String maSinhVien = sp.getString("maSinhVien", null);
        String matKhau = sp.getString("matKhau", null);

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

                SharedPreferences.Editor editor = getSharedPreferences("sinhVien", MODE_PRIVATE).edit();
                editor.remove("maSinhVien");
                editor.remove("matKhau");
                editor.remove("hoTen");
                editor.remove("nganhHoc");
                editor.remove("khoaHoc");
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

    private void showDrawerButton(boolean show)  {
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
}
