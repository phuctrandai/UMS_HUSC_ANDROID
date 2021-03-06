package com.practice.phuc.ums_husc.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.MainActivity;
import com.practice.phuc.ums_husc.MessageModule.DetailMessageActivity;
import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.NewsModule.DetailNewsActivity;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ScheduleModule.ScheduleTaskHelper;
import com.practice.phuc.ums_husc.SettingFragment;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.util.Objects;

import static com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper.ACCOUNT_SP;
import static com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper.STUDENT_SEMSTER;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    @SuppressLint("StaticFieldLeak")
    public static Context mContext;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("DEBUG", "Nhận thông báo - xử lý ở đây !!!");

        if (remoteMessage.getData().size() > 0) {
            String messageType = remoteMessage.getData().get("type");
            Log.d("DEBUG", "onMessageReceived: Message type: " + messageType);

            String accountId = SharedPreferenceHelper.getInstance()
                    .getSharedPrefStr(this,
                            ACCOUNT_SP,
                            SharedPreferenceHelper.ACCOUNT_ID, "");

            if (messageType != null && messageType.equals(Reference.getInstance().NEWS_NOTIFICATION)) {
                boolean isAllow = SharedPreferenceHelper.getInstance()
                        .getSharedPrefBool(this,
                                SettingFragment.SHARED_SETTING,
                                SettingFragment.SHARED_PRE_NEWS_NOTI, true);
                if (isAllow && (!accountId.equals(""))) {
                    Log.d("DEBUG", "onMessageReceived: Rise news noti");
                    riseNotification(createNewsNotification(remoteMessage));
                }

            } else if (messageType != null && messageType.equals(Reference.getInstance().MESSAGE_NOTIFICATION)) {
                boolean isAllow = SharedPreferenceHelper.getInstance()
                        .getSharedPrefBool(this,
                                SettingFragment.SHARED_SETTING,
                                SettingFragment.SHARED_PRE_MESSAGE_NOTI, true);
                if (isAllow && (!accountId.equals(""))) {
                    Log.d("DEBUG", "onMessageReceived: Rise message noti");
                    riseNotification(createMessageNotification(remoteMessage));
                }

            } else if (messageType != null && messageType.equals(Reference.getInstance().ADD_SCHEDULE_NOTIFICATION)) {
                boolean isAllow = SharedPreferenceHelper.getInstance()
                        .getSharedPrefBool(this,
                                SettingFragment.SHARED_SETTING,
                                SettingFragment.SHARED_PRE_TIMETABLE_ALARM, true);
                String body = remoteMessage.getData().get("body");

                if (isAllow && (!accountId.equals(""))) {
                    Log.d("DEBUG", "onMessageReceived: Rise add schedule noti");
                    ThoiKhoaBieu thoiKhoaBieu = ThoiKhoaBieu.fromJson(body);

                    if (thoiKhoaBieu != null) {
                        riseNotification(createAddScheduleNotification(remoteMessage, thoiKhoaBieu));
                    } else {
                        Log.d("DEBUG", "onMessageReceived: Schedule from json is Null");
                    }
                }

                if (!accountId.equals("")) {
                    String title = remoteMessage.getData().get("title");
                    ThoiKhoaBieu thoiKhoaBieu = ThoiKhoaBieu.fromJson(body);
                    String maHocKy =  SharedPreferenceHelper.getInstance()
                            .getSharedPrefStr(this, ACCOUNT_SP, STUDENT_SEMSTER, "12");

                    if (thoiKhoaBieu != null && title != null && !title.equals("Thông báo nghỉ học")) {
                        ScheduleTaskHelper.getInstance().fetchSchedule(this,
                                Reference.getInstance().getStudentId(this),
                                Reference.getInstance().getAccountPassword(this),
                                Integer.parseInt(maHocKy));

                    } else if (thoiKhoaBieu != null && title != null && title.equals("Thông báo nghỉ học")) {
                        ScheduleTaskHelper.getInstance().fetchSchedule(this,
                                Reference.getInstance().getStudentId(this),
                                Reference.getInstance().getAccountPassword(this),
                                Integer.parseInt(maHocKy));
                    }
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

        if (mContext == null) { // Luc app ko chay
            context = this;
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        } else {
            context = mContext;
            intent = new Intent(context, DetailNewsActivity.class);
        }

        THONGBAO thongBao = new THONGBAO();
        thongBao.setTieuDe(notiBody);
        thongBao.setThoiGianDang(newsPostTime);
        thongBao.setMaThongBao(Integer.parseInt(Objects.requireNonNull(newsId)));
        intent.putExtra(Reference.getInstance().BUNDLE_EXTRA_NEWS, THONGBAO.toJson(thongBao));
        intent.putExtra(Reference.getInstance().BUNDLE_KEY_NEWS_LAUNCH_FROM_NOTI, true);

        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Khoi tao thong bao
        NotificationCompat.Builder mBuilder = CreateNotificationCompatBuilder();
        mBuilder.setContentTitle(notiTitle);
        mBuilder.setContentText("Lúc " + newsPostTime);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notiBody));
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setGroup(Reference.getInstance().NEWS_NOTIFICATION);
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
        TINNHAN tinNhan = new TINNHAN();
        tinNhan.MaTinNhan = messageId;
        tinNhan.TieuDe = messageTitle;
        tinNhan.HoTenNguoiGui = messageSender;
        tinNhan.ThoiDiemGui = messageSendTime;

        if (mContext == null) { // Luc app ko chay
            context = this;
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            context = mContext;
            intent = new Intent(context, DetailMessageActivity.class);
        }
        intent.putExtra(Reference.getInstance().BUNDLE_EXTRA_MESSAGE, TINNHAN.toJson(tinNhan));
        intent.putExtra(Reference.getInstance().BUNDLE_KEY_MESSAGE_LAUNCH_FROM_NOTI, true);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Khoi tao thong bao
        NotificationCompat.Builder mBuilder = CreateNotificationCompatBuilder();
        mBuilder.setContentTitle(messageSender + " đã gửi 1 tin nhắn"); // Tieu de tb la ten nguoi gui
        mBuilder.setContentText("Lúc " + messageSendTime); // Noi dung la thoi gian gui
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageTitle)); // Noi dung chinh la tieu de tin nhan
        mBuilder.setGroup(Reference.getInstance().MESSAGE_NOTIFICATION);
        return mBuilder.build();
    }

    private Notification createAddScheduleNotification(RemoteMessage remoteMessage, ThoiKhoaBieu thoiKhoaBieu) {
        String notiTitle = remoteMessage.getData().get("title");
        String notiText = DateHelper.formatDateTimeString(remoteMessage.getData().get("postTime"));
        String date = DateHelper.formatYMDToDMY(thoiKhoaBieu.NgayHoc.substring(0, 10));
        StringBuilder schedule = new StringBuilder();
        schedule.append(thoiKhoaBieu.TenLopHocPhan)
                .append("\n * Ngày học: ")
                .append(date)
                .append("\n * Phòng : ")
                .append(thoiKhoaBieu.TenPhong)
                .append("\n * Tiết : ")
                .append(thoiKhoaBieu.TietHocBatDau)
                .append(" - ")
                .append(thoiKhoaBieu.TietHocKetThuc)
                .append(" ( ")
                .append(thoiKhoaBieu.getLessionStartTimeStr())
                .append(" - ")
                .append(thoiKhoaBieu.getLessionEndTimeStr())
                .append(" ) ")
                .append("\n * Giáo viên : ")
                .append(thoiKhoaBieu.HoVaTen);

        NotificationCompat.Builder builder = CreateNotificationCompatBuilder();
        builder.setContentTitle(notiTitle);
        builder.setContentText("Đưa ra lúc " + notiText);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(schedule));
        builder.setGroup(Reference.getInstance().ADD_SCHEDULE_NOTIFICATION);
        return builder.build();
    }

    private NotificationCompat.Builder CreateNotificationCompatBuilder() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.chanel_id));
        mBuilder.setSmallIcon(R.mipmap.noti_icon);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.large_icon));
        mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setAutoCancel(true);
        mBuilder.setGroupSummary(true);
        return mBuilder;
    }

    private void riseNotification(Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int norificationId = (int) SystemClock.uptimeMillis();
        notificationManager.notify(norificationId, notification);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("DEBUG", "Gọi khi nhận token mới: " + s);

        getSharedPreferences(getString(R.string.share_pre_key_firebase), MODE_PRIVATE)
                .edit()
                .putString(getString(R.string.pre_key_token), s)
                .apply();
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences(context.getString(R.string.share_pre_key_firebase), MODE_PRIVATE)
                .getString(context.getString(R.string.pre_key_token), null);
    }
}
