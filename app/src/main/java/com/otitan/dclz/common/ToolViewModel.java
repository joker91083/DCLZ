package com.otitan.dclz.common;

import android.content.Context;
import android.content.Intent;

import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.esri.arcgisruntime.arcgisservices.ArcGISMapServiceSublayerInfo;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISSublayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.util.ListenableList;
import com.otitan.dclz.activity.GdnavigationActivity;
import com.otitan.dclz.activity.NavigationActivity;
import com.otitan.dclz.dialog.LayerControlDialog;
import com.otitan.dclz.R;
import com.titan.baselibrary.util.ToastUtil;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class ToolViewModel {

    /*图层控制*/
    public void showLayer(Context mContext,MapView mapView,Point point){
        LayerControlDialog lcDialog = new LayerControlDialog(mContext, R.style.style_Dialog, mapView,this,point);
        lcDialog.show();
    }


    /*测量距离*/
    public void distance(MapView mapView){

        SketchEditor sketchEditor = mapView.getSketchEditor();
        sketchEditor.start(SketchCreationMode.POLYLINE);

    }

    /*测量面积*/
    public void area(MapView mapView){
        SketchEditor sketchEditor = mapView.getSketchEditor();
        sketchEditor.start(SketchCreationMode.FREEHAND_POLYGON);
    }

    /*清除标会*/
    public void clear(MapView mapView){
        ListenableList<GraphicsOverlay> graphicsOverlays = mapView.getGraphicsOverlays();
        for(GraphicsOverlay overlay : graphicsOverlays){
            overlay.getGraphics().clear();
        }

        mapView.getSketchEditor().clearGeometry();

        mapView.getCallout().dismiss();

        mapView.getSketchEditor().stop();
    }

    /*获取点坐标*/
    public void getMapPoint(MapView mapView){
        SketchEditor sketchEditor = mapView.getSketchEditor();
        sketchEditor.start(SketchCreationMode.POINT);
    }

    /*属性查询*/
    public void iSercher(MapView mapView){
        SketchEditor sketchEditor = mapView.getSketchEditor();
        sketchEditor.start(SketchCreationMode.POINT);
    }

    /*查询小班属性信息*/
    public void iquery(final MapView mapView, final Geometry geometry, final CalloutViewModel calloutViewModel){

        QueryParameters parameters = new QueryParameters();
        parameters.setGeometry(geometry);
        parameters.setReturnGeometry(true);
        parameters.setSpatialRelationship(QueryParameters.SpatialRelationship.INTERSECTS);

        LayerList list = mapView.getMap().getOperationalLayers();

        for(final Layer layer : list){
            final FeatureTable table = ((FeatureLayer)layer).getFeatureTable();
            final ListenableFuture<FeatureQueryResult> featureQueryResult = table.queryFeaturesAsync(parameters);
            featureQueryResult.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        FeatureQueryResult result = featureQueryResult.get();
                        Iterator<Feature> it = result.iterator();
                        Feature queryFeature;
                        while (it.hasNext()){
                            queryFeature = it.next();

                            calloutViewModel.showQueryInfo(mapView,queryFeature,geometry);
                        }

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /*添加基础图*/
    public void addStreetLayer(MapView mapView){
        Basemap basemap = Basemap.createOpenStreetMap();
        mapView.setMap(new ArcGISMap(basemap));
    }

    /*添加影像图*/
    public void addImageLayer(MapView mapView){
        Basemap basemap = Basemap.createImagery();
        mapView.setMap(new ArcGISMap(basemap));
    }

    /*添加水花影像图*/
    public void addShuiImageLayer(final MapView mapView, final String ytime) {

        final String time = ytime.replace("-", "");
        //final String time = "20171229";

        String url = mapView.getContext().getResources().getString(R.string.shui_imager);
        final ArcGISMapImageLayer imageLayer = new ArcGISMapImageLayer(url);
        imageLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (imageLayer.getLoadStatus() == LoadStatus.LOADED) {
                    ListenableList<ArcGISSublayer> sublayerList = imageLayer.getSublayers();

                    boolean pflag = false;
                    boolean cflag = false;
                    for (int i = 0; i < sublayerList.size(); i++) {
                        ArcGISSublayer mapImageSublayer = sublayerList.get(i);
                        String tablename = mapImageSublayer.getName();
                        if (time.contains(tablename)) {

                            pflag = true;
                            mapImageSublayer.setVisible(true);
                            ListenableList<ArcGISSublayer> sublayers = mapImageSublayer.getSublayers();
                            for (int m = 0; m < sublayers.size(); m++) {
                                ArcGISSublayer sublayer = sublayers.get(m);
                                String name = sublayer.getName();
                                if (time.contains(name)) {
                                    cflag = true;
                                    sublayer.setVisible(true);
                                    Envelope envelope = imageLayer.getFullExtent();
                                    mapView.setViewpointGeometryAsync(envelope,2000);

                                    ArcGISMapServiceSublayerInfo info = sublayer.getMapServiceSublayerInfo();
                                    if(info != null){
                                        Envelope envelope1 = info.getExtent();
                                        mapView.setViewpointGeometryAsync(envelope1);
                                    }

                                    break;
                                }
                            }
                        }
                    }

                    if(!pflag || !cflag){
                        ToastUtil.setToast(mapView.getContext(),"没有选择时段的影像" +time);
                    }
                }else{
                    ToastUtil.setToast(mapView.getContext(),"影像数据图层加载失败" +time);
                }
            }
        });
        imageLayer.loadAsync();
        ArcGISMap map = new ArcGISMap(Basemap.createOpenStreetMap());
        map.getOperationalLayers().add(imageLayer);
        mapView.setMap(map);
    }

    /*添加地基影像图*/
    public void addDjImageLayer(final MapView mapView, final String ytime) {

        final String time = ytime.replace("-", "");
        //final String time = "20171229";

        String url = mapView.getContext().getResources().getString(R.string.diji_imager);
        final ArcGISMapImageLayer imageLayer = new ArcGISMapImageLayer(url);
        imageLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (imageLayer.getLoadStatus() == LoadStatus.LOADED) {
                    ListenableList<ArcGISSublayer> sublayerList = imageLayer.getSublayers();

                    boolean pflag = false;
                    boolean cflag = false;
                    for (int i = 0; i < sublayerList.size(); i++) {
                        ArcGISSublayer mapImageSublayer = sublayerList.get(i);
                        String tablename = mapImageSublayer.getName();
                        if (time.contains(tablename)) {

                            pflag = true;
                            mapImageSublayer.setVisible(true);
                            ListenableList<ArcGISSublayer> sublayers = mapImageSublayer.getSublayers();
                            for (int m = 0; m < sublayers.size(); m++) {
                                ArcGISSublayer sublayer = sublayers.get(m);
                                String name = sublayer.getName();
                                if (time.contains(name)) {
                                    cflag = true;
                                    sublayer.setVisible(true);
                                    Envelope envelope = imageLayer.getFullExtent();
                                    mapView.setViewpointGeometryAsync(envelope,2000);

                                    ArcGISMapServiceSublayerInfo info = sublayer.getMapServiceSublayerInfo();
                                    if(info != null){
                                        Envelope envelope1 = info.getExtent();
                                        mapView.setViewpointGeometryAsync(envelope1);
                                    }

                                    break;
                                }
                            }
                        }
                    }

                    if(!pflag || !cflag){
                        ToastUtil.setToast(mapView.getContext(),"没有选择时段的影像" +time);
                    }
                }else{
                    ToastUtil.setToast(mapView.getContext(),"影像数据图层加载失败" +time);
                }
            }
        });
        imageLayer.loadAsync();
        ArcGISMap map = new ArcGISMap(Basemap.createOpenStreetMap());
        map.getOperationalLayers().add(imageLayer);
        mapView.setMap(map);
    }


    /*定位到当前位置*/
    public void zoomToMylocation(Context context,MapView mapView, Point point){
        if (point != null) {
            mapView.setViewpointCenterAsync(point, 5000);
        } else {
            ToastUtil.setToast(context, "未获取到当前位置");
        }
    }

    /*导航*/
    public void navigation(Context context, INaviInfoCallback infoCallback){
        AmapNaviPage.getInstance().showRouteActivity(context, new AmapNaviParams(null, null, null, AmapNaviType.DRIVER), infoCallback);
    }

}
