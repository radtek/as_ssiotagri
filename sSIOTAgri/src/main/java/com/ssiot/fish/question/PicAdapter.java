package com.ssiot.fish.question;

import android.R.mipmap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ssiot.agri.R;
import com.ssiot.remote.GetImageThread;
import com.ssiot.remote.view.SquareImageView;

import java.util.ArrayList;

public class PicAdapter extends BaseAdapter{
    ArrayList<String> mImgPaths;
    private Context mContext;
    private Handler mHandler;
    LayoutInflater mInflater;
    String FTP_PATH = QuestionNewActivity.FTP_QUESTION_PATH;

    public PicAdapter(Context context, ArrayList<String> imgPaths,Handler handler){
        mImgPaths = imgPaths;
        mContext = context;
        mHandler = handler;
        mInflater = LayoutInflater.from(mContext);
    }
    
    public PicAdapter(Context context, ArrayList<String> imgPaths,Handler handler, String ftpPath){
        mImgPaths = imgPaths;
        mContext = context;
        mHandler = handler;
        mInflater = LayoutInflater.from(mContext);
        FTP_PATH = ftpPath;
    }
    
    @Override
    public int getCount() {
        return mImgPaths.size();//最后一个的add
    }

    @Override
    public Object getItem(int position) {
        return mImgPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
//            imageView = new SquareImageView(mContext);
            View v = mInflater.inflate(R.layout.item_image, parent,false);//TODO 不方
//            imageView.setScaleType(ScaleType.CENTER_CROP);
//            AbsListView.LayoutParams vlp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            imageView.setLayoutParams(vlp);
            imageView = (ImageView) v.findViewById(R.id.imageview);
            convertView = v;
        String path = mImgPaths.get(position);
        new GetImageThread(imageView, "http://" + FTP_PATH + path, mHandler).start();//注意 handler中必须是2
        return convertView;
    }
    
}