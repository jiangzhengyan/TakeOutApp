package com.xiaozhang.takeout.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xiaozhang.takeout.model.bean.Seller;
import com.xiaozhang.takeout.ui.fragment.BaseFragment;
import com.xiaozhang.takeout.ui.fragment.GoodsFragment;
import com.xiaozhang.takeout.ui.fragment.SellerFragment;
import com.xiaozhang.takeout.ui.fragment.SuggestFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiao on 2018/3/6.
 */
public class BusinessFragmentPagerAdapter extends FragmentPagerAdapter {
    private Seller seller;
    private String[] stringArray;
    public List<BaseFragment> fragmentList = new ArrayList<>();

    public BusinessFragmentPagerAdapter(FragmentManager fm, String[] stringArray, Seller seller) {
        super(fm);
        this.stringArray = stringArray;
        this.seller = seller;
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment baseFragment = null;
        switch (position){
            case 0:
                baseFragment = new GoodsFragment();
                break;
            case 1:
                baseFragment = new SuggestFragment();
                break;
            case 2:
                baseFragment = new SellerFragment();
                break;
        }
        //用bundle将序列化后的seller对象封装起来
        Bundle bundle = new Bundle();
        bundle.putSerializable("seller",seller);
        //将封装了seller的bundle对象传递给baseFragment
        baseFragment.setArguments(bundle);

        if (!fragmentList.contains(baseFragment)){
            fragmentList.add(baseFragment);
        }

        return baseFragment;
    }

    @Override
    public int getCount() {
        return stringArray.length;
    }
    //如果使用过的是PagerAdapter,必须重写以下2个方法
    //instantiateItem 向viewPager中加一个页面方法,
    // 从viewPager中删除一个界面的方法destroyItem

    //FragmentPagerAdapter
    //将getItem方法返回的Fragment中oncreatView方法里面返回的view,添加至viewPager内部


    @Override
    public CharSequence getPageTitle(int position) {
        return stringArray[position];
    }
}
