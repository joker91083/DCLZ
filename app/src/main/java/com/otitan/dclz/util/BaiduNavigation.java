package com.otitan.dclz.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.baidu.android.common.logging.Log;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.otitan.dclz.activity.BNGuideActivity;
import com.otitan.dclz.activity.BNaviGuideActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 初始化导航
     */
    public void initNavigator(Activity activity, final String startNaviName, final String endNaviName, final LatLng[] naviLatLng) {
        Toast.makeText(activity,"正在初始化，请稍候...",Toast.LENGTH_SHORT).show();
        initDirs();
        if (!isBaiduNaviInitSuccess) {

            BaiduNaviManager.getInstance().init(activity, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
                @Override
                public void onAuthResult(int i, String s) {
                    if (0 == i) {
                        authinfo = "key校验成功!";
                    } else {
                        authinfo = "key校验失败, " + s;
                    }
                    Log.e("tag", "key校验情况:" + authinfo);
                }

                @Override
                public void initStart() {
                    Log.e("tag", "百度导航引擎初始化开始");
                }

                @Override
                public void initSuccess() {
                    Log.e("tag", "百度导航引擎初始化成功");
                    isBaiduNaviInitSuccess = true;

                    // 设置显示总路况条
                    BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
                    // 设置导航播报模式  Novice 新手模式, Quite 静音, Veteran 老手模式
                    BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Novice);
                    // 是否开启路况  NAVI_ITS_ON 开, NAVI_ITS_OFF 关
                    BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
                    // 到达目的地时自动退出
                    BNaviSettingManager.setIsAutoQuitWhenArrived(true);

                    Bundle bundle = new Bundle();
                    // 必须设置APPID，否则会静音
                    bundle.putString(BNCommonSettingParam.TTS_APP_ID, "10356243");
                    BNaviSettingManager.setNaviSdkParam(bundle);

                    //开始百度导航
                    routePlanToNavi(startNaviName, endNaviName, naviLatLng);

                }

                @Override
                public void initFailed() {
                    Log.e("tag", "百度导航引擎初始化失败");
                    isBaiduNaviInitSuccess = false;
                }
            }, null, ttsCallbackHandler, ttsPlayStateListener);
        } else {
            routePlanToNavi(startNaviName, endNaviName, naviLatLng);
        }
    }

    /**
     * 路线规划函数
     *
     * @param startNaviName 导航起始地
     * @param endNaviName   导航目的地
     */
    private void routePlanToNavi(String startNaviName, String endNaviName, LatLng[] naviLatLng) {
        BNRoutePlanNode sNode = new BNRoutePlanNode(naviLatLng[0].longitude, naviLatLng[0].latitude, startNaviName, null, BNRoutePlanNode.CoordinateType.WGS84);
        BNRoutePlanNode eNode = new BNRoutePlanNode(naviLatLng[1].longitude, naviLatLng[1].latitude, endNaviName, null, BNRoutePlanNode.CoordinateType.WGS84);
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(sNode);
        list.add(eNode);
        BaiduNaviManager.getInstance().launchNavigator(activity, list, 1, true, new BaiduRoutePlanListener(sNode));
    }

    class BaiduRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode node = null;

        BaiduRoutePlanListener(BNRoutePlanNode node) {
            this.node = node;
        }

        @Override
        public void onJumpToNavigator() {
            Intent intent = new Intent(activity, BNaviGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("routePlanNode", node);
            intent.putExtras(bundle);
            activity.startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
//            MaterialDialogUtil.showSureDialog(activity, "路线规划失败").build().show();
        }
    }

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsCallbackHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    //showToastMsg("Handler : TTS play start");
                    //                    Toast.makeText(activity,"开始播报语音",Toast.LENGTH_SHORT).show();
                    Log.e("tag", "TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    //showToastMsg("Handler : TTS play end");
                    //                    Toast.makeText(activity,"播报语音结束",Toast.LENGTH_SHORT).show();
                    Log.e("tag", "TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            //showToastMsg("TTSPlayStateListener : TTS play end");
            //            Toast.makeText(activity,"播报语音结束",Toast.LENGTH_SHORT).show();
            Log.e("tag", "TTS play end");
        }

        @Override
        public void playStart() {
            //showToastMsg("TTSPlayStateListener : TTS play start");
            //            Toast.makeText(activity,"开始播报语音",Toast.LENGTH_SHORT).show();
            Log.e("tag", "TTS play start");
        }
    };

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
