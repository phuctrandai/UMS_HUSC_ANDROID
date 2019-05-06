package com.practice.phuc.ums_husc.ScheduleModule;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.ThoiKhoaBieu;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class ScheduleDailyNotification {

    public static void setReminder(Context context, int requestCode, Class<?> cls, long interval, int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);

        if (interval != -1 && setcalendar.before(calendar)) {
            setcalendar.add(Calendar.DATE, 1);
            Log.d("DEBUG", "setReminder: Alarm will schedule for tomorrow !");
        }

        // Enable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, cls);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (interval == -1) { // Alarm for classes of today
            if (!setcalendar.before(calendar)) {
                am.set(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent);
                Log.d("DEBUG", "setReminder: Set alarm for today classes !");
            }
        } else { // Alarm as daily service
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(),
                    interval, pendingIntent);
            Log.d("DEBUG", "setReminder: Set daily alarm at 00:00 !");
        }

    }

    public static void cancelReminder(Context context, int requestCode, Class<?> cls) {
        // Disable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    private static NotificationCompat.Builder createNotificationCompatBuilder(Context context) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, context.getString(R.string.chanel_id));
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.large_icon));
        mBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setGroup(Reference.SCHEDULE_NOTIFICATION);
        mBuilder.setSmallIcon(R.mipmap.noti_icon);
        return mBuilder;
    }

    static Notification createScheduleNotification(Context context, List<ThoiKhoaBieu> classes) {
        Date now = DateHelper.getCalendar().getTime();
        String today = DateHelper.getDayOfWeekStr(now) + " ngày " + DateHelper.toShortDateString(now);
        StringBuilder schedule = new StringBuilder();

        for (int i = 0; i < classes.size(); i++) {
            schedule.append("\n");
            ThoiKhoaBieu item = classes.get(i);
            String no = i + 1 + ". ";
            schedule.append(no);
            schedule.append(item.TenLopHocPhan);
            schedule.append("\n * Phòng : ");
            schedule.append(item.TenPhong);
            schedule.append("\n * Tiết : ");
            schedule.append(item.TietHocBatDau);
            schedule.append(" - ");
            schedule.append(item.TietHocKetThuc);
            schedule.append("\n * Giáo viên : ");
            schedule.append(item.HoVaTen);
        }

        NotificationCompat.Builder builder = createNotificationCompatBuilder(context);
        builder.setContentTitle("Lịch học hôm nay " + today);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(schedule));

        return builder.build();
    }

    static void riseNotification(Context context, int id, Notification notification) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }
}
