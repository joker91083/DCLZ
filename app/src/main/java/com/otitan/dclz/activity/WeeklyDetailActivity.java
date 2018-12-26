package com.otitan.dclz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.otitan.dclz.R;
import com.otitan.dclz.bean.Weekly;
import com.titan.baselibrary.util.ToastUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeeklyDetailActivity extends AppCompatActivity {


    @BindView(R.id.weekly_title)
    TextView weeklyTitle;
    @BindView(R.id.weekly_type)
    TextView weeklyType;
    @BindView(R.id.weekly_author)
    TextView weeklyAuthor;
    @BindView(R.id.weekly_time)
    TextView weeklyTime;
    @BindView(R.id.weekly_year)
    TextView weeklyYear;
    @BindView(R.id.weekly_stage)
    TextView weeklyStage;
    @BindView(R.id.weekly_startdate)
    TextView weeklyStartdate;
    @BindView(R.id.weekly_enddate)
    TextView weeklyEnddate;
    @BindView(R.id.weeklydetailclose)
    ImageView weeklydetailclose;
    @BindView(R.id.weekly_file)
    TextView weeklyFile;
    private Weekly weekly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_detail);

        ButterKnife.bind(this);

        initData();

    }


    private void initData() {
        weekly = (Weekly) getIntent().getSerializableExtra("weekly");

        weeklyTitle.setText(weekly.getJCBG_TITLE());
        weeklyAuthor.setText(weekly.getJCBG_USERID());
        weeklyTime.setText("生成时间: " + weekly.getJCBG_DATE());
        String type = weekly.getJCBG_TYPE();
        if (type.equals("1")) {
            weeklyType.setText("第 " + weekly.getJCBG_QS() + " 期周报");
        } else if (type.equals("2")) {
            weeklyType.setText("第 " + weekly.getJCBG_QS() + " 期月报");
        } else if (type.equals("3")) {
            weeklyType.setText("第 " + weekly.getJCBG_QS() + " 期年报");
        }
        weeklyYear.setText("年份: " + weekly.getJCBG_NF());
        String startdate = weekly.getJCBG_KSRQ();
        weeklyStartdate.setText("开始时间: " + startdate);

        String enddate = weekly.getJCBG_JSRQ();
        weeklyEnddate.setText("结束时间: " + enddate);

        String host = getResources().getString(R.string.serverhost);
        String url = host+weekly.getJCBG_FILE();
        File file = new File(url);
        weeklyFile.setText(file.getName());

    }

    @OnClick({R.id.weeklydetailclose,R.id.weekly_file})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.weeklydetailclose:
                WeeklyDetailActivity.this.finish();
                break;
            case R.id.weekly_file:
                toFileActivity();
                break;
        }

    }

    /**跳转到文件浏览页面*/
    private void toFileActivity(){
        String host = getResources().getString(R.string.serverhost);
        try {
            String value = URLEncoder.encode(weekly.getJCBG_FILE(), "utf-8");
            String url = host+value;

            File file = new File(url);
            if(file.getName().contains(".pdf")){
                Intent intent = new Intent(WeeklyDetailActivity.this,PdfActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("name",value);
                startActivity(intent);
            }else if(file.getName().contains(".docx") || file.getName().contains(".doc")){
                Intent intent = new Intent(WeeklyDetailActivity.this,DocActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("name",value);
                startActivity(intent);
            }else{
                ToastUtil.setToast(WeeklyDetailActivity.this,"未知类型文件");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
