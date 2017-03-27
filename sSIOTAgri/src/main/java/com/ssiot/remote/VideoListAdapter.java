package com.ssiot.remote;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssiot.remote.data.model.VLCVideoInfoModel;

import java.util.List;
import com.ssiot.agri.R;
import com.videogo.ui.realplay.EZPrepareAct;

public class VideoListAdapter extends BaseAdapter{
    private static String tag = "VideoListAdapter";
    private List<VLCVideoInfoModel> mDataList;
    private LayoutInflater mInflater;
    private Context mContext;
    
    public VideoListAdapter(Context c,List<VLCVideoInfoModel> ss){
        Log.v(tag, "----------videolistsize:"+ss.size());
        mContext = c;
        mDataList = ss;
        mInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.videolist_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.videolist_icon);
            holder.textView = (TextView) convertView.findViewById(R.id.videoitem_text);
            holder.video_type = (TextView) convertView.findViewById(R.id.video_type);
            holder.typeText = (TextView) convertView.findViewById(R.id.videoitem_type);
//            holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
            holder.status = (ImageView) convertView.findViewById(R.id.status);
            holder.ysYun = (TextView) convertView.findViewById(R.id.startysyun);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final VLCVideoInfoModel vModel = mDataList.get(position);
        holder.imageView.setImageResource(vModel._devicetype == 0 ? R.drawable.ic_section_surveillance : R.drawable.ic_section_surveillance_ball);
        holder.textView.setText(vModel._address.trim());
        holder.video_type.setText("设备类型：" +vModel._type);
        holder.typeText.setText(vModel._type);
        if(vModel.status == 1){
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setImageResource(R.drawable.connect_ok_green);
        } else if (vModel.status == 2){
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setImageResource(R.drawable.connect_fail_2);
        } else {
            holder.status.setVisibility(View.INVISIBLE);
        }
        if (vModel._ssiotezviz){
        	holder.status.setVisibility(View.GONE);
        }
        holder.ysYun.setOnClickListener(null);
        if (vModel._ssiotezviz){
            holder.ysYun.setVisibility(View.VISIBLE);
            holder.ysYun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ysPrepareAct = new Intent(mContext,EZPrepareAct.class);
                    ysPrepareAct.putExtra("deviceserial", vModel._serialno);
                    ysPrepareAct.putExtra("verifycode", "");
                    mContext.startActivity(ysPrepareAct);
                }
            });
        } else {
            holder.ysYun.setVisibility(View.GONE);
        }
        
        return convertView;
    }
    
    private class ViewHolder{
        ImageView imageView;
        TextView textView;
        TextView video_type;
        TextView typeText;
//        ImageView arrow;
        ImageView status;
        TextView ysYun;
    }
    
}