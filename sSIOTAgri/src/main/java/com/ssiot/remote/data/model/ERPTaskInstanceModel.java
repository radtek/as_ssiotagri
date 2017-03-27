package com.ssiot.remote.data.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ERPTaskInstanceModel extends ERPTaskModel implements Serializable{

	public int _taskmouldid;//任务模板复制过来的id，如果小于1 表示不是复制过来的任务
	public int _batchid;
    public String _workerids;
    public int _userid;//发起人
    public String _img;
    public int _state;
    public Timestamp _createtime;
    
    
    public List<Integer> getReceiverUserIDsList(){
        List<Integer> userList = new ArrayList<Integer>();
        try {
        	String[] str = _workerids.split(",");
        	for (int i = 0; i < str.length; i ++){
        		try {
        			int num = Integer.parseInt(str[i]);
            		userList.add(num);
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
//            JSONArray jaArray = new JSONArray(_workerids);
//            for (int i = 0; i < jaArray.length(); i ++){
//                JSONObject jo = jaArray.optJSONObject(i);
//                int userid = jo.getInt("to");
//                userList.add(userid);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }   
    
    public String _parsedReceiverNames = ""; 
    public String getReceiverUsersStr(){
        List<Integer> userids = getReceiverUserIDsList();
        String str = ""; 
        if (null != userids){
            for (int i = 0; i < userids.size(); i ++){
                str = str + userids.get(i) + ",";
            }
        }
        if (str.endsWith(",")){
            str = str.substring(0,str.length()-1);
        }
        return str;
    }
}