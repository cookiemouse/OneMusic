package com.mouse.cookie.onemusic.manager;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.mouse.cookie.onemusic.data.FileManagerAdapetrData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cookie on 17-3-15.
 */

public class FileManager {

    private final static String TAG = "FileManager";
    private final static String pathRoot = Environment.getExternalStorageDirectory().getPath();
    private List<String> mListEntered = new ArrayList<>();

    private File mFile;

    private List<FileManagerAdapetrData> mDataList = new ArrayList<>();
    private List<FileManagerAdapetrData> mDataListFile = new ArrayList<>();

    public FileManager() {
        Log.i(TAG, "path-->" + pathRoot);
        mFile = new File(pathRoot);
        if (!mFile.exists()) {
            Log.i(TAG, "file is net exists");
        }
        Log.i(TAG, "name-->" + mFile.getName());
    }

    //获取目录
    public List<FileManagerAdapetrData> getList() {
        //利用两个list来分类，mDataList先添加文件夹，mDataListFile添加文件，最后将mDataListFile填充后mDataList的最后
        mDataList.clear();
        mDataListFile.clear();

        for (File file : mFile.listFiles()) {
            FileManagerAdapetrData data = new FileManagerAdapetrData(file.getName());
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
    public List<FileManagerAdapetrData> enterFolder(String fileName) {
        mListEntered.add(fileName);
        mFile = new File(getPath() + "/" + fileName);
        return getList();
    }

    //返回上层目录
    public List<FileManagerAdapetrData> backFolder() {
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
    public List<FileManagerAdapetrData> enterAppointFolder(String fileName) {
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
