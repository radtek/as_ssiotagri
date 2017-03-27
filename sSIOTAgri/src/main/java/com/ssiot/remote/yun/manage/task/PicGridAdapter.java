package com.ssiot.remote.yun.manage.task;

import android.R.mipmap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ssiot.agri.R;

import java.util.ArrayList;

public class PicGridAdapter extends BaseAdapter{
    ArrayList<Bitmap> mImgs;
    private Context mContext;
    LayoutInflater mInflater;

    public PicGridAdapter(Context context, ArrayList<Bitmap> imgs){
        mImgs = imgs;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }
    
    @Override
    public int getCount() {
        return mImgs.size() + 1;//最后一个的add
    }

    @Override
    public Object getItem(int position) {
        return mImgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        convertView = mInflater.inflate(R.layout.item_image, parent,false);
        imageView = (ImageView) convertView.findViewById(R.id.imageview);
//        imageView.setScaleType(ScaleType.CENTER_CROP);
        if (position < mImgs.size()){
//            Bitmap bmp = BitmapFactory.decodeFile(mImgs.get(position));
            Bitmap bmp = mImgs.get(position);
            imageView.setImageBitmap(bmp);
        } else {
            imageView.setImageResource(R.drawable.icon_image_add);
        }
        return convertView;
    }
    
}