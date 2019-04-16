package com.practice.phuc.ums_husc.ViewModel;

import android.support.annotation.NonNull;

public class QuanHuyen {
    public int MaQuanHuyen;
    public String TenQuanHuyen;

    @NonNull
    @Override
    public String toString() {
        return this.TenQuanHuyen;
    }
}
