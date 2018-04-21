package com.xiaozhang.takeout.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.global.MyApplication;
import com.xiaozhang.takeout.presenter.OrderPresenter;
import com.xiaozhang.takeout.ui.adapter.OrderListAdpater;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhangxiao on 2018/3/5.
 */
public class OrderFragment extends BaseFragment {
    @InjectView(R.id.rv_order_list)
    RecyclerView rvOrderList;
    @InjectView(R.id.srl_order)
    SwipeRefreshLayout srlOrder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("","===========OrderFragment onCreateView ");
        View view = View.inflate(getActivity(), R.layout.fragment_order, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("","===========OrderFragment onActivityCreated");
        OrderListAdpater orderListAdpater = new OrderListAdpater(getActivity());
        rvOrderList.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        rvOrderList.setAdapter(orderListAdpater);

        OrderPresenter orderPresenter = new OrderPresenter(orderListAdpater);
        orderPresenter.getOrderData(MyApplication.userId);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
