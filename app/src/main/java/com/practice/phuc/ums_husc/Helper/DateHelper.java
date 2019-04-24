package com.practice.phuc.ums_husc.Helper;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    public final static int SUNDAY = 1;
    public final static int MONDAY = 2;
    public final static int TUESDAY = 3;
    public final static int WEDNESDAY = 4;
    public final static int THURSDAY = 5;
    public final static int FRIDAY = 6;
    public final static int SATURDAY = 7;

    public final static int MORNING = 8;
    public final static int AFTERNOON = 9;
    public final static int EVENING = 10;

    private static Calendar calendar;
    public static Calendar getCalendar() {
        if (calendar == null) calendar = Calendar.getInstance(new Locale("vie", "VN"));
        return calendar;
    }

    public static String formatYMDToDMY(String dateString) {
        String result = "";
        String arr[] = {};
        String splitChar = "/";
        if (dateString.contains("-")) {
            arr = dateString.split("-");
//            splitChar = "-";
        } else if (dateString.contains("/")) {
            arr = dateString.split("/");
//            splitChar = "/";
        } else if (dateString.contains(".")) {
            arr = dateString.split(".");
//            splitChar = ".";
        }
        result += arr[2] + splitChar;
        result += arr[1] + splitChar;
        result += arr[0];
        return result;
    }

    public static String formatMDYToDMY(String dateString) {
        String result = "";
        if (dateString != null) {
            String arr[] = {};
            String splitChar = "/";
            if (dateString.contains("-")) {
                arr = dateString.split("-");
//            splitChar = "-";
            } else if (dateString.contains("/")) {
                arr = dateString.split("/");
//            splitChar = "/";
            } else if (dateString.contains(".")) {
                arr = dateString.split(".");
//            splitChar = ".";
            }
            if (arr[1].length() == 1) arr[1] = "0" + arr[1]; // D -> DD
            if (arr[0].length() == 1) arr[0] = "0" + arr[0]; // M -> MM

            result += arr[1] + splitChar;   //DD
            result += arr[0] + splitChar;   //MM
            result += arr[2];               // YYYY
        }
        return result;
    }

    // MM/DD/YYYY hh:mm:ss pm -> DD/MM/YYYY hh:mm pm
    static String formatDateTimeString(String dateTimeString) {
        String result = "";
        if (dateTimeString != null) {
            String arr[] = dateTimeString.split(" ");
            String dateStr = arr[0];
            String timeStr = arr[1];
            String timeAPM = arr[2];

            result += formatMDYToDMY(dateStr) + " " + formatTimeString(timeStr) + " " + timeAPM;
        }
        return result;
    }

    // hh:mm:ss -> hh:mm
    private static String formatTimeString(String timeString) {
        String result = "";
        if (timeString != null) {
            String arr[] = timeString.split(":");
            String hh = arr[0];
            String mm = arr[1];
            result = hh + ":" + mm;
        }
        return result;
    }

    public static String toShortDateString(Date date) {
        int nDayOfMonth = getDayOfMonth(date);
        int nMonth = getMonth(date);
        String dayOfMonth = nDayOfMonth < 10 ? "0" + nDayOfMonth : nDayOfMonth + "";
        String month = nMonth < 10 ? "0" + nMonth : nMonth + "";
        return dayOfMonth + "/" + month + "/" + getYear(date);
    }

    public static String toDateTimeString(Date date) {
        int nMinute = getMinute(date);
        String minute = nMinute < 10 ? "0" + nMinute : nMinute + "";
        return toShortDateString(date) + " " + DateHelper.getHourOfDay(date) + ":" + minute;
    }

    public static Date plusDay(Date date, int days) {
        Calendar calendar = Calendar.getInstance(new Locale("vie", "VN"));
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static Date stringToDate(String dateStr, String dateFormat) {
        try {
            @SuppressLint("SimpleDateFormat") Date date = new SimpleDateFormat(dateFormat).parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date minDateInArr(Date[] dateArr) {
        Date minDate = null;
        int length = dateArr.length;
        if (length > 0) {
            minDate = dateArr[0];
            for (Date aDateArr : dateArr) {
                int compare = minDate.compareTo(aDateArr);
                if (compare > 0) minDate = aDateArr;
            }
        }
        return minDate;
    }

    public static Date maxDateInArr(Date[] dateArr) {
        Date maxDate = null;
        int length = dateArr.length;
        if (length > 0) {
            maxDate = dateArr[0];
            for (Date aDateArr : dateArr) {
                int compare = maxDate.compareTo(aDateArr);
                if (compare < 0) maxDate = aDateArr;
            }
        }
        return maxDate;
    }

    public static Date getTheFirstDateOfWeek(Date date) {
        int dayOfWeek = getDayOfWeek(date);
        switch (dayOfWeek){
            case SUNDAY: return plusDay(date, -6);
            case TUESDAY: return plusDay(date, -1);
            case WEDNESDAY: return plusDay(date, -2);
            case THURSDAY: return plusDay(date, -3);
            case FRIDAY: return plusDay(date, -4);
            case SATURDAY: return plusDay(date, -5);
            default: return date;
        }
    }

    public static Date getDate(Date startDateOfWeek, int dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return startDateOfWeek;
            case TUESDAY: return plusDay(startDateOfWeek, 1);
            case WEDNESDAY: return plusDay(startDateOfWeek, 2);
            case THURSDAY: return plusDay(startDateOfWeek, 3);
            case FRIDAY: return plusDay(startDateOfWeek, 4);
            case SATURDAY: return plusDay(startDateOfWeek, 5);
            case SUNDAY: return plusDay(startDateOfWeek, 6);
            default: return null;
        }
    }

    public static String getDayOfWeekStr(Date date) {
        int dayOfWeek = getDayOfWeek(date);
        switch (dayOfWeek){
            case SUNDAY: return "Chủ nhật";
            case MONDAY: return "Thứ 2";
            case TUESDAY: return "Thứ 3";
            case WEDNESDAY: return "Thứ 4";
            case THURSDAY: return "Thứ 5";
            case FRIDAY: return "Thứ 6";
            case SATURDAY: return "Thứ 7";
            default: return null;
        }
    }

    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance(new Locale("vie", "VN"));
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance(new Locale("vie", "VN"));
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance(new Locale("vie", "VN"));
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance(new Locale("vie", "VN"));
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getHourOfDay(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static boolean isBetweenTwoDate(Date startDate, Date endDate, Date current) {
        boolean a = beforeOrEquals(startDate, current);
        boolean d = beforeOrEquals(current, endDate);
        return a && d;
    }

    private static boolean beforeOrEquals(Date thisDate, Date compareToDate) {
        int dayOfMonth_1 = getDayOfMonth(thisDate);
        int month_1 = getMonth(thisDate);
        int year_1 = getYear(thisDate);
        int dayOfMonth_2 = getDayOfMonth(compareToDate);
        int month_2 = getMonth(compareToDate);
        int year_2 = getYear(compareToDate);

        if (dayOfMonth_1 == dayOfMonth_2 && month_1 == month_2 && year_1 == year_2) return true;

        else if (year_1 == year_2) {

            if (month_1 < month_2) return true;

            else if (month_1 > month_2) return false;

            else return dayOfMonth_1 <= dayOfMonth_2;

        } else {
            return year_1 <= year_2;
        }
    }
}
