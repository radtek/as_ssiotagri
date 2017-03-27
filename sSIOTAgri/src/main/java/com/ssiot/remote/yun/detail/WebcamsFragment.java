package com.ssiot.remote.yun.detail;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.dahuavideo.DahuaLiveActivity;
import com.ssiot.remote.data.model.VLCVideoInfoModel;
import com.ssiot.remote.hikvision.RTSPVideo;
import com.ssiot.remote.hikvision.VideoActivity;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.TintedBitmapDrawable;
import com.ssiot.remote.yun.webapi.WS_API;
import com.videogo.ui.realplay.EZPrepareAct;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class WebcamsFragment extends DevicesFragment{
    private static final String tag = "WebcamsFragment";
    ListView webcamList;
    List<DeviceBean> deviceBeans = new ArrayList<DeviceBean>();
    WebcamAdapter webcamAdapter;
    
    private boolean cancelStatus = false;
    private static final int MSG_NOTIFY = 1;
    private static final int MSG_ACCESS_OK = 2;
    private static final int MSG_ACCESS_FAIL = 3;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_NOTIFY:
                    if (null != webcamAdapter){
                        webcamAdapter.notifyDataSetChanged();
                        if (mRunning == false){
                            new UpdateStatusThread(deviceBeans).start();
                        }
                    }
                    break;
                case MSG_ACCESS_OK:
                    deviceBeans.get(msg.arg1).status = 1;
                    webcamAdapter.notifyDataSetChanged();
                    break;
                case MSG_ACCESS_FAIL:
                    deviceBeans.get(msg.arg1).status = -1;
                    webcamAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        };
    };
    
    @Override
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return paramLayoutInflater.inflate(R.layout.activity_farm_monitor_detail_webcams_fragment, paramViewGroup, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        webcamList = (ListView) view.findViewById(android.R.id.list);
        super.onViewCreated(view, savedInstanceState);
        deviceBeans.clear();
        for (int i = 0; i < mYunNodeModels.size(); i ++){
            if (null != mYunNodeModels.get(i).list){
                deviceBeans.addAll(mYunNodeModels.get(i).list);
            }
        }
        for (DeviceBean d : deviceBeans){
        	d.status = -1;//先设置为离线
        }
        webcamAdapter = new WebcamAdapter(deviceBeans);
        webcamList.setAdapter(webcamAdapter);
        webcamList.setDividerHeight(0);
        webcamList.setOnItemClickListener(itmClickListener);
        new GetVLCVideoInfoThread().start();//webservice获取ipc的信息
//        if (mRunning == false){
//            new UpdateStatusThread(deviceBeans).start();
//        }
    }
    
    AdapterView.OnItemClickListener itmClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (null != deviceBeans.get(position) && deviceBeans.get(position).vlcModel != null){
                Log.v(tag, "----------onclick------type:"+ deviceBeans.get(position).vlcModel._type);
                VLCVideoInfoModel vModel = deviceBeans.get(position).vlcModel;
                if (!Utils.isNetworkConnected(getActivity())){
                    Toast.makeText(getActivity(), R.string.please_check_net, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (vModel._ssiotezviz){
                	Toast.makeText(getActivity(), "请使用萤石云播放", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("大华".equals(vModel._type) && vModel._tcpport != 0){
                    Intent intent = new Intent(getActivity(), DahuaLiveActivity.class);
                    intent.putExtra("videoip", vModel._ip);
                    intent.putExtra("videoport", vModel._port);
                    intent.putExtra("videoname", vModel._username);
                    intent.putExtra("videopswd", vModel._password);
                    intent.putExtra("addrtitle", vModel._address);
                    intent.putExtra("tcpport", vModel._tcpport);
                    intent.putExtra("devicetype", vModel._devicetype);
                    startActivity(intent);	
                } else if ("海康".equals(vModel._type) && vModel._tcpport != 0){ 
                    Intent intent = new Intent(getActivity(), VideoActivity.class);
                    Bundle videoBundle = new Bundle();
                    videoBundle.putString("videoip", vModel._ip);
                    videoBundle.putString("videoname", vModel._username);
                    videoBundle.putString("videopswd", vModel._password);
                    videoBundle.putString("addrtitle", vModel._address);
                    videoBundle.putInt("tcpport", vModel._tcpport);
//                    intent.putExtra("devicetype", vModel._devicetype);
                    videoBundle.putInt("devicetype", vModel._devicetype);
                    intent.putExtra("videobundle", videoBundle);
                    startActivity(intent);
                } else {
                    Intent i = new Intent(getActivity(), RTSPVideo.class);
                    i.putExtra("videourl", vModel._url);
                    i.putExtra("addrtitle", vModel._address);
                    startActivity(i);
                }
            } else {
            	showToastMSG("数据不完整");
            }
        }
    };
    
    @Override
    public void onDestroyView() {
        cancelStatus = true;
        super.onDestroyView();
        
    }

    @Override
    int getTabID() {
        return R.id.tcagri_farm_detail_tab_webcams;
    }

    @Override
    void add(@NonNull DeviceBean paramDevice) {
//        if (paramDevice.isCameraDevice()) {
//            this.cameraAdapter.addAsync(paramDevice);
//        } else {
//            this.webcamAdapter.addAsync(paramDevice);
//        }
    }

    @Override
    int totalDevices() {
//        return this.cameraAdapter.getCount() + this.webcamAdapter.getCount();
        return 0;
    }
    
    @Override
    void updateUI() {
        // TODO Auto-generated method stub
        
    }
    
    private final class WebcamAdapter extends BaseAdapter{
        private List<DeviceBean> datas;
        
        public WebcamAdapter(List<DeviceBean> list) {
            datas = list;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public DeviceBean getItem(int position) {
            if (position < datas.size()){
                return datas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            if (position < datas.size()){
                return datas.get(position).getDeviceID();
            }
            return -1;
        }

        @Override
        public View getView(int position, View paramView, ViewGroup paramViewGroup) {
            ViewHolder localViewHolder;
            if ((paramView != null) && ((paramView.getTag() instanceof ViewHolder))) {
                localViewHolder = (ViewHolder) paramView.getTag();
            } else {
                paramView = getLayoutInflater(null).inflate(R.layout.activity_farm_monitor_detail_webcams_list_item, paramViewGroup, false);
                localViewHolder = new ViewHolder(paramView);
            }
            DeviceBean localDevice = getItem(position);
            localViewHolder.name.setTag(localDevice);
            localViewHolder.menu.setTag(localDevice);
            localViewHolder.button.setTag(Integer.valueOf(position));
            if (localDevice != null) {
                paramView.setVisibility(View.VISIBLE);
            } else {
                paramView.setVisibility(View.GONE);
            }
            // localViewHolder.menu.setOnClickListener(this);
            // localViewHolder.name.setOnClickListener(this);
            // localViewHolder.name.setOnLongClickListener(this);
            // localViewHolder.button.setOnClickListener(this);
            localViewHolder.setup(localDevice);
            return paramView;
        }
        
        private final class ViewHolder {
            final View button;
            final View menu;
            final TextView name;
            final ImageView screen;
            final ImageView state;

            ViewHolder(View v) {
                v.setTag(this);
                screen = ((ImageView) v.findViewById(android.R.id.icon1));
                button = v.findViewById(android.R.id.icon2);
                menu = v.findViewById(R.id.setting_cameras);
                name = ((TextView) v.findViewById(android.R.id.title));
                state = ((ImageView) v.findViewById(android.R.id.icon));
            }

            void setup(final DeviceBean paramDevice) {
                Drawable d = null;
                if (null == paramDevice.vlcModel){
                	Log.e(tag, "-----vlcmodel = null !!!!");
                    return;
                }
                if (paramDevice.getContactStatus() < 0) {
                    if (paramDevice.vlcModel._devicetype > 0){
                        d = new TintedBitmapDrawable(getResources(), R.drawable.ic_section_surveillance_ball, R.color.grey);
                    } else {
                        d = new TintedBitmapDrawable(getResources(), R.drawable.ic_section_surveillance, R.color.grey);
                    }
//                    state.setImageResource(R.drawable.tcagri_farm_device_offline_hint);
                } else {
                    Log.v(tag, "---------------video在线");
                    if (paramDevice.vlcModel._devicetype > 0){
                        d = new TintedBitmapDrawable(getResources(), R.drawable.ic_section_surveillance_ball, R.color.DarkGreen);
                    } else {
                        d = new TintedBitmapDrawable(getResources(), R.drawable.ic_section_surveillance, R.color.DarkGreen);
                    }
                }
                state.setImageDrawable(d);
                name.setText(paramDevice.mName);// + " " + paramDevice.vlcModel._address

//                screen.setImageBitmap(BitmapFactory.decodeFile(
//                        new File(getActivity().getCacheDir(), paramDevice.getDeviceID() + ".jpg")
//                                .getAbsolutePath()));
                // if (paramDevice.isWarning()){
                // state.setImageResource(R.drawable.tcagri_farm_device_warning_hint);
                // } else {
                // state.setImageResource(R.drawable.tcagri_farm_device_connect_hint);
                // }
                // state.setImageResource(i);
                button.setEnabled(true);
                menu.setEnabled(true);
                menu.setOnClickListener(null);
                if (paramDevice.vlcModel._ssiotezviz){//瑞擎萤石
                	menu.setVisibility(View.VISIBLE);
                    menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent ysPrepareAct = new Intent(getActivity(),EZPrepareAct.class);
                            ysPrepareAct.putExtra("deviceserial", paramDevice.vlcModel._serialno);
                            ysPrepareAct.putExtra("verifycode", "");
                            startActivity(ysPrepareAct);
                        }
                    });
                } else {
                	menu.setVisibility(View.GONE);
                }
            }
        }
    }
    
    private boolean mRunning = false;
    public class UpdateStatusThread extends Thread{
        List<DeviceBean> models;
        public UpdateStatusThread(List<DeviceBean> ms){
            models = ms;
            cancelStatus = false;
        }
        @Override
        public void run() {
            mRunning = true;
            if (null != models){
                int size = models.size();
                for (int i = 0; i < size; i ++){
                    if (cancelStatus){
                        mRunning = false;
                        return;
                    }
                    Socket socket = new Socket();
                    VLCVideoInfoModel model = models.get(i).vlcModel;
                    if (null == model){
                        Log.e(tag, "!!!!!!!!!!!!vlcmodel = null");
                        return;
                    }
                    try {
                        int port = model._tcpport;
                        if (port == 0){
                            port = Integer.parseInt(model._port);
//                            if (port==5566){
//                                port =77;
//                            }
                        }
                        Log.v(tag, "-----start to test " + model._ip +  port + "  tcpport:"+model._tcpport);
                        SocketAddress socketAddress = new InetSocketAddress(model._ip, port);
                        socket.connect(socketAddress, 3000);
                        Message msg = mHandler.obtainMessage();
                        msg.what = MSG_ACCESS_OK;
                        msg.arg1 = i;
                        mHandler.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Message msg = mHandler.obtainMessage();
                        msg.what = MSG_ACCESS_FAIL;
                        msg.arg1 = i;
                        mHandler.sendMessage(msg);
                    }
                    try {
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                }
            }
            mRunning = false;
        }
    }
    
    private class GetVLCVideoInfoThread extends Thread{
    	@Override
    	public void run() {
    		if (null != deviceBeans && deviceBeans.size() > 0){
    			for (DeviceBean d : deviceBeans){
    				VLCVideoInfoModel m = new WS_API().GetIPCByID(d.videoID);
    				d.vlcModel = m;
    			}
    			mHandler.sendEmptyMessage(MSG_NOTIFY);
    		}
    	}
    }
    
}