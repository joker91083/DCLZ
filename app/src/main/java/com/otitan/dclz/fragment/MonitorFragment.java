package com.otitan.dclz.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esri.arcgisruntime.geometry.Geometry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.otitan.dclz.R;
import com.otitan.dclz.activity.WeeklyDetailActivity;
import com.otitan.dclz.adapter.WeeklyAdapter;
import com.otitan.dclz.bean.Weekly;
import com.otitan.dclz.common.ValueCallback;
import com.otitan.dclz.net.RetrofitHelper;
import com.otitan.dclz.util.Constant;
import com.otitan.dclz.util.TimeUtil;
import com.titan.baselibrary.timepaker.TimePopupWindow;
import com.titan.baselibrary.util.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.time_start)
    TextView timeStart;
    @BindView(R.id.time_end)
    TextView timeEnd;

    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_monitor, container,false);
        ButterKnife.bind(this, inflate);

        mContext = MonitorFragment.this.getContext();

        initView();

        return inflate;
    }

    private void initView() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE,day-1);
        Date date = calendar.getTime();

        String month = Constant.yearFormat.format(new Date());
        int mon = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH,mon-1);
        Date mont = calendar.getTime();
        String cur = Constant.yearFormat.format(mont);

        timeStart.setText(cur);
        timeEnd.setText(month);

        getWeeklyByMonth(cur,month);
    }

    /**
     * 获取周报列表
     */
    private void getWeeklyByMonth(String month,String curtime) {
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getWeeklyBytime(month,curtime);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.setToast(mContext, "获取周报" + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                if (!s.equals("0")) {
                    ArrayList<Weekly> list = new Gson().fromJson(s, new TypeToken<ArrayList<Weekly>>() {
                    }.getType());

                    initReportAdapter(list);

                } else {
                    //ToastUtil.setToast(mContext,"没有周报数据");
                }
            }
        });
    }

    /**
     * 获取周报列表
     */
    private void getWeeklyByTime(String startime,String endtime) {
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getWeeklyBytime(startime,endtime);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.setToast(mContext, "获取周报" + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                if (!s.equals("0")) {
                    ArrayList<Weekly> list = new Gson().fromJson(s, new TypeToken<ArrayList<Weekly>>() {
                    }.getType());

                    initReportAdapter(list);

                } else {
                    ToastUtil.setToast(mContext,"当前时间段内没有周报数据");
                }
            }
        });
    }

    private void initReportAdapter(final ArrayList<Weekly> list) {

        mRv_monitor.setLayoutManager(new LinearLayoutManager(mContext));
        WeeklyAdapter adapter = new WeeklyAdapter(mContext, list);


        mRv_monitor.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRv_monitor.setAdapter(adapter);

        adapter.setItemClickListener(new WeeklyAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Weekly weekly = list.get(position);
                Intent intent = new Intent(mContext, WeeklyDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("weekly", weekly);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    @OnClick({R.id.time_start, R.id.time_end})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_start:
                selectTime(mContext, timeStart, new ValueCallback() {
                    @Override
                    public void onSuccess(Object t) {

                    }

                    @Override
                    public void onFail(String value) {
                        ToastUtil.setToast(mContext,value);
                    }

                    @Override
                    public void onGeometry(Geometry geometry) {

                    }
                });
                break;
            case R.id.time_end:
                selectTime(mContext, timeEnd, new ValueCallback() {
                    @Override
                    public void onSuccess(Object t) {
                        // 根据选择的时间，查询周报信息
                        String startime = timeStart.getText().toString();
                        String endtime = timeEnd.getText().toString();
                        getWeeklyByTime(startime,endtime);
                    }

                    @Override
                    public void onFail(String value) {
                        ToastUtil.setToast(mContext,value);
                    }

                    @Override
                    public void onGeometry(Geometry geometry) {

                    }
                });
                break;
        }


    }

    private void selectTime(Context context, final TextView textView, final ValueCallback callback){
        TimePopupWindow timePopupWindow = new TimePopupWindow(context, TimePopupWindow.Type.YEAR_MONTH_DAY);
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
                    textView.setText(choose);
                    callback.onSuccess(choose);

                } else {
                    callback.onFail("选择日期错误，无法查询！");
                }
            }
        });
        timePopupWindow.showAtLocation(textView, Gravity.CENTER, 0, 0, new Date());
    }
}
