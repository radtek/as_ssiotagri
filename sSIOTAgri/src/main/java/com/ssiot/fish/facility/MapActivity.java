package com.ssiot.fish.facility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.model.LatLng;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;

public class MapActivity extends HeadActivity{
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    
    BitmapDescriptor bdA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        SDKInitializer.initialize(getApplicationContext());
        
        if (intent.hasExtra("x") && intent.hasExtra("y")) {
            // 当用intent参数时，设置中心点为指定点
            Bundle b = intent.getExtras();
            LatLng p = new LatLng(b.getFloat("y"), b.getFloat("x"));
            mMapView = new MapView(this,
                    new BaiduMapOptions().mapStatus(new MapStatus.Builder()
                            .target(p).build()));
        } else {
            mMapView = new MapView(this, new BaiduMapOptions());
        }
        setContentView(mMapView);
        mBaiduMap = mMapView.getMap();
        
        if (intent.hasExtra("locations")){
            float[] floats = intent.getFloatArrayExtra("locations");
            if (floats.length % 2 == 0){
                initOverlay(floats);
            }
        }
    }
    
    private void initOverlay(float[] floats){
        for (int i = 0; i < floats.length; i = i + 2){
            LatLng llA = new LatLng(floats[i + 1], floats[i]);
            bdA = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_gcoding);
            MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA)
                    .zIndex(9).draggable(true);
//            ooA.animateType(MarkerAnimateType.drop);
            mBaiduMap.addOverlay(ooA);
        }
    }
    
//    private void test(){
//        mMapView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                AlertDialog.Builder b = new AlertDialog.Builder(MapActivity.this);
//                b.setMessage("正在寻找附近的，已找到1个");
//                b.create().show();
//            }
//        }, 5000);
//    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();
        bdA.recycle();
    }
}