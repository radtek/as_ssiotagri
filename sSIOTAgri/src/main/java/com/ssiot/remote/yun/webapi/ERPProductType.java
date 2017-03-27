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

import com.ssiot.remote.data.model.ERPProductTypeModel;

public class ERPProductType extends WebBasedb2{
	private static final String tag = "ERPProductType";
    private String MethodFile = "ProductType.asmx";

    public void Delete() {

    }

    public List<ERPProductTypeModel> GetProductType() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "ParentID in (select ID from ERP_ProductType where ParentID=2)");//先固定全部查找渔业的,渔业-甲克类-螃蟹
        String txt = exeRetString(MethodFile, "GetProductType", params);
        return parse(txt);
    }
    
    private List<ERPProductTypeModel> parse(String str) {
        List<ERPProductTypeModel> models = new ArrayList<ERPProductTypeModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPProductTypeModel cType = new ERPProductTypeModel();
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
                    } else if (nodeName.equals("Name")) {
                        cType._name = valueStr;
                    } else if (nodeName.equals("ParentID")){
                        cType._parentid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("Depth")){
                        cType._depth = Integer.parseInt(valueStr);
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPProductTypeModel m : models){
            Log.v(tag, "------id:" +m._id + " name:" + m._name);
        }
        return models;
    }
    
    
}