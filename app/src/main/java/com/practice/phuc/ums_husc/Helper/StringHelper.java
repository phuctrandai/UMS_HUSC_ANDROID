package com.practice.phuc.ums_husc.Helper;

public class StringHelper {
    public static boolean isNullOrEmpty(final String checkStr) {
        return checkStr == null || checkStr.isEmpty();
    }

    public static String getValueOrEmpty(final String string) {
        return isNullOrEmpty(string) ? "..." : string;
    }

    public static String getFirstCharToCap(final String string) {
        return string.substring(0, 1).toUpperCase();
    }
}
