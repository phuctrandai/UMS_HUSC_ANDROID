package com.practice.phuc.ums_husc.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.practice.phuc.ums_husc.Model.LOPHOCPHAN;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG", "ALARM !!!!!!!!!!!");

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

        ScheduleDailyNotification.riseNotification(
                context, ((int) (Calendar.getInstance().getTimeInMillis() / 1000)),
                ScheduleDailyNotification.createScheduleNotification(context, todayClasses));
    }
}