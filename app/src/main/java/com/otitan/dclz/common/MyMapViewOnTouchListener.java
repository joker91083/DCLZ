package com.otitan.dclz.common;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapView;

public class MyMapViewOnTouchListener extends DefaultMapViewOnTouchListener {

    private MapView mapView;
    private Context mContext;
    private View.OnTouchListener _listener;
    private DefaultMapViewOnTouchListener _maplistener;
    private ValueCallback callback;

    public MyMapViewOnTouchListener(Context context, MapView mapView,View.OnTouchListener listener,ValueCallback callback) {
        super(context, mapView);
        this.mContext = context;
        this.mapView = mapView;
        this._listener = listener;
        this._maplistener = (DefaultMapViewOnTouchListener) listener;
        this.callback = callback;
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        if (_maplistener != null) {
            return _maplistener.onSingleTapUp(e);
        }
        return false;
    }


    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_UP){
            Geometry geometry = mapView.getSketchEditor().getGeometry();
            callback.onGeometry(geometry);
        }

        if (_maplistener != null) {
            return _maplistener.onTouch(v, e);
        }
        if (_listener != null)
            return _listener.onTouch(v, e);

        return false;
    }
}
