package com.otitan.dclz.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.otitan.dclz.R;
import com.otitan.dclz.activity.UserActivity;
import com.otitan.dclz.adapter.PatrolAdapter;
import com.otitan.dclz.bean.AlgalBlooms;
import com.otitan.dclz.bean.Buoy;
import com.otitan.dclz.bean.Monitor;
import com.otitan.dclz.bean.Patrol;
import com.otitan.dclz.net.RetrofitHelper;
import com.otitan.dclz.util.Constant;
import com.otitan.dclz.util.MobileUtil;
import com.squareup.picasso.Picasso;
import com.titan.baselibrary.util.MobileInfoUtil;
import com.titan.baselibrary.util.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sp on 2018/9/25.
 * 首页
 */
public class HomeFragment extends Fragment implements View.OnClickListener, OnBannerListener {

    @BindView(R.id.dl_home)
    DrawerLayout mDl_home;

    @BindView(R.id.iv_user)
    ImageView mIv_user;

    @BindView(R.id.bn_top)
    Banner mBn_top;

    @BindView(R.id.iv_satellite_detail)
    ImageView mIv_satellite_detail;
    @BindView(R.id.tv_satellite_time)
    TextView mTv_satellite_time;
    @BindView(R.id.tv_satellite_description)
    TextView mTv_satellite_description;

    @BindView(R.id.iv_ground_detail)
    ImageView mIv_ground_detail;
    @BindView(R.id.tv_ground_time)
    TextView mTv_ground_time;
    @BindView(R.id.tv_ground_description)
    TextView mTv_ground_description;

    @BindView(R.id.tv_buoy_time)
    TextView mTv_buoy_time;
    @BindView(R.id.tv_chlorophyll)
    TextView mTv_chlorophyll;
    @BindView(R.id.tv_oxygen)
    TextView mTv_oxygen;
    @BindView(R.id.tv_cyanobacteria)
    TextView mTv_cyanobacteria;
    @BindView(R.id.tv_temperature)
    TextView mTv_temperature;
    @BindView(R.id.tv_turbidity)
    TextView mTv_turbidity;
    @BindView(R.id.tv_humidity)
    TextView mTv_humidity;

    @BindView(R.id.tv_odds_first)
    TextView mTv_odds_first;
    @BindView(R.id.tv_odds_second)
    TextView mTv_odds_second;
    @BindView(R.id.tv_odds_third)
    TextView mTv_odds_third;

    @BindView(R.id.rv_patrol)
    RecyclerView mRv_patrol;

    @BindView(R.id.nv_home)
    NavigationView mNv_home;

    private Context mContext;

    private String mac;

    // banner 图片地址
    private List<String> pathList = new ArrayList<>();
    // banner 标题
    private List<String> titleList = new ArrayList<>();

    private OnHeadlineSelectedListener mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, inflate);

        mContext = HomeFragment.this.getContext();

        initView();

        return inflate;
    }

    private void initView() {
        // 设备号
        mac = MobileUtil.getInstance().getMacAdress(mContext);

        getData();
        getBanner();

        mIv_user.setOnClickListener(this);
        mIv_satellite_detail.setOnClickListener(this);
        mIv_ground_detail.setOnClickListener(this);

        // 开启手势滑动打开侧滑菜单栏，如果要关闭手势滑动，将后面的UNLOCKED替换成LOCKED_CLOSED 即可
        mDl_home.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mNv_home.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_version:
                        break;
                }
                //mDl_home.closeDrawer(mNv_home); // 关闭侧滑栏
                return true;
            }
        });

        // 静态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        mContext.registerReceiver(receiver, filter);
    }

    /**
     * 获取数据
     */
    private void getData() {
        // 遥感监测实况
        getRemoteData(0); // 0 卫星遥感
        getRemoteData(1); // 1 地基遥感

        // 当前时间
        String now = Constant.dateFormat.format(new Date(System.currentTimeMillis()));
        // 一小时前
        long current = System.currentTimeMillis();
        current -= 60 * 60 * 1000;
        String anHourAgo = Constant.dateFormat.format(new Date(current));

        mTv_buoy_time.setText(now);

        // 浮标站实时数据
        getBuoy(anHourAgo, now);

        // 未来水华发生概率
        getOdds("1");
        getOdds("2");
        getOdds("3");

        // 巡查实时事件
        getPatrol(mac, anHourAgo, now);
    }

    /**
     * 获取顶部轮播图
     */
    private void getBanner() {
        pathList.clear();
        titleList.clear();

        String pathTitle = mContext.getResources().getString(R.string.serverhost);

        pathList.add(pathTitle + "InformationSharing/images/picture/banner1.png");
        pathList.add(pathTitle + "InformationSharing/images/picture/banner2.png");
        pathList.add(pathTitle + "InformationSharing/images/picture/banner3.png");

        initBanner();
    }

    // 接收广播
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                // 刷新数据
                getData();
            }
        }
    };

    /**
     * 获取遥感监测数据
     */
    private void getRemoteData(final int i) {
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getMonitorData("", i);
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
                    ArrayList<Monitor> list = new Gson().fromJson(s, new TypeToken<ArrayList<Monitor>>() {
                    }.getType());
                    switch (i) {
                        case 0: // 卫星遥感
                            mTv_satellite_time.setText(list.get(0).getJC_DATE());
                            mTv_satellite_description.setText(list.get(0).getJC_JGFX());
                            break;

                        case 1: // 地基遥感
                            mTv_ground_time.setText(list.get(0).getJC_DATE());
                            mTv_ground_description.setText(list.get(0).getJC_JGFX());
                            break;
                    }
                } else {
                    switch (i) {
                        case 0: // 卫星遥感
                            mTv_satellite_time.setText("");
                            mTv_satellite_description.setText("暂无数据");
                            break;

                        case 1: // 地基遥感
                            mTv_ground_time.setText("");
                            mTv_ground_description.setText("暂无数据");
                            break;
                    }
                }
            }
        });
    }

    /**
     * 获取浮标站数据
     */
    private void getBuoy(String anHourAgo, String now) {
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getBuoyData(anHourAgo, now);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.setToast(mContext,e.getMessage());
            }

            @Override
            public void onNext(String s) {
                if (!s.equals("0")) {
                    ArrayList<Buoy> list = new Gson().fromJson(s, new TypeToken<ArrayList<Buoy>>() {
                    }.getType());
                    Buoy buoy = list.get(0);
                    mTv_chlorophyll.setText(Constant.strFormat(buoy.getYE_LV_SU()));
                    mTv_oxygen.setText(Constant.strFormat(buoy.getRJ_YANG()));
                    mTv_cyanobacteria.setText(Constant.strFormat(buoy.getLAN_ZAO()));
                    mTv_temperature.setText(Constant.strFormat(buoy.getSHUI_WEN()));
                    mTv_turbidity.setText(Constant.strFormat(buoy.getZHU_DU()));
                    mTv_humidity.setText(Constant.strFormat(buoy.getSHI_DU()));
                } else {
                    //么有数据
                }
            }
        });
    }

    /**
     * 未来水华发生概率
     */
    private void getOdds(final String i) {
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getAlgalBloomsOdds(i);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.setToast(mContext,""+e.getMessage());
            }

            @Override
            public void onNext(String s) {
                if(s.equals("0")){
                    switch (i) {
                        case "1":
                            mTv_odds_first.setText("");
                            break;

                        case "2":
                            mTv_odds_second.setText("");
                            break;

                        case "3":
                            mTv_odds_third.setText("");
                            break;
                    }
                    //没有数据
                    return;
                }
                if (!TextUtils.isEmpty(s)) {
                    ArrayList<AlgalBlooms> list = new Gson().fromJson(s, new TypeToken<ArrayList<AlgalBlooms>>() {
                    }.getType());
                    switch (i) {
                        case "1":
                            mTv_odds_first.setText(list.get(0).getGL());
                            break;

                        case "2":
                            mTv_odds_second.setText(list.get(0).getGL());
                            break;

                        case "3":
                            mTv_odds_third.setText(list.get(0).getGL());
                            break;
                    }
                } else {
                    switch (i) {
                        case "1":
                            mTv_odds_first.setText("");
                            break;

                        case "2":
                            mTv_odds_second.setText("");
                            break;

                        case "3":
                            mTv_odds_third.setText("");
                            break;
                    }
                }
            }
        });
    }

    /**
     * 获取巡护事件
     */
    private void getPatrol(String mac, String anHourAgo, String now) {
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getPatrolInfo(mac, anHourAgo, now);
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
                    ArrayList<Patrol> list = new Gson().fromJson(s, new TypeToken<ArrayList<Patrol>>() {
                    }.getType());
                    mRv_patrol.setLayoutManager(new LinearLayoutManager(mContext));
                    PatrolAdapter adapter = new PatrolAdapter(mContext, list);
                    mRv_patrol.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                    mRv_patrol.setAdapter(adapter);
                } else {

                }
            }
        });
    }

    /**
     * 顶部轮播图
     */
    private void initBanner() {

        //设置内置样式，共有六种可以点入方法内逐一体验使用。
//        mBn_top.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        mBn_top.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器，图片加载器在下方
        mBn_top.setImageLoader(new MyLoader());
        //设置图片网址或地址的集合
        mBn_top.setImages(pathList);
        //设置轮播的动画效果，内含多种特效，可点入方法内查找后内逐一体验
        mBn_top.setBannerAnimation(Transformer.Default);
        //设置轮播图的标题集合
//        mBn_top.setBannerTitles(titleList);
        //设置轮播间隔时间
        mBn_top.setDelayTime(3000);
        //设置是否为自动轮播，默认是“是”。
        mBn_top.isAutoPlay(true);
        //设置指示器的位置，小点点，左中右。
        mBn_top.setIndicatorGravity(BannerConfig.CENTER);
        //以上内容都可写成链式布局，这是轮播图的监听。比较重要。方法在下面。
        mBn_top.setOnBannerListener(this);
        //必须最后调用的方法，启动轮播图。
        mBn_top.start();
    }

    // 轮播图的监听方法
    @Override
    public void OnBannerClick(int position) {

    }

    // 自定义的图片加载器
    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Picasso.with(mContext).load((String) path).into(imageView);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_user:
                startActivity(new Intent(mContext, UserActivity.class));
                break;

            case R.id.iv_satellite_detail:
                mCallback.onArticleSelected("1",mTv_satellite_time.getText().toString());
                break;

            case R.id.iv_ground_detail:
                mCallback.onArticleSelected("2",mTv_ground_time.getText().toString());
                break;
        }
    }

    // fragment 的上一级 activtiy 实现这个接口
    public interface OnHeadlineSelectedListener {
        void onArticleSelected(String type,String time);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // 这是为了保证Activity容器实现了用以回调的接口。如果没有，它会抛出一个异常。
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}
