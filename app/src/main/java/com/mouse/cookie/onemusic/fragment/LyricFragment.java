package com.mouse.cookie.onemusic.fragment;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mouse.cookie.onemusic.R;
import com.mouse.cookie.onemusic.data.Path;
import com.mouse.cookie.onemusic.databean.LyricContentBean;
import com.mouse.cookie.onemusic.databean.LyricSearchBean;
import com.mouse.cookie.onemusic.manager.DatabaseManager;
import com.mouse.cookie.onemusic.manager.FileManager;
import com.mouse.cookie.onemusic.network.NetOperator;

import java.io.File;
import java.io.IOException;

import me.wcy.lrcview.LrcView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LyricFragment extends Fragment {

    private static final String TAG = "LyricFragment";

    private String songName;
    private int current;

    private LrcView mLrcView;
    private FileManager mFileManager;
    private NetOperator mNetOperator;
    private DatabaseManager mDatabaseManager;
    private Cursor mCursor;

//    private static LyricFragment mLyricFragment;
//
//    public static LyricFragment getInstance() {
//        if (null == mLyricFragment) {
//            mLyricFragment = new LyricFragment();
//            synchronized (LyricFragment.class) {
//                if (null == mLyricFragment) {
//                    mLyricFragment = new LyricFragment();
//                }
//            }
//        }
//        return mLyricFragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewLyric = inflater.inflate(R.layout.fragment_lyric, container, false);

        initView(viewLyric);

        return viewLyric;
    }

    private void initView(View view) {
        mFileManager = new FileManager();

        mNetOperator = NetOperator.getInstance();

        mLrcView = (LrcView) view.findViewById(R.id.lv_fragment_lyric);
    }

    private void setLrc(String lrc) {
        mLrcView.loadLrc(lrc);
    }

    private void setLrc(File file) {
        mLrcView.loadLrc(file);
    }

    private void setLyricName(String name, String mills) {
        this.songName = name;
        String path = getLocaleLyric(name);
        if (null == path) {
            downloadLyric(name, mills);
            return;
        }
        File file = new File(path);
        setLrc(file);
    }

    private String getLocaleLyric(String song) {
        return mFileManager.getLyricPath(song);
    }

    private void saveLyric(String name, String content) {
        mFileManager.saveLyric(name, content);
    }

    private void downloadLyric(String song, String mills) {
        mNetOperator.searchLyric(song, mills, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: exception-->" + e);
                setLrc("");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                LyricSearchBean lyricSearchBean = gson.fromJson(result, LyricSearchBean.class);
                if (lyricSearchBean.getStatus() == 200) {
                    mNetOperator.downloadLyric(lyricSearchBean.getProposal()
                            , lyricSearchBean.getCandidates().get(0).getAccesskey()
                            , new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d(TAG, "onFailure: exception-->" + e);
                                    setLrc("");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String result = response.body().string();
                                    Gson gson = new Gson();
                                    LyricContentBean lyricContentBean = gson.fromJson(result, LyricContentBean.class);
                                    String decodedString = new String(Base64.decode(lyricContentBean.getContent(), Base64.DEFAULT));
                                    saveLyric(songName, decodedString);
                                    setLrc(decodedString);
                                }
                            });
                    return;
                }
                setLrc("");
            }
        });
    }

    private class MyAsyncTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDatabaseManager = new DatabaseManager(getContext());
        }

        @Override
        protected Object doInBackground(Object[] params) {
            if (null != mDatabaseManager) {
                return mDatabaseManager.queryAllData();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            mCursor = (Cursor) o;
            if (null != mCursor && mCursor.getCount() > current && current >= 0) {
                Log.d(TAG, "setPosition: mCursor-->" + mCursor);
                mCursor.move(current + 1);
                String song = mCursor.getString(mCursor.getColumnIndex(Path.DATABASE_TABLE_TITLE));
                String duration = mCursor.getString(mCursor.getColumnIndex(Path.DATABASE_TABLE_DURATION));
                mCursor.close();
                setLyricName(song, duration);
                return;
            }
            Log.d(TAG, "setPosition: mCursor-->" + mCursor);
            setLrc("");
//            super.onPostExecute(o);
        }
    }

    /*对外方法*/
    public void setPosition(int position) {
        this.current = position;
        new MyAsyncTask().execute();
    }

    public void updateLrc(int mills) {
        if (null != mLrcView) {
            mLrcView.updateTime(mills);
        }
    }
}
