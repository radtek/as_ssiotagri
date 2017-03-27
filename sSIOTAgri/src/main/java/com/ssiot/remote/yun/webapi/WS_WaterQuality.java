package com.ssiot.remote.yun.webapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.ssiot.remote.data.model.WaterDetailModel;
import com.ssiot.remote.data.model.view.SensorThresholdModel;

public class WS_WaterQuality extends WS_Fish{//在哪个接口里？？ TODO
	private static final String tag = "WS_Fish_waterquality";
	private String MethodFile = "FisherService.asmx";
	
	public List<WaterDetailModel> GetWaterStandardDetailByFacility(int facilityid){//返回了简单几个字段
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("facilityid", ""+facilityid);
        String txt = exeRetString(MethodFile, "GetWaterStandardDetailByFacility", params);//不存在结果时接口会报错，但不影响我使用
        List<WaterDetailModel> list = new ArrayList<WaterDetailModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				WaterDetailModel m = new WaterDetailModel();
				m._id = jo.getInt("id");
				m._sentypeid = jo.getInt("sen");
				m._upperorlower = "true".equalsIgnoreCase(jo.getString("ul"));
				m._warnvalue = (float) jo.getDouble("wv");
				m._alertvalue = (float) jo.getDouble("av");
				list.add(m);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
        return list;
	}
	
	public List<WaterDetailModel> GetWaterStandardDetail(String where){
		//WaterStandardID in (select WaterStandardID from iot2014.dbo.iot_FisheriesFacilities where FacilitiesID=11)
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("where ", ""+where);
        String txt = exeRetString(MethodFile, "GetWaterStandardDetail", params);
        List<WaterDetailModel> list = new ArrayList<WaterDetailModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				WaterDetailModel m = new WaterDetailModel();
				m._id = jo.getInt("ID");
				m._waterstandardid = jo.getInt("WaterStandardID");
				m._sentypeid = jo.getInt("SensorTypeID");
				m._upperorlower = "true".equalsIgnoreCase(jo.getString("UpperOrLowerLimit"));
				m._warnvalue = (float) jo.getDouble("WarnValue");
				m._alertvalue = (float) jo.getDouble("AlertValue");
				m._causereason = jo.getString("CauseReason");
				m._resulttext = jo.getString("ResultText");
				m._resolve = jo.getString("Resolve");
				m._medicineIds = jo.getString("MedicineIDList");
				list.add(m);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
        return list;
	}
	
	public List<SensorThresholdModel> getSensorThresholdInfo(List<WaterDetailModel> list){
		List<SensorThresholdModel> threadsholds = new ArrayList<SensorThresholdModel>();
		if (null != list && list.size() > 0){
			for (int i = 0; i < list.size(); i ++){
				WaterDetailModel waterDetail = list.get(i);
				int sen = waterDetail._sentypeid;
				boolean exist = false;
				for (SensorThresholdModel s : threadsholds){
					if (s.mDeviceTypeNo == sen){
						exist = true;
						s.thresholdType = 3;
						if (waterDetail._upperorlower){
							s.upperwarnvalue = waterDetail._warnvalue;
							s.upperalertvalue = waterDetail._alertvalue;
						} else {
							s.lowerwarnvalue = waterDetail._warnvalue;
							s.loweralertvalue = waterDetail._alertvalue;
						}
						break;
					}
				}
				if (!exist){
					SensorThresholdModel m = new SensorThresholdModel(sen);
					m.thresholdType = waterDetail._upperorlower ? 1 : 2;
					if (waterDetail._upperorlower){
						m.upperwarnvalue = waterDetail._warnvalue;
						m.upperalertvalue = waterDetail._alertvalue;
					} else {
						m.lowerwarnvalue = waterDetail._warnvalue;
						m.loweralertvalue = waterDetail._alertvalue;
					}
					threadsholds.add(m);
				}
			}
		}
		return threadsholds;
	}
	
//	public WaterDetailModel GetWaterDetailBySensor_test(int facilityid, int sen, boolean upperorlower){//本水质一个传感器
//		WaterDetailModel model = null;
//		String cmd = "select * from iot2014.dbo.WaterStandardDetail where SensorTypeID=" + sen + " and UpperOrLowerLimit=" + (upperorlower? "1" : "0") 
//				+ " and WaterStandardID=(select WaterStandardID from iot2014.dbo.iot_FisheriesFacilities where FacilitiesID=" + facilityid + ")";
//		SsiotResult sr = new DbHelperSQL().Query(cmd);
//		if (null != sr && sr.mRs != null){
//			ResultSet rs = sr.mRs;
//			try {
//				if (rs.next()){
//					model = new WaterDetailModel();
//					model._id = rs.getInt("ID");
//					model._sentypeid = sen;
//					model._upperorlower = upperorlower;
//					model._causereason = rs.getString("CauseReason");
//					model._resulttext = rs.getString("ResultText");
//					model._resolve = rs.getString("Resolve");
//					model._medicineIds = rs.getString("MedicineIDList");
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		if (null != sr){
//			sr.close();
//		}
//		return model;
//	}
	
	public WaterDetailModel GetWaterDetailBySensor(int facilityid, int sen, boolean upperorlower){//本水质一个传感器
		List<WaterDetailModel> list = GetWaterStandardDetail("SensorTypeID=" + sen + " and UpperOrLowerLimit=" + (upperorlower? "1" : "0") 
				+ " and WaterStandardID=(select WaterStandardID from iot2014.dbo.iot_FisheriesFacilities where FacilitiesID=" + facilityid + ")");
		if (null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	private void GetWaterResolveByFacility(int facilityid,String uniqueid){//我分析还是接口分析？
		
	}
}