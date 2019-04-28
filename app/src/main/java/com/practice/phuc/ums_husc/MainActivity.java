package com.practice.phuc.ums_husc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.FireBaseIDTask;
import com.practice.phuc.ums_husc.Helper.MessageTaskHelper;
import com.practice.phuc.ums_husc.Helper.MyFireBaseMessagingService;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.StringHelper;
import com.practice.phuc.ums_husc.MessageModule.DetailMessageActivity;
import com.practice.phuc.ums_husc.MessageModule.MessageFragment;
import com.practice.phuc.ums_husc.NewsModule.DetailNewsActivity;
import com.practice.phuc.ums_husc.NewsModule.MainFragment;
import com.practice.phuc.ums_husc.ResumeModule.ResumeFragment;
import com.practice.phuc.ums_husc.ScheduleModule.ScheduleFragment;
import com.practice.phuc.ums_husc.ViewModel.VHocKy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private FragmentManager fragmentManager;
    private int currentNavItem;
    private String currentFragment;
    private String mPrevFragment;
    private List<VHocKy> semesters;
    private ArrayAdapter<VHocKy> semesterAdapter;

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
            initSemsterDialog();
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
        boolean isChangePassFrag = currentFragment.equals(ChangePasswordFragment.class.getName());
        boolean isSettingFrag = currentFragment.equals(SettingFragment.class.getName());

        menu.findItem(R.id.action_goToToday).setVisible(isScheduleFrag);
        menu.findItem(R.id.action_refreshResume).setVisible(isResumeFrag);
        menu.findItem(R.id.action_newMessage).setVisible(isMessageFrag);
        menu.findItem(R.id.action_searchMessage).setVisible(isMessageFrag);
        menu.findItem(R.id.action_selectSemester).setVisible(!(isMessageFrag || isResumeFrag || isChangePassFrag || isSettingFrag));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_selectSemester:
                showSelectSemesterDialog();
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
        String hocKi = sharedPreferences.getString("semester_string", "");

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

                deleteTokenForAccount();

                NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
                nm.cancelAll();

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

    private int selectedSemesterIndex;
    private int attempSelectedSemesterIndex;

    private void initSemsterDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        String semesterId = sharedPreferences.getString(getString(R.string.pre_key_semester), null);
        int index = Integer.parseInt(semesterId != null ? semesterId : "0");

        semesters = new ArrayList<>();
        semesters.add(new VHocKy("12", "2", "5", "2018", "2019"));
        semesters.add(new VHocKy("11", "1", "5", "2018", "2019"));
        semesters.add(new VHocKy("10", "3", "4", "2017", "2018"));
        semesters.add(new VHocKy("9", "2", "4", "2017", "2018"));
        semesters.add(new VHocKy("8", "1", "4", "2017", "2018"));
        semesters.add(new VHocKy("7", "3", "3", "2016", "2017"));
        semesters.add(new VHocKy("6", "2", "3", "2016", "2017"));
        semesters.add(new VHocKy("5", "1", "3", "2016", "2017"));
        semesters.add(new VHocKy("3", "3", "1", "2015", "2016"));
        semesters.add(new VHocKy("2", "2", "1", "2015", "2016"));
        semesters.add(new VHocKy("1", "1", "1", "2015", "2016"));
        semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, semesters);

        for (int i = 0; i < semesters.size(); i++) {
            if (semesters.get(i).MaHocKy.equals(semesterId)) {
                index = i;
                break;
            }
        }
        selectedSemesterIndex = index;
    }

    private void showSelectSemesterDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.action_selectSemester));
        builder.setSingleChoiceItems(semesterAdapter, selectedSemesterIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                attempSelectedSemesterIndex = index;
                String hocKy = Objects.requireNonNull(semesterAdapter.getItem(index)).toString();
                ((TextView) findViewById(R.id.tv_hocKiNamHoc)).setText(hocKy);
                findViewById(R.id.tv_hocKiNamHoc).refreshDrawableState();
            }
        });
        builder.setPositiveButton("Tác nghiệp", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (NetworkUtil.getConnectivityStatus(MainActivity.this) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    Toasty.custom(MainActivity.this, getString(R.string.error_network_disconected),
                            getResources().getDrawable(R.drawable.ic_signal_wifi_off_white_24),
                            getResources().getColor(R.color.colorRed),
                            getResources().getColor(android.R.color.white),
                            Toasty.LENGTH_SHORT, true, true)
                            .show();
                    return;
                }

                String maHocKyStr = Objects.requireNonNull(semesterAdapter.getItem(selectedSemesterIndex)).MaHocKy;
                new TacNghiepTask(maHocKyStr).execute();
            }
        });
        builder.setNegativeButton("Thôi", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveSelectedSemester(String id) {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE).edit();
        editor.putString(getString(R.string.pre_key_semester), id);
        editor.apply();
        selectedSemesterIndex = attempSelectedSemesterIndex;
    }

    @SuppressLint("StaticFieldLeak")
    private class TacNghiepTask extends AsyncTask<String, Void, Boolean> {
        String responseMessage;
        String maHocKy;

        TacNghiepTask(String maHocKy) { this.maHocKy = maHocKy; }

        @Override
        protected Boolean doInBackground(String... strings) {
            String url = Reference.HOST + "api/sinhvien/hocky/tacnghiep" +
                    "?masinhvien=" + Reference.getStudentId(MainActivity.this) +
                    "&matkhau=" + Reference.getAccountPassword(MainActivity.this) +
                    "&mahocky=" + maHocKy;
            Response response = NetworkUtil.makeRequest(url, false, null);

            if (response == null) {
                responseMessage = "Không tìm thấy máy chủ";
                return false;
            }

            if (response.code() == NetworkUtil.OK) {
                responseMessage = "Tác nghiệp thành công";
                return true;
            }

            responseMessage = "Có lỗi xảy ra, thử lại sau";
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                Toasty.success(MainActivity.this, responseMessage, Toasty.LENGTH_SHORT).show();
                saveSelectedSemester(maHocKy);

            } else {
                Toasty.error(MainActivity.this, responseMessage, Toasty.LENGTH_SHORT).show();
            }
        }
    }
}
