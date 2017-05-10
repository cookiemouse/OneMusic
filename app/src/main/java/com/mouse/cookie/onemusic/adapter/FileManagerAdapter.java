package com.mouse.cookie.onemusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mouse.cookie.onemusic.R;
import com.mouse.cookie.onemusic.data.FileManagerAdapterData;
import com.mouse.cookie.onemusic.data.FileType;

import java.util.List;

/**
 * Created by cookie on 17-3-19.
 */

public class FileManagerAdapter extends BaseAdapter {

    private Context context;
    private List<FileManagerAdapterData> mDataList;

    public FileManagerAdapter(Context context, List<FileManagerAdapterData> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public FileManagerAdapterData getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder = null;
        FileManagerAdapterData data = mDataList.get(position);

        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_filemanager, null);
            mViewHolder = new ViewHolder();
            mViewHolder.mImageViewIcon = (ImageView) convertView.findViewById(R.id.iv_adapter_filemanager_icon);
            mViewHolder.mTextViewName = (TextView) convertView.findViewById(R.id.tv_adapter_filemanager_name);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        if (data.isDir()) {
            mViewHolder.mImageViewIcon.setImageResource(R.drawable.folder_64);
        } else {
            switch (data.getType()) {
                case FileType.MP3: {
                    mViewHolder.mImageViewIcon.setImageResource(R.drawable.mp3_64);
                    break;
                }
                case FileType.MID: {
                    mViewHolder.mImageViewIcon.setImageResource(R.drawable.mid_64);
                    break;
                }
                case FileType.WMA: {
                    mViewHolder.mImageViewIcon.setImageResource(R.drawable.wma_64);
                    break;
                }
                case FileType.JPG: {
                    mViewHolder.mImageViewIcon.setImageResource(R.drawable.jpg_64);
                    break;
                }
                case FileType.PNG: {
                    mViewHolder.mImageViewIcon.setImageResource(R.drawable.png_64);
                    break;
                }
                case FileType.ZIP: {
                    mViewHolder.mImageViewIcon.setImageResource(R.drawable.zip_64);
                    break;
                }
                case FileType.RAR: {
                    mViewHolder.mImageViewIcon.setImageResource(R.drawable.rar_64);
                    break;
                }
                case FileType.PDF: {
                    mViewHolder.mImageViewIcon.setImageResource(R.drawable.pdf_64);
                    break;
                }
                default: {
                    mViewHolder.mImageViewIcon.setImageResource(R.drawable.unknown_64);
                }
            }
        }
        mViewHolder.mTextViewName.setText(data.getName());

        return convertView;
    }

    private static class ViewHolder {
        ImageView mImageViewIcon;
        TextView mTextViewName;
    }
}
