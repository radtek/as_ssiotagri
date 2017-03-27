package com.ssiot.remote.data.model.view;

import java.util.List;

public class S30HostViewModel{
    public int _hostId;
    public String _hostName;
    public int _facilitesId;
    public String _facilitiesName;
    public int _landId;
    public String _landName;
    
    public int _parentHostId = 0;
    
    public int _productId;
    //TODO 查询ControlDevice表的控制设备
    public List<Integer> _controlDevices;
    
}