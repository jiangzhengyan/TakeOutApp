package com.xiaozhang.takeout.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.model.bean.GoodsInfo;
import com.xiaozhang.takeout.model.bean.GoodsTypeInfo;
import com.xiaozhang.takeout.model.bean.Seller;
import com.xiaozhang.takeout.presenter.GoodsPresenter;
import com.xiaozhang.takeout.ui.adapter.GoodsAdapter;
import com.xiaozhang.takeout.ui.adapter.GoodsTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
/**
 * Created by zhangxiao on 2018/3/6.
 */
public class GoodsFragment extends BaseFragment {
    @InjectView(R.id.rv_goods_type)
    RecyclerView rvGoodsType;

    @InjectView(R.id.slhlv)
    StickyListHeadersListView slhlv;
    private Seller seller;
    public GoodsAdapter goodsAdapter;
    public GoodsTypeAdapter goodsTypeAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        seller = (Seller) arguments.getSerializable("seller");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_goods, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //Fragment生命周期图
        goodsTypeAdapter = new GoodsTypeAdapter(getActivity(),this);
        rvGoodsType.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        rvGoodsType.setAdapter(goodsTypeAdapter);

        //右侧列表的数据适配器
        goodsAdapter = new GoodsAdapter(getActivity(),this);
        slhlv.setAdapter(goodsAdapter);

        slhlv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //获取右侧列表第1个位置可见条目的GoodsInfo对象,获取其typeId,用此TypeId和左侧选中分类的id比对,
                //如果一致,则左侧不用动
                //如果不一致,则左侧需要滚动至typeId和id一致的那个条目
                ArrayList<GoodsInfo> goodsInfoList = goodsAdapter.getData();
                List<GoodsTypeInfo> goodsTypeList = goodsTypeAdapter.getData();
                if (goodsInfoList!=null && goodsInfoList.size()>0 && goodsTypeList!=null && goodsTypeList.size()>0){
                    GoodsInfo goodsInfo = goodsInfoList.get(firstVisibleItem);
                    int typeId = goodsInfo.getTypeId();
                    //和左侧列表当前选中的条目的id比对是否一致
                    //通过GoodsTypeAdapter中记录的currentPosition获取左侧选中分类对象的id
                    GoodsTypeInfo goodsTypeInfo = goodsTypeList.get(goodsTypeAdapter.currentPosition);
                    int id = goodsTypeInfo.getId();

                    if (typeId != id){
                        for (int i = 0; i < goodsTypeList.size(); i++) {
                            if (goodsTypeList.get(i).getId() == typeId){
                                //recyclerView数据适配器滚动  smoothToPosition
                                rvGoodsType.smoothScrollToPosition(i);
                                //将需要滚动到的条目,指向当前选中条目
                                goodsTypeAdapter.currentPosition = i;
                                //刷新rvGoodsType数据适配器
                                goodsTypeAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }
        });
        //请求网络,将数据放置在数据适配器上
        GoodsPresenter goodsPresenter = new GoodsPresenter(goodsTypeAdapter,seller, goodsAdapter);
        goodsPresenter.getBusinessData(seller.getId());

        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * @param id    左侧列表条目点中分类对象唯一性id
     *              此id用于索引右侧列表的数据集合
     */
    public void switchTypeId(int id) {
        //右侧列表的数据集合,就是商品列表集合
        ArrayList<GoodsInfo> data = goodsAdapter.getData();
        if (data!=null && data.size()>0){
            for (int i = 0; i < data.size(); i++) {
                if (id == data.get(i).getTypeId()){
                    slhlv.setSelection(i);
                    break;//找到第一个id一致的商品后,就需要跳出循环,否则就会让listView指向分类的最后一件商品
                }
            }
        }
    }
}
