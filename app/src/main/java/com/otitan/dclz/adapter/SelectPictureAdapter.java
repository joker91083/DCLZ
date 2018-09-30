package com.otitan.dclz.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.otitan.dclz.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sp on 2018/9/29.
 * 图片选择
 */
public class SelectPictureAdapter extends RecyclerView.Adapter<SelectPictureAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> mDatas;
    private int mColumnWidth = 0;

    private MyItemClickListener mItemClickListener;

    public SelectPictureAdapter(Context context, List<String> datas, int columnWidth) {
        mContext = context;
        mDatas = datas;
        mColumnWidth = columnWidth;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_select_picture, null), mItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == 0) {
            holder.mIv_picture.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_picture));
        } else {
            String path = mDatas.get(position - 1);
            Picasso.with(mContext).load("file://" + path).into(holder.mIv_picture);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mIv_picture;

        private MyItemClickListener mListener;

        MyViewHolder(View itemView, MyItemClickListener myItemClickListener) {
            super(itemView);

            mIv_picture = itemView.findViewById(R.id.iv_picture);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mColumnWidth, mColumnWidth);
            mIv_picture.setLayoutParams(params);

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
