package com.ssiot.remote.yun.webapi;

import android.util.Log;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPTaskReportModel;
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

public class TaskReport extends WebBasedb2 {
    private static final String tag = "TaskReport";
    private String MethodFile = "TaskReport.asmx";

    public void Delete() {
    	
    }
    
    public List<ERPTaskReportModel> GetTaskReports(int taskInstanceID){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "TaskInstanceID=" + taskInstanceID);
        String txt = exeRetString(MethodFile, "GetTaskReport", params);
        return parse(txt);
    }
    
    public int Save(ERPTaskReportModel taskReport) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", ""+taskReport._id);
        params.put("TaskInstanceID", ""+taskReport._taskinstanceid);
        params.put("UserID", ""+taskReport._userid);
        params.put("CreateTime", ""+(int)(taskReport._createtime.getTime()/1000));//82端口改为了int
        params.put("ReportDetail", ""+taskReport._reportdetail);
        params.put("Imgs", ""+taskReport._imgs);
        params.put("Location", ""+taskReport._location);
        params.put("FillTables", ""+taskReport._filltable);
        params.put("Finish", taskReport._finish ? "1" : "0");
        String txt = exeRetString(MethodFile, "Save", params);
        return parseSave(txt);
    }
    
    private List<ERPTaskReportModel> parse(String str) {
        List<ERPTaskReportModel> models = new ArrayList<ERPTaskReportModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPTaskReportModel cType = new ERPTaskReportModel();
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
                    } else if (nodeName.equals("TaskInstanceID")) {
                        cType._taskinstanceid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("UserID")) {
                        cType._userid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("CreateTime")){
                        cType._createtime = new Timestamp((long) Integer.parseInt(valueStr) * 1000);//Utils.getMyTimestamp(valueStr);
                    } else if (nodeName.equals("ReportDetail")){
                        cType._reportdetail = valueStr;
                    } else if (nodeName.equals("Imgs")){
                        cType._imgs = valueStr;
                    } else if (nodeName.equals("Location")){
                        cType._location = valueStr;
                    } else if (nodeName.equals("FillTables")){
                        cType._filltable = valueStr;
                    } else if (nodeName.equals("Finish")){
                        cType._finish = "1".equals(valueStr);
                    } else if (nodeName.equals("UserName")){
                    	cType._UserName = valueStr;
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPTaskReportModel m : models){
            Log.v(tag, "------id:" +m._id + " reportdetail:" + m._reportdetail);
        }
        return models;
    }
}
