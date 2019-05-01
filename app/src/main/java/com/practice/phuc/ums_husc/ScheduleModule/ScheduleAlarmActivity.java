package com.practice.phuc.ums_husc.ScheduleModule;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.practice.phuc.ums_husc.R;

public class ScheduleAlarmActivity extends Activity {
    private String TAG = this.getClass().getSimpleName();
    private int WAKELOCK_TIMEOUT = 5 * 60 * 1000;

    private PowerManager.WakeLock mWakeLock;
    private MediaPlayer mPlayer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_alarm);

        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {0, 800, 200, 1000, 300, 1000, 200, 4000};

        if (v.hasVibrator()) {
            Log.v("Can Vibrate", "YES");
        } else {
            Log.v("Can Vibrate", "NO");
        }

        Button btnTurnOff = findViewById(R.id.btn_turn_off_alarm);
        btnTurnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.stop();
                v.cancelLongPress();
                finish();
            }
        });

        //Play alarm tone
        try {
            Uri toneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (toneUri != null) {
                mPlayer = MediaPlayer.create(getApplicationContext(), toneUri);
                mPlayer.setDataSource(this, toneUri);
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mPlayer.setLooping(true);
                mPlayer.prepare();
                mPlayer.start();
                v.vibrate(WAKELOCK_TIMEOUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
