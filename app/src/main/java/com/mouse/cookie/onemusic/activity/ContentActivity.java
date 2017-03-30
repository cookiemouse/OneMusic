package com.mouse.cookie.onemusic.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mouse.cookie.onemusic.R;
import com.mouse.cookie.onemusic.adapter.MyFragmentPagerAdapter;
import com.mouse.cookie.onemusic.data.Action;
import com.mouse.cookie.onemusic.data.Msg;
import com.mouse.cookie.onemusic.data.Path;
import com.mouse.cookie.onemusic.fragment.LyricFragment;
import com.mouse.cookie.onemusic.fragment.MusicListFragment;
import com.mouse.cookie.onemusic.fragment.PlayingFragment;
import com.mouse.cookie.onemusic.manager.DatabaseManager;
import com.mouse.cookie.onemusic.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "ContentActivity";
    private final static String PERMISSION_READ_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private final static int PERMISSION_REQUEST_CODE = 1;

    private int duration, progress;
    private int current;
    private String strError = "";

    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private MusicListFragment musicListFragment = new MusicListFragment();
    private PlayingFragment mPlayingFragment = new PlayingFragment();
    private LyricFragment mLyricFragment = new LyricFragment();

    private ImageView mImageViewAblum;
    private TextView mTextViewTitle, mTextViewArtist;

    private DatabaseManager mDatabaseManager;

    private PlayerService mPlayerService;

    private MyHandler myHandler = new MyHandler();

    private ProgressBar mProgressBar;

    //bindService
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
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
            switch (intent.getAction()) {
                case Action.UPDATE: {
                    duration = intent.getIntExtra(Action.UPDATA_DURATIOn, 0);
                    progress = intent.getIntExtra(Action.UPDATE_PROGRESS, 0);

                    myHandler.obtainMessage(Msg.MSG_UPDATE).sendToTarget();
                    break;
                }
                case Action.ERROR: {
                    strError = intent.getStringExtra(Action.SEND_ERROR);
                    myHandler.obtainMessage(Msg.MSG_ERROR).sendToTarget();
                    break;
                }
                case Action.CHANGE: {
                    current = intent.getIntExtra(Action.CHANGE_CURRENT, 0);
                    myHandler.obtainMessage(Msg.MSG_CHANGE).sendToTarget();
                    break;
                }
                case Action.STOP: {
                    myHandler.obtainMessage(Msg.MSG_STOP).sendToTarget();
                    break;
                }
                default: {
                    Log.i(TAG, "Broadcast default " + intent.getAction());
                }
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
        if (null != mPlayerService) {
            current = mPlayerService.getCurrent();
        }
        updateUI();
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
        if (null != mPlayerService && mPlayerService.isPlaying()) {
            ContentActivity.this.moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    //初始化视图
    private void initView() {
        mDatabaseManager = new DatabaseManager(getApplicationContext());

        mImageViewAblum = (ImageView) findViewById(R.id.iv_layout_bottom_icon);
        mTextViewTitle = (TextView) findViewById(R.id.tv_layout_bottom_title);
        mTextViewArtist = (TextView) findViewById(R.id.tv_layout_bottom_name);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.vp_activity_content_);
        List<Fragment> mFragmentList = new ArrayList<>();

        mFragmentList.add(musicListFragment);
        mFragmentList.add(mPlayingFragment);
        mFragmentList.add(mLyricFragment);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(
                getSupportFragmentManager()
                , mFragmentList);

        mViewPager.setAdapter(myFragmentPagerAdapter);

        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(3);

        Button mButtonPlayOrPause = (Button) findViewById(R.id.btn_layout_bottom_playorpause);
        Button mButtonNext = (Button) findViewById(R.id.btn_layout_bottom_next);
        Button mButtonUp = (Button) findViewById(R.id.btn_layout_bottom_up);
        mButtonPlayOrPause.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mButtonUp.setOnClickListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_layout_bottom_);
    }

    //开启服务
    private void startService() {
        Intent intent = new Intent(ContentActivity.this, PlayerService.class);
        startService(intent);
    }

    //关闭服务
    private void stopService() {
        if (null != mPlayerService && !mPlayerService.isPlaying()) {
            Intent intent = new Intent(ContentActivity.this, PlayerService.class);
            stopService(intent);
        }
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
        mIntentFilter.addAction(Action.ERROR);
        mIntentFilter.addAction(Action.CHANGE);
        mIntentFilter.addAction(Action.STOP);
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

    //更新底部控件
    private void updateUI(){
        Cursor cursor = mDatabaseManager.queryAllData();
        if (current <= cursor.getCount()) {
            cursor.move(current + 1);
            String title = cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_ARTIST));
            byte[] embeddedPicture = cursor.getBlob(cursor.getColumnIndex(Path.DATABASE_TABLE_PIC));
            Bitmap bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length);

            mImageViewAblum.setImageBitmap(bitmap);
            mTextViewTitle.setText(title);
            mTextViewArtist.setText(artist);
        }
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
                Log.i(TAG, "Permission default");
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
                case Msg.MSG_ERROR: {
                    Toast.makeText(ContentActivity.this, strError, Toast.LENGTH_SHORT).show();
                    break;
                }
                case Msg.MSG_CHANGE: {
                    //更新底部控件状态
                    updateUI();
                    //更新列表中的播放状态
                    musicListFragment.setPosition(current);
                    break;
                }
                case Msg.MSG_STOP:{
                    mProgressBar.setProgress(0);
                    musicListFragment.setStop();
                    break;
                }
                default: {
                    Log.i(TAG, "MyHandler default " + msg.what);
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
            case R.id.btn_layout_bottom_next: {
                mPlayerService.playNext();
                break;
            }
            case R.id.btn_layout_bottom_up: {
                mPlayerService.playUp();
                break;
            }
            default: {
                Log.i(TAG, "onClick default");
            }
        }
    }

    //更新Viewpager Adapter
    public void updateAdapter() {
        myFragmentPagerAdapter.notifyDataSetChanged();
    }

    //播放音乐,待定
    public void play(int position) {
        mPlayerService.play(position);
    }

    //提供当前current
    public int getCurrent() {
        return current;
    }
}
