package com.ssiot.remote.expert;

import java.util.List;

import com.ssiot.agri.R;
import com.ssiot.remote.data.model.FishPartModel;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FishPartAdapter extends BaseAdapter{
	private List<FishPartModel> mData;
	LayoutInflater mInflater;
	Context mContext;
	
	public FishPartAdapter(Context c,List<FishPartModel> list){
		mContext = c;
		mData = list;
		mInflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.itm_fish_part_text, parent,false);
		TextView t = (TextView) convertView.findViewById(R.id.part_txtview);
		FishPartModel partModel = mData.get(position);
		int checkedCount = 0;
		for (int i = 0; i < partModel._symptoms.size(); i ++){
			if (partModel._symptoms.get(i).isChecked){
				checkedCount ++;
			}
		}
		t.setText(partModel._name + (checkedCount > 0 ? "("+checkedCount+")" : ""));
		return convertView;
	}
	
}