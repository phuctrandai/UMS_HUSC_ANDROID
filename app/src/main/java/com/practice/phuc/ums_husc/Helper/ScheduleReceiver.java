package com.practice.phuc.ums_husc.Helper;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.practice.phuc.ums_husc.Model.LOPHOCPHAN;
import com.practice.phuc.ums_husc.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleReceiver extends BroadcastReceiver {
    private Context mContext;
    private Intent mIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG", "ALARM !!!!!!!!!!!");
        mContext = context;
        mIntent = intent;

        /*TODO: Get classes from DB*/
        List<LOPHOCPHAN> todayClasses = new ArrayList<>();
        LOPHOCPHAN l = new LOPHOCPHAN(
                "2018-2019.1.TIN4063.001", "Phần mềm mã nguồn mở - Nhóm 1",
                "Lab 5_CNTT", 5, 7, "Nguyễn Dũng",
                "20/01/2019", "10/05/2019", DateHelper.MONDAY
        );
        todayClasses.add(l);

        l = new LOPHOCPHAN(
                "2018-2019.1.TIN4133.001", "Quản trị dự án phần mềm - Nhóm 1",
                "Lab 2_CNTT", 2, 4, "Nguyễn Mậu Hân",
                "20/01/2019", "03/05/2019", DateHelper.MONDAY
        );
        todayClasses.add(l);
        riseNotification(createScheduleNotification(todayClasses));
    }

    private Notification createScheduleNotification(List<LOPHOCPHAN> classes) {
        Date now = DateHelper.getCalendar().getTime();
        String today = DateHelper.getDayOfWeekStr(now) + " " + DateHelper.toShortDateString(now);
        StringBuilder schedule = new StringBuilder();

        for (int i = 0; i < classes.size(); i++) {
            LOPHOCPHAN item = classes.get(i);
            String no = i + 1 + ". ";
            schedule.append(no);
            schedule.append(item.getTenLopHocPhan());
            schedule.append("\n    Phòng : ");
            schedule.append(item.getPhongHoc());
            schedule.append("\n    Tiết : ");
            schedule.append(item.getTietBatDau());
            schedule.append(" - ");
            schedule.append(item.getTietKetThuc());
            schedule.append("\n    Giáo viên : ");
            schedule.append(item.getGiaoVien());
            schedule.append("\n");
        }

        NotificationCompat.Builder builder = createNotificationCompatBuilder();
        builder.setContentTitle("Lịch học hôm nay " + today);
        builder.setContentIntent(null);
        builder.setGroup(Reference.SCHEDULE_NOTIFICATION);
        builder.setVibrate(new long[]{1000, 1000, 1000});
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(schedule));

        return builder.build();
    }

    private NotificationCompat.Builder createNotificationCompatBuilder() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, mContext.getString(R.string.chanel_id));
        mBuilder.setSmallIcon(R.mipmap.noti_icon);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.large_icon));
        mBuilder.setColor(mContext.getResources().getColor(R.color.colorPrimary));
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setAutoCancel(true);
        mBuilder.setGroupSummary(true);
        return mBuilder;
    }

    private void riseNotification(Notification notification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        int norificationId = Integer.parseInt(String.valueOf(new Date().getTime() / 1000));
        notificationManager.notify(norificationId, notification);
    }
}