package com.xiaozhang.takeout.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.model.bean.HomeInfo;
import com.xiaozhang.takeout.model.bean.Promotion;
import com.xiaozhang.takeout.model.bean.Seller;
import com.xiaozhang.takeout.ui.activity.BusinessActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
/**
 * Created by zhangxiao on 2018/3/5.
 */
public class HomeAdapter extends RecyclerView.Adapter {
    private static final int ITEM_HEADER = 0;//header条目类型的状态码
    private static final int ITEM_SELLER = 1;//一般商家条目类型的状态码
    private static final int ITEM_DIV = 2;//分割线条目类型的状态码

    private Activity activity;
    private HomeInfo data;

    public HomeAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //返回包含来的view的ViewHolder
        //viewType就是在getItemViewType(position)返回的状态码中的某一个
        if (viewType == ITEM_HEADER) {
            //准备header布局文件转换成的view对象
            View view = View.inflate(activity, R.layout.item_title, null);
            HeaderHolder headerHolder = new HeaderHolder(view);
            return headerHolder;
        } else if (viewType == ITEM_SELLER) {
            View view = View.inflate(activity, R.layout.item_seller, null);
            SellerHolder sellerHolder = new SellerHolder(view);
            return sellerHolder;
        } else {
            View view = View.inflate(activity, R.layout.item_division, null);
            DivHolder divHolder = new DivHolder(view);
            return divHolder;
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.slider)
        SliderLayout slider;
        //在创建holder的时候,必须传递一个view对象,此view对象就是一个条目的布局转换成view对象
        public HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);

            //data对象中header里面
            int picCount = data.getHead().getPromotionList().size();
            for (int i = 0; i < picCount; i++) {
                Promotion promotion = data.getHead().getPromotionList().get(i);
                //显示图片和描述文本的控件
                TextSliderView textSliderView = new TextSliderView(activity);
                        textSliderView.description(promotion.getInfo())//指定描述文本
                        .image(promotion.getPic())//指定图片链接地址
                                .setScaleType(BaseSliderView.ScaleType.Fit);//指定图片展示方式
                //将textSliderView放置到容器中
                slider.addSlider(textSliderView);
            }
            //内部图片切换的时候,使用的动画类型
            //slider.setPresetTransformer(SliderLayout.Transformer.RotateDown);
            //设置可以轮播容器内部点的所在位置
            slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            //文本描述出现的时候,指定的动画
            slider.setCustomAnimation(new DescriptionAnimation());
            //动画的执行时长
            slider.setDuration(4000);
        }
    }

    class SellerHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tvCount)
        TextView tvCount;
        @InjectView(R.id.tv_title)
        TextView tvTitle;
        @InjectView(R.id.ratingBar)
        RatingBar ratingBar;
        private int position;

        //商家的holder
        public SellerHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取点中索引位置商家的数据
                    Seller seller = data.getBody().get(position).getSeller();
                    Intent intent = new Intent(activity, BusinessActivity.class);
                    //如果页面间传递对象,则此对象所在的类需要实现序列化接口
                    intent.putExtra("seller",seller);
                    activity.startActivity(intent);
                }
            });
        }

        /**
         * @param position  用于区分点中那个商家条目,点中商家条目使用集合的索引值
         */
        public void setPosition(int position) {
            this.position = position;
        }
    }

    class DivHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tv_division_title)
        TextView tvDivisionTitle;
        @InjectView(R.id.tv1)
        TextView tv1;
        @InjectView(R.id.tv2)
        TextView tv2;
        @InjectView(R.id.tv3)
        TextView tv3;
        @InjectView(R.id.tv4)
        TextView tv4;
        @InjectView(R.id.tv5)
        TextView tv5;
        @InjectView(R.id.tv6)
        TextView tv6;

        //分割线的holder
        public DivHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //给ViewHolder绑定数据
//        if (holder instanceof SellerHolder) 根据holder到底是那个类的对象判断
//        if (holder instanceof DivHolder) 根据holder到底是那个类的对象判断
//        if (holder instanceof HeaderHolder) 根据holder到底是那个类的对象判断

        //根据不同的索引,给不同的holder绑定不一样的数据
        if (position == 0) {

        } else if (data.getBody().get(position - 1).type == 0) {
            setSellerHolderData(holder, position - 1);
            ((SellerHolder) holder).setPosition(position - 1);
        } else {
            //给DivHolder中的多个TextView赋值
            setHolderData(holder, position - 1);
        }
    }

    //设置商家holder数据
    private void setSellerHolderData(RecyclerView.ViewHolder holder, int position) {
        ((SellerHolder) holder).tvTitle.setText(data.getBody().get(position).getSeller().getName());
    }

    //设置分割线条目的holder
    private void setHolderData(RecyclerView.ViewHolder holder, int position) {
        ((DivHolder) holder).tv1.setText(data.getBody().get(position).getRecommendInfos().get(0));
        ((DivHolder) holder).tv2.setText(data.getBody().get(position).getRecommendInfos().get(1));
        ((DivHolder) holder).tv3.setText(data.getBody().get(position).getRecommendInfos().get(2));
        ((DivHolder) holder).tv4.setText(data.getBody().get(position).getRecommendInfos().get(3));
        ((DivHolder) holder).tv5.setText(data.getBody().get(position).getRecommendInfos().get(4));
        ((DivHolder) holder).tv6.setText(data.getBody().get(position).getRecommendInfos().get(5));
    }

    @Override
    public int getItemCount() {
        if (data != null && data.getBody() != null && data.getHead() != null
                && data.getBody().size() > 0) {
            //返回列表总数
            return data.getBody().size() + 1;
        }
        return 0;
    }

    public void setData(HomeInfo data) {
        this.data = data;
        notifyDataSetChanged();
    }
    //判断position位置指向的条目类型
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            //索引位置为0的时候,header条目
            return ITEM_HEADER;
        } else if (data.getBody().get(position - 1).type == 0) {
            //服务器端传递type为0的时候,一般商家条目
            return ITEM_SELLER;
        } else {
            //服务器端传递type为1的时候,分割线条目
            return ITEM_DIV;
        }
    }
}
