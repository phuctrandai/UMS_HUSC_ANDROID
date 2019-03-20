package com.practice.phuc.ums_husc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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

import com.practice.phuc.ums_husc.LyLichCaNhan.ResumeActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private String currentFragment;
    private int currentNavItem;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.title_nav_news));

        // set up navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initFragmentManager();

        if (savedInstanceState == null) {
            // set selected navigation item
            navigationView.setCheckedItem(R.id.nav_news);

            // set first fragment
            initFragment(MainFragment.newInstance(this));

            // hien thi thong tin tai khoan
            hienThiThongTinCaNhan();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("UMS_HUSC", "Back pressed !!!");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentFragment.equals(MainFragment.class.getName())) {
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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_thongBao) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        Log.d("DEBUG", id + " - current nav item id");
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
            }, 300);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Khoi tao fragment manager
    private void initFragmentManager() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.d("UMS_HUSC", "Back stack changed !!");
                Fragment fragment = fragmentManager.findFragmentById(R.id.frame_layout);
                if (fragment != null) {
                    Log.d("UMS_HUSC", "TAG: " + fragment.getTag());
                    updateByFragmentTag(fragment.getTag());
                }
            }
        });
    }

    // Khoi tao fragment dau tien
    private void initFragment(Fragment fragment) {
        currentNavItem = R.id.nav_news;
        currentFragment = fragment.getClass().getName();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.frame_layout, fragment, currentFragment).commit();
    }

    // Add fragment to back stack
    protected void replaceFragment(Fragment fragment) {
        String fragmentTag = fragment.getClass().getName();
        currentFragment = fragmentTag;
        Log.d("DEBUG", "Current fragment: " + currentFragment);

        if (fragment.getClass().getName().equals(MainFragment.class.getName())) {
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                fragmentManager.popBackStack();
            }
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.replace(R.id.frame_layout, fragment, fragmentTag);
        ft.addToBackStack(fragmentTag);
        ft.commit();
        Log.d("DEBUG", "Back stack entry count: " + fragmentManager.getBackStackEntryCount());
    }

    // Update UI when press back
    private void updateByFragmentTag(String fragmentTag) {
        if (fragmentTag.equals(MainFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_news));
            navigationView.setCheckedItem(R.id.nav_news);
            currentFragment = fragmentTag;
            currentNavItem = R.id.nav_news;

            showDrawerButton(true);
            return;

        } else if (fragmentTag.equals(MessageFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_message));
            navigationView.setCheckedItem(R.id.nav_message);
            currentFragment = fragmentTag;
            currentNavItem = R.id.nav_message;

        } else if (fragmentTag.equals(ScheduleFragment.class.getName())) {
            setTitle(getString(R.string.title_nav_timetable));
            navigationView.setCheckedItem(R.id.nav_timetable);
            currentFragment = fragmentTag;
            currentNavItem = R.id.nav_timetable;
        }
        showDrawerButton(false);
    }

    // Switch drawer nav button / back button
    private void showDrawerButton(boolean show) {
        if (show) {
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            toggle.syncState();
        } else {
            toggle.setDrawerIndicatorEnabled(false);
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replaceFragment(MainFragment.newInstance(MainActivity.this));
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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
                SharedPreferences.Editor editor = getSharedPreferences("sinhVien", MODE_PRIVATE).edit();
                editor.remove("maSinhVien");
                editor.remove("matKhau");
                editor.remove("hoTen");
                editor.remove("nganhHoc");
                editor.remove("khoaHoc");
//                editor.commit();
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

    // Hien thi thong tin ca nhan
    private void hienThiThongTinCaNhan() {
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
    }

}
