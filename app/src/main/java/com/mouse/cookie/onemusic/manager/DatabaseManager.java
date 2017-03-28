package com.mouse.cookie.onemusic.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mouse.cookie.onemusic.data.Path;

/**
 * Created by cookie on 17-3-15.
 */

public class DatabaseManager {

    /**
     *
     * 有问题，存的应该不仅仅是名字，还应该包括封面在类的所有数据
     *
     * **/

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    public DatabaseManager(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }

    //增
    public void inserData(String name, String path){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Path.DATABASE_TABLE_NAME, name);
        contentValues.put(Path.DATABASE_TABLE_PATH, path);
        mSqLiteDatabase.insert(Path.DATABASE_TABLE, null, contentValues);
    }

    //删
    public void deleteData(String name){
        mSqLiteDatabase.delete(Path.DATABASE_TABLE
                , Path.DATABASE_TABLE_NAME + "=?"
                , new String[]{name});
    }

    //改
    public void updateData(String name, String path){
    }

    //查
    public Cursor queryData(){
        return mSqLiteDatabase.query(Path.DATABASE_TABLE
                , new String[]{Path.DATABASE_TABLE_NAME, Path.DATABASE_TABLE_PATH}
                , null , null , null, null, null);
    }
}
