package com.ssiot.remote.data.model;

import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.manage.CustomModel.GetCustomShowInterface;

import java.io.Serializable;
import java.sql.Timestamp;

//投入品 入库
public class ERPInputsInModel implements Serializable,GetCustomShowInterface{
    public int _id;
    public int _userid;
    public int _inputstypeid;
    public String _namedetail;
//    public String _inputsunit;
    public float _amount;
    public Timestamp _duetime;
    public String _suppliername;
    public String _relatedpeopleinfo;
    
//    public String _UserName;
//    public String _InputsTypeName;
    
    public Timestamp _productdate;//生产日期
    public float _warranty;//保质期
    public String _warrantyunit;//天
//    public String _amountunit;
//    public float _peramount;
//    public String _peramountunit;
    public int _warehouseid;
    
    @Override
    public String getTitle() {
        return "名称:"+ _namedetail;
    }
    
    @Override
    public String getContent() {
        return "到期日:"+Utils.formatTime(_duetime) + " 数量:" + _amount + " 人员:" + _relatedpeopleinfo;
    }
}