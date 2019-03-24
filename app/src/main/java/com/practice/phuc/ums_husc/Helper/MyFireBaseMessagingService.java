package com.practice.phuc.ums_husc.Helper;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.practice.phuc.ums_husc.DetailNewsActivity;
import com.practice.phuc.ums_husc.R;

import java.util.Date;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    public static Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("DEBUG", "Nhận thông báo - xử lý ở đây !!!");
        //Log.d("DEBUG", remoteMessage.getData().toString() + remoteMessage.getData().size());

        // Xu ly payload data
        if (remoteMessage.getData().size() > 0) {
            String messageType = remoteMessage.getData().get("type");

            if (messageType.equals(Reference.NEWS_NOTIFICATION)) {
                // Thong bao co tin tuc moi
                riseNotification(createNewsNotification(remoteMessage));

            } else if (messageType.equals(Reference.MESSAGE_NOTIFICATION)) {
                // Thong bao co tin nhan moi

            }
        }
    }

    private Notification createNewsNotification(RemoteMessage remoteMessage) {
        String newsTitle = remoteMessage.getData().get("title");
        String newsContent = remoteMessage.getData().get("body");
        String newsPostTime = remoteMessage.getData().get("postTime");

        Bundle bundle = new Bundle();
        bundle.putString("title", newsTitle);
        bundle.putString("body", newsContent);
        bundle.putString("postTime", newsPostTime);

        Intent detailNewsIntent;
        PendingIntent pendingIntent;

        if (context == null) {
            detailNewsIntent = new Intent(this, DetailNewsActivity.class);
            bundle.putBoolean("fromNotification", true);
            detailNewsIntent.putExtra("news", bundle);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(detailNewsIntent);
            pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            detailNewsIntent = new Intent(context, DetailNewsActivity.class);
            bundle.putBoolean("fromNotification", false);
            detailNewsIntent.putExtra("news", bundle);
            pendingIntent = PendingIntent.getActivity(context,
                    0, detailNewsIntent, PendingIntent.FLAG_UPDATE_CURRENT, bundle);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.chanel_id));
        mBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
        mBuilder.setContentText(remoteMessage.getNotification().getBody());
        mBuilder.setSmallIcon(R.mipmap.logo);
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
