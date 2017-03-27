package com.ssiot.remote.yun.webapi;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPInputsInModel;

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

public class WS_InputsIn extends WebBasedb2{
    
    private static final String tag = "InputsIn";
    private String MethodFile = "InputsIn.asmx";

    public void Delete() {

    }

    public List<ERPInputsInModel> GetInputsIn(int mainUserid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", " UserID=" + mainUserid);
        String txt = exeRetString(MethodFile, "GetInputsIn", params);
        return parse(txt);
    }
    
//    public int Save(ERPInputsInModel m) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("ID", "" + m._id);
//        params.put("UserID", "" + m._userid);
//        params.put("InputsTypeID", "" + m._inputstypeid);
//        params.put("NameDetail", "" + m._namedetail);
//        params.put("Amount", "" + m._amount);
//        params.put("DueTime", Utils.formatTime(m._duetime));
//        params.put("SupplierName", "" + m._suppliername);
//        params.put("RelatedPeopleInfo", "" + m._relatedpeopleinfo);
//        params.put("ProductDate", "" + Utils.formatTime(m._productdate));
//        params.put("Warranty", "" + m._warranty);
//        params.put("WarrantyUnit", "" + m._warrantyunit);
//        params.put("AmountUnit", "" + m._amountunit);
//        params.put("PerAmount", "" + m._peramount);
//        params.put("PerAmountUnit", "" + m._peramountunit);
//        String txt = exeRetString(MethodFile, "Save", params);
//        return saveParse(txt);
//    }
    
    public int SaveAndAddWarehouse(ERPInputsInModel m, String amountunit, float peramount, String peramountunit){
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("userid", "" + m._userid);
        params.put("inputstypeid", "" + m._inputstypeid);
        params.put("namedetial", "" + m._namedetail);
        params.put("amount", "" + m._amount);
        params.put("duetime", ""+m._duetime.getTime()/1000);//Utils.formatTime(m._duetime)
        params.put("suppliername", "" + m._suppliername);
        params.put("people", "" + m._relatedpeopleinfo);
        params.put("productdate", "" + m._productdate.getTime()/1000);//Utils.formatTime(m._productdate)
        params.put("warranty", "" + m._warranty);
        params.put("warranyunit", "" + m._warrantyunit);
        params.put("amountunit", "" + amountunit);//直接传人
        params.put("peramount", "" + peramount);
        params.put("peramountunit", "" + peramountunit);
        String txt = exeRetString(MethodFile, "SaveAndAddWarehouse", params);
        return saveParse(txt);
    }
    
    public int SaveAndUpdateWarehouse(ERPInputsInModel m){
    	HashMap<String, String> params = new HashMap<String, String>();
//        params.put("userid", "" + m._userid);
//        params.put("inputstypeid", "" + m._inputstypeid);
//        params.put("namedetial", "" + m._namedetail);
        params.put("amount", "" + m._amount);
        params.put("duetime", Utils.formatTime(m._duetime));
        params.put("suppliername", "" + m._suppliername);
        params.put("people", "" + m._relatedpeopleinfo);
        params.put("productdate", "" + Utils.formatTime(m._productdate));
        params.put("warranty", "" + m._warranty);
        params.put("warranyunit", "" + m._warrantyunit);
        params.put("warehousid", "" + m._warehouseid);
        String txt = exeRetString(MethodFile, "SaveAndUpdateWarehouse", params);
        return saveParse(txt);
    }
    
    
    private List<ERPInputsInModel> parse(String str) {
        List<ERPInputsInModel> models = new ArrayList<ERPInputsInModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPInputsInModel cType = new ERPInputsInModel();
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
                    } else if (nodeName.equals("DueTime")){
                        cType._duetime = new Timestamp((long) Integer.parseInt(valueStr) * 1000);//Utils.getMyTimestamp(valueStr);
                    } else if (nodeName.equals("SupplierName")){
                        cType._suppliername = valueStr;
                    } else if (nodeName.equals("RelatedPeopleInfo")){
                        cType._relatedpeopleinfo = valueStr;
                    } else if (nodeName.equals("ProductDate")){
                        cType._productdate = new Timestamp((long) Integer.parseInt(valueStr) * 1000);//Utils.getMyTimestamp(valueStr);
                    } else if (nodeName.equals("Warranty")){
                        cType._warranty = Float.parseFloat(valueStr);
                    } else if (nodeName.equals("WarrantyUnit")){
                        cType._warrantyunit = valueStr;
                    }
//                    else if (nodeName.equals("AmountUnit")){
//                        cType._amountunit = valueStr;
//                    } else if (nodeName.equals("PerAmount")){
//                        cType._peramount = Float.parseFloat(valueStr);
//                    } else if (nodeName.equals("PerAmountUnit")){
//                        cType._peramountunit = valueStr;
//                    }  
                    else if (nodeName.equals("WarehouseID")){
                        cType._warehouseid = Integer.parseInt(valueStr);
                    } 
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPInputsInModel m : models){
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
