package com.xiaozhang.takeout.presenter;




import android.util.Log;

import com.google.gson.Gson;
import com.xiaozhang.takeout.model.bean.BusinessInfo;
import com.xiaozhang.takeout.model.bean.GoodsInfo;
import com.xiaozhang.takeout.model.bean.GoodsTypeInfo;
import com.xiaozhang.takeout.model.bean.ResponseInfo;
import com.xiaozhang.takeout.model.bean.Seller;
import com.xiaozhang.takeout.ui.adapter.GoodsAdapter;
import com.xiaozhang.takeout.ui.adapter.GoodsTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by zhangxiao on 2018/3/6.
 */

public class GoodsPresenter extends BasePresenter {
    private static final String TAG = "GoodsPresenter";
    private GoodsAdapter goodsAdapter;
    private Seller seller;
    private GoodsTypeAdapter goodsTypeAdapter;
    private BusinessInfo businessInfo;
    private ArrayList<GoodsInfo> goodsInfosList;

    public GoodsPresenter(GoodsTypeAdapter goodsTypeAdapter, Seller seller, GoodsAdapter goodsAdapter) {
        this.goodsTypeAdapter = goodsTypeAdapter;
        this.seller = seller;
        this.goodsAdapter = goodsAdapter;
    }

    @Override
    protected void parseJson(String json) {
        //获取数据后,解析json方法
        Log.i(TAG,json);
        Gson gson = new Gson();
        businessInfo = gson.fromJson(json, BusinessInfo.class);
        //从businessInfo对象中获取左侧分类集合数据,和分类下具体商品的集合数据
        //先处理左侧列表(商品分类)的数据展示
        goodsTypeAdapter.setData(businessInfo.getList());
        //商品的数据都放置在每一个分类GoodsTypeInfo对象中,需要将GoodsTypeInfo中的list集合获取,并且拼接成一个大的集合,用于填充右侧商品的listView
        initGoodsData();
        //将商品集合的数据,传递给右侧的数据适配器
        goodsAdapter.setData(goodsInfosList);
    }

    /**
     * 重新处理商品数据,给每一件商品,设置分类id,分类名称,商家id
     */
    private void initGoodsData() {
        goodsInfosList = new ArrayList<>();
        List<GoodsTypeInfo> goodsTypeList = businessInfo.getList();//商品分类的集合
        for (int i = 0; i < goodsTypeList.size(); i++) {
            GoodsTypeInfo goodsTypeInfo = goodsTypeList.get(i);//获取商品分类的对象

            List<GoodsInfo> goodsInfoList = goodsTypeInfo.getList();//此分类对应的商品集合
            for (int j = 0; j < goodsInfoList.size(); j++) {
                GoodsInfo goodsInfo = goodsInfoList.get(j);
                //设置商家的id
                goodsInfo.setSellerId((int) seller.getId());
                //设置此件商品所属的分类id
                goodsInfo.setTypeId(goodsTypeInfo.getId());
                //设置此件商品所属的分类名称
                goodsInfo.setTypeName(goodsTypeInfo.getName());
                //放置到一个大的集合中,此集合用于填充ListView的数据适配器
                goodsInfosList.add(goodsInfo);
            }
        }
    }

    @Override
    protected void showErrorMesssage(String message) {
        //请求服务器异常,如何处理异常方法
    }

    /**
     * @param sellerId  商家的唯一性id
     *                  不同的商家id一定不一致,通过不同的id得到的返回数据也会有差异
     */
    public void getBusinessData(long sellerId){
        Call<ResponseInfo> businessInfo = responseInfoApi.getBusinessInfo(sellerId);
        businessInfo.enqueue(new CallBackAdapter());
    }
}
