package com.ssiot.remote.aliyun;

public class StringUtils {
	public static boolean isBlank(String xx){
		if (null == xx || xx.length() == 0){
			return true;
		}
		return false;
	}
	
	public static boolean isNotBlank(String xx){
		return !isBlank(xx);
	}
}