package com.ssiot.remote.yun.webapi;

import android.util.Log;

import com.ssiot.remote.data.model.UserModel;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;  
import org.w3c.dom.NodeList;  

public class WS_User extends WebBasedb2 {
    private static final String tag = "WS_User";
    private String MethodFile = "User.asmx";

    public void Delete() {

    }
    
    public UserModel GetUserByPsw(String account, String pwd){//TODO
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("strUserCode", "" + account);
        params.put("strPassword", "" + pwd);
        String txt = exeRetString(MethodFile, "GetUserByPsw", params);
        List<UserModel> list = parse(txt);
        if (null != list && list.size() > 0){
        	return list.get(0);
        }
        return null;
    }

    public UserModel GetUserByID(int userid) {//根据id获取一个
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", "" + userid);
        String txt = exeRetString(MethodFile, "GetUserByUserID", params);
        List<UserModel> list = parse(txt);
        if (null != list && list.size() > 0){
        	return list.get(0);
        }
        return null;
    }
    
    public int getMainUserId(int userid){
    	List<UserModel> list = GetAllGroupUsersByID(userid);
    	if (null != list && list.size() > 0){
    		for (int i = 0; i < list.size(); i ++){
    			if (list.get(i)._parentid == 12){//12是管理员
    				return list.get(i)._userid;
    			}
    		}
    	}
    	return userid;
    }
    
    public List<UserModel> GetReceiverUserNames(List<Integer> ids) {
        List<UserModel> userModels = new ArrayList<UserModel>();
        for (int i = 0; i < ids.size(); i ++){
            UserModel umodel = GetUserByID(ids.get(i));
            if (null != umodel){
                userModels.add(umodel);
            }
        }
        return userModels;
    }
    
    public List<UserModel> GetAllGroupUsersByID(int userid){//本组织下的所有人
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", "" + userid);
        String txt = exeRetString(MethodFile, "GetUsersByUserID", params);
        return parse(txt);
    }
    
    private List<UserModel> parse(String str) {
        List<UserModel> models = new ArrayList<UserModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                UserModel cType = new UserModel();
                Node item = items.item(i);
                NodeList properties = item.getChildNodes();
                for (int j = 0; j < properties.getLength(); j++) {
                    Node property = properties.item(j);
                    String nodeName = property.getNodeName();
                    if (null == property.getFirstChild()){
                        continue;
                    }
                    String valueStr = property.getFirstChild().getNodeValue();
                    if (nodeName.equals("UserID")) {
                        cType._userid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("Account")){
                        cType._account = valueStr;
                    } else if (nodeName.equals("UserName")){
                        cType._username = valueStr;
                    } else if (nodeName.equals("UniqueID")){
                    	cType._uniqueid = valueStr;
                    } else if (nodeName.equals("AreaID")){
                        cType._areaid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("UserType")){
                        cType._UserType = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("UserDeviceType")){
                        cType._devicetype = Integer.parseInt(valueStr);
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (UserModel m : models){
            Log.v(tag, "------id:" +m._userid + " account:" + m._account);
        }
        return models;
    }
    
    public int UserBindNode(String account, String qrcode){
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("account", "" + account);
        params.put("qrcode", "" + qrcode);
        String txt = exeRetString(MethodFile, "UserBindNode", params);
        try {
			JSONObject jObject = new JSONObject(txt);
			return parseSave(jObject.getString("result"));
		} catch (Exception e) {
			e.printStackTrace();
		}
        return -1;
    }
    
    
    public int CheckAccountExist(String nodeUnique){//1存在，0 不存在，其他就是有问题
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("Account", "" + nodeUnique);
        String txt = exeRetString(MethodFile, "CheckAccountExist", params);
        if ("anyType{}".equals(txt)){
    		return -1;
    	}
        try {
//			JSONObject jo = new JSONObject(txt);
//			return jo.getInt("result");
        	return Integer.parseInt(txt);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return -1;
	}
	
	public int SendSms(String phone,String ssiotsecret){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", "" + phone);
        params.put("ssiotsecret", "" + ssiotsecret);
        String txt = exeRetString(MethodFile, "SendSms", params);
        if ("anyType{}".equals(txt)){
    		return -1;
    	}
        try {
//			JSONObject jo = new JSONObject(txt);
//			return jo.getInt("result");
        	if (null != txt && txt.length() > 10){
        		return 1;
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return -1;
	}
	
	//1 成功 -1失败
	public int StartRegister(String phone, String verifyCode, String pwd, String inviteuserid){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", "" + phone);
        params.put("verifyCode", "" + verifyCode);
        params.put("pwd", "" + pwd);
        params.put("inviteuserid", "" + inviteuserid);//TODO 算法
        String txt = exeRetString(MethodFile, "StartRegister", params);
        if ("anyType{}".equals(txt)){
    		return -4;
    	}
        try {
//			JSONObject jo = new JSONObject(txt);
//			return jo.getInt("result");
			return Integer.parseInt(txt);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return -5;
	}

}
