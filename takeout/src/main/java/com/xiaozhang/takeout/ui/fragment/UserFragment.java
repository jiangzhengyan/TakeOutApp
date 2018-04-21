package com.xiaozhang.takeout.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.global.MyApplication;
import com.xiaozhang.takeout.model.bean.UserInfo;
import com.xiaozhang.takeout.model.db.DBHelper;
import com.xiaozhang.takeout.ui.activity.AddAddressActivity;
import com.xiaozhang.takeout.ui.activity.AddressListActivity;
import com.xiaozhang.takeout.ui.activity.LoginActivity;

import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhangxiao on 2018/3/5.
 */
public class UserFragment extends BaseFragment {
    @InjectView(R.id.tv_user_setting)
    ImageView tvUserSetting;
    @InjectView(R.id.iv_user_notice)
    ImageView ivUserNotice;
    @InjectView(R.id.login)
    ImageView login;
    @InjectView(R.id.username)
    TextView username;
    @InjectView(R.id.phone)
    TextView phone;
    @InjectView(R.id.ll_userinfo)
    LinearLayout llUserinfo;


    @InjectView(R.id.tv_jifen)
    TextView tvJifen;
    @InjectView(R.id.tv_youhui)
    TextView tvYouhui;
    @InjectView(R.id.tv_yue)
    TextView tvYue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_user, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnClick({R.id.login, R.id.tv_user_setting, R.id.tv_yue, R.id.tv_youhui, R.id.tv_jifen, R.id.iv_user_notice, R.id.ll_my_address, R.id.ll_my_collection, R.id.ll_my_comment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_user_setting:
            case R.id.iv_user_notice:
            case R.id.tv_yue:
            case R.id.tv_youhui:
            case R.id.tv_jifen:
            case R.id.ll_my_collection://我的收藏
            case R.id.ll_my_comment://我的评价
                makeText("敬请期待!");
                break;
            case R.id.ll_my_address://收货地址
                Intent intentAddress = new Intent(getActivity(), AddressListActivity.class);
                startActivity(intentAddress);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        //读取数据库中的数据，用于展示
        int userId = MyApplication.userId;
        if (userId != -1) {
            //1.获取dbHelper对象
            DBHelper dbHelper = DBHelper.getInstance(getActivity());
            Dao<UserInfo, Integer> dao = dbHelper.getDao(UserInfo.class);
            String name = "";
            String userPhone = "";
            try {
                UserInfo userInfo = dao.queryForId(userId);
                name = userInfo.getName();
                userPhone = userInfo.getPhone();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //将去登录图片隐藏
            login.setVisibility(View.GONE);
            //显示电话号码和名称的控件显示
            llUserinfo.setVisibility(View.VISIBLE);

            username.setText(name);
            phone.setText(userPhone);
        } else {
            //将去登录图片隐藏
            login.setVisibility(View.VISIBLE);
            //显示电话号码和名称的控件显示
            llUserinfo.setVisibility(View.GONE);
        }


        super.onResume();
    }
}
