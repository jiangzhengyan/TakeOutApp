package com.xiaozhang.takeout.util;

/**
 * Created by jiang_yan on 2017/2/23.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;


import com.xiaozhang.takeout.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 通知栏
 */
public class NotifyUtils {

    public static void notifyUpdate(Context context, String title, String content, int notifyID) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        //先设定RemoteViews
        RemoteViews view_custom = new RemoteViews(context.getPackageName(), R.layout.view_update_custom);
        //设置对应IMAGEVIEW的ID的资源图片
        view_custom.setImageViewResource(R.id.iv_update_title, R.drawable.ic_launcher);
        view_custom.setTextViewText(R.id.tv_update_title, title);
        view_custom.setTextViewText(R.id.tv_update_content, content);

        //点击的事件处理
        Intent buttonIntent = new Intent("com.notifications.intent.action.ButtonClick");
        //点击更新按钮
        buttonIntent.putExtra("INTENT.BUTTON.ACTION", 1);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_update_now = PendingIntent.getBroadcast(context, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        view_custom.setOnClickPendingIntent(R.id.btn_update_now, intent_update_now);

        //点击通知栏
        buttonIntent.putExtra("INTENT.BUTTON.ACTION", 2);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_update_rl = PendingIntent.getBroadcast(context, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        view_custom.setOnClickPendingIntent(R.id.rl_update, intent_update_rl);

        mBuilder.setContent(view_custom)
                .setContentIntent(PendingIntent.getActivity(context, 1, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT))//Notification.FLAG_AUTO_CANCEL
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker(title)//通知首次出现在通知栏，带上升动画效果的
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(false)//不是正在进行的   true为正在进行  效果和.flag一样
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                .setSmallIcon(R.drawable.ic_launcher);//第一次通知时候,状态栏弹出的图标
//		mNotificationManager.notify(notifyId, mBuilder.build());
        Notification notify = mBuilder.build();
        notify.contentView = view_custom;
//		Notification notify = new Notification();
//		notify.icon = R.drawable.icon;
//		notify.contentView = view_custom;
//		notify.contentIntent = getDefalutIntent(Notification.FLAG_AUTO_CANCEL);
        mNotificationManager.notify(notifyID, notify);
    }
}
