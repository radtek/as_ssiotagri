package com.ssiot.remote.expert;

import java.util.List;

import com.ssiot.agri.R;
import com.ssiot.remote.data.model.DiseaseModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DiseaseListAdapter extends BaseAdapter{
	LayoutInflater inflater;
	List<DiseaseModel> mDatas;
	
	public DiseaseListAdapter(Context c, List<DiseaseModel> data){
		inflater = LayoutInflater.from(c);
		mDatas = data;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.itm_disease, null, false);
		TextView nameView = (TextView) convertView.findViewById(R.id.disease_name);
		TextView proView = (TextView) convertView.findViewById(R.id.disease_pro);
		TextView detailView = (TextView) convertView.findViewById(R.id.disease_detail);
		DiseaseModel diseaseModel = mDatas.get(position);
		nameView.setText(diseaseModel._name);
		proView.setText(""+diseaseModel._probability +"%");
		detailView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO  或者onitemclickListener
			}
		});
		return convertView;
	}
	
}