package com.ssiot.remote.yun.webapi;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.TextUtils;
import com.ssiot.remote.data.model.AnswerModel;
import com.ssiot.remote.data.model.DiseaseModel;
import com.ssiot.remote.data.model.FishPartModel;
import com.ssiot.remote.data.model.FishTypeModel;
import com.ssiot.remote.data.model.GoodsModel;
import com.ssiot.remote.data.model.ManualAnalysisModel;
import com.ssiot.remote.data.model.ManualDataModel;
import com.ssiot.remote.data.model.QuestionModel;
import com.ssiot.remote.data.model.SensorModel;
import com.ssiot.remote.data.model.SymptomModel;
import com.ssiot.remote.data.model.WaterColorDiagnoseModel;

public class WS_Fish extends WebBaseFish{
	private static final String tag = "WS_Fish";
	
	private String MethodFile = "FisherService.asmx";
	
	public List<FishTypeModel> GetAllFishTypes(String account){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("account", "" + account);
        String txt = exeRetString(MethodFile, "GetAllFishTypes", params);
        return parseFishType(txt);
	}
	
	public List<FishTypeModel> GetAllCropTypes(String account){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("account", "" + account);
        String txt = exeRetString(MethodFile, "GetAllCropTypes", params);
        return parseFishType(txt);
	}
	
//	public List<FishTypeModel> GetAllFishTypes_test(String account){
//		List<FishTypeModel> typeModels = new ArrayList<FishTypeModel>();
//		String cmd = "select * from Fish_Type";
//		SsiotResult sr = DbHelperSQLFish.getInstance().Query(cmd);
//		if (null != sr && sr.mRs != null){
//			ResultSet rs = sr.mRs;
//			try {
//				while (rs.next()){
//					FishTypeModel m = new FishTypeModel();
//					m._id = rs.getInt("ID");
//					m._name = rs.getString("FishName");
//					typeModels.add(m);
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		if (null != sr){
//			sr.close();
//		}
//		return typeModels;
//	}
	
	public List<FishPartModel> GetSymptomByType(int fishtype){//获取一种鱼的所有症状现象，（可能数据量太大）,json部位嵌套症状
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("fishtype", "" + fishtype);
        String txt = exeRetString(MethodFile, "GetSymptomByType", params);
        return parsePartSymptom(txt);
	}
	
//	public List<FishPartModel> GetSymptomByType_test(int fishtype){//获取一种鱼的所有症状现象，（可能数据量太大）
//		List<FishPartModel> fpModels = new ArrayList<FishPartModel>();
//		String cmd = "select Fish_Symptom.ID,Fish_Symptom.PartID,Fish_Symptom.SymptomText,fp.PartName from Fish_Symptom left join Fish_PartList as fp " +
//				"on Fish_Symptom.PartID=fp.ID where Fish_Symptom.FishTypeID=" + fishtype;
//		SsiotResult sr = DbHelperSQLFish.getInstance().Query(cmd);
//		if (null != sr && sr.mRs != null){
//			ResultSet rs = sr.mRs;
//			try {
//				while (rs.next()){
//					int partId = rs.getInt("PartID");
//					String partName = rs.getString("PartName");
//					int syId = rs.getInt("ID");
//					String syTxt = rs.getString("SymptomText");
//					boolean found = false;
//					for (FishPartModel pp : fpModels){
//						if (pp._id == partId){
//							SymptomModel syModel = new SymptomModel();
//							syModel._id = syId;
//							syModel._partid = partId;
//							syModel._symptomtxt = syTxt;
//							pp._symptoms.add(syModel);
//							found = true;
//							break;
//						}
//					}
//					if (!found){
//						FishPartModel m = new FishPartModel();
//						m._id = partId;
//						m._name = partName;
//						SymptomModel syModel = new SymptomModel();
//						syModel._id = syId;
//						syModel._partid = partId;
//						syModel._symptomtxt = syTxt;
//						m._symptoms.add(syModel);
//						fpModels.add(m);
//					}
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		if (null != sr){
//			sr.close();
//		}
//		return fpModels;
//	}
	
	public DiseaseModel GetDiease(int id){//诊断
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", "" + id);
        String txt = exeRetString(MethodFile, "GetDiease", params);
        return parseOneDisease(txt);
	}
	
	public List<DiseaseModel> GetDieaseBySymptom(int fishtype,String sylist){//诊断
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("fishtype", "" + fishtype);
        params.put("sylist", "" + sylist);
        String txt = exeRetString(MethodFile, "GetDieaseBySymptom", params);
        return parseDisease(txt);
	}
	
//	public List<DiseaseModel> GetDieaseBySymptom_test(int fishtype,String sylist){
//		List<DiseaseModel> diseases = new ArrayList<DiseaseModel>();
//		if (!TextUtils.isEmpty(sylist)){
//			String cmd = "select * from (select * from Fish_Diagnose where SymptomID in ("+sylist+"))T " +
//					"left join Fish_Disease on T.DiseaseID=Fish_Disease.ID " +
//					"order by T.DiseaseID ASC";
//			SsiotResult sr = DbHelperSQLFish.getInstance().Query(cmd);
//			if (sr != null && sr.mRs != null){
//				ResultSet rs = sr.mRs;
//				try {
//					while(rs.next()){
//						int id = rs.getInt("DiseaseID");
//						boolean exists = false;
//						for (DiseaseModel d : diseases){
//							if (d._id == id){
//								d._probability += rs.getInt("Probability");
//								exists = true;
//								break;
//							}
//						}
//						if (!exists){
//							DiseaseModel dModel = new DiseaseModel();
//							dModel._id = id;
//							dModel._probability = rs.getInt("Probability");
//							dModel._name = rs.getString("DiseaseName");
//							dModel._causereason = rs.getString("CauseReason");
//							dModel._resulttext = rs.getString("ResultText");
//							dModel._resolve = rs.getString("Resolve");
//							dModel._medicineIds = rs.getString("MedicineIDList");
//							diseases.add(dModel);
//						}
//					}
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//			if (null != sr){
//				sr.close();
//			}
//		}
//		return diseases;
//	}
	
//	public List<GoodsModel> GetMultiGoods_test(String idlist){
//		List<GoodsModel> goodsModels = new ArrayList<GoodsModel>();
//		if (!TextUtils.isEmpty(idlist)){
//			String cmd = "select * from Fish_Goods where id in (" + idlist + ")";
//			SsiotResult sr = DbHelperSQLFish.getInstance().Query(cmd);
//			if (null != sr && sr.mRs != null){
//				ResultSet rs = sr.mRs;
//				try {
//					while(rs.next()){
//						GoodsModel m = new GoodsModel();
//						m._id = rs.getInt("ID");
//						m._name = rs.getString("GoodsName");
//						m._detail = rs.getString("Detail");
//						m._price = rs.getFloat("Price");
//						m._img = rs.getString("Image");
//						goodsModels.add(m);
//					}
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//			if (null != sr){
//				sr.close();
//			}
//		}
//		return goodsModels;
//	}
	
	public List<GoodsModel> GetMultiGoods(String idlist){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("pageindex", "1");
		params.put("pagesize", "100");
        params.put("where", "id in (" + idlist + ")");
        String txt = exeRetString(MethodFile, "GetProductGoods", params);
        List<GoodsModel> list = new ArrayList<GoodsModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				GoodsModel g = new GoodsModel();
				g._id = jo.getInt("ID");
				g._code = jo.getString("Code");
				g._name = jo.getString("Title");
				g._detail = jo.getString("ContentText");
				g._price = (float)jo.getDouble("Price");
				g._img = jo.getString("Image");
				list.add(g);
			}
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
        return list;
	}
	
	public List<WaterColorDiagnoseModel> GetAllWaterColors(){//只返回了少量，！！
		HashMap<String, String> params = new HashMap<String, String>();
        String txt = exeRetString(MethodFile, "GetAllWaterColors", params);
        List<WaterColorDiagnoseModel> models = new ArrayList<WaterColorDiagnoseModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				WaterColorDiagnoseModel m = new WaterColorDiagnoseModel();
				m._id = jo.getInt("id");
				m._name = jo.getString("name");
				m._img = jo.getString("img");
				models.add(m);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return models;
	}
	
	public WaterColorDiagnoseModel GetWaterColorResolve(int id){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", ""+id);
        String txt = exeRetString(MethodFile, "GetWaterColorResolve", params);
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				WaterColorDiagnoseModel m = new WaterColorDiagnoseModel();
				m._id = jo.getInt("id");
				m._name = jo.getString("name");
				m._img = jo.getString("img");
				m._causereason = jo.getString("cause");
				m._resulttext = jo.getString("result");
				m._resolve = jo.getString("resolv");
				JSONArray goodsArray = jo.getJSONArray("goods");
				String goodsStr = "";
				for (int j = 0; j < goodsArray.length(); j ++){
					JSONObject jGood = goodsArray.optJSONObject(j);
					goodsStr += jGood.getInt("id") + ",";
				}
				if (goodsStr.endsWith(",")){
					goodsStr = goodsStr.substring(0, goodsStr.length() - 1);
				}
				m._medicineIds = goodsStr;
				return m;
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public List<SensorModel> GetManualSensor(){//
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "ManualEnable=1");
        String txt = exeRetString(MethodFile, "GetSensor", params);
        List<SensorModel> senModels = new ArrayList<SensorModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				SensorModel s = new SensorModel();
				s._sensorno = jo.getInt("SensorNo");
				s._sensorname = jo.getString("SensorName");
				s._shortname = jo.getString("ShortName");
				senModels.add(s);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return senModels;
	}
	
	public List<SensorModel> GetSensor(String where){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "1=1");
        String txt = exeRetString(MethodFile, "GetSensor", params);
        List<SensorModel> senModels = new ArrayList<SensorModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				SensorModel s = new SensorModel();
				s._sensorno = jo.getInt("SensorNo");
				s._sensorname = jo.getString("SensorName");
				s._shortname = jo.getString("ShortName");
				s._unit = jo.getString("Unit");
				senModels.add(s);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return senModels;
	}
	
	public List<QuestionModel> GetQuestions(int pageindex, int pagesize, int questiontype){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("pageindex", "" + pageindex);
        params.put("pagesize", "" + pagesize);
        params.put("QuestionType", "" + questiontype);
        String txt = exeRetString(MethodFile, "GetQuestions", params);
        List<QuestionModel> list = new ArrayList<QuestionModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				QuestionModel q = new QuestionModel();
				q._id = jo.getInt("ID");
				q._userId = jo.getInt("UserID");
				q._title = jo.getString("Title");
				q._contentText = jo.getString("ContentText");
				q._createTime = new Timestamp((long) jo.getInt("CreateTime") * 1000);
				q._picUrls = jo.getString("PicUrls");
				q._addr = jo.getString("Addr");
				q._longitude = (float)jo.getDouble("LongiTude");
				q._latitude = (float)jo.getDouble("Latitude");
				q._replyCount = jo.getInt("ReplyCount");
				q._type = jo.getInt("QuestionType");
				q._username = jo.getString("UserName");
				list.add(q);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public List<AnswerModel> GetAnswers(int questionid){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("questionid", "" + questionid);
        String txt = exeRetString(MethodFile, "GetAnswers", params);
        List<AnswerModel> list = new ArrayList<AnswerModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				AnswerModel a = new AnswerModel();
				a._id = jo.getInt("ID");
				a._questionid = jo.getInt("QuestionID");
				a._userid = jo.getInt("UserID");
				a._username = jo.getString("UserName");
				a._addr = jo.getString("Addr");
				a._userpicurl = jo.getString("UserPicUrl");
				a._createTime = new Timestamp((long) jo.getInt("CreateTime") * 1000);
				a._contenttext = jo.getString("ContentText");
				list.add(a);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public int SaveQuestion(QuestionModel q){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + q._id);
        params.put("UserID", "" +q._userId);
        params.put("Title", "" + q._title);
        params.put("ContentText", "" + q._contentText);
        params.put("CreateTime", "" + (int) (q._createTime.getTime()/1000));
        params.put("PicUrls", "" + q._picUrls);
        params.put("Addr", "" + q._addr);
        params.put("LongiTude", "" + q._longitude);
        params.put("Latitude", "" + q._latitude);
        params.put("ReplyCount", "" + q._replyCount);
        params.put("QuestionType", "" + q._type);	
        String txt = exeRetString(MethodFile, "SaveQuestion", params);
        return parseSave(txt);
	}
	
	public int SaveAnswer(AnswerModel a){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("ID", "" + a._id);
        params.put("QuestionID", "" + a._questionid);
        params.put("UserID", "" +a._userid);
        params.put("UserName", "" +a._username);
        params.put("Addr", "" + a._addr);
        params.put("UserPicUrl", "" + a._userpicurl);
        params.put("ContentText", "" + a._contenttext);
        params.put("CreateTime", "" + (int) (a._createTime.getTime()/1000));
        String txt = exeRetString(MethodFile, "SaveAnswer", params);
        return parseSave(txt);
	}
	
	public Timestamp GetManualDataLastTime(int facilityid){//
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("where", "ID=(select top 1 ID from iot2014.dbo.iot_ManualData where FacilityID="+facilityid+" order by CollectionTime desc)");
        String txt = exeRetString(MethodFile, "GetManualData", params);
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				return new Timestamp(((long) jo.getInt("CollectionTime")) * 1000);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public int SaveManualData(ManualDataModel m){//
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", ""+m._id);
        params.put("collectiontime", ""+(int)(m._collectiontime.getTime()/1000));
        params.put("facilityid", "" + m._facilityid);
        params.put("facilitytype", "渔业");//TODO
        params.put("sensorno", ""+m._sensorno);
        params.put("data", ""+m._data);
        String txt = exeRetString(MethodFile, "SaveManualData", params);
        return parseSave(txt);
	}
	
//	public int DelManualData(int id){
//		
//	}
	
	public int SaveWaterManualAnalysis(ManualAnalysisModel m){//
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", ""+m._id);
        params.put("collectiontime", ""+(int)(m._collectiontime.getTime()/1000));
        params.put("facilityid", "" + m._facilityid);
        params.put("facilitytype", "渔业");//TODO
        params.put("analysis", ""+m._analysis);
        String txt = exeRetString(MethodFile, "SaveWaterManualAnalysis", params);
        return parseSave(txt);
	}
	
	private List<FishTypeModel> parseFishType(String txt){
		List<FishTypeModel> models = new ArrayList<FishTypeModel>();
		if (TextUtils.isEmpty(txt)){
			return models;
		}
		try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				FishTypeModel m = new FishTypeModel();
				m._id = jo.getInt("id");
				m._name = jo.getString("name");
				models.add(m);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return models;
	}
	
	private List<FishPartModel> parsePartSymptom(String txt){
		List<FishPartModel> models = new ArrayList<FishPartModel>();
		if (TextUtils.isEmpty(txt)){
			return models;
		}
		try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				FishPartModel m = new FishPartModel();
				m._id = jo.getInt("partid");
				m._name = jo.getString("partname");
				JSONArray symptomArray = jo.getJSONArray("ss");
				for (int j = 0; j < symptomArray.length(); j ++){
					SymptomModel syModel = new SymptomModel();
					JSONObject jSy = symptomArray.getJSONObject(j);
					syModel._id = jSy.getInt("id");
					syModel._symptomtxt = jSy.getString("sy");
					syModel._partid = m._id;
					m._symptoms.add(syModel);
				}
				models.add(m);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return models;
	}
	
	private List<DiseaseModel> parseDisease(String txt){
		List<DiseaseModel> models = new ArrayList<DiseaseModel>();
		if (TextUtils.isEmpty(txt)){
			return models;
		}
		try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				DiseaseModel m = new DiseaseModel();
				m._id = jo.getInt("id");
				m._name = jo.getString("name");
				m._probability = jo.getInt("pro");//概率
				models.add(m);//此函数只返回这几个字段
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return models;
	}
	
	private DiseaseModel parseOneDisease(String txt){
		try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				DiseaseModel m = new DiseaseModel();
				m._id = jo.getInt("id");
				m._name = jo.getString("name");
				m._fishname = jo.getString("fishtype");
//				m._probability = jo.getInt("pro");//概率
				m._causereason = jo.getString("cause");
				m._resulttext = jo.getString("result");
				m._resolve = jo.getString("resolve");
				JSONArray goods = jo.getJSONArray("goods");
				String str = "";
				for (int j = 0; j < goods.length(); j ++){
					str += goods.optJSONObject(j).getInt("id") + ",";
				}
				if (str.endsWith(",")){
					str = str.substring(0, str.length() - 1);
				}
				m._medicineIds = str;
				return m;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}