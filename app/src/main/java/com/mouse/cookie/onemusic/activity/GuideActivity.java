package com.mouse.cookie.onemusic.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mouse.cookie.onemusic.R;

public class GuideActivity extends AppCompatActivity {

    private final static int DELAY = 2000;

    private MyHandler myHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toActivity();
            }
        }, DELAY);
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
