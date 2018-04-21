package com.xiaozhang.takeout.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.xiaozhang.takeout.observer.OrderObserver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhangxiao on 2018/3/23.
 */

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";
    private HashMap<String,String> map = new HashMap<>();
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction());
       if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "收到了通知");
            // 获取发送消息过程中传递过来的数据
            String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
//            Log.i(TAG,json+"=================");
           try {
               JSONObject jsonObject = new JSONObject(json);
               if (jsonObject.has("orderId")){
                   String orderId = jsonObject.getString("orderId");
                   map.put("orderId",orderId);
                   Log.i(TAG,orderId+"===============");
               }
               if (jsonObject.has("type")){
                   String type = jsonObject.getString("type");
                   map.put("type",type);
                   Log.i(TAG,type+"===============");
               }
               if (jsonObject.has("lat")){
                   String type = jsonObject.getString("lat");
                   map.put("lat",type);
               }
               if (jsonObject.has("lng")){
                   String type = jsonObject.getString("lng");
                   map.put("lng",type);
                   Log.i(TAG,type+"===============");
               }
               //观察者模式RxJava
//               Observer
//               Observable
               //因为获取到了需要更改状态的数据,所以需要通知页面更新
               OrderObserver.getInstance().changeUI(map);
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
    }
}
