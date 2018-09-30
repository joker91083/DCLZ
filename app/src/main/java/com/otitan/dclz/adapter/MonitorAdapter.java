package com.otitan.dclz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.otitan.dclz.R;
import com.otitan.dclz.bean.Patrol;

import java.util.List;

/**
 * Created by sp on 2018/9/28.
 * 事件列表
 */
public class MonitorAdapter extends RecyclerView.Adapter<MonitorAdapter.MyViewHolder> {

    private Context mContext;
    private List<Patrol> mDatas;

    private MyItemClickListener mItemClickListener;

    public MonitorAdapter(Context context, List<Patrol> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_monitor, null), mItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTv_title.setText(mDatas.get(position).getXJ_SJMC());
        holder.mTv_name.setText(mDatas.get(position).getXJ_SBBH());
        holder.mTv_time.setText(mDatas.get(position).getXJ_SCRQ());

        holder.mTv_status.setTextColor(Color.parseColor("#4CAF50")); // 已上报
        holder.mTv_status.setText("已上报");

        /*holder.mTv_status.setTextColor(Color.parseColor("#fc6f30")); // 未上报
        holder.mTv_status.setText("未上报");*/
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mTv_title;
        private final TextView mTv_name;
        private final TextView mTv_time;
        private final TextView mTv_status;

        private MyItemClickListener mListener;

        MyViewHolder(View itemView, MyItemClickListener myItemClickListener) {
            super(itemView);

            mTv_title = itemView.findViewById(R.id.tv_title);
            mTv_name = itemView.findViewById(R.id.tv_name);
            mTv_time = itemView.findViewById(R.id.tv_time);
            mTv_status = itemView.findViewById(R.id.tv_status);

            // 将全局的监听赋值给接口
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
