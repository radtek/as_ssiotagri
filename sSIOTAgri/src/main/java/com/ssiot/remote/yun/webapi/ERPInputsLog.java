package com.ssiot.remote.yun.webapi;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPInputsLogModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ERPInputsLog extends WebBasedb2{
    
    private static final String tag = "InputsLog";
    private String MethodFile = "InputsLog.asmx";

    public void Delete() {

    }

    public List<ERPInputsLogModel> GetInputsLog(int mainUserid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "BatchID in (select ID from ERP_ProductBatch where UserID=" + mainUserid + ")");
        String txt = exeRetString(MethodFile, "GetInputsLog", params);
        return parse(txt);
    }
    
    public List<ERPInputsLogModel> GetInputsLogByBatch(int batchid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "BatchID=" + batchid);
        String txt = exeRetString(MethodFile, "GetInputsLog", params);
        return parse(txt);
    }
    
    public int Save(ERPInputsLogModel m) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + m._id);
        params.put("Name", "" + m._name);
        params.put("InputsTypeID", "" + m._inputstypeid);
        params.put("BatchID", "" + m._batchid);
        params.put("Amount", "" + m._amount);
        params.put("Unit", "" + m._unit);
        params.put("Time", ""+m._time.getTime()/1000);//Utils.formatTime(m._time)
        params.put("Brand", "" + m._brand);
        params.put("UseDate", "" + m._usedate);
        params.put("WarehouseID", "" + m._warehouseid);
        String txt = exeRetString(MethodFile, "Save", params);
        return saveParse(txt);
    }
    
    
    private List<ERPInputsLogModel> parse(String str) {
        List<ERPInputsLogModel> models = new ArrayList<ERPInputsLogModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPInputsLogModel cType = new ERPInputsLogModel();
                Node item = items.item(i);
                NodeList properties = item.getChildNodes();
                for (int j = 0; j < properties.getLength(); j++) {
                    Node property = properties.item(j);
                    String nodeName = property.getNodeName();
                    if (null == property.getFirstChild()){
                        continue;
                    }
                    String valueStr = property.getFirstChild().getNodeValue();
                    if (nodeName.equals("ID")) {
                        cType._id = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("Name")) {
                        cType._name = valueStr;
                    } else if (nodeName.equals("InputsTypeID")) {
                        cType._inputstypeid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("BatchID")){
                        cType._batchid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("Amount")){
                        cType._amount = Float.parseFloat(valueStr);
                    } else if (nodeName.equals("Unit")){
                        cType._unit = valueStr;
                    } else if (nodeName.equals("Time")){
                        cType._time = new Timestamp((long) Integer.parseInt(valueStr) * 1000);//TODO
                    } else if (nodeName.equals("Brand")){
                        cType._brand = valueStr;
                    } else if (nodeName.equals("UseDate")){
                    	cType._usedate = valueStr;
                    } else if (nodeName.equals("WarehouseID")){
                        cType._warehouseid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("BatchName")){
                    	cType._batchname = valueStr;
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPInputsLogModel m : models){
            Log.v(tag, "------id:" +m._id + " name:" + m._name);
        }
        return models;
    }
    
    private int saveParse(String str) {
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
