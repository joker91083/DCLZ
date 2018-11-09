package com.otitan.dclz.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.otitan.dclz.activity.BNaviGuideActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sp on 2018/10/17.
 * 导航
 */
public class BaiduNavigation {
    private Activity activity;
    //导航是否初始化成功 false 否 true 是
    private boolean isBaiduNaviInitSuccess = false;
    private static BaiduNavigation single;
    private static final String APP_FOLDER_NAME = "BaiduNavigation";
    private String mSDCardPath;
    private String authinfo;

    public static BaiduNavigation getInstance(Activity activity) {
        if (single == null) {
            synchronized (BaiduNavigation.class) {
                single = new BaiduNavigation(activity);
            }
        }
        return single;
    }

    private BaiduNavigation(Activity activity) {
        this.activity = activity;
    }



    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                boolean isCreate = f.mkdir();
                if (!isCreate) {
                    Log.e("tag", "导航文件夹创建失败");
                    return false;
                }
            } catch (Exception e) {
                Log.e("tag", "导航文件夹创建异常：" + e);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取内存卡地址
     *
     * @return
     */
    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }
}
