package com.ssiot.remote.yun.manage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.ssiot.agri.R;
import com.ssiot.remote.data.model.ERPTaskModel;
import com.ssiot.remote.yun.webapi.TaskInstance;

import java.util.List;

public class TaskListAdapter extends BaseAdapter {
    Context mContext;
    List<ERPTaskModel> mData;
    private LayoutInflater mInflater;
    OnEnableTaskListener listener;

    public TaskListAdapter(List<ERPTaskModel> paramArrayList, Context c) {
//        mData = paramArrayList;
//        mContext = c;
//        mInflater = ((LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        this(paramArrayList,c,null);
    }
    
    public TaskListAdapter(List<ERPTaskModel> paramArrayList, Context c, OnEnableTaskListener listen) {
        mData = paramArrayList;
        mContext = c;
        mInflater = ((LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        listener = listen;
    }

    private View createViewFromResource(int position, View paramView, ViewGroup paramViewGroup) {
        final VHolder vHolder;
        if (paramView == null) {
            paramView = mInflater.inflate(R.layout.itm_erptask, paramViewGroup, false);
            vHolder = new VHolder();
            vHolder.stageTextView = ((TextView) paramView.findViewById(R.id.task_stage));
            vHolder.typeTextView = ((TextView) paramView.findViewById(R.id.task_type));
            vHolder.detailTextView = (TextView) paramView.findViewById(R.id.task_detail);
            vHolder.ownerTextView = (TextView) paramView.findViewById(R.id.task_owner);
            vHolder.enableBtn = (Button) paramView.findViewById(R.id.task_enable);
            paramView.setTag(vHolder);
        } else {
            vHolder = (VHolder) paramView.getTag();
        }
        if (mData != null) {
            final ERPTaskModel d = ((ERPTaskModel) mData.get(position));// TODO
            vHolder.stageTextView.setText(""+d._StageName);
            vHolder.typeTextView.setText(""+d._TypeName);
            vHolder.detailTextView.setText(d._taskdetail);
//            vHolder.ownerTextView.setText(d._ownerid == 0 ? "平台" : d._UserName);
            if (null != listener && d.enabled == false){
	            vHolder.enableBtn.setVisibility(View.VISIBLE);
	            vHolder.enableBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null != listener){
							listener.onEnableTask(d);
						}
					}
				});
            } else {
            	vHolder.enableBtn.setVisibility(View.GONE);
            }
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

    public class VHolder {
        public TextView stageTextView;
        public TextView typeTextView;
        public TextView detailTextView;
        public TextView ownerTextView;
        public Button enableBtn;
    }
    
    public interface OnEnableTaskListener{
    	void onEnableTask(ERPTaskModel taskmodel);
    }
}