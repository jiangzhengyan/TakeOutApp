package com.xiaozhang.takeout.global;

import android.app.Application;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import com.xiaozhang.takeout.model.bean.UserInfo;
import com.xiaozhang.takeout.model.db.DBHelper;

import java.sql.SQLException;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;

/**
 * Created by zhangxiao on 2018/3/6.
 */

public class MyApplication extends Application {
    public static int statusBarHeight;
    public static int userId = -1;

    @Override
    public void onCreate() {
        super.onCreate();

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        //20
        SMSSDK.initSDK(this, "24be0c161e3fe", "c5f00de9e4e55e4c32e6e63cc4edc68a");

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //一旦开启应用，就需要查询数据库中已经登录的用户，将登录用户的id赋值给userId
        //select * from t_user where isLogin = 1;
        //1.获取DBHelper对象
        DBHelper dbHelper = DBHelper.getInstance(this);
        //2.获取Dao对象
        Dao<UserInfo,Integer> dao = dbHelper.getDao(UserInfo.class);
        //3.根据where条件进行查询
        Where<UserInfo, Integer> where = dao.queryBuilder().where();
        try {
            List<UserInfo> isLoginList = where.eq("isLogin", 1).query();
            if (isLoginList!=null && isLoginList.size()>0){
                UserInfo userInfo = isLoginList.get(0);
                userId = userInfo.get_id();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
