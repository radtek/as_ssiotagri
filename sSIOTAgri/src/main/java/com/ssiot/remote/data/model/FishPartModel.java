package com.ssiot.remote.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FishPartModel implements Serializable{
	public int _id;
	public String _name;
	
	public List<SymptomModel> _symptoms = new ArrayList<SymptomModel>();
}