package com.ssiot.remote.data.model;

import java.io.Serializable;

public class DiseaseModel implements Serializable{
	public int _id;
	public int _fishtypeid;
	public String _name;
	public String _causereason;
	public String _resulttext;
	public String _resolve;
	public String _medicineIds;
	
	public int _probability;//概率
	public String _fishname;
}