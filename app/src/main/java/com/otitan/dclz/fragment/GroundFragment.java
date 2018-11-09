package com.otitan.dclz.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.otitan.dclz.R;
import com.otitan.dclz.bean.ActionModel;
import com.otitan.dclz.bean.Monitor;
import com.otitan.dclz.common.CalloutViewModel;
import com.otitan.dclz.common.GeometryChangedListener;
import com.otitan.dclz.common.MyMapViewOnTouchListener;
import com.otitan.dclz.common.ToolViewModel;
import com.otitan.dclz.common.ValueCallback;
import com.otitan.dclz.net.RetrofitHelper;
import com.otitan.dclz.util.Constant;
import com.otitan.dclz.util.TimeUtil;
import com.titan.baselibrary.timepaker.TimePopupWindow;
import com.titan.baselibrary.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sp on 2018/9/25.
 * 地基遥感
 */
public class GroundFragment extends BaseFragment implements View.OnClickListener,ValueCallback{

    @BindView(R.id.mv_ground)
    MapView mapView;

    @BindView(R.id.rg_ground)
    RadioGroup mRg_satellite;
    @BindView(R.id.rb_first)
    RadioButton mRb_first;
    @BindView(R.id.rb_second)
    RadioButton mRb_second;
    @BindView(R.id.rb_third)
    RadioButton mRb_third;
    @BindView(R.id.iv_more)
    ImageView mIv_more;

    @BindView(R.id.ic_edit)
    View mIc_edit;

    @BindView(R.id.ll_tool)
    LinearLayout mLl_tool;
    @BindView(R.id.ll_layer)
    LinearLayout mLl_layer;

    @BindView(R.id.ic_tool)
    View mIc_tool;

    @BindView(R.id.iv_location)
    ImageView mIv_location;

    private TextView mTv_title;
    private ImageView mIv_close;
    private TextView mTv_edit;

    private LinearLayout mLl_coordinate;
    private LinearLayout mLl_navigation;
    private LinearLayout mLl_attribute;
    private LinearLayout mLl_distance;
    private LinearLayout mLl_area;
    private LinearLayout mLl_clean;

    private Context mContext;

    private Point currentPoint;
    private boolean isFirst = true;
    private int index = 0;
    private int iTool = 0;
    private ArrayList<Monitor> monitorList;

    private ActionModel actionModel;
    private ToolViewModel toolViewModel;
    private CalloutViewModel calloutViewModel;
    private MyMapViewOnTouchListener touchListener = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_ground, null);
        ButterKnife.bind(this, inflate);

        mContext = GroundFragment.this.getContext();

        initViewModel();

        initView();

        return inflate;
    }

    private void initView() {
        initMap();
        initLocation();

        // 获取遥感监测数据
        getRemoteData("", 0);

        // 顶部时间选择
        mRb_first.setOnClickListener(this);
        mRb_second.setOnClickListener(this);
        mRb_third.setOnClickListener(this);

        // 更多时间选择
        mIv_more.setOnClickListener(this);

        // 定位
        mIv_location.setOnClickListener(this);

        // 监测记录详情
        mTv_title = mIc_edit.findViewById(R.id.tv_title); // 标题
        mIv_close = mIc_edit.findViewById(R.id.iv_close); // 关闭按钮
        mTv_edit = mIc_edit.findViewById(R.id.tv_edit); // 记录内容
        mIv_close.setOnClickListener(this);

        // 工具
        mLl_tool.setOnClickListener(this);

        // 图层
        mLl_layer.setOnClickListener(this);

        // 工具详情
        mLl_coordinate = mIc_tool.findViewById(R.id.ll_coordinate); // 获取点坐标
        mLl_navigation = mIc_tool.findViewById(R.id.ll_navigation); // 导航
        mLl_attribute = mIc_tool.findViewById(R.id.ll_attribute); // 属性查询
        mLl_distance = mIc_tool.findViewById(R.id.ll_distance); // 测量距离
        mLl_area = mIc_tool.findViewById(R.id.ll_area); // 测量面积
        mLl_clean = mIc_tool.findViewById(R.id.ll_clean); // 清除标绘
        mLl_coordinate.setOnClickListener(this);
        mLl_navigation.setOnClickListener(this);
        mLl_attribute.setOnClickListener(this);
        mLl_distance.setOnClickListener(this);
        mLl_area.setOnClickListener(this);
        mLl_clean.setOnClickListener(this);
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        //ArcGISMap arcGISMap = new ArcGISMap(Basemap.createOpenStreetMap());
        //TileCache tileCache = new TileCache(getResources().getString(R.string.World_Imagery));
        //ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(tileCache);
        //arcGISMap.getBasemap().getBaseLayers().add(arcGISTiledLayer);
        //mapView.setMap(arcGISMap);

        toolViewModel.addStreetLayer(mapView);

        // 去除下方 powered by esri 按钮
        mapView.setAttributionTextVisible(false);

        SketchEditor sketchEditor = new SketchEditor();
        sketchEditor.addGeometryChangedListener(new GeometryChangedListener(this,mapView));
        mapView.setSketchEditor(sketchEditor);

    }


    /**/
    private void initViewModel(){
        toolViewModel = new ToolViewModel();
        calloutViewModel = new CalloutViewModel();
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        // 当前位置
        LocationDisplay mLocDisplay = mapView.getLocationDisplay();
        // 设置显示的位置
        mLocDisplay.setNavigationPointHeightFactor(0.5f);
        // 定位显示
        mLocDisplay.startAsync();
        // 设置位置变化监听
        mLocDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                // 当前坐标点
                currentPoint = locationChangedEvent.getLocation().getPosition();
                if (isFirst && currentPoint != null) {
                    toolViewModel.zoomToMylocation(mContext,mapView,currentPoint);
                    isFirst = false;
                }
            }
        });
    }

    /**
     * 监测记录
     */
    private void showEdit(int i) {
        String value = "";
        switch (i) {
            case 1:
                index = 1;
                value = mRb_first.getText().toString().trim() + " 监测记录";
                mTv_title.setText(value);
                mTv_edit.setText(monitorList.get(0).getJC_JGFX());

                break;

            case 2:
                index = 2;
                value = mRb_second.getText().toString().trim() + " 监测记录";
                mTv_title.setText(value);
                mTv_edit.setText(monitorList.get(1).getJC_JGFX());

                break;

            case 3:
                index = 3;
                value = mRb_third.getText().toString().trim() + " 监测记录";
                mTv_title.setText(value);
                mTv_edit.setText(monitorList.get(2).getJC_JGFX());

                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_first:
                mRb_first.setTextColor(getResources().getColor(R.color.colorPrimary));
                mRb_second.setTextColor(getResources().getColor(R.color.gray));
                mRb_third.setTextColor(getResources().getColor(R.color.gray));
                mIc_edit.setVisibility(View.VISIBLE);
                toolViewModel.addShuiImageLayer(mapView,mRb_first.getText().toString());
                showEdit(1);
                break;

            case R.id.rb_second:
                mRb_first.setTextColor(getResources().getColor(R.color.gray));
                mRb_second.setTextColor(getResources().getColor(R.color.colorPrimary));
                mRb_third.setTextColor(getResources().getColor(R.color.gray));
                mIc_edit.setVisibility(View.VISIBLE);
                toolViewModel.addShuiImageLayer(mapView,mRb_second.getText().toString());
                showEdit(2);
                break;

            case R.id.rb_third:
                mRb_first.setTextColor(getResources().getColor(R.color.gray));
                mRb_second.setTextColor(getResources().getColor(R.color.gray));
                mRb_third.setTextColor(getResources().getColor(R.color.colorPrimary));
                mIc_edit.setVisibility(View.VISIBLE);
                toolViewModel.addShuiImageLayer(mapView,mRb_third.getText().toString());
                showEdit(3);
                break;

            case R.id.iv_more:
                TimePopupWindow timePopupWindow = new TimePopupWindow(mContext, TimePopupWindow.Type.YEAR_MONTH_DAY);
                timePopupWindow.setCyclic(true);
                // 时间选择后回调
                timePopupWindow.setOnTimeSelectListener(new TimePopupWindow.OnTimeSelectListener() {

                    @Override
                    public void onTimeSelect(Date date) {
                        String choose = Constant.yearFormat.format(date);
                        String today = Constant.yearFormat.format(new Date(System.currentTimeMillis()));
                        long lChoose = TimeUtil.getLonfromYyr(choose);
                        long lToday = TimeUtil.getLonfromYyr(today);
                        if (lChoose <= lToday) {
                            mTv_title.setText(choose + " 监测记录");
                            // 根据选择的时间，查询监测信息
                            getRemoteData(choose, 1);
                            toolViewModel.addShuiImageLayer(mapView,choose);
                        } else {
                            ToastUtil.setToast(mContext, "选择日期错误，无法查询！");
                        }
                    }
                });
                timePopupWindow.showAtLocation(mTv_title, Gravity.CENTER, 0, 0, new Date());
                break;

            case R.id.iv_location:
                toolViewModel.zoomToMylocation(mContext,mapView,currentPoint);
                break;

            case R.id.iv_close:
                mIc_edit.setVisibility(View.INVISIBLE);
                switch (index) {
                    case 1:
                        mRb_first.setTextColor(getResources().getColor(R.color.gray));
                        break;
                    case 2:
                        mRb_second.setTextColor(getResources().getColor(R.color.gray));
                        break;
                    case 3:
                        mRb_third.setTextColor(getResources().getColor(R.color.gray));
                        break;
                }
                break;

            case R.id.ll_tool:
                if (iTool == 0) {
                    mIc_tool.setVisibility(View.VISIBLE);
                    iTool = 1;
                } else if (iTool == 1) {
                    mIc_tool.setVisibility(View.GONE);
                    iTool = 0;
                }
                break;

            case R.id.ll_coordinate: // 获取点坐标
                ToastUtil.setToast(mContext, "获取点坐标");
                actionModel = ActionModel.MapPOINT;
                toolViewModel.getMapPoint(mapView);
                break;

            case R.id.ll_navigation: // 导航
                ToastUtil.setToast(mContext, "导航");
                toolViewModel.navigation(mContext,this);
                break;

            case R.id.ll_attribute: // 属性查询
                ToastUtil.setToast(mContext, "属性查询");
                actionModel = ActionModel.IQUERY;
                toolViewModel.iSercher(mapView);
                break;

            case R.id.ll_distance: // 测量距离
                ToastUtil.setToast(mContext, "测量距离");
                actionModel = ActionModel.DISTANCE;
                toolViewModel.distance(mapView);
                break;

            case R.id.ll_area: // 测量面积
                ToastUtil.setToast(mContext, "测量面积");
                actionModel = ActionModel.AREA;
                toolViewModel.area(mapView);
                break;

            case R.id.ll_clean: // 清除标绘
                ToastUtil.setToast(mContext, "清除标绘");
                toolViewModel.clear(mapView);
                break;

            case R.id.ll_layer:
                toolViewModel.showLayer(mContext,mapView,currentPoint);
                break;
        }
    }

    /**
     * 获取遥感监测数据
     */
    private void getRemoteData(String time, final int i) {
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getMonitorData(time, 1);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                if (!s.equals("0")) {
                    switch (i) {
                        case 0:
                            monitorList = new Gson().fromJson(s, new TypeToken<ArrayList<Monitor>>() {}.getType());

                            mRb_first.setText(monitorList.get(0).getJC_DATE());
                            mRb_second.setText(monitorList.get(1).getJC_DATE());
                            mRb_third.setText(monitorList.get(2).getJC_DATE());
                            break;
                        case 1:
                            Monitor monitor = new Gson().fromJson(s.substring(1, s.length() - 1), Monitor.class);
                            mTv_edit.setText(monitor.getJC_JGFX());
                            mIc_edit.setVisibility(View.VISIBLE);
                            break;
                    }
                } else {
                    switch (i) {
                        case 0:
                            break;
                        case 1:
                            mTv_edit.setText("暂无监测数据");
                            mIc_edit.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onSuccess(Object t) {

    }

    @Override
    public void onFail(String value) {

    }

    @Override
    public void onGeometry(Geometry geometry) {
        if(actionModel == ActionModel.DISTANCE){
            calloutViewModel.showDistance(mapView,geometry);
        }

        if(actionModel == ActionModel.AREA){
            calloutViewModel.showDistance(mapView,geometry);
        }

        if(actionModel == ActionModel.MapPOINT){
            calloutViewModel.showPoint(mapView,geometry);
        }

        if(actionModel == ActionModel.IQUERY){
            toolViewModel.iquery(mapView,geometry,calloutViewModel);
        }
    }


    public void getHomeTime(String type,String time){
        toolViewModel.addShuiImageLayer(mapView,time);
    }

}
