package com.ssiot.remote.data.model.view;

import java.io.Serializable;

public class SensorThresholdModel implements Serializable{
	public int mDeviceTypeNo = 0;
	
	public final float min;
	public final float max;
	
	public int thresholdType = 1;//单向向上1，单向向下2，双向限制3。
	
	public float upperwarnvalue;
	public float upperalertvalue;
	public float lowerwarnvalue;
	public float loweralertvalue;
	
	public SensorThresholdModel(int devicetype){
		mDeviceTypeNo = devicetype;
		float [] minmax = getMinMax(devicetype);
		min = minmax[0];
		max = minmax[1];
	}
	
	public static float[] getMinMax(int sen){
		float min = 0;
		float max = 0;//差值最好是3的倍数，刻度就不会有小数
		switch (sen) {
		case 770://温度
			min = 0;
			max = 100;
			break;
		case 771://大气压
			min = 900;
			max = 1100;
			break;
		case 1001://ph
			min = 4;
			max = 10;
			break;
		case 1002://溶解氧？
			min = 0;
			max = 9;
			break;
		case 1003://氨离子
			min = 0;
			max = 100;
			break;
		case 1006://溶解氧饱和度？
			min = 0;
			max = 100;
			break;
		case 1007://溶解氧相位
			min = 0;
			max = 100;
			break;
		case 1008://氨氮
			min = 0;
			max = 3;
			break;
		case 8008://水氨氮传感器
			min = 0;
			max = 6;
			break;
			
		default:
			break;
		}
		float[] ret = new float[2];
		ret[0] = min;
		ret[1] = max;
		return ret;
	}
	
	@Override
	public String toString() {
		return "mDeviceTypeNo" + mDeviceTypeNo + " min:" + min + " max:" + max + " upperwarnvalue" + upperwarnvalue + " upperalertvalue" + upperalertvalue
				+ " lowerwarnvalue" + lowerwarnvalue + " loweralertvalue" + loweralertvalue;
	}
	
}