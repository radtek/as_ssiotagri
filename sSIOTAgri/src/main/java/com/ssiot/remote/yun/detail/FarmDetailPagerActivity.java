package com.ssiot.remote.yun.detail;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.WaterDetailModel;
import com.ssiot.remote.data.model.view.SensorThresholdModel;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.webapi.WS_API;
import com.ssiot.remote.yun.webapi.WS_Fish;
import com.ssiot.remote.yun.webapi.WS_MQTT;
import com.ssiot.remote.yun.webapi.WS_WaterQuality;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FarmDetailPagerActivity extends HeadActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static final String tag = "FarmDetailPagerActivity";

    ViewPager pager;
    final List<DevicesFragment> fragments = new ArrayList(3);// DevicesFragment
    RadioGroup tabs;
    View indicator;
    RadioButton sensorsTab;
    RadioButton cntrolsTab;
    RadioButton webcamsTab;
    final ImplementationAdapter pagerAdapter = new ImplementationAdapter(getSupportFragmentManager());
    final DevicesFragment sensorsFragment = new SensorsFragment();
    final DevicesFragment cntrolsFragment = new CtrlersFragment();
    final DevicesFragment webcamsFragment = new WebcamsFragment();

    volatile transient long mLastRefreshedTimeMS = System.currentTimeMillis();
    private volatile transient AsyncTask<?, ?, ?> lastTask;
    private int _OrgID;//组织的id，在此理解为设施id
    ArrayList<YunNodeModel> mYunNodes;
    ArrayList<YunNodeModel> sensorNodes = new ArrayList<YunNodeModel>();
    ArrayList<YunNodeModel> cnNodes = new ArrayList<YunNodeModel>();
    ArrayList<YunNodeModel> cameraNodes = new ArrayList<YunNodeModel>();
//    int deviceversion = 2;
    
//    private static final int MSG_GET_END = 1;
    private static final int MSG_MQTT_GET = WS_MQTT.MSG_MQTT_GET;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
//                case MSG_GET_END:
//                    mAdapter.notifyDataSetChanged();
//                    swipeRefreshLayout.setRefreshing(false);
//                    startGetStates();
//                    break;
                case MSG_MQTT_GET:
//                    String str = (String) msg.obj;
//                    parseJSON(str, mYunNodes);
                    sensorsFragment.updateUI();
                    cntrolsFragment.updateUI();
                    webcamsFragment.updateUI();
//                    for (int i = 0; i < mYunNodes.size(); i ++){
//                    	Log.v(tag, "~~~~~" +mYunNodes.get(i).dumpAllDeviceBeans());
//                    }
//                    for (int i = 0; i < cnNodes.size(); i ++){
//                    	Log.v(tag, "~~~~cnnodes~" +cnNodes.get(i).dumpAllDeviceBeans());
//                    }
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_monitor_detail_pager);
        mYunNodes = (ArrayList<YunNodeModel>) getIntent().getSerializableExtra("yunnodemodels");
        if (null != mYunNodes){
            if (mYunNodes.size() > 0 && !TextUtils.isEmpty(mYunNodes.get(0).facilityStr)){
                getSupportActionBar().setTitle(mYunNodes.get(0).facilityStr);
            }
            for (int i = 0; i < mYunNodes.size(); i ++){
                switch (mYunNodes.get(i).nodeType) {
                    case DeviceBean.TYPE_SENSOR:
                        sensorNodes.add(mYunNodes.get(i));
                        break;
                    case DeviceBean.TYPE_ACTUATOR:
                        cnNodes.add(mYunNodes.get(i));
                        break;
                    case DeviceBean.TYPE_CAMERA:
                        cameraNodes.add(mYunNodes.get(i));
                        break;
                    default:
                        break;
                }
            }
        } else {
            Log.e(tag, "-------!!!!--------yunodes=null");
        }
        
        findViews();
        sensorsFragment.setData(sensorNodes);
        cntrolsFragment.setData(cnNodes);
        webcamsFragment.setData(cameraNodes);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        pager.removeCallbacks(runnable);
        pager.postDelayed(runnable, 30000L);//定时刷新
        onRefresh();
        Log.v(tag, "-------onresume end----");
    }
    
    Runnable runnable = new Runnable() {
		@Override
		public void run() {
			long l = System.currentTimeMillis() - mLastRefreshedTimeMS;
            if (l > 30000L) {// 30秒自动刷新
            	internalRefresh();
                Log.v(tag, "---------refresh--------");
//                if (deviceversion == 3){
//                    startGetStatesContinously(mYunNodes,mHandler);
//                }
                pager.removeCallbacks(this);
                pager.postDelayed(this, 30000L);
            } else {
                pager.removeCallbacks(this);
                pager.postDelayed(this, 30000L - l);
            }
		}
	};
    
    protected void onStart() {
      super.onStart();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        pager.removeCallbacks(runnable);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pager.removeCallbacks(runnable);
    }

    private void findViews() {
        tabs = (RadioGroup) findViewById(R.id.tabs);
        sensorsTab = (RadioButton) findViewById(R.id.tcagri_farm_detail_tab_sensors);
        cntrolsTab = (RadioButton) findViewById(R.id.tcagri_farm_detail_tab_cntrols);
        webcamsTab = (RadioButton) findViewById(R.id.tcagri_farm_detail_tab_webcams);
        indicator = findViewById(R.id.indicator);
        
        if (!fragments.contains(sensorsFragment) && null != sensorNodes && sensorNodes.size() > 0) {
            sensorsTab.setVisibility(View.VISIBLE);
            fragments.add(sensorsFragment);
        } else {
            sensorsTab.setVisibility(View.GONE);
        }
        if (!fragments.contains(cntrolsFragment) && null != cnNodes && cnNodes.size() > 0) {
            cntrolsTab.setVisibility(View.VISIBLE);
            fragments.add(cntrolsFragment);
        } else {
            cntrolsTab.setVisibility(View.GONE);
        }
        if (!fragments.contains(webcamsFragment) && null != cameraNodes && cameraNodes.size() > 0) {
            webcamsTab.setVisibility(View.VISIBLE);
            fragments.add(webcamsFragment);
        } else {
            webcamsTab.setVisibility(View.GONE);
        }
        
//        if (null != sensorNodes && sensorNodes.size() > 0){
//        } else {
//            sensorsTab.setVisibility(View.GONE);
//        }
//        if (null != cnNodes && cnNodes.size() > 0){
//        } else {
//            cntrolsTab.setVisibility(View.GONE);
//        }
//        if (null != cameraNodes && cameraNodes.size() > 0){
//        } else {
//            webcamsTab.setVisibility(View.GONE);
//        }
        pager = (ViewPager) findViewById(R.id.myViewPager);

        tabs.setOnCheckedChangeListener(pagerAdapter);
        pager.addOnPageChangeListener(pagerAdapter);
        pager.setAdapter(pagerAdapter);

        // findViewById(R.id.FrameLayoutWarn).setOnClickListener(this.adapter);
        // findViewById(R.id.toOldLayout).setOnClickListener(new
        // View.OnClickListener() {
        // public void onClick(View paramAnonymousView) {
        // startActivity(getIntent()
        // .setClass(FarmDetailPagerActivity.this, FarmDetailed.class));
        // finish();
        // }
        // });
    }
    
    @Override
    public void onRefresh() {
        Log.v(tag, "----onRefresh----fragSize:" + fragments.size());// ?
        internalRefresh();
        Iterator localIterator = fragments.iterator();
        while (localIterator.hasNext()) {
            ((DevicesFragment) localIterator.next()).setRefreshing(true);
        }
    }

    private final void internalRefresh() {// 开启 异步线程Task
        mLastRefreshedTimeMS = System.currentTimeMillis();
        if (this.lastTask != null) {
            lastTask.cancel(true);
        }
        NetworkTask localNetworkTask = new NetworkTask();
        Integer[] arrayOfInteger = new Integer[1];
        arrayOfInteger[0] = Integer.valueOf(_OrgID);
        lastTask = localNetworkTask.execute(arrayOfInteger);
        mLastRefreshedTimeMS = System.currentTimeMillis();
    }

    private final class ImplementationAdapter extends FragmentPagerAdapter
            implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener{// PopupMenu.OnMenuItemClickListener

        public ImplementationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public DevicesFragment getItem(int paramInt) {
            return (DevicesFragment) fragments.get(paramInt);
        }

        @Override
        public long getItemId(int paramInt) {
            return getItem(paramInt).getTabID();
        }

        @Override
        public void onCheckedChanged(RadioGroup paramRadioGroup, int paramInt) {
            for (int i = 0;; i++) {
                if (i >= fragments.size()) {
                    return;
                }
                if (((DevicesFragment) fragments.get(i)).getTabID() == paramInt) {
                    pager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int paramInt) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (fragments.size() >= 1) {
                DevicesFragment localDevicesFragment = (DevicesFragment) fragments.get(position);
                View radioBtn = tabs.findViewById(localDevicesFragment.getTabID());
                ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams) indicator
                        .getLayoutParams();
                localMarginLayoutParams.width = (radioBtn.getRight() - radioBtn.getLeft());
                localMarginLayoutParams.leftMargin = ((int) (positionOffset * localMarginLayoutParams.width) + radioBtn
                        .getLeft());
                indicator.setLayoutParams(localMarginLayoutParams);
            }
        }

        @Override
        public void onPageSelected(int paramInt) {
            tabs.check((int) getItemId(paramInt));
        }
    }
    
    private final void updateIndicatorWidth() {//一开始tab就隐藏会导致获取不到宽度
        int i = 0;
        if (sensorsTab.getVisibility() == View.VISIBLE) {
            i = sensorsTab.getMeasuredWidth();
        } else if (cntrolsTab.getVisibility() == View.VISIBLE) {
            i = cntrolsTab.getMeasuredWidth();
        } else if (webcamsTab.getVisibility() == ViewGroup.VISIBLE) {
            i = webcamsTab.getMeasuredWidth();
        }
        ViewGroup.LayoutParams localLayoutParams = indicator.getLayoutParams();
        localLayoutParams.width = i;
        Log.v(tag, "-------updateIndicatorWidth---------" + i);
        indicator.setLayoutParams(localLayoutParams);
    }

    private final class NetworkTask extends AsyncTask<Integer, DeviceBean, JSONArray> {
        private final String tag = "FarmDetailPagerActivity-NetworkTask";

        NetworkTask() {
        }

        @Override
        protected JSONArray doInBackground(Integer[] paramArrayOfInteger) {
//            int facilityid = paramArrayOfInteger[0];
//            for (int i = 0; i < mYunNodes.size(); i ++){
//                YunNodeModel ym = mYunNodes.get(i);
//                for (int j = 0; j < ym.list.size(); j ++){
//                    DeviceBean d = ym.list.get(j);//TODO 每个devicebean都要 查询一下数据
//                }
//            }
            for (int j = 0; j < sensorNodes.size(); j ++){
                YunNodeModel sensorNodeM = sensorNodes.get(j);
                if (sensorNodeM.devver != 3){
//                	new WS_API().GetSensorNodeDatas_v2(sensorNodeM.mNodeUnique, sensorNodes);//TODO 二代产品怎么界面更新
                	new WS_API().GetSensorNodeDatasOneNode_v2(sensorNodeM);
                } else {
//                	startGetStatesContinously(mYunNodes,mHandler);//20161017先改为不循环获取的
                	startGetStates_OneNode(sensorNodeM, mHandler);
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.w("地块详情", "更新被取消！");
            sensorsFragment.totalDevices();
            cntrolsFragment.totalDevices();
            webcamsFragment.totalDevices();
            pagerAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(JSONArray paramJSONArray) {// 执行完毕了，in UI线程
            Log.v(tag, "-------onPostExecute---------paramJSONArray:" + paramJSONArray);
            
            pagerAdapter.notifyDataSetChanged();
            for (DevicesFragment d : fragments){
                d.setRefreshing(false);
                d.updateUI();
            }
//            if (deviceversion == 3){
//            	startGetStatesContinously(mYunNodes,mHandler);
//            }
            // indicator的更新
            updateIndicatorWidth();
        }

        @Override
        protected void onProgressUpdate(DeviceBean[] devices) {
            super.onProgressUpdate(devices);
            if (devices == null) {
                return;
            }
            //start test
            
            int i = devices.length;
            for (int j = 0; j < i; j++) {
                DeviceBean localDevice = devices[j];
                if (null != localDevice) {
                    if (localDevice.isSensorDevice()) {
                        sensorsFragment.add(localDevice);
                    } else if (localDevice.isCntrolDevice()) {
                        cntrolsFragment.add(localDevice);
                    } else if (localDevice.isWebcamDevice()) {
                        webcamsFragment.add(localDevice);
                    }
                }
            }
        }

    }
}
