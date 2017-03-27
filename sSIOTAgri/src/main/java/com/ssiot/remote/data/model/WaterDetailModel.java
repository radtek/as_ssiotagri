package com.ssiot.remote.data.model;

import java.io.Serializable;

import com.ssiot.remote.data.model.view.SensorThresholdModel;

public class WaterDetailModel implements Serializable{
	public int _id;
	public int _waterstandardid;
	public int _sentypeid;
	public boolean _upperorlower;
	public float _warnvalue;
	public float _alertvalue;
	public String _causereason;
	public String _resulttext;
	public String _resolve;
	public String _medicineIds;
	
	public SensorThresholdModel thresholdModel;//TODO 是否需要这个
}