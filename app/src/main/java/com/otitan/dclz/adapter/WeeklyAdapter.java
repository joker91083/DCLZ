package com.otitan.dclz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.otitan.dclz.R;
import com.otitan.dclz.bean.Weekly;

import java.util.ArrayList;
import java.util.List;

public class WeeklyAdapter extends RecyclerView.Adapter<WeeklyAdapter.ViewHolde>{

    private Context mContext;
    private List<Weekly> mDatas = new ArrayList<>();

    private MyItemClickListener mItemClickListener;

    public WeeklyAdapter(Context context, List<Weekly> datas){
        this.mContext = context;
        this.mDatas = datas;
    }

    @Override
    public ViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolde holde = new ViewHolde(LayoutInflater.from(mContext).inflate(R.layout.item_monitor, null),mItemClickListener);
        return holde;
    }

    @Override
    public void onBindViewHolder(ViewHolde holder, int position) {
        holder.mTv_title.setText(mDatas.get(position).getJCBG_TITLE());
        holder.mTv_name.setText(mDatas.get(position).getJCBG_NF());
        holder.mTv_time.setText(mDatas.get(position).getJCBG_DATE());

        String lid = mDatas.get(position).getJCBG_TYPE();
        if(lid.equals("1")){
            holder.mTv_status.setTextColor(Color.parseColor("#fc6f30"));
            holder.mTv_status.setText("周报");
        }else if(lid.equals("2")){
            holder.mTv_status.setTextColor(Color.parseColor("#4CAF50"));
            holder.mTv_status.setText("月报");
        }else if(lid.equals("3")){
            holder.mTv_status.setTextColor(Color.parseColor("#4CEF50"));
            holder.mTv_status.setText("年报");
        }else{
            holder.mTv_status.setTextColor(Color.parseColor("#4EAF50"));
            holder.mTv_status.setText("未知类型");
        }

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    class ViewHolde extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView mTv_title;
        private final TextView mTv_name;
        private final TextView mTv_time;
        private final TextView mTv_status;

        private MyItemClickListener mListener;

        public ViewHolde(View itemView, MyItemClickListener myItemClickListener) {
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
