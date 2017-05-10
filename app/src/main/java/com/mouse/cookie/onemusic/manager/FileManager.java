package com.mouse.cookie.onemusic.manager;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.mouse.cookie.onemusic.data.FileManagerAdapterData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cookie on 17-3-15.
 */

public class FileManager {

    private final static String TAG = "FileManager";
    private final static String pathRoot = Environment.getExternalStorageDirectory().getPath();

    private final static String PATH_LYRIC = pathRoot + "/OneMusic/Lyric";

    private List<String> mListEntered = new ArrayList<>();

    private File mFile;

    private List<FileManagerAdapterData> mDataList = new ArrayList<>();
    private List<FileManagerAdapterData> mDataListFile = new ArrayList<>();

    public FileManager() {
        mFile = new File(PATH_LYRIC);
        if (!mFile.exists()) {
            if (!mFile.mkdirs()) {
                Log.d(TAG, "FileManager: lyric path not created-->" + mFile.getAbsolutePath());
            }
        }

        Log.i(TAG, "path-->" + pathRoot);
        mFile = new File(pathRoot);
        if (!mFile.exists()) {
            Log.i(TAG, "file is net exists");
        }
        Log.i(TAG, "name-->" + mFile.getName());
    }

    //获取目录
    public List<FileManagerAdapterData> getList() {
        //利用两个list来分类，mDataList先添加文件夹，mDataListFile添加文件，最后将mDataListFile填充后mDataList的最后
        mDataList.clear();
        mDataListFile.clear();

        for (File file : mFile.listFiles()) {
            FileManagerAdapterData data = new FileManagerAdapterData(file.getName());
            if (file.isDirectory()) {
                data.setDir(true);
                mDataList.add(data);
            } else {
                data.setDir(false);
                data.setType(getExtensionName(file.getName()));
                mDataListFile.add(data);
            }
            Log.i(TAG, "文件列表-->" + file.getName());
        }
        for (int i = 0; i < mDataList.size(); i++) {
            Log.i(TAG, "debug-1->" + mDataList.get(i).getName());
        }
        mDataList.addAll(mDataListFile);
        for (int i = 0; i < mDataList.size(); i++) {
            Log.i(TAG, "debug-2->" + mDataList.get(i).getName());
        }
        return mDataList;
    }

    //进入目录
    public List<FileManagerAdapterData> enterFolder(String fileName) {
        mListEntered.add(fileName);
        mFile = new File(getPath() + "/" + fileName);
        return getList();
    }

    //返回上层目录
    public List<FileManagerAdapterData> backFolder() {
        if (mListEntered.size() > 0) {
            mListEntered.remove(mListEntered.size() - 1);
            String path = pathRoot;
            for (String string : mListEntered) {
                path += ("/" + string);
            }
            mFile = new File(path);
            return getList();
        } else {
            return null;
        }
    }

    //进入指定目录
    public List<FileManagerAdapterData> enterAppointFolder(String fileName) {
//        String path = "/";
//        for (String string : mListEntered) {
//            if (string.equals(fileName)) {
//                break;
//            } else {
//                path += string
//            }
//        }
//        mListEntered.clear();
//        mFile = new File(getPath() + "/" + fileName);
        return null;
    }

    //进入过程目录
    public List<String> getProcessList() {
        return mListEntered;
    }

    //当前目录的绝对路径
    public String getPath() {
        return mFile.getAbsolutePath();
    }

    /***************************line***************************/

    //是歌词是否存在
    private boolean isLyricExists(String name) {
        mFile = new File(PATH_LYRIC + "/" + name + ".lrc");
        return mFile.exists();
    }

    //保存歌词
    public void saveLyric(String name, String content) {
        if (isLyricExists(name)) {
            return;
        }

        try {
            if (mFile.createNewFile()) {
                FileOutputStream fileOutputStream = new FileOutputStream(mFile);
                fileOutputStream.write(content.getBytes("UTF-8"));
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return null;
            }
        };
        asyncTask.execute();
    }

    //返回歌词路径
    public String getLyricPath(String name) {
        if (isLyricExists(name)) {
            return mFile.getAbsolutePath();
        }
        return null;
    }


    //根据文件后缀名为文件分类
    private String getExtensionName(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length() - 1))) {
                return fileName.substring(dot + 1);
            }
        }
        return fileName;
    }
}
