package com.mouse.cookie.onemusic.data;

import android.graphics.Bitmap;

/**
 * Created by cookie on 17-3-27.
 */

public class MusicListAdapterData {
    private Bitmap pic;
    private String musicName, artist, summary, bitRate;//音乐名，作家，描述，比特率
    private String path;
    private boolean isPlaying;//是否正在播放

    public MusicListAdapterData(Bitmap pic, String musicName, String artist, String summary, String bitRate, String path, boolean isPlaying) {
        this.pic = pic;
        this.musicName = musicName;
        this.artist = artist;
        this.summary = summary;
        this.bitRate = bitRate;
        this.path = path;
        this.isPlaying = isPlaying;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBitRate() {
        return bitRate;
    }

    public void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
