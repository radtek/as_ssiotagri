package com.ssiot.remote.yun.webapi;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPGrowthModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ERPGrowth extends WebBasedb2{
    
    private static final String tag = "Growth";
    private String MethodFile = "Growth.asmx";

    public void Delete() {

    }

    public List<ERPGrowthModel> GetGrowth(int mainUserid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " BatchID in (select ID from cms2016.dbo.ERP_ProductBatch where IsFinish<>1 and UserID=" + mainUserid + ")");
        String txt = exeRetString(MethodFile, "GetGrowth", params);
        return parse(txt);
    }
    
    public List<ERPGrowthModel> GetGrowthByBatch(int batchid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " BatchID=" + batchid);
        String txt = exeRetString(MethodFile, "GetGrowth", params);
        return parse(txt);
    }
    
    public int Save(ERPGrowthModel m) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + m._id);
        params.put("BatchID", "" + m._batchid);
        params.put("ProductLength", "" + m._productlength);
        params.put("ProductWeight", "" + m._productweight);
        params.put("CreateTime", ""+ (int) (m._createtime.getTime()/1000));
        params.put("Image", "" + m._image);
        params.put("InnerType", ""+m._sex);
        String txt = exeRetString(MethodFile, "Save", params);
        return saveParse(txt);
    }
    
    private List<ERPGrowthModel> parse(String str) {
        List<ERPGrowthModel> models = new ArrayList<ERPGrowthModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPGrowthModel cType = new ERPGrowthModel();
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
                    } else if (nodeName.equals("BatchID")) {
                        cType._batchid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("ProductLength")) {
                        cType._productlength = Float.parseFloat(valueStr);//new BigDecimal(valueStr);
//                        Log.v(tag, "" + new BigDecimal(valueStr).toString());//2.1还是变成了2.0999999046325684
                    } else if (nodeName.equals("ProductWeight")){
                        cType._productweight = Float.parseFloat(valueStr);
                    } else if (nodeName.equals("CreateTime")){
                        cType._createtime = new Timestamp((long) Integer.parseInt(valueStr) * 1000);//TODO
                    } else if (nodeName.equals("Image")){
                        cType._image = valueStr;
                    } else if (nodeName.equals("BatchName")){
                    	cType._batchName = valueStr;
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPGrowthModel m : models){
            Log.v(tag, "------id:" +m._id + " 长度:" + m._productlength);
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
