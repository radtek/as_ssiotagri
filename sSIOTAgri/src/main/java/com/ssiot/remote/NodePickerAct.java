package com.ssiot.remote;

import java.util.ArrayList;
import java.util.List;

import com.ssiot.remote.data.model.NodeModel;
import com.ssiot.remote.yun.webapi.WS_TraceProject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;

public class NodePickerAct extends HeadActivity{
	
	CheckAdapter mAdapter;
	ListView mNodeList;
	List<NodeBean> mStatusList = new ArrayList<NodeBean>();
	
	private static final int MSG_GET_END = 1;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GET_END:
				mAdapter.notifyDataSetChanged();
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
		setContentView(R.layout.act_node_pick);
		mNodeList = (ListView) findViewById(R.id.node_list);
		mAdapter = new CheckAdapter(mStatusList);
		mNodeList.setAdapter(mAdapter);
		new GetNodesThread().start();
		initTitleBar();
	}
	
	private void initTitleBar(){
		TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	String str = "";
            	for (int i = 0; i < mStatusList.size(); i ++){
            		if (mStatusList.get(i).isSelected){
            			str += mStatusList.get(i).mNodeModel._nodeno + ",";
            		}
            	}
            	if (str.endsWith(",")){
            		str = str.substring(0, str.length() - 1);
            	}
            	
            	SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(NodePickerAct.this);
            	String account = Utils.getStrPref(Utils.PREF_USERNAME, NodePickerAct.this);
            	Editor e = mPref.edit();
            	e.putString(account + Utils.PREF_OFFLINE_NOTICE, str);
            	e.putString(Utils.PREF_LAST_OFFLINE, "");
            	e.commit();
            	finish();
            }
        });
        TextView titleLeft = (TextView) findViewById(R.id.title_bar_left);
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}
	
	private class CheckAdapter extends BaseAdapter{
		
		private List<NodeBean> list;
		
		public CheckAdapter(List<NodeBean> tlist){
			list = tlist;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_node_check, null);
			ViewHolder vh = new ViewHolder(v, position);
			return v;
		}
		
		private class ViewHolder{
			public ImageView imageViewIcon;
			public TextView mTitleView;
			public CheckBox mCkBox;
			
			public ViewHolder(View v, final int position) {
	            imageViewIcon = (ImageView) v.findViewById(R.id.imageViewIcon);
	            mTitleView = (TextView) v.findViewById(R.id.textViewOrg);
	            mTitleView.setText(mStatusList.get(position).mNodeModel._location + " " + mStatusList.get(position).mNodeModel._nodeno);
	            mCkBox = (CheckBox) v.findViewById(R.id.checkBoxSel);
	            mCkBox.setOnCheckedChangeListener(null);
	            mCkBox.setChecked(mStatusList.get(position).isSelected);
	            mCkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	                @Override    
	                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	                    mStatusList.get(position).isSelected = isChecked;
	                }
	            });
	        }
		}
        
    }
	
	public class NodeBean{
		public NodeModel mNodeModel;
		public boolean isSelected = false;
		
		public NodeBean(NodeModel nm, boolean isSelec){
			mNodeModel = nm;
			isSelected = isSelec;
		}
	}
	
	private class GetNodesThread extends Thread{
		@Override
		public void run() {
			String account = Utils.getStrPref(Utils.PREF_USERNAME, NodePickerAct.this);
			List<NodeModel> nl = new WS_TraceProject().GetAllNodes(account);
			String offlinepref = Utils.getStrPref(account + Utils.PREF_OFFLINE_NOTICE, NodePickerAct.this);
			String[] nodenos = offlinepref.split(",");
			mStatusList.clear();
			for (int i = 0; i < nl.size(); i ++){
				mStatusList.add(new NodeBean(nl.get(i), containInPref(nodenos, nl.get(i)._nodeno)));
			}
			mHandler.sendEmptyMessage(MSG_GET_END);
		}
	}
	
	private boolean containInPref(String[] nodenos, int nodeno){
		if (null != nodenos && nodenos.length> 0){
			for (int i = 0 ; i < nodenos.length; i ++){
				if (TextUtils.isEmpty(nodenos[i])){
					continue;
				}
				int tmpno = Integer.parseInt(nodenos[i]);
				if (tmpno ==nodeno){
					return true;
				}
			}
		}
		return false;
	}
}