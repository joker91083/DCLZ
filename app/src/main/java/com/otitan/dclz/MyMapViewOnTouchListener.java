package com.otitan.dclz;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;

import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapView;

/**
 * Created by sp on 2018/10/15.
 * 地图点击监听
 */
public class MyMapViewOnTouchListener extends DefaultMapViewOnTouchListener {

    public MyMapViewOnTouchListener(Context context, MapView mapView) {
        super(context, mapView);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Point point = new Point(Integer.parseInt(String.valueOf(e.getX())), Integer.parseInt(String.valueOf(e.getY())));
        return super.onSingleTapConfirmed(e);
    }
}
