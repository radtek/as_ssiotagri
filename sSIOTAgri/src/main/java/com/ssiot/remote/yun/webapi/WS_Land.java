package com.ssiot.remote.yun.webapi;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.ssiot.remote.data.model.LandModel;

public class WS_Land extends WebBasedb2{
	private static final String tag = "WS_Land";
	private String MethodFile = "Land.asmx";
	
	public ArrayList<LandModel> GetLands(String where){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "" + where);
        String txt = exeRetString(MethodFile, "GetLands", params);
        if ("anyType{}".equals(txt)){
    		return null;
    	}
        ArrayList<LandModel> list = new ArrayList<LandModel>();
        try {
        	JSONArray ja = new JSONArray(txt);
        	for (int i = 0; i < ja.length(); i ++){
        		JSONObject jo = ja.optJSONObject(i);
        		LandModel lm = new LandModel();
        		lm._landid = jo.getInt("LandID");
        		lm._areaid = jo.getInt("AreaID");
        		lm._name = jo.getString("Name");
        		lm._points = jo.getString("Points");
        		list.add(lm);
        	}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public ArrayList<LandModel> GetLandsByAccount(String account){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("account", "" + account);
        String txt = exeRetString(MethodFile, "GetLandsByAccount", params);
        if ("anyType{}".equals(txt)){
    		return null;
    	}
        ArrayList<LandModel> list = new ArrayList<LandModel>();
        try {
        	JSONArray ja = new JSONArray(txt);
        	for (int i = 0; i < ja.length(); i ++){
        		JSONObject jo = ja.optJSONObject(i);
        		LandModel lm = new LandModel();
        		lm._landid = jo.getInt("LandID");
        		lm._areaid = jo.getInt("AreaID");
        		lm._name = jo.getString("Name");
        		lm._points = jo.getString("Points");
        		list.add(lm);
        	}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public int Save(int id, int areaid, String name, String points){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("LandID", "" + id);
        params.put("AreaID", "" + areaid);
        params.put("Name", "" + name);
        params.put("Points", "" + points);
        String txt = exeRetString(MethodFile, "Save", params);
        if ("anyType{}".equals(txt)){
    		return 0;
    	}
        return parseSave(txt);
	}
	
	public int Delete(int id){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + id);
        String txt = exeRetString(MethodFile, "Delete", params);
        if ("anyType{}".equals(txt)){
    		return 0;
    	}
        return parseSave(txt);
	}
	
}