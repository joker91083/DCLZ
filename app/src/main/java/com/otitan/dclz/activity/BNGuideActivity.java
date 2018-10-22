package com.otitan.dclz.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
/*import com.baidu.navisdk.adapter.BNaviBaseCallbackModel;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.NaviModuleFactory;
import com.baidu.navisdk.adapter.NaviModuleImpl;*/

/**
 * Created by sp on 2018/10/17.
 * 百度导航
 */
public class BNGuideActivity extends AppCompatActivity {

//    //百度导航通用模块
//    private BaiduNaviCommonModule mBaiduNaviCommonModule = null;
//
//    private BNRoutePlanNode mBNRoutePlanNode = null;
//    private long mExitTime;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        View view = null;
//        mBaiduNaviCommonModule = NaviModuleFactory.getNaviModuleManager().getNaviCommonModule(
//                NaviModuleImpl.BNaviCommonModuleConstants.ROUTE_GUIDE_MODULE, this,
//                BNaviBaseCallbackModel.BNaviBaseCallbackConstants.CALLBACK_ROUTEGUIDE_TYPE, mOnNavigationListener);
//        if (mBaiduNaviCommonModule != null) {
//            mBaiduNaviCommonModule.onCreate();
//            view = mBaiduNaviCommonModule.getView();
//        }
//        if (view != null) {
//            setContentView(view);
//        }
//
//        Intent intent = getIntent();
//        if (intent != null) {
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                mBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable("routePlanNode");
//            }
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        if (mBaiduNaviCommonModule != null) {
//            mBaiduNaviCommonModule.onResume();
//        }
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mBaiduNaviCommonModule != null) {
//            mBaiduNaviCommonModule.onPause();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (mBaiduNaviCommonModule != null) {
//            mBaiduNaviCommonModule.onDestroy();
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onStop() {
//        if (mBaiduNaviCommonModule != null) {
//            mBaiduNaviCommonModule.onStop();
//        }
//        super.onStop();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (mBaiduNaviCommonModule != null) {
//            mBaiduNaviCommonModule.onBackPressed(false);
//        }
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        if (mBaiduNaviCommonModule != null) {
//            mBaiduNaviCommonModule.onConfigurationChanged(newConfig);
//        }
//        super.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode==KeyEvent.KEYCODE_BACK){
//            if ((System.currentTimeMillis() - mExitTime) > 1500){
//                Toast.makeText(this,"再按一次导航结束",Toast.LENGTH_SHORT).show();
//                mExitTime = System.currentTimeMillis();
//                return true;
//            }else {
//                if (mBaiduNaviCommonModule != null) {
//                    Bundle mBundle = new Bundle();
//                    mBundle.putInt("keyCode", keyCode);
//                    mBundle.putParcelable("event", event);
//                    mBaiduNaviCommonModule.setModuleParams(1, mBundle);
//                    try {
//                        Boolean ret = (Boolean) mBundle.get("module.ret");
//                        if (ret != null && ret) {
//                            return true;
//                        }
//                    } catch (Exception e) {
//                        Log.e("tag", "导航返回按钮异常：" + e);
//                    }
//                }
//            }
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    private BNRouteGuideManager.OnNavigationListener mOnNavigationListener = new BNRouteGuideManager.OnNavigationListener() {
//
//        @Override
//        public void onNaviGuideEnd() {
//            //退出导航
//            finish();
//        }
//
//        @Override
//        public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {
//
//            if (actionType == 0) {
//                //导航到达目的地 自动退出
//                Log.e("tag", "notifyOtherAction actionType = " + actionType + ",导航到达目的地！");
//            }
//
//            Log.e("tag", "actionType:" + actionType + "arg1:" + arg1 + "arg2:" + arg2 + "obj:" + obj.toString());
//        }
//
//    };
}
