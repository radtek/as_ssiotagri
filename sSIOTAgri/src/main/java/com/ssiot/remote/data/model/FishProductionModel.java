package com.ssiot.remote.data.model;

import java.sql.Timestamp;

public class FishProductionModel{
    public int _id;
    public int _userid;
    public int _productiontype;
    public String _name;
    public String _amount;
    public float _totalprice;
    public String _detail;
    public String _worker;
    public boolean _isProductIn;
    public Timestamp _actiontime;
}