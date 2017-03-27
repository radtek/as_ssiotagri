package com.ssiot.remote.yun.detail.cntrols;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.yun.detail.sensors.SensorCalibrationFrag;
import com.ssiot.remote.yun.detail.sensors.SensorLineChartFrag;
import com.ssiot.remote.yun.monitor.DeviceBean;

import java.util.ArrayList;

public class FarmDetailCtrlerPagerActivity extends HeadActivity{
    private static final String tag = "FarmDetailCtrlerPagerActivity";
    
    TextView value;
    ViewPager mPager;
    ArrayList<Fragment> fragmentArray;
    View indicater;
    RadioGroup tabs;
    DeviceBean mDeviceBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceBean = (DeviceBean) getIntent().getSerializableExtra("devicebean");
        getSupportActionBar().setTitle(mDeviceBean.mName);
        setContentView(R.layout.activity_farm_monitor_detail_ctr_pager);
        initViewPager();
        FarmDetailCtrlerOperateFragment localFarmDetailCtrlerOperateFragment = new FarmDetailCtrlerOperateFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentcontainer,localFarmDetailCtrlerOperateFragment).commit();
    }
    
    private void initViewPager() {
        tabs = (RadioGroup) findViewById(android.R.id.tabs);
        indicater = findViewById(R.id.indicator);
        mPager = (ViewPager) findViewById(android.R.id.tabcontent);
        
        tabs.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < tabs.getChildCount(); i++) {
                    if (checkedId == tabs.getChildAt(i).getId()) {
                        Log.v(tag, "----onCheckedChanged-----" + i);
                        mPager.setCurrentItem(i);
                    }
                }
            }
        });
        fragmentArray = new ArrayList<Fragment>();
        fragmentArray.add(new FarmDetailCtrlerLogFragment());
        fragmentArray.add(new FarmDetailCtrlerSettingsListFragment());
        mPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragmentArray));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
    
    
    public class FragmentAdapter extends FragmentPagerAdapter {
        //这个是存放四个Fragment的数组，待会从MainActivity中传过来就行了
        private ArrayList<Fragment> fragmentArray;
        
        //自己添加一个构造函数从MainActivity中接收这个Fragment数组
        public FragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragArray) {
            this(fm);
            fragmentArray = fragArray;
        }
        
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }
        
        //这个函数的作用是当切换到第arg0个页面的时候调用。
        @Override
        public Fragment getItem(int arg0) {
            return fragmentArray.get(arg0);
        }

        @Override
        public int getCount() {
            return fragmentArray.size();
        }
    }
    
    public class MyOnPageChangeListener implements OnPageChangeListener {
        
        @Override
        public void onPageSelected(int arg0) {
            Log.v(tag, "----onPageSelected----" + arg0);
            tabs.check(tabs.getChildAt(arg0).getId());
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            Log.v(tag, "----onPageScrolled----arg1:" + arg1 + " arg0:" + arg0 + " arg2:"+ arg2);
            if (fragmentArray.size() > 1){
                View localView = tabs.getChildAt(arg0);//tabs.findViewById((int)getItemId(arg0));
                ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams) indicater.getLayoutParams();
                localMarginLayoutParams.width = (localView.getRight() - localView.getLeft());
                localMarginLayoutParams.leftMargin = ((int)(arg1 * localMarginLayoutParams.width) + localView.getLeft());
                indicater.setLayoutParams(localMarginLayoutParams);
                indicater.setVisibility(View.VISIBLE);
                
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
}