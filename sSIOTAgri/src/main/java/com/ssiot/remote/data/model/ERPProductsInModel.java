package com.ssiot.remote.data.model;

import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.manage.CustomModel.GetCustomShowInterface;

import java.io.Serializable;
import java.sql.Timestamp;

//采收
public class ERPProductsInModel implements Serializable,GetCustomShowInterface{
    public int _id;
    public int _productbatchid;
    public String _facilityids;
    public String _nodeids;
    public Timestamp _createtime;
    public float _amount;
    
    public String _ProductBatchName;
    
    @Override
    public String getTitle() {
        return "批次名:"+_ProductBatchName;
    }
    
    @Override
    public String getContent() {
        return "采收时间:"+Utils.formatTime(_createtime) + " 数量:" + _amount;
    }
}