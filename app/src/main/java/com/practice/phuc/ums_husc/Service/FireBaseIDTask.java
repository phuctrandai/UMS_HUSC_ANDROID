package com.practice.phuc.ums_husc.Service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FireBaseIDTask extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {
        try {
            String url = params[0];
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/xml; charset=utf-8"), "");
            Response response = NetworkUtil.makeRequest(url, true, requestBody);
            if (response != null) {
                if (response.code() == NetworkUtil.OK) {
                    Log.e("DEBUG", "FireBase token id do in background Success !!!");
                    return true;
                } else if (response.code() == NetworkUtil.BAD_REQUEST) {
                    Log.e("DEBUG", "FireBase token id do in background Badrequest: " +
                            (response.body() != null ? response.body().string() : null));
                    return false;
                }
            }
        } catch (Exception ex) {
            Log.e("DEBUG", "FireBase token id do in background Fail: " + ex.toString());
        }
        Log.e("DEBUG", "FireBase token id do in background Fail !!!");
        return false;
    }

    public static void saveTokenForAccount(Context context, String maSinhVien, String token) {
        Log.e("DEBUG", "FireBase save token");
        new FireBaseIDTask().execute(Reference.getInstance().getSaveTokenApiUrl(context, maSinhVien, token));
    }

    public static void deleteTokenFromAccount(Context context, String maSinhVien, String token) {
        Log.e("DEBUG", "FireBase delete token");
        new FireBaseIDTask().execute(Reference.getInstance().getDeleteTokenApiUrl(context, maSinhVien, token));
    }
}
