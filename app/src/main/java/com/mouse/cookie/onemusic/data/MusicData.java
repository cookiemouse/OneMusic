package com.mouse.cookie.onemusic.data;

import android.graphics.Bitmap;

/**
 * Created by cookie on 17-3-28.
 */

public class MusicData {
    private String title;
    private String path;
    private String album;
    private String artist;
    private String bitRate;
    private Bitmap pic;

    public MusicData(String title, String path, String album, String artist, String bitRate, Bitmap pic) {
        this.title = title;
        this.path = path;
        this.album = album;
        this.artist = artist;
        this.bitRate = bitRate;
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getBitRate() {
        return bitRate;
    }

    public void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }
}
