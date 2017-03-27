package com.ssiot.remote.data.model;

import com.ssiot.remote.yun.manage.CustomModel.GetCustomShowInterface;

import java.io.Serializable;

public class ERPTaskModel implements Serializable, GetCustomShowInterface{
    public int _id;
    public int _ownerid;
    public int _croptypeid;
    public int _stagetype;
    public int _tasktype;
    public String _taskdetail;
    public boolean _requirepic;
    public boolean _requirelocaion;
    public String _requirefilltables;
    public float _workload;
    
    public String _OwnerName;
    public String _StageName;
    public String _TypeName;
    
    public boolean enabled = false;//界面是否显示激活按钮所用

    @Override
    public String getTitle() {
        return _taskdetail;
    }

    @Override
    public String getContent() {
        return "种类id:" + _croptypeid +" 阶段:" + _StageName;
    }
}