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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

public class ContentActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "ContentActivity";
    private final static String PERMISSION_READ_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private final static int PERMISSION_REQUEST_CODE = 1;

    private int duration, progress;
    private int current;
    private String strError = "";

    private ViewPager mViewPager;

    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private MusicListFragment musicListFragment = new MusicListFragment();
    private PlayingFragment mPlayingFragment = new PlayingFragment();
    private LyricFragment mLyricFragment = new LyricFragment();

    private RelativeLayout mRelativeLayoutBottom;
    private ImageView mImageViewAblum;
    private TextView mTextViewTitle, mTextViewArtist;
    private Button mButtonPlayOrPause, mButtonNext, mButtonUp;

    private DatabaseManager mDatabaseManager;

    private PlayerService mPlayerService;

    private MyHandler myHandler = new MyHandler();

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_content);

        initView();

        setEventListener();

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

        mRelativeLayoutBottom = (RelativeLayout) findViewById(R.id.rl_activity_bottom);

        mImageViewAblum = (ImageView) findViewById(R.id.iv_layout_bottom_icon);
        mTextViewTitle = (TextView) findViewById(R.id.tv_layout_bottom_title);
        mTextViewArtist = (TextView) findViewById(R.id.tv_layout_bottom_name);

        mViewPager = (ViewPager) findViewById(R.id.vp_activity_content_);
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

        mButtonPlayOrPause = (Button) findViewById(R.id.btn_layout_bottom_playorpause);
        mButtonNext = (Button) findViewById(R.id.btn_layout_bottom_next);
        mButtonUp = (Button) findViewById(R.id.btn_layout_bottom_up);
        mButtonPlayOrPause.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mButtonUp.setOnClickListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_layout_bottom_);
    }

    //设置监听器
    private void setEventListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0: {
                        mRelativeLayoutBottom.setAlpha(1f - positionOffset);
                        break;
                    }
                    case 1: {
                        mRelativeLayoutBottom.setAlpha(positionOffset);
                        break;
                    }
                    case 2: {
                        mRelativeLayoutBottom.setAlpha(1f - positionOffset);
                        break;
                    }
                    default: {
                        Log.i(TAG, "onPageScrolled.default");
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING: {
                        mRelativeLayoutBottom.setVisibility(View.VISIBLE);
                        break;
                    }
                    case ViewPager.SCROLL_STATE_SETTLING: {
                        if (mViewPager.getCurrentItem() == 1) {
                            mRelativeLayoutBottom.setVisibility(View.GONE);
                        }
                        break;
                    }
                    case ViewPager.SCROLL_STATE_IDLE: {
//                        if (mViewPager.getCurrentItem() == 1) {
//                            mRelativeLayoutBottom.setVisibility(View.GONE);
//                        }
                        break;
                    }
                    default: {
                        Log.i(TAG, "onPageScrollStateChanged.default");
                    }
                }
            }
        });
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
        mIntentFilter.addAction(Action.START);
        mIntentFilter.addAction(Action.PAUSE);
        mIntentFilter.addAction(Action.STOP);
        mIntentFilter.addAction(Action.NO_MUSIC);
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
    private void updateUI() {
        Cursor cursor = mDatabaseManager.queryAllData();

        Bitmap bitmap = null;

        if (current <= cursor.getCount() && cursor.getCount() > 0) {
            cursor.move(current + 1);
            String title = cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_ARTIST));
            byte[] embeddedPicture = cursor.getBlob(cursor.getColumnIndex(Path.DATABASE_TABLE_PIC));
            bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length);

            mImageViewAblum.setImageBitmap(bitmap);
            mTextViewTitle.setText(title);
            mTextViewArtist.setText(artist);
        }

        mButtonPlayOrPause.setSelected(true);

        mPlayingFragment.updateUI(bitmap);

//        mButtonUp.setClickable(true);
//        mButtonNext.setClickable(true);
//
//        if (0 == current) {
//            mButtonUp.setClickable(false);
//        }
//        if (current == cursor.getColumnCount()) {
//            mButtonNext.setClickable(false);
//        }
    }

    private void updateUIPause() {
        mButtonPlayOrPause.setSelected(false);
        mPlayingFragment.updateUIPause();
    }

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
                case Action.NO_MUSIC: {
                    showChoiceFileDialog();
                    break;
                }
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
                case Action.START: {
                    current = intent.getIntExtra(Action.START_CURRENT, 0);
                    myHandler.obtainMessage(Msg.MSG_START).sendToTarget();
                    break;
                }
                case Action.PAUSE: {
                    updateUIPause();
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

    //弹出是否选择文件目录对话框
    private void showChoiceFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ContentActivity.this);
        builder.setMessage("未找到音乐资源，现在去选择存放音乐的目录？");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openFile();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
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
                    //更新界面
                    mProgressBar.setMax(duration);
                    mProgressBar.setProgress(progress);

                    mPlayingFragment.updateSeek(progress, duration);
                    break;
                }
                case Msg.MSG_ERROR: {
                    Toast.makeText(ContentActivity.this, strError, Toast.LENGTH_SHORT).show();
                    break;
                }
                case Msg.MSG_START: {
                    //更新底部控件状态
                    updateUI();
                    //更新列表中的播放状态
                    musicListFragment.setPosition(current);
                    break;
                }
                case Msg.MSG_STOP: {
                    mProgressBar.setMax(0);
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

                playOrPause();

                if (isPlaying()) {
                    mButtonPlayOrPause.setSelected(true);
                } else {
                    mButtonPlayOrPause.setSelected(false);
                }

//                openFile();
                Log.i(TAG, "PlayOrPause Button Click");
                break;
            }
            case R.id.btn_layout_bottom_next: {
                playNext();
                break;
            }
            case R.id.btn_layout_bottom_up: {
                playUp();
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

    //播放音乐，给Fragment使用
    public void playOrPause() {
        mPlayerService.playOrPause();
    }

    public void playNext() {
        mPlayerService.playNext();
    }

    public void playUp() {
        mPlayerService.playUp();
    }

    //播放音乐,待定
    public void play(int position) {
        mPlayerService.play(position);
    }

    //调整播放进度
    public void setPlayProgress(int progress) {
        mPlayerService.setProgress(progress);
    }

    //提供当前current
    public int getCurrent() {
        return current;
    }

    //提供是否正在播放
    public boolean isPlaying() {
        return null != mPlayerService && mPlayerService.isPlaying();
    }
}
