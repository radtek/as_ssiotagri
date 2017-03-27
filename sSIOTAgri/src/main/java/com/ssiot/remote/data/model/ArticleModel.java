package com.ssiot.remote.data.model;

import java.sql.Timestamp;

public class ArticleModel {
	public int _id;
	public String _type;
	public String _title;
	public String _description;
	public String _image;
	public Timestamp _createtime;
	public String _author;
	
	public String _contenttext;//为何在ArticleContent表里
}