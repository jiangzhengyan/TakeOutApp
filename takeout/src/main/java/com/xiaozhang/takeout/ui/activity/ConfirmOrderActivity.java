package com.xiaozhang.takeout.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.global.MyApplication;
import com.xiaozhang.takeout.model.bean.GoodsInfo;
import com.xiaozhang.takeout.model.bean.ReceiptAddressBean;
import com.xiaozhang.takeout.model.bean.Seller;
import com.xiaozhang.takeout.model.dao.ReceiptAddressDao;
import com.xiaozhang.takeout.util.CountPriceFormater;

import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhangxiao on 2018/3/23.
 */
public class ConfirmOrderActivity extends BaseActivity {
    @InjectView(R.id.ib_back)
    ImageButton ibBack;
    @InjectView(R.id.tv_login)
    TextView tvLogin;
    @InjectView(R.id.iv_location)
    ImageView ivLocation;
    @InjectView(R.id.tv_hint_select_receipt_address)
    TextView tvHintSelectReceiptAddress;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_sex)
    TextView tvSex;
    @InjectView(R.id.tv_phone)
    TextView tvPhone;
    @InjectView(R.id.tv_label)
    TextView tvLabel;
    @InjectView(R.id.tv_address)
    TextView tvAddress;
    @InjectView(R.id.ll_receipt_address)
    LinearLayout llReceiptAddress;
    @InjectView(R.id.iv_arrow)
    ImageView ivArrow;
    @InjectView(R.id.rl_location)
    RelativeLayout rlLocation;
    @InjectView(R.id.iv_icon)
    ImageView ivIcon;
    @InjectView(R.id.tv_seller_name)
    TextView tvSellerName;
    @InjectView(R.id.ll_select_goods)
    LinearLayout llSelectGoods;
    @InjectView(R.id.tv_deliveryFee)
    TextView tvDeliveryFee;
    @InjectView(R.id.tv_CountPrice)
    TextView tvCountPrice;
    @InjectView(R.id.tvSubmit)
    TextView tvSubmit;
    private List<GoodsInfo> goodsInfoList;
    private Seller seller;
    private String[] addressLabels;
    private int[] bgLabels;
    private ReceiptAddressDao receiptAddressDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        ButterKnife.inject(this);
        addressLabels = new String[]{"家", "公司", "学校"};
        //家  橙色
        //公司 蓝色
        //学校   绿色
        bgLabels = new int[]{
                Color.parseColor("#fc7251"),//家  橙色
                Color.parseColor("#468ade"),//公司 蓝色
                Color.parseColor("#02c14b"),//学校   绿色
        };
        
        seller = (Seller) getIntent().getSerializableExtra("seller");
        goodsInfoList = (List<GoodsInfo>) getIntent().getSerializableExtra("shopCartList");

        String deliveryFee = seller.getDeliveryFee();
        float floatDeliveryFee = Float.parseFloat(deliveryFee);
        tvDeliveryFee.setText(CountPriceFormater.format(floatDeliveryFee));

        //给确认订单界面的线性布局添加多件商品,让其列表展示
        createShopCartListView();
        //计算需要支付的总金额 = 商品总价+运费
        float totalPrice = 0.0f;
        for (int i = 0; i < goodsInfoList.size(); i++) {
            GoodsInfo goodsInfo = goodsInfoList.get(i);
            totalPrice += goodsInfo.getCount() * goodsInfo.getNewPrice();
        }
        totalPrice += floatDeliveryFee;
        tvCountPrice.setText(CountPriceFormater.format(totalPrice));

        //数据库查询(查询当前登录用户的默认选中地址)
        receiptAddressDao = new ReceiptAddressDao(this);
        ReceiptAddressBean userSelectAddress
                = receiptAddressDao.getUserSelectAddress(MyApplication.userId, 1);
        if (userSelectAddress!=null){
            showReceiptAddress(userSelectAddress);
        }
    }

    private void createShopCartListView() {
        llSelectGoods.removeAllViews();
        for (int i = 0; i < goodsInfoList.size(); i++) {
            GoodsInfo goodsInfo = goodsInfoList.get(i);
            View view = View.inflate(this,R.layout.item_confirm_order_goods,null);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            TextView tvCount = (TextView)view.findViewById(R.id.tv_count);
            TextView tvPrice = (TextView)view.findViewById(R.id.tv_price);

            tvName.setText(goodsInfo.getName());
            tvCount.setText(goodsInfo.getCount()+"");//resId
            float totalPrice = goodsInfo.getCount() * goodsInfo.getNewPrice();
            tvPrice.setText(CountPriceFormater.format(totalPrice));

            llSelectGoods.addView(view);
        }
    }
    @OnClick({R.id.tvSubmit,R.id.rl_location})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tvSubmit:
                Intent intent = new Intent(this, PayOnlineActivity.class);
                //因为在ConfirmOrderActivity页面中需要显示购买的商品,和总金额,所以需要传递运费和购买商品集合到下一个界面
                intent.putExtra("seller",seller);//用于计算运费
                intent.putExtra("shopCartList", (Serializable) goodsInfoList);
                startActivity(intent);
                break;
            case R.id.rl_location:
                Intent intentAddress = new Intent(this, AddressListActivity.class);
                startActivityForResult(intentAddress,100);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null && requestCode == 100 && resultCode == 101){
            ReceiptAddressBean receiptAddressBean
                    = (ReceiptAddressBean) data.getSerializableExtra("receiptAddress");
            if (receiptAddressBean!=null){
                showReceiptAddress(receiptAddressBean);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showReceiptAddress(ReceiptAddressBean receiptAddressBean) {
        tvName.setText(receiptAddressBean.getName());
        tvSex.setText(receiptAddressBean.getSex());

        if (!TextUtils.isEmpty(receiptAddressBean.getPhone()) && !TextUtils.isEmpty(receiptAddressBean.getPhoneOther())) {
            tvPhone.setText(receiptAddressBean.getPhone() + "," + receiptAddressBean.getPhoneOther());
        }
        if (!TextUtils.isEmpty(receiptAddressBean.getPhone()) && TextUtils.isEmpty(receiptAddressBean.getPhoneOther())) {
            tvPhone.setText(receiptAddressBean.getPhone());
        }

        tvAddress.setText(receiptAddressBean.getReceiptAddress() + receiptAddressBean.getDetailAddress());
        tvLabel.setText(receiptAddressBean.getLabel());
        int index = getIndex(receiptAddressBean.getLabel());
        tvLabel.setBackgroundColor(bgLabels[index]);
        tvLabel.setVisibility(View.VISIBLE);
    }

    private int getIndex(String label) {
        int index = -1;
        for (int i = 0; i < addressLabels.length; i++) {
            if (label.equals(addressLabels[i])){
                index = i;
                break;
            }
        }
        return index;
    }
}
