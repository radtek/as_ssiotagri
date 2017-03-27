package com.ssiot.remote.yun.detail.sensors;

import android.util.Log;

import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.unit.XYStringHolder;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChartListModel {
    public String nodeunique;
    DeviceBean deviceBean ;

    public float todayHigh;
    public float todayLow;
    public float yesAver;
    public float yesHigh;
    public float yesLow;
    public float yesyesAver;
    public float yesyesHigh;
    public float yesyesLow;
    //TODO
    List<XYStringHolder> datas;

    public boolean GOT = false;

    public void calculateAllData(){
        List<XYStringHolder> todayList = new ArrayList<>();
        List<XYStringHolder> yesList = new ArrayList<>();
        List<XYStringHolder> yesyesList = new ArrayList<>();
        int todayStart = (int) (getTodayStartTime()/1000);
        if (null != datas){
            for (XYStringHolder y : datas){
                if (todayStart < y.timeInt){//今天
                    todayList.add(y);
                } else if ((todayStart-24 * 3600) < y.timeInt){//昨天
                    yesList.add(y);
                } else {
                    yesyesList.add(y);
                }
            }
            if (todayList.size() > 0) {
                todayHigh = todayList.get(0).yData;
                todayLow = todayList.get(0).yData;
                for (XYStringHolder xyToday : todayList) {
                    if (xyToday.yData > todayHigh) {
                        todayHigh = xyToday.yData;
                    }
                    if (xyToday.yData < todayLow) {
                        todayLow = xyToday.yData;
                    }
                }
            }
            if (yesList.size() > 0) {
                yesHigh = yesList.get(0).yData;
                yesLow = yesList.get(0).yData;
                float yesSum = 0;
                for (XYStringHolder xy : yesList) {
                    yesSum += xy.yData;
                    if (xy.yData > yesHigh) {
                        yesHigh = xy.yData;
                    }
                    if (xy.yData < yesLow) {
                        yesLow = xy.yData;
                    }
                }
                yesAver = yesSum/yesList.size();
            }
            if (yesyesList.size() > 0) {
                yesyesHigh = yesyesList.get(0).yData;
                yesyesLow = yesyesList.get(0).yData;
                float yesyesSum = 0;
                for (XYStringHolder xy : yesyesList) {
                    yesyesSum += xy.yData;
                    if (xy.yData > yesyesHigh) {
                        yesyesHigh = xy.yData;
                    }
                    if (xy.yData < yesyesLow) {
                        yesyesLow = xy.yData;
                    }
                }
                yesyesAver = yesyesSum/yesyesList.size();
            }
        }
    }

    private long getTodayStartTime(){
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }
}
