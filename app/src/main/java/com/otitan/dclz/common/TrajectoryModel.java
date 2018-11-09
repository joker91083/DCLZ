package com.otitan.dclz.common;

import android.content.Context;

import com.google.gson.Gson;
import com.otitan.dclz.bean.Trajectory;
import com.otitan.dclz.net.RetrofitHelper;
import com.titan.baselibrary.util.ToastUtil;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*记录轨迹*/
public class TrajectoryModel implements ITrajectoryModel {


    @Override
    public void sendTrajectory(final Context context, final Trajectory trajectory, final ValueCallback callback) {
        Gson gson = new Gson();
        String json = gson.toJson(trajectory);
        rx.Observable<String> oberver = RetrofitHelper.getInstance(context).getServer().uPPatrolLine(json);
        oberver.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        ToastUtil.setToast(context,"轨迹记录上传异常"+throwable.getMessage());
                        sendTrajectory(context,trajectory,callback);
                    }

                    @Override
                    public void onNext(String s) {
                        if(s.equals("true") && trajectory.getXC_ENDTIME().equals("")){
                            //返回巡查路线ID
                            callback.onSuccess(trajectory);

                        }else if(s.equals("true") && !trajectory.getXC_ENDTIME().equals("")){

                            ToastUtil.setToast(context,"轨迹记录上传成功");

                        }else if(s.equals("false") && !trajectory.getXC_ENDTIME().equals("")){

                            ToastUtil.setToast(context,"轨迹记录上传失败");
                        }
                    }
                });
    }


}
