package com.ssiot.remote.data.model;

import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.manage.CustomModel.GetCustomShowInterface;

import java.io.Serializable;
import java.sql.Timestamp;

//投入品 出库
public class ERPInputsOutModel implements Serializable,GetCustomShowInterface{
    public int _id;
    public int _userid;
    public int _inputstypeid;
    public String _namedetail;
    public String _inputsunit;
    public float _amount;
    public String _takingperson;
    public Timestamp _takingtime;
    
//    public String _InputsTypeName;
//    public String _TakingPersonName;
    
    @Override
    public String getTitle() {
        return "名称:"+ _namedetail;
    }
    
    @Override
    public String getContent() {
        return "时间:"+Utils.formatTime(_takingtime) + " 数量:" + _amount + " 人员:" + _takingperson;
    }
}