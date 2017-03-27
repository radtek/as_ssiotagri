package com.ssiot.remote.data.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.manage.CustomModel.GetCustomShowInterface;

public class ERPDiseaseLogModel implements Serializable,GetCustomShowInterface{
	public int _id;
	public int _batchid;
	public Timestamp _time;
	public String _symptom;
	public String _disease;
	public String _resolve;
	
	public String _batchname;
	
	@Override
	public String getTitle() {
		return _disease;
	}
	@Override
	public String getContent() {
		return "批次:" + _batchname + " 时间:" + Utils.formatTime(_time);
	}
}