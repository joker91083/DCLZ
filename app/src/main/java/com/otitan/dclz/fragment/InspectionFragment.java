package com.otitan.dclz.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.otitan.dclz.R;
import com.titan.baselibrary.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sp on 2018/9/25.
 * 移动巡检
 */
public class InspectionFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.mv_inspection)
    MapView mMv_inspection;

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

    private LinearLayout mLl_coordinate;
    private LinearLayout mLl_navigation;
    private LinearLayout mLl_attribute;
    private LinearLayout mLl_distance;
    private LinearLayout mLl_area;
    private LinearLayout mLl_clean;

    private Context mContext;

    private Point currentPoint;
    private boolean isFirst = true;
    private int iTool = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_inspection, null);
        ButterKnife.bind(this, inflate);

        mContext = InspectionFragment.this.getContext();

        initView();

        return inflate;
    }

    private void initView() {
        initMap();
        initLocation();

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

    /**
     * 初始化地图
     */
    private void initMap() {
        ArcGISMap arcGISMap = new ArcGISMap(Basemap.createImagery());
        TileCache tileCache = new TileCache(getResources().getString(R.string.World_Imagery));
        ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(tileCache);
        arcGISMap.getBasemap().getBaseLayers().add(arcGISTiledLayer);
        mMv_inspection.setMap(arcGISMap);

        // 去除下方 powered by esri 按钮
        mMv_inspection.setAttributionTextVisible(false);
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        // 当前位置
        LocationDisplay mLocDisplay = mMv_inspection.getLocationDisplay();
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
                    myLocation();
                    isFirst = false;
                }
            }
        });
    }

    /**
     * 当前位置
     */
    private void myLocation() {
        if (currentPoint != null) {
            mMv_inspection.setViewpointCenterAsync(currentPoint, 5000);
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
                ToastUtil.setToast(mContext, "图层控制");
                break;

            case R.id.ll_locus: // 轨迹查询
                ToastUtil.setToast(mContext, "轨迹查询");
                break;

            case R.id.ll_report: // 事件上报
                ToastUtil.setToast(mContext, "事件上报");
                break;

            case R.id.ll_inquire: // 事件查询
                ToastUtil.setToast(mContext, "事件查询");
                break;

            case R.id.ll_coordinate: // 获取点坐标
                ToastUtil.setToast(mContext, "获取点坐标");
                break;

            case R.id.ll_navigation: // 导航
                ToastUtil.setToast(mContext, "导航");
                break;

            case R.id.ll_attribute: // 属性查询
                ToastUtil.setToast(mContext, "属性查询");
                break;

            case R.id.ll_distance: // 测量距离
                ToastUtil.setToast(mContext, "测量距离");
                break;

            case R.id.ll_area: // 测量面积
                ToastUtil.setToast(mContext, "测量面积");
                break;

            case R.id.ll_clean: // 清除标绘
                ToastUtil.setToast(mContext, "清除标绘");
                break;

            case R.id.tv_start: // 开始记录
                ToastUtil.setToast(mContext, "开始记录");
                break;
        }
    }
}
