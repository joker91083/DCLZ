package com.otitan.dclz.net.local;

import com.otitan.dclz.bean.TrackPoint;
import com.otitan.dclz.bean.Trajectory;
import com.otitan.dclz.common.ValueCallback;

import java.util.Date;

public interface LocalDataSource {

    /**
     * 保存轨迹到本地数据库
     */
    void saveTrackPoint(TrackPoint trackPoint, ValueCallback callback);

    /**
     * 轨迹查询
     */
    void queryTrackPoint(String id,ValueCallback callback);
    /**
     * 保存轨迹记录
     */
    void saveTrajectory(Trajectory trajectory,ValueCallback callback);
    /**
     *
     *查询本地轨迹记录
     */
    void queryTrajectory(ValueCallback callback);


}
