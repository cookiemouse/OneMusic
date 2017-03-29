package com.mouse.cookie.onemusic.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.mouse.cookie.onemusic.R;
import com.mouse.cookie.onemusic.adapter.MyFragmentPagerAdapter;
import com.mouse.cookie.onemusic.data.Action;
import com.mouse.cookie.onemusic.data.Msg;
import com.mouse.cookie.onemusic.fragment.LyricFragment;
import com.mouse.cookie.onemusic.fragment.MusicListFragment;
import com.mouse.cookie.onemusic.fragment.PlayingFragment;
import com.mouse.cookie.onemusic.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "ContentActivity";
    private final static String PERMISSION_READ_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private final static int PERMISSION_REQUEST_CODE = 1;

    private boolean isPlaying;
    private int duration, progress;

    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    private PlayerService mPlayerService;

    private MyHandler myHandler = new MyHandler();

    private Button mButtonPlayOrPause;

    private ProgressBar mProgressBar;

    //bindService
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceDisconnected");
            mPlayerService = ((PlayerService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
            mPlayerService = null;
        }
    };

    //广播播收器
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Action.UPDATE)) {
                isPlaying = intent.getBooleanExtra(Action.UPDATE_ISPALYING, false);
                duration = intent.getIntExtra(Action.UPDATE_DURATION, 0);
                progress = intent.getIntExtra(Action.UPDATE_PROGRESS, 0);

                myHandler.obtainMessage(Msg.MSG_UPDATE).sendToTarget();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        initView();

        applyPermission();
    }

    @Override
    protected void onStart() {
        startService();
        super.onStart();
    }

    @Override
    protected void onResume() {
        bindService();
        registerBroadcast();
        super.onResume();
    }

    @Override
    protected void onStop() {
        unbindService(mServiceConnection);
        unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isPlaying) {
            ContentActivity.this.moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    //初始化视图
    private void initView() {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.vp_activity_content_);
        List<Fragment> mFragmentList = new ArrayList<>();

        MusicListFragment musicListFragment = new MusicListFragment();
        PlayingFragment mPlayingFragment = new PlayingFragment();
        LyricFragment mLyricFragment = new LyricFragment();

        mFragmentList.add(musicListFragment);
        mFragmentList.add(mPlayingFragment);
        mFragmentList.add(mLyricFragment);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(
                getSupportFragmentManager()
                , mFragmentList);

        mViewPager.setAdapter(myFragmentPagerAdapter);

        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(3);

        mButtonPlayOrPause = (Button) findViewById(R.id.btn_layout_bottom_playorpause);
        mButtonPlayOrPause.setOnClickListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_layout_bottom_);
    }

    //开启服务
    private void startService() {
        Intent intent = new Intent(ContentActivity.this, PlayerService.class);
        startService(intent);
    }

    //关闭服务
    private void stopService() {
        Intent intent = new Intent(ContentActivity.this, PlayerService.class);
        stopService(intent);
    }

    //绑定服务
    private void bindService() {
        Intent intent = new Intent(ContentActivity.this, PlayerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    //注册广播
    private void registerBroadcast() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Action.UPDATE);
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    //运行时权限
    private void applyPermission() {
        if (ContextCompat.checkSelfPermission(ContentActivity.this, PERMISSION_READ_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ContentActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    //打开文件夹
    private void openFile() {
        Intent intent = new Intent(ContentActivity.this, FileManagerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "权限已成功申请");
                } else {
                    Log.i(TAG, "权限未申请成功");
                    showDialog("未获取相关权限，请在设置里应用权限!");
                }
                break;
            }
            default: {
                Log.i(TAG, "default");
            }
        }
    }

    //弹出信息对话框
    private void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ContentActivity.this);
        builder.setMessage(msg);
//        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ContentActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Msg.MSG_WHAT: {
                    break;
                }
                case Msg.MSG_UPDATE: {
                    // TODO: 17-3-16 更新界面
                    mProgressBar.setMax(duration);
                    mProgressBar.setProgress(progress);
                    break;
                }
                default: {
                    Log.i(TAG, "default");
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_layout_bottom_playorpause: {
                mPlayerService.playOrPause();
//                openFile();
                Log.i(TAG, "PlayOrPause Button Click");
                break;
            }
            default: {
                Log.i(TAG, "default");
            }
        }
    }

    //更新Viewpager Adapter
    public void updaAdapter() {
        myFragmentPagerAdapter.notifyDataSetChanged();
    }

    //播放音乐,待定
    public void play(int position) {
        mPlayerService.play(position);
    }
}
