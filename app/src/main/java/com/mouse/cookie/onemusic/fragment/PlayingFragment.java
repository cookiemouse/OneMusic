package com.mouse.cookie.onemusic.fragment;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mouse.cookie.onemusic.FastBlur;
import com.mouse.cookie.onemusic.R;
import com.mouse.cookie.onemusic.activity.ContentActivity;

public class PlayingFragment extends Fragment {

    private static final String TAG = "PlayingFragment";

    private LinearLayout mLinearLayout;
    private ImageView mImageViewIcon;
    private Button mButtonPlayOrPause, mButtonUp, mButtonNext;
    private TextView mTextViewCurrent, mTextViewTotal;
    private SeekBar mSeekBar;

    //当seekbar被托动时，不再改变进度
    private boolean isSeeked;

    private ContentActivity mContentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View viewPlaying = inflater.inflate(R.layout.fragment_playing, container, false);

        initView(viewPlaying);

        return viewPlaying;
    }

    private void initView(View view) {
        mContentActivity = (ContentActivity) getActivity();

        mLinearLayout = (LinearLayout) view.findViewById(R.id.ll_fragment_playing);
        mImageViewIcon = (ImageView) view.findViewById(R.id.iv_fragment_playing_icon);
        mButtonPlayOrPause = (Button) view.findViewById(R.id.btn_layout_playing_pause_play);
        mButtonNext = (Button) view.findViewById(R.id.btn_layout_playing_next);
        mButtonUp = (Button) view.findViewById(R.id.btn_layout_playing_up);
        mTextViewCurrent = (TextView) view.findViewById(R.id.tv_layout_playing_seek_current);
        mTextViewTotal = (TextView) view.findViewById(R.id.tv_layout_playing_seek_total);
        mSeekBar = (SeekBar) view.findViewById(R.id.sb_layout_playing_seek);

        //事件监听
        mButtonPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentActivity.play();
            }
        });

        mButtonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentActivity.playUp();
            }
        });

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentActivity.playNext();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextViewCurrent.setText(formatTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeked = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeked = false;
                mContentActivity.setPlayProgress(seekBar.getProgress());
            }
        });
    }

    public void updateSeek(int progress, int duration) {
        if (null != mSeekBar && null != mTextViewCurrent && null != mTextViewTotal && !isSeeked) {
            mSeekBar.setMax(duration);
            mSeekBar.setProgress(progress);
            mTextViewCurrent.setText(formatTime(progress));
            mTextViewTotal.setText(formatTime(duration));
        }
    }

    //更新UI
    public void updateUI(boolean select, final Bitmap bitmap) {

        if (null == mButtonPlayOrPause || null == mImageViewIcon) {
            return;
        }

        if (null != bitmap) {
            mImageViewIcon.setImageBitmap(bitmap);
//            bitmap = FastBlur.doBlur(bitmap, 20, true);

            mImageViewIcon.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mImageViewIcon.getViewTreeObserver().removeOnPreDrawListener(this);
                    mImageViewIcon.buildDrawingCache();

                    Bitmap bmp = mImageViewIcon.getDrawingCache();

                    bmp = FastBlur.doBlur(bmp, 200, true);
                    BitmapDrawable bd = new BitmapDrawable(bmp);
                    mLinearLayout.setBackground(bd);
                    return true;
                }
            });

//            mLinearLayout.setBackground();
        }
//        mTextViewTitle.setText(title);
//        mTextViewArtist.setText(artist);

        if (select) {
            mButtonPlayOrPause.setSelected(true);
        } else {
            mButtonPlayOrPause.setSelected(false);
        }

//        mButtonUp.setClickable(true);
//        mButtonNext.setClickable(true);
//
//        if (0 == current) {
//            mButtonUp.setClickable(false);
//        }
//        if (current == cursor.getColumnCount()) {
//            mButtonNext.setClickable(false);
//        }
    }

    private String formatTime(int time) {
        String s_min = "0", s_sec = "0";
        int min = time / 60000;
        int sec = time / 1000 % 60;
        if (min >= 0 && min < 10) {
            s_min += min;
        } else {
            s_min = "" + min;
        }
        if (sec < 10) {
            s_sec += sec;
        } else {
            s_sec = "" + sec;
        }
        return s_min + ":" + s_sec;
    }

}
