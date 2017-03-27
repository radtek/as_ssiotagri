package com.ssiot.remote.data.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TaskCenterModel implements Serializable {
    public int _id;
    public int _userid;
    public String _tousers;
    public String _contenttext;
    public Timestamp _createtime;
    public Timestamp _starttime;
    public Timestamp _endtime;
    public String _img;
    public boolean _needimg;
    public boolean _needlocation;
    public int _state;//新建1 进行中2 完成3
    public Timestamp _receivedtime;
    public String _username;//从user表中查询得到，只在TaskCenter中查询时为空
    
    
    public List<Integer> getToUsersList(){
        List<Integer> userList = new ArrayList<Integer>();
        try {
            JSONArray jaArray = new JSONArray(_tousers);
            for (int i = 0; i < jaArray.length(); i ++){
                JSONObject jo = jaArray.optJSONObject(i);
                int userid = jo.getInt("to");
                userList.add(userid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userList;
    }
    
    public String _parsedReceiverNames = "";
    public String getToUsersStr(){
        List<Integer> userids = getToUsersList();
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