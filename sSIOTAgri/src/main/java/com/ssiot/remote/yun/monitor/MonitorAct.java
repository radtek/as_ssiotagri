package com.ssiot.remote.yun.monitor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.detail.FarmDetailPagerActivity;
import com.ssiot.remote.yun.monitor.MoniNodeAdapter.MyItemClickListener;
import com.ssiot.remote.yun.webapi.WS_API;
import com.ssiot.remote.yun.webapi.WS_MQTT;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MonitorAct extends HeadActivity{
    private static final String tag = "MonitorAct";
    ImageView homeView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    List<YunNodeModel> mDatas = new ArrayList<YunNodeModel>();
    int deviceversion = 2;
    GetIPCStatusThread mIpcStatusThread;
    View mapBtn;
    
    private static final int MSG_GET_END = 1;
    private static final int MSG_MQTT_GET = WS_MQTT.MSG_MQTT_GET;
    private static final int MSG_IPC_STATUS_GET = 2;
    private static final int MSG_NOTIFY = 3;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    if (null != mDatas && mDatas.size() > 0){
                    	for (int i = 0; i < mDatas.size(); i ++){
                    		YunNodeModel tmp = mDatas.get(i);
                    		if (tmp.devver == 3){
                    			startGetStates_OneNode(mDatas.get(i), mHandler);//20170224改为每个节点判断设备版本
                    		}
                    	}
//                        startGetStates(mDatas, mHandler);
                    }
                    break;
                case MSG_MQTT_GET:
//                    String str = (String) msg.obj;
//                    parseJSON(str, mDatas);
                	
//                    mAdapter.notifyDataSetChanged();
//                    break;
                case MSG_IPC_STATUS_GET:
                	removeMessages(MSG_MQTT_GET);//可能很多次触发刷新导致anr
                	removeMessages(MSG_IPC_STATUS_GET);//可能很多次触发刷新导致anr
                	mAdapter.notifyDataSetChanged();
                	break;
                case MSG_NOTIFY:
                	mAdapter.notifyDataSetChanged();
                	break;

                default:
                    break;
            }
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_farm);
        deviceversion = Utils.getIntPref(Utils.PREF_USERDEVICETYPE, MonitorAct.this);
        Log.v(tag, "-----------------------deviceversion" + deviceversion);
        findViews();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
//    	if (null != mDatas && mDatas.size() > 0 && deviceversion == 3){
//            startGetStates(mDatas, mHandler);//onresume时主动刷新？？？
//        }
    	mRecyclerView.post(runnable);
    	
    }
    
    @Override
    protected void onPause() {
    	mRecyclerView.removeCallbacks(runnable);
    	if (null != mIpcStatusThread){
    		mIpcStatusThread.cancle();
    	}
    	super.onPause();
    }
    
    Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (null != mDatas && mDatas.size() > 0 && Utils.isNetworkConnected(MonitorAct.this)){
				for (int i = 0; i < mDatas.size(); i++) {
					YunNodeModel tmp = mDatas.get(i);
					if (tmp.devver == 3) {
						startGetStates_OneNode(mDatas.get(i), mHandler);// 20170224改为每个节点判断设备版本
					}
				}
	        }
			mRecyclerView.postDelayed(runnable, 30000L);
		}
	};
    
    private void findViews(){
        homeView = (ImageView) findViewById(R.id.imageViewMore);
        homeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                home();
            }
        });
        mapBtn = (ImageView) findViewById(R.id.btn_map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MonitorAct.this, BaiduMapAct.class);
//				i.putExtra("latlngarray", value);
				startActivity(i);
			}
		});
        
        initList();
    }
    
    private void initList(){
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipelist_layout);
        mRecyclerView = ((RecyclerView)LayoutInflater.from(this).inflate(R.layout.vertical_recycler_view, null));
        Log.v(tag, "-----inflate mRecyclerView ok--");
        swipeRefreshLayout.addView(mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager bLinearLayoutManager = new LinearLayoutManager(this);
        bLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(bLinearLayoutManager);
//        mRecyclerView.addItemDecoration(new ItemDivider(CompanyListActivity.this, R.drawable.myshape));
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
    
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetYunFarmThread().start();
            }
        });
        mRecyclerView.setOnTouchListener(// http://www.tuicool.com/articles/IvQvyaN 有时滑动崩溃BUG
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
//                    if (mIsRefreshing) {//TODO TODO TODO 
//                        return true;
//                    } else {
                        return false;
//                    }
                }
            });
        mAdapter = new MoniNodeAdapter(this,mDatas, listen);
        mRecyclerView.setAdapter(mAdapter);
        
        new GetYunFarmThread().start();
    }
    
    MyItemClickListener listen = new MyItemClickListener() {
        @Override
        public void onMyItemClicked(YunNodeModel clickedYunModel) {
            Intent intent = new Intent(MonitorAct.this, FarmDetailPagerActivity.class);
            
            ArrayList<YunNodeModel> batchs = new ArrayList<YunNodeModel>();
            for (int i = 0; i < mDatas.size(); i ++){
                YunNodeModel tmp = mDatas.get(i);
                if (clickedYunModel.mFacilityID <= 0){//旧设备兼容显示
                    if (tmp.mNodeNo == clickedYunModel.mNodeNo){
                        batchs.add(tmp);
                    }
                } else {
                    if (tmp.mFacilityID == clickedYunModel.mFacilityID){// 相同facility id的都放进去
                        batchs.add(tmp);
                    }
                }
            }
            intent.putExtra("yunnodemodels", batchs);
            startActivity(intent);
        }
    };
    
    private class GetYunFarmThread extends Thread{
        @Override
        public void run() {
//            mDatas.clear();
//            mHandler.sendEmptyMessage(MSG_NOTIFY);
            int areaid = Utils.getIntPref(Utils.PREF_AREAID, MonitorAct.this);
            String account = Utils.getStrPref(Utils.PREF_USERNAME, MonitorAct.this);
            List<YunNodeModel> list;
//            if (Utils.getIntPref(Utils.PREF_USERDEVICETYPE, MonitorAct.this) != 3){//2.0设备兼容显示
//                list = DataAPI.getYunNodeModels(areaid);
//            } else {//3.0设备
//                list = DataAPI.getYun3NodeModels(areaid);
//            }
            list = new WS_API().GetFirstPage(account,deviceversion);
            if (null != list){
            	mDatas.clear();//clear和add放在一起才避免了IndexOutOfBoundsException问题
                mDatas.addAll(list);
            }
            mHandler.sendEmptyMessage(MSG_GET_END);
            if (null != mIpcStatusThread){
            	mIpcStatusThread.cancle();
            	mIpcStatusThread = null;
            }
            mIpcStatusThread = new GetIPCStatusThread();
            mIpcStatusThread.start();
            
        }
    }
    
    private class GetIPCStatusThread extends Thread{
    	private boolean cancle = false;
    	@Override
    	public void run() {
    		for (YunNodeModel y : mDatas){
    			if (y.nodeType == DeviceBean.TYPE_CAMERA){
    				if (y.list != null && y.list.size() > 0){
    					for (DeviceBean d : y.list){
    						if (cancle){
    							return;
    						} else {
    							d.vlcModel = new WS_API().GetIPCByID(d.videoID);
    							Socket socket = new Socket();
    		                    if (null == d.vlcModel){
    		                        Log.e(tag, "!!!!!!!!!!!!vlcmodel = null");
    		                        continue;
    		                    }
    		                    try {
    		                    	if (d.vlcModel._ssiotezviz){
    		                    		d.status = 0;
        		                        mHandler.sendEmptyMessage(MSG_IPC_STATUS_GET);//萤石云全都显示在线
        		                        continue;
    		                    	}
    		                        int port = d.vlcModel._tcpport;
    		                        if (port == 0){
    		                            port = Integer.parseInt(d.vlcModel._port);
    		                        }
    		                        Log.v(tag, "-----start to test " + d.vlcModel._ip +  port + "  tcpport:"+d.vlcModel._tcpport);
    		                        SocketAddress socketAddress = new InetSocketAddress(d.vlcModel._ip, port);
    		                        socket.connect(socketAddress, 2000);
    		                        d.status = 0;
    		                        mHandler.sendEmptyMessage(MSG_IPC_STATUS_GET);
//    		                        Message msg = mHandler.obtainMessage();
//    		                        msg.what = MSG_ACCESS_OK;
//    		                        msg.arg1 = i;
//    		                        mHandler.sendMessage(msg);
    		                    } catch (Exception e) {
    		                    	Log.e(tag, "-------摄像头端口不通" + d.vlcModel._ip);
//    		                        e.printStackTrace();
//    		                        Message msg = mHandler.obtainMessage();
//    		                        msg.what = MSG_ACCESS_FAIL;
//    		                        msg.arg1 = i;
//    		                        mHandler.sendMessage(msg);
    		                    }
    		                    try {
    		                        socket.close();
    		                    } catch (Exception e) {
    		                        e.printStackTrace();
    		                    }
    						}
    					}
    				}
    			}
    		}
    	}
    	
    	private void cancle(){
    		cancle = true;
    	}
    }
    
    
    
}