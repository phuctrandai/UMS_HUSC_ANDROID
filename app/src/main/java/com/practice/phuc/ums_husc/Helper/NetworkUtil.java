package com.practice.phuc.ums_husc.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtil {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    /* Status response code */
    public static final int OK = 200;
    public static final int NOT_FOUND = 404;
    public static final int BAD_REQUEST = 400;

    public static int getConnectivityStatus(Context context) {
        if (context == null) return TYPE_WIFI;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    public static Response makeRequest(String url, boolean isPostMethod, RequestBody requestBody) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        Request request = null;
        if (requestBody == null)
            requestBody = RequestBody.create(
                MediaType.parse("application/xml; charset=utf-8"), ""
            );
        try {
            if (isPostMethod) {
                request = new Request.Builder().url(url).post(requestBody).build();
            } else {
                request = new Request.Builder().url(url).get().build();
            }
            return okHttpClient.newCall(request).execute();
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
