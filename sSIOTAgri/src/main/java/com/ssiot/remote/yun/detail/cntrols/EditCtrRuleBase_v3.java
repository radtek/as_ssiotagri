package com.ssiot.remote.yun.detail.cntrols;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.webapi.WS_MQTT;

public class EditCtrRuleBase_v3 extends HeadActivity{
	YunNodeModel yModel;
	TextView mDevicesText;
	View mAddOnOff;
	ListView mOnOffListView;
	List<OnOffBean> mOnOffs = new ArrayList<OnOffBean>();
	OnOffAdapter mOnOffAdapter;
	LayoutInflater mInflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideActionBar();
		yModel = (YunNodeModel) getIntent().getSerializableExtra("yunnodemodel");
		setContentView(R.layout.rule_edit_devicepick_layout);
		initViews();
	}
	
	private void initViews(){
		initTitleBar();
		mDevicesText = (TextView) findViewById(R.id.txt_deviceselect);
		mAddOnOff = (View) findViewById(R.id.add_onoff);
		mOnOffListView = (ListView) findViewById(R.id.list_onoff);
		mOnOffAdapter = new OnOffAdapter(mOnOffs);
		mOnOffListView.setAdapter(mOnOffAdapter);
		
		mAddOnOff.setOnClickListener(new View.OnClickListener() {//添加开关动作
			@Override
			public void onClick(View v) {//TODO 改为dialog 然后添加进去
				showOnOffCheckDialog();
//				mOnOffs.add(new OnOffBean());
//				mOnOffAdapter.notifyDataSetChanged();
			}
		});
	}
	
	private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (true){
//            		new WS_MQTT().CtrRuleNow(nodeunique, devicenos, icmd, ispn);
            		showToast("正在开发");
            	} else {
            		showToast("填写完整");
            	}
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
	
	private void showOnOffCheckDialog(){
		AlertDialog.Builder bui = new AlertDialog.Builder(this);
		View v = LayoutInflater.from(this).inflate(R.layout.itm_onoff, null,false);
		final RadioGroup rg = (RadioGroup) v.findViewById(R.id.onoffgroup);
		final EditText e = (EditText) v.findViewById(R.id.edit_seconds);
		bui.setView(v);
		bui.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	OnOffBean bean = new OnOffBean();
            	int rbid = rg.getCheckedRadioButtonId();
            	switch (rbid) {
				case R.id.radio_on:
					bean.onoff = 1;
					break;
				case R.id.radio_off:
					bean.onoff = 0;
					break;
				case R.id.radio_keep:
					bean.onoff = 2;
					break;
				default:
					break;
				}
            	try {
            		bean.seconds = Integer.parseInt(e.getText().toString());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
            	if (bean.seconds > 0){
            		mOnOffs.add(bean);
    				mOnOffAdapter.notifyDataSetChanged();
            	} else {
            		showToast("填写错误");
            	}
            }
        });
		bui.create().show();
	}
	
	boolean[] pickStatus;
	String[] deviceNames;
	String showDevicesStr = "";
	public void ClickDevicePick(View v){
		if (null == yModel || yModel.list.size() == 0){
			return;
		}
		
		if (null == pickStatus || pickStatus.length == 0 || deviceNames == null){
			deviceNames = new String[yModel.list.size()];
			pickStatus = new boolean[yModel.list.size()];
			for (int i = 0; i < deviceNames.length; i ++){
				deviceNames[i] = yModel.list.get(i).mName;
				pickStatus[i] = false;
			}
		}
		
		AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setMultiChoiceItems(deviceNames,  pickStatus, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				pickStatus[which] = isChecked;
			}
		});
        bui.setTitle("选择被控设备").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	showDevicesStr = buildDevicesStr();
            	mDevicesText.setText(showDevicesStr);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
	}
	
	private String buildDevicesStr(){
		showDevicesStr = "";
		for (int i = 0; i < yModel.list.size(); i ++){
			if (pickStatus[i]){
				showDevicesStr += yModel.list.get(i).mName + ",";
			}
		}
		return showDevicesStr;
	}
	
	private class OnOffAdapter extends BaseAdapter{
		List<OnOffBean> datas;
		
		private OnOffAdapter(List<OnOffBean> data){
			datas = data;
			mInflater = LayoutInflater.from(EditCtrRuleBase_v3.this);
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView){
				convertView = mInflater.inflate(
						R.layout.itm_list_onoff, parent, false);
			} else {
				
			}
			if (datas.size() == 1){
				convertView.setBackgroundResource(R.drawable.bg_radius10);
			} else if (position == 0){
				convertView.setBackgroundResource(R.drawable.bg_optionlist_top_normal);
			} else if (position == datas.size()-1){
				convertView.setBackgroundResource(R.drawable.bg_optionlist_bottom_normal);
			} else {
				convertView.setBackgroundResource(R.drawable.bg_optionlist_mid_normal);
			}
			TextView operation = (TextView) convertView.findViewById(R.id.operation);
			TextView seconds = (TextView) convertView.findViewById(R.id.seconds);
			Button deleteBtn = (Button) convertView.findViewById(R.id.btn_delete);
			final OnOffBean b = datas.get(position);
			operation.setText(b.getOnOffStr());
			seconds.setText(""+b.seconds);
			deleteBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOnOffs.remove(b);
					mOnOffAdapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}
		
	}
	
	private class OnOffBean{
		int onoff = 0;//1开 0 关 2维持
		int seconds = 0;
		
		private String getOnOffStr(){
			switch (onoff) {
			case 0:
				return "关闭";
			case 1:
				return "打开";
			case 2:
				return "维持";

			default:
				break;
			}
			return "";
		}
	}
	
	
}