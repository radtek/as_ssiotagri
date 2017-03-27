package com.ssiot.remote.yun;

import android.R.integer;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.MarshalHashtable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class MyWebService{
    private static final String tag = "MyWebService";
    
    String serviceUrl = "http://192.168.1.110:8080/";
    // 定义webservice的命名空间  
    public static final String SERVICE_NAMESPACE = "http://192.168.1.110:8080/";
       // 定义webservice提供服务的url  
    public static final String SERVICE_URL = "http://192.168.1.110:8080/SsiotWebserviceTest9/services/WebService1";
    
    private ArrayList<String> array = new ArrayList<String>();
    public MyWebService(){
        array.add("jiangsu");
        array.add("shanghai");
    }
    
    public float plus(float x, float y){
        try {
            return exeRetFloat("plus", x, y);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
    
    public int func2(int x){
        try {
            return exeRetInt("func2", x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public String func5(ArrayList<String> as){
        try {
            exeRetArrayList("func5", as);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ArrayList<String> func6(){
        try {
            exeRetArrayList("func6");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //--------------------------------以下是webservice具体调用--------
    
    private float exeRetFloat(String methodName, float f1, float f2) throws Exception{
        HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);  
        ht.debug = true;  
        SoapObject soapObject = new SoapObject(SERVICE_NAMESPACE, methodName);  
        soapObject.addProperty("x", f1);//float 不能序列化？？
        soapObject.addProperty("y", f2);
        // 使用soap1.1协议创建envelop对象  
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  
        envelope.bodyOut = soapObject;
        
        envelope.encodingStyle = SoapSerializationEnvelope.XSD;//??http://stackoverflow.com/questions/18943475/ksoap2-in-android-cannot-serialize
        MarshalFloat mf = new MarshalFloat();
        mf.register(envelope);//解决了float不能串行化的问题！
        
        // 设置与.NET提供的webservice保持较好的兼容性  
        envelope.dotNet = true;
  
        // 调用webservice  
        try {
            ht.call(SERVICE_NAMESPACE + methodName, envelope);//参数不对导致HTTP request failed, HTTP status: 500
            if (envelope.getResponse() != null) {
                // 获取服务器响应返回的SOAP消息  
                SoapObject result = (SoapObject) envelope.bodyIn;  
                Object detail = (Object) result.getProperty(methodName  
                        + "Return");  //Result
                if (detail instanceof SoapPrimitive){
                    Log.v(tag, "----SoapPrimitive----"+((SoapPrimitive) detail).toString());
                } else if (detail instanceof SoapObject){
                    Log.v(tag, "---SoapObject----"+((SoapObject) detail).toString());//TODO 怎么解析返回值
                }
                // 解析服务器响应的SOAP消息  
                Log.v(tag, "--------webservice return:"+ detail.toString());//--------webservice return:nullreturn ok?????
//                return parseProvinceOrCity(detail);  
                return 0;
            }  
        } catch (SoapFault e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (XmlPullParserException e) {  
            e.printStackTrace();  
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private int exeRetInt(String methodName, int x) throws Exception{
        HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);  
        ht.debug = true;  
        SoapObject soapObject = new SoapObject(SERVICE_NAMESPACE, methodName);  
        soapObject.addProperty("x", x);//float 不能序列化？？
        // 使用soap1.1协议创建envelop对象  
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  
        envelope.bodyOut = soapObject;
        
        
        
        // 设置与.NET提供的webservice保持较好的兼容性  
        envelope.dotNet = true;
  
        // 调用webservice  
        try {
            ht.call(SERVICE_NAMESPACE + methodName, envelope);//参数不对导致HTTP request failed, HTTP status: 500
            if (envelope.getResponse() != null) {
                // 获取服务器响应返回的SOAP消息  
                SoapObject result = (SoapObject) envelope.bodyIn;  
                Object detail = (Object) result.getProperty(methodName  
                        + "Return");  //Result
                if (detail instanceof SoapPrimitive){
                    Log.v(tag, "----SoapPrimitive----"+((SoapPrimitive) detail).toString());
                } else if (detail instanceof SoapObject){
                    Log.v(tag, "---SoapObject----"+((SoapObject) detail).toString());//TODO 怎么解析返回值
                }
                // 解析服务器响应的SOAP消息  
                Log.v(tag, "--------webservice return:"+ detail.toString());//--------webservice return:nullreturn ok?????
//                return parseProvinceOrCity(detail);  
                return 0;
            }  
        } catch (SoapFault e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (XmlPullParserException e) {  
            e.printStackTrace();  
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private ArrayList exeRetArrayList(String methodName, ArrayList<String> as){
        HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);  
        ht.debug = true;  
        SoapObject soapObject = new SoapObject(SERVICE_NAMESPACE, methodName);  
        soapObject.addProperty("x", as);//float 不能序列化？？
        // 使用soap1.1协议创建envelop对象  
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  
        envelope.bodyOut = soapObject;
        
        // 设置与.NET提供的webservice保持较好的兼容性  
        envelope.dotNet = true;
  
        // 调用webservice  
        try {
            ht.call(SERVICE_NAMESPACE + methodName, envelope);//参数不对导致HTTP request failed, HTTP status: 500
            if (envelope.getResponse() != null) {
                // 获取服务器响应返回的SOAP消息  
                SoapObject result = (SoapObject) envelope.bodyIn;  
                Object detail = (Object) result.getProperty(methodName  
                        + "Return");  //Result
                if (detail instanceof SoapPrimitive){
                    Log.v(tag, "----SoapPrimitive----"+((SoapPrimitive) detail).toString());
                } else if (detail instanceof SoapObject){
                    Log.v(tag, "---SoapObject----"+((SoapObject) detail).toString());//TODO 怎么解析返回值
                }
                // 解析服务器响应的SOAP消息  
                Log.v(tag, "--------webservice return:"+ detail.toString());//--------webservice return:nullreturn ok?????
//                return parseProvinceOrCity(detail);  
                return null;
            }  
        } catch (SoapFault e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (XmlPullParserException e) {  
            e.printStackTrace();  
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private ArrayList exeRetArrayList(String methodName){
        HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);  
        ht.debug = true;  
        SoapObject soapObject = new SoapObject(SERVICE_NAMESPACE, methodName);  
        // 使用soap1.1协议创建envelop对象  
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  
        envelope.bodyOut = soapObject;
        
        // 设置与.NET提供的webservice保持较好的兼容性  
        envelope.dotNet = true;
  
        // 调用webservice  
        try {
            ht.call(SERVICE_NAMESPACE + methodName, envelope);//参数不对导致HTTP request failed, HTTP status: 500
            if (envelope.getResponse() != null) {
                // 获取服务器响应返回的SOAP消息  
                SoapObject result = (SoapObject) envelope.bodyIn;  
                Object detail = (Object) result.getProperty(methodName  
                        + "Return");  //Result
                if (detail instanceof SoapPrimitive){
                    Log.v(tag, "----SoapPrimitive----"+((SoapPrimitive) detail).toString());
                } else if (detail instanceof SoapObject){
                    Log.v(tag, "---SoapObject----"+((SoapObject) detail).toString());//TODO 怎么解析返回值
                }
                // 解析服务器响应的SOAP消息  
                Log.v(tag, "--------webservice return:"+ detail.toString());//--------webservice return:nullreturn ok?????
//                return parseProvinceOrCity(detail);  
                return null;
            }  
        } catch (SoapFault e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (XmlPullParserException e) {  
            e.printStackTrace();  
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}