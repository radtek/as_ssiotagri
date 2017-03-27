package com.ssiot.remote.data.model;

import java.io.Serializable;

public class VLCVideoInfoModel implements Serializable{
    public int _vlcvideoinfoid;
    public int _areaid;
    public String _areaname;
    public String _username;
    public String _password;
    public String _url;
    public String _ip;
    public String _port;
    public String _address;
//    public int? _channel;
    public String _subtype;
    public String _type;
//    public DateTime? _createtime;
    public String _remark;
    public String _longitude;
    public String _latitude;
    public int _tcpport;
    public int _facilitiesid = 0;
    public int _devicetype = 0;
    public String _serialno;
    public String _verifycode = "";
    public boolean _ssiotezviz = false;
    
    //非数据库内的值，表示连接状态 0=未知，1= online；2=fail
    public int status = 0;//
}