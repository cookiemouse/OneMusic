package com.mouse.cookie.onemusic.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mouse.cookie.onemusic.data.Path;

/**
 * Created by cookie on 17-3-15.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final int VERSION = 1;

    public DatabaseHelper(Context context) {
        this(context, Path.DATABASE_NAME);
    }

    public DatabaseHelper(Context context, String name) {
        this(context, name, null, VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");

        //删除表
//        db.execSQL("DROP TABLE " + Path.DATABASE_NAME + "." + Path.DATABASE_TABLE);

        //创建表
        String sql = "create table "
                + Path.DATABASE_TABLE
                + "("
                + Path.DATABASE_TABLE_TITLE
                + " TEXT,"
                + Path.DATABASE_TABLE_PATH
                + " TEXT,"
                + Path.DATABASE_TABLE_ALBUM
                + " TEXT,"
                + Path.DATABASE_TABLE_ARTIST
                + " TEXT,"
                + Path.DATABASE_TABLE_BITRATE
                + " TEXT,"
                + Path.DATABASE_TABLE_PIC
                + " BLOB"
                + ")";
        Log.i(TAG, sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade");
    }
}
