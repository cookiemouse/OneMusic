package com.mouse.cookie.onemusic.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mouse.cookie.onemusic.R;

import me.wcy.lrcview.LrcView;

public class LyricFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewLyric = inflater.inflate(R.layout.fragment_lyric, container, false);
        return viewLyric;
    }

    private void initView(View view){
        LrcView lrcView = (LrcView) view.findViewById(R.id.lv_fragment_lyric);
    }
}
