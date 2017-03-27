package com.ssiot.remote.data.model;

import com.ssiot.remote.yun.manage.CustomModel.GetCustomShowInterface;

import java.io.Serializable;

public class ERPProductPlanModel implements Serializable,GetCustomShowInterface{
    public int _id;
    public String _name;
    public int _owenerid;
    public int _croptype;
    public String _taskids;
    
    public String _UserName;
    
    @Override
    public String getTitle() {
        return _name;
    }
    @Override
    public String getContent() {
        return "方案归属:"+ (_owenerid == 0 ? "平台" : "我");
    }
}