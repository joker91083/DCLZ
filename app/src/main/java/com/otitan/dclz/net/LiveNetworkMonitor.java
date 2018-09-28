package com.otitan.dclz.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by whs on 2017/2/24
 * 是否有网络
 */

public class LiveNetworkMonitor implements NetworkMonitor {
    private final Context applicationContext;

    LiveNetworkMonitor(Context context) {
        applicationContext = context.getApplicationContext();
    }

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
