package com.xiaozhang.takeout.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.xiaozhang.takeout.R;

public class SplashActivity extends BaseActivity {
    private long mRemainTime = 2800;//黄金开机等待时间
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mHandler.postDelayed(mRunnable, mRemainTime);
    }

    /**
     * 开始跳转
     */
    private void intentStart() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            intentStart();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }

    }
}
