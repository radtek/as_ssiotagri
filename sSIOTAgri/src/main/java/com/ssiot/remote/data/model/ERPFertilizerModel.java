package com.ssiot.remote.data.model;

import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.manage.CustomModel.GetCustomShowInterface;

import java.io.Serializable;
import java.sql.Timestamp;

//肥料
public class ERPFertilizerModel implements Serializable,GetCustomShowInterface{
    public int _id;
    public int _productbatchid;
    public String _usedate;//幼苗期
    public String _name;
    public float _dosage;
    public String _brand;
    public String _suppliername;
    public String _unit;
    public Timestamp _usedatetime;
    public String _type;
    
    @Override
    public String getTitle() {
        return "肥料:"+_name;
    }
    
    @Override
    public String getContent() {
        return "使用时间:"+Utils.formatTime(_usedatetime) + " 量:" + _dosage + _unit;
    }
}