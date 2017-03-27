package com.ssiot.remote.yun.webapi;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPProductBatchModel;
import com.ssiot.remote.data.model.ERPProductsInModel;

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

public class WS_ProductsIn extends WebBasedb2 {
    private static final String tag = "ERPProductsIn";
    private String MethodFile = "ProductsIn.asmx";

    public void Delete() {

    }

    public List<ERPProductsInModel> GetProductsIn(int mainUserid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "  ProductBatchID in ( select ID from cms2016.dbo.ERP_ProductBatch where UserID=" + mainUserid + ")");
        String txt = exeRetString(MethodFile, "GetProductsIn", params);
        return parse(txt);
    }
    
    public List<ERPProductsInModel> GetProductsIn(String where) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", where);
        String txt = exeRetString(MethodFile, "GetProductsIn", params);
        return parse(txt);
    }
    
    public int Save(ERPProductsInModel m) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + m._id);
        params.put("ProductBatchID", "" + m._productbatchid);
        params.put("FacilityIDs", "" + m._facilityids);
        params.put("NodeIDs", "" + m._nodeids);
        params.put("CreateTime", "" + (int) (m._createtime.getTime()/1000));
        params.put("Amount", "" + m._amount);
        String txt = exeRetString(MethodFile, "Save", params);
        return saveParse(txt);
    }
    
    
    private List<ERPProductsInModel> parse(String str) {
        List<ERPProductsInModel> models = new ArrayList<ERPProductsInModel>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 取得DocumentBuilderFactory实例
            DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
            InputStream is = new ByteArrayInputStream(str.getBytes());
            Document doc = builder.parse(is);// 解析输入流 得到Document实例
            Element rootElement = doc.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("ds");
            for (int i = 0; i < items.getLength(); i++) {
                ERPProductsInModel cType = new ERPProductsInModel();
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
                    } else if (nodeName.equals("FacilityIDs")){
                        cType._facilityids = valueStr;
                    } else if (nodeName.equals("NodeIDs")){
                        cType._nodeids = valueStr;
                    } else if (nodeName.equals("CreateTime")){
                        if (!TextUtils.isEmpty(valueStr)){
                            cType._createtime = new Timestamp((long) Integer.parseInt(valueStr) * 1000);//Utils.getMyTimestamp(valueStr);
                        }
                    } else if (nodeName.equals("Amount")){
                        cType._amount = Float.parseFloat(valueStr);
                    }
                }
                cType._ProductBatchName = getProductBatchName(cType._productbatchid);
                models.add(cType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (ERPProductsInModel m : models){
            Log.v(tag, "------id:" +m._id + " batchid:" + m._productbatchid);
        }
        return models;
    }
    
    private String getProductBatchName(int productBatchID){//TODO 暂时这么处理
    	List<ERPProductBatchModel> list = new ProductBatch().GetProductBatch("ID=" + productBatchID);
    	if (null != list && list.size() > 0){
    		return list.get(0)._name;
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
