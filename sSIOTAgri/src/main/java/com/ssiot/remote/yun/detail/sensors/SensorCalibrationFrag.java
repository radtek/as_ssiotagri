package com.ssiot.remote.yun.detail.sensors;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
//import com.ssiot.remote.data.AjaxCalibration;
import com.ssiot.remote.data.model.SettingModel;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.webapi.WS_API;

//校准frag  二代的界面
public class SensorCalibrationFrag extends Fragment{
    YunNodeModel mYunNodeModel;
    DeviceBean mDeviceBean;
    
    TextView bdNowTextView;
    EditText bdEdit;
    TextView bdSend;
    TextView xzTitle;//修正的title
    TextView xzNowTextView;
    EditText xzEdit;
    TextView xzSend;
    
    private static final int MSG_GET_END = 1;
    private static final int MSG_SEND_END = 2;
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    float f = (Float) msg.obj;
                    if (msg.arg1 == 1){
                        xzNowTextView.setText("当前修正值:" + f);
                    } else if (msg.arg1 == 3){//标定
                        bdNowTextView.setText("当前标定值:" + f);
                    }
                    break;
                case MSG_SEND_END:
                    boolean result = (Boolean) msg.obj;
                    Toast.makeText(getActivity(), result ? "保存成功" : "保存失败", Toast.LENGTH_SHORT).show();
                    if (result){
                        new GetExistJiaoZhunThread().start();
                    }
                    break;

                default:
                    break;
            }
        };
    };
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle b = getArguments();
        mYunNodeModel = (YunNodeModel) b.getSerializable("yunnodemodel");
        mDeviceBean = (DeviceBean) b.getSerializable("devicebean");
        if (null == mYunNodeModel || null == mDeviceBean){
            Toast.makeText(getActivity(), "参数错误", Toast.LENGTH_SHORT).show();
        }
        super.onActivityCreated(savedInstanceState);
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_farm_monitor_detail_sensor_cali_fragment, container, false);
        initViews(rootView);
        return rootView;
    }
    
    private void initViews(View rootView){
        bdNowTextView = (TextView) rootView.findViewById(R.id.bd_now);
        bdEdit = (EditText) rootView.findViewById(R.id.bd_edit);
        bdSend = (TextView) rootView.findViewById(R.id.bd_send);
        
        xzTitle = (TextView) rootView.findViewById(R.id.title_xz);
        xzNowTextView = (TextView) rootView.findViewById(R.id.xz_now);
        xzEdit = (EditText) rootView.findViewById(R.id.xz_edit);
        xzSend = (TextView) rootView.findViewById(R.id.xz_send);
//        xzTitle.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showxz(true);
//                return true;
//            }
//        });
        bdSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendbdThread().start();
            }
        });
        xzSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendxzThread().start();
            }
        });
//        showxz(false);
        new GetExistJiaoZhunThread().start();
    }
    
    private void showxz(boolean b){
        if (b){
            xzTitle.setTextColor(getResources().getColor(R.color.black));
            xzEdit.setEnabled(true);
            xzSend.setEnabled(true);
        } else {
            xzTitle.setTextColor(getResources().getColor(R.color.grey));
            xzEdit.setEnabled(false);
            xzSend.setEnabled(false);
        }
    }
    
    private class GetExistJiaoZhunThread extends Thread{//标定type = 3
        @Override
        public void run() {
            if (null == mYunNodeModel || null == mDeviceBean){
                return;
            }
//            SettingModel sModel = new AjaxCalibration().GetJiaoZhunBySensorNameAndType(mYunNodeModel.mNodeNo, 3, mDeviceBean.mDeviceTypeNo, 
//                    mDeviceBean.mChannel);
//            int deviceVersion = Utils.getIntPref(Utils.PREF_USERDEVICETYPE, getActivity());
//            if (deviceVersion == 3){//3代产品
//            	new WS_API().GetCali_v3(""+mYunNodeModel.mNodeNo, mDeviceBean.mDeviceTypeNo, mDeviceBean.mChannel);
//            	
//            } else {
//            	
//            }
            List<SettingModel> list = new WS_API().GetCali_v2(mYunNodeModel.mNodeUnique, mDeviceBean.mDeviceTypeNo, mDeviceBean.mChannel);//TODO
        	if (null != list && list.size() > 0){
        		for (int i = 0; i < list.size(); i ++){
        			if (list.get(i)._type == 3){
        				Message m = mHandler.obtainMessage(MSG_GET_END);
                        m.arg1 = 3;
                        m.obj = list.get(i)._value;
                        mHandler.sendMessage(m);
        			} else if (list.get(i)._type == 1){
        				Message m = mHandler.obtainMessage(MSG_GET_END);
                        m.arg1 = 1;
                        m.obj = list.get(i)._value;
                        mHandler.sendMessage(m);
        			}
        		}
        	}
            
//            if (null != sModel){
//                Message m = mHandler.obtainMessage(MSG_GET_END);
//                m.arg1 = 3;
//                m.obj = sModel._value;
//                mHandler.sendMessage(m);
//            }
//            SettingModel sModel2 = new AjaxCalibration().GetJiaoZhunBySensorNameAndType(mYunNodeModel.mNodeNo, 1, mDeviceBean.mDeviceTypeNo,
//                    mDeviceBean.mChannel);
//            if (null != sModel2){
//                Message m = mHandler.obtainMessage(MSG_GET_END);
//                m.arg1 = 1;
//                m.obj = sModel2._value;
//                mHandler.sendMessage(m);
//            }
        }
    }
    
    private class SendbdThread extends Thread{
        @Override
        public void run() {
            float value = Float.parseFloat(bdEdit.getText().toString());
//            boolean b = new AjaxCalibration().SendModify(true, mDeviceBean.mChannel, mYunNodeModel.mNodeNo, value, mDeviceBean.mName);
            int time = (int) (System.currentTimeMillis()/1000);
            boolean b = new WS_API().SetCali_v2(mYunNodeModel.mNodeUnique, 3, mDeviceBean.mDeviceTypeNo, mDeviceBean.mChannel, value, time) > 0;
            Message m = mHandler.obtainMessage(MSG_SEND_END);
            m.obj = b;
            mHandler.sendMessage(m);
        }
    }
    
    private class SendxzThread extends Thread{
        @Override
        public void run() {
            float value = Float.parseFloat(xzEdit.getText().toString());
//            boolean b = new AjaxCalibration().SendModify(false, mDeviceBean.mChannel, mYunNodeModel.mNodeNo, value, mDeviceBean.mName);
            int time = (int) (System.currentTimeMillis()/1000);
            boolean b = new WS_API().SetCali_v2(mYunNodeModel.mNodeUnique, 1, mDeviceBean.mDeviceTypeNo, mDeviceBean.mChannel, value, time) > 0;
            Message m = mHandler.obtainMessage(MSG_SEND_END);
            m.obj = b;
            mHandler.sendMessage(m);
        }
    }
    
    
}