package com.ssiot.fish.facility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;

public class GetLocationActivity extends HeadActivity {
    private static final String tag = "GetLocationActivity";

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    boolean isFirstLoc = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.mapview);
//        mMapView = new MapView(this, new BaiduMapOptions());
        mBaiduMap = mMapView.getMap();
        LatLng cenpt = new LatLng(30, 120);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
        .target(cenpt)
        .zoom(14)
        .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        InitLocation();
        completeLis();
        initTitleView();
    }
    
    private void initTitleView(){
        initTitleLeft(R.id.title_bar_left);
        TextView mRightView = (TextView) findViewById(R.id.title_bar_right);
        mRightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latlng = mBaiduMap.getMapStatus().target;
                Intent resultIntent = new Intent();
                resultIntent.putExtra("resultx", (float) latlng.longitude);
                resultIntent.putExtra("resulty", (float) latlng.latitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private void InitLocation(){
        LocationClient mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(new MyLocationListener());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }
    
    /**
     * 实现实位回调监听
     */
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
         // map view 销毁后不在处理新接收的位置
            
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                Log.v(tag, "定位完成" + ll.latitude + " " + ll.longitude);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }
    
    public void completeLis(){
        mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
            
            @Override
            public void onMapLoaded() {
//                Toast.makeText(GetLocationActivity.this, "加载完成", Toast.LENGTH_SHORT).show();
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
            
            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                // TODO Auto-generated method stub
                LatLng latlng = mBaiduMap.getMapStatus().target;
                System.out.println("*****************lat = " + latlng.latitude);
                System.out.println("*****************lng = " + latlng.longitude);
            }
            
            @Override
            public void onMapStatusChange(MapStatus arg0) {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
}