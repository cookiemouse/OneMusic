package com.mouse.cookie.onemusic.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import com.mouse.cookie.onemusic.manager.MediaDataManager;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {

    private ContentActivity mContentActivity;

    private ListView mListView;
    private List<MusicListAdapterData> musicListAdapterDataList;
    private MusicListAdapter musicListAdapter;

    private DatabaseManager mDatabaseManager;
    private MediaDataManager mediaDataManager;

    private MyAsyncTask myAsyncTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewMusicList = inflater.inflate(R.layout.fragment_music_list, container, false);

        initView(viewMusicList);

        setEventListener();

        return viewMusicList;
    }

    private void initView(View view) {

        mContentActivity = (ContentActivity) getActivity();

        mListView = (ListView) view.findViewById(R.id.lv_fragment_musiclist);

        musicListAdapterDataList = new ArrayList<>();

        mDatabaseManager = new DatabaseManager(mContentActivity.getApplicationContext());
        mediaDataManager = new MediaDataManager(getContext());

        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

        musicListAdapter = new MusicListAdapter(getContext(), musicListAdapterDataList);

        mListView.setAdapter(musicListAdapter);
    }

    private void setEventListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                for (MusicListAdapterData data : musicListAdapterDataList) {
//                    data.setPlaying(false);
//                }
//                musicListAdapterDataList.get(position).setPlaying(true);
//                musicListAdapter.notifyDataSetChanged();

                mContentActivity.play(musicListAdapterDataList.get(position).getPath());
            }
        });
    }

    private class MyAsyncTask extends AsyncTask {
        @Override
        protected void onPostExecute(Object o) {
            musicListAdapter.notifyDataSetChanged();
            mContentActivity.updaAdapter();
//            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Cursor cursor = mDatabaseManager.queryData();
            if (null != cursor) {
                cursor.moveToFirst();
                do {
                    mediaDataManager.setResource(cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_PATH)));
                    String album = mediaDataManager.getAlbum();
                    String musicName = mediaDataManager.getTitle();
                    String artist = mediaDataManager.getArtist();
                    String bitRate = mediaDataManager.getBitRate();
                    String path = cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_PATH));
                    byte[] embeddedPicture = mediaDataManager.getEmbeddedPicture();

                    Bitmap bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length);
                    musicListAdapterDataList.add(new MusicListAdapterData(scaleBitmap(bitmap), musicName, artist, album, bitRate, path, false));
                } while (cursor.moveToNext());
            }
            return null;
        }
    }

    //压缩图片
    private Bitmap scaleBitmap(Bitmap bitmap){
        if (null == bitmap){
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(0.2f, 0.2f);
        Bitmap bitmapReturn = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        if (bitmap.equals(bitmapReturn)){
            return bitmapReturn;
        }
        bitmap.recycle();
        return bitmapReturn;
    }
}
