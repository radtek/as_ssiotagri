package com.ssiot.remote.yun.webapi;

import com.ssiot.remote.data.model.CompanyModel;
import com.ssiot.remote.data.model.ERPProductsPackModel;
import com.ssiot.remote.data.model.IOTCompanyModel;
import com.ssiot.remote.data.model.NodeModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WS_TraceProject extends WebBasedb2 {//TODO
    private static final String tag = "WS_TraceProject";
    private String MethodFile = "TraceProject.asmx";

    public void Delete() {

    }

//    public List<PlantCropTypesModel> GetProCropTypes(String account) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("strAccount", account);
//        String txt = exeRetString(MethodFile, "GetProCropTypes", params);
//        return parseCropTypes(txt);
//    }
    
    public List<CompanyModel> GetAllCompanys(String account){
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("strAccount", account);
        String txt = exeRetString(MethodFile, "GetAllCompanys", params);
        return parseCompanys(txt);
    }
    
    public List<IOTCompanyModel> GetCompanys(int pageindex,int pagesize, String where){
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("pageindex", ""+pageindex);
        params.put("pagesize", ""+pagesize);
        params.put("where", where);
        String txt = exeRetString(MethodFile, "GetCompanys", params);
        List<IOTCompanyModel> list = new ArrayList<IOTCompanyModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				IOTCompanyModel m = new IOTCompanyModel();
				m._companyid = jo.getInt("CompanyID");
				m._name = jo.getString("Name");
				m._url = jo.getString("Url");
				m._parentid = jo.getInt("ParentID");
				m._createTime = new Timestamp((long) jo.getInt("CreateTime") * 1000);
				m._modifyTime = new Timestamp((long) jo.getInt("ModifyTime") * 1000);
				m._isdelete = "true".equalsIgnoreCase(jo.getString("IsDelete"));
				m._companyContent = jo.getString("CompanyContent");
				m._remark = jo.getString("Remark");
				list.add(m);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return list;
    }
    
    public List<NodeModel> GetAllNodes(String account){
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("strAccount", account);
        String txt = exeRetString(MethodFile, "GetAllNodes", params);
        return parseNodes(txt);
    }
    
    public int ProductsPackSave(ERPProductsPackModel packModel, String img, int companyid, String account, int nodeno){
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("strName", packModel._name);//例如海安黄金米
        params.put("intProductsInID", ""+packModel._productsinid);//采收id，由此可查出BatchID
        params.put("strPackTypeID", packModel._packtypeid);//袋，箱，盒
        params.put("strPackUnit", packModel._packunit);//10斤  ，单盒重量
        params.put("nodeno", "" + nodeno);//节点
        params.put("intCropTypeID", "0");//TODO原先溯源种类表和这边的表不同！，现在不需要了，从采收id-批次可以查出种类id
        params.put("strImage", img);//主图
        params.put("ProSupplierID", ""+companyid);//选择本帐号在公司表里的一个公司
        params.put("strAccount", account);
        params.put("ProVariety", packModel._name);//同上面的名字
        String txt = exeRetString(MethodFile, "ProductsPackSave", params);
        return parseSave(txt);
    }
    
//    private List<PlantCropTypesModel> parseCropTypes(String str) {
//        List<PlantCropTypesModel> models = new ArrayList<PlantCropTypesModel>();
//        try {
//			JSONArray jArray = new JSONArray(str);
//			for (int i = 0; i < jArray.length(); i ++){
//				JSONObject jo = jArray.optJSONObject(i);
//				
//				JSONArray childArray = jo.optJSONArray("child");
//				for (int j = 0; j < childArray.length(); j ++){
//					JSONObject joChild = childArray.optJSONObject(j);
//					PlantCropTypesModel m = new PlantCropTypesModel();
//					m._parentid = jo.getInt("croptypeid");
//					m._parentName = jo.getString("croptypename");
//					m._id = joChild.getInt("croptypeid");
//					m._name = joChild.getString("croptypename");
//					models.add(m);
//				}
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//        return models;
//    }
    
    private List<CompanyModel> parseCompanys(String str) {
        List<CompanyModel> models = new ArrayList<CompanyModel>();
        try {
			JSONArray jArray = new JSONArray(str);
			for (int i = 0; i < jArray.length(); i ++){
				JSONObject jo = jArray.optJSONObject(i);
				CompanyModel m = new CompanyModel();
				m._id = jo.getInt("companyID");
				m._name = jo.getString("name");
				models.add(m);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return models;
    }
    
    private List<NodeModel> parseNodes(String str) {
        List<NodeModel> models = new ArrayList<NodeModel>();
        try {
			JSONArray jArray = new JSONArray(str);
			for (int i = 0; i < jArray.length(); i ++){
				JSONObject jo = jArray.optJSONObject(i);
				NodeModel m = new NodeModel();
//				m._nodeid = jo.getInt("nodeid");
				m._uniqueid = jo.getString("uniqueID");
				m._nodeno = jo.getInt("nodeno");
				m._location = jo.getString("nodename");
				models.add(m);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return models;
    }
}
