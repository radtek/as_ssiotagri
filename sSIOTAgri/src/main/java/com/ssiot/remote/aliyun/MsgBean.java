package com.ssiot.remote.aliyun;

import java.sql.Timestamp;

public class MsgBean{
	int id;
	String title;
	String detail;
	String url;
	Timestamp mTimestamp;
	String timeStr;
	
	public MsgBean(){
		
	}
	private MsgBean (int i, String t, Timestamp time){
		id = i;
		detail = t;
		mTimestamp = time;
	}
	
	private MsgBean (int i, String t, String timeString){
		id = i;
		detail = t;
		timeStr = timeString;
	}
}