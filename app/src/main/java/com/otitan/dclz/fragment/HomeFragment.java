package com.otitan.dclz.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.otitan.dclz.R;
import com.otitan.dclz.adapter.PatrolAdapter;
import com.otitan.dclz.bean.AlgalBlooms;
import com.otitan.dclz.bean.Buoy;
import com.otitan.dclz.bean.Monitor;
import com.otitan.dclz.bean.Patrol;
import com.otitan.dclz.net.RetrofitHelper;
import com.titan.baselibrary.util.MobileInfoUtil;
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
 * 首页
 */
public class HomeFragment extends Fragment {

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

    private Context mContext;

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
        // 遥感监测实况
        getRemoteData(0); // 0 卫星遥感
        getRemoteData(1); // 1 地基遥感

        // 当前时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        // 一小时前
        long current = System.currentTimeMillis();
        current -= 60 * 60 * 1000;
        String anHourAgo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));

        mTv_buoy_time.setText(now);

        // 浮标站实时数据
        getBuoy(anHourAgo, now);

        // 未来水华发生概率
        getOdds("1");
        getOdds("2");
        getOdds("3");

        // 设备号
        String mac = MobileInfoUtil.getMAC(mContext);

        // 巡查实时事件
        getPatrol(mac, anHourAgo, now);
    }

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
                    ArrayList<Monitor> list = new Gson().fromJson(s, new TypeToken<ArrayList<Monitor>>() {}.getType());
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
        // TODO: 2018/9/28
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getBuoyData("2018-6-9 7:40:00", "2018-6-9 8:40:00");
//        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getBuoyData(anHourAgo, now);
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
                    ArrayList<Buoy> list = new Gson().fromJson(s, new TypeToken<ArrayList<Buoy>>() {}.getType());
                    Buoy buoy = list.get(0);
                    mTv_chlorophyll.setText(buoy.getYE_LV_SU());
                    mTv_oxygen.setText(buoy.getRJ_YANG());
                    mTv_cyanobacteria.setText(buoy.getLAN_ZAO());
                    mTv_temperature.setText(buoy.getSHUI_WEN());
                    mTv_turbidity.setText(buoy.getZHU_DU());
                    mTv_humidity.setText(buoy.getSHI_DU());
                } else {

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

            }

            @Override
            public void onNext(String s) {
                if (!TextUtils.isEmpty(s)) {
                    ArrayList<AlgalBlooms> list = new Gson().fromJson(s, new TypeToken<ArrayList<AlgalBlooms>>() {}.getType());
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
        // TODO: 2018/9/28
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer()
                .getPatrolInfo("C0:11:73:99:B1:65", "2018-9-27 8:12:22", "2018-9-27 10:12:22");
//        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getPatrolInfo(mac, anHourAgo, now);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.setToast(mContext, e.toString());
            }

            @Override
            public void onNext(String s) {
                if (!s.equals("0")) {
                    ArrayList<Patrol> list = new Gson().fromJson(s, new TypeToken<ArrayList<Patrol>>() {}.getType());
                    mRv_patrol.setLayoutManager(new LinearLayoutManager(mContext));
                    PatrolAdapter adapter = new PatrolAdapter(mContext, list);
                    mRv_patrol.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                    mRv_patrol.setAdapter(adapter);
                } else {

                }
            }
        });
    }
}
