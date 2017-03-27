package com.ssiot.remote.yun.admin;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ssiot.agri.R;
import com.ssiot.fish.HeadActivity;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.FacilityModel;
import com.ssiot.remote.data.model.LandModel;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.unit.AllListView;
import com.ssiot.remote.yun.webapi.WS_API;
import com.ssiot.remote.yun.webapi.WS_Land;

public class DeviceTagEditorAct extends HeadActivity{
	private static final String tag = "DeviceTagEditorAct";
	FacilityModel mFacilityModel;
	List<YunNodeModel> allNodes;
	ArrayList<YunNodeModel> containedDevices = new ArrayList<YunNodeModel>();
	ArrayList<YunNodeModel> notContainedDevices = new ArrayList<YunNodeModel>();
	EditText mFacilityNameEdit;
	AllListView containedListView;
	AllListView notContainedListView;
	NodeAdapter containedAdapter;
	NodeAdapter notContainedAdapter;
	
	LayoutInflater inflater;
	
	private static final int MSG_GET_END = 1;
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GET_END:
				containedDevices.clear();
				if (null != allNodes){
					for (YunNodeModel y : allNodes){
						if (y.mFacilityID > 0){
							containedDevices.add(y);
						} else {
							notContainedDevices.add(y);
						}
					}
				}
				if (null != containedAdapter && null != notContainedAdapter){
					containedAdapter.notifyDataSetChanged();
					notContainedAdapter.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideActionBar();
		mFacilityModel = (FacilityModel) getIntent().getSerializableExtra("facilitymodel");
		inflater = LayoutInflater.from(this);
		if (mFacilityModel == null){
			showToast("数据不完整，请重试");
			return;
		}
		setContentView(R.layout.activity_edit_device_tag);
		initViews();
		initTitleBar();
		new GetNodesThread().start();
	}
	
	private void initViews(){
		mFacilityNameEdit = (EditText) findViewById(R.id.name_edit);
		containedListView = (AllListView) findViewById(R.id.tag_owned_container);
		notContainedListView = (AllListView) findViewById(R.id.tag_recommend_container);
		mFacilityNameEdit.setText(mFacilityModel._facilitiesname);
		containedAdapter = new NodeAdapter(containedDevices,false);
		notContainedAdapter = new NodeAdapter(notContainedDevices,true);
		containedListView.setAdapter(containedAdapter);
		notContainedListView.setAdapter(notContainedAdapter);
		containedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				notContainedDevices.add(containedDevices.get(position));
				containedDevices.remove(position);
				notifyChanged();
			}
		});
		notContainedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				containedDevices.add(notContainedDevices.get(position));
				notContainedDevices.remove(position);
				notifyChanged();
			}
		});
	}
	
	private void initTitleBar(){
		TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	for (YunNodeModel y : allNodes){
            		if (y.mFacilityID > 0){
            			if (!containsInArray(y, containedDevices)){
            				new Thread(new Runnable() {
								
								@Override
								public void run() {
									sendToast("TODO");//TODO
								}
							}).start();
            			}
            		} else if (containsInArray(y, containedDevices)){
            			new Thread(new Runnable() {
							
							@Override
							public void run() {
								if (true){
									sendToast("成功？");
								}
							}
						}).start();
            		}
            	}
            }
        });
        initTitleLeft(R.id.title_bar_left);
	}
	
	private boolean containsInArray(YunNodeModel y, List<YunNodeModel> list){
		for (YunNodeModel tmp : list){
			if (y.equals(tmp)){
				return true;
			}
		}
		return false;
	}
	
	private class GetNodesThread extends Thread{
		@Override
		public void run() {
			String account = Utils.getStrPref(Utils.PREF_USERNAME, DeviceTagEditorAct.this);
			allNodes = new WS_API().GetFirstPageShort(account, 3);
			mHandler.sendEmptyMessage(MSG_GET_END);
		}
	}
	
	private class NodeAdapter extends BaseAdapter{
		ArrayList<YunNodeModel> nodes;
		boolean isaddicon;
		private NodeAdapter(ArrayList<YunNodeModel> ns,boolean isAdd){
			nodes = ns;
			isaddicon = isAdd;
		}
		
		@Override
		public int getCount() {
			return nodes.size();
		}

		@Override
		public Object getItem(int position) {
			return nodes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.itm_device_tag, null, false);
			TextView t = (TextView) convertView.findViewById(R.id.title);
			YunNodeModel y = nodes.get(position);
			t.setText("" + y.nodeStr + "("+y.mNodeNo+")");
			if (isaddicon){
				t.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.client_all_column_add),
						null, null, null);
			} else {
				t.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.client_all_column_remove),
						null, null, null);
			}
			
			return convertView;
		}
	}
	
	private void notifyChanged(){
		if (containedAdapter != null){
			containedAdapter.notifyDataSetChanged();
		}
		if (notContainedAdapter != null){
			notContainedAdapter.notifyDataSetChanged();
		}
	}
}