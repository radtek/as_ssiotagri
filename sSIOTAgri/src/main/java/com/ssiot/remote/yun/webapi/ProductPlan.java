package com.ssiot.remote.yun.webapi;

import android.util.Log;
import com.ssiot.remote.data.model.ERPProductPlanModel;
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

public class ProductPlan extends WebBasedb2 {
    private static final String tag = "ProductPlan";
    private String MethodFile = "ProductPlan.asmx";

    public void Delete() {

    }

    public List<ERPProductPlanModel> GetProductPlan(int userid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "OwnerID=0 or OwnerID=" + userid);
        String txt = exeRetString(MethodFile, "GetProductPlan", params);// TODO xml工具
        return parse(txt);
    }
    
    public List<ERPProductPlanModel> GetProductPlan(String where) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", where);
        String txt = exeRetString(MethodFile, "GetProductPlan", params);// TODO xml工具
        return parse(txt);
    }
    
    public ERPProductPlanModel GetProductPlanByID(int id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "ID=" + id);
        String txt = exeRetString(MethodFile, "GetProductPlan", params);// TODO xml工具
        List<ERPProductPlanModel> ms = parse(txt);
        if (null != ms && ms.size() > 0){
            return ms.get(0);
        }
        return null;
    }

    public int Save(ERPProductPlanModel model) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + model._id);
        params.put("OwnerID", "" + model._owenerid);
        params.put("Name", "" + model._name);
        params.put("CropTypeID", "" + model._croptype);
        params.put("TaskIDs", model._taskids);
        String txt = exeRetString(MethodFile, "Save", params);
        return parseSave(txt);
    }
    
    
    private List<ERPProductPlanModel> parse(String str) {
        List<ERPProductPlanModel> models = new ArrayList<ERPProductPlanModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPProductPlanModel pModel = new ERPProductPlanModel();
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
                        pModel._id = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("OwnerID")) {
                        pModel._owenerid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("Name")) {
                        pModel._name = valueStr;
                    } else if (nodeName.equals("CropTypeID")){
                        pModel._croptype = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("TaskIDs")){
                        pModel._taskids = valueStr;
                    }
                }
                models.add(pModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPProductPlanModel m : models){
            Log.v(tag, "------id:" +m._id + " owner:" + m._owenerid + " name:" + m._name);
        }
        return models;
    }

}
