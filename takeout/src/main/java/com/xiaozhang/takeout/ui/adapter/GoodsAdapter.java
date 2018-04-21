package com.xiaozhang.takeout.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.global.MyApplication;
import com.xiaozhang.takeout.model.bean.GoodsInfo;
import com.xiaozhang.takeout.ui.activity.BusinessActivity;
import com.xiaozhang.takeout.ui.fragment.GoodsFragment;
import com.xiaozhang.takeout.util.Constant;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by zhangxiao on 2018/3/6.
 * 右侧商品列表数据适配器
 */
public class GoodsAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private GoodsFragment goodsFragment;
    private Activity activity;
    private ArrayList<GoodsInfo> data;
    private int operate = -1;

    public GoodsAdapter(Activity activity, GoodsFragment goodsFragment) {
        this.activity = activity;
        this.goodsFragment = goodsFragment;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(activity, R.layout.item_type_header, null);
        textView.setText(data.get(position).getTypeName());
        return textView;
    }

    @Override
    public long getHeaderId(int position) {
//        if(){
//            return 1;
//        }else if{
//            return 2;
//        }else if{
//            return 3;
//        }
        //有几种返回值情况,就有几个灰色头title
        //灰色title的个数等于左侧商品分类的个数
        //第一种分类
        // 商品1  typeId == 100
        // 商品2  typeId == 100
        // 商品3  typeId == 100
        // 商品4  typeId == 100
        // 商品5  typeId == 100
        // 商品6  typeId == 100
        //第二种分类
        // 商品1  typeId == 101
        // 商品2  typeId == 101
        // 商品3  typeId == 101
        // 商品4  typeId == 101
        //第三种分类
        // 商品1  typeId == 102
        // 商品2  typeId == 102
        // 商品3  typeId == 102
        // 商品4  typeId == 102
        return data.get(position).getTypeId();
    }

    @Override
    public int getCount() {
        if (data != null && data.size() > 0) {
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //1.判断convertView是否能复用
        //2.创建ViewHolder减少findViewById
        //3.加静态
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.item_goods, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvName.setText(data.get(position).getName());
        viewHolder.tvNewprice.setText("¥"+data.get(position).getNewPrice() + "");
        viewHolder.tvOldprice.setText("¥"+data.get(position).getOldPrice() + "");
        Picasso.with(activity).load(data.get(position).getIcon()).into(viewHolder.ivIcon);

        viewHolder.setPosition(position);

        //判断position位置的商品数量是否大于0，如果大于0，让减号显示，让商品数量显示
        if (data.get(position).getCount()>0){
            viewHolder.ibMinus.setVisibility(View.VISIBLE);
            viewHolder.tvCount.setVisibility(View.VISIBLE);

            viewHolder.tvCount.setText(data.get(position).getCount()+"");
        }else{
            viewHolder.ibMinus.setVisibility(View.GONE);
            viewHolder.tvCount.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setData(ArrayList<GoodsInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public ArrayList<GoodsInfo> getData() {
        return data;
    }

    class ViewHolder {
        @InjectView(R.id.iv_icon)
        ImageView ivIcon;
        @InjectView(R.id.tv_name)
        TextView tvName;
        @InjectView(R.id.tv_zucheng)
        TextView tvZucheng;
        @InjectView(R.id.tv_yueshaoshounum)
        TextView tvYueshaoshounum;
        @InjectView(R.id.tv_newprice)
        TextView tvNewprice;
        @InjectView(R.id.tv_oldprice)
        TextView tvOldprice;
        @InjectView(R.id.ib_minus)
        ImageButton ibMinus;
        @InjectView(R.id.tv_count)
        TextView tvCount;
        @InjectView(R.id.ib_add)
        ImageButton ibAdd;
        private int position;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
        @OnClick({R.id.ib_add,R.id.ib_minus})
        public void onClick(View view){
            view.setEnabled(false);
            switch (view.getId()){
                case R.id.ib_add:
                    //添加一件商品,并且执行动画(0-1 减号出现动画+平抛动画  从1件开始叠加 平抛动画)
                    addGoods(view);
                    operate = Constant.ADD;
                    break;
                case R.id.ib_minus:
                    delectGoods(view);//有一个动画，需要耗时500毫秒，500毫秒后，商品的数量才更新为0
                    operate = Constant.DELETE;
                    break;
            }
            //获取选中的商品的分类typeId
            GoodsInfo goodsInfo = data.get(position);
            int typeId = goodsInfo.getTypeId();
            //获取商品的GoodsFragment,获取左侧数据适配器对象
            //operate用于记录到底是加1件商品，减1件商品
            //typeId用于区分到底更新那个分类的数量
            goodsFragment.goodsTypeAdapter.refreshGoodsType(operate,typeId);
            //购物车中数量更新方法
            //此行代码已经执行结束了
            ((BusinessActivity)activity).businessPresenter.refreshShopCart();//100毫秒就执行到这里了
        }

        private void delectGoods(final View view) {
            if (data.get(position).getCount() == 1 ){
                //需要执行减号滚回动画
                //旋转
                RotateAnimation rotateAnimation = new RotateAnimation(720,0,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //透明
                AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                //平移
                TranslateAnimation translateAnimation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 2,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                AnimationSet animationSet = new AnimationSet(false);
                animationSet.addAnimation(rotateAnimation);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setDuration(500);

                ibMinus.startAnimation(animationSet);

                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ibMinus.setVisibility(View.GONE);
                        tvCount.setVisibility(View.GONE);
                        //1件商品递减，则商品数量变为0
                        data.get(position).setCount(0);

                        ((BusinessActivity)activity).businessPresenter.refreshShopCart();//100毫秒就执行到这里了

                        view.setEnabled(true);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }else{
                int count = data.get(position).getCount()-1;
                data.get(position).setCount(count);
                notifyDataSetChanged();

                view.setEnabled(true);
            }

        }

        private void addGoods(View view) {
            //点中的条目，商品由0变成1，执行以下动画
            if (data.get(position).getCount() == 0){
                //旋转
                RotateAnimation rotateAnimation = new RotateAnimation(0, 720,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //透明
                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                //平移
                TranslateAnimation translateAnimation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 2, Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                AnimationSet animationSet = new AnimationSet(false);
                animationSet.addAnimation(rotateAnimation);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setDuration(500);

                ibMinus.startAnimation(animationSet);

                ibMinus.setVisibility(View.VISIBLE);
                tvCount.setVisibility(View.VISIBLE);
            }
            //获取+号所在屏幕中的位置(x,y),view就是点中的+号
            int[] sourceLocation = new int[2];
            view.getLocationInWindow(sourceLocation);
            //创建一个用于飞行ImageView图片
            ImageView imageView = new ImageView(activity);
            imageView.setBackgroundResource(R.drawable.button_add);

            //指定imageView所在屏幕的位置
            imageView.setX(sourceLocation[0]);
            imageView.setY(sourceLocation[1]- MyApplication.statusBarHeight);

            //将imageView添加在BusinessActivity中的帧布局中
            ((BusinessActivity)activity).addFlyImageView(imageView,view.getWidth(),view.getHeight());
            //获取目标位置的坐标,获取购物车在屏幕中的坐标
            int[] desLocation = ((BusinessActivity) activity).getDesLocation();
            //飞行方法
            fly(imageView,sourceLocation,desLocation,view);
            //点击+号按钮后，需要将商品数量增加一个
            int count = data.get(position).getCount()+1;
            data.get(position).setCount(count);
            notifyDataSetChanged();
        }

         private void fly(final ImageView imageView, int[] sourceLocation, int[] desLocation, final View view) {
             int startX = sourceLocation[0];//起始点的x轴坐标
             int startY = sourceLocation[1];//起始点的y轴坐标

             int endX = desLocation[0];//终止点的x轴坐标
             int endY = desLocation[1];//终止点的y轴坐标
             //x轴匀速运动 动画 匀速插值器
             TranslateAnimation translateAnimationX = new TranslateAnimation(
                     Animation.ABSOLUTE,0,Animation.ABSOLUTE,endX-startX,
                     Animation.ABSOLUTE,0,Animation.ABSOLUTE,0
             );
             translateAnimationX.setInterpolator(new LinearInterpolator());

             //y轴加速运动 动画 加速插值器
             TranslateAnimation translateAnimationY = new TranslateAnimation(
                     Animation.ABSOLUTE,0,Animation.ABSOLUTE,0,
                     Animation.ABSOLUTE,0,Animation.ABSOLUTE,endY-startY
             );
             translateAnimationY.setInterpolator(new AccelerateInterpolator());

             AnimationSet animationSet = new AnimationSet(false);
             animationSet.addAnimation(translateAnimationX);
             animationSet.addAnimation(translateAnimationY);
             animationSet.setDuration(500);

             imageView.startAnimation(animationSet);

             animationSet.setAnimationListener(new Animation.AnimationListener() {
                 @Override
                 public void onAnimationStart(Animation animation) {

                 }

                 @Override
                 public void onAnimationEnd(Animation animation) {
                    //从BusinessActivity中移除imageView
                     ((BusinessActivity)activity).removeFlyImageView(imageView);
                     view.setEnabled(true);
                 }

                 @Override
                 public void onAnimationRepeat(Animation animation) {

                 }
             });
         }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
