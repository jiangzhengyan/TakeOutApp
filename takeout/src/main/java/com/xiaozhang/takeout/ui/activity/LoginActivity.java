package com.xiaozhang.takeout.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.presenter.LoginPresenter;
import com.xiaozhang.takeout.util.SMSUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by zhangxiao on 2018/3/16.
 */
public class LoginActivity extends BaseActivity {
    private static final int KEEP_TIME_MIN = 100;
    private static final int RESET_TIME = 101;

    //发送验证码短信成功和失败的状态码
    private static final int SEND_CODE_SUCCESS = 102;
    private static final int SEND_CODE_FAIL = 103;

    //校验验证码短信成功和失败的状态码
    private static final int VERIFICATION_CODE_SUCCESS = 104;
    private static final int VERIFICATION_CODE_FAIL = 105;

    @InjectView(R.id.iv_user_back)
    ImageView ivUserBack;
    @InjectView(R.id.iv_user_password_login)
    TextView ivUserPasswordLogin;
    @InjectView(R.id.et_user_phone)
    EditText etUserPhone;
    @InjectView(R.id.tv_user_code)
    TextView tvUserCode;
    @InjectView(R.id.et_user_psd)
    EditText etUserPsd;
    @InjectView(R.id.et_user_code)
    EditText etUserCode;
    @InjectView(R.id.login)
    TextView login;
    private int time = 60;
    private Handler handler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case KEEP_TIME_MIN:
                    tvUserCode.setText("稍后再发("+time--+")");
                    break;
                case RESET_TIME:
                    tvUserCode.setText("重新发送");
                    time = 60;
                    break;
                case SEND_CODE_SUCCESS:
                    Toast.makeText(LoginActivity.this,"发送验证码短信成功",Toast.LENGTH_SHORT).show();
                    break;
                case SEND_CODE_FAIL:
                    Toast.makeText(LoginActivity.this,"发送验证码短信失败",Toast.LENGTH_SHORT).show();
                    break;
                case VERIFICATION_CODE_SUCCESS:
                    Toast.makeText(LoginActivity.this,"校验验证码短信成功",Toast.LENGTH_SHORT).show();
                    //在此消息接收到了以后,可以做真实的用户注册流程,发送注册登录请求
                    login();
                    break;
                case VERIFICATION_CODE_FAIL:
                    Toast.makeText(LoginActivity.this,"校验验证码短信失败",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void login() {
        String phone = etUserPhone.getText().toString().trim();
        String psd = etUserPsd.getText().toString();
        if (SMSUtil.isMobileNO(phone) && !TextUtils.isEmpty(psd)){
            LoginPresenter loginPresenter = new LoginPresenter(this);
            loginPresenter.getLoginData(phone,psd,phone,2);
        }
    }

    private EventHandler eventHandler = new EventHandler(){
        @Override
        public void afterEvent(int event, int result, Object data) {
            //event事件类型,比如下发验证码短信至手机,就是一种事件类型
            //result 上诉事件是否成功
            if (result == SMSSDK.RESULT_COMPLETE){
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //下发验证码短信成功
                    Log.i("","短信下发成功");
                    handler.sendEmptyMessage(SEND_CODE_SUCCESS);
                }else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                    //如果事件类型是校验验证码,提示用户注册成功
                    handler.sendEmptyMessage(VERIFICATION_CODE_SUCCESS);
                }
            }else{
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //下发验证码短信失败
                    Log.i("","短信下发失败");
                    handler.sendEmptyMessage(SEND_CODE_FAIL);
                }else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                    //如果事件类型是校验验证码,提示用户注册失败
                    handler.sendEmptyMessage(VERIFICATION_CODE_FAIL);
                }
            }

            super.afterEvent(event, result, data);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        //用eventHandler对下发验证码短信的事件进行监听
        SMSSDK.registerEventHandler(eventHandler);
    }
    @OnClick({R.id.tv_user_code,R.id.login})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_user_code:
                sendCode();
                break;
            case R.id.login:
                checkLogin();
                break;
        }
    }

    private void checkLogin() {
        String phone = etUserPhone.getText().toString().trim();
        String psd = etUserPsd.getText().toString();
        String code = etUserCode.getText().toString();

        if (SMSUtil.isMobileNO(phone) && !TextUtils.isEmpty(psd) && !TextUtils.isEmpty(code)){
            //可以出发验证逻辑
//            SMSSDK.submitVerificationCode("86",phone,code);
            login();
        }
    }

    private void sendCode() {
        //如果手机号码输入合法,再做一下操作
        String phone = etUserPhone.getText().toString().trim();
        if (SMSUtil.isMobileNO(phone)){
        //发送验证码短信到指定的手机
            SMSSDK.getVerificationCode("86",phone, new OnSendMessageHandler() {
                @Override
                public boolean onSendMessage(String country, String phone) {
                    return false;
                }
            });

            new Thread(){
                @Override
                public void run() {
                    while(time>0){
                        //每间隔1秒就发送消息,更新显示时间
                        try {
                            Thread.sleep(999);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //发送消息
                        handler.sendEmptyMessage(KEEP_TIME_MIN);
                    }
                    //在循环结束后,再次做60秒的计数
                    handler.sendEmptyMessage(RESET_TIME);
                }
            }.start();
        }else{
            Toast.makeText(this,"请输入电话号码",Toast.LENGTH_SHORT).show();
        }
        //18500103402
        //18127095636
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }
}
