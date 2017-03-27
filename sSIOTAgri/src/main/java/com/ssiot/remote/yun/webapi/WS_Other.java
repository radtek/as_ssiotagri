package com.ssiot.remote.yun.webapi;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.ssiot.remote.data.model.ArticleModel;
import com.ssiot.remote.data.model.ArticleTypeModel;
import com.ssiot.remote.data.model.FishTypeModel;

public class WS_Other extends WebBasedb2{
	private static final String tag = "WS_Other";
	
	private String MethodFile = "Article.asmx";//"SensorProject.asmx";
	
	public List<FishTypeModel> GetAllFishTypes(String account){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("account", "" + account);
        String txt = exeRetString(MethodFile, "GetAllFishTypes", params);
        return null;
	}
	
	public List<ArticleTypeModel> GetAllFishArticleTypes (){
		HashMap<String, String> params = new HashMap<String, String>();
        String txt = exeRetString(MethodFile, "GetAllFishArticleTypes", params);
        List<ArticleTypeModel> list = new ArrayList<ArticleTypeModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				ArticleTypeModel atM = new ArticleTypeModel();
				atM._id = jo.getInt("ID");
				atM._name = jo.getString("Name");
				atM._code = jo.getString("Code");
				atM._parentcode = jo.getString("ParentCode");
				list.add(atM);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return list;
		
		
//		String cmd = "select * from iot2014.dbo.biz_ArticleType where UserId=226";
//		SsiotResult sr = DbHelperSQL.getInstance().Query(cmd);
//		List<ArticleTypeModel> list = new ArrayList<ArticleTypeModel>();
//		if (null != sr && sr.mRs != null){
//			ResultSet rs = sr.mRs;
//			try {
//				while(rs.next()){
//					ArticleTypeModel m = new ArticleTypeModel();
//					m._id = rs.getInt("ID");
//					m._parentcode = rs.getString("ParentCode");
//					m._code = rs.getString("Code");
//					m._name = rs.getString("Name");
//					list.add(m);
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		if (null != sr){
//			sr.close();
//		}
	}
	
	public List<ArticleModel> GetArticles(int pageindex, int pagesize, String where){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("pageindex", "" + pageindex);
		params.put("pagesize", "" + pagesize);
		params.put("where", "" + where);
        String txt = exeRetString(MethodFile, "GetArticles", params);
        List<ArticleModel> list = new ArrayList<ArticleModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				ArticleModel a = new ArticleModel();
				a._id = jo.getInt("ID");
				a._type = jo.getString("ArticleTypeCode");
				a._title = jo.getString("Title");
				a._description = jo.getString("Description");
				a._createtime = new Timestamp((long) jo.getInt("CreateTime") * 1000);
				a._image = jo.getString("Image");
//				a._contenttext//这个在另外一张表
				list.add(a);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return list;
		
		
//		String cmd = "select * from iot2014.dbo.biz_Article where UserId=226 and " + where;
//		SsiotResult sr = DbHelperSQL.getInstance().Query(cmd);
//		List<ArticleModel> list = new ArrayList<ArticleModel>();
//		if (null != sr && sr.mRs != null){
//			ResultSet rs = sr.mRs;
//			try {
//				while(rs.next()){
//					ArticleModel m = new ArticleModel();
//					m._id = rs.getInt("ID");
//					m._type = rs.getString("ArticleTypeCode");
//					m._title = rs.getString("Title");
//					m._description = rs.getString("Description");
//					m._image = rs.getString("Image");
//					m._createtime = rs.getTimestamp("CreateTime");
//					m._author = rs.getString("Author");
//					list.add(m);
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		if (null != sr){
//			sr.close();
//		}
//		return list;
	}
	
	public String GetArticleContent(int articleid){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("articleid", "" + articleid);
        String txt = exeRetString(MethodFile, "GetArticleContent", params);
        return txt;
//        try {
//        	txt = txt.replace("\t", "#########");
//        	JSONArray ja = new JSONArray(txt);//TODO 王生辉
//			JSONObject jo = ja.optJSONObject(0);
//			return jo.getString("ContentText");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//        return "";
	}
	
}