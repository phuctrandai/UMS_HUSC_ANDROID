package com.practice.phuc.ums_husc.Helper;

public class StringHelper {
    public static boolean isNullOrEmpty(final String checkStr) {
        return checkStr == null || checkStr.isEmpty();
    }

    public static String getValueOrEmpty(final String string) {
        return isNullOrEmpty(string) ? "..." : string;
    }
}
