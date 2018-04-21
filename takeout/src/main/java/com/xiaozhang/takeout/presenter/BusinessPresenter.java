package com.xiaozhang.takeout.presenter;

import android.util.Log;

import com.xiaozhang.takeout.model.bean.GoodsInfo;
import com.xiaozhang.takeout.model.bean.GoodsTypeInfo;
import com.xiaozhang.takeout.ui.activity.BusinessActivity;
import com.xiaozhang.takeout.ui.fragment.GoodsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiao on 2018/3/16.
 * 封装BusinessActivity的业务提供的类
 * BusinessActivity包含3个Fragment
 *      GoodsFragment
 *          左侧分类数据适配器---->左侧分类的集合goodsTypeInfo的数据集合
 *          GoodsAdapter右侧商品的数据适配器--->右侧商品集合goodsInfo数据集合
 *      SuggestFragment
 *      SellerFragment
 */

public class BusinessPresenter extends BasePresenter{
    private BusinessActivity businessActivity;

    public BusinessPresenter(BusinessActivity businessActivity) {
        this.businessActivity = businessActivity;
    }

    @Override
    protected void parseJson(String json) {

    }

    @Override
    protected void showErrorMesssage(String message) {

    }

    /**
     * 更新购物车中的商品数量，和购买金额
     */
    public void refreshShopCart() {
        //购买的商品数量，已经在右侧列表的集合中记录了
        int totalCount = 0;
        float totalPrice = 0;
        GoodsFragment goodsFragment = businessActivity.getGoodsFragment();
        if (goodsFragment!=null){
            ArrayList<GoodsInfo> goodsInfoList = goodsFragment.goodsAdapter.getData();
            for (int i = 0; i < goodsInfoList.size(); i++) {
                GoodsInfo goodsInfo = goodsInfoList.get(i);
                if (goodsInfo.getCount()>0){
                    totalCount += goodsInfo.getCount();
                    //商品数量*商品的单价
                    totalPrice += goodsInfo.getCount()*goodsInfo.getNewPrice();
                }
            }
            Log.i("","==========totalCount = "+totalCount);
            Log.i("","==========totalPrice = "+totalPrice);
            //将计算好的数据，传递给BusinesActivity中的控件，显示即可
            businessActivity.updateShopCart(totalCount,totalPrice);
        }
    }

    /**
     * @return 获取购物车商品集合
     */
    public List<GoodsInfo> getShopCartList(){
        List<GoodsInfo> goodsInfoArrayList = new ArrayList<>();
        GoodsFragment goodsFragment = businessActivity.getGoodsFragment();
        if (goodsFragment!=null){
            ArrayList<GoodsInfo> goodsInfoList = goodsFragment.goodsAdapter.getData();
            for (int i = 0; i < goodsInfoList.size(); i++) {
                GoodsInfo goodsInfo = goodsInfoList.get(i);
                if (goodsInfo.getCount()>0){
                    goodsInfoArrayList.add(goodsInfo);//内存溢出
                }
            }
            return goodsInfoArrayList;
        }
        return null;
    }

    public void clearShopCart() {
        //1.右侧商品列表集合,清空数量
        clearGoodsAdapter();
        //2.左侧商品分类集合,清空数量
        clearGoodsTypeAdapter();
        //3.清空购物车集合数量
        clearShopCartList();
        //4.更新购物车气泡&金额
        refreshShopCart();
        //5.弹回购物车对话框
        businessActivity.dismissSheetViewDialog();
    }

    private void clearShopCartList() {
        List<GoodsInfo> shopCartList = getShopCartList();
        for (int i = 0; i < shopCartList.size(); i++) {
            GoodsInfo goodsInfo = shopCartList.get(i);
            if (goodsInfo.getCount()>0){
                goodsInfo.setCount(0);
            }
        }
        businessActivity.shopCartAdapter.notifyDataSetChanged();
    }

    private void clearGoodsTypeAdapter() {
        GoodsFragment goodsFragment = businessActivity.getGoodsFragment();
        if (goodsFragment!=null){
            List<GoodsTypeInfo> goodsTypeInfoList = goodsFragment.goodsTypeAdapter.getData();
            for (int i = 0; i < goodsTypeInfoList.size(); i++) {
                GoodsTypeInfo goodsTypeInfo = goodsTypeInfoList.get(i);
                if (goodsTypeInfo.getCount()>0){
                    goodsTypeInfo.setCount(0);
                }
            }
            goodsFragment.goodsTypeAdapter.notifyDataSetChanged();
        }
    }

    private void clearGoodsAdapter() {
        GoodsFragment goodsFragment = businessActivity.getGoodsFragment();
        if (goodsFragment!=null){
            ArrayList<GoodsInfo> goodsInfoList = goodsFragment.goodsAdapter.getData();
            for (int i = 0; i < goodsInfoList.size(); i++) {
                GoodsInfo goodsInfo = goodsInfoList.get(i);
                if (goodsInfo.getCount()>0){
                    goodsInfo.setCount(0);
                }
            }
            goodsFragment.goodsAdapter.notifyDataSetChanged();
        }
    }
}