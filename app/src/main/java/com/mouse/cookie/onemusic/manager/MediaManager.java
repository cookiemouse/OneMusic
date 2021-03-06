package com.mouse.cookie.onemusic.manager;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.mouse.cookie.onemusic.data.PlayState;

import java.io.IOException;

/**
 * Created by xp on 17-3-15.
 * 提供对外的方法有setSource、setLoop、isLoog、pause、replay、setProgress、destroy
 * 对外回调PlayListener==>onCompletion、onStateChanged、onError
 * mediaPlayer.prepare();
 * mediaPlayer.prepareAsync();
 * mediaPlayer.start();
 * mediaPlayer.pause();
 * mediaPlayer.stop();
 * mediaPlayer.reset();
 * mediaPlayer.release();
 */

public class MediaManager {

    private final static String TAG = "MediaManager";

    private static MediaManager mediaManager;

    private MediaPlayer mediaPlayer;

    private int mPlayState = PlayState.Idle;

    private int initProgress;

    private PlayListener mPlayListener;

    //单例模式
    public static MediaManager getInstance() {
        if (null == mediaManager) {
            //仅同步实例化的代码块
            synchronized (MediaManager.class) {
                if (null == mediaManager) {
                    mediaManager = new MediaManager();
                }
            }
        }
        return mediaManager;
    }

    private MediaManager() {

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared: initProgress-->" + initProgress);
                mPlayState = PlayState.Start;
                if (null != mPlayListener) {
                    mPlayListener.onStateChanged(mPlayState);
                } else {
                    throw new UnsupportedOperationException("PlayListener is null");
                }

                if (initProgress > 0) {
                    mediaManager.setProgress(initProgress);
                    initProgress = 0;
                    return;
                }

                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayState = PlayState.Stop;
                if (null != mPlayListener) {
                    mPlayListener.onStateChanged(mPlayState);
                    mPlayListener.onCompletion();
                } else {
                    throw new UnsupportedOperationException("PlayListener is null");
                }
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (null != mPlayListener) {
                    mPlayState = PlayState.Idle;
                    mPlayListener.onStateChanged(mPlayState);
                    mPlayListener.onError("播放出错！");
                } else {
                    throw new UnsupportedOperationException("PlayListener is null");
                }
                return false;
            }
        });

        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.d(TAG, "onSeekComplete: progress-->" + mp.getCurrentPosition());
                if (mPlayState == PlayState.Start) {
                    mediaPlayer.start();
                }
            }
        });
    }

    public void setResource(String path) {

        Log.i(TAG, "setSource path");
        if (null == mediaPlayer) {
            return;
        }

        //重置
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
    }

    //待定
    public void setResource(Context context, Uri uri) {

        //重置
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
    }

    public int getPlayState() {
        return mPlayState;
    }

    //待定，应该由PlayerService来控制播放模式
    //随机，或者单曲循环，列表循环，顺序播放
//    public void setLoop(boolean isloop) {
//        if (null != mediaPlayer) {
//            mediaPlayer.setLooping(isloop);
//        }
//    }
//
//    public boolean isLoop() {
//        return mediaPlayer.isLooping();
//    }

    public void pause() {
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mPlayState = PlayState.Pause;

            if (null != mPlayListener) {
                mPlayListener.onStateChanged(PlayState.Pause);
            } else {
                throw new UnsupportedOperationException("PlayListener is null");
            }
        }
    }

    public void replay() {
        if (null != mediaPlayer && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mPlayState = PlayState.Start;

            if (null != mPlayListener) {
                mPlayListener.onStateChanged(mPlayState);
            } else {
                throw new UnsupportedOperationException("PlayListener is null");
            }
        }
    }

    public void setProgress(int ms) {
        if (null != mediaPlayer) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            mediaPlayer.seekTo(ms);
        } else {
            throw new UnsupportedOperationException("media seek to failure");
        }
    }

    public void setInitProgress(int ms) {
        this.initProgress = ms;
    }

    public int getProgress() {
        if (null == mediaPlayer) {
            return 0;
        }
        return mediaPlayer.getCurrentPosition();
    }

    public void destroy() {
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        mediaManager = null;
    }

    public interface PlayListener {
        //播放完成
        void onCompletion();

        //播放状态改变
        void onStateChanged(int state);

        //播放出错
        void onError(String error);
    }

    public void setPlayListener(PlayListener listener) {
        this.mPlayListener = listener;
    }

}

