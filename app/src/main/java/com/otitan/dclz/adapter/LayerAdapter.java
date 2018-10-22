package com.otitan.dclz.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.otitan.dclz.R;
import com.otitan.dclz.bean.OtitanLayer;

import java.util.List;

/**
 * Created by sp on 2018/10/21.
 * 图层控制
 */
public class LayerAdapter extends RecyclerView.Adapter<LayerAdapter.MyViewHolder> {

    private Context mContext;
    private List<OtitanLayer> mDatas;

    private MyItemClickListener mItemClickListener;

    public LayerAdapter(Context context, List<OtitanLayer> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_layer, null), mItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.mCtv_layer.setText(mDatas.get(position).getName());
        holder.mCtv_layer.setChecked(mDatas.get(position).isSelect());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (OtitanLayer layer : mDatas) {
                    layer.setSelect(false);
                }
                mDatas.get(position).setSelect(true);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CheckedTextView mCtv_layer;

        private MyItemClickListener mListener;

        MyViewHolder(View itemView, MyItemClickListener myItemClickListener) {
            super(itemView);

            mCtv_layer = itemView.findViewById(R.id.ctv_layer);

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
