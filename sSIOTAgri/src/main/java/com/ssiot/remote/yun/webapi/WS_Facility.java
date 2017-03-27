package com.ssiot.remote.yun.webapi;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.ssiot.remote.data.model.FacilityModel;

public class WS_Facility extends WebBasedb2{
	private static final String tag = "WS_Facility";
	private String MethodFile = "Facility.asmx";
	
	public ArrayList<FacilityModel> GetAgricultureFacilities(String where){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "" + where);
        String txt = exeRetString(MethodFile, "GetAgricultureFacilities", params);
        if ("anyType{}".equals(txt)){
    		return null;
    	}
        ArrayList<FacilityModel> list = new ArrayList<FacilityModel>();
        try {
        	JSONArray ja = new JSONArray(txt);
        	for (int i = 0; i < ja.length(); i ++){
        		JSONObject jo = ja.optJSONObject(i);
        		FacilityModel lm = new FacilityModel();
        		lm._facilitiesid = jo.getInt("FacilitiesID");
        		lm._facilitiesname = jo.getString("FacilitiesName");
        		lm._facilitiestypeid = jo.getInt("FacilitiesTypeID");
        		lm._landid = jo.getInt("LandID");
        		String areacoveredStr = jo.getString("AreaCovered");
        		lm._areacoveredunit = jo.getString("AreaCoveredUnit"); 
        		String lngStr = jo.getString("Longitude");
        		String latStr = jo.getString("Latitude");
        		try {
        			lm._longitude = Float.parseFloat(lngStr);
        			lm._latitude = Float.parseFloat(latStr);
				} catch (Exception e) {
					Log.w(tag, "---Warning--longitudelatitude");
				}
        		try {
					lm._areacovered = Float.parseFloat(areacoveredStr);
				} catch (Exception e) {
					Log.w(tag, "---Warning--_areacovered");
				}
        		lm._areaid = jo.getInt("AreaID");
//        		lm._typename = jo.getString("TypeName");//玻璃温室 or
//        		lm._landname = jo.getString("LandName");
        		lm._parentid = jo.getInt("ParentID");
        		list.add(lm);
        	}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public int SaveAgricultureFacility(FacilityModel m){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("FacilitiesID", "" + m._facilitiesid);
        params.put("FacilitiesName", "" + m._facilitiesname);
        params.put("FacilitiesTypeID", "" + m._facilitiestypeid);
        params.put("LandID", "" + m._landid);
        params.put("Longitude", "" + m._longitude);
        params.put("Latitude", "" + m._latitude);
        params.put("AreaID", "" + m._areaid);
//        params.put("TypeName", "" + m._typename);
//        params.put("LandName", "" + m._landname);
        params.put("ParentID", "" + m._parentid);
        String txt = exeRetString(MethodFile, "SaveAgricultureFacility", params);
        if ("anyType{}".equals(txt)){
    		return 0;
    	}
        return parseSave(txt);
	}
	
	public ArrayList<FacilityModel> GetFisheriesFacilities(String where){//TODO
		return null;
	}
	
	public ArrayList<FacilityModel> SaveFisheriesFacility(FacilityModel m){
		return null;
	}
	
	public int DeleteAgricultureFacilities(int id){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + id);
        String txt = exeRetString(MethodFile, "DeleteAgricultureFacilities", params);
        if ("anyType{}".equals(txt)){
    		return 0;
    	}
        return parseSave(txt);
	}
	
}