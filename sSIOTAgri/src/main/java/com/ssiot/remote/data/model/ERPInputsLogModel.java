package com.ssiot.remote.data.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.ssiot.remote.yun.manage.CustomModel.GetCustomShowInterface;

public class ERPInputsLogModel implements Serializable,GetCustomShowInterface{//投入品使用记录的物品都是从投入品入库的表里选择的。
	public int _id;//
	public String _name;
	public int _inputstypeid;//
	public int _batchid;
	public float _amount;//
	public String _unit;
	public Timestamp _time;//
	public String _brand;
	public String _usedate;//育苗期
	public int _warehouseid;//
	
	public String _batchname;
	
	@Override
	public String getTitle() {
		return _name;
	}
	@Override
	public String getContent() {
		return "批次:"+ _batchname + " 使用量:" + _amount;
	}
}