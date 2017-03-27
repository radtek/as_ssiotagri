package com.ssiot.remote.data.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ERPTaskReportModel implements Serializable{
    public int _id;
    public int _taskinstanceid;
    public int _userid;
    public Timestamp _createtime;
    public String _reportdetail;
    public String _imgs;
    public String _location;
    public String _filltable;
    public boolean _finish;
    
    public String _UserName;
}