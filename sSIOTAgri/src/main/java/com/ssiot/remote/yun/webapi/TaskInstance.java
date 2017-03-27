package com.ssiot.remote.yun.webapi;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPTaskInstanceModel;
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

public class TaskInstance extends WebBasedb2 {
    private static final String tag = "TaskInstance";
    private String MethodFile = "TaskInstance.asmx";

    public void Delete() {

    }
    
    public List<ERPTaskInstanceModel> GetTaskInstance(String where) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", where);
        String txt = exeRetString(MethodFile, "GetTaskInstance", params);
        return parse(txt);
    }

    public List<ERPTaskInstanceModel> GetTaskInstance(int id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " ID=" + id);
        String txt = exeRetString(MethodFile, "GetTaskInstance", params);
        return parse(txt);
    }
    
    public List<ERPTaskInstanceModel> GetMySendTaskInstances(int userid){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " UserID=" + userid);
        String txt = exeRetString(MethodFile, "GetTaskInstance", params);
        return parse(txt);
    }
    
    public List<ERPTaskInstanceModel> GetMyReceiveTaskInstances(int userid){
        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("where", " WorkerIDs like '%:" + userid +"}%'");
        String str = "'," + userid + ",'";
        params.put("where", " charindex("+str+",','+WorkerIDs+',') > 0");
        String txt = exeRetString(MethodFile, "GetTaskInstance", params);
        return parse(txt);
    }
    
    public int EnabelTaskToTaskInstance(int taskid, int batchid, String workerids, int userid) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("taskid", "" + taskid);
        params.put("batchid", "" + batchid);
        params.put("workerids", "" + workerids);
        params.put("userid", "" + userid);
        String txt = exeRetString(MethodFile, "EnabelTaskToTaskInstance", params);
        return saveParse(txt);
    }
    
    public int Save(ERPTaskInstanceModel taskInstance) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + taskInstance._id);
        params.put("TaskID", "" + taskInstance._taskmouldid);
        params.put("OwnerID", "" + taskInstance._ownerid);
        params.put("BatchID", ""+taskInstance._batchid);
        params.put("CropTypeID", "" + taskInstance._croptypeid);
        params.put("StageType", "" + taskInstance._stagetype);
        params.put("TaskType", "" + taskInstance._tasktype);
        params.put("TaskDetail", "" + taskInstance._taskdetail);
        params.put("RequirePic", taskInstance._requirepic ? "1" : "0");
        params.put("RequireLocation", taskInstance._requirelocaion ? "1" : "0");
        params.put("RequireFillTableNames", "" + taskInstance._requirefilltables);
        params.put("WorkLoad", "" + taskInstance._workload);
        params.put("WorkerIDs", "" + taskInstance._workerids);
        params.put("UserID", "" + taskInstance._userid);
        params.put("Img", "" + taskInstance._img);
        params.put("State", "" + taskInstance._state);
        String txt = exeRetString(MethodFile, "Save", params);
        return saveParse(txt);
    }
    
    
    private List<ERPTaskInstanceModel> parse(String str) {
        List<ERPTaskInstanceModel> models = new ArrayList<ERPTaskInstanceModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPTaskInstanceModel cType = new ERPTaskInstanceModel();
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
                    } else if (nodeName.equals("TaskID")) {
                        cType._taskmouldid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("OwenerID")) {
                        cType._ownerid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("BatchID")) {
                        cType._batchid = Integer.parseInt(valueStr);
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
                    
                    else if (nodeName.equals("WorkerIDs")){
                        cType._workerids = valueStr;
                    } else if (nodeName.equals("UserID")){
                        cType._userid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("Img")){
                        cType._img = valueStr;
                    } else if (nodeName.equals("State")){
                        cType._state = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("CreateTime")){
                    	cType._createtime = new Timestamp((long) Integer.parseInt(valueStr) * 1000);//Utils.getMyTimestamp(valueStr);
                    } else if (nodeName.equals("TaskID")){//
                    	try {
                    		cType._taskmouldid = Integer.parseInt(valueStr);
						} catch (Exception e) {
							e.printStackTrace();
						}
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
        for (ERPTaskInstanceModel m : models){
            Log.v(tag, "------id:" +m._id + " taskdetail:" + m._taskdetail);
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
//        try {
//            Log.v(tag, "--saveparse--"+str);
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
//            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
//            InputStream is = new ByteArrayInputStream(str.getBytes());
//            Document doc = builder.parse(is);// 解析输入流 得到Document实例
//            Log.v(tag, "--saveparse--doc:"+doc.toString());
//            Element rootElement = doc.getDocumentElement();
//            Log.v(tag, "--saveparse--rootelement:"+rootElement.toString());//---------ok le ?
//            NodeList items = rootElement.getElementsByTagName("int");
//            Log.v(tag, "----");//TODO
//            result = 1;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return result;
    }

}
