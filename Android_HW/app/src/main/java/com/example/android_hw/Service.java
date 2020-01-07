package com.example.android_hw;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class Service {
    private Context context;

    Service(Context context) {
        this.context = context;
    }

    boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}