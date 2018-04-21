package com.xiaozhang.takeout.presenter.net;

import com.xiaozhang.takeout.model.bean.ResponseInfo;
import com.xiaozhang.takeout.util.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by zhangxiao on 2018/3/5.
 */
public interface ResponseInfoApi {
    //完整的url地址,get post请求方式,请求参数,接收请求结果(xml,json)
    //http://10.0.2.2:8080/TakeoutService/home?latitude=lat&longitude=lon
    //Call后面的T,就代表网络请求发送成功后,获取到的javabean
    @GET(Constant.HOME)
    Call<ResponseInfo> getHomeInfo(@Query("latitude") String lat, @Query("longitude") String lon);

    @GET(Constant.BUSINESS)
    Call<ResponseInfo> getBusinessInfo(@Query("sellerId") long sellerId);

    @GET(Constant.LOGIN)
    Call<ResponseInfo> getLoginInfo(@Query("username") String username, @Query("password") String psd,
                                    @Query("phone") String phone, @Query("type") int type);
    @GET(Constant.ORDER)
    Call<ResponseInfo> getOrderInfo(@Query("userId") int userId);
}
