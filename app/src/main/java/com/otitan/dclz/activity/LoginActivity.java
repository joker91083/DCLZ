package com.otitan.dclz.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.otitan.dclz.MainActivity;
import com.otitan.dclz.R;
import com.otitan.dclz.net.RetrofitHelper;
import com.otitan.dclz.util.MobileUtil;
import com.otitan.dclz.util.ResourceHelper;
import com.titan.baselibrary.util.ToastUtil;
import com.titan.versionupdata.VersionUpdata;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 登录
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.et_username)
    EditText mEt_username;
    @BindView(R.id.et_password)
    EditText mEt_password;
    @BindView(R.id.tv_login)
    TextView mTv_login;

    // 临时文件
    public static final String PREFS_NAME = "MYSP";
    public static SharedPreferences MSP;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        mContext = LoginActivity.this;

        MSP = this.getSharedPreferences(PREFS_NAME, 0);

        initView();
    }

    private void initView() {
        mEt_username.setText(MSP.getString("USERNAME", "").trim());
        mEt_password.setText(MSP.getString("PASSWORD", "").trim());

        mTv_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                String user = mEt_username.getText().toString().trim();
                String pass = mEt_password.getText().toString().trim();

                if (user.equals("")) {
                    ToastUtil.setToast(this, "请输入用户名");
                } else if (pass.equals("")) {
                    ToastUtil.setToast(this, "请输入密码");
                } else {
                    if(RetrofitHelper.getInstance(this).networkMonitor.isConnected()){
                        login(user, pass);
                    }else{
                        ToastUtil.setToast(this,"网络未连接");
                    }
                }
                break;
        }
    }

    /**
     * 登录
     */
    private void login(final String user, final String pass) {
        String sbh = MobileUtil.getInstance().getMacAdress(mContext);
        String xlh = MobileUtil.getInstance().getMobileXlh(mContext);
        Observable<String> observable = RetrofitHelper.getInstance(this).getServer().checkLogin(user, pass,sbh,xlh);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() { // 完成请求后

            }

            @Override
            public void onError(Throwable e) { // 异常处理
                ToastUtil.setToast(mContext,"登录"+e.getMessage());
            }

            @Override
            public void onNext(String s) { // 请求成功
                if (s.equals("1")) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                    MSP.edit().putString("USERNAME", user).apply(); // 用户名
                    MSP.edit().putString("PASSWORD", pass).apply(); // 密码
                } else {
                    ToastUtil.setToast(LoginActivity.this, "登录失败");
                }
            }
        });
    }

    /*注册设备号到后台*/
    private void addMacAddress(){
        String sbh = MobileUtil.getInstance().getMacAdress(this);
        String xlh = MobileUtil.getInstance().getMobileXlh(this);
        Observable<String> observable = RetrofitHelper.getInstance(this).getServer().addMacAddress(sbh, xlh);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() { // 完成请求后

            }

            @Override
            public void onError(Throwable e) { // 异常处理

            }

            @Override
            public void onNext(String s) { // 请求成功
                if (s.contains("1")) {
                    //注册成功
                } else {

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(RetrofitHelper.getInstance(this).networkMonitor.isConnected()){
            String url = this.getResources().getString(R.string.updataurl);
            boolean flag = new VersionUpdata(this).checkVersion(url);
            if(!flag){
                //最新版本
            }else{
                //更新版本
            }
        }
    }
}
