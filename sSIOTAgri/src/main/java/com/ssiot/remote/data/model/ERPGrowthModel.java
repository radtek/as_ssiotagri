package com.ssiot.remote.data.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.manage.CustomModel.GetCustomShowInterface;

public class ERPGrowthModel implements Serializable, GetCustomShowInterface{
	public int _id;
	public int _batchid;
	public float _productlength;
	public int _sex = 1;//雌雄分类
	public float _productweight;
	public Timestamp _createtime;
	public String _image;
	public String _batchName;
	
	@Override
	public String getTitle() {
		return "长度(cm):" + _productlength;
	}
	@Override
	public String getContent() {
		return "批次:" + _batchName + " 时间:" + Utils.formatTime(_createtime);
	}
}