package com.ssiot.remote.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class LandModel implements Serializable{
	public int _landid;
	public int _areaid;
	public String _name;
	public String _points = "";
	
	public ArrayList<FacilityModel> facilities = new ArrayList<FacilityModel>();
	
	public FacilityModel getFacility(int position){
		if (facilities.size() > position){
			return facilities.get(position);
		}
		return null;
	}
}