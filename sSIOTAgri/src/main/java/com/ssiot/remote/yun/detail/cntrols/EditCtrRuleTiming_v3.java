package com.ssiot.remote.yun.detail.cntrols;

import android.os.Bundle;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.yun.webapi.WS_MQTT;

//立即连续开关  &  定时连续开关   只是一次性的
public class EditCtrRuleTiming_v3 extends EditCtrRuleBase_v3{
	private static final String tag = "EditCtrRuleTiming_v3";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rule_edit_devicepick_layout);//TODO
//		new WS_MQTT().CtrRuleNow(nodeunique, devicenos, icmd, ispn);
		initViews();
	}
	
	private void initViews(){
		
	}
	
	
	
}