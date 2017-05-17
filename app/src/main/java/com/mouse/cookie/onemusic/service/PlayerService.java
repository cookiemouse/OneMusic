package com.mouse.cookie.onemusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mouse.cookie.onemusic.data.Action;
import com.mouse.cookie.onemusic.data.Msg;
import com.mouse.cookie.onemusic.data.PlayState;
import com.mouse.cookie.onemusic.manager.DatabaseManager;
import com.mouse.cookie.onemusic.manager.MediaDataManager;
import com.mouse.cookie.onemusic.manager.MediaManager;

public class PlayerService extends Service {

    private static final String TAG = "PlayerService";

    private static final int DELAY = 500;

    private int current = 0;

    @NonNull
    private String TEST_PATH = "";
    private Uri TEST_URI = null;

    private MyBinder myBinder;
    private MyHandler myHandler;
    //更新进度的Runnable
    private Runnable mRunnableUpdate;

    private MediaManager mediaManager;
    private DatabaseManager mDatabaseManager;
    private MediaDataManager mediaDataManager;

    //音频焦点控制
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private boolean isInterupted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        myBinder = new MyBinder();

        myHandler = new MyHandler();
        mRunnableUpdate = new Runnable() {
            @Override
            public void run() {
                myHandler.obtainMessage(Msg.MSG_CYCLE).sendToTarget();
                sendStateBroadcast();
            }
        };

        mediaManager = MediaManager.getInstance();
        mDatabaseManager = new DatabaseManager(getApplicationContext());
        mediaDataManager = new MediaDataManager(getApplicationContext());

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        mediaManager.setPlayListener(new MediaManager.PlayListener() {
            @Override
            public void onCompletion() {
//                myHandler.obtainMessage(Msg.MSG_STOP).sendToTarget();
            }

            @Override
            public void onStateChanged(int state) {
                switch (state) {
                    case PlayState.Start: {
                        myHandler.obtainMessage(Msg.MSG_START).sendToTarget();
                        break;
                    }
                    case PlayState.Pause: {
                        myHandler.obtainMessage(Msg.MSG_PAUSE).sendToTarget();
                        break;
                    }
                    case PlayState.Stop: {
                        myHandler.obtainMessage(Msg.MSG_STOP).sendToTarget();
                        break;
                    }
                    default: {
                        Log.d(TAG, "onStateChanged: default");
                    }
                }

//                if (paused) {
//                    myHandler.obtainMessage(Msg.MSG_WHAT).sendToTarget();
//                } else {
//                    myHandler.obtainMessage(Msg.MSG_CYCLE).sendToTarget();
//                    myHandler.obtainMessage(Msg.MSG_START).sendToTarget();
//                }
            }

            @Override
            public void onError(String error) {
                myHandler.obtainMessage(Msg.MSG_ERROR, error).sendToTarget();
            }
        });

        mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN: {
                        //完全获得焦点
                        Log.i(TAG, "完全获得焦点");
                        if (isInterupted) {
                            playOrPause();
                        }
                        isInterupted = false;
                        break;
                    }
                    case AudioManager.AUDIOFOCUS_LOSS: {
                        //完全丢失焦点
                        Log.i(TAG, "完全丢失焦点");
                        mediaManager.pause();
                        isInterupted = true;
                        break;
                    }
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                        //暂时丢失焦点
                        Log.i(TAG, "暂时丢失焦点");
                        mediaManager.pause();
                        isInterupted = true;
                        break;
                    }
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                        //暂时丢失，但能共存
                        Log.i(TAG, "暂时丢失，但能共存");
                        break;
                    }
                    default: {
                        Log.i(TAG, "default");
                    }
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return this.myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        mediaManager.destroy();
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Msg.MSG_WHAT: {
                    break;
                }
                case Msg.MSG_CYCLE: {
                    myHandler.postDelayed(mRunnableUpdate, DELAY);
                    break;
                }
                case Msg.MSG_START: {
                    myHandler.obtainMessage(Msg.MSG_CYCLE).sendToTarget();
                    sendStartBroadcast();
                    break;
                }
                case Msg.MSG_PAUSE: {
                    myHandler.removeCallbacks(mRunnableUpdate);
                    sendPauseBroadcast();
                    break;
                }
                case Msg.MSG_STOP: {
                    myHandler.removeCallbacks(mRunnableUpdate);
                    sendStopBroadcast();
                    playNext();
                    break;
                }
                case Msg.MSG_ERROR: {
                    sendErrorBroadcast((String) msg.obj);
                    break;
                }
                case Msg.MSG_NO_MUSIC: {
                    sendNoMusicBroadcast();
                    break;
                }
                default: {
                    Log.i(TAG, "default");
                }
            }
        }
    }

    //请求获得音频焦点
    private void applyAudioFocus() {
        mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener
                , AudioManager.STREAM_MUSIC
                , AudioManager.AUDIOFOCUS_GAIN);
    }

    //释放音频焦点
    private void abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }

    //设置资源
    private void setResource(String path) {
        mediaManager.setResource(path);
        mediaDataManager.setResource(path);
        TEST_PATH = path;
    }

    private void setResource(Uri uri) {
        mediaManager.setResource(getApplicationContext(), uri);
        mediaDataManager.setResource(uri);
        TEST_URI = uri;
    }

    //发送无歌曲错误，去添加歌曲目录
    private void sendNoMusicBroadcast() {
        Intent intent = new Intent(Action.NO_MUSIC);
        sendBroadcast(intent);
    }

    //发送广播更新播放状态
    private void sendStateBroadcast() {
        Intent intent = new Intent(Action.UPDATE);
        intent.putExtra(Action.UPDATE_PROGRESS, mediaManager.getProgress());
        intent.putExtra(Action.UPDATA_DURATIOn, Integer.valueOf(mediaDataManager.getDuration()));
        sendBroadcast(intent);
    }

    //发送歌曲改变广播
    private void sendStartBroadcast() {
        Intent intent = new Intent(Action.START);
        intent.putExtra(Action.START_CURRENT, current);
        sendBroadcast(intent);
    }

    //发送Pause广播
    private void sendPauseBroadcast() {
        Intent intent = new Intent(Action.PAUSE);
        sendBroadcast(intent);
    }

    //发送Stop广播
    private void sendStopBroadcast() {
        Intent intent = new Intent(Action.STOP);
        sendBroadcast(intent);
    }

    //发送Error广播
    private void sendErrorBroadcast(String error) {
        Intent intent = new Intent(Action.ERROR);
        intent.putExtra(Action.SEND_ERROR, error);
        sendBroadcast(intent);
    }

    /**
     * 控制
     * ========================================================================
     **/

    public void playOrPause() {
        Log.i(TAG, "play or pause");
        if (getPlayState() == PlayState.Start) {
            mediaManager.pause();
//            myHandler.obtainMessage(Msg.MSG_WHAT).sendToTarget();
            abandonAudioFocus();
        } else if (mediaManager.getPlayState() == PlayState.Pause) {
            applyAudioFocus();
            mediaManager.replay();
//            myHandler.obtainMessage(Msg.MSG_CYCLE).sendToTarget();
        } else {
            applyAudioFocus();
            play(current);
        }
    }

    public void play(int position) {
        current = position;
        TEST_PATH = mDatabaseManager.queryPath(position);
        if (null != TEST_PATH) {
            setResource(TEST_PATH);
//            myHandler.obtainMessage(Msg.MSG_START).sendToTarget();
        } else {
            myHandler.obtainMessage(Msg.MSG_NO_MUSIC).sendToTarget();
        }
    }

    public void play(Uri uri) {
        if (null != uri) {
            TEST_URI = uri;
            setResource(uri);
//            myHandler.obtainMessage(Msg.MSG_START).sendToTarget();
        }
    }

    public void playNext() {
        setInitProgress(0);
        if (current < mDatabaseManager.queryAllData().getCount() - 1) {
            current++;
            play(current);
        }
    }

    public void playUp() {
        setInitProgress(0);
        if (current > 0) {
            current--;
            play(current);
        }
    }

    public int getPlayState() {
        if (null == mediaManager){
            return PlayState.Idle;
        }
        return mediaManager.getPlayState();
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    //待定，要不要退出时再进入，设置保存的进度
    //开放，通过托动进度条来设置进度
    public void setProgress(int progress) {
        mediaManager.setProgress(progress);
    }

    public void setInitProgress(int progress) {
        mediaManager.setInitProgress(progress);
    }
}
