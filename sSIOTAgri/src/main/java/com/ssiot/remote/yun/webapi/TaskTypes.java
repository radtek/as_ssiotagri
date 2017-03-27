package com.ssiot.remote.yun.webapi;

import android.util.Log;
import com.ssiot.remote.data.model.ERPTaskTypesModel;
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

public class TaskTypes extends WebBasedb2 {
    private static final String tag = "TaskTypes";
    private String MethodFile = "TaskTypes.asmx";

    public void Delete() {

    }

    public List<ERPTaskTypesModel> GetAllTaskTypes(int userid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "UserID=0 or UserID=" + userid);
        String txt = exeRetString(MethodFile, "GetTaskStageTypes", params);//王生辉误改此名
        return parse(txt);
    }
    
    public void Save() {

    }
    
    
    private List<ERPTaskTypesModel> parse(String str) {
        List<ERPTaskTypesModel> models = new ArrayList<ERPTaskTypesModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPTaskTypesModel pModel = new ERPTaskTypesModel();
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
                    } else if (nodeName.equals("UserID")) {
                        pModel._userid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("Name")) {
                        pModel._name = valueStr;
                    }
                }
                models.add(pModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPTaskTypesModel m : models){
            Log.v(tag, "------id:" +m._id + " owner:" + m._name + " name:" + m._name);
        }
        return models;
    }

}
