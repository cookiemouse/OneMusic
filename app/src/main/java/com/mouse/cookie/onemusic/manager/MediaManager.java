package com.mouse.cookie.onemusic.manager;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

/**
 * Created by xp on 17-3-15.
 * 提供对外的方法有setSource、setLoop、isLoog、pause、replay、setProgress、destory
 * 对外回调PlayListener==>onCompletion、onStatuChanged、onError
 */

public class MediaManager {

    private final static String TAG = "MediaManager";

    private MediaPlayer mediaPlayer;

    private boolean isPause;

    private PlayListener mPlayListener;

    private Context mContext;

    public MediaManager(Context context) {

        this.mContext = context;
        mediaPlayer = new MediaPlayer();

//        mediaPlayer.prepare();
//        mediaPlayer.prepareAsync();
//        mediaPlayer.start();
//        mediaPlayer.pause();
//        mediaPlayer.stop();
//        mediaPlayer.reset();
//        mediaPlayer.release();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                if (null != mPlayListener) {
                    mPlayListener.onStatuChanged(isPause);
                } else {
                    throw new UnsupportedOperationException("PalyListener is null");
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (null != mPlayListener) {
                    mPlayListener.onCompletion();
                } else {
                    throw new UnsupportedOperationException("PalyListener is null");
                }
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (null != mPlayListener) {
                    mPlayListener.onError("播放出错！");
                } else {
                    throw new UnsupportedOperationException("PalyListener is null");
                }
                return false;
            }
        });

        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mediaPlayer.start();
                if (null != mPlayListener) {
                    mPlayListener.onStatuChanged(isPause);
                } else {
                    throw new UnsupportedOperationException("PalyListener is null");
                }
            }
        });
    }

    public void setResource(String path) {

        Log.i(TAG, "setSource path");
        //重置
        pause();
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
    }

    //待定
    public void setResource(Uri uri) {

        //重置
        pause();
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(mContext, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public boolean isPause(){
        return isPause;
    }

    public void setLoop(boolean isloop) {
        if (null != mediaPlayer) {
            mediaPlayer.setLooping(isloop);
        }
    }

    public boolean isLoop() {
        return mediaPlayer.isLooping();
    }

    public void pause() {
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;

            if (null != mPlayListener) {
                mPlayListener.onStatuChanged(true);
            } else {
                throw new UnsupportedOperationException("PalyListener is null");
            }
        }
    }

    public void replay() {
        if (null != mediaPlayer && isPause) {
            mediaPlayer.start();
            isPause = false;

            if (null != mPlayListener) {
                mPlayListener.onStatuChanged(false);
            } else {
                throw new UnsupportedOperationException("PalyListener is null");
            }
        }
    }

    public void setProgress(int second) {
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(second * 1000);
        } else {
            throw new UnsupportedOperationException("media seekto failure");
        }
    }

    public int getProgress(){
        return mediaPlayer.getCurrentPosition();
    }

    public void destory() {
        pause();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public interface PlayListener {
        //播放完成
        void onCompletion();

        //播放状态改变
        void onStatuChanged(boolean paused);

        //播放出错
        void onError(String error);
    }

    public void setPlayListener(PlayListener listener) {
        this.mPlayListener = listener;
    }

}

