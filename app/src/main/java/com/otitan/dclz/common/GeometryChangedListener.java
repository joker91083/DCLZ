package com.otitan.dclz.common;

import android.view.View;

import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedEvent;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedListener;

public class GeometryChangedListener implements SketchGeometryChangedListener {
    private ValueCallback callback;
    private MapView mapView;

    public GeometryChangedListener(ValueCallback callback,MapView mapView){
        this.callback = callback;
        this.mapView = mapView;
    }

    @Override
    public void geometryChanged(SketchGeometryChangedEvent event) {
        boolean b = event.getSource().isSketchValid();
        Geometry geometry = event.getGeometry();
        boolean flag = false;
        if(geometry != null){
            flag = (geometry.getGeometryType() == GeometryType.POINT);
        }
        if(b && flag){
            callback.onGeometry(geometry);
        }else if(b && !flag){
            setOnTouchListener();
        }
    }

    public void setOnTouchListener(){
        View.OnTouchListener lo = mapView.getOnTouchListener();
        if (lo instanceof MyMapViewOnTouchListener){
            return;
        }
        MyMapViewOnTouchListener touchListener = new MyMapViewOnTouchListener(mapView.getContext(),mapView,lo,callback);
        mapView.setOnTouchListener(touchListener);
    }

}
