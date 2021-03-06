package com.mouse.cookie.onemusic.data;

/**
 * Created by cookie on 17-3-15.
 */

public class Msg {
    public final static String MSG_HANDLER = "com.mouse.cookie.onemusic.handler";
    public final static int MSG_WHAT = 0;

    public final static int MSG_ERROR = 1;

    public final static int MSG_UPDATE = 2; //更新界面，在ContentActivity里使用
    public final static int MSG_START = 3; //更新界面，在ContentActivity里使用
    public final static int MSG_CYCLE = 4;  //循环发送广播，在PlayerService里使用
    public final static int MSG_PAUSE = 5;  //循环发送广播，在PlayerService里使用
    public final static int MSG_STOP = 6;  //循环发送广播，在PlayerService里使用

    public final static int MSG_NO_MUSIC = 7;   //没有音乐资源，在PlayerService里使用
}
