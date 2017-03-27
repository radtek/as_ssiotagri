package com.ssiot.remote.data.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class IOTCompanyModel implements Serializable{
    public int _companyid;
    public String _name;
    public String _url;
    public int _parentid;
    public Timestamp _createTime;
    public Timestamp _modifyTime;
    public boolean _isdelete;
    public String _companyContent;
    public String _remark;
}