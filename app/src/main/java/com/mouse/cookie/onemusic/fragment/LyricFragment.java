package com.mouse.cookie.onemusic.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mouse.cookie.onemusic.R;

public class LyricFragment extends Fragment {

    private View mViewLyric;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewLyric = inflater.inflate(R.layout.fragment_lyric, container, false);
        return mViewLyric;
    }
}
