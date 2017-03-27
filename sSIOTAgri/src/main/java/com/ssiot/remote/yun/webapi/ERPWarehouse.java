package com.ssiot.remote.yun.webapi;

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

import android.util.Log;

import com.ssiot.remote.data.model.ERPWarehouseModel;

public class ERPWarehouse extends WebBasedb2{
	private static final String tag = "ERPWarehouse";
    private String MethodFile = "WareHouse.asmx";

    public void Delete() {

    }

    public List<ERPWarehouseModel> GetWarehouse(int userid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "UserID=" + userid);
        String txt = exeRetString(MethodFile, "GetWareHouse", params);
        return parse(txt);
    }
    
    private List<ERPWarehouseModel> parse(String str) {
        List<ERPWarehouseModel> models = new ArrayList<ERPWarehouseModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPWarehouseModel cType = new ERPWarehouseModel();
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
                    } else if (nodeName.equals("UserInputsTypeID")) {
                        cType._userinputstypeid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("Name")) {
                        cType._name = valueStr;
                    } else if (nodeName.equals("Total")){
                        cType._total = Float.parseFloat(valueStr);
                    } else if (nodeName.equals("AmountUnit")){
                        cType._amountUnit = valueStr;
                    } else if (nodeName.equals("PreAmount")){//拼写错误
                        cType._peramount = Float.parseFloat(valueStr);	
                    } else if (nodeName.equals("PreAmountUnit")){
                        cType._peramountunit = valueStr;
                    } else if (nodeName.equals("UserID")) {
                        cType._userid = Integer.parseInt(valueStr);
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPWarehouseModel m : models){
            Log.v(tag, "------id:" +m._id + " name:" + m._name);
        }
        return models;
    }
    
    
}