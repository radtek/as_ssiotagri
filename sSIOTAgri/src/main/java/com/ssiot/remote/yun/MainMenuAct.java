package com.ssiot.remote.yun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.yun.monitor.MonitorAct;
import com.ssiot.remote.yun.sta.StatisticsAct;

//主界面 TODO com.tcloud.fruitfarm.MainMenuAct
public class MainMenuAct extends HeadActivity{
    LinearLayout monitorLayout;
    LinearLayout topLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initView();
    }
    
    private void initView(){
        setContentView(R.layout.farm);
        monitorLayout = (LinearLayout) findViewById(R.id.LinearLayoutMonitor);
        topLayout = (LinearLayout) findViewById(R.id.LinearLayoutTop);
    }
    
    public void Monitor(View v) {
        startActivity(new Intent(this, MonitorAct.class));
    }
    
    public void Sta(View v){
      startActivity(new Intent(this, StatisticsAct.class));
    }
}