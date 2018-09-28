package com.otitan.dclz.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.otitan.dclz.R;
import com.otitan.dclz.bean.Patrol;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sp on 2018/9/27.
 * 巡查实时事件
 */

public class PatrolAdapter extends RecyclerView.Adapter<PatrolAdapter.MyViewHolder> {

    private Context mContext;
    List<Patrol> mDatas;

    private MyItemClickListener mItemClickListener;

    public PatrolAdapter(Context context, List<Patrol> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_patrol, null), mItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTv_title.setText(mDatas.get(position).getXJ_SJMC());
        holder.mTv_name.setText(mDatas.get(position).getXJ_SBBH());
        holder.mTv_time.setText(mDatas.get(position).getXJ_SCRQ());

        String title = mContext.getResources().getString(R.string.serverhost);
        String fj_url = mDatas.get(position).getFJ_URL();
        String[] split = fj_url.split(",");
//        String path = title + split[0];
        String path = "http://39.129.3.99:8001/UpLoadFiles/2017-04/472b3f0b-bbca-47f2-af6f-11515b23c916_2912_TMPSNAPSHOT1491274024229.jpg";
        Picasso.with(mContext).load(path).into(holder.mIv_picture);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mTv_title;
        private final TextView mTv_name;
        private final TextView mTv_time;
        private final ImageView mIv_picture;

        private MyItemClickListener mListener;

        MyViewHolder(View itemView, MyItemClickListener myItemClickListener) {
            super(itemView);

            mTv_title = itemView.findViewById(R.id.tv_title);
            mTv_name = itemView.findViewById(R.id.tv_name);
            mTv_time = itemView.findViewById(R.id.tv_time);
            mIv_picture = itemView.findViewById(R.id.iv_picture);

            //将全局的监听赋值给接口
            this.mListener = myItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(view, getPosition());
            }
        }
    }

    /**
     * 创建一个回调接口
     */
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 在activity里面adapter就是调用的这个方法,将点击事件监听传递过来,并赋值给全局的监听
     */
    public void setItemClickListener(MyItemClickListener myItemClickListener) {
        this.mItemClickListener = myItemClickListener;
    }
}
