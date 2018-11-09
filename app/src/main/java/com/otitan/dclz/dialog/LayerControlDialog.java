package com.otitan.dclz.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.OpenStreetMapLayer;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.otitan.dclz.R;
import com.otitan.dclz.adapter.LayerAdapter;
import com.otitan.dclz.bean.OtitanLayer;
import com.otitan.dclz.common.ToolViewModel;
import com.otitan.dclz.util.ResourcesManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by sp on 2018/10/19.
 * 图层控制
 */
public class LayerControlDialog extends Dialog {

    @BindView(R.id.rv_layer)
    RecyclerView mRv_layer;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.rb_base)
    RadioButton rbBase;
    @BindView(R.id.rb_image)
    RadioButton rbImage;

    private Context mContext;

    private MapView mapView;

    private List<OtitanLayer> layerList = new ArrayList<>();
    private LayerAdapter mLAdapter;

    private List<OtitanLayer> baseList = new ArrayList<>();
    private List<OtitanLayer> imageList = new ArrayList<>();
    private List<File> imgPathList = new ArrayList<>();

    private ToolViewModel toolViewModel;
    private Point currpoint;
    private boolean initFlag = true;


    public LayerControlDialog(@NonNull Context context, int themeResId, MapView mapView, ToolViewModel toolViewModel,Point point) {
        super(context, themeResId);
        this.mContext = context;
        this.mapView = mapView;
        this.toolViewModel = toolViewModel;
        this.currpoint = point;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layercontrol);

        ButterKnife.bind(this);

        mContext = LayerControlDialog.this.getContext();

        initView();
    }

    private void initView() {

        initFlag = true;
        LayerList list = mapView.getMap().getBasemap().getBaseLayers();
        for (Layer layer : list) {
            if (layer instanceof OpenStreetMapLayer) {
                rbBase.setChecked(true);
                rbImage.setChecked(false);
            } else {
                rbBase.setChecked(false);
                rbImage.setChecked(true);
            }
        }

        imgPathList = ResourcesManager.getInstance(mContext).getImgTitlePath();
        for (File file : imgPathList) {
            OtitanLayer layer = new OtitanLayer();
            layer.setName(file.getName());
            imageList.add(layer);
        }

        mRv_layer.setLayoutManager(new LinearLayoutManager(mContext));
        mLAdapter = new LayerAdapter(mContext, layerList);
        mRv_layer.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRv_layer.setAdapter(mLAdapter);
        mLAdapter.setItemClickListener(new LayerAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                addLayer(position);

                for (OtitanLayer layer : layerList) {
                    layer.setSelect(false);
                }
                layerList.get(position).setSelect(true);
                mLAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 添加图层
     */
    private void addLayer(int position) {
        ArcGISTiledLayer layer = new ArcGISTiledLayer(imgPathList.get(position).getAbsolutePath());
        mapView.getMap().getOperationalLayers().add(layer);
    }

    @OnClick({R.id.iv_close})
    void myClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
        }
    }

    // 写法1
    @OnCheckedChanged(R.id.rb_base)
    void radioButtonCheckChange(boolean isl) {
        if (isl) {
            //layerList.clear();
            //layerList.addAll(baseList);
            //mLAdapter.notifyDataSetChanged();
            toolViewModel.addStreetLayer(mapView);
            if(currpoint != null && !initFlag){
                toolViewModel.zoomToMylocation(mContext,mapView,currpoint);
            }
            initFlag = false;
        }
    }

    // 写法2
    @OnCheckedChanged(R.id.rb_image)
    void radioButtonCheckChange(RadioButton radioButton, boolean isl) {
        if (isl) {
//            layerList.clear();
//            layerList.addAll(imageList);
//            mLAdapter.notifyDataSetChanged();
            toolViewModel.addImageLayer(mapView);
            if(currpoint != null && !initFlag){
                toolViewModel.zoomToMylocation(mContext,mapView,currpoint);
            }
            initFlag = false;
        }
    }
}
