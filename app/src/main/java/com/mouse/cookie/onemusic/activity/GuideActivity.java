package com.mouse.cookie.onemusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.WindowManager;

import com.mouse.cookie.onemusic.R;
import com.mouse.cookie.onemusic.service.PlayerService;

public class GuideActivity extends Activity {

    private final static int DELAY = 500;

    private MyHandler myHandler = new MyHandler();
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);

        startService();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                toActivity();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        myHandler.postDelayed(mRunnable, DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    //开启服务
    private void startService() {
        Intent intent = new Intent(GuideActivity.this, PlayerService.class);
        startService(intent);
    }

    //跳转
    private void toActivity() {
        Intent intent = new Intent(GuideActivity.this, ContentActivity.class);
        startActivity(intent);
        this.finish();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
