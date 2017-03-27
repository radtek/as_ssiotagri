package com.ssiot.remote.yun.detail.cntrols;

import android.content.res.Resources;
import android.text.TextUtils;

import com.ssiot.agri.R;

public class CtrlRuleBean {
    
    public int ruleType = 15;
    public long startTime;
    public long endTime;
    public int workTime;
    public int spandTime;
    public int redundant;
    
    public String getWeekdaysText(Resources resources){
        String str = "";
        int weekday = 0;
        int tmp = redundant;
        if (tmp > 0){
            while(tmp != 0){
                int i = tmp % 2;
                tmp = tmp / 2;
                if (i > 0){
                    str += resources.getStringArray(R.array.datesItem)[weekday] + ",";
                }
                weekday ++;
            }
        }
        if (!TextUtils.isEmpty(str)){
            str = str.substring(0, str.length() -1);
            str = "周" + str;
        }
        return str;
    }
    
}