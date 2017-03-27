package com.ssiot.remote.yun.webapi;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPProductBatchModel;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  
import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;  
import org.w3c.dom.NodeList;  

public class ProductBatch extends WebBasedb2 {
    private static final String tag = "ProductBatch";
    private String MethodFile = "ProductBatch.asmx";

    public void Delete() {

    }

    public List<ERPProductBatchModel> GetProductBatch(int userid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "UserID=" + userid);
        String txt = exeRetString(MethodFile, "GetProductBatch", params);// TODO xml工具
        return parse(txt);
    }
    
    public List<ERPProductBatchModel> GetActiveProductBatch(int userid) {//只查询未完成的批次
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " IsFinish<>1 and UserID=" + userid);
        String txt = exeRetString(MethodFile, "GetProductBatch", params);// TODO xml工具
        return parse(txt);
    }
    
    public List<ERPProductBatchModel> GetProductBatch(String where) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", where);
        String txt = exeRetString(MethodFile, "GetProductBatch", params);// TODO xml工具
        return parse(txt);
    }

    public int Save(ERPProductBatchModel m) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + m._id);
        params.put("Name", "" + m._name);
        params.put("InputsInID", "" + m._inputsinid);
        params.put("CropTypeID", "" + m._croptype);
        params.put("FacilityIDs", "" + m._facilityids);
        params.put("PlanID", "" + m._planid);
        params.put("ExpectedStartTime", Utils.formatTime(m._expectstart));
        params.put("ExpectedEndTime", Utils.formatTime(m._expectend));
        params.put("UserID", "" + m._userid);
        String txt = exeRetString(MethodFile, "Save", params);
        return saveParse(txt);
    }
    
    
    private List<ERPProductBatchModel> parse(String str) {
        List<ERPProductBatchModel> models = new ArrayList<ERPProductBatchModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPProductBatchModel cType = new ERPProductBatchModel();
                Node item = items.item(i);
                NodeList properties = item.getChildNodes();
                for (int j = 0; j < properties.getLength(); j++) {
                    Node property = properties.item(j);
                    String nodeName = property.getNodeName();
                    if (property.getFirstChild() == null){
                        continue;
                    }
                    String valueStr = property.getFirstChild().getNodeValue();
                    if (nodeName.equals("ID")) {
                        cType._id = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("UserID")) {
                        cType._userid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("Name")) {
                        cType._name = valueStr;
                    } else if (nodeName.equals("InputsInID")){
                        cType._inputsinid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("CropTypeID")){
                        cType._croptype = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("FacilityIDs")){
                    	cType._facilityids = valueStr;
                    } else if (nodeName.equals("PlanID")){
                        cType._planid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("ExpectedStartTime")){
                        cType._expectstart = new Timestamp((long) Integer.parseInt(valueStr) * 1000);//Utils.getMyTimestamp(valueStr);
                    } else if (nodeName.equals("ExpectedEndTime")){
                        cType._expectend = new Timestamp((long) Integer.parseInt(valueStr) * 1000);
                    } else if (nodeName.equals("CropTypeName")){
                        cType._CropName = valueStr;
                    } else if (nodeName.equals("UserName")){
                        cType._UserName = valueStr;
                    } else if (nodeName.equals("FacilityNames")){
                        cType._FacilityName = valueStr;
                    } else if (nodeName.equals("IsFinish")){
                    	cType._isfinish = "true".equalsIgnoreCase(valueStr);
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPProductBatchModel m : models){
            Log.v(tag, "------id:" +m._id + " useid:" + m._userid + " name:" + m._name);
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
