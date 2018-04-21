package com.xiaozhang.takeout.ui.fragment;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.presenter.HomePresenter;
import com.xiaozhang.takeout.ui.adapter.HomeAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhangxiao on 2018/3/5.
 */
public class HomeFragment extends BaseFragment {
    @InjectView(R.id.rv_home)
    RecyclerView rvHome;
    @InjectView(R.id.home_tv_address)
    TextView homeTvAddress;
    @InjectView(R.id.ll_title_search)
    LinearLayout llTitleSearch;
    @InjectView(R.id.ll_title_container)
    LinearLayout llTitleContainer;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private int sumY = 0;

    //指定开始变化色值
    //渐变规则
    //指定结束变化色值
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sumY = 0;
        View view = View.inflate(getActivity(), R.layout.fragment_home, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        HomeAdapter homeAdapter = new HomeAdapter(getActivity());
        //设置recyclerview模式方向  列表 竖直
        rvHome.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        rvHome.setAdapter(homeAdapter);

        HomePresenter homePresenter = new HomePresenter(homeAdapter);
        homePresenter.getHomeData("","");

        rvHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //滚动状态发生改变的方法
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //滚动过程中方法
                //在滚动过程中,需要修改顶部悬浮title颜色
                //记录y轴移动距离的总和
                sumY += dy;
                int bgColor = 0X553190E8;
                if (sumY<=0){
                    //开始颜色
                    bgColor = 0X553190E8;
                }else if (sumY>=300){
                    //最终色值
                    bgColor = 0XFF3190E8;
                }else{
                    //颜色渐变过程(开始颜色,最终色值,变化规则)
                    bgColor = (int) argbEvaluator.evaluate(sumY / 300.0f, 0X553190E8, 0XFF3190E8);
                }
                llTitleContainer.setBackgroundColor(bgColor);
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
