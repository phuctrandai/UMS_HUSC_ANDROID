package com.practice.phuc.ums_husc.Helper;

public class DateHelper {
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
    public static String formatDateTimeString(String dateTimeString) {
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
    public static String formatTimeString(String timeString) {
        String result = "";
        if (timeString != null) {
            String arr[] = timeString.split(":");
            String hh = arr[0];
            String mm = arr[1];
            result = hh + ":" + mm;
        }
        return result;
    }
}
