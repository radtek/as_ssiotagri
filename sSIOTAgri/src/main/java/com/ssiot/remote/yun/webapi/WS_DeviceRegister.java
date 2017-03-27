package com.ssiot.remote.yun.webapi;

import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;  
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ssiot.remote.data.model.S30HostModel;

public class WS_DeviceRegister extends WebBasedb2 {
    private static final String tag = "WS_DeviceRegister";
    private String MethodFile = "DeviceRegister.asmx";

    public S30_ModuleInventory GetDeviceInfo(String serialno){//TODO
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("serialno", "" + serialno);
        String txt = exeRetString(MethodFile, "GetDeviceInfo", params);
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				S30_ModuleInventory model = new S30_ModuleInventory();
				model._id = jo.getInt("ID");
				model._HID = jo.getString("HID");
				model._moduleName = jo.getString("ModuleName");
				model._isMain = "True".equalsIgnoreCase(jo.getString("IsMain"));
				return model;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
    }
    
    public int DeviceRegist(String serialno,String account,String parenthid){
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("serialno", "" + serialno);
        params.put("account", "" + account);
        params.put("parenthid", "" + parenthid);
        String txt = exeRetString(MethodFile, "DeviceRegist", params);
        try {
			return Integer.parseInt(txt);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return -10;
    }
    
    public List<S30HostModel> GetAllHost(String strAccount){
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("strAccount", "" + strAccount);
        String txt = exeRetString(MethodFile, "GetAllHost", params);
        try {
        	List<S30HostModel> list = new ArrayList<S30HostModel>();
			JSONArray jArray = new JSONArray(txt);
			for (int i = 0; i < jArray.length(); i ++){
				JSONObject jo = jArray.optJSONObject(i);
				S30HostModel m = new S30HostModel();
				m._hid = jo.getInt("HID");
				m._moduleid = jo.getInt("ModuleID");
				m._name = jo.getString("Name");
				list.add(m);
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public int RemoveRegisterModule(String macaddress){
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("macaddress", "" + macaddress);
        String txt = exeRetString(MethodFile, "RemoveRegisterModule", params);
    	return parseSave(txt);
    }
    
    public class S30_ModuleInventory{
    	public int _id;
    	int _moduleid;
    	String _MacAddr;
    	String _HID;//>0则是主机
    	int _States;
    	
    	String _moduleName;
    	boolean _isMain;
    	public boolean isMain(){
    		try {
    			int i = Integer.parseInt(_HID);
    			if (i > 0){
    				return true;
    			}
			} catch (NumberFormatException e) {
			}
    		return false;
    	}
    }
    
}
