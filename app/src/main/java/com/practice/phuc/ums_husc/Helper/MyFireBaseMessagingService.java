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
import com.practice.phuc.ums_husc.MainActivity;
import com.practice.phuc.ums_husc.MessageModule.DetailMessageActivity;
import com.practice.phuc.ums_husc.NewsModule.DetailNewsActivity;
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

            if (messageType != null && messageType.equals(Reference.NEWS_NOTIFICATION)) {
                boolean isAllow = mSharedPreferences.getBoolean(getString(R.string.share_pre_key_news), true);
                if (isAllow) {
                    riseNotification(createNewsNotification(remoteMessage));
                }

            } else if (messageType != null && messageType.equals(Reference.MESSAGE_NOTIFICATION)) {
                boolean isAllow = mSharedPreferences.getBoolean(getString(R.string.share_pre_key_message), true);
                if (isAllow) {
                    riseNotification(createMessageNotification(remoteMessage));
                }
            }
        }
        super.onMessageReceived(remoteMessage);
    }

    private Notification createNewsNotification(RemoteMessage remoteMessage) {
        String newsId = remoteMessage.getData().get("id");
        String newsPostTime = DateHelper.formatDateTimeString(remoteMessage.getData().get("postTime"));

        String notiTitle = remoteMessage.getData().get("title");
        String notiBody = remoteMessage.getData().get("body");

        Intent intent;
        PendingIntent pendingIntent;
        Context context;
        Bundle bundle = new Bundle();
        if (mContext == null) { // Luc app ko chay
            context = this;
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(Reference.BUNDLE_KEY_NEWS_ID, newsId);
            intent.putExtra(Reference.BUNDLE_KEY_NEWS_TITLE, notiBody);
            intent.putExtra(Reference.BUNDLE_KEY_NEWS_POST_TIME, newsPostTime);
            intent.putExtra(Reference.BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, true);
        } else {
            intent = new Intent(mContext, DetailNewsActivity.class);
            bundle.putString(Reference.BUNDLE_KEY_NEWS_ID, newsId);
            bundle.putString(Reference.BUNDLE_KEY_NEWS_TITLE, notiBody);
            bundle.putString(Reference.BUNDLE_KEY_NEWS_POST_TIME, newsPostTime);
            bundle.putBoolean(Reference.BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, true);
            intent.putExtra(Reference.BUNDLE_EXTRA_NEWS, bundle);
            context = mContext;
        }

        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Khoi tao thong bao
        NotificationCompat.Builder mBuilder = CreateNotificationCompatBuilder();
        mBuilder.setContentTitle(notiTitle);
        mBuilder.setContentText("Lúc " + newsPostTime);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notiBody));
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setGroup(Reference.NEWS_NOTIFICATION);
        return mBuilder.build();
    }

    private Notification createMessageNotification(RemoteMessage remoteMessage) {
        String messageId = remoteMessage.getData().get("id");
        String messageTitle = remoteMessage.getData().get("body");
        String messageSender = remoteMessage.getData().get("title");
        String messageSendTime = DateHelper.formatDateTimeString(remoteMessage.getData().get("postTime"));

        Intent intent;
        PendingIntent pendingIntent;
        Context context;
        Bundle bundle = new Bundle();
        if (mContext == null) { // Luc app ko chay
            context = this;
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(Reference.BUNDLE_KEY_MESSAGE_ID, messageId);
            intent.putExtra(Reference.BUNDLE_KEY_MESSAGE_TITLE, messageTitle);
            intent.putExtra(Reference.BUNDLE_KEY_MESSAGE_SENDER, messageSender);
            intent.putExtra(Reference.BUNDLE_KEY_MESSAGE_SEND_TIME, messageSendTime);
            intent.putExtra(Reference.BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI, true);
        } else {
            context = mContext;
            intent = new Intent(mContext, DetailMessageActivity.class);
            bundle.putString(Reference.BUNDLE_KEY_MESSAGE_ID, messageId);
            bundle.putString(Reference.BUNDLE_KEY_MESSAGE_TITLE, messageTitle);
            bundle.putString(Reference.BUNDLE_KEY_MESSAGE_SENDER, messageSender);
            bundle.putString(Reference.BUNDLE_KEY_MESSAGE_SEND_TIME, messageSendTime);
            bundle.putBoolean(Reference.BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI, true);
            intent.putExtra(Reference.BUNDLE_EXTRA_MESSAGE, bundle);
        }

        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Khoi tao thong bao
        NotificationCompat.Builder mBuilder = CreateNotificationCompatBuilder();
        mBuilder.setContentTitle(messageSender + " đã gửi 1 tin nhắn"); // Tieu de tb la ten nguoi gui
        mBuilder.setContentText("Lúc " + messageSendTime); // Noi dung la thoi gian gui
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageTitle)); // Noi dung chinh la tieu de tin nhan
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setGroup(Reference.MESSAGE_NOTIFICATION);
        return mBuilder.build();
    }

    private NotificationCompat.Builder CreateNotificationCompatBuilder() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.chanel_id));
        mBuilder.setSmallIcon(R.mipmap.logo);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.logo));
        mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setAutoCancel(true);
        mBuilder.setGroupSummary(true);
        return mBuilder;
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
