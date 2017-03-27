package com.ssiot.remote.yun.webapi;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ssiot.agri.ContextUtilApp;
import com.ssiot.remote.Utils;
import com.ssiot.remote.receiver.SsiotReceiver;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WebBaseFish{
    private static final String tag = "WebBaseFFFFFFF-FISH";
    public static String URI_STATION = "http://db.fisher88.com:81/";//数据库（公网）114.55.145.45 内网1025.253.68 名fisher密码324#$th6s 数据库名fisher
    private static final String NAMESPACE = "http://tempuri.org/";
    
//    private String exeRetString(String methodFile, String method, Map<String, String> params){
//        String urlString = "URI_STATION" + methodFile;//.asmx
//        
//        URL url;
//        try {
//            url = new URL(urlString);
//            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        return null;
//    }
    
    
    public String exeRetString(String methodFile, String method, HashMap<String, String> params) {
        SoapObject soapObject = new SoapObject(NAMESPACE, method);
        // 给调用参数赋值
        if (null != params && params.size() != 0) {
            for (String key : params.keySet()) {
                Log.v(tag, "-----webapi " + method+ " params: key:" + key + " value:" + params.get(key));
                soapObject.addProperty(key, params.get(key));
            }
        }
        // 设置一些基本参数
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.bodyOut = soapObject;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soapObject);
        HttpTransportSE httpTransportSE = new HttpTransportSE(URI_STATION + methodFile);
        try {
            // 实际调用webservice的操作
            httpTransportSE.call(NAMESPACE + method, envelope);
            
        } catch (Exception e) {
            showErrorToast(method);
            e.printStackTrace();
        }
        // 获得调用的结果
        SoapObject object = (SoapObject) envelope.bodyIn;//错误时java.lang.ClassCastException: org.ksoap2.SoapFault cannot be cast to org.ksoap2.serialization.SoapObject
        String txt = null;
        if (object == null){
            Log.e(tag, "!!!!!!!!!!----web ret null");
        } else {
        	if (object.getPropertyCount() <= 0){
            	Log.e(tag, "!!!!!!!!!webret count=0");
            	return "";
            } else {
            	txt = object.getProperty(0).toString();
            	//返回anyType{}怎么处理 TODO
                Log.v(tag, "-------"+method+":webret----" + txt);
            }
        }
        try {
			httpTransportSE.getServiceConnection().disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}//TODO 测试小米上进入后台关屏5分钟后连接不上，即使唤醒也不行
        return txt;
    }
    
    private void showErrorToast(String str){
    	if (!Utils.isBackground(ContextUtilApp.getInstance())){
    		Intent i = new Intent(SsiotReceiver.ACTION_SSIOT_MSG);
            i.putExtra("showmsg", "WebService出现问题。" + str);
            ContextUtilApp.getInstance().sendBroadcast(i);
    	}
    }
    
    public int parseSave(String str) {
        int result = 0;
        if (!TextUtils.isEmpty(str)){
            try {
                result = Integer.parseInt(str);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}