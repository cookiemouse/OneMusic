package com.mouse.cookie.onemusic.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mouse.cookie.onemusic.R;
import com.mouse.cookie.onemusic.activity.ContentActivity;
import com.mouse.cookie.onemusic.adapter.MusicListAdapter;
import com.mouse.cookie.onemusic.data.MusicListAdapterData;
import com.mouse.cookie.onemusic.data.Path;
import com.mouse.cookie.onemusic.data.PlayState;
import com.mouse.cookie.onemusic.manager.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {

    private static final String TAG = "MusicListFragment";

    private int current = 0;

    private ContentActivity mContentActivity;

    private ListView mListView;
    private List<MusicListAdapterData> musicListAdapterDataList;
    private MusicListAdapter musicListAdapter;

    private DatabaseManager mDatabaseManager;

    private MyAsyncTask myAsyncTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewMusicList = inflater.inflate(R.layout.fragment_music_list, container, false);

        initView(viewMusicList);

        setEventListener();

        return viewMusicList;
    }

    @Override
    public void onResume() {
        if (mContentActivity.getPlayState() == PlayState.Start) {
            setPosition(mContentActivity.getCurrent());
        }
        super.onResume();
    }

    private void initView(View view) {

        mContentActivity = (ContentActivity) getActivity();

        mListView = (ListView) view.findViewById(R.id.lv_fragment_musiclist);

        musicListAdapterDataList = new ArrayList<>();

        mDatabaseManager = new DatabaseManager(mContentActivity.getApplicationContext());

        musicListAdapter = new MusicListAdapter(getContext(), musicListAdapterDataList);

        mListView.setAdapter(musicListAdapter);

        flushListData();
    }

    private void setEventListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mContentActivity.getPlayState() != PlayState.Start) {
                    setPosition(position);
                    mContentActivity.play(position);
                    return;
                }
                if (current != position) {
                    setPosition(position);

                    mContentActivity.play(position);
                }
            }
        });
    }

    private class MyAsyncTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            musicListAdapterDataList.clear();
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Cursor cursor = mDatabaseManager.queryAllData();
            return cursor;
        }

        @Override
        protected void onPostExecute(Object o) {
            Cursor cursor = (Cursor) o;
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String album = cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_ALBUM));
                    String title = cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_ARTIST));
                    String bitRate = cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_BITRATE));
                    String path = cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_PATH));
                    byte[] embeddedPicture = cursor.getBlob(cursor.getColumnIndex(Path.DATABASE_TABLE_PIC));
                    Bitmap bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length);

                    musicListAdapterDataList.add(new MusicListAdapterData(bitmap, title, artist, album, bitRate, path, false));
                } while (cursor.moveToNext());
            }
            musicListAdapter.notifyDataSetChanged();
            mContentActivity.updateAdapter();
//            super.onPostExecute(o);
        }
    }

    //listview如果Item不可见滑动中间
    private void scrollToMiddle() {
        int top = mListView.getFirstVisiblePosition() + 1;
        int bottom = mListView.getLastVisiblePosition() - 1;
        if (current < top) {
            mListView.smoothScrollToPosition(current - ((bottom - top) / 2));
        } else if (current > bottom) {
            mListView.smoothScrollToPosition(current + ((bottom - top) / 2));
        }
    }

    private void flushListData() {
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    private void updateState() {
        for (MusicListAdapterData data : musicListAdapterDataList) {
            data.setPlaying(false);
        }
        if (current < musicListAdapterDataList.size()) {
            musicListAdapterDataList.get(current).setPlaying(true);
        }
        musicListAdapter.notifyDataSetChanged();

        scrollToMiddle();
    }

    /**
     * 由ContentActivity操作，主要用于被动更新状态
     */
    public void setPosition(int current) {
        this.current = current;
        updateState();
    }

    public void setStop() {
        for (MusicListAdapterData data : musicListAdapterDataList) {
            data.setPlaying(false);
        }
        musicListAdapter.notifyDataSetChanged();
    }
}
