package com.ssiot.remote.yun.webapi;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPInputsOutModel;

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

public class WS_InputsOut extends WebBasedb2{
    
    private static final String tag = "InputsOut";
    private String MethodFile = "InputsOut.asmx";

    public void Delete() {

    }

    public List<ERPInputsOutModel> GetInputsOut(int mainUserid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " UserID=" + mainUserid);
        String txt = exeRetString(MethodFile, "GetInputsOut", params);
        return parse(txt);
    }
    
    public int Save(ERPInputsOutModel m) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + m._id);
        params.put("UserID", "" + m._userid);
        params.put("InputsTypeID", "" + m._inputstypeid);
        params.put("NameDetail", "" + m._namedetail);
        params.put("Amount", "" + m._amount);
        params.put("TakingPerson", "" + m._takingperson);
        params.put("TakingTime", Utils.formatTime(m._takingtime));
        String txt = exeRetString(MethodFile, "Save", params);
        return saveParse(txt);
    }
    
    
    private List<ERPInputsOutModel> parse(String str) {
        List<ERPInputsOutModel> models = new ArrayList<ERPInputsOutModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPInputsOutModel cType = new ERPInputsOutModel();
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
                    } else if (nodeName.equals("InputsTypeID")) {
                        cType._inputstypeid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("NameDetail")){
                        cType._namedetail = valueStr;
                    } else if (nodeName.equals("Amount")){
                        cType._amount = Float.parseFloat(valueStr);
                    } else if (nodeName.equals("TakingPerson")){
                        cType._takingperson = valueStr;
                    } else if (nodeName.equals("TakingTime")){
                        if (!TextUtils.isEmpty(valueStr)){
                            cType._takingtime = Utils.getMyTimestamp(valueStr);
                        }
                    } 
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPInputsOutModel m : models){
            Log.v(tag, "------id:" +m._id + " namedetail:" + m._namedetail);
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