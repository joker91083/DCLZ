package com.otitan.dclz.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.otitan.dclz.R;
import com.otitan.dclz.net.RetrofitHelper;
import com.titan.baselibrary.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 修改密码
 */
public class ChangePassActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView mIv_back;
    @BindView(R.id.et_old)
    EditText mEt_old;
    @BindView(R.id.et_new)
    EditText mEt_new;
    @BindView(R.id.et_check)
    EditText mEt_check;
    @BindView(R.id.tv_change)
    TextView mTv_change;

    public static final String PREFS_NAME = "MYSP";
    public static SharedPreferences MSP;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        ButterKnife.bind(this);

        MSP = this.getSharedPreferences(PREFS_NAME, 0);

        initView();
    }

    private void initView() {
        username = MSP.getString("USERNAME", "").trim();
        password = MSP.getString("PASSWORD", "").trim();

        mIv_back.setOnClickListener(this);
        mTv_change.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_change:
                String oldPass = mEt_old.getText().toString().trim();
                String newPass = mEt_new.getText().toString().trim();
                String checkPass = mEt_check.getText().toString().trim();

                if (!oldPass.equals(password)) {
                    ToastUtil.setToast(this, "原始密码不正确");
                } else if (TextUtils.isEmpty(newPass)) {
                    ToastUtil.setToast(this, "请输入新密码");
                } else if (TextUtils.isEmpty(checkPass)) {
                    ToastUtil.setToast(this, "请确认新密码");
                } else if (!newPass.equals(checkPass)) {
                    ToastUtil.setToast(this, "密码输入不一致");
                } else {
                    changePass(newPass);
                }
                break;
        }
    }

    /**
     * 修改密码
     */
    private void changePass(String newPass) {
        Observable<String> observable = RetrofitHelper.getInstance(this).getServer().changePassword("15559607687", newPass);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                if (s.equals("1")) {
                    ToastUtil.setToast(ChangePassActivity.this, "修改成功");
                } else {
                    ToastUtil.setToast(ChangePassActivity.this, "修改失败");
                }
            }
        });
    }
}
