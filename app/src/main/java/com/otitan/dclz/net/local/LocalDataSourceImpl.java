package com.otitan.dclz.net.local;

import com.otitan.dclz.Myapplication;
import com.otitan.dclz.bean.TrackPoint;
import com.otitan.dclz.bean.TrackPoint_;
import com.otitan.dclz.bean.Trajectory;
import com.otitan.dclz.common.ValueCallback;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.query.Query;
import io.objectbox.reactive.DataObserver;

public class LocalDataSourceImpl implements LocalDataSource{


    @Override
    public void saveTrackPoint(TrackPoint trackPoint, ValueCallback callback) {
        Box<TrackPoint> trackPointBox = Myapplication.getBoxstore().boxFor(TrackPoint.class);
        long id = trackPointBox.put(trackPoint);
        if(id > 0){
            callback.onSuccess("本地保存轨迹点成功");
        }else{
            callback.onSuccess("本地保存轨迹点失败");
        }
    }

    @Override
    public void queryTrackPoint(String id, final ValueCallback callback) {
        try {
            Box<TrackPoint> trackPointBox = Myapplication.getBoxstore().boxFor(TrackPoint.class);
            Query<TrackPoint> query = trackPointBox.query().contains(TrackPoint_.tid,id).build();
            query.subscribe().on(AndroidScheduler.mainThread()).observer(new DataObserver<List<TrackPoint>>() {
                @Override
                public void onData(List<TrackPoint> data) {
                    callback.onSuccess(data);
                }
            });
        }catch (Exception e){
            callback.onFail("查询轨迹点异常$e");
        }
    }

    @Override
    public void saveTrajectory(Trajectory trajectory, ValueCallback callback) {
        Box<Trajectory> trajectoryBox = Myapplication.getBoxstore().boxFor(Trajectory.class);
        long id = trajectoryBox.put(trajectory);
        if(id > 0){
            callback.onSuccess("轨迹记录本地保存成功");
        }else{
            callback.onSuccess("轨迹记录本地保存失败");
        }
    }

    @Override
    public void queryTrajectory(final ValueCallback callback) {
        try {
            Box<Trajectory> trajectoryBox = Myapplication.getBoxstore().boxFor(Trajectory.class);
            Query<Trajectory> query = trajectoryBox.query().build();
            query.subscribe().on(AndroidScheduler.mainThread()).observer(new DataObserver<List<Trajectory>>() {
                @Override
                public void onData(List<Trajectory> data) {
                    callback.onSuccess(data);
                }
            });
        }catch (Exception e){
            callback.onFail("查询轨迹记录异常$e");
        }
    }


}
