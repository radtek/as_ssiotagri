package com.ssiot.remote.yun.unit;

import android.text.TextUtils;
import android.util.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class XYStringHolder{
    private static final String tag = "XYStringHolder";
    public int timeInt;
    public String xString;//2016-04-08 14:00:00.0
    String yString;//376ppm
    public float yData;
    public String unit;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public XYStringHolder(int timeInt,String y){
        this.timeInt = timeInt;
        Timestamp ts = new Timestamp((long) timeInt * 1000);
        xString = formatter.format(ts);
        yString = y;
        if (!TextUtils.isEmpty(yString)){
            try {
                String yDStr = getNumberPartFromStr(yString);
//                yData = new BigDecimal(yDStr);
                yData = Float.parseFloat(yDStr);
                unit = yString.substring(yDStr.length(), yString.length());
//                Log.v(tag, "------x:" + xString + " yDStr:" + yDStr  + " data:" + yData + "  logtime:" + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String getNumberPartFromStr(String value_str){
        String dataStr = ""; 
        for (int j = 0; j < value_str.length(); j ++){
            if ((value_str.charAt(j) >= '0' && value_str.charAt(j) <= '9')
                    || value_str.charAt(j) == '.' || value_str.charAt(j) == '-'){
                dataStr += value_str.charAt(j);
            } else {
                break;
            }
        }
        if (TextUtils.isEmpty(dataStr)){
            dataStr = "0";//jingbo set this default
        }
        return dataStr;
    }
    
}