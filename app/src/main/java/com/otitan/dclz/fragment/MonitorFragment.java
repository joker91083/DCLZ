package com.otitan.dclz.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.otitan.dclz.R;
import com.otitan.dclz.activity.MonitorDetailActivity;
import com.otitan.dclz.adapter.MonitorAdapter;
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
 * 监测周报
 */
public class MonitorFragment extends Fragment {

    @BindView(R.id.rv_monitor)
    RecyclerView mRv_monitor;

    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_monitor, null);
        ButterKnife.bind(this, inflate);

        mContext = MonitorFragment.this.getContext();

        initView();

        return inflate;
    }

    private void initView() {
        // 设备号
        String mac = MobileInfoUtil.getMAC(mContext);

        // 当前时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        // 一小时前
        long current = System.currentTimeMillis();
        current -= 60 * 60 * 1000;
        String anHourAgo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));

        getMonitor(mac, anHourAgo, now);
    }

    /**
     * 获取事件列表
     */
    private void getMonitor(String mac, String anHourAgo, String now) {
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

            }

            @Override
            public void onNext(String s) {
                if (!s.equals("0")) {
                    ArrayList<Patrol> list = new Gson().fromJson(s, new TypeToken<ArrayList<Patrol>>() {}.getType());
                    mRv_monitor.setLayoutManager(new LinearLayoutManager(mContext));
                    MonitorAdapter adapter = new MonitorAdapter(mContext, list);
                    mRv_monitor.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                    mRv_monitor.setAdapter(adapter);

                    adapter.setItemClickListener(new MonitorAdapter.MyItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            startActivity(new Intent(mContext, MonitorDetailActivity.class));
                        }
                    });
                } else {

                }
            }
        });
    }
}
