package com.xiaozhang.takeout.presenter;


import android.util.Log;

import com.google.gson.Gson;
import com.xiaozhang.takeout.model.bean.HomeInfo;
import com.xiaozhang.takeout.model.bean.ResponseInfo;
import com.xiaozhang.takeout.ui.adapter.HomeAdapter;

import retrofit2.Call;

/**
 * Created by zhangxiao on 2018/3/5.
 */

public class HomePresenter extends BasePresenter{
    private static final String TAG = "HomePresenter";
    private HomeAdapter homeAdapter;

    public HomePresenter(HomeAdapter homeAdapter) {
        this.homeAdapter = homeAdapter;
    }

    @Override
    protected void parseJson(String json) {
        Log.i(TAG,json);
        //json解析---->javabean编写
        Gson gson = new Gson();
        HomeInfo homeInfo = gson.fromJson(json, HomeInfo.class);
        //homeInfo中的数据,需要展示在HomeFragment对应的RecyclerView的数据适配器中4

        homeAdapter.setData(homeInfo);
    }

    @Override
    protected void showErrorMesssage(String message) {

    }

    //发送请求方法
    public void getHomeData(String lat,String lon){
        //发送请求出去,链接地址,请求方式,请求参数由getHomeInfo指定
        Call<ResponseInfo> homeInfo = responseInfoApi.getHomeInfo(lat, lon);
        //请求发出去后,在哪个回调方法中处理,指定CallBackAdapter处理(CallBackAdapter 实现了 CallBack的接口 并且实现了
        // onResponse和onFail方法)
        homeInfo.enqueue(new CallBackAdapter());
    }
}
