package com.xiaozhang.takeout.ui.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.model.bean.GoodsTypeInfo;
import com.xiaozhang.takeout.ui.fragment.GoodsFragment;
import com.xiaozhang.takeout.util.Constant;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhangxiao on 2018/3/6.
 * 左侧分类的数据适配器
 */
public class GoodsTypeAdapter extends RecyclerView.Adapter {
    private GoodsFragment goodsFragment;
    private Activity activity;
    private List<GoodsTypeInfo> data;
    //选中条目的索引值
    public int currentPosition = 0;

    public GoodsTypeAdapter(Activity activity,GoodsFragment goodsFragment) {
        this.activity = activity;
        this.goodsFragment = goodsFragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(activity, R.layout.item_type, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).type.setText(data.get(position).getName());
        if (position == currentPosition) {
            //此条目背景变白,文字变红
            ((ViewHolder) holder).itemView.setBackgroundColor(Color.WHITE);
            ((ViewHolder) holder).type.setTextColor(Color.RED);
        } else {
            //此条目背景变灰色,文字变黑
            ((ViewHolder) holder).itemView.setBackgroundColor(activity.getResources().getColor(R.color.goods_otem_type_color));
            ((ViewHolder) holder).type.setTextColor(Color.BLACK);
        }
        ((ViewHolder) holder).setPosition(position);
        //显示商品分类数量
        if (data.get(position).getCount()>0){
            //此分类有商品被选中，红色的气泡显示
            ((ViewHolder) holder).tvCount.setVisibility(View.VISIBLE);
            //气泡显示选中商品数量
            ((ViewHolder) holder).tvCount.setText(data.get(position).getCount()+"");
        }else{
            //此分类没有商品被选中，红色的气泡隐藏
            ((ViewHolder) holder).tvCount.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (data != null && data.size() > 0) {
            return data.size();
        }
        return 0;
    }

    public void setData(List<GoodsTypeInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public List<GoodsTypeInfo> getData() {
        return data;
    }

    /**
     * @param operate   对分类做的操作（增加，减少）
     * @param typeId    对那个分类做选中数量的增加，减少
     */
    public void refreshGoodsType(int operate, int typeId) {
        //根据typeId，查找分类中id和typeId一致GoodsTypeInfo对象，根据操作符，更改数量
        for (int i = 0; i < data.size(); i++) {
            GoodsTypeInfo goodsTypeInfo = data.get(i);
            if (goodsTypeInfo.getId() == typeId){
                switch (operate){
                    case Constant.ADD:
                        int addCount = goodsTypeInfo.getCount() + 1;
                        goodsTypeInfo.setCount(addCount);
                        break;
                    case Constant.DELETE:
                        if (goodsTypeInfo.getCount()>0){
                            int deleteCount = goodsTypeInfo.getCount() - 1;
                            goodsTypeInfo.setCount(deleteCount);
                        }
                        break;
                }
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.tvCount)
        TextView tvCount;
        @InjectView(R.id.type)
        TextView type;
        private int position;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = position;
                    notifyDataSetChanged();
                    //将右侧列表进行滚动逻辑
                    //左侧分类列表集合和右侧商品列表集合
                    //(GoodsTypeInfo)id和(GoodsInfo)typeid一致
                    int id = data.get(currentPosition).getId();
                    goodsFragment.switchTypeId(id);
                }
            });
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
