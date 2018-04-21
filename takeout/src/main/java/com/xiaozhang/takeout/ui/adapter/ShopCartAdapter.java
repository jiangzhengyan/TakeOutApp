package com.xiaozhang.takeout.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.model.bean.GoodsInfo;
import com.xiaozhang.takeout.model.bean.GoodsTypeInfo;
import com.xiaozhang.takeout.ui.activity.BusinessActivity;
import com.xiaozhang.takeout.ui.fragment.GoodsFragment;
import com.xiaozhang.takeout.util.Constant;
import com.xiaozhang.takeout.util.CountPriceFormater;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhangxiao on 2018/3/23.
 */
public class ShopCartAdapter extends RecyclerView.Adapter {
    private BusinessActivity activity;
    private List<GoodsInfo> data;

    public ShopCartAdapter(BusinessActivity activity) {
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(activity, R.layout.item_cart, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).tvName.setText(data.get(position).getName());
        ((ViewHolder) holder).tvCount.setText(data.get(position).getCount() + "");
        float totalPrice = data.get(position).getCount() * data.get(position).getNewPrice();
        ((ViewHolder) holder).tvTypeAllPrice.setText(CountPriceFormater.format(totalPrice));

        ((ViewHolder) holder).setPosition(position);
    }

    @Override
    public int getItemCount() {
        if (data != null && data.size() > 0) {
            return data.size();
        }
        return 0;
    }

    public void setData(List<GoodsInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.tv_name)
        TextView tvName;
        @InjectView(R.id.tv_type_all_price)
        TextView tvTypeAllPrice;
        @InjectView(R.id.ib_minus)
        ImageButton ibMinus;
        @InjectView(R.id.tv_count)
        TextView tvCount;
        @InjectView(R.id.ib_add)
        ImageButton ibAdd;
        @InjectView(R.id.ll)
        LinearLayout ll;
        private int position;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        @OnClick({R.id.ib_minus,R.id.ib_add})
        public void onClick(View view){
            switch (view.getId()){
                case R.id.ib_minus:
                    deleteGoodsInfo(Constant.DELETE);
                    break;
                case R.id.ib_add:
                    addGoodsInfo(Constant.ADD);
                    break;
            }
        }

        private void deleteGoodsInfo(int operate) {
            //右侧商品列表的集合
            updateGoodsAdapter(operate);
            //左侧商品分类的集合
            updataGoodsTypeAdapter(operate);
            //购物车列表集合
            notifyDataSetChanged();
            //如果被减商品的数量已经变为0,则需要将此商品从data集合中移除
            GoodsInfo goodsInfo = data.get(position);
            if (goodsInfo.getCount() == 0){
                data.remove(goodsInfo);
                notifyDataSetChanged();
            }
            //当商品递减为0的时候,也就是data中不包含任何对象的时候,需要将对话框隐藏
            if (data.size() == 0){
                //隐藏对话框
                activity.dismissSheetViewDialog();
            }
            //购物车显示的气泡,金额更新
            activity.businessPresenter.refreshShopCart();
        }

        /**
         * 购物车中添加一件商品
         */
        private void addGoodsInfo(int operate) {
            //右侧商品列表的集合
            updateGoodsAdapter(operate);
            //左侧商品分类的集合
            updataGoodsTypeAdapter(operate);
            //购物车列表集合
            notifyDataSetChanged();
            //购物车显示的气泡,金额更新
            activity.businessPresenter.refreshShopCart();
        }

        private void updataGoodsTypeAdapter(int operate) {
            //获取需要变更数量的商品的分类typeId
            int typeId = data.get(position).getTypeId();
            //获取右侧商品列表的集合
            GoodsFragment goodsFragment = activity.getGoodsFragment();
            if (goodsFragment!=null){
                List<GoodsTypeInfo> goodsTypeInfoList = goodsFragment.goodsTypeAdapter.getData();
                for (int i = 0; i < goodsTypeInfoList.size(); i++) {
                    GoodsTypeInfo goodsTypeInfo = goodsTypeInfoList.get(i);
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
                goodsFragment.goodsTypeAdapter.notifyDataSetChanged();
            }
        }

        /**
         * 更新右侧商品列表的集合
         * @param operate    通过operate操作符判断是加一件商品还是减一件商品
         */
        private void updateGoodsAdapter(int operate) {
            //获取需要变更数量的商品的唯一性id
            int id = data.get(position).getId();
            //获取右侧商品列表的集合
            GoodsFragment goodsFragment = activity.getGoodsFragment();
            if (goodsFragment!=null){
                ArrayList<GoodsInfo> goodsInfoList = goodsFragment.goodsAdapter.getData();
                for (int i = 0; i < goodsInfoList.size(); i++) {
                    GoodsInfo goodsInfo = goodsInfoList.get(i);
                    if (goodsInfo.getId() == id){
                        switch (operate){
                            case Constant.ADD:
                                int addCount = goodsInfo.getCount() + 1;
                                goodsInfo.setCount(addCount);
                                break;
                            case Constant.DELETE:
                                if (goodsInfo.getCount()>0){
                                    int deleteCount = goodsInfo.getCount() - 1;
                                    goodsInfo.setCount(deleteCount);
                                }
                                break;
                        }

                        break;
                    }
                }
                goodsFragment.goodsAdapter.notifyDataSetChanged();
            }
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
