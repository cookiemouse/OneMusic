package com.mouse.cookie.onemusic.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.mouse.cookie.onemusic.data.Path;

/**
 * Created by cookie on 17-3-15.
 */

public class SharedPreferenceManager {

    private SharedPreferences mSharedPreferences;

    public SharedPreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(Path.SHAREDPREFERENCES, Context.MODE_PRIVATE);
    }

    //歌曲路径
    public void savePath(String path) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(Path.DATA_S_PATH, path);
        mEditor.apply();
    }

    public String getPath() {
        return mSharedPreferences.getString(Path.DATA_S_PATH, "");
    }

    //进度
    public void saveProgress(int progress) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putInt(Path.DATA_S_PROGRESS, progress);
        mEditor.apply();
    }

    public int getProgress() {
        return mSharedPreferences.getInt(Path.DATA_S_PROGRESS, 0);
    }

    //是否随机
    public void saveRandom(boolean isRandom) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(Path.DATA_S_RANDOM, isRandom);
        mEditor.apply();
    }

    public boolean isRandom() {
        return mSharedPreferences.getBoolean(Path.DATA_S_RANDOM, false);
    }
}
