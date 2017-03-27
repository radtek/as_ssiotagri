package com.ssiot.remote.yun.webapi;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPProductsInModel;
import com.ssiot.remote.data.model.ERPProductsPackModel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;  
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WS_ProductsPack extends WebBasedb2 {
    private static final String tag = "ERPProductsPack";
    private String MethodFile = "ProductsPack.asmx";

    public void Delete() {

    }

    public List<ERPProductsPackModel> GetProductsPack(int mainUserid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "ProductsInID in ( select ID from cms2016.dbo.ERP_ProductsIn where ProductBatchID in " +
        		"(select ID from cms2016.dbo.ERP_ProductBatch where UserID=" + mainUserid+"))");
        String txt = exeRetString(MethodFile, "GetProductsPack", params);
        return parse(txt);
    }
    
    public List<ERPProductsPackModel> GetProductsPackByBatch(int batchid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "ProductsInID in ( select ID from cms2016.dbo.ERP_ProductsIn where ProductBatchID="+batchid+")");
        String txt = exeRetString(MethodFile, "GetProductsPack", params);
        return parse(txt);
    }
    
//    public int Save(ERPProductsPackModel m) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("ID", "" + m._id);
//        params.put("Name", "" + m._name);
//        params.put("ProductsInID", "" + m._productsinid);
//        params.put("PackTypeID", "" + m._packtypeid);
//        params.put("PackUnit", "" + m._packunit);
//        params.put("CreateTime", "" + Utils.formatTime(m._createtime));
//        String txt = exeRetString(MethodFile, "Save", params);
//        return saveParse(txt);
//    }
    
    public int Save(ERPProductsPackModel m, int nodeno,int companyid, String account) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("strName", "" + m._name);
        params.put("intProductsInID", "" + m._productsinid);
        params.put("strPackTypeID", "" + m._packtypeid);
        params.put("strPackUnit", "" + m._packunit);
        params.put("nodeno", "" + nodeno);
        params.put("strImage", "" + m._proimg);
        params.put("ProSupplierID", "" + companyid);
        params.put("strAccount", "" + account);
        String txt = exeRetString(MethodFile, "Save", params);
        return saveParse(txt);
    }
    
    public int Update(int id, String key, String value){
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", "" + id);
        params.put("key", "" + key);
        params.put("value", "" + value);
        String txt = exeRetString(MethodFile, "Update", params);
        return saveParse(txt);
    }
    
    
    private List<ERPProductsPackModel> parse(String str) {
        List<ERPProductsPackModel> models = new ArrayList<ERPProductsPackModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPProductsPackModel cType = new ERPProductsPackModel();
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
                    } else if (nodeName.equals("Name")){
                    	cType._name = valueStr;
                    } else if (nodeName.equals("ProductsInID")) {
                        cType._productsinid = Integer.parseInt(valueStr);
                    } else if (nodeName.equals("PackTypeID")){
                        cType._packtypeid = valueStr;
                    } else if (nodeName.equals("PackUnit")){
                        cType._packunit = valueStr;
                    } else if (nodeName.equals("CreateTime")){
                        if (!TextUtils.isEmpty(valueStr)){
                            cType._createtime = new Timestamp((long) Integer.parseInt(valueStr) * 1000);//Utils.getMyTimestamp(valueStr);
                        }
                    } else if (nodeName.equals("TraceProfileCode")){
                    	cType._traceprofilecode = valueStr;
                    } else if (nodeName.equals("ProDesc")){
                    	cType._prodesc = valueStr;
                    } else if (nodeName.equals("ProComDesc")){
                    	cType._comdesc = valueStr;
                    } else if (nodeName.equals("QRCode")){
                    	cType._qrcode = valueStr;
                    }
                }
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPProductsPackModel m : models){
            Log.v(tag, "------id:" +m._id + " name:" + m._name);
        }
        return models;
    }
    
    private String getProductBatchName(int productsInID){//TODO 暂时这么处理
    	List<ERPProductsInModel> list = new WS_ProductsIn().GetProductsIn("ID=" + productsInID);
    	if (null != list && list.size() > 0){
    		return list.get(0)._ProductBatchName;
    	}
    	return "";
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
