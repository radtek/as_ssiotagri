package com.ssiot.remote.yun.sta;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.unit.ViewPagerAdapter;
import com.ssiot.remote.yun.webapi.WS_API;

import java.util.ArrayList;
import java.util.List;

public class StatisticsAct extends HeadActivity{
    private static final String tag = "StatisticsAct";
    int deviceversion = 2;
    
    private ArrayList<YunNodeModel> mYModels = new ArrayList<YunNodeModel>();//保存为了下一个界面用
    ViewPager mPager;
    RadioGroup staGroup;
    RadioButton deviceButton;
    RadioButton exeButton;
    ArrayList<View> mList;
    ViewPagerAdapter mPagerAdapter;
    GridView staGridView;
    StaAdapter mStaAdapter;
    ArrayList<DeviceBean> mDatas = new ArrayList<DeviceBean>();
    Button btn_composite_chart;
    
    private static final int MSG_GET_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    if (mStaAdapter != null){
                        mStaAdapter.notifyDataSetChanged();
                    }
                    break;

                default:
                    break;
            }
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceversion = Utils.getIntPref(Utils.PREF_USERDEVICETYPE, StatisticsAct.this);
        initView(savedInstanceState);
    }
    
    protected void initView(Bundle bundle) {
        setContentView(R.layout.statistics);
        btn_composite_chart = (Button) findViewById(R.id.btn_composite_chart);
        btn_composite_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatisticsAct.this, CompositeChartAct.class);
                i.putExtra("yunnodemodels", mYModels);
                startActivity(i);
            }
        });
        initPageAndGroup();
        setTitle("报表中心");
        setData();
    }
    
    private void initPageAndGroup() {
        mPager = ((ViewPager) findViewById(R.id.PagerSta));
        staGroup = ((RadioGroup) findViewById(R.id.RadioGroupSta));
        deviceButton = ((RadioButton) findViewById(R.id.RadioButtonDevice));
        exeButton = ((RadioButton) findViewById(R.id.RadioButtonExe));
        staGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.RadioButtonDevice:
                        mPager.setCurrentItem(0);
                        break;
                    case R.id.RadioButtonExe:
                        mPager.setCurrentItem(1);
                        break;
                }
            }
        });
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {// 0
                    switch (mPager.getCurrentItem()) {
                        case 0:
                            deviceButton.setChecked(true);
                            break;
                        case 1:
                            exeButton.setChecked(true);
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float paramFloat, int paramInt2) {
            }

            @Override
            public void onPageSelected(int position) {
            }
        });
        mList = new ArrayList<View>();
        mPagerAdapter = new ViewPagerAdapter(mList);
        mPager.setAdapter(mPagerAdapter);
    }
    
    private void initViewPage(ArrayList<DeviceBean> paramArrayList) {
        mStaAdapter = new StaAdapter(paramArrayList, this);
        staGridView.setAdapter(mStaAdapter);
        staGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StatisticsAct.this, StaLineAct.class);
                int type = mDatas.get(position).mDeviceTypeNo;
                if (type == 1281 || type == 772){//光照度 or 雨量
                    intent = new Intent(StatisticsAct.this, StaBarAct.class);
                } else if (type == 773){//风向
                    intent = new Intent(StatisticsAct.this, StaWindOraAct.class);
                }
                intent.putExtra("yunnodemodels", mYModels);
                intent.putExtra("devicebean", mDatas.get(position));
                startActivity(intent);
            }
        });
    }

    
    private void initGrid(GridView gridView) {
        gridView.setNumColumns(3);
        gridView.setBackgroundColor(getResources().getColor(R.color.white));
//      setListSelector(gridView);
    }
    
    private void setData(){
        staGridView = new GridView(this);
        
        initGrid(staGridView);
        mList.add(staGridView);
        mPagerAdapter.notifyDataSetChanged();
        initViewPage(mDatas);
        new GetSensorModelThread().start();
    }
    
    private class GetSensorModelThread extends Thread{
        @Override
        public void run() {//TODO 三代 二代
            mDatas.clear();
            int areaid = Utils.getIntPref(Utils.PREF_AREAID, StatisticsAct.this);
            String account = Utils.getStrPref(Utils.PREF_USERNAME, StatisticsAct.this);
            List<YunNodeModel> list;
//            if ( != 3){//2.0设备兼容显示
//                list = DataAPI.getYunNodeModels(areaid);
//            } else {//3.0设备
//                list = DataAPI.getYun3NodeModels(areaid);
//            }
            list = new WS_API().GetFirstPage(account, deviceversion);
            if (list != null){
                mYModels.clear();
                mYModels.addAll(list);
                for (YunNodeModel y : list){
                    if (y.nodeType == DeviceBean.TYPE_SENSOR && y.list != null){
                        for (DeviceBean d : y.list){
                            if (!sensorExists(mDatas, d.mDeviceTypeNo)){
                                mDatas.add(d);
                            }
                        }
                    }
                }
            }
            mHandler.sendEmptyMessage(MSG_GET_END);
        }
    }
    
    private boolean sensorExists(List<DeviceBean> list, int sensorType){
        for (DeviceBean d : list){
            if (d.mDeviceTypeNo == sensorType){
                return true;
            }
        }
        return false;
    }
}