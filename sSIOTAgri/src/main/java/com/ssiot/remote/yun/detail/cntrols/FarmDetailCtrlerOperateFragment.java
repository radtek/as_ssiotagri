package com.ssiot.remote.yun.detail.cntrols;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.ssiot.remote.BaseFragment;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ControlActionInfoModel;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.unit.MyNumberPicker;
import com.ssiot.remote.yun.webapi.WS_API;
import com.ssiot.remote.yun.webapi.WS_MQTT;

import org.json.JSONArray;
import org.json.JSONObject;

public class FarmDetailCtrlerOperateFragment extends BaseFragment{
	private static final String tag = "FarmDetailCtrlerOperateFragment";
    private MyNumberPicker pickHour;
    private MyNumberPicker pickMinute;
    
//    private int deviceversion = 2;
    private DeviceBean device;
    private YunNodeModel mYunNodeModel;
//    String address = "";
    
    private static final int MSG_MQTT_GET = WS_MQTT.MSG_MQTT_GET;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_MQTT_GET:
                    String str = (String) msg.obj;
                    showToastMSG(str);//parseCtrRetJSON(str, mYunNodes); parseCtrRetJSON(str)
                    break;

                default:
                    break;
            }
        };
    };
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        deviceversion = Utils.getIntPref(Utils.PREF_USERDEVICETYPE, getActivity());
        device = ((DeviceBean) getActivity().getIntent().getSerializableExtra("devicebean"));
        mYunNodeModel = ((YunNodeModel) getActivity().getIntent().getSerializableExtra("yunnodemodel"));
        if (TextUtils.isEmpty(mYunNodeModel.mNodeUnique)){
            Toast.makeText(getActivity(), "数据出现问题mNodeUnique = null", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_farm_monitor_detail_ctrler_timedope_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        pickHour = ((MyNumberPicker)view.findViewById(R.id.pick1));
        pickMinute = ((MyNumberPicker)view.findViewById(R.id.pick2));
//        pickHour.updateAll();
//        pickMinute.updateAll();//一个bug？？ addview竟然在构造函数前执行，且我的int值为0
        View button1 = view.findViewById(android.R.id.button1);
        View button2 = view.findViewById(android.R.id.button2);
        View button3 = view.findViewById(android.R.id.button3);
        super.onViewCreated(view, savedInstanceState);
        pickHour.setMaxValue(23);
        pickHour.setMinValue(0);
        pickHour.setValue(0);
        pickMinute.setMaxValue(59);
        pickMinute.setMinValue(0);
        pickMinute.setValue(0);
//        pickHour.setCyclic(true);
//        pickHour.setDrawShadows(false);
//        pickHour.setViewAdapter(newWheelAdapter(0, 23));
//        pickHour.addChangingListener(this.listener);
//        pickHour.setOnClickListener(this.listener);
//        pickMinute.setCyclic(true);
//        pickMinute.setDrawShadows(false);
//        pickMinute.setViewAdapter(newWheelAdapter(0, 59));
//        pickMinute.addChangingListener(this.listener);
//        pickMinute.setOnClickListener(this.listener);
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
        button3.setOnClickListener(listener);
//        new GetAddressThread().start();
    }
    
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case android.R.id.button1:
                    if (null != pickHour && null != pickMinute){
                        int minutes = pickHour.getValue() * 60 + pickMinute.getValue();
                        if (minutes == 0){
                            Toast.makeText(getActivity(), "请先设置时间", Toast.LENGTH_SHORT).show();
                        } else {
                        	if (mYunNodeModel.devver == 2){
                        		startSetCtrOpen_v2(device.mChannel, minutes);
                        	} else {
                        		startSetCtrOpen_v3(mHandler, device.mChannel, minutes * 60);
                        	}
                        }
                    }
                    break;
                case android.R.id.button2:
                	if (mYunNodeModel.devver == 2){
                		startSetCtrClose_v2(device.mChannel);
                	} else {
                		startSetCtrClose_v3(mHandler, device.mChannel);
                	}
                    break;
                case android.R.id.button3:
                    break;
                default:
                    break;
            }
        }
    };
    
    private void startSetCtrOpen_v3(final Handler h, final int dev, int seconds){
        if (null != mYunNodeModel && mYunNodeModel.mNodeNo > 0){
        	new Thread(new Runnable() {
				@Override
				public void run() {
					int ret = new WS_MQTT().SetOneDeviceStateImmediately(mYunNodeModel.mNodeUnique, dev, 1);
					if (ret >= 0){
						device.status = 1;
					}
					Message m = h.obtainMessage(MSG_MQTT_GET);
					m.obj = (ret >= 0 ? "操作成功" : "操作失败");
					h.sendMessage(m);
				}
			}).start();
        	
//            new MQTT().subMsg("v1/n/"+ mYunNodeModel.mNodeUnique +"/switch/w/ack", h, 0);
//            new MQTT().pubMsg("v1/n/"+ mYunNodeModel.mNodeUnique +"/switch/w", "{\"addr\":\"" + address +"\",\"value\":\"1\"}");
            //TODO 存controlLog? 
        } else {
        	showToastMSG("数据出现问题，请重试");
        }
    }
    
    private void startSetCtrClose_v3(final Handler h, final int dev){
        if (null != mYunNodeModel && mYunNodeModel.mNodeNo > 0){
        	new Thread(new Runnable() {
				@Override
				public void run() {
					int ret = new WS_MQTT().SetOneDeviceStateImmediately(mYunNodeModel.mNodeUnique, dev, 0);
					if (ret >= 0){
						device.status = 0;
					}
					Message m = h.obtainMessage(MSG_MQTT_GET);
					m.obj = (ret >= 0 ? "关闭成功" : "关闭失败");
					h.sendMessage(m);
				}
			}).start();
//            new MQTT().subMsg("v1/n/"+ mYunNodeModel.mNodeUnique +"/switch/w/ack", h, 0);//TODO 是否需要持续监听状态
//            new MQTT().pubMsg("v1/n/"+ mYunNodeModel.mNodeUnique +"/switch/w", "{\"addr\":\"" + address +"\",\"value\":\"0\"}");
        } else {
        	showToastMSG("数据出现问题，请重试");
        }
    }
    
    private void startSetCtrOpen_v2(final int dev, final int minutes){
    	if (null != mYunNodeModel && mYunNodeModel.mNodeNo > 0){
    		new Thread(new Runnable() {
				@Override
				public void run() {
//					String ids = ""; 
//			        String openTimes = ""; 
//			        List<ControlActionInfoModel> data = new AjaxGetControlActionInfo().GetControlActionInfo(uniqueIDs, deviceNos);//TODO
//			        if (null != data){
//			            for (int i = 0; i < data.size(); i ++){//此设备下控制规则是1的，找出最后一个 运行时间 ？
//			                if (data.get(i)._controltype == 1){ 
//			                    ids = "" + data.get(i)._id;
//			                    openTimes = data.get(i)._controlcondition;
//			                }
//			            }
//			        }
			        
					String userkey = Utils.getStrPref(Utils.PREF_USERKEY, getActivity());
//					boolean ret = new AjaxGetNodesDataByUserkey().SaveControlAdd(userkey, ""+minutes, mYunNodeModel.mNodeUnique, 1, ""+dev, "");//TODO 接口
					int ret = new WS_API().Ctr_v2(mYunNodeModel.mNodeUnique, dev, 1, minutes);
					showToastMSG(ret > 0 ? "打开成功" : "打开失败");
				}
			}).start();
    	}
    }
    
    private void startSetCtrClose_v2(final int dev){
    	if (null != mYunNodeModel && mYunNodeModel.mNodeNo > 0){
    		new Thread(new Runnable() {
				@Override
				public void run() {
//					String data2 = new AjaxGetNodesDataByUserkey().ControlDevice(mYunNodeModel.mNodeUnique, ""+dev, "无", "close");
//		    		showToastMSG("true".equalsIgnoreCase(data2) ? "关闭成功" : "关闭失败");
					int ret = new WS_API().Ctr_v2(mYunNodeModel.mNodeUnique, dev, 2, 0);
					showToastMSG(ret > 0 ? "关闭成功" : "关闭失败");
				}
			}).start();
    	}
    }
    
    private void parseCtrRetJSON(String str){
        try {
            String topic = str.substring(0,str.indexOf("###"));
            String mqttmsg = str.substring(str.indexOf("###") + 3, str.length());
            
            JSONObject jo = new JSONObject(mqttmsg);
            device.status = jo.getInt("status");
            showToastMSG("操作成功。状态" + device.status);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
//    private class GetAddressThread extends Thread{
//    	@Override
//    	public void run() {
//    		if (null == mYunNodeModel || null == device){
//    			Log.e(tag, "--------!!!!!!!!!! null");
//    		}
//    		address = new WS_API().getCtrAddress(mYunNodeModel.mNodeNo, device.mChannel);
//    	}
//    }
}