package com.mouse.cookie.onemusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mouse.cookie.onemusic.R;
import com.mouse.cookie.onemusic.data.MusicListAdapterData;

import java.util.List;

/**
 * Created by cookie on 17-3-28.
 */

public class MusicListAdapter extends BaseAdapter {

    private Context mContext;
    private List<MusicListAdapterData> musicListAdapterDataList;

    public MusicListAdapter(Context mContext, List<MusicListAdapterData> musicListAdapterDataList) {
        this.mContext = mContext;
        this.musicListAdapterDataList = musicListAdapterDataList;
    }

    @Override
    public int getCount() {
        return musicListAdapterDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicListAdapterDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MusicListAdapterData data = musicListAdapterDataList.get(position);
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_musiclist, null);
            viewHolder.imageViewPlaying = (ImageView) convertView.findViewById(R.id.iv_adapter_filemanager_playing);
            viewHolder.imageViewAblum = (ImageView) convertView.findViewById(R.id.iv_adapter_musiclist_playing);
            viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.tv_adapter_musiclist_title);
            viewHolder.textViewSummary = (TextView) convertView.findViewById(R.id.tv_adapter_musiclist_summary);
            viewHolder.textViewArtist = (TextView) convertView.findViewById(R.id.tv_adapter_musiclist_artist);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.isPlaying()) {
            viewHolder.imageViewPlaying.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageViewPlaying.setVisibility(View.GONE);
        }
        viewHolder.imageViewAblum.setImageBitmap(data.getPic());
        viewHolder.textViewTitle.setText(data.getMusicName());
        viewHolder.textViewSummary.setText(data.getSummary());
        viewHolder.textViewArtist.setText(data.getArtist());

        return convertView;
    }

    private class ViewHolder {
        ImageView imageViewAblum, imageViewPlaying;
        TextView textViewTitle, textViewSummary, textViewArtist;
    }
}
