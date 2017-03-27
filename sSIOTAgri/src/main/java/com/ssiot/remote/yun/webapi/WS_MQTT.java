package com.ssiot.remote.yun.webapi;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;

public class WS_MQTT extends WebBaseMQTT{
	public static final int MSG_MQTT_GET = 6666;

	private static final String tag = "WS_MQTT";
	
	public String GetOneNodeData(String nodeUnique){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("uniqueid", "" + nodeUnique);
        String txt = exeRetString("GetNodeData.asmx", "GetOneNodeData", params);
        if ("anyType{}".equals(txt)){
    		return null;
    	}
        try {//类似{"uniqueid":"10000613","data":{"770_0":"0.000","769_0":"0.000","rtc":"0"},"time":"1476685641"}
			JSONObject jo = new JSONObject(txt);
//			return jo.getString("data");
			return txt;
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public void GetOneSensorData(String uniqueid, int sensorno, int channel){		
	}
	
	public String GetNodeOneDeviceState(String nodeUnique, int deviceno){
//		HashMap<String, String> params = new HashMap<String, String>();
//        params.put("uniqueid", "" + nodeUnique);
//        params.put("deviceno", "" + deviceno);
//        String txt = exeRetString("GetNodeState.asmx", "GetNodeOneDeviceState", params);
        return null;
	}
	
	public String GetOneNodeDevicesState(String nodeUnique){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("uniqueid", "" + nodeUnique);
        String txt = exeRetString("GetNodeState.asmx", "GetOneNodeDevicesState", params);
        try {//类似	{"uniqueid":"10000613","data":{"values":["0","0","0"]},"time":"1476684115"}
			JSONObject jo = new JSONObject(txt);
			return jo.getString("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	
	
	public void SetDevicesStateImmediately(String nodeUnique, String value){
		
	}
	
	public int SetOneDeviceStateImmediately(String nodeUnique, int deviceno, int value){//立即开关单个继电器
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("uniqueid", "" + nodeUnique);
        params.put("deviceno", "" + deviceno);
        params.put("value", "" + value);
        String txt = exeRetString("SetDeviceState.asmx", "SetOneDeviceStateImmediately", params);
        try {//类似	{"uniqueid":"10000613","data":{"status":"0"},"time":"1476686511"}
			JSONObject jo = new JSONObject(txt);
			JSONObject jData = jo.getJSONObject("data");
			return jData.getInt("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return -1;
	}
	
	public int SetNodeCalibration(String nodeUnique, int sensor, int channel, String jsonstr){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("uniqueid", "" + nodeUnique);
        params.put("sensor", "" + sensor);
        params.put("channel", "" + channel);
        params.put("jsonstr", "" + jsonstr);//参数类似[{"y":1,"x":2},{"y":4030,"x":400}]
        String txt = exeRetString("SetSensorCalibration.asmx", "SetNodeCalibration", params);
        try {//类似{"uniqueid":"10000613","data":{"status":"0"},"time":"1476689650"}	
			JSONObject jo = new JSONObject(txt);
			JSONObject jData = jo.getJSONObject("data");
			return jData.getInt("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return -1;
	}
	
	//溶解氧 ph 快捷标定
	public int SetNodeCalibration2(String nodeUnique, int sensor, int channel, String jsonstr){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("uniqueid", "" + nodeUnique);
        params.put("sensor", "" + sensor);
        params.put("channel", "" + channel);
        params.put("jsonstr", "" + jsonstr);//参数类似[{"y":1,"x":2},{"y":4030,"x":400}]
        String txt = exeRetString("SetSensorCalibration.asmx", "SetNodeCalibration2", params);
        try {//类似{"uniqueid":"10000613","data":{"status":"0"},"time":"1476689650"}	
        	if ("anyType{}".equals(txt)){
        		return -1;
        	}
			JSONObject jo = new JSONObject(txt);
			JSONObject jData = jo.getJSONObject("data");
			return jData.getInt("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return -1;
	}
	
	
	
	//---------------------------------以下是控制规则-----------------------
	public int CtrRuleNow(String nodeunique, String devicenos, String icmd, String ispn){
		
		return 0;
	}
	
	public int CtrRuleTiming(String nodeunique, String devicenos, String icmd, String ispn, int time){
		return 0;
	}
	
	//每天
	public int CtrRuleLoopDay(String nodeunique, String devicenos, String icmd, String ispn, String daytime){
		return 0;
	}
	
	//一周的几天
	public int CtrRuleLoopWeek(String nodeunique, String devicenos, String icmd, String ispn, String daytime, String idays){
		return 0;
	}
	
	//一月的几号
	public int CtrRuleLoopMonth(String nodeunique, String devicenos, String icmd, String ispn, String daytime, String idays){
		return 0;
	}
	
	//触发
	public int CtrRuleTrigger(String nodeunique, String devicenos, String icmd, String ispn,
			String sens,String lessmores ,String threshold,String relation){
		return 0;
	}
}