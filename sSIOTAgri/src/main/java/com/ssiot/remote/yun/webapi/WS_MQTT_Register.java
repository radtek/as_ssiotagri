package com.ssiot.remote.yun.webapi;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class WS_MQTT_Register extends WebBaseMQTT{
	public static final int MSG_MQTT_GET = 6666;

	private static final String tag = "WS_MQTT";
	private static final String METHOD_FILE = "DeviceRegisterAll.asmx";
	
	public int SendSensorConfiguration(String macaddress){
		MQTT_TIME_OUT = 30 * 1000;//这个函数超时时间长
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("macaddress", "" + macaddress);
        String txt = exeRetString(METHOD_FILE, "SendSensorConfiguration", params);
        try {
			JSONObject jo = new JSONObject(txt);
			return jo.getInt("status");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return -1;
	}
}