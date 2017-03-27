package com.ssiot.remote.yun.webapi;

import android.util.Log;
import com.ssiot.remote.data.model.ERPTaskModel;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  
import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;  
import org.w3c.dom.NodeList;  

public class Task extends WebBasedb2 {
    private static final String tag = "Task";
    private String MethodFile = "Task.asmx";

    public void Delete() {

    }
    
    public List<ERPTaskModel> GetTasksByCropID(int userid, int cropid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " (OwnerID=0 or OwnerID=" + userid + ") and CropTypeID=" + cropid);
        String txt = exeRetString(MethodFile, "GetTask", params);
        return parse(txt);
    }

    public List<ERPTaskModel> GetTasks(int userid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " OwnerID=0 or OwnerID=" + userid);
        String txt = exeRetString(MethodFile, "GetTask", params);
        return parse(txt);
    }
    
    public List<ERPTaskModel> GetTasksByIDs(List<Integer> ids) {
        String idsStr = "(";
        for (Integer i : ids){
            idsStr += "" + i + ",";
        }
        if (idsStr.endsWith(",")){
            idsStr = idsStr.substring(0, idsStr.length() -1);
        }
        idsStr += ")";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " ID in" + idsStr);
        String txt = exeRetString(MethodFile, "GetTask", params);
        List<ERPTaskModel> tasks = parse(txt);
        
        //任务需要排序
        List<ERPTaskModel> reorderedList = new ArrayList<ERPTaskModel>();
        if (null != tasks){
            for (Integer j : ids){
                for (int k = 0 ; k < tasks.size(); k ++){
                    if (j == tasks.get(k)._id){
                        reorderedList.add(tasks.get(k));
                        break;
                    }
                }
            }
        }
        return reorderedList;
    }
    
    public int Save(ERPTaskModel model) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + model._id);
        params.put("OwnerID", "" + model._ownerid);
        params.put("CropTypeID", "" + model._croptypeid);
        params.put("StageType", "" + model._stagetype);
        params.put("TaskType", "" + model._tasktype);
        params.put("TaskDetail", "" + model._taskdetail);
        params.put("RequirePic", model._requirepic ? "1" : "0");
        params.put("RequireLocation", model._requirelocaion ? "1" : "0");
        params.put("RequireFillTableNames", "" + model._requirefilltables);
        params.put("WorkLoad", "" + model._workload);
        String txt = exeRetString(MethodFile, "Save", params);
        return parseSave(txt);
    }
    
    
    private List<ERPTaskModel> parse(String str) {
        List<ERPTaskModel> models = new ArrayList<ERPTaskModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPTaskModel cType = new ERPTaskModel();
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
                    } else if (nodeName.equals("OwenerID")) {
                        cType._ownerid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("CropTypeID")) {
                        cType._croptypeid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("StageType")){
                        cType._stagetype = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("TaskType")){
                        cType._tasktype = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("TaskDetail")){
                        cType._taskdetail = valueStr;
                    } else if (nodeName.equals("RequirePic")){
                        cType._requirepic = "1".equals(valueStr) ? true : false;
                    } else if (nodeName.equals("RequireLocation")){
                        cType._requirelocaion = "1".equals(valueStr) ? true : false;
                    } else if (nodeName.equals("RequireFillTableNames")){
                        cType._requirefilltables = valueStr;
                    } else if (nodeName.equals("WorkLoad")){
                        cType._workload = Float.parseFloat(valueStr);
                    }
                    
                    else if (nodeName.equals("OwnerName")){
                        cType._OwnerName = valueStr;
                    } else if (nodeName.equals("StageTypeName")){
                        cType._StageName = valueStr;
                    } else if (nodeName.equals("TaskTypeName")){
                        cType._TypeName = valueStr;
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPTaskModel m : models){
            Log.v(tag, "------id:" +m._id + " taskdetail:" + m._taskdetail);
        }
        return models;
    }

}
