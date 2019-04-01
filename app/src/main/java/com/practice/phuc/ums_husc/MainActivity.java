package com.practice.phuc.ums_husc;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import com.practice.phuc.ums_husc.LyLichCaNhanModule.ResumeActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // UI
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    // param
    private static String currentFragment;
    private int currentNavItem;
    private static FragmentManager fragmentManager;
    private boolean mIsLogined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.d("DEBUG", "ON create Main activity");

        mIsLogined = localLogin();

        if (mIsLogined) {
            initAll();

            initFragmentManager();
            // set first fragment
            initFragment(MainFragment.newInstance(this));
            // hien thi thong tin tai khoan
            showAccountInfo();
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
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.getBoolean(Reference.BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI)) {
                String title = bundle.getString(Reference.BUNDLE_KEY_NEWS_TITLE);
                String postTime = bundle.getString(Reference.BUNDLE_KEY_NEWS_POST_TIME);
                String id = bundle.getString(Reference.BUNDLE_KEY_NEWS_ID);

                Bundle b = new Bundle();
                b.putString(Reference.BUNDLE_KEY_NEWS_TITLE, title);
                b.putString(Reference.BUNDLE_KEY_NEWS_POST_TIME, postTime);
                b.putString(Reference.BUNDLE_KEY_NEWS_ID, id);
                b.putBoolean(Reference.BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, true);
                Intent intent = new Intent(this, DetailNewsActivity.class);
                intent.putExtra(Reference.BUNDLE_EXTRA_NEWS, b);
                startActivity(intent);
            }
        }
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
//        Log.d("DEBUG", "ON RESUME Main activity");
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
//        Log.d("UMS_HUSC", "Back pressed !!!");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_thongBao) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
//        Log.d("DEBUG", id + " - current nav item id");
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
                            replaceFragment(com.practice.phuc.ums_husc.MessageModule.MessageFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_setting:
                            currentNavItem = R.id.nav_setting;
                            setTitle(getString(R.string.title_nav_setting));
                            replaceFragment(SettingFragment.newInstance(MainActivity.this));
                            break;
                        case R.id.nav_resume:
                            startActivity(new Intent(MainActivity.this, ResumeActivity.class));
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

    // Add fragment to back stack
    protected void replaceFragment(Fragment fragment) {
        String fragmentTag = fragment.getClass().getName();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

//        Log.d("DEBUG", "Current fragment: " + currentFragment);
        updateByFragmentTag(fragmentTag);

        Fragment temp = fragmentManager.findFragmentByTag(fragmentTag);
        if (temp != null) {
            ft.replace(R.id.frame_layout, temp, fragmentTag);
        } else {
            ft.replace(R.id.frame_layout, fragment, fragmentTag);
            ft.addToBackStack(fragmentTag);
        }
        ft.commit();
//        Log.d("DEBUG", "Back stack entry count: " + fragmentManager.getBackStackEntryCount());
    }

    // Update UI when press back
    private void updateByFragmentTag(String fragmentTag) {
        currentFragment = fragmentTag;
        if (fragmentTag.equals(MainFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_news));
            navigationView.setCheckedItem(R.id.nav_news);
            currentNavItem = R.id.nav_news;

//            showDrawerButton(true);
            return;

        } else if (fragmentTag.equals(MessageFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_message));
            navigationView.setCheckedItem(R.id.nav_message);
            currentNavItem = R.id.nav_message;

        } else if (fragmentTag.equals(ScheduleFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_timetable));
            navigationView.setCheckedItem(R.id.nav_timetable);
            currentNavItem = R.id.nav_timetable;
        }
//        showDrawerButton(false);
    }

    // Khoi tao
    private void initAll() {
        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.title_nav_news));

        // set up navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Khoi tao fragment manager
    private void initFragmentManager() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
//                Log.d("UMS_HUSC", "Back stack changed !!");
                Fragment fragment = fragmentManager.findFragmentById(R.id.frame_layout);
                if (fragment != null) {
//                    Log.d("UMS_HUSC", "TAG: " + fragment.getTag());
                    updateByFragmentTag(fragment.getTag());
                }
            }
        });
    }

    // Khoi tao fragment dau tien
    private void initFragment(Fragment fragment) {
        currentNavItem = R.id.nav_news;
        currentFragment = MainFragment.class.getName();
        navigationView.setCheckedItem(R.id.nav_news);
        replaceFragment(fragment);
    }

    // Hien thi thong tin ca nhan
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

    // Xoa token cua app khoi database
    private void deleteTokenForAccount() {
        String maSinhVien = getSharedPreferences("sinhVien", MODE_PRIVATE)
                .getString("maSinhVien", null);
        String token = getSharedPreferences("FIREBASE", MODE_PRIVATE)
                .getString("TOKEN", null);
        FireBaseIDTask.deleteTokenFromAccount(maSinhVien, token);
    }

    // Dang nhap voi thong tin da luu tren may
    private boolean localLogin() {
        // Kiem tra da dang nhap truoc do chua
        SharedPreferences sp = getSharedPreferences("sinhVien", MODE_PRIVATE);
        String maSinhVien = sp.getString("maSinhVien", null);
        String matKhau = sp.getString("matKhau", null);

        if ((maSinhVien != null) && (matKhau != null))
            return true;

        return false;
    }

    // Dang xuat tai khoan hien tai
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
}
