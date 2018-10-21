package com.otitan.dclz.common;

import android.content.Context;

import com.otitan.dclz.bean.Trajectory;

public interface ITrajectoryModel {

    void sendTrajectory(Context context, Trajectory trajectory,ValueCallback callback);

}
