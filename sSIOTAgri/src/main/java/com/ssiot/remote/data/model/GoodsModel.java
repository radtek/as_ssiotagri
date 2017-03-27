package com.ssiot.remote.data.model;

import java.io.Serializable;

public class GoodsModel implements Serializable{//就是相当于biz_Product表，原来的电商商品
	public int _id;
	public String _code;
	public String _name;
	public float _price;
	public String _detail;
	public String _img;
}