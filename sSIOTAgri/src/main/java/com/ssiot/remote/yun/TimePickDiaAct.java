package com.ssiot.remote.yun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ssiot.agri.R;

import java.text.SimpleDateFormat;
import java.util.Date;
//由于俩控件的颜色诡异的变白了，暂不使用！//TODO
public class TimePickDiaAct extends Activity{
    DatePicker dp;
    TimePicker tp;
    TextView titleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_timepickdia);//dia_date_time_pick
        findViews();
    }
    
    private void findViews(){
        titleView = (TextView) findViewById(R.id.textViewTitle);
        titleView.setText("时间选择");
        dp = (DatePicker) findViewById(R.id.date_pick);
        tp = (TimePicker) findViewById(R.id.time_pick);
    }
    
    public void Back(View v){
        finish();
    }
    
    public void Sure(View v){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date d = new Date(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
        String str = formatter.format(d);
        Intent data = new Intent();
        data.putExtra("timestr", str);
        data.putExtra("timepoint", d.getTime());
        setResult(RESULT_OK, data);
        finish();
    }
}