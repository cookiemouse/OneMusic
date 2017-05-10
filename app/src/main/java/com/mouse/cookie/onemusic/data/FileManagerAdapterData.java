package com.mouse.cookie.onemusic.data;

/**
 * Created by cookie on 17-3-19.
 */

public class FileManagerAdapterData {
    private boolean isDir;
    private String name;
    private String type;

    public FileManagerAdapterData(String name) {
        this.name = name;
    }

    public FileManagerAdapterData(boolean isDir, String name) {
        this.isDir = isDir;
        this.name = name;
    }

    public FileManagerAdapterData(boolean isDir, String name, String type) {
        this.isDir = isDir;
        this.name = name;
        this.type = type;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getName() {
        return name;
    }

//    public void setName(String name) {
//        this.name = name;
//    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
