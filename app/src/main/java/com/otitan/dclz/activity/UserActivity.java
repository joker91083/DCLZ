package com.otitan.dclz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.TextOptions;
import com.google.gson.Gson;
import com.otitan.dclz.R;
import com.otitan.dclz.bean.User;
import com.otitan.dclz.hikvision.HikVisionActivity;
import com.otitan.dclz.net.RetrofitHelper;
import com.titan.baselibrary.util.ToastUtil;
import com.titan.versionupdata.VersionUpdata;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 个人中心
 */
public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView mIv_back;
    @BindView(R.id.tv_unit)
    TextView mTv_unit;
    @BindView(R.id.tv_department)
    TextView mTv_department;
    @BindView(R.id.tv_name)
    TextView mTv_name;
    @BindView(R.id.tv_sex)
    TextView mTv_sex;
    @BindView(R.id.tv_mobile)
    TextView mTv_mobile;
    @BindView(R.id.tv_email)
    TextView mTv_email;
    @BindView(R.id.tv_version)
    TextView mTv_version;
    @BindView(R.id.tv_change)
    TextView mTv_change;
    @BindView(R.id.tv_check)
    TextView mTv_check;
    @BindView(R.id.up_version)
    LinearLayout upVersion;
    @BindView(R.id.tv_hik)
    TextView mTv_hik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        getUser();
        String value = "V" + getPackageInfo(this).versionName;
        mTv_version.setText(value);

        mIv_back.setOnClickListener(this);
        mTv_change.setOnClickListener(this);
        mTv_check.setOnClickListener(this);

        upVersion.setOnClickListener(this);

        mTv_hik.setOnClickListener(this);
    }

    /**
     * 获取用户信息
     */
    private void getUser() {
        Observable<String> observable = RetrofitHelper.getInstance(this).getServer().getUserInfo("15559607687", "admin1", "admin");
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
                    User user = new Gson().fromJson(s, User.class);

                    mTv_unit.setText(user.getUNITNAME());
                    mTv_department.setText(user.getDEPTNAME());
                    mTv_name.setText(user.getREALNAME());
                    mTv_sex.setText(user.getSEX());
                    mTv_mobile.setText(user.getMOBILEPHONENO());
                    mTv_email.setText(user.getUSERMZ());
                } else {

                }
            }
        });
    }

    /**
     * 版本信息
     */
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_change:
                startActivity(new Intent(this, ChangePassActivity.class));
                break;

            case R.id.tv_check:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.up_version:
                String url = this.getResources().getString(R.string.updataurl);
                boolean flag = new VersionUpdata(this).checkVersion(url);
                if(!flag){
                    ToastUtil.setToast(this,"已经是最新版本");
                }

                break;

            case R.id.tv_hik:
                startActivity(new Intent(this, HikVisionActivity.class));
                break;
        }
    }
}
