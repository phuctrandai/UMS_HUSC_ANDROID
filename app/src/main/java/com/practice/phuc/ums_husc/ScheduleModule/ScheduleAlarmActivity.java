package com.practice.phuc.ums_husc.ScheduleModule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

public class ScheduleAlarmActivity extends Activity {
    private int WAKELOCK_TIMEOUT = 5 * 60 * 1000;
    private PowerManager.WakeLock mWakeLock;
    private Ringtone mRingtone;
    private Vibrator mVibrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_alarm);

        setUpData();

        setUpVibrateRingtone();

        setUpDismissButton();
    }

    @SuppressLint("SetTextI18n")
    private void setUpData() {
        String data = getIntent().getStringExtra("data");
        ThoiKhoaBieu thoiKhoaBieu = ThoiKhoaBieu.fromJson(data);

        TextView tvSubject = findViewById(R.id.tv_subject);
        TextView tvStartTime = findViewById(R.id.tv_startTime);
        TextView tvRoom = findViewById(R.id.tv_room);
        TextView tvTeacher = findViewById(R.id.tv_teacher);

        tvSubject.setText(thoiKhoaBieu.TenLopHocPhan);
        tvStartTime.setText("Vào học lúc: " + thoiKhoaBieu.getLessionStartHour() + ":" + thoiKhoaBieu.getLessionStartMinute());
        tvRoom.setText("Phòng: " + thoiKhoaBieu.TenPhong);
        tvTeacher.setText("Giảng viên: " + thoiKhoaBieu.HoVaTen);
    }

    private void setUpVibrateRingtone() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};

        try {
            Uri toneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM
                    | RingtoneManager.TYPE_RINGTONE | RingtoneManager.TYPE_NOTIFICATION);

            if (toneUri != null) {
                mRingtone = RingtoneManager.getRingtone(getApplicationContext(), toneUri);
                mRingtone.play();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mVibrator.vibrate(VibrationEffect.createOneShot(WAKELOCK_TIMEOUT, VibrationEffect.DEFAULT_AMPLITUDE));

                } else {
                    //deprecated in API 26
                    mVibrator.vibrate(pattern, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpDismissButton() {
        //Ensure wakelock release
        Runnable releaseWakelock = new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
            }
        };
        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);

        // Dismiss button
        Button btnTurnOff = findViewById(R.id.btn_turn_off_alarm);
        btnTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRingtone.stop();
                mVibrator.cancel();
                finish();
                System.exit(0);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Acquire wakelock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            String TAG = "DEBUG:" + getClass().getSimpleName();
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK |
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire(WAKELOCK_TIMEOUT);
            Log.i("DEBUG", "Wakelock aquired!!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
