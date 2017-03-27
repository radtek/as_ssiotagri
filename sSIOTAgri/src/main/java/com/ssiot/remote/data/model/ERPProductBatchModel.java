package com.ssiot.remote.data.model;

import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.manage.CustomModel.GetCustomShowInterface;

import java.io.Serializable;
import java.sql.Timestamp;

public class ERPProductBatchModel implements Serializable,GetCustomShowInterface{
    public int _id;
    public String _name = "";
    public int _inputsinid;
    public int _croptype;
    public String _facilityids;
    public int _planid;
    public Timestamp _expectstart;
    public Timestamp _expectend;
    public int _userid;
    public boolean _isfinish;
    
    public String _CropName;
    public String _FacilityName;
    public String _PlanName;
    public String _UserName;
    
    @Override
    public String getTitle() {
        return _name;
    }
    @Override
    public String getContent() {
        return "产品:" + _CropName + " 开始时间:" + Utils.formatTime(_expectstart);
    }
}