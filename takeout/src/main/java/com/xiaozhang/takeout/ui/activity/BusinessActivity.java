package com.xiaozhang.takeout.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.model.bean.GoodsInfo;
import com.xiaozhang.takeout.model.bean.Seller;
import com.xiaozhang.takeout.presenter.BusinessPresenter;
import com.xiaozhang.takeout.ui.adapter.BusinessFragmentPagerAdapter;
import com.xiaozhang.takeout.ui.adapter.ShopCartAdapter;
import com.xiaozhang.takeout.ui.fragment.BaseFragment;
import com.xiaozhang.takeout.ui.fragment.GoodsFragment;
import com.xiaozhang.takeout.util.CountPriceFormater;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhangxiao on 2018/3/6.
 */
public class BusinessActivity extends BaseActivity {

    @InjectView(R.id.ib_back)
    ImageButton ibBack;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.ib_menu)
    ImageButton ibMenu;
    @InjectView(R.id.tabs)
    TabLayout tabs;
    @InjectView(R.id.vp)
    ViewPager vp;
    @InjectView(R.id.bottomSheetLayout)
    BottomSheetLayout bottomSheetLayout;
    @InjectView(R.id.imgCart)
    ImageView imgCart;
    @InjectView(R.id.tvSelectNum)
    TextView tvSelectNum;
    @InjectView(R.id.tvCountPrice)
    TextView tvCountPrice;
    @InjectView(R.id.tvDeliveryFee)
    TextView tvDeliveryFee;
    @InjectView(R.id.tvSendPrice)
    TextView tvSendPrice;
    @InjectView(R.id.tvSubmit)
    TextView tvSubmit;
    @InjectView(R.id.bottom)
    LinearLayout bottom;
    @InjectView(R.id.fl_Container)
    FrameLayout flContainer;
    private Seller seller;

    private String[] stringArray = new String[]{"商品","评价","商家"};
    public BusinessPresenter businessPresenter;
    private BusinessFragmentPagerAdapter businessFragmentPagerAdapter;
    private View sheetView;
    public ShopCartAdapter shopCartAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seller = (Seller) getIntent().getSerializableExtra("seller");
        setContentView(R.layout.activity_bussiness);
        ButterKnife.inject(this);

        initTab();
        initViewPager();
        tabs.setupWithViewPager(vp);
        //设置商家名称
        tvTitle.setText(seller.getName());
        //设置最低起送价
        float floatSendPrice = Float.parseFloat(seller.getSendPrice());

        tvSendPrice.setText("起送价:"+CountPriceFormater.format(floatSendPrice));
        //运送费
        float floatDeliveryFee = Float.parseFloat(seller.getDeliveryFee());
        tvDeliveryFee.setText("配送费:"+CountPriceFormater.format(floatDeliveryFee));

        businessPresenter = new BusinessPresenter(this);
    }

    private void initViewPager() {
        businessFragmentPagerAdapter = new BusinessFragmentPagerAdapter(getSupportFragmentManager(), stringArray,seller);
        vp.setAdapter(businessFragmentPagerAdapter);
    }

    /**
     * 添加tabLayout中的选项卡
     */
    private void initTab() {
        for (int i = 0; i < stringArray.length; i++) {
            tabs.addTab(tabs.newTab().setText(stringArray[i]));
        }
    }

    public void addFlyImageView(ImageView imageView, int width, int height) {
        if(imageView!=null){
            flContainer.addView(imageView,width,height);
        }
    }

    /**
     * @return 返回购物车所在屏幕中的坐标
     */
    public int[] getDesLocation() {
        int[] desLocation = new int[2];
        imgCart.getLocationInWindow(desLocation);
        return desLocation;
    }

    public void removeFlyImageView(ImageView imageView) {
        if(imageView!=null){
            flContainer.removeView(imageView);
        }
    }

    /**
     * @return  返回商品对应的GoodsFragment对象
     */
    public GoodsFragment getGoodsFragment() {
        List<BaseFragment> fragmentList = businessFragmentPagerAdapter.fragmentList;
        if (fragmentList!=null && fragmentList.size()>0){
            GoodsFragment goodsFragment = (GoodsFragment) fragmentList.get(0);
            return goodsFragment;
        }
        return null;
    }

    /**
     * @param totalCount    显示的商品数量
     * @param totalPrice    显示的商品总金额
     */
    public void updateShopCart(int totalCount, float totalPrice) {
        if (totalCount>0){
            //显示气泡的控件
            tvSelectNum.setVisibility(View.VISIBLE);
            //控件显示气泡数量
            tvSelectNum.setText(totalCount+"");
            //显示购买商品的总金额
            tvCountPrice.setText(CountPriceFormater.format(totalPrice));
        }else{
            tvSelectNum.setVisibility(View.GONE);
            tvCountPrice.setText(CountPriceFormater.format(0.0f));
        }
//        起送价<=总金额	下单  显示下单的按钮
//        起送价>总金额	购买的商品不足以达到配送要求
        String sendPrice = seller.getSendPrice();
        float floatSendPrice = Float.parseFloat(sendPrice);
        if (floatSendPrice<totalPrice){
            tvSubmit.setVisibility(View.VISIBLE);
            tvSendPrice.setVisibility(View.GONE);
        }else{
            tvSubmit.setVisibility(View.GONE);
            tvSendPrice.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.bottom,R.id.tvSubmit})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bottom:
                //控制bottomSheetLayout内部对话框显示隐藏
                //bottomSheetLayout.showWithSheetView(view);显示bottomSheetLayout中的对话框
                //bottomSheetLayout.dismissSheet();隐藏bottomSheetLayout中的对话框

                //生成bottomSheetLayout中显示的view对象
                if (sheetView == null){
                    sheetView = onCreateSheetView();
                }
                if (bottomSheetLayout.isSheetShowing()){
                    bottomSheetLayout.dismissSheet();
                }else{
                    //显示购物车列表
                    List<GoodsInfo> shopCartList = businessPresenter.getShopCartList();
                    if (shopCartList!=null && shopCartList.size()>0){
                        shopCartAdapter.setData(shopCartList);
                        bottomSheetLayout.showWithSheetView(sheetView);
                    }
                }
                break;
            case R.id.tvSubmit:
                Intent intent = new Intent(BusinessActivity.this, ConfirmOrderActivity.class);
                //因为在ConfirmOrderActivity页面中需要显示购买的商品,和总金额,所以需要传递运费和购买商品集合到下一个界面
                intent.putExtra("seller",seller);//用于计算运费
                intent.putExtra("shopCartList", (Serializable) businessPresenter.getShopCartList());
                startActivity(intent);
                break;
        }
    }

    private View onCreateSheetView() {
        View view = View.inflate(this,R.layout.cart_list,null);
        TextView tvClear = (TextView) view.findViewById(R.id.tvClear);
        RecyclerView rvCart = (RecyclerView) view.findViewById(R.id.rvCart);

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BusinessActivity.this);
                builder.setTitle("是否清空购物车?");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //购物车清空
                        businessPresenter.clearShopCart();
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        shopCartAdapter = new ShopCartAdapter(this);
        rvCart.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvCart.setAdapter(shopCartAdapter);

        List<GoodsInfo> shopCartList = businessPresenter.getShopCartList();
        shopCartAdapter.setData(shopCartList);
        return view;
    }

    public void dismissSheetViewDialog() {
        if (bottomSheetLayout.isSheetShowing()){
            bottomSheetLayout.dismissSheet();
        }
    }
}
