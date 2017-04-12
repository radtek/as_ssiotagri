
package com.ssiot.remote.yun.monitor;

import android.R.integer;
import android.util.Log;
import android.widget.ImageView;

import com.ssiot.agri.R;
import com.ssiot.remote.data.model.SensorModel;
import com.ssiot.remote.data.model.VLCVideoInfoModel;
import com.ssiot.remote.data.model.view.SensorThresholdModel;
import com.ssiot.remote.yun.webapi.WS_API;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

public class DeviceBean implements Serializable{
    private static final String tag = "DeviceBean";
    public static final int TYPE_SENSOR = 1;
    public static final int TYPE_ACTUATOR = 2;
    public static final int TYPE_CAMERA = 3;
    public int mType = 0;//大类 1 2 3
    public int mDeviceTypeNo = 0;
    public int mChannel = 0;
    public String mName;
    public float value;
    public String valueStr;//由于float位数不精确，
    public Timestamp mTime;
    public int status = -1;// -1=离线，0在线关闭,  1 在线且打开。 在线是绿色。运行中是蓝色，离线是红色
    public VLCVideoInfoModel vlcModel;//只有TYPE_CAMERA时有用
    public int videoID;
    private static boolean UNIT_UPDATED = false;
    public int ColorRes = 0;
    
    public SensorThresholdModel thresholdModel;
    
    private int deviceID = -1;
    
    private static HashMap<Integer, Integer> mSensorIconMap = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> mCtrlIconMap = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> mCameraIconMap = new HashMap<Integer, Integer>();
    static{
        mSensorIconMap.put(769, R.drawable.ic_section_humidity);//湿度
        mSensorIconMap.put(770, R.drawable.ic_section_temp);//温度
        mSensorIconMap.put(771, R.drawable.ic_section_soiltension);//大气压？？
        mSensorIconMap.put(772, R.drawable.ic_section_rain);//雨量
        mSensorIconMap.put(773, R.drawable.ic_section_vane);//风向
        mSensorIconMap.put(774, R.drawable.ic_section_wind);//风速
        mSensorIconMap.put(1001, R.drawable.ic_section_ph);//PH
        mSensorIconMap.put(1002, R.drawable.ic_section_do);//溶解氧？
        mSensorIconMap.put(1003, R.drawable.ic_section_default_sensor);//氨离子
        mSensorIconMap.put(1006, R.drawable.ic_section_default_sensor);//溶解氧饱和度？
        mSensorIconMap.put(1007, R.drawable.ic_section_default_sensor);//溶解氧相位
        mSensorIconMap.put(1008, R.drawable.ic_section_default_sensor);//氨氮
        mSensorIconMap.put(1009, R.drawable.ic_section_default_sensor);//铅离子
        mSensorIconMap.put(1010, R.drawable.ic_section_default_sensor);//镉离子
        mSensorIconMap.put(1011, R.drawable.ic_section_default_sensor);//氟离子传感器
        mSensorIconMap.put(1020, R.drawable.ic_section_erate);//电导率
        mSensorIconMap.put(1022, R.drawable.ic_section_default_sensor);//盐度传感器
        mSensorIconMap.put(1025, R.drawable.ic_section_soilhumidity);//土壤水分传感器
        mSensorIconMap.put(1102, R.drawable.ic_section_co2);//二氧化碳传感器浓度中
        mSensorIconMap.put(1123, R.drawable.ic_section_default_sensor);//钠离子浓度
        mSensorIconMap.put(1124, R.drawable.ic_section_default_sensor);//钾离子
        mSensorIconMap.put(1281, R.drawable.ic_section_lux);//光照强度  Lux
        mSensorIconMap.put(1282, R.drawable.ic_section_default_sensor);//光辐射度
        mSensorIconMap.put(1301, R.drawable.ic_section_default_sensor);//距离
        mSensorIconMap.put(1302, R.drawable.ic_section_default_sensor);//植物茎直径
        mSensorIconMap.put(1303, R.drawable.ic_section_wetness);//叶面湿度
        mSensorIconMap.put(1304, R.drawable.ic_section_default_sensor);//植物茎流量
        mSensorIconMap.put(1305, R.drawable.ic_section_default_sensor);//叶面温
        mSensorIconMap.put(1306, R.drawable.ic_section_default_sensor);//环境温
        mSensorIconMap.put(1401, R.drawable.ic_section_default_sensor);//电池电量
        mSensorIconMap.put(1402, R.drawable.ic_section_default_sensor);//电池电压
        mSensorIconMap.put(6101, R.drawable.ic_section_flow);//累计流量
        mSensorIconMap.put(6201, R.drawable.ic_section_default_sensor);//实时流量
        mSensorIconMap.put(7101, R.drawable.ic_section_default_sensor);//经度
        mSensorIconMap.put(7102, R.drawable.ic_section_default_sensor);//纬度
        mSensorIconMap.put(7201, R.drawable.ic_section_default_sensor);//速度
        mSensorIconMap.put(7202, R.drawable.ic_section_default_sensor);//方位角
        mSensorIconMap.put(7203, R.drawable.ic_section_default_sensor);//磁偏角
        mSensorIconMap.put(8001, R.drawable.ic_section_default_sensor);//十分钟平均风速
        mSensorIconMap.put(8002, R.drawable.ic_section_default_sensor);//十分钟平均风向
        mSensorIconMap.put(8003, R.drawable.ic_section_default_sensor);//总辐射传感器
        mSensorIconMap.put(8004, R.drawable.ic_section_default_sensor);//日照时数传感器
        mSensorIconMap.put(8005, R.drawable.ic_section_default_sensor);//十分钟累计雨量
        mSensorIconMap.put(8006, R.drawable.ic_section_default_sensor);//亚硝酸盐传感器
        mSensorIconMap.put(8007, R.drawable.ic_section_level);//水位传感器
        mSensorIconMap.put(8008, R.drawable.ic_section_default_sensor);//水氨氮传感器
        mSensorIconMap.put(8009, R.drawable.ic_section_default_sensor);//浊度传感器
        mSensorIconMap.put(8010, R.drawable.ic_section_cod);//COD传感器
        mSensorIconMap.put(8011, R.drawable.ic_section_default_sensor);//氨气传感器
        mSensorIconMap.put(8012, R.drawable.ic_section_h2s);//硫化氢传感器
        mSensorIconMap.put(8013, R.drawable.ic_section_default_sensor);//水温
        mSensorIconMap.put(8014, R.drawable.ic_section_default_sensor);//土壤肥力
        mSensorIconMap.put(8015, R.drawable.ic_section_soiltemp);//土壤温度
        mSensorIconMap.put(8016, R.drawable.ic_section_default_sensor);//气温
        mSensorIconMap.put(8017, R.drawable.ic_section_default_sensor);//分贝
        mSensorIconMap.put(8018, R.drawable.ic_section_default_sensor);//粉尘
        mSensorIconMap.put(8019, R.drawable.ic_section_default_sensor);//光谱
        mSensorIconMap.put(8020, R.drawable.ic_section_default_sensor);//扬尘
        mSensorIconMap.put(8021, R.drawable.ic_section_default_sensor);//土壤ph
        mSensorIconMap.put(8022, R.drawable.ic_section_default_sensor);//草料温度
        mSensorIconMap.put(8023, R.drawable.ic_section_default_sensor);//空气温度
        mSensorIconMap.put(8024, R.drawable.ic_section_default_sensor);//环境温度
        mSensorIconMap.put(8025, R.drawable.ic_section_default_sensor);//环境湿度
        mSensorIconMap.put(8026, R.drawable.ic_section_default_sensor);//光照照度
        mSensorIconMap.put(8027, R.drawable.ic_section_default_sensor);//设定温度
        mSensorIconMap.put(8028, R.drawable.ic_section_default_sensor);//设定湿度
        mSensorIconMap.put(8029, R.drawable.ic_section_default_sensor);//设定COⅡ浓度
        mSensorIconMap.put(8030, R.drawable.ic_section_default_sensor);//果实大小
        mSensorIconMap.put(8031, R.drawable.ic_section_default_sensor);//系统开关机输出状态
        mSensorIconMap.put(8032, R.drawable.ic_section_default_sensor);//门磁
        mSensorIconMap.put(8033, R.drawable.ic_section_default_sensor);//硝酸根离子
        mSensorIconMap.put(8034, R.drawable.ic_section_default_sensor);//开关状态
        mSensorIconMap.put(8035, R.drawable.ic_section_default_sensor);//铜离子
        mSensorIconMap.put(8036, R.drawable.ic_section_default_sensor);//溴离子
        mSensorIconMap.put(8037, R.drawable.ic_section_default_sensor);//钙离子
        mSensorIconMap.put(8038, R.drawable.ic_section_default_sensor);//硬度
        mSensorIconMap.put(8039, R.drawable.ic_section_default_sensor);//ORP
        
        mCtrlIconMap.put(1, R.drawable.ic_section_default);
        
        mCameraIconMap.put(1, R.drawable.ic_section_surveillance);
        mCameraIconMap.put(2, R.drawable.ic_section_surveillance_ball);
    }

    public static List<SensorModel> sensorMap;
    private static HashMap<Integer, String> mSensorUnitMap = new HashMap<Integer, String>();
    private static HashMap<Integer, Integer> mSensorAccurancyMap = new HashMap<Integer, Integer>();

    public static void updateunit(){
    	if (!UNIT_UPDATED){
    		new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					List<SensorModel> sensors = new WS_API().GetAllSensors();
					mSensorUnitMap.clear();
					for (int i = 0; i < sensors.size(); i ++){
						int sensorno = sensors.get(i)._sensorno;
						mSensorUnitMap.put(sensorno, sensors.get(i)._unit);
                        mSensorAccurancyMap.put(sensorno, sensors.get(i)._accuracy);
					}
					if (sensors.size() > 0 && mSensorUnitMap.size() > 0){
						UNIT_UPDATED = true;
                        sensorMap = sensors;
					}
				}
			}).start();
    	}
    }

    public static String getFormatedAccurancy(String dataStr, int accurancy){
        if (accurancy >= 0){
            int dotIndex = dataStr.indexOf(".");
            if (dotIndex >= 0){
                try {
                    return dataStr.substring(0,dotIndex + accurancy);
                } catch (Exception e){

                }
            }
        }
        return dataStr;
    }

    public static int getaccurancy(int sensorno){//必须要参数
//        if (null != sensorMap) {
//            for (SensorModel m : sensorMap) {
//                if (m._sensorno == sensorno){
//                    return m._accuracy;
//                }
//            }
//        }
        if (mSensorAccurancyMap.containsKey(sensorno)) {
            return mSensorAccurancyMap.get(sensorno);
        }
        return -1;
    }

    public static String getAccurancyedValueStr(String dataStr, int sensorno){
        int accurancy = getaccurancy(sensorno);
        return getFormatedAccurancy(dataStr, accurancy);
    }
    
    public DeviceBean(int type, int deviceTypeNo, String name) {// 例如 光照
        mType = type;
        mDeviceTypeNo = deviceTypeNo;
        mName = name;
//        Imageview setColorFilter 可以动态改变图标的颜色
    }
    
    public DeviceBean(int type, int deviceTypeNo, int channel,String name) {// 例如 光照
        mType = type;
        mDeviceTypeNo = deviceTypeNo;
        mChannel = channel;
        mName = name;
    }
    
    public DeviceBean(int type, int deviceTypeNo, int channel,String name, Timestamp time) {// 例如 光照
        mType = type;
        mDeviceTypeNo = deviceTypeNo;
        mChannel = channel;
        mName = name;
        mTime = time;
    }
    
    public int getContactStatus(){//TODO
        return status;
    }
    
    public int getRunStatus(){//TODO
        return 1;
    }

    // 设置为static比较好
    public static int getIconRes(int type, int deviceno) {
        int resId = R.drawable.ic_section_default_sensor;
        try {
            switch (type) {
                case 1:// sensor 类
                    if (mSensorIconMap.containsKey(deviceno)){
                        resId = mSensorIconMap.get(deviceno);
                    }
                    break;
                case 2:// 控制 类
//                    if (mCtrlIconMap.containsKey(deviceno)){
//                        resId = mCtrlIconMap.get(deviceno);
//                    }
                	resId = R.drawable.ic_section_default;
                    break;
                case 3:// 视频 类
                    if (mCameraIconMap.containsKey(deviceno)){
                        resId = mCameraIconMap.get(deviceno);
                    }
                    
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resId = R.drawable.ic_section_default_sensor;
        }
        
        return resId;
    }
    
    public int getIconRes(){
        return getIconRes(mType, mDeviceTypeNo);
    }
    
    public String getUnit(){
        if (mSensorUnitMap.containsKey(mDeviceTypeNo)){
            return mSensorUnitMap.get(mDeviceTypeNo);
        }
        return "";
    }
    
    public boolean isSensorDevice(){
        return mType == TYPE_SENSOR;
    }
    
    public boolean isWebcamDevice(){
        return mType == TYPE_CAMERA;
    }
    
    public boolean isCntrolDevice(){
        return mType == TYPE_ACTUATOR;
    }
    
    public boolean isOffline(){//TODO 一个全局int状态
        return false;
    }
    
    public int getDeviceID() {
      return deviceID;
    }
    
    public int getAbnormal(){
    	if (null != thresholdModel){
    		if (thresholdModel.thresholdType == 1){
    			if (value > thresholdModel.upperwarnvalue){
    				return 1;
    			}
    		} else if (thresholdModel.thresholdType == 2){
    			if (value < thresholdModel.lowerwarnvalue){
    				return 2;
    			}
    		} else if (thresholdModel.thresholdType == 3){
    			if (value > thresholdModel.upperwarnvalue){
    				return 1; 
    			} else if (value < thresholdModel.lowerwarnvalue){
    				return 2;
    			}
    		}
    	}
    	return -1;
    }
    
    public int getWarnLevel(){//0 正常  1=黄色预警 2= 红色预警 陆教授环境监测
    	if (null != thresholdModel){
    		if (thresholdModel.thresholdType == 1){
    			if (value > thresholdModel.upperalertvalue){
    				return 2;
    			} else if (value > thresholdModel.upperwarnvalue){
    				return 1;
    			}
    		} else if (thresholdModel.thresholdType == 2){
    			if (value < thresholdModel.loweralertvalue){
    				return 2;
    			} else if (value < thresholdModel.lowerwarnvalue){
    				return 1;
    			}
    		} else if (thresholdModel.thresholdType == 3){
    			if (value > thresholdModel.upperalertvalue || value < thresholdModel.loweralertvalue){
    				return 2; 
    			} else if (value < thresholdModel.lowerwarnvalue || value > thresholdModel.upperwarnvalue){
    				return 1;
    			}
    		}
    	}
    	return 0;
    }
    
    @Override
    public String toString() {
        String str = "";
        switch (mType) {
            case TYPE_SENSOR:
                str += "传感器";
                break;
            case TYPE_ACTUATOR:
                str += "执行器";
                break;
            case TYPE_CAMERA:
                str += "摄像机";
                break;
            default:
                break;
        }
        str += " mName:" + mName +" mDeviceTypeNo:"+ mDeviceTypeNo +" channel:"+ mChannel + " status;" +status;
        return str;
    }
}
