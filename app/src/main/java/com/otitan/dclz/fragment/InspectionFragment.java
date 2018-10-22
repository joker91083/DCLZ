package com.otitan.dclz.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.otitan.dclz.LayerControlDialog;
import com.otitan.dclz.R;
import com.otitan.dclz.activity.MonitorDetailActivity;
import com.otitan.dclz.activity.NavigationActivity;
import com.otitan.dclz.bean.ActionModel;
import com.otitan.dclz.bean.Trajectory;
import com.otitan.dclz.common.CalloutViewModel;
import com.otitan.dclz.common.GeometryChangedListener;
import com.otitan.dclz.common.ITrajectoryModel;
import com.otitan.dclz.common.MyMapViewOnTouchListener;
import com.otitan.dclz.common.ToolViewModel;
import com.otitan.dclz.common.TrajectoryModel;
import com.otitan.dclz.common.ValueCallback;
import com.otitan.dclz.net.RetrofitHelper;
import com.otitan.dclz.util.Constant;
import com.otitan.dclz.util.MobileUtil;
import com.titan.baselibrary.util.ConverterUtils;
import com.titan.baselibrary.util.ToastUtil;

import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sp on 2018/9/25.
 * 移动巡检
 */
public class InspectionFragment extends Fragment implements View.OnClickListener, ValueCallback {

    @BindView(R.id.mv_inspection)
    MapView mapview;

    @BindView(R.id.ll_tool)
    LinearLayout mLl_tool;
    @BindView(R.id.ll_layer)
    LinearLayout mLl_layer;
    @BindView(R.id.ll_locus)
    LinearLayout mLl_locus;
    @BindView(R.id.ll_report)
    LinearLayout mLl_report;
    @BindView(R.id.ll_inquire)
    LinearLayout mLl_inquire;

    @BindView(R.id.ic_tool)
    View mIc_tool;

    @BindView(R.id.iv_location)
    ImageView mIv_location;

    @BindView(R.id.tv_start)
    TextView mTv_start;
    @BindView(R.id.ll_coordinate)
    LinearLayout llCoordinate;
    @BindView(R.id.ll_navigation)
    LinearLayout llNavigation;
    @BindView(R.id.tv_suspend)
    TextView tvSuspend;
    @BindView(R.id.tv_restart)
    TextView tvRestart;
    @BindView(R.id.tv_end)
    TextView tvEnd;

    private LinearLayout mLl_coordinate;
    private LinearLayout mLl_navigation;
    private LinearLayout mLl_attribute;
    private LinearLayout mLl_distance;
    private LinearLayout mLl_area;
    private LinearLayout mLl_clean;

    private Context mContext;

    private Point gpspoint;
    private Point mappoint;
    private Unbinder unbinder;

    private HomeFragment.OnHeadlineSelectedListener mCallback;

    private Point currentPoint;
    private boolean isFirst = true;
    private int iTool = 0;
    private String address = "";

    private ActionModel actionModel;
    private ToolViewModel toolViewModel;
    private CalloutViewModel calloutViewModel;
    private MyMapViewOnTouchListener touchListener = null;

    /*记录轨迹*/
    private Polyline polyline = null;
    private PointCollection collection = null;
    private GraphicsOverlay overlay = null;
    private Graphic lineGraphic = null;
    private SimpleLineSymbol lineSymbol = null;
    private boolean isStart = false;
    private ITrajectoryModel trajectoryModel = null;
    private Trajectory trajectory = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_inspection, null);
//        ButterKnife.bind(this, inflate);
        unbinder = ButterKnife.bind(this, inflate);

        mContext = InspectionFragment.this.getContext();

        initView();

        initViewModel();

        return inflate;
    }

    private void initView() {
        initMap();
        initLocation();
        bdLocation();

        // 定位
        mIv_location.setOnClickListener(this);

        // 工具
        mLl_tool.setOnClickListener(this);
        // 图层控制
        mLl_layer.setOnClickListener(this);
        // 轨迹查询
        mLl_locus.setOnClickListener(this);
        // 事件上报
        mLl_report.setOnClickListener(this);
        // 事件查询
        mLl_inquire.setOnClickListener(this);

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

        // 开始记录
        mTv_start.setOnClickListener(this);
    }

    private void initViewModel() {
        toolViewModel = new ToolViewModel();
        calloutViewModel = new CalloutViewModel();

        trajectoryModel = new TrajectoryModel();
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        ArcGISMap arcGISMap = new ArcGISMap(Basemap.createImagery());
        TileCache tileCache = new TileCache(getResources().getString(R.string.World_Imagery));
        ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(tileCache);
        arcGISMap.getBasemap().getBaseLayers().add(arcGISTiledLayer);
        mapview.setMap(arcGISMap);

        // 去除下方 powered by esri 按钮
        mapview.setAttributionTextVisible(false);


        SketchEditor sketchEditor = new SketchEditor();
        sketchEditor.addGeometryChangedListener(new GeometryChangedListener(this, mapview));
        mapview.setSketchEditor(sketchEditor);

        View.OnTouchListener lo = mapview.getOnTouchListener();
        if (null != lo) {
            touchListener = new MyMapViewOnTouchListener(mapview.getContext(), mapview, lo, this);
            mapview.setOnTouchListener(touchListener);
        }

    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        // 当前位置
        final LocationDisplay mLocDisplay = mapview.getLocationDisplay();
        // 设置显示的位置
        mLocDisplay.setNavigationPointHeightFactor(0.5f);
        // 定位显示
        mLocDisplay.startAsync();
        // 设置位置变化监听
        mLocDisplay.addLocationChangedListener(new MyLocationChangedListener(mLocDisplay) {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                // 当前坐标点
                gpspoint = locationChangedEvent.getLocation().getPosition();
                mappoint = mLocDisplay.getMapLocation();

                if (isFirst && mappoint != null) {
                    myLocation();
                    isFirst = false;
                }


                record();
            }
        });
    }

    /**
     * 百度定位
     */
    private void bdLocation() {
        LocationClient locationClient = new LocationClient(mContext);
        LocationClientOption clientOption = new LocationClientOption();
        clientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 设置定位模式（高精度）
        clientOption.openGps = true; // 打开GPS
        clientOption.setCoorType("bd09ll"); // 设置坐标系 bd09ll gcj02
        clientOption.setScanSpan(5000); // 定位回掉时间(毫秒)
        clientOption.setIsNeedAddress(true); // 获取具体位置
        locationClient.setLocOption(clientOption);
        // 注册位置监听器
        locationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation != null) {
                    double longitude = bdLocation.getLongitude(); // 经度
                    double latitude = bdLocation.getLatitude(); // 纬度

                    // 当前地址
//                    myAddress = bdLocation.getAddrStr();
                }
            }
        });
        locationClient.start();
    }

    /**
     * 当前位置
     */
    private void myLocation() {
        if (mappoint != null) {
            mapview.setViewpointCenterAsync(mappoint, 5000);
        } else {
            ToastUtil.setToast(mContext, "未获取到当前位置");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_location: // 定位
                myLocation();
                break;

            case R.id.ll_tool: // 工具
                if (iTool == 0) {
                    mIc_tool.setVisibility(View.VISIBLE);
                    iTool = 1;
                } else if (iTool == 1) {
                    mIc_tool.setVisibility(View.GONE);
                    iTool = 0;
                }
                break;

            case R.id.ll_layer: // 图层控制
                LayerControlDialog lcDialog = new LayerControlDialog(mContext, R.style.style_Dialog, mapview);
                lcDialog.show();
                break;

            case R.id.ll_locus: // 轨迹查询
                ToastUtil.setToast(mContext, "轨迹查询");
                break;

            case R.id.ll_report: // 事件上报
                Intent intent = new Intent(mContext, MonitorDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("X", ConverterUtils.toString(gpspoint.getX()));
                bundle.putString("Y", ConverterUtils.toString(gpspoint.getY()));
                bundle.putString("address", address);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.ll_inquire: // 事件查询
                ToastUtil.setToast(mContext, "事件查询");
                break;

            case R.id.ll_coordinate: // 获取点坐标
                ToastUtil.setToast(mContext, "获取点坐标");
                actionModel = ActionModel.MapPOINT;
                toolViewModel.getMapPoint(mapview);
                break;

            case R.id.ll_navigation: // 导航
                startActivity(new Intent(mContext, NavigationActivity.class));
                break;

            case R.id.ll_attribute: // 属性查询
                ToastUtil.setToast(mContext, "属性查询");
                break;

            case R.id.ll_distance: // 测量距离
                ToastUtil.setToast(mContext, "测量距离");
                actionModel = ActionModel.DISTANCE;
                toolViewModel.distance(mapview);
                break;

            case R.id.ll_area: // 测量面积
                ToastUtil.setToast(mContext, "测量面积");
                actionModel = ActionModel.AREA;
                toolViewModel.area(mapview);
                break;

            case R.id.ll_clean: // 清除标绘
                ToastUtil.setToast(mContext, "清除标绘");
                toolViewModel.clear(mapview);
                break;

            case R.id.tv_start: // 开始记录
                ToastUtil.setToast(mContext, "开始记录轨迹");
                start();
                break;
        }
    }

    @OnClick({R.id.tv_suspend, R.id.tv_restart, R.id.tv_end})
    public void onClicks(View view) {
        switch (view.getId()) {
            case R.id.tv_suspend:
                suspend();
                break;
            case R.id.tv_restart:
                restart();
                break;
            case R.id.tv_end:
                end();
                break;
            default:
                break;
        }
    }

    /*开始记录*/
    private void start() {
        isStart = true;
        actionModel = ActionModel.RECODE;

        mTv_start.setVisibility(View.INVISIBLE);
        tvSuspend.setVisibility(View.VISIBLE);
        tvEnd.setVisibility(View.VISIBLE);

        if (null == polyline) {
            collection = new PointCollection(mapview.getSpatialReference());
            polyline = new Polyline(collection);
            overlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
            mapview.getGraphicsOverlays().add(overlay);
            lineSymbol = new SimpleLineSymbol();
            lineSymbol.setStyle(SimpleLineSymbol.Style.SOLID);
            lineSymbol.setWidth(3.0f);
            lineSymbol.setColor(Color.RED);
        }

        trajectory = new Trajectory();
        String id = UUID.randomUUID().toString();
        trajectory.setXC_ID(id);
        String macAddress = MobileUtil.getInstance().getMacAdress(mContext);
        trajectory.setXC_SBH(macAddress);
        String startime = Constant.dateFormat.format(new Date());
        trajectory.setXC_STARTTIME(startime);
        trajectory.setXC_METHOD("0");
        trajectory.setXC_XLLC("");
        trajectory.setXC_NAME("");
        trajectory.setXC_ENDTIME("");
        trajectory.setREMARK("");

        trajectoryModel.sendTrajectory(mContext, trajectory, this);

    }

    /*暂停*/
    private void suspend() {
        isStart = false;
        tvRestart.setVisibility(View.VISIBLE);
        tvSuspend.setVisibility(View.GONE);
    }

    /*继续*/
    private void restart() {
        isStart = true;
        tvSuspend.setVisibility(View.VISIBLE);
        tvRestart.setVisibility(View.GONE);
    }

    /*结束*/
    private void end() {
        isStart = false;

        tvSuspend.setVisibility(View.GONE);
        tvEnd.setVisibility(View.GONE);
        tvRestart.setVisibility(View.GONE);
        mTv_start.setVisibility(View.VISIBLE);


        double leng = Math.abs(GeometryEngine.length(polyline));
        if (leng <= 1) {
            ToastUtil.setToast(mContext, "你记录的距离太短,不保存本次记录");
            return;
        }

        trajectory.setXC_METHOD("0");
        String ll = Constant.disFormat.format(leng);
        trajectory.setXC_XLLC(ll);
        String name = trajectory.getXC_STARTTIME();
        trajectory.setXC_NAME(name);
        String endtime = Constant.dateFormat.format(new Date());
        trajectory.setXC_ENDTIME(endtime);
        trajectory.setREMARK("");

        trajectoryModel.sendTrajectory(mContext, trajectory, this);

    }


    @Override
    public void onSuccess(Object t) {
        if (actionModel == ActionModel.RECODE) {
            trajectory = (Trajectory) t;
        }
    }

    @Override
    public void onFail(String value) {

    }

    @Override
    public void onGeometry(Geometry geometry) {
        if (actionModel == ActionModel.DISTANCE) {
            calloutViewModel.showDistance(mapview, geometry);
        }

        if (actionModel == ActionModel.AREA) {
            calloutViewModel.showDistance(mapview, geometry);
        }

        if (actionModel == ActionModel.MapPOINT) {
            calloutViewModel.showPoint(mapview, geometry);
        }

        if (actionModel == ActionModel.IQUERY) {
            toolViewModel.iquery(mapview, geometry, calloutViewModel);
        }
    }

    abstract class MyLocationChangedListener implements LocationDisplay.LocationChangedListener {
        LocationDisplay mLocDisplay;

        public MyLocationChangedListener(LocationDisplay locationDisplay) {
            this.mLocDisplay = locationDisplay;
        }
    }

    // fragment 的上一级 activtiy 实现这个接口
    public interface OnHeadlineSelectedListener {
        void onArticleSelected(String s);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // 这是为了保证Activity容器实现了用以回调的接口。如果没有，它会抛出一个异常。
        try {
//            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    /*@Override
    public void onLocationChanged(LocationDisplay.LocationChangedEvent event) {

        // 当前坐标点
        gpspoint = event.getLocation().getPosition();
        mappoint = mLocDisplay.getMapLocation();

        if (isFirst && mappoint != null) {
            myLocation();
            isFirst = false;
        }


        record();

    }*/


    /*记录轨迹*/
    private void record() {
        if (!isStart) {
            return;
        }

        overlay.getGraphics().remove(lineGraphic);
        collection.add(mappoint);
        polyline = new Polyline(collection);
        lineGraphic = new Graphic(polyline, lineSymbol);
        overlay.getGraphics().add(lineGraphic);

        boolean netState = RetrofitHelper.getInstance(mContext).networkMonitor.isConnected();
        if(netState){
            uPLonLat();
        }else{
            ToastUtil.setToast(mContext,"网络未连接");
        }

    }

    /*上传轨迹点*/
    private void uPLonLat() {
        String SBH = MobileUtil.getInstance().getMacAdress(mContext);
        String lon = ConverterUtils.toString(gpspoint.getX());
        String lat = ConverterUtils.toString(gpspoint.getY());
        String time = Constant.dateFormat.format(new Date());

        Observable<String> oberver = RetrofitHelper.getInstance(mContext).getServer().uPLonLat(SBH, lon, lat, time);
        oberver.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        ToastUtil.setToast(mContext, "轨迹点上传异常" + throwable.getMessage());
                        uPLonLat();
                    }

                    @Override
                    public void onNext(String s) {
                        if (s.equals("0")) {
                            //成功
                        } else if (s.equals("1")) {
                            //失败
                        } else if (s.equals("2")) {
                            //设备未注册
                        }
                    }
                });
    }
}
