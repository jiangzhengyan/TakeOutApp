package com.xiaozhang.takeout.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaozhang.takeout.model.bean.Order;
import com.xiaozhang.takeout.model.bean.ResponseInfo;
import com.xiaozhang.takeout.ui.adapter.OrderListAdpater;

import java.util.List;

import retrofit2.Call;

/**
 * Created by zhangxiao on 2018/3/23.
 */
public class OrderPresenter extends BasePresenter{
    private OrderListAdpater orderListAdpater;

    public OrderPresenter(OrderListAdpater orderListAdpater) {
        this.orderListAdpater = orderListAdpater;
    }

    @Override
    protected void parseJson(String json) {
        Log.i("",json);
        Gson gson = new Gson();
        List<Order> orderList = gson.fromJson(json,new TypeToken<List<Order>>(){}.getType());

        orderListAdpater.setData(orderList);
    }

    @Override
    protected void showErrorMesssage(String message) {

    }
    public void getOrderData(int userId) {
        Call<ResponseInfo> orderInfo = responseInfoApi.getOrderInfo(userId);
        orderInfo.enqueue(new CallBackAdapter());
    }
    
}
