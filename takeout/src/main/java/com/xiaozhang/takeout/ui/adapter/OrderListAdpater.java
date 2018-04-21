package com.xiaozhang.takeout.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.model.bean.Order;
import com.xiaozhang.takeout.observer.OrderObserver;
import com.xiaozhang.takeout.ui.activity.OrderDetailActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhangxiao on 2018/3/23.
 */
public class OrderListAdpater extends RecyclerView.Adapter implements Observer{
    private Context mCtx;
    private List<Order> data;

    public OrderListAdpater(Context ctx) {
        this.mCtx = ctx;
        //此行代码就等同于向observable的list集合中添加一个observer对象的操作
        OrderObserver.getInstance().addObserver(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mCtx, R.layout.item_order_item, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Order order = data.get(position);
        ((ViewHolder)holder).tvOrderItemSellerName.setText(order.getSeller().getName());
        //订单状态
        String type = order.getType();
        ((ViewHolder)holder).tvOrderItemType.setText(getOrderTypeInfo(type));//根据类别设置订单类型

        ((ViewHolder)holder).setPosition(position);
    }

    private String getOrderTypeInfo(String type) {
        String typeInfo = "";
        switch (type) {
            case OrderObserver.ORDERTYPE_UNPAYMENT:
                typeInfo = "未支付";
                break;
            case OrderObserver.ORDERTYPE_SUBMIT:
                typeInfo = "已提交订单";
                break;
            case OrderObserver.ORDERTYPE_RECEIVEORDER:
                typeInfo = "商家接单";
                break;
            case OrderObserver.ORDERTYPE_DISTRIBUTION:
                typeInfo = "配送中";
                break;
            case OrderObserver.ORDERTYPE_SERVED:
                typeInfo = "已送达";
                break;
            case OrderObserver.ORDERTYPE_CANCELLEDORDER:
                typeInfo = "取消的订单";
                break;
        }
        return typeInfo;
    }

    @Override
    public int getItemCount() {
        if (data!=null && data.size()>0){
            return data.size();
        }
        return 0;
    }

    public void setData(List<Order> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    //等同于调用observer中的update方法
    @Override
    public void update(Observable o, Object arg) {
        HashMap<String, String> map = (HashMap<String, String>) arg;
        String orderId = map.get("orderId");
        String type = map.get("type");
        int position = -1;
        for (int i = 0; i < data.size(); i++) {
            String id = data.get(i).getId();
            if (id.equals(orderId)){
                data.get(i).setType(type);
                position = i;
                break;
            }
        }
        if (position!=-1){
            notifyItemChanged(position);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.iv_order_item_seller_logo)
        ImageView ivOrderItemSellerLogo;
        @InjectView(R.id.tv_order_item_seller_name)
        TextView tvOrderItemSellerName;
        @InjectView(R.id.tv_order_item_type)
        TextView tvOrderItemType;
        @InjectView(R.id.tv_order_item_time)
        TextView tvOrderItemTime;
        @InjectView(R.id.tv_order_item_foods)
        TextView tvOrderItemFoods;
        @InjectView(R.id.tv_order_item_money)
        TextView tvOrderItemMoney;
        @InjectView(R.id.tv_order_item_multi_function)
        TextView tvOrderItemMultiFunction;
        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mCtx, OrderDetailActivity.class);
                    intent.putExtra("orderId",data.get(position).getId());
                    intent.putExtra("type",data.get(position).getType());
                    mCtx.startActivity(intent);
                }
            });
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
