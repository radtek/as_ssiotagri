package com.ssiot.remote.yun.webapi;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.TextUtils;
import android.util.Log;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.AgricultureFacilityModel;
import com.ssiot.remote.data.model.AlarmRuleModel;
import com.ssiot.remote.data.model.CameraFileModel;
import com.ssiot.remote.data.model.ControlLogModel;
import com.ssiot.remote.data.model.SensorModel;
import com.ssiot.remote.data.model.SettingModel;
import com.ssiot.remote.data.model.VLCVideoInfoModel;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.unit.XYStringHolder;

public class WS_API extends WebBasedb2{
	private static final String tag = "WS_API";
	
	private String MethodFile = "SensorProject.asmx";
	
	public List<YunNodeModel> GetFirstPage(String account, int deviceVersion){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("strAccount", "" + account);
        String txt = exeRetString(MethodFile, "GetFirstPage", params);
        return parseYNode(txt, deviceVersion);
	}
	
	public List<YunNodeModel> GetFirstPageShort(String account, int deviceVersion){//王桂华添加的,水质分析界面
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("strAccount", "" + account);
        String txt = exeRetString(MethodFile, "GetFirstPageShort", params);
        return parseFacilityNode_short(txt, deviceVersion);
	}
	
	public void GetSensorNodeDatas_v2(String nodeunique, List<YunNodeModel> ylist){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("UniqueID", "" + nodeunique);
        String txt = exeRetString(MethodFile, "GetSensorNodeDatas_v2", params);
        parseLatestDataToModel(txt,nodeunique, ylist);
	}
	
	public void GetSensorNodeDatasOneNode_v2(YunNodeModel yModel){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("UniqueID", "" + yModel.mNodeUnique);
        String txt = exeRetString(MethodFile, "GetSensorNodeDatas_v2", params);
        JSONArray jArray;
		try {
			jArray = new JSONArray(txt);
			for (int i = 0; i < jArray.length(); i ++){
				JSONObject jo = jArray.optJSONObject(i);
				if (null != yModel){
					int sen = jo.getInt("sendtype");//笔误
					int channel = jo.getInt("chan");
					for (int k = 0; k < yModel.list.size(); k ++){
						if (yModel.list.get(k).mDeviceTypeNo == sen && yModel.list.get(k).mChannel == channel){
							double v = jo.getDouble("v");
							int time = jo.getInt("t");
							yModel.list.get(k).value = (float) v;
							yModel.list.get(k).valueStr = jo.getString("v");//值 的字符串
							yModel.list.get(k).mTime = new Timestamp((long) time * 1000);
							break;
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<XYStringHolder> GetSensorHisData(String nodeunique, int startTime, int endTime,int tableIndex, int deviceTypeNo,int channel){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("uniqueid", "" + nodeunique);
        params.put("starttime", "" + startTime);
        params.put("endtime", "" + endTime);
        params.put("tableindex", "" + tableIndex);
        params.put("devicetypeno", "" + deviceTypeNo);
        params.put("channel", "" + channel);
        String txt = exeRetString(MethodFile, "GetSensorHisData", params);
        return parseHisData(txt, deviceTypeNo);
	}
	
	public List<XYStringHolder> GetSensorHisData_His(String nodeunique, int startTime, int endTime,int tableIndex, int deviceTypeNo,int channel){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("uniqueid", "" + nodeunique);
        params.put("starttime", "" + startTime);
        params.put("endtime", "" + endTime);
        params.put("tableindex", "" + tableIndex);
        params.put("devicetypeno", "" + deviceTypeNo);
        params.put("channel", "" + channel);
        String txt = exeRetString(MethodFile, "GetSensorHisData_His", params);
        return parseHisData(txt, deviceTypeNo);
	}
	
	public List<SensorModel> GetAllSensors(){//目地是获取所有传感器的单位unit
		HashMap<String, String> params = new HashMap<String, String>();
        String txt = exeRetString(MethodFile, "GetAllSensors", params);
        List<SensorModel> list = new ArrayList<SensorModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				SensorModel s = new SensorModel();
				JSONObject jo = ja.optJSONObject(i);
				s._sensorno = jo.getInt("sensorno");
				s._unit = jo.getString("unit");
				s._accuracy = parseIntAnyWay(jo.getString("accuracy"));
				list.add(s);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public AlarmRuleModel GetAlarmRules_v2(String nodeUnique){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeunique", "" + nodeUnique);
        String txt = exeRetString(MethodFile, "GetAlarmRules_v2", params);
        return parseAlarmRule_v2(nodeUnique, txt);
	}
	
	public HashMap<String, String> GetAlarming_v2(String account){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("account", "" + account);
        String txt = exeRetString(MethodFile, "GetAlarming_v2", params);
        return parseAlarming_v2(txt);
	}
	
	public int SaveAlarmRule(AlarmRuleModel alarmRuleModel){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", "" + alarmRuleModel._id);
        params.put("NodeUniqueID", "" + alarmRuleModel._uniqueID);
        params.put("Relation", alarmRuleModel._relation);
        params.put("RuleStr", alarmRuleModel._ruleStr);
        String str = exeRetString(MethodFile, "SaveAlarmRule_v2", params);
        return parseSave(str);
	}
	
	public String GetSensorModifyData(int sensorno){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("sensorno", "" + sensorno);
        String txt = exeRetString(MethodFile, "GetSensorMidifyData", params);
        return txt;
	}
	
	public List<SettingModel> GetCali_v2(String nodeunique,int deviceno, int chan){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeunique", "" + nodeunique);
        params.put("devceno", "" + deviceno);
        params.put("chan", "" + chan);
        String txt = exeRetString(MethodFile, "GetCali_v2", params);
        List<SettingModel> list  = new ArrayList<SettingModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				SettingModel s = new SettingModel();
				s._type = jo.getInt("type");
				s._value = (float) jo.getDouble("value");
				list.add(s);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public int SetCali_v2(String nodeunique, int type, int deviceno, int chan, float val, int time){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeunique", "" + nodeunique);
        params.put("type", "" + type);
        params.put("deviceno", "" + deviceno);
        params.put("chan", "" + chan);
        params.put("val", "" + val);
        params.put("time", "" + time);
        String txt = exeRetString(MethodFile, "SetCali_v2", params);
        return parseSave(txt);
	}
	
	public String GetCali_v3(String nodeno, int deviceno, int chan){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeno", "" + nodeno);
        params.put("deviceno", "" + deviceno);
        params.put("chan", "" + chan);
        return exeRetString(MethodFile, "GetCali_v3", params);
        
//		String cmd = " select top 1 * from s30_UserNodeCalibration where UniqueID='" + nodeunique +"' and SensorID=" + deviceno + " and Channel=" + chan;
//		SsiotResult sResult = DbHelperSQL.getInstance().Query(cmd);
//		String caliText = "";
//		if (null != sResult && sResult.mRs != null){
//			ResultSet rs = sResult.mRs;
//			try {
//				while(rs.next()){
//					caliText = rs.getString("CalibrationText");
//					break;
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		if (null != sResult){
//			sResult.close();
//		}
//		return caliText;
	}
	
	public int SetCali_v3(int nodeno, int deviceno, int channel, String address, String calibrationText){
		Log.e(tag, "--------弃用的api------需要使用ws_mqtt");
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeno", "" + nodeno);
        params.put("deviceno", "" + deviceno);
        params.put("channel", ""+channel);
        params.put("address", "" + address);
        params.put("calibrationText", "" + calibrationText);
        String txt = exeRetString(MethodFile, "SetCali_v3", params);
        return parseSave(txt);
	}
	
	public String getSensorAddress(int nodeno, int deviceno, int channel){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeno", "" + nodeno);
        params.put("deviceno", "" + deviceno);
        params.put("channel", "" + channel);
        return exeRetString(MethodFile, "getSensorAddress", params);
        
//		String cmd = "select * from iot2014.dbo.s30_NodeSensor where NodeNo='" + nodeno + "' and SensorTypeID=" + deviceno  + " and Channel=" + channel;
//		SsiotResult sResult = DbHelperSQL.getInstance().Query(cmd);
//		String address = "";
//		if (null != sResult && sResult.mRs != null){
//			ResultSet rs = sResult.mRs;
//			try {
//				while(rs.next()){
//					address = rs.getString("Address");
//					break;
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		if (null != sResult){
//			sResult.close();
//		}
//		return address;
	}
	
	public String getCtrAddress(int nodeno, int deviceno){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeno", "" + nodeno);
        params.put("deviceno", "" + deviceno);
        return exeRetString(MethodFile, "getCtrAddress", params);
//        
//		String cmd = "select * from iot2014.dbo.vw_iot_ControlDeviceList where NodeNo='" + nodeno + "' and DeviceNo=" + deviceno ;
//		SsiotResult sResult = DbHelperSQL.getInstance().Query(cmd);
//		String address = "";
//		if (null != sResult && sResult.mRs != null){
//			ResultSet rs = sResult.mRs;
//			try {
//				while(rs.next()){
//					address = rs.getString("Address");
//					break;
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		if (null != sResult){
//			sResult.close();
//		}
//		return address;
	}
	
	public VLCVideoInfoModel GetIPCByID(int id){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", "" + id);
        String str = exeRetString(MethodFile, "GetIPCByID", params);
        try {
			JSONObject jo = new JSONObject(str);
			VLCVideoInfoModel v = new VLCVideoInfoModel();
			v._vlcvideoinfoid = jo.getInt("vlcvideoinfoid");
			v._areaid = parseIntAnyWay(jo.getString("areaid"));
			v._areaname = jo.getString("areaname");
			v._username = jo.getString("username");
			v._password = jo.getString("password");
			v._url = jo.getString("url");
			v._ip = jo.getString("ip");
			v._port = jo.getString("port");
			v._address = jo.getString("address");
			v._type = jo.getString("type");
			v._remark = jo.getString("remark");
			v._tcpport = parseIntAnyWay(jo.getString("tcpport"));
			v._serialno = jo.getString("serialno");
			v._facilitiesid = parseIntAnyWay(jo.getString("facilitiesid"));
			v._devicetype = parseIntAnyWay(jo.getString("devicetype"));
			v._verifycode = jo.getString("verifycode");
			v._ssiotezviz = "True".equals(jo.getString("ssiotezviz"));
			return v;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public List<VLCVideoInfoModel> GetAllIPC(int userid){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("userid", "" + userid);
        String txt = exeRetString(MethodFile, "GetAllIPC", params);
        
        List<VLCVideoInfoModel> list = new ArrayList<VLCVideoInfoModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				VLCVideoInfoModel v = new VLCVideoInfoModel();
				v._vlcvideoinfoid = jo.getInt("VLCVideoInfoID");
				v._areaid = parseIntAnyWay(jo.getString("AreaID"));
				v._areaname = jo.getString("AreaName");
				v._username = jo.getString("UserName");
				v._password = jo.getString("PassWord");
				v._url = jo.getString("URL");
				v._ip = jo.getString("IP");
				v._port = jo.getString("Port");
				v._address = jo.getString("Address");
				v._type = jo.getString("Type");
				v._remark = jo.getString("Remark");
				v._tcpport = parseIntAnyWay(jo.getString("TcpPort"));
				v._serialno = jo.getString("SerialNo");
				v._facilitiesid = parseIntAnyWay(jo.getString("FacilitiesID"));
				v._devicetype = parseIntAnyWay(jo.getString("DeviceType"));
				v._verifycode = jo.getString("VerifyCode");
				v._ssiotezviz = "True".equals(jo.getString("SSiotEzviz"));
				list.add(v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public List<CameraFileModel> GetCameraFiles(int top, int cameraid){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("top", "" + top);
        params.put("cameraid", "" + cameraid);
        String txt = exeRetString(MethodFile, "GetCameraFiles", params);
        List<CameraFileModel> list = new ArrayList<CameraFileModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				CameraFileModel m = new CameraFileModel();
				m._id = jo.getInt("ID");
				m._url = jo.getString("FileUrl");
				m._time = new Timestamp((long) jo.getInt("Time") * 1000);
				list.add(m);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public int Ctr_v2(String nodeunique, int deviceno, int ctrtype, int minutes){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeunique", "" + nodeunique);
        params.put("deviceno", "" + deviceno);
        params.put("ctrtype", "" + ctrtype);
        params.put("minutes", "" + minutes);
        String txt = exeRetString(MethodFile, "Ctr_v2", params);
        return parseSave(txt);
	}
	
	public int CtrRule_v2(String nodeunique,int deviceno,int ctrtype,String collectuniqueids,String condition){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeunique", "" + nodeunique);
        params.put("deviceno", "" + deviceno);
        params.put("ctrtype", "" + ctrtype);
        params.put("collectuniqueids", "" + collectuniqueids);
        params.put("condition", "" + condition);
        String txt = exeRetString(MethodFile, "CtrRule_v2", params);
        return parseSave(txt);
	}
	
	public List<AgricultureFacilityModel> GetFacilitiesByUser(int userid){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("userid", "" + userid);
        String txt = exeRetString(MethodFile, "GetFacilitiesByUser", params);
        List<AgricultureFacilityModel> list = new ArrayList<AgricultureFacilityModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				AgricultureFacilityModel m = new AgricultureFacilityModel();
				m._id = jo.getInt("FacilitiesID");
				m._name = jo.getString("FacilitiesName");
				m._areaid = jo.getInt("AreaID");
				m._landid = jo.getInt("LandID");
				list.add(m);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public List<ControlLogModel> GetControlLogs(String nodeunique,int deviceno, int pagesize, int pageindex){
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("nodeunique", "" + nodeunique);
        params.put("deviceno", "" + deviceno);
        params.put("pagesize", "" + pagesize);
        params.put("pageindex", "" + pageindex);
        String txt = exeRetString(MethodFile, "GetControlLogs", params);
        List<ControlLogModel> list = new ArrayList<ControlLogModel>();
        try {
			JSONArray ja = new JSONArray(txt);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				ControlLogModel m = new ControlLogModel();
				m._logtype = jo.getInt("LogType");
				m._uniqueid = jo.getString("UniqueID");
				m._deviceno = jo.getInt("DeviceNo");
				m._starttype = jo.getInt("StartType");
				m._startvalue = jo.getInt("StartValue");
				m._runtime = jo.getInt("RunTime");
				m._endtype = jo.getInt("EndType");
				m._endvalue = jo.getInt("EndValue");
				m._createtime = new Timestamp((long)jo.getInt("CreateTimeSpan") * 1000);
				list.add(m);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return list;
	}
	
	private List<YunNodeModel> parseYNode(String str, int deviceversion) {
        List<YunNodeModel> models = new ArrayList<YunNodeModel>();
        try {
			JSONArray jarray = new JSONArray(str);
			for (int i = 0; i < jarray.length(); i ++){
				JSONObject jsonYunNode = jarray.optJSONObject(i);
				int nodetype = jsonYunNode.getInt("nodetype");
				String landIdStr = jsonYunNode.getString("landid");
				int landId = parseIntAnyWay(landIdStr);
				String landString = jsonYunNode.getString("landname");
				String facilityIdStr = jsonYunNode.getString("facilityid");
				int facilityId = parseIntAnyWay(facilityIdStr);
				String facility = jsonYunNode.getString("facilityname");
				int nodeNo = jsonYunNode.getInt("nodeno");
				String nodeString = jsonYunNode.getString("nodename");
				String nodeunique = jsonYunNode.getString("uniqueid");
				String timeStr = jsonYunNode.getString("lasttime");
				int devver = jsonYunNode.getInt("devver");
				YunNodeModel y = new YunNodeModel(nodetype, landId, landString, facilityId, facility, nodeNo, nodeString);
				y.mNodeUnique = nodeunique;
				y.devver = devver;
				if (!TextUtils.isEmpty(timeStr)){
					int timeInt = parseIntAnyWay(timeStr);
					if (timeInt > 0){
						y.mLastTime = new Timestamp(((long) timeInt) * 1000);
					}
				}
				JSONArray deviceArray = jsonYunNode.getJSONArray("devices");
				for (int j = 0; j < deviceArray.length(); j ++){
					JSONObject jsonDevice = deviceArray.optJSONObject(j);
					int deviceTypeNo = jsonDevice.getInt("devicetypeno");
					int channel = jsonDevice.getInt("channel");
					String name = jsonDevice.getString("name");
					String unit = jsonDevice.getString("unit");
					if (DeviceBean.TYPE_SENSOR == nodetype){
						DeviceBean d = new DeviceBean(nodetype, deviceTypeNo, channel, name);
						if (devver != 3){
							if (y.mLastTime != null && (((System.currentTimeMillis() - y.mLastTime.getTime())/1000) < (long)(Utils.offlineSeconds))){//TODO test 145 * 24 * 
								d.status = 0;
							}
						}
						y.addDeviceBean(d);
					} else if (DeviceBean.TYPE_ACTUATOR == nodetype){
						DeviceBean d = new DeviceBean(nodetype, deviceTypeNo, channel, name);
						if (devver != 3){
							d.status = 0;//二代控制都显示在线
						}
						y.addDeviceBean(d);
					} else if (DeviceBean.TYPE_CAMERA == nodetype){//TODO 枪机球机 海康大华
						int devicetype = deviceTypeNo == 1 ? 2 : 1;
						DeviceBean d = new DeviceBean(nodetype, devicetype, channel, name);
						if (TextUtils.isEmpty(unit)){
							d.videoID = parseIntAnyWay(y.mNodeUnique);
						} else {
							d.videoID = parseIntAnyWay(unit);//暂时把这个id存在webservice的这个unit字段里
						}
						d.status = -1;
						y.addDeviceBean(d);
					}
				}
				models.add(y);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        for (YunNodeModel m : models){
        	String timeStr = "";
        	if (null != m.mLastTime){
        		timeStr = Utils.formatTime(m.mLastTime);
        	}
        	
            Log.v(tag, "------nodestr:" + m.nodeStr + " no" + m.mNodeNo + " time:" + timeStr);
        }
        if (deviceversion == 3){
        	Collections.sort(models, new YNodeComparator());
        	Collections.sort(models, new YNodeComparatorStep2());
        } else {
//        	mysortv2(models);YNodeComparatorV2
        	Collections.sort(models, new YNodeComparatorV2(models));
        }
        return models;
    }
	
	private List<YunNodeModel> parseFacilityNode_short(String str, int deviceversion) {
        List<YunNodeModel> models = new ArrayList<YunNodeModel>();
        try {
			JSONArray jarray = new JSONArray(str);
			for (int i = 0; i < jarray.length(); i ++){
				JSONObject jsonYunNode = jarray.optJSONObject(i);
				int nodetype = jsonYunNode.getInt("nodetype");
				String landIdStr = jsonYunNode.getString("landid");
				int landId = parseIntAnyWay(landIdStr);
				String landString = jsonYunNode.getString("landname");
				String facilityIdStr = jsonYunNode.getString("facilityid");
				int facilityId = parseIntAnyWay(facilityIdStr);
				String facility = jsonYunNode.getString("facilityname");
				int nodeNo = jsonYunNode.getInt("nodeno");
				String nodeString = jsonYunNode.getString("nodename");
				String nodeunique = jsonYunNode.getString("uniqueid");
				String timeStr = jsonYunNode.getString("lasttime");
				YunNodeModel y = new YunNodeModel(nodetype, landId, landString, facilityId, facility, nodeNo, nodeString);
				try {
					if (!TextUtils.isEmpty(jsonYunNode.getString("latitude"))){
						y.latitude = jsonYunNode.getDouble("latitude");
						y.longitude = jsonYunNode.getDouble("longitude");
					}
				} catch (Exception e) {//需要单独catch
					e.printStackTrace();
				}
				y.mNodeUnique = nodeunique;
				if (!TextUtils.isEmpty(timeStr)){
					int timeInt = parseIntAnyWay(timeStr);
					if (timeInt > 0){
						y.mLastTime = new Timestamp(((long) timeInt) * 1000);
					}
				}
				models.add(y);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        for (YunNodeModel m : models){
        	String timeStr = "";
        	if (null != m.mLastTime){
        		timeStr = Utils.formatTime(m.mLastTime);
        	}
        	
            Log.v(tag, "------faciid:"+m.mFacilityID +" nodestr:" + m.nodeStr + " nodeno:" + m.mNodeNo + " time:" + timeStr + " (x,y):" + m.latitude + m.longitude);
        }
        if (deviceversion == 3){
        	Collections.sort(models, new YNodeComparator());
        } else {
//        	mysortv2(models);YNodeComparatorV2
        	Collections.sort(models, new YNodeComparatorV2(models));
        }
        return models;
    }
	
	private void parseLatestDataToModel(String str, String nodeunique, List<YunNodeModel> ylist){
		try {
			YunNodeModel yModel = null;
			for (int j = 0; j <ylist.size(); j ++){
				if (nodeunique.equals(ylist.get(j).mNodeUnique)){
					yModel = ylist.get(j);
					break;
				}
			}
			JSONArray jArray = new JSONArray(str);
			for (int i = 0; i < jArray.length(); i ++){
				JSONObject jo = jArray.optJSONObject(i);
				if (null != yModel){
					int sen = jo.getInt("sendtype");//笔误
					int channel = jo.getInt("chan");
					for (int k = 0; k < yModel.list.size(); k ++){
						if (yModel.list.get(k).mDeviceTypeNo == sen && yModel.list.get(k).mChannel == channel){
							double v = jo.getDouble("v");
							int time = jo.getInt("t");
							yModel.list.get(k).value = (float) v;
							yModel.list.get(k).valueStr = jo.getString("v");//值 的字符串
							yModel.list.get(k).mTime = new Timestamp((long) time * 1000);
							break;
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private List<XYStringHolder> parseHisData(String str, int sensorno){
		List<XYStringHolder> datas = new ArrayList<XYStringHolder>();
		int accurancy = DeviceBean.getaccurancy(sensorno);
		int maxCount = 24*3;//TODO
		try {
			JSONArray ja = new JSONArray(str);
			for (int i = 0; i < ja.length(); i ++){
				JSONObject jo = ja.optJSONObject(i);
				maxCount --;
				if (maxCount < 0){
					break;//太多数据会导致界面卡
				}
				if (!jo.isNull("returncount")){
					continue;
				}
				int time = jo.getInt("t");
				String valueTmp = jo.getString("v");
				//TODO

				String value = DeviceBean.getFormatedAccurancy(valueTmp, accurancy);

				datas.add(new XYStringHolder(time, value));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return datas;
	}


	
	//3代产品根据 facility 进行sort
	private static class YNodeComparator implements Comparator<YunNodeModel> {
        @Override  
        public int compare(YunNodeModel obj1, YunNodeModel obj2) {
            if (obj1.mFacilityID < obj2.mFacilityID){
                return 1;
            } else if (obj1.mFacilityID == obj2.mFacilityID){
                if (obj1.nodeType < obj2.nodeType){
                    return -1;
                } else if(obj1.nodeType == obj2.nodeType){
                    return 0;
                } else if (obj1.nodeType > obj2.nodeType){
                    return 1;
                }
            } else if (obj1.mFacilityID > obj2.mFacilityID){
                return -1;
            }
            return 0;  
        }  
    }
	//3代产品 第二次进行sort  之前已经按设施排序了
	private static class YNodeComparatorStep2 implements Comparator<YunNodeModel> {
        @Override  
        public int compare(YunNodeModel obj1, YunNodeModel obj2) {
            if (obj1.mNodeNo < obj2.mNodeNo){
                return -1;
            } else if (obj1.mNodeNo == obj2.mNodeNo){
//                if (obj1.nodeType < obj2.nodeType){
//                    return -1;
//                } else if(obj1.nodeType == obj2.nodeType){
//                    return 0;
//                } else if (obj1.nodeType > obj2.nodeType){
//                    return 1;
//                }
            	return 0;
            } else if (obj1.mNodeNo > obj2.mNodeNo){
                return 1;
            }
            return 0;  
        }  
    }
	
	
	
	private static Timestamp getBindSensorNodeTime(YunNodeModel m, List<YunNodeModel> list){
		if (m.nodeType == DeviceBean.TYPE_SENSOR){
			return m.mLastTime;
		} else {
			for (int i = 0; i < list.size(); i ++){
				if (list.get(i).nodeType == DeviceBean.TYPE_SENSOR && list.get(i).mNodeNo == m.mNodeNo){
					return list.get(i).mLastTime;
				}
			}
		}
		return null;
	}
	
	private static class YNodeComparatorV2 implements Comparator<YunNodeModel> {
		List<YunNodeModel> mList;
		public YNodeComparatorV2(List<YunNodeModel> list){
			mList = list;
		}
        @Override  
        public int compare(YunNodeModel obj1, YunNodeModel obj2) {
        	Timestamp time1 = getBindSensorNodeTime(obj1, mList);
        	Timestamp time2 = getBindSensorNodeTime(obj2, mList);
        	if (null != time1 && null == time2){
        		return 1;
        	} else if (null == time1 && null != time2){
        		return -1;
        	} else if (null != time1 && null != time2){
        		if (time1.getTime() > time2.getTime()){
        			return -1;
        		} else if (time1.getTime() == time2.getTime()){
        			if (obj1.mNodeNo < obj2.mNodeNo){
        				return -1;
        			} else if (obj1.mNodeNo == obj2.mNodeNo){
        				if (obj1.nodeType < obj2.nodeType){
        					return -1;
        				} else if (obj1.nodeType == obj2.nodeType){
        					return 0;
        				} else if (obj1.nodeType > obj2.nodeType){
        					return 1;
        				}
        			} else if (obj1.mNodeNo > obj2.mNodeNo){
        				return 1;
        			}
        		} else if (time1.getTime() < time2.getTime()){
        			return 1;
        		}
        	}
            return 0;  
        }  
    }
	
	private AlarmRuleModel parseAlarmRule_v2(String nodeUnique, String str){
		AlarmRuleModel alarmRuleModel = null;
		try {
			if ("anyType{}".equals(str) || TextUtils.isEmpty(str)){
				return null;
			}
//			JSONArray jArray = new JSONArray(str);
			JSONObject jo = new JSONObject(str);
			alarmRuleModel = new AlarmRuleModel();
			alarmRuleModel._id = jo.getInt("id");
			alarmRuleModel._uniqueID = nodeUnique;
			int relationid = jo.getInt("relation");
			alarmRuleModel._relation = relationid == 1 ?  "满足其中之一" : "同时满足条件";
			alarmRuleModel._ruleStr = jo.getString("rule");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return alarmRuleModel;
	}
	
	private HashMap<String, String> parseAlarming_v2(String str){
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			if (!TextUtils.isEmpty(str)){
				JSONArray jArray = new JSONArray(str);//好象是会nullexception导致线程死掉
				for (int i = 0; i < jArray.length(); i ++){
					JSONObject jo = jArray.optJSONObject(i);
					String uniqueid = jo.getString("uniqueid");
					String nodeName = jo.getString("node");
					JSONArray senArray = jo.getJSONArray("sensors");
					String senValues = "";
					for (int j = 0; j < senArray.length(); j ++){
						JSONObject senObject = senArray.optJSONObject(j);
						String senName = senObject.getString("senname");
						String liveData = senObject.getString("livedata");
						senValues += senName + "当前值为" + liveData;
					}
					if (!TextUtils.isEmpty(senValues)){
						params.put(uniqueid, senValues);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return params;
	}
	
	private VLCVideoInfoModel parseVLCVideoInfoModel(String str){
		VLCVideoInfoModel vModel = null;
		try {
			JSONObject jo = new JSONObject(str);
			vModel = new VLCVideoInfoModel();
			vModel._vlcvideoinfoid = jo.getInt("VLCVideoInfoID");
			vModel._username = jo.getString("UserName");
            vModel._password = jo.getString("PassWord");
            vModel._url = jo.getString("URL");
            vModel._ip = jo.getString("IP");
            vModel._port = jo.getString("Port");
            vModel._address = jo.getString("Address");
            vModel._type = jo.getString("Type");
            try {
                vModel._tcpport = jo.getInt("TcpPort");
                vModel._devicetype = jo.getInt("DeviceType");
            } catch (Exception e) {
                e.printStackTrace();
            }
            vModel._remark = jo.getString("Remark");
            vModel._devicetype = jo.getInt("DeviceType");
            vModel._serialno = jo.getString("SerialNo");
            vModel._verifycode = jo.getString("VerifyCode");
            vModel._ssiotezviz = jo.getBoolean("SSiotEzviz");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return vModel;
	}
	
	private int parseIntAnyWay(String str){
		try {
			if (!TextUtils.isEmpty(str)){
				return Integer.parseInt(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}