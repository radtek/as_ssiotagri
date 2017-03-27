
package com.ssiot.remote.yun.sta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ssiot.agri.R;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.TintedBitmapDrawable;

import java.util.ArrayList;
import java.util.HashMap;

public class StaAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<DeviceBean> mData;
    private LayoutInflater mInflater;
    HashMap<String, Object> map;

    public StaAdapter(ArrayList<DeviceBean> paramArrayList, Context c) {
        mData = paramArrayList;
        mContext = c;
        mInflater = LayoutInflater.from(c);
    }

    private View createViewFromResource(int position, View paramView, ViewGroup paramViewGroup) {
        ExpandableListHolder vHolder;
        if (paramView == null) {
            paramView = mInflater.inflate(R.layout.sta_item, paramViewGroup, false);
            vHolder = new ExpandableListHolder();
            vHolder.deviceName = ((TextView) paramView.findViewById(R.id.textViewName));
            vHolder.deviceIcon = ((ImageView) paramView.findViewById(R.id.imageViewIcon));
            paramView.setTag(vHolder);
        } else {
            vHolder = (ExpandableListHolder) paramView.getTag();
        }
        if (mData != null) {
            DeviceBean d = ((DeviceBean) mData.get(position));// TODO
            vHolder.deviceIcon.setImageResource(d.getIconRes());
            vHolder.deviceIcon.setImageDrawable(new TintedBitmapDrawable(mContext.getResources(), d.getIconRes(), R.color.white));
            vHolder.deviceIcon.setBackgroundResource(R.drawable.icbg_device_sta_actived);//icbg_device_sta_inactiv icbg_device_sta_actived
            vHolder.deviceName.setText(d.mName);
        }
        return paramView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int paramInt) {
        return mData.get(paramInt);
    }

    @Override
    public long getItemId(int paramInt) {
        return paramInt;
    }

    @Override
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        return createViewFromResource(paramInt, paramView, paramViewGroup);
    }

    public class ExpandableListHolder {
        public ImageView deviceIcon;
        public TextView deviceName;
    }
}
