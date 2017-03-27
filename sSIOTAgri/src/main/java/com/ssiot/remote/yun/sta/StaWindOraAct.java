package com.ssiot.remote.yun.sta;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ssiot.agri.R;
import com.ssiot.remote.yun.detail.sensors.SensorLineChartFrag;
import com.ssiot.remote.yun.unit.XYStringHolder;
import com.ssiot.remote.yun.unit.achar.Radar;
import com.ssiot.remote.yun.unit.achar.RadarGraph;
import com.ssiot.remote.yun.webapi.WS_API;

import java.util.ArrayList;
import java.util.List;

public class StaWindOraAct extends StaDetailAct{
    private static final String tag = "StaWindOraAct";
    ArrayList<Radar> localArrayList = new ArrayList<Radar>();
    RadarGraph radarGraph;
    float[] val = {0f,0,0,0,0,0,0,0};
    
    private static final int MSG_GET_END = 1;
    private static final int MSG_ERROR = 2;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    refreshChart();
                    break;
                case MSG_ERROR:
                    showToast((String) msg.obj);
                    break;

                default:
                    break;
            }
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        new GetDataThread().start();
    }
    
    private void init(){
        findViewById(R.id.graphLinearLayout).setVisibility(View.GONE);
        findViewById(R.id.LinearLayoutRadar).setVisibility(View.VISIBLE);
        radarGraph = (RadarGraph) findViewById(R.id.RadarGraph);
        int[] winval = {6,1,5,5,5,5,5,5};
        localArrayList.add(new Radar(val,winval,9));
//        localArrayList.add(new Radar(val,winval,5));
//        localArrayList.add(new Radar(val,winval,45));
//        localArrayList.add(new Radar(val,winval,1));
//        localArrayList.add(new Radar(val,winval,1));
//        localArrayList.add(new Radar(val,winval,9));
//        localArrayList.add(new Radar(val,winval,8));
//        localArrayList.add(new Radar(val,winval,3));
        radarGraph.setRadars(localArrayList);
        radarGraph.invalidate();
    }
    
    private void refreshChart(){
        radarGraph.invalidate();
    }
    
    private void resetVal(){
        for (int i = 0; i < val.length; i ++){
            val[i] = 0;
        }
    }
    
    @Override
    public boolean onClickSearch() {
    	boolean ret = super.onClickSearch();
    	if (ret){
    		new GetDataThread().start();
    	}
        return true;
    }
    
    private class GetDataThread extends Thread{
        @Override
        public void run() {
            resetVal();
            String column = mSelectColumnStr;// + (mDeviceBean.mChannel == 0 ? "" : mDeviceBean.mChannel);
            if (TextUtils.isEmpty(mTableName) || TextUtils.isEmpty(mSelectColumnStr) || TextUtils.isEmpty(mSelectNodeUnique)
                    || startTimestamp == null || endTimestamp == null){
                Message msg = mHandler.obtainMessage(MSG_ERROR);
                msg.obj = "----查询设置错误------" + mTableName + mSelectColumnStr + mSelectNodeUnique;
                mHandler.sendMessage(msg);
                return;
            }
//            List<XYStringHolder> list = DataAPI.getSingleSensorDataByTime(mTableName, mSelectNodeUnique, "Avg", column,startTimestamp,endTimestamp);
            List<XYStringHolder> list = new WS_API().GetSensorHisData_His(mSelectNodeUnique, 
            		(int) (startTimestamp.getTime()/1000), (int) (endTimestamp.getTime()/1000), 
            		getTableIndex(mTableName), mDeviceBean.mDeviceTypeNo, currentChannel);
            if (null != list){
                for (XYStringHolder xy : list){
                    int direction = ((int) ((xy.yData + 22.5)/45)) % 8;
                    val[direction] ++; 
                }
                Log.v(tag, "------风向:" + val[0] + val[1] + val[2] + val[3] + val[4] + val[5] + val[6] + val[7]);
                mHandler.sendEmptyMessage(MSG_GET_END);
            }
        }
    }
    
}