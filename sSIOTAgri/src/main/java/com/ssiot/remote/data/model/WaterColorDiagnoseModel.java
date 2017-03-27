package com.ssiot.remote.data.model;

import java.io.Serializable;

public class WaterColorDiagnoseModel implements Serializable{
	public int _id;
	public String _name;
	public String _img;
	
	public String _causereason;
	public String _resulttext;
	public String _resolve;
	public String _medicineIds;
}