package com.mouse.cookie.onemusic.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import com.mouse.cookie.onemusic.data.MusicData;
import com.mouse.cookie.onemusic.data.Path;
import java.io.ByteArrayOutputStream;

/**
 * Created by cookie on 17-3-15.
 */

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    public DatabaseManager(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }

    //增
    public void inserData(MusicData musicData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Path.DATABASE_TABLE_TITLE, musicData.getTitle());
        contentValues.put(Path.DATABASE_TABLE_PATH, musicData.getPath());
        contentValues.put(Path.DATABASE_TABLE_ALBUM, musicData.getAlbum());
        contentValues.put(Path.DATABASE_TABLE_ARTIST, musicData.getArtist());
        contentValues.put(Path.DATABASE_TABLE_BITRATE, musicData.getBitRate());
        contentValues.put(Path.DATABASE_TABLE_PIC, bitmapToTyte(musicData.getPic()));
        mSqLiteDatabase.insert(Path.DATABASE_TABLE, null, contentValues);
    }

    //删
    public void deleteData(String name) {
        mSqLiteDatabase.delete(Path.DATABASE_TABLE
                , Path.DATABASE_TABLE_TITLE + "=?"
                , new String[]{name});
    }

    //改
    public void updateData(MusicData musicData) {
    }

    //查所有数据，用于显示
    public Cursor queryAllData() {
        return mSqLiteDatabase.query(
                Path.DATABASE_TABLE
                , new String[]{
                        Path.DATABASE_TABLE_TITLE
                        , Path.DATABASE_TABLE_PATH
                        , Path.DATABASE_TABLE_ALBUM
                        , Path.DATABASE_TABLE_ARTIST
                        , Path.DATABASE_TABLE_BITRATE
                        , Path.DATABASE_TABLE_PIC}
                , null, null, null, null, null);
    }

    //查路径，用于服务播放音乐
    public String queryPath(int position) {
        Cursor cursor = mSqLiteDatabase.query(
                Path.DATABASE_TABLE
                , new String[]{Path.DATABASE_TABLE_PATH}
                , null, null, null, null, null);
        if (cursor.getCount() >= position && position >= 0){
            cursor.move(position + 1);
            Log.i(TAG, "queryPath.position-->" + position);
            Log.i(TAG, "columnIndex-->" + cursor.getColumnIndex(Path.DATABASE_TABLE_PATH));
            String path = cursor.getString(cursor.getColumnIndex(Path.DATABASE_TABLE_PATH));
            cursor.close();
            return path;
        }
        return null;
    }

    //Bitmap转byte[]
    private byte[] bitmapToTyte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);//转换质量为100%
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
