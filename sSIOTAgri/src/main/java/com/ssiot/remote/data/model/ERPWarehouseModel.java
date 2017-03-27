package com.ssiot.remote.data.model;

import java.io.Serializable;

public class ERPWarehouseModel implements Serializable{
	public int _id;
	public int _userinputstypeid;
	public String _name;
	public float _total;
	public String _amountUnit;//袋子，盒子,箱
	public float _peramount;
	public String _peramountunit;
	public int _userid;
}