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
import com.otitan.dclz.activity.WeeklyDetailActivity;
import com.otitan.dclz.adapter.MonitorAdapter;
import com.otitan.dclz.adapter.WeeklyAdapter;
import com.otitan.dclz.bean.Weekly;
import com.otitan.dclz.net.RetrofitHelper;
import com.otitan.dclz.util.Constant;
import com.titan.baselibrary.util.ToastUtil;

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
        String month = Constant.monthFormat.format(new Date());
        getWeeklyByMonth(month);
    }

    /**
     * 获取事件列表
     */
    private void getWeeklyByMonth(String month) {
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getWeeklyData(month);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.setToast(mContext,"获取周报"+e.getMessage());
            }

            @Override
            public void onNext(String s) {
                if (!s.equals("0")) {
                    ArrayList<Weekly> list = new Gson().fromJson(s, new TypeToken<ArrayList<Weekly>>() {}.getType());

                    initReportAdapter(list);

                } else {
                    ToastUtil.setToast(mContext,"没有周报数据");
                }
            }
        });
    }

    private void initReportAdapter(final ArrayList<Weekly> list){


        mRv_monitor.setLayoutManager(new LinearLayoutManager(mContext));
        WeeklyAdapter adapter = new WeeklyAdapter(mContext,list);


        mRv_monitor.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRv_monitor.setAdapter(adapter);

        adapter.setItemClickListener(new WeeklyAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Weekly weekly = list.get(position);
                Intent intent = new Intent(mContext,WeeklyDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("weekly",weekly);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
