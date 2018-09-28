package com.otitan.dclz.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.otitan.dclz.R;

import butterknife.ButterKnife;

/**
 * Created by sp on 2018/9/25.
 * 地基遥感
 */
public class GroundFragment extends Fragment {

    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_ground, null);
        ButterKnife.bind(this, inflate);

        mContext = GroundFragment.this.getContext();

        //initView();

        return inflate;
    }
}
