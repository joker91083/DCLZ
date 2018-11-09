package com.otitan.dclz.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.otitan.dclz.Myapplication;
import com.otitan.dclz.R;
import com.otitan.dclz.adapter.MonitorAdapter;
import com.otitan.dclz.bean.EventReport;
import com.otitan.dclz.net.RetrofitHelper;
import com.otitan.dclz.util.Constant;
import com.titan.baselibrary.util.MobileInfoUtil;
import com.titan.baselibrary.util.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class QueryEventActivity extends AppCompatActivity {


    @BindView(R.id.rv_monitor)
    RecyclerView mRv_monitor;
    @BindView(R.id.query_close)
    ImageView queryClose;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_event);

        ButterKnife.bind(this);

        mContext = this;

        initView();

    }

    private void initView() {
        // 设备号
        String mac = MobileInfoUtil.getMAC(mContext);

        // 当前时间
        String now = Constant.dateFormat.format(new Date());
        // 三天前
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day - 3);
        String anHourAgo = Constant.dateFormat.format(calendar.getTime());

        getMonitor(mac, anHourAgo, now);
    }

    @OnClick(R.id.query_close)
    public void onclike(View view){
        switch (view.getId()){
            case R.id.query_close:
                QueryEventActivity.this.finish();
                break;
        }
    }

    /**
     * 获取事件列表
     */
    private void getMonitor(String mac, String anHourAgo, String now) {
        // TODO: 2018/9/28
        Observable<String> observable = RetrofitHelper.getInstance(mContext).getServer().getPatrolInfo(mac, anHourAgo, now);
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
                    ArrayList<EventReport> list = new Gson().fromJson(s, new TypeToken<ArrayList<EventReport>>() {
                    }.getType());

                    initReportAdapter(list);

                } else {
                    ToastUtil.setToast(mContext, "没有数据");
                }
            }
        });
    }

    private void initReportAdapter(final ArrayList<EventReport> list) {
        final ArrayList<EventReport> arrayList = new ArrayList<>();
        arrayList.addAll(list);

        Box<EventReport> reportBox = Myapplication.getBoxstore().boxFor(EventReport.class);
        QueryBuilder<EventReport> query = reportBox.query();
        List<EventReport> local = query.build().find();

        arrayList.addAll(local);

        mRv_monitor.setLayoutManager(new LinearLayoutManager(mContext));
        MonitorAdapter adapter = new MonitorAdapter(mContext, arrayList);
        mRv_monitor.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRv_monitor.setAdapter(adapter);

        adapter.setItemClickListener(new MonitorAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext,MonitorDetailActivity.class);
                EventReport eventReport = arrayList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("eventReport",eventReport);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
