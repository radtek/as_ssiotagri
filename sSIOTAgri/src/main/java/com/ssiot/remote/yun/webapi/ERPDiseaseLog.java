package com.ssiot.remote.yun.webapi;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPDiseaseLogModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ERPDiseaseLog extends WebBasedb2{
    
    private static final String tag = "DiseaseLog";
    private String MethodFile = "DiseaseLog.asmx";

    public void Delete() {

    }

    public List<ERPDiseaseLogModel> GetDiseaseLog(int mainUserid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " BatchID in (select ID from cms2016.dbo.ERP_ProductBatch where UserID=" + mainUserid + ")");
        String txt = exeRetString(MethodFile, "GetDiseaseLog", params);
        return parse(txt);
    }
    
    public List<ERPDiseaseLogModel> GetDiseaseLogByBatch(int batchid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " BatchID=" + batchid);
        String txt = exeRetString(MethodFile, "GetDiseaseLog", params);
        return parse(txt);
    }
    
    public int Save(ERPDiseaseLogModel m) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + m._id);
        params.put("BatchID", "" + m._batchid);
        params.put("Time", ""+ (int) (m._time.getTime()/1000));
        params.put("Symptom", "" + m._symptom);
        params.put("Disease", "" + m._disease);
        params.put("resolve", "" + m._resolve);
        String txt = exeRetString(MethodFile, "Save", params);
        return saveParse(txt);
    }
    
    private List<ERPDiseaseLogModel> parse(String str) {
        List<ERPDiseaseLogModel> models = new ArrayList<ERPDiseaseLogModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPDiseaseLogModel cType = new ERPDiseaseLogModel();
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
                    } else if (nodeName.equals("Time")){
                        cType._time = new Timestamp((long) Integer.parseInt(valueStr) * 1000);//TODO
                    } else if (nodeName.equals("Symptom")){
                        cType._symptom = valueStr;
                    } else if (nodeName.equals("Disease")){
                        cType._disease = valueStr;
                    } else if (nodeName.equals("Resolve")){
                        cType._resolve = valueStr;
                    } else if (nodeName.equals("BatchName")){
                    	cType._batchname = valueStr;
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPDiseaseLogModel m : models){
            Log.v(tag, "------id:" +m._id + " 病log:" + m._disease);
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
