package com.practice.phuc.ums_husc.Helper;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.practice.phuc.ums_husc.MainActivity;
import com.practice.phuc.ums_husc.R;

import java.util.Date;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    @SuppressLint("StaticFieldLeak")
    public static Context mContex = null;
//    private String mNotificationId;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("DEBUG", "Nhận thông báo - xử lý ở đây !!!");

        // Xu ly payload data
        if (remoteMessage.getData().size() > 0) {
            String messageType = remoteMessage.getData().get("type");

            if (messageType.equals(Reference.NEWS_NOTIFICATION)) {
                Log.d("DEBUG", "Thong bao co tin tuc moi !!!");
                // Thong bao co tin tuc moi
                riseNotification(createNewsNotification(remoteMessage));

            } else if (messageType.equals(Reference.MESSAGE_NOTIFICATION)) {
                Log.d("DEBUG", "Thong bao co tin nhan moi !!!");
                // Thong bao co tin nhan moi

            }
        }
    }

    private Notification createNewsNotification(RemoteMessage remoteMessage) {
        String newsId = remoteMessage.getData().get("id");
        String newsPostTime = DateHelper.formatDateTimeString(remoteMessage.getData().get("postTime"));
        // tieu de thong bao
        String contentTitle = remoteMessage.getNotification() != null ?
                remoteMessage.getNotification().getTitle() : "Tiêu đề";
        // noi dung thong bao
        String contentText = remoteMessage.getNotification() != null ?
                remoteMessage.getNotification().getBody() : "Nội dung";

        Bundle bundle = new Bundle();
        bundle.putString("title", contentText); // tieu de cua ban tin
        bundle.putString("id", newsId + ""); // ma ban tin
        bundle.putString("postTime", newsPostTime); // thoi diem dang

        PendingIntent pendingIntent;

//        if (mContex == null) {
            Intent mainIntent = new Intent(this, MainActivity.class);
            bundle.putBoolean("fromNotification", true);
            mainIntent.putExtra("news", bundle);
            pendingIntent = PendingIntent.getActivity(this,
                    0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT, bundle);
//        } else {
//            Intent detailNewsIntent = new Intent(this, DetailNewsActivity.class);
//            bundle.putBoolean("fromNotification", true);
//            detailNewsIntent.putExtra("news", bundle);
//            pendingIntent = PendingIntent.getActivity(this,
//                    0, detailNewsIntent, PendingIntent.FLAG_UPDATE_CURRENT, bundle);
//        }
        // Khoi tao thong bao day
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.chanel_id));
        mBuilder.setContentTitle(contentTitle);
        mBuilder.setContentText("Lúc " + newsPostTime);
        mBuilder.setSmallIcon(R.mipmap.logo);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.logo));
        mBuilder.setColor(getResources().getColor(R.color.colorAccent));
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(contentText));
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
