package com.ssiot.remote.yun.webapi;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPFertilizerModel;

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

public class ERPFertilizer extends WebBase {
    private static final String tag = "ERPFertilizer";
    private String MethodFile = "Fertilizer.asmx";

    public void Delete() {

    }

    public List<ERPFertilizerModel> GetFertilizer(int mainUserid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " productBatchID In (select ID from cms2016.dbo.ERP_ProductBatch where UserID=" + mainUserid + ") ");//TODO
        String txt = exeRetString(MethodFile, "GetFertilizer", params);
        return parse(txt);
    }
    
    public int Save(ERPFertilizerModel m) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + m._id);
        params.put("ProductBatchID", "" + m._productbatchid);
        params.put("UseDate", "" + m._usedate);
        params.put("Name", "" + m._name);
        params.put("Dosase", "" + m._dosage);
        params.put("Brand", "" + m._brand);
        params.put("SupplierName", m._suppliername);
        params.put("Unit", m._unit);
        params.put("UseDateTime", "" + Utils.formatTime(m._usedatetime));
        params.put("Type", "" + m._type);
        String txt = exeRetString(MethodFile, "Save", params);
        return saveParse(txt);
    }
    
    
    private List<ERPFertilizerModel> parse(String str) {
        List<ERPFertilizerModel> models = new ArrayList<ERPFertilizerModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPFertilizerModel cType = new ERPFertilizerModel();
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
                    } else if (nodeName.equals("ProductBatchID")) {
                        cType._productbatchid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("UseDate")) {
                        cType._usedate = valueStr;
                    } else if (nodeName.equals("Name")) {
                        cType._name = valueStr;
                    } else if (nodeName.equals("Dosase")) {
                        cType._dosage = Float.parseFloat(valueStr);
                    } else if (nodeName.equals("Brand")) {
                        cType._brand = valueStr;
                    } else if (nodeName.equals("SupplierName")) {
                        cType._suppliername = valueStr;
                    } else if (nodeName.equals("Unit")) {
                        cType._unit = valueStr;
                    } else if (nodeName.equals("UseDateTime")) {
                        if (!TextUtils.isEmpty(valueStr)){
                            cType._usedatetime = Utils.getMyTimestamp(valueStr);
                        }
                    } else if (nodeName.equals("Type")) {
                        cType._type = valueStr;
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPFertilizerModel m : models){
            Log.v(tag, "------id:" +m._id + " batchid:" + m._productbatchid);
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
