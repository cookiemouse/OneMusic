package com.mouse.cookie.onemusic.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

        current = mContentActivity.getCurrent();

        super.onResume();
    }

    private void initView(View view) {

        mContentActivity = (ContentActivity) getActivity();

        mListView = (ListView) view.findViewById(R.id.lv_fragment_musiclist);

        musicListAdapterDataList = new ArrayList<>();

        mDatabaseManager = new DatabaseManager(mContentActivity.getApplicationContext());

        musicListAdapter = new MusicListAdapter(getContext(), musicListAdapterDataList);

        mListView.setAdapter(musicListAdapter);
    }

    private void setEventListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (current != position) {
                    current = position;
                    updateState();

                    mContentActivity.play(position);
                }
            }
        });
    }

    private class MyAsyncTask extends AsyncTask {
        @Override
        protected void onPostExecute(Object o) {
            updateState();
            mContentActivity.updateAdapter();
//            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Cursor cursor = mDatabaseManager.queryAllData();
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
            return null;
        }
    }

    private void updateState() {
        for (MusicListAdapterData data : musicListAdapterDataList) {
            data.setPlaying(false);
        }
        if (current < musicListAdapterDataList.size()) {
            musicListAdapterDataList.get(current).setPlaying(true);
        }
        musicListAdapter.notifyDataSetChanged();
    }

    /**
     * 由ContentActivity操作，主要用于被动更新状态
     */
    public void updateStatu(int current) {
        this.current = current;
        updateState();
    }

    // TODO: 17-3-29 还有一个是否正在播放，如果在播放则显示图标，未播放则不显示
}
