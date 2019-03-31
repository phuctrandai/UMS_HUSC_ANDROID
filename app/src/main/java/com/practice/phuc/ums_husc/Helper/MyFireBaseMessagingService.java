package com.practice.phuc.ums_husc.Helper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.practice.phuc.ums_husc.DetailNewsActivity;
import com.practice.phuc.ums_husc.MainActivity;
import com.practice.phuc.ums_husc.R;

import java.util.Date;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    @SuppressLint("StaticFieldLeak")
    public static Context mContext;
    private SharedPreferences mSharedPreferences;
//    private String mNotificationId;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("DEBUG", "Nhận thông báo - xử lý ở đây !!!");
        mSharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);

        if (remoteMessage.getData().size() > 0) {
            String messageType = remoteMessage.getData().get("type");

            if (messageType.equals(Reference.NEWS_NOTIFICATION)) {
//                Log.d("DEBUG", "Thong bao co tin tuc moi !!!");
                boolean isAllow = mSharedPreferences.getBoolean(getString(R.string.share_pre_key_news), true);
                if (isAllow) {
                    riseNotification(createNewsNotification(remoteMessage));
                }

            } else if (messageType.equals(Reference.MESSAGE_NOTIFICATION)) {
//                Log.d("DEBUG", "Thong bao co tin nhan moi !!!");

            }
        }
        super.onMessageReceived(remoteMessage);
    }

    private Notification createNewsNotification(RemoteMessage remoteMessage) {
        String newsId = remoteMessage.getData().get("id");
        String newsPostTime = DateHelper.formatDateTimeString(remoteMessage.getData().get("postTime"));
        String newsTitle = remoteMessage.getData().get("title");
        String newsBody = remoteMessage.getData().get("body");

        Intent intent;
        PendingIntent pendingIntent;
        Context context;
        Bundle bundle = new Bundle();
        if (mContext == null) { // Luc app ko chay
            context = this;
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(Reference.BUNDLE_KEY_NEWS_TITLE, newsBody);
            intent.putExtra(Reference.BUNDLE_KEY_NEWS_ID, newsId);
            intent.putExtra(Reference.BUNDLE_KEY_NEWS_POST_TIME, newsPostTime);
            intent.putExtra(Reference.BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, true);
        } else {
            intent = new Intent(mContext, DetailNewsActivity.class);
            bundle.putString(Reference.BUNDLE_KEY_NEWS_TITLE, newsBody);
            bundle.putString(Reference.BUNDLE_KEY_NEWS_ID, newsId);
            bundle.putString(Reference.BUNDLE_KEY_NEWS_POST_TIME, newsPostTime);
            bundle.putBoolean(Reference.BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, true);
            intent.putExtra(Reference.BUNDLE_EXTRA_NEWS, bundle);
            context = mContext;
        }

        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Khoi tao thong bao
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.chanel_id));
        mBuilder.setContentTitle(newsTitle);
        mBuilder.setContentText("Lúc " + newsPostTime);
        mBuilder.setSmallIcon(R.mipmap.logo);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.logo));
        mBuilder.setColor(getResources().getColor(R.color.colorAccent));
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(newsBody));
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setGroup(Reference.NEWS_NOTIFICATION);
        mBuilder.setGroupSummary(true);
        return mBuilder.build();
    }

    private void riseNotification(Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int norificationId = Integer.parseInt(String.valueOf(new Date().getTime() / 1000));
        notificationManager.notify(norificationId, notification);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("DEBUG", "Gọi khi nhận token mới: " + s);

        getSharedPreferences("FIREBASE", MODE_PRIVATE).edit().putString("TOKEN", s).apply();
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("FIREBASE", MODE_PRIVATE)
                .getString("TOKEN", "empty");
    }
}
