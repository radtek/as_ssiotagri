package com.ssiot.remote.data.model;

import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.manage.CustomModel.GetCustomShowInterface;

import java.io.Serializable;
import java.sql.Timestamp;

//包装  从采收入库来的
public class ERPProductsPackModel implements Serializable,GetCustomShowInterface{
    public int _id;
    public String _name;//例如 海安黄金米
    public int _productsinid;
    public String _packtypeid;
    public String _packunit;//包装规格
    public Timestamp _createtime;
    public String _traceprofilecode;
    
    public String _proimg;
    
    public String _prodesc;
    public String _comdesc;
    public String _qrcode;

    public String _qrimgurl;
    public String _probatchno;
    
    @Override
    public String getTitle() {
        return "包装好的产品名:"+_name;
    }
    
    @Override
    public String getContent() {
        return "包装时间:"+Utils.formatTime(_createtime) + " 包装规格:" + _packunit;
    }
}