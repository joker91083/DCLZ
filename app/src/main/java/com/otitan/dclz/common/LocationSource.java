package com.otitan.dclz.common;

import android.app.Notification;
import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;

public interface LocationSource {

    AMapLocationClient initGdlocation(Context context, AMapLocationClient client, AMapLocationListener locationListener, Notification notification);

}
