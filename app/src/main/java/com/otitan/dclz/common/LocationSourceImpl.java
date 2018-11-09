package com.otitan.dclz.common;


import android.app.Notification;
import android.content.Context;
import android.os.Build;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class LocationSourceImpl implements LocationSource {


    @Override
    public AMapLocationClient initGdlocation(Context context, AMapLocationClient client, AMapLocationListener listener, Notification notification) {
        if(client == null){
            client = new AMapLocationClient(context);
        }

        client.setLocationListener(listener);
        if (Build.VERSION.SDK_INT >= 26 && notification != null) {
            client.enableBackgroundLocation(2001, notification);// 调起前台定位
        }
        AMapLocationClientOption option = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        option.setInterval(1000);
        //设置是否返回地址信息（默认返回地址信息）
        option.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        option.setMockEnable(true);
        //关闭缓存机制
        option.setLocationCacheEnable(false);
        //设置GPS优先
        option.setGpsFirst(false);
        //设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        client.setLocationOption(option);
        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        client.stopLocation();
        client.startLocation();
        return client;
    }
}
