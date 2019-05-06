package com.practice.phuc.ums_husc.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceHelper {

    private SharedPreferenceHelper() {
    }

    private static SharedPreferenceHelper instance;

    public static SharedPreferenceHelper getInstance() {
        instance = instance == null ? new SharedPreferenceHelper() : instance;
        return instance;
    }

    public String getSharedPrefStr(Context context, String sharedName, String preKey, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(sharedName, MODE_PRIVATE);
        return sp.getString(preKey, defValue);
    }

    public boolean getSharedPrefBool(Context context, String sharedName, String preKey, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(sharedName, MODE_PRIVATE);
        return sp.getBoolean(preKey, defValue);
    }

    public int getSharedPrefInt(Context context, String sharedName, String preKey, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(sharedName, MODE_PRIVATE);
        return sp.getInt(preKey, defValue);
    }

    public void setSharedPref(Context context, String sharedName, String preKey, String value) {
        SharedPreferences sp = context.getSharedPreferences(sharedName, MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putString(preKey, value);
        et.apply();
    }

    public void setSharedPref(Context context, String sharedName, String preKey, int value) {
        SharedPreferences sp = context.getSharedPreferences(sharedName, MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putInt(preKey, value);
        et.apply();
    }

    public void setSharedPref(Context context, String sharedName, String preKey, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(sharedName, MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putBoolean(preKey, value);
        et.apply();
    }

    /*
     * Account
     */
    public static String ACCOUNT_SP = "student";
    public static String ACCOUNT_ID = "account_id";
    public static String ACCOUNT_PASSWORD = "password";
    public static String ACCOUNT_NAME = "name";
    public static String STUDENT_ID = "student_id";
    public static String STUDENT_COURSE = "course";
    public static String STUDENT_MAJORS = "majors";
    public static String STUDENT_SEMSTER = "semester";
    public static String STUDENT_SEMSTER_STR = "semester_string";
}
