package com.otitan.dclz.common;

import android.app.Activity;
import android.content.Context;

import com.otitan.dclz.Myapplication;
import com.otitan.dclz.bean.EventReport;
import com.otitan.dclz.net.RetrofitHelper;
import com.titan.baselibrary.util.ConverterUtils;
import com.titan.baselibrary.util.ProgressDialogUtil;
import com.titan.baselibrary.util.ToastUtil;

import io.objectbox.Box;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EventReportModel {


    /*上传到服务器*/
    public void senInofToServer(String json, final String id,final Activity activity) {
        Observable<String> oberver = RetrofitHelper.getInstance(activity).getServer().upPatrolEvent(json);
        oberver.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        ToastUtil.setToast(activity,"网络连接错误"+throwable.getMessage());
                        ProgressDialogUtil.stopProgressDialog(activity);
                    }

                    @Override
                    public void onNext(String s) {
                        ProgressDialogUtil.stopProgressDialog(activity);
                        if(s.equals("true")){
                            ToastUtil.setToast(activity, "上报成功");
                            if(id.equals("")){
                                delLocalResport(ConverterUtils.toLong(id));
                            }
                            activity.finish();
                        }else{
                            ToastUtil.setToast(activity, "上报失败");
                        }
                    }
                });
    }


    /*保存到本地数据库*/
    public boolean addLocalResport(Context mContext, EventReport report){

        Box<EventReport> reportBox = Myapplication.getBoxstore().boxFor(EventReport.class);
        long id = reportBox.put(report);
        if(id > 0){
            return true;
        }
        return false;
    }

    /*删除本地数据库的数据*/
    public void delLocalResport(long id){
        Box<EventReport> reportBox = Myapplication.getBoxstore().boxFor(EventReport.class);
        reportBox.remove(id);
    }



}
