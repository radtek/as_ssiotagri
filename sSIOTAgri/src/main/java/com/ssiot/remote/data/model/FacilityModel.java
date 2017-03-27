package com.ssiot.remote.data.model;

import java.io.Serializable;

public class FacilityModel implements Serializable{
	public int _facilitiesid;
	public String _facilitiesname;
	public int _facilitiestypeid;
	public int _landid;
	public float _length;
	public float _width;
	public float _height;
	public float _areacovered;
	public String _areacoveredunit;
	public float _longitude;
	public float _latitude;
	public float _altitude;
	public String _points;
	public String _responsibleperson;
	public String _maintenancestaff;
	public int _areaid;
	public String _backgroudimage;//图
//	public String _facilitiestypename;//农业 or 渔业
//	public String _typename;//玻璃温室 or 大田 简易薄膜大棚...
//	public String _landname;
	public int _parentid;
}