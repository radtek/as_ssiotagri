package com.ssiot.remote.expert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.webapi.WS_API;

public class FacilityNodeListAct extends HeadActivity{
	private static final String tag = "FacilityNodeListAct水质部分";
	
	ListView mFacilityListView;
	List<FaciModel> mFacilitNodes = new ArrayList<FaciModel>();//简单数据，没有任何传感器
	FacilityAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_facility_nodelist);
		initViews();
		new GetNodeThread().start();
	}
	
	private void initViews(){
		mFacilityListView = (ListView) findViewById(R.id.faci_list);
		mAdapter = new FacilityAdapter(mFacilitNodes);
		mFacilityListView.setAdapter(mAdapter);
	}
	
	private class GetNodeThread extends Thread{
		@Override
		public void run() {
			mFacilitNodes.clear();
			String account = Utils.getStrPref(Utils.PREF_USERNAME, FacilityNodeListAct.this);
			int deviceVersion = Utils.getIntPref(Utils.PREF_USERDEVICETYPE, FacilityNodeListAct.this);
			
			List<YunNodeModel> list = new WS_API().GetFirstPageShort(account, deviceVersion);
			if (null != list && list.size() > 0){
				for (YunNodeModel y : list){
					if (y.nodeType != DeviceBean.TYPE_SENSOR || y.mFacilityID <= 0){
						continue;
					}
					FaciModel f = existFaci(y.mFacilityID, mFacilitNodes);
					if (f != null){
						f.nodes.add(y);
					} else {
						Log.v(tag, "-------设施ID：" + y.mFacilityID);
						f = new FaciModel(y.mFacilityID, y.facilityStr);
						f.nodes.add(y);
						mFacilitNodes.add(f);
					}
				}
			}
			Log.v(tag, " ------------------facisize:"+mFacilitNodes.size());
			runOnUiThread(new Runnable() {
				public void run() {
					if (mFacilitNodes.size() == 0){
						sendToast("无设施信息，请先在云平台上添加设施。");
					}
					mAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	private FaciModel existFaci(int faciID, List<FaciModel> list){
		if (null != list){
			for (FaciModel f : list){
				if (f.faciID == faciID){
					return f;
				}
			}
		}
		return null;
	}
	
	private class FacilityAdapter extends BaseAdapter{
		List<FaciModel> mDatas;
		LayoutInflater inflater;
		public FacilityAdapter(List<FaciModel> list){
			mDatas = list;
			inflater = LayoutInflater.from(FacilityNodeListAct.this);
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
			ViewHold vHold;
			if (convertView == null){
				convertView = inflater.inflate(R.layout.itm_facility, null, false);
				vHold = new ViewHold();
				vHold.titleView = (TextView) convertView.findViewById(R.id.facility_name);
//				vHold.editBtn = (Button) convertView.findViewById(R.id.water_edit);
				vHold.newBtn = (Button) convertView.findViewById(R.id.water_new);
				vHold.mNodeListView = (ListView) convertView.findViewById(R.id.node_list);
				convertView.setTag(vHold);
			} else {
				vHold = (ViewHold) convertView.getTag();
			}
			final FaciModel y = mDatas.get(position);
			vHold.titleView.setText(y.facilityStr);
			NodeAdapter nA = new NodeAdapter(y.nodes);
			vHold.mNodeListView.setAdapter(nA);//TODO 全部展开的问题，可能没体现出来
//			vHold.mNodeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view,
//						int position, long id) {
//					
//				}
//			});
//			vHold.editBtn.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					
//				}
//			});
			vHold.newBtn.setVisibility(y.faciID > 0 ? View.VISIBLE : View.GONE);
			vHold.newBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(FacilityNodeListAct.this, ManualDataEditAct.class);
					Log.v(tag, "-----------设施ID："+y.faciID);
					i.putExtra("facilityid", y.faciID);
					startActivity(i);
				}
			});
			return convertView;
		}
		
		private class ViewHold{
			TextView titleView;
			Button newBtn;
			Button editBtn;
			ListView mNodeListView;
		}
	}
	
	private void showHisPickDialog(){//TODO 已经填写的需要加值或修改。
		
	}
	
	private class NodeAdapter extends BaseAdapter{
		List<YunNodeModel> mYNodes;
		private NodeAdapter(List<YunNodeModel> ylist){
			mYNodes = ylist;
		}

		@Override
		public int getCount() {
			return mYNodes.size();
		}

		@Override
		public Object getItem(int position) {
			return mYNodes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView t = new TextView(FacilityNodeListAct.this);
			t.setTextAppearance(FacilityNodeListAct.this, R.style.SetContentText);
			t.setPadding(5, 5, 5, 5);
			convertView = t;
			t.setText(mYNodes.get(position).nodeStr);
			return convertView;
		}
		
	}
	
	private class FaciModel {
		private FaciModel(int faciId, String faciStr){
			faciID = faciId;
			facilityStr = faciStr;
			nodes = new ArrayList<YunNodeModel>();
		}
		public int faciID;
		public String facilityStr;
		public List<YunNodeModel> nodes;
	}
}