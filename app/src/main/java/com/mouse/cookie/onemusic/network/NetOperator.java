package com.mouse.cookie.onemusic.network;

import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by cookie on 17-5-9.
 */

public class NetOperator {
//根据歌名和时长搜歌词    http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=We%20are%20young&duration=250000
//根据上面的结果得到歌词    http://lyrics.kugou.com/download?ver=1&client=pc&id=21978379&accesskey=8D68BC5961F2FE6B2CF94BF878938BA8&fmt=lrc&charset=utf8

    private final static String TAG = "NetOperator";

    private final static String URL_SEARCH = "http://lyrics.kugou.com/search";
    private final static String URL_CONTENT = "http://lyrics.kugou.com/download";

    private OkHttpClient mOkHttpClient;
    private Call mCall;
    private Request mRequest;

    private static NetOperator mNetOperator;

    private NetOperator() {
        mOkHttpClient = new OkHttpClient();
    }

    public static NetOperator getInstance() {
        if (null == mNetOperator) {
            synchronized (NetOperator.class) {
                if (null == mNetOperator) {
                    mNetOperator = new NetOperator();
                }
            }
        }
        return mNetOperator;
    }

    public void searchLyric(String song, String mills, Callback callback) {
        Request.Builder builder = new Request.Builder();
        builder.url(URL_SEARCH + "?ver=1&man=yes&client=pc" + "&keyword=" + song + "&duration=" + mills);
        mRequest = builder.build();
        Log.d(TAG, "searchLyric: url-->" + mRequest.url());
        mCall = mOkHttpClient.newCall(mRequest);
        mCall.enqueue(callback);
    }

    public void downloadLyric(String proposal, String accesskey, Callback callback) {
        Request.Builder builder = new Request.Builder();
        builder.url(URL_CONTENT + "?ver=1&client=pc&fmt=lrc&charset=utf8" + "&id=" + proposal + "&accesskey=" + accesskey);
        mRequest = builder.build();
        Log.d(TAG, "downloadLyric: url-->" + mRequest.url());
        mCall = mOkHttpClient.newCall(mRequest);
        mCall.enqueue(callback);
    }
}
