package com.ssiot.remote.yun.webapi;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.data.model.ERPUserInputsTypeModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WS_UserInputsType extends WebBasedb2{
    
    private static final String tag = "UserInputsType";
    private String MethodFile = "UserInputsType.asmx";

    public void Delete() {

    }

    public List<ERPUserInputsTypeModel> GetUserInputsType(int mainUserid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " UserID=0 or UserID=" + mainUserid);
        String txt = exeRetString(MethodFile, "GetUserInputsType", params);
        return parse(txt);
    }
    
    public int Save(ERPUserInputsTypeModel m) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + m._id);
        params.put("UserID", "" + m._userid);
        params.put("InputsTypeName", "" + m._inputstypename);
        params.put("InputsUnit", "" + m._inputsunit);
        String txt = exeRetString(MethodFile, "Save", params);
        return saveParse(txt);
    }
    
    
    private List<ERPUserInputsTypeModel> parse(String str) {
        List<ERPUserInputsTypeModel> models = new ArrayList<ERPUserInputsTypeModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPUserInputsTypeModel cType = new ERPUserInputsTypeModel();
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
                    } else if (nodeName.equals("UserID")) {
                        cType._userid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("InputsTypeName")) {
                        cType._inputstypename = valueStr;
                    } else if (nodeName.equals("InputsUnit")){
                        cType._inputsunit = valueStr;
                    }
                    
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPUserInputsTypeModel m : models){
            Log.v(tag, "------id:" +m._id + " inputstypename:" + m._inputstypename);
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