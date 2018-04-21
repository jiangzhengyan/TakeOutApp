package com.xiaozhang.takeout.presenter;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.xiaozhang.takeout.global.MyApplication;
import com.xiaozhang.takeout.model.bean.ResponseInfo;
import com.xiaozhang.takeout.model.bean.UserInfo;
import com.xiaozhang.takeout.model.db.DBHelper;
import com.xiaozhang.takeout.ui.activity.LoginActivity;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import retrofit2.Call;

/**
 * Created by zhangxiao on 2018/3/16.
 */
public class LoginPresenter extends BasePresenter{
    private Activity activity;
    private Savepoint savepoint;

    public LoginPresenter(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void parseJson(String json) {
        Log.i("",json);
        Gson gson = new Gson();
        UserInfo userInfo = gson.fromJson(json, UserInfo.class);
        MyApplication.userId = userInfo.get_id();

        if (userInfo!=null){
            DBHelper dbHelper = DBHelper.getInstance(activity);
            AndroidDatabaseConnection connection = new AndroidDatabaseConnection(dbHelper.getWritableDatabase(), true);
            //获取操作表的dao对象
            Dao<UserInfo,Integer> dao = dbHelper.getDao(UserInfo.class);
            //事物中执行
            //1.将数据库中所有的用户查询出来,然后将isLogin设置为0,代表都没有登录
            try {
                //savepoint用于记录回滚点对象
                //此行代码的作用就是指定sql语句回滚到的地方
                savepoint = connection.setSavePoint("start");
                //不要自动提交事务
                connection.setAutoCommit(false);
                List<UserInfo> userInfoList = dao.queryForAll();//select * from t_user;
                //设置isLogin为0
                for (int i = 0; i < userInfoList.size(); i++) {
                    UserInfo userInfo1 = userInfoList.get(i);
                    userInfo1.setLogin(0);//没有登录
                    //update sql语句
                    dao.update(userInfo1);
                }
                //根据id查找已登录过的用户
                UserInfo userBean = dao.queryForId(userInfo.get_id());
                if (userBean!=null){
                    //2.1   之前已经有登录过的用户,查询此用户将isLogin设置为1,登录
                    userBean.setLogin(1);
                    dao.update(userBean);
                }else{
                    //确保插入的用户,处于登录状态
                    //2.2   之前此用户没有登录过,新注册的用户,插入一条数据,将插入数据的isLogin字段设置为1,登录
                    userInfo.setLogin(1);
                    dao.create(userInfo);//insert to
                }
                //提交事务
                connection.commit(savepoint);
            } catch (SQLException e) {
                e.printStackTrace();
                //只要出现异常，sql语句回滚
                try {
                    connection.rollback(savepoint);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            ((LoginActivity)activity).finish();
        }
    }

    @Override
    protected void showErrorMesssage(String message) {

    }

    //触发网络请求方法(即给用户注册用,又给用户登录使用)
    public void getLoginData(String username,String psd,String phone,int type){
        Call<ResponseInfo> loginInfo = responseInfoApi.getLoginInfo(username, psd, phone, type);
        loginInfo.enqueue(new CallBackAdapter());
    }
}
