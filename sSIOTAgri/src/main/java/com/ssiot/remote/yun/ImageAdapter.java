package com.ssiot.remote.yun;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ImageAdapter extends BaseAdapter {
    ArrayList localArrayList;
    private Context mContext;

    public ImageAdapter(Context paramContext, ArrayList paramArrayList) {
        this.mContext = paramContext;
        this.localArrayList = paramArrayList;
    }

    public int getCount() {
        return this.localArrayList.size();
    }

    public Object getItem(int paramInt) {
        return this.localArrayList.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        if (paramView == null){
            paramView = ((Activity) this.mContext).getLayoutInflater().inflate(R.layout.layout_gridview_item, null);
        }
//        HashMap localHashMap = (HashMap) this.localArrayList.get(paramInt);
        CellModel cellModel = (CellModel) this.localArrayList.get(paramInt);
        ImageView localImageView1 = (ImageView) paramView.findViewById(R.id.imageView_ItemImage);
        RelativeLayout localRelativeLayout = (RelativeLayout) paramView.findViewById(R.id.relativeLayout2);
        TextView localTextView = (TextView) paramView.findViewById(R.id.textView_ItemText);
        ViewGroup.LayoutParams localLayoutParams1 = localRelativeLayout.getLayoutParams();
        localLayoutParams1.height = (10 + HeadActivity.width / 7);
        localLayoutParams1.width = localLayoutParams1.height;
        localRelativeLayout.setLayoutParams(localLayoutParams1);
        ViewGroup.LayoutParams localLayoutParams2 = localImageView1.getLayoutParams();
        localLayoutParams2.height = (HeadActivity.width / 7);
        localLayoutParams2.width = localLayoutParams2.height;
        localImageView1.setLayoutParams(localLayoutParams2);
//        localImageView1.setImageResource(((Integer) localHashMap.get("itemImage")).intValue());
//        localTextView.setText((String) localHashMap.get("itemText"));
        localImageView1.setImageResource(cellModel.itemImage);
        localTextView.setText(cellModel.itemText);
        localTextView.setSingleLine(true);
        localTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        ImageView localImageView2 = (ImageView) paramView.findViewById(2131034372);//红点
//        if ((localHashMap.get("isgengxin") == null)
//                || (!(localHashMap.get("isgengxin").equals("true")))){
//            localImageView2.setVisibility(4);
//        }
        return paramView;
    }
}
