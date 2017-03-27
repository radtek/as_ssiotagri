package com.ssiot.remote.yun.admin;

import java.util.List;

import android.R.integer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssiot.agri.R;
import com.ssiot.fish.HeadActivity;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.S30HostModel;
import com.ssiot.remote.yun.webapi.WS_DeviceRegister;
import com.ssiot.remote.yun.webapi.WS_DeviceRegister.S30_ModuleInventory;
import com.ssiot.remote.yun.webapi.WS_MQTT;
import com.ssiot.remote.yun.webapi.WS_MQTT_Register;
import com.ssiot.remote.yun.widget.PieProgressBar;

public class DeviceRegistAct extends HeadActivity{
	private static final String tag = "DeviceRegistAct";
	String serialNo = "";
	PieProgressBar pieProgressBar;
	TextView percenTextView;
	TextView infoTextView;
	TextView titleTextView;
	TextView subTitleView;
	Button nextButton;
	private int status = -1;//0已查到Host信息，1 子信息，2已第一步,3已第二部
	List<S30HostModel> hostModels;//只有需要注册子模块时用到
	S30HostModel selectedHost;
	TextView selectedHostText;
	S30_ModuleInventory currentModel;
	
	private static final int MSG_INFO_GET = 3;
	private static final int MSG_STATUS_CHANGED = 1;
	private static final int MSG_FAIL = 2;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_INFO_GET:
				if (currentModel.isMain()){
					status = 0;
					setProgressSecondTick(0,99);
					titleTextView.setText("设备连接中,请稍候");
					new RegisterDeviceThread("").start();
				} else {
					getAllHost();
					status = 1;
					findViewById(R.id.row_host).setVisibility(View.VISIBLE);
					nextButton.setEnabled(true);
				}
				break;
			case MSG_STATUS_CHANGED:
				int start = 0;
				int end = 100;
				if (status == 2){
					start = 50;
					end = 99;
				} else if (status == 3){
					start = 100;
					end = 100;
					nextButton.setEnabled(true);
					titleTextView.setText("成功");
					subTitleView.setText("成功");
				}
				setProgressSecondTick(start, end);
				break;
			case MSG_FAIL:
				String msgStr = (String) msg.obj;
				pieProgressBar.cancelAnim();
				findViewById(R.id.fail_icon).setVisibility(View.VISIBLE);
				pieProgressBar.setVisibility(View.GONE);
				percenTextView.setVisibility(View.INVISIBLE);
				titleTextView.setText("注册设备失败，确保网络畅通后重试");
				if (!TextUtils.isEmpty(msgStr)){
					titleTextView.setText(msgStr);
				}
				//TODO 重试
			break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		serialNo = getIntent().getStringExtra("serialno");
		setContentView(R.layout.smart_config_start_config_ui);//act_device_regist
		if (TextUtils.isEmpty(serialNo)){
			showToast("出现问题");
		}
		initViews();
		getDeviceInfoThread();
	}
	
	private void initViews(){
		selectedHostText = (TextView) findViewById(R.id.txt_host);
		pieProgressBar = (PieProgressBar) findViewById(R.id.base_ui_progress_bar);
		percenTextView = (TextView) findViewById(R.id.base_ui_progress_bar_text);
		infoTextView = (TextView) findViewById(R.id.device_info);
		infoTextView.setText("设备唯一编号:"+serialNo);
		titleTextView = (TextView) findViewById(R.id.base_ui_progress_bar_title);
		subTitleView = (TextView) findViewById(R.id.smart_config_common_main_sub_title);
		nextButton = (Button) findViewById(R.id.next_btn);
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (status < 0){
					showToast("未查询到设备信息");
				} else if (status == 1){//子设备
					if (null != selectedHost){
						setProgressSecondTick(0,99);
						titleTextView.setText("设备连接中,请稍候");
						new RegisterDeviceThread(""+selectedHost._hid).start();
						v.setEnabled(false);
					}
					
					
				} else {
					finish();
				}
			}
		});
		
		pieProgressBar.setPercentView(percenTextView);
//		setProgressSecondTick(0,99);
	}
	
	public void ClickHost(View v){
		AlertDialog.Builder bui = new AlertDialog.Builder(this);
		if (null == hostModels || hostModels.size() == 0){
			return;
		}
		String[] itms =  new String[hostModels.size()];
		for (int i = 0; i < hostModels.size(); i ++){
			itms[i] = hostModels.get(i)._hid + " "+hostModels.get(i)._name;
		}
		
        bui.setSingleChoiceItems(itms, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	selectedHost = hostModels.get(which);
            }
        });
        bui.setTitle("请选择").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	selectedHostText.setText(selectedHost._name);
//            	txt_faType.setText(mFacilityTypeStr);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
	}
	
	private void setProgressSecondTick(int start, int end){
//		pieProgressBar.cancelAnim();
		pieProgressBar.setFromPercent(start);
		pieProgressBar.setToPercent(end);
		pieProgressBar.setDuration((end -start) * 1000);
		pieProgressBar.startAnim();
	}
	
	private void getDeviceInfoThread(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				currentModel = new WS_DeviceRegister().GetDeviceInfo(serialNo);
				if (null != currentModel) {
					mHandler.sendEmptyMessage(MSG_INFO_GET);
				} else {
					sendToast("未获取到设备相关信息");
				}
			}
		}).start();
	}
	
	private void getAllHost(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				String account = Utils.getStrPref(Utils.PREF_USERNAME, DeviceRegistAct.this);
				hostModels = new WS_DeviceRegister().GetAllHost(account);
			}
		}).start();
	}
	
	private class RegisterDeviceThread extends Thread{
		String parentHid = "";
		public RegisterDeviceThread(String parenthid){
			parentHid = parenthid;
		}
		@Override
		public void run() {
			String account = Utils.getStrPref(Utils.PREF_USERNAME, DeviceRegistAct.this);
			int ret1 = new WS_DeviceRegister().DeviceRegist(serialNo, account, parentHid);
			if (ret1 > 0){
				status = 2;
				mHandler.sendEmptyMessage(MSG_STATUS_CHANGED);
				int ret2 = new WS_MQTT_Register().SendSensorConfiguration(serialNo);
				if (ret2 >= 0){
					status = 3;
					mHandler.sendEmptyMessage(MSG_STATUS_CHANGED);
				} else {
					new WS_DeviceRegister().RemoveRegisterModule(serialNo);
					mHandler.sendEmptyMessage(MSG_FAIL);
					sendToast("第二步失败,请确保设备网络正常");
				}
			} else {
				Message failmsg = mHandler.obtainMessage(MSG_FAIL);
				if (ret1 == -1){
					failmsg.obj = "区域ID不存在";
					mHandler.sendMessage(failmsg);
				} else if (ret1 == -2){
					failmsg.obj = "编号不存在";
					mHandler.sendMessage(failmsg);
				} else if (ret1 == -3){
					failmsg.obj = "设备已注册";
					mHandler.sendMessage(failmsg);
				} else if (ret1 == -4){
					failmsg.obj = "主机不存在";
					mHandler.sendMessage(failmsg);
				} else if (ret1 == -5){
					failmsg.obj = "主机未注册，请先注册主机";
					mHandler.sendMessage(failmsg);
				} else {
					failmsg.obj = "第一步失败,请确保手机网络正常";
					mHandler.sendMessage(failmsg);
				}
				
			}
		}
	}

	private class GetDeviceInfoThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
		}
	}
}