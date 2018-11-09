package com.otitan.dclz.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.navi.INaviInfoCallback;
import com.otitan.dclz.R;
import com.otitan.dclz.adapter.NavigationAdapter;
import com.otitan.dclz.util.BaiduNavigation;
import com.titan.baselibrary.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 导航
 */
public class NavigationActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView mIv_back;
    @BindView(R.id.et_start)
    EditText mEt_start;
    @BindView(R.id.et_end)
    EditText mEt_end;
    @BindView(R.id.rv_navigation)
    RecyclerView mRv_navigation;
    @BindView(R.id.iv_location)
    ImageView mIv_location;
    @BindView(R.id.tv_navigation)
    TextView mTv_navigation;

    private double mLongitude; // 经度
    private double mLatitude; // 纬度
    private String mCity; // 城市

    private List<String> suggestionList = new ArrayList<>();
    private NavigationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        initLocation();

        mEt_start.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (suggestionList.size() > 0) {
                        suggestionList.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                    getSuggestion(mEt_start);

                    mEt_start.setFocusable(true);
                    mEt_start.setFocusableInTouchMode(true);
                    mEt_start.requestFocus();
                    mEt_start.findFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEt_start, InputMethodManager.SHOW_FORCED); // 显示软键盘
                }
            }
        });
        mEt_end.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (suggestionList.size() > 0) {
                        suggestionList.clear();
                        mAdapter.notifyDataSetChanged();
                    }
                    getSuggestion(mEt_end);

                    mEt_end.setFocusable(true);
                    mEt_end.setFocusableInTouchMode(true);
                    mEt_end.requestFocus();
                    mEt_end.findFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEt_end, InputMethodManager.SHOW_FORCED); // 显示软键盘
                }
            }
        });

        mRv_navigation.setLayoutManager(new LinearLayoutManager(NavigationActivity.this));
        mAdapter = new NavigationAdapter(NavigationActivity.this, suggestionList);
        mRv_navigation.addItemDecoration(new DividerItemDecoration(NavigationActivity.this, DividerItemDecoration.VERTICAL));
        mRv_navigation.setAdapter(mAdapter);
    }

    @OnClick({R.id.iv_back, R.id.iv_location, R.id.tv_navigation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_location:
                showLocation(mLatitude, mLongitude);
                break;

            case R.id.tv_navigation:
                ToastUtil.setToast(this, "导航");
                break;
        }
    }

    /**
     * 获取当前位置
     */
    private void initLocation() {
    }

    /**
     * 显示当前位置
     */
    private void showLocation(double latitude, double longitude) {

    }

    /**
     * 搜索起止位置
     */
    private void getSuggestion(final EditText editText) {

    }
}
