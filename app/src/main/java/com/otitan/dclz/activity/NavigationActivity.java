package com.otitan.dclz.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.otitan.dclz.R;
import com.otitan.dclz.adapter.NavigationAdapter;
import com.otitan.dclz.util.BaiduNavigation;
import com.titan.baselibrary.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 导航
 */
public class NavigationActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView mIv_back;
    @BindView(R.id.et_start)
    EditText mEt_start;
    @BindView(R.id.et_end)
    EditText mEt_end;
    @BindView(R.id.rv_navigation)
    RecyclerView mRv_navigation;
    @BindView(R.id.mv_navigation)
    MapView mMv_navigation;
    @BindView(R.id.iv_location)
    ImageView mIv_location;
    @BindView(R.id.tv_navigation)
    TextView mTv_navigation;

    private BaiduMap baiduMap;
    private double mLongitude; // 经度
    private double mLatitude; // 纬度
    private String mCity; // 城市

    private List<String> suggestionList = new ArrayList<>();
    private NavigationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_navigation);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        baiduMap = mMv_navigation.getMap();

        initLocation();

        mEt_start.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (suggestionList.size() > 0) {
                        suggestionList.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                    getSuggestion(mEt_start);

                    mEt_start.setFocusable(true);
                    mEt_start.setFocusableInTouchMode(true);
                    mEt_start.requestFocus();
                    mEt_start.findFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEt_start, InputMethodManager.SHOW_FORCED); // 显示软键盘
                }
            }
        });
        mEt_end.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (suggestionList.size() > 0) {
                        suggestionList.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                    getSuggestion(mEt_end);

                    mEt_end.setFocusable(true);
                    mEt_end.setFocusableInTouchMode(true);
                    mEt_end.requestFocus();
                    mEt_end.findFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEt_end, InputMethodManager.SHOW_FORCED); // 显示软键盘
                }
            }
        });

        mRv_navigation.setLayoutManager(new LinearLayoutManager(NavigationActivity.this));
        mAdapter = new NavigationAdapter(NavigationActivity.this, suggestionList);
        mRv_navigation.addItemDecoration(new DividerItemDecoration(NavigationActivity.this, DividerItemDecoration.VERTICAL));
        mRv_navigation.setAdapter(mAdapter);
    }

    @OnClick({R.id.iv_back, R.id.iv_location, R.id.tv_navigation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_location:
                showLocation(mLatitude, mLongitude);
                break;

            case R.id.tv_navigation:
                // 金潜广场 31.799035 / 117.240661
                // 合肥南站 31.805594 / 117.296652
                LatLng[] latLngs = new LatLng[]{
                        new LatLng(31.799035, 117.240661),
                        new LatLng(31.805594, 117.296652)};
                BaiduNavigation.getInstance(this).initNavigator(this, "开始", "结束", latLngs);
                ToastUtil.setToast(this, "导航");
                break;
        }
    }

    /**
     * 获取当前位置
     */
    private void initLocation() {
        LocationClient locationClient = new LocationClient(this);
        LocationClientOption clientOption = new LocationClientOption();
        clientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 设置定位模式（高精度）
        clientOption.openGps = true; // 打开GPS
        clientOption.setCoorType("bd09ll"); // 设置坐标系 bd09ll gcj02
        //clientOption.setScanSpan(1000); // 定位回掉时间(毫秒)
        clientOption.setIsNeedAddress(true); // 获取具体位置
        locationClient.setLocOption(clientOption);
        // 注册位置监听器
        locationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation != null) {
                    mLongitude = bdLocation.getLongitude(); // 经度
                    mLatitude = bdLocation.getLatitude(); // 纬度

                    showLocation(mLatitude, mLongitude);

                    // 城市
                    mCity = bdLocation.getCity();
                }
            }
        });
        locationClient.start();
    }

    /**
     * 显示当前位置
     */
    private void showLocation(double latitude, double longitude) {
        // 设定中心点坐标
        LatLng cenpt = new LatLng(latitude, longitude);
        // 定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(18).build();

        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        // 构造定位数据
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.accuracy(0); // 精度
        builder.direction(100); // 方向传感器
        builder.latitude(latitude);
        builder.longitude(longitude);
        MyLocationData data = builder.build();
        baiduMap.setMyLocationData(data);

        // 图标样式
        /*baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null,
                0xAAFFFF88, 0xAA00FF00));*/

        // 当不需要定位图层时关闭定位图层
        //baiduMap.setMyLocationEnabled(false);

        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        // 改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);
    }

    /**
     * 搜索起止位置
     */
    private void getSuggestion(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() { // 添加文本变化监听
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                if (suggestionList.size() > 0) {
                    suggestionList.clear();
                    mAdapter.notifyDataSetChanged();
                }
                mAdapter.notifyDataSetChanged();
                mRv_navigation.setVisibility(View.VISIBLE);
                // 创建在线建议查询实例
                final SuggestionSearch suggestionSearch = SuggestionSearch.newInstance();

                // 设置在线建议查询监听者
                suggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() { // 创建在线建议查询监听者
                    public void onGetSuggestionResult(SuggestionResult res) {

                        if (res == null || res.getAllSuggestions() == null) {
                            // 未找到相关结果
                            return;
                        }

                        // 获取在线建议检索结果
                        suggestionList.clear();
                        List<SuggestionResult.SuggestionInfo> allSuggestions = res.getAllSuggestions();
                        for (SuggestionResult.SuggestionInfo suggestion : allSuggestions) {
                            suggestionList.add(suggestion.key);
                        }

                        mAdapter.notifyDataSetChanged();

                        mAdapter.setItemClickListener(new NavigationAdapter.MyItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                editText.setText(suggestionList.get(position));
                                mRv_navigation.setVisibility(View.GONE);

                                suggestionList.clear();
                                mAdapter.notifyDataSetChanged();

                                editText.setFocusable(true);
                                editText.setFocusableInTouchMode(true);
                            }
                        });
                    }
                });

                // 发起在线建议查询；
                // 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新

                suggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(editable.toString().trim())
                        .city(mCity));
                // 释放在线建议查询实例；
                suggestionSearch.destroy();
            }
        });
    }
}
