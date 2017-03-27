package com.ssiot.remote.yun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.ssiot.agri.R;

import java.util.Date;

public class WheelValAct extends Activity{
    private static final String tag = "WheelValAct";
    NumberPicker hourPicker;
    NumberPicker minutePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wheel);
        findViews();
    }
    
    private void findViews(){
        hourPicker = (NumberPicker) findViewById(R.id.hourpicker);
        minutePicker = (NumberPicker) findViewById(R.id.minuteicker);
        hourPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                String tmpStr = String.valueOf(value);
                if (value < 10) {
                    tmpStr = "0" + tmpStr;
                }
                return tmpStr;
            }
        });
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                Toast.makeText(WheelValAct.this, "原来的值 " + oldVal + "--新值: "
//                                + newVal, Toast.LENGTH_SHORT).show();
            }
        });
        
        hourPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                // TODO Auto-generated method stub
                
            }
        });
        hourPicker.setMaxValue(24);
        hourPicker.setMinValue(0);
        hourPicker.setValue(9);
        
        minutePicker.setMaxValue(60);
        minutePicker.setMinValue(0);
        minutePicker.setValue(49);
        //TODO
    }
    
    public void Back(View v){
        finish();
    }
    
    public void Sure(View v){
        int hour = hourPicker.getValue();
        int minute = minutePicker.getValue();
        Log.v(tag, "----hour:" + hour+ " minute:" + minute);
        Intent data = new Intent();
        data.putExtra("time", hour * 60 + minute);
        Date date = new Date();
        date.setHours(hour);
        date.setMinutes(minute);
        date.setSeconds(0);
        data.putExtra("timepoint", date.getTime());
        setResult(RESULT_OK, data);
        finish();
    }
    
}