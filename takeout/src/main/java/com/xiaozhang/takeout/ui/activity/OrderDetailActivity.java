package com.xiaozhang.takeout.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.observer.OrderObserver;
import com.xiaozhang.takeout.util.Constant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhangxiao on 2018/3/23.
 */
public class OrderDetailActivity extends BaseActivity implements Observer{
    @InjectView(R.id.iv_order_detail_back)
    ImageView ivOrderDetailBack;
    @InjectView(R.id.tv_seller_name)
    TextView tvSellerName;
    @InjectView(R.id.tv_order_detail_time)
    TextView tvOrderDetailTime;
    @InjectView(R.id.map)
    MapView map;
    @InjectView(R.id.ll_order_detail_type_container)
    LinearLayout llOrderDetailTypeContainer;
    @InjectView(R.id.ll_order_detail_type_point_container)
    LinearLayout llOrderDetailTypePointContainer;
    @InjectView(R.id.iv_call_phone)
    ImageView iv_call_phone;
    private int index = -1;
    private AMap aMap;
    private LatLng latlngBuyer;
    private LatLng latlngSeller;
    private LatLng riderPos;
    private Marker markerRider;
    private List<LatLng> riderPosList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.inject(this);
        OrderObserver.getInstance().addObserver(this);

        String orderId = getIntent().getStringExtra("orderId");
        String type = getIntent().getStringExtra("type");

        getIndex(type);
        ChangeUI();

        map.onCreate(savedInstanceState);// 此方法必须重写
        aMap = map.getMap();
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @OnClick({R.id.iv_call_phone})
    public void detailClick(View v
    ){
        switch (v.getId()) {
            case R.id.iv_call_phone:
                //打电话
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "17600908294"));
                startActivity(intent);
                break;

        }
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    private void ChangeUI() {
        //获取需要显示状态线性布局孩子节点个数,先将所有的孩子节点都设置为黑色
        //根据type的值,修改type指向状态孩子节点的颜色
        for (int i = 0; i < llOrderDetailTypeContainer.getChildCount(); i++) {
            TextView textView = (TextView) llOrderDetailTypeContainer.getChildAt(i);
            ImageView imageView = (ImageView) llOrderDetailTypePointContainer.getChildAt(i);

            textView.setTextColor(Color.GRAY);
            imageView.setImageResource(R.drawable.order_time_node_normal);
        }
        //根据type的状态码返回需要修改孩子节点索引

        if (index != -1){
            TextView textView = (TextView) llOrderDetailTypeContainer.getChildAt(index);
            ImageView imageView = (ImageView) llOrderDetailTypePointContainer.getChildAt(index);

            textView.setTextColor(Color.BLUE);
            imageView.setImageResource(R.drawable.order_time_node_disabled);
        }
    }

    private void getIndex(String type) {
        switch (type){
            case OrderObserver.ORDERTYPE_SUBMIT:
                index = 0;
                break;
            case OrderObserver.ORDERTYPE_RECEIVEORDER:
                index = 1;
                break;
            case OrderObserver.ORDERTYPE_DISTRIBUTION:
                index = 2;
                break;
            case OrderObserver.ORDERTYPE_SERVED:
                index = 3;
                break;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        HashMap<String, String> map = (HashMap<String, String>) arg;
        String orderId = map.get("orderId");
        String type = map.get("type");
        getIndex(type);
        ChangeUI();

        switch (type){
            case OrderObserver.ORDERTYPE_DISTRIBUTION:
                //处于配送中的状态,则需要显示地图
                initMap();
                break;
            case OrderObserver.ORDERTYPE_DISTRIBUTION_RIDER_RECEIVE:
                //骑手接单,显示骑手位置
                initRider(arg);
                break;
            case OrderObserver.ORDERTYPE_DISTRIBUTION_RIDER_TAKE_MEAL:
            case OrderObserver.ORDERTYPE_DISTRIBUTION_RIDER_GIVE_MEAL:
                changeRider(arg);
                break;

        }
    }
    private void changeRider(Object data) {
        HashMap<String, String> hashMap = (HashMap<String, String>) data;

        String type = hashMap.get("type");

        String lat = hashMap.get(Constant.LAT);
        String lng = hashMap.get(Constant.LNG);

        //获取经纬度信息,定位骑手的位置
        LatLng currentPos=new LatLng(Double.valueOf(lat),Double.valueOf(lng));
        //将经纬度添加在经纬度集合中
        riderPosList.add(currentPos);
        //设置骑手的所在位置
        markerRider.setPosition(currentPos);
        //地图定位焦点
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(currentPos));

        String info="";
        DecimalFormat format=new DecimalFormat(".00");

        switch (type) {
            case OrderObserver.ORDERTYPE_DISTRIBUTION_RIDER_TAKE_MEAL:
                // 取餐,距离卖家的距离
                float ds = AMapUtils.calculateLineDistance(currentPos, latlngSeller);
                info="距离商家"+format.format(ds)+"米";
                break;
            case OrderObserver.ORDERTYPE_DISTRIBUTION_RIDER_GIVE_MEAL:
                // 送餐,距离买家的距离
                float db = AMapUtils.calculateLineDistance(currentPos, latlngBuyer);
                info="距离买家"+format.format(db)+"米";
                break;
        }

        markerRider.setSnippet(info);
        markerRider.showInfoWindow();
        //参数一:当前所处位置
        //参数二:历史的点
        drawLine(currentPos,riderPosList.get(riderPosList.size()-2));
    }

    private void drawLine(LatLng currentPos, LatLng pos) {
        aMap.addPolyline(new PolylineOptions().add(pos,currentPos).width(2).color(Color.GREEN));
    }

    private void initRider(Object object) {
        riderPosList.clear();
        HashMap<String, String> hashMap = (HashMap<String, String>) object;
        String lat = hashMap.get(Constant.LAT);
        String lng = hashMap.get(Constant.LNG);

        if(TextUtils.isEmpty(lat)|| TextUtils.isEmpty(lng)){
            return;
        }
        riderPos = new LatLng(Double.valueOf(lat),Double.valueOf(lng));
        //初始化图片
        ImageView markerRiderIcon = new ImageView(this);
        markerRiderIcon.setImageResource(R.drawable.order_rider_icon);
        //指定骑手所在的经纬度，以及在地图的中心点显示
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(riderPos));
        //地图缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        //骑手图片的显示

        //指定锚点，以及描述文本
        markerRider = aMap.addMarker(new MarkerOptions().anchor(0.5f,1).position(riderPos));
        markerRider.setSnippet("骑手已接单");
        //显示骑手
        markerRider.showInfoWindow();
        markerRider.setIcon(BitmapDescriptorFactory.fromView(markerRiderIcon));

        //记录点的位置
        riderPosList.add(riderPos);
    }

    private void initMap() {
        //显示地图
        map.setVisibility(View.VISIBLE);
        //将地图的缩放级别变为13
        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));

        // 添加买家marker
        latlngBuyer = new LatLng(40.100519, 116.365828);
        //将地图中心位置指向latlngBuyer经纬度坐标
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latlngBuyer));

        //在地图上悬浮的点
        Marker markerLatlngBuye=aMap.addMarker(new MarkerOptions().anchor(0.5f,1).position(latlngBuyer));
        ImageView markerBuyerIcon = new ImageView(this);
        markerBuyerIcon.setImageResource(R.drawable.order_buyer_icon);
        markerLatlngBuye.setIcon(BitmapDescriptorFactory.fromView(markerBuyerIcon));

        // 添加卖家marker
        latlngSeller = new LatLng(40.060244, 116.343513);

        Marker markerLatlngSeller=aMap.addMarker(new MarkerOptions().anchor(0.5f,1).position(latlngSeller));
        ImageView markerSellerIcon = new ImageView(this);
        markerSellerIcon.setImageResource(R.drawable.order_seller_icon);
        markerLatlngSeller.setIcon(BitmapDescriptorFactory.fromView(markerSellerIcon));
    }
}
