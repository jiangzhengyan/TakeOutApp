package com.xiaozhang.takeout.ui.activity;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Created by zhangxiao on 2018/3/6.
 */
public class BaseActivity extends FragmentActivity{
    private Toast toast;
    private long exitTime = 0;

    /**
     *  显示吐司
     * @param msg
     */
    public void makeText(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /**
     * 描述：退出程序
     */
    protected void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            makeText( "再按一次就退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
