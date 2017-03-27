package com.ssiot.remote.yun.monitor;

import com.ssiot.remote.data.model.VLCVideoInfoModel;

public class CameraDeviceBean extends DeviceBean{
    public VLCVideoInfoModel videoModel;

    public CameraDeviceBean(int type, int deviceTypeNo, String name) {
        super(type, deviceTypeNo, name);
        // TODO Auto-generated constructor stub
    }
    
    public void setVideoModel(VLCVideoInfoModel vModel){
        videoModel = vModel;
    }
    
}