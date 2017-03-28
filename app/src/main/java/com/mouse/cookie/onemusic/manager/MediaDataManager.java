package com.mouse.cookie.onemusic.manager;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

/**
 * Created by xp on 17-3-15.
 */

public class MediaDataManager {

    private Context mContext;

    private MediaMetadataRetriever mediaMetadataRetriever;

    public MediaDataManager(Context context) {
        this.mContext = context;
        mediaMetadataRetriever = new MediaMetadataRetriever();
    }

    public void setResource(String path){
        mediaMetadataRetriever.setDataSource(path);
    }

    public void setResource(Uri uri){
        mediaMetadataRetriever.setDataSource(mContext, uri);
    }

    //描述    0
    public String getDescribing(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
    }

    //相册    1
    public String getAlbum(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
    }

    //艺术家   2
    public String getArtist(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
    }

    //作者    3
    public String getAuthor(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
    }

    //作曲家    4
    public String getComposer(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
    }

    //修改日期  5
    public String getModifyData(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
    }

    //类别    6
    public String getGenre(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
    }

    //标题    7
    public String getTitle(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
    }

    //修改年份  8
    public String getModifyYear(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
    }

    //时长    9
    public String getDuration(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    }

    //音轨    10
    public String getStrack(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS);
    }

    //作词人   11
    public String getLyricist(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER);
    }

    //演员    12
    public String getMineType(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
    }

    //艺术家相关 13
    public String getAlbumArtist(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
    }

    //音频来源  14
    public String getDiscNumber(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER);
    }

    //未知(编缉)    15
    public String getCompilation(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPILATION);
    }

    //未知(音频内容)  16
    public String getAudio(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
    }

    //未知(视频内容)  17
    public String getVideo(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
    }

    //视频宽度  18
    public String getVideoWidth(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
    }

    //视频高度  19
    public String getVideoHeight(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
    }

    //比特率   20
    public String getBitRate(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
    }

    //未知(定位)    23
    public String getLocation(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
    }

    //视频旋转角度    24
    public String getVideoRotation(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
    }

    //帧率    25
    public String getFrameRate(){
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE);
    }

    //专辑封面  26
    public byte[] getEmbeddedPicture(){
        return mediaMetadataRetriever.getEmbeddedPicture();
    }
}




























