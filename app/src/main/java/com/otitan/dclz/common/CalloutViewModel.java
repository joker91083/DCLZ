package com.otitan.dclz.common;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.otitan.dclz.R;
import com.otitan.dclz.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalloutViewModel {

    public void showDistance(MapView mapView, Geometry geometry){
        if(null == geometry){
            return;
        }
        Point point = null;
        String value = "";
        if(geometry.getGeometryType() == GeometryType.POLYLINE){
            Polyline line = (Polyline) geometry;
            point = geometry.getExtent().getCenter();
            double length = Math.abs(GeometryEngine.length(line));
            value = Constant.disFormat.format(length)+"米";
        }else if(geometry.getGeometryType() == GeometryType.POLYGON){
            Polygon polygon = (Polygon)geometry;
            point = polygon.getExtent().getCenter();
            double area = Math.abs(GeometryEngine.area(polygon));
            value = Constant.disFormat.format(area)+"平方米";
        }

        TextView calloutContent = new TextView(mapView.getContext());
        calloutContent.setTextColor(Color.RED);
        calloutContent.setSingleLine();
        // format coordinates to 4 decimal places
        calloutContent.setText(value);

        Callout callout = mapView.getCallout();
        callout.setContent(calloutContent);
        assert point != null;
        callout.setLocation(point);
        callout.show();
    }

    /*展示小班属性信息*/
    public void showQueryInfo(MapView mapView, Feature feature, Geometry point){

        View view = LayoutInflater.from(mapView.getContext()).inflate(R.layout.callout_geometry_info,null);
        ListView listView = view.findViewById(R.id.callout);
        Map<String,Object> map = feature.getAttributes();
        List<String> attList = new ArrayList<>();
        for (String key:map.keySet()){
            attList.add(key+":"+map.get(key));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mapView.getContext(),R.layout.item_callout,attList);
        listView.setAdapter(adapter);
        //设置Callout样式
        Callout.Style style = new Callout.Style(mapView.getContext());
        style.setMaxWidth(250); //设置最大宽度
        style.setMaxHeight(400);  //设置最大高度
        style.setMinWidth(200);  //设置最小宽度
        style.setMinHeight(100);  //设置最小高度
        style.setBorderWidth(1); //设置边框宽度
        style.setBorderColor(Color.BLUE); //设置边框颜色
        style.setBackgroundColor(Color.WHITE); //设置背景颜色
        style.setCornerRadius(8); //设置圆角半径

        Callout callout = mapView.getCallout();
        callout.setStyle(style);
        callout.setContent(view);
        callout.setLocation(point.getExtent().getCenter());
        callout.show();

        mapView.invalidate();
    }

    /*显示点坐标值*/
    public void showPoint(MapView mapView,Geometry geometry){
        if(null == geometry){
            return;
        }
        Point mapPoint = (Point) geometry;
        Point point = (Point) GeometryEngine.project(geometry,SpatialReference.create(4326));

        TextView calloutContent = new TextView(mapView.getContext());
        calloutContent.setTextSize(16);
        calloutContent.setTextColor(Color.RED);
        //calloutContent.setSingleLine();
        // format coordinates to 4 decimal places
        String lon = Constant.sixFormat.format(point.getX());
        String lat = Constant.sixFormat.format(point.getY());
        String X = Constant.sixFormat.format(mapPoint.getX());
        String Y = Constant.sixFormat.format(mapPoint.getY());

        String value = "经度: "+lon+"\n纬度: "+lat+"\nX: "+X+"\nY: "+Y;
        calloutContent.setText(value);

        Callout callout = mapView.getCallout();
        callout.setContent(calloutContent);
        callout.setLocation(point);
        callout.show();

    }

    


}
