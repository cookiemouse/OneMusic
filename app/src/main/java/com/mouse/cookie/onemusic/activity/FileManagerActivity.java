package com.mouse.cookie.onemusic.activity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mouse.cookie.onemusic.R;
import com.mouse.cookie.onemusic.adapter.FileManagerAdapter;
import com.mouse.cookie.onemusic.data.FileManagerAdapetrData;
import com.mouse.cookie.onemusic.data.FileType;
import com.mouse.cookie.onemusic.data.MusicData;
import com.mouse.cookie.onemusic.data.MusicListAdapterData;
import com.mouse.cookie.onemusic.data.Path;
import com.mouse.cookie.onemusic.manager.DatabaseManager;
import com.mouse.cookie.onemusic.manager.FileManager;
import com.mouse.cookie.onemusic.manager.MediaDataManager;

import java.util.List;

public class FileManagerActivity extends AppCompatActivity {

    private final static String TAG = "FileManagerActivity";

    private TextView mTextViewPath;
    private ListView mListViewShow;
    private Button mButtonAdd;

    private FileManagerAdapter mFileManagerAdapter;
    private List<FileManagerAdapetrData> mFileManagerAdapetrDataList;
    private FileManager mFileManager;

    private String pathChoiced;

    private DatabaseManager mDatabaseManager;
    private MediaDataManager mediaDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        initView();

        setEventListener();
    }

    private void initView() {

        mFileManager = new FileManager();
        mDatabaseManager = new DatabaseManager(getApplicationContext());
        mediaDataManager = new MediaDataManager(FileManagerActivity.this);

        mTextViewPath = (TextView) findViewById(R.id.tv_activity_filemanager_path);
        mListViewShow = (ListView) findViewById(R.id.lv_activity_filemanager_show);
        mButtonAdd = (Button) findViewById(R.id.btn_activity_filemanager_add);

        mFileManagerAdapetrDataList = mFileManager.getList();

        setNavigationPath();

        mFileManagerAdapter = new FileManagerAdapter(FileManagerActivity.this, mFileManagerAdapetrDataList);

        mListViewShow.setAdapter(mFileManagerAdapter);
    }

    private void setEventListener() {

        mListViewShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 17-3-19 进入目录
                FileManagerAdapetrData data = mFileManagerAdapetrDataList.get(position);
                if (data.isDir()) {
                    mFileManagerAdapetrDataList.clear();
                    mFileManager.enterFolder(data.getName());
                    mFileManagerAdapter.notifyDataSetChanged();
                    mListViewShow.scrollTo(0, 0);
                    setNavigationPath();
                } else {
                    // TODO: 17-3-19 如果不是文件夹的点击事件
                }
            }
        });

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 17-3-19 添加文件夹到播放器
                pathChoiced = mFileManager.getPath();

                showMessageDialog("添加当前路径到播放器？");
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //这里为什么不用赋值呢？
        // 因为在上面进入目录的时候用的是FileManager里的list的句柄，所有当FileManager里的list发生改变之后，这里只需要notify即可
        List<FileManagerAdapetrData> mDataListBack = mFileManager.backFolder();

        if (null != mDataListBack) {
            mFileManagerAdapter.notifyDataSetChanged();
            mListViewShow.scrollTo(0, 0);
            setNavigationPath();
        } else {
            // TODO: 17-3-19 弹出是否不选择文件夹的对话框
            this.finish();
        }
    }

    //弹出确定添加对话框
    private void showMessageDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FileManagerActivity.this);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute();
            }
        });
        builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //设置导航栏路径
    private void setNavigationPath() {

        String path = "/";
        for (final String string : mFileManager.getProcessList()) {
            path += (string + "/");
        }
        mTextViewPath.setText(path);
    }

    //将文件名及路径添加入数据库
    private void insertIntoDatabase() {
        for (FileManagerAdapetrData data : mFileManagerAdapetrDataList) {
            String type = data.getType();
            if (type.equals(FileType.MP3) || type.equals(FileType.WMA) || type.equals(FileType.MID)) {
                String path = pathChoiced + "/" + data.getName();
                mediaDataManager.setResource(path);
                String album = mediaDataManager.getAlbum();
                String title = mediaDataManager.getTitle();
                String artist = mediaDataManager.getArtist();
                String bitRate = mediaDataManager.getBitRate();
                byte[] embeddedPicture = mediaDataManager.getEmbeddedPicture();
                Bitmap bitmap = BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length);

                MusicData musicData = new MusicData(title, path, album, artist, bitRate, scaleBitmap(bitmap));

                mDatabaseManager.inserData(musicData);
            }
        }
    }

    private class MyAsyncTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            // TODO: 17-3-28 显示Loading动画
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            FileManagerActivity.this.finish();
//            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            insertIntoDatabase();
            return null;
        }
    }

    //压缩图片
    private Bitmap scaleBitmap(Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(0.2f, 0.2f);
        Bitmap bitmapReturn = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        if (bitmap.equals(bitmapReturn)) {
            return bitmapReturn;
        }
        bitmap.recycle();
        return bitmapReturn;
    }
}
