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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.otitan.dclz.MyMapViewOnTouchListener;
import com.otitan.dclz.R;
import com.otitan.dclz.bean.Monitor;
import com.otitan.dclz.net.RetrofitHelper;
import com.titan.baselibrary.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sp on 2018/9/25.
 * 卫星遥感
 */
public class SatelliteFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    @BindView(R.id.mv_satellite)
    MapView mMv_satellite;

    @BindView(R.id.rg_satellite)
    RadioGroup mRg_satellite;
    @BindView(R.id.rb_first)
    RadioButton mRb_first;
    @BindView(R.id.rb_second)
    RadioButton mRb_second;
    @BindView(R.id.rb_third)
    RadioButton mRb_third;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_statellite, null);
        ButterKnife.bind(this, inflate);

        mContext = SatelliteFragment.this.getContext();

        initView();

        return inflate;
    }

    private void initView() {
        initMap();
        initLocation();

        // 获取遥感监测数据
        getRemoteData();

        /*Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        // 今天
        mRb_first.setText(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));
        // 昨天
        c.set(Calendar.DATE, date - 1);
        mRb_second.setText(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));
        // 前天
        c.set(Calendar.DATE, date - 2);
        mRb_third.setText(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));*/

        // 顶部时间选择
        mRg_satellite.setOnCheckedChangeListener(this);

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
        ArcGISMap arcGISMap = new ArcGISMap(Basemap.createImagery());
        TileCache tileCache = new TileCache(getResources().getString(R.string.World_Imagery));
        ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(tileCache);
        arcGISMap.getBasemap().getBaseLayers().add(arcGISTiledLayer);
        mMv_satellite.setMap(arcGISMap);

        // 去除下方 powered by esri 按钮
        mMv_satellite.setAttributionTextVisible(false);
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        // 当前位置
        LocationDisplay mLocDisplay = mMv_satellite.getLocationDisplay();
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
            mMv_satellite.setViewpointCenterAsync(currentPoint, 5000);
        } else {
            ToastUtil.setToast(mContext, "未获取到当前位置");
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_first:
                mIc_edit.setVisibility(View.VISIBLE);
                showEdit(1);
                break;
            case R.id.rb_second:
                mIc_edit.setVisibility(View.VISIBLE);
                showEdit(2);
                break;
            case R.id.rb_third:
                mIc_edit.setVisibility(View.VISIBLE);
                showEdit(3);
                break;
        }
    }

    /**
     * 监测记录
     */
    private void showEdit(int i) {
        switch (i) {
            case 1:
                index = 1;
                mTv_title.setText(mRb_first.getText().toString().trim() + " 监测记录");
                mTv_edit.setText(monitorList.get(0).getJC_JGFX());
                break;

            case 2:
                index = 2;
                mTv_title.setText(mRb_second.getText().toString().trim() + " 监测记录");
                mTv_edit.setText(monitorList.get(1).getJC_JGFX());
                break;

            case 3:
                index = 3;
                mTv_title.setText(mRb_third.getText().toString().trim() + " 监测记录");
                mTv_edit.setText(monitorList.get(2).getJC_JGFX());
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_location:
                myLocation();
                break;

            case R.id.iv_close:
                mIc_edit.setVisibility(View.INVISIBLE);
                switch (index) {
                    case 1:
                        mRb_first.setChecked(false);
                        break;

                    case 2:
                        mRb_second.setChecked(false);
                        break;

                    case 3:
                        mRb_third.setChecked(false);
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
//                mMv_satellite.setOnTouchListener(new MyMapViewOnTouchListener(mContext, mMv_satellite));
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

            case R.id.ll_layer:
                ToastUtil.setToast(mContext, "图层");
                break;
        }
    }

    /**
     * 获取遥感监测数据
     */
    private void getRemoteData() {
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getMonitorData("", 0);
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
                    monitorList = new Gson().fromJson(s, new TypeToken<ArrayList<Monitor>>() {}.getType());

                    mRb_first.setText(monitorList.get(0).getJC_DATE());
                    mRb_second.setText(monitorList.get(1).getJC_DATE());
                    mRb_third.setText(monitorList.get(2).getJC_DATE());
                } else {

                }
            }
        });
    }

    /*private inner class MapViewOnTouchListener(mContext: Context, mMapView: MapView) : DefaultMapViewOnTouchListener(mContext, mMapView) {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            try {
                clearSelect()
                val screenPoint = e?.let { android.graphics.Point(it.x.toInt(), it.y.toInt()) }
                        ?: return super.onSingleTapConfirmed(e)
                val identifyGraphic:ListenableFuture<IdentifyGraphicsOverlayResult>?
                identifyGraphic = if (viewModel!!.editLayer!!.name!!.contains("苗圃")) {
                    mMapView.identifyGraphicsOverlayAsync(mNurseryOverlay, screenPoint, 10.0, false, 1)
                } else {
                    mMapView.identifyGraphicsOverlayAsync(mLandOverlay, screenPoint, 10.0, false, 1)
                }
                identifyGraphic.addDoneListener {
                    val result = identifyGraphic.get()
                    if (identifyGraphic.isDone) {
                        /owCallout(result.graphics[0])
                        if (result.graphics.isNotEmpty()) {
                            mSelectGraphic = result.graphics[0]
                            viewModel?.mSelectGraphic = mSelectGraphic
                            result.graphics[0].isSelected = true
                            //mGraphicsOverlay.graphics.get
                        } else {
                            mSelectGraphic = null
                            viewModel?.mSelectGraphic = null
                        }
                    } else {
                        viewModel?.snackbarText?.set("选择数据失败" + result.error)
                    }
                }
            } catch (e: Exception) {
                showToast(0, "地图点击事件错误：$e")
            }

            return super.onSingleTapConfirmed(e)
        }

        override fun onRotate(event: MotionEvent?, rotationAngle: Double): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            super.onScaleEnd(detector)
            Log.e("tag", "比例尺：${binding?.mapview?.mapScale}")
        }
    }
*/
}
