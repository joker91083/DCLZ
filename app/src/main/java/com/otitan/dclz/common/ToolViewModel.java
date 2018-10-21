package com.otitan.dclz.common;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.util.ListenableList;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ToolViewModel {



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

}
