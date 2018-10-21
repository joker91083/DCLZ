package com.otitan.dclz;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.otitan.dclz.bean.MyObjectBox;
import com.tencent.bugly.crashreport.CrashReport;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

@SuppressLint("Registered")
public class Myapplication extends Application {

    private static Context instance;
    //本地数据库
    private static BoxStore boxStore = null;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        //初始化bugly
        CrashReport.initCrashReport(getApplicationContext(), "6a98d5a0af", true);

        //ObjectBox初始化
        boxStore = MyObjectBox.builder().androidContext(this).build();
        if (BuildConfig.DEBUG) {
            //打开调试信息
            new AndroidObjectBrowser(boxStore).start(this);
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getInstance(){
        return instance;
    }

    public static BoxStore getBoxstore(){
        if(boxStore == null){
            boxStore = MyObjectBox.builder().androidContext(instance).build();
        }
        return boxStore;
    }




}
