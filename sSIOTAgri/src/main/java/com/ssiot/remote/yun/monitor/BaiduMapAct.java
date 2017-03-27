package com.ssiot.remote.yun.monitor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.inner.Point;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.webapi.WS_API;

/**
 * 演示覆盖物的用法
 */
public class BaiduMapAct extends HeadActivity {

	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	ArrayList<LatLng> mLatLngs = new ArrayList<LatLng>();//节点的坐标
	ArrayList<String> mNames = new ArrayList<String>();//节点名称
	ArrayList<Marker> mMarkers = new ArrayList<Marker>();
	private InfoWindow mInfoWindow;
	
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());//百度
		setContentView(R.layout.activity_overlay);
		bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);//init之后才能做
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
		initOverlay(mLatLngs);
//		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
//			public boolean onMarkerClick(final Marker marker) {
//				Button button = new Button(getApplicationContext());
//				button.setBackgroundResource(R.drawable.popup);
//				for (Marker m : mMarkers){
//					if (m == marker){
//						
//					}
//				}
//				button.setText("节点名称");
//				LatLng ll = marker.getPosition();
//				mInfoWindow = new InfoWindow(button, ll, -47);
//				mBaiduMap.showInfoWindow(mInfoWindow);
//				return true;
//			}
//		});
		new GetLatLngThread().start();
	}

	public void initOverlay(ArrayList<LatLng> latlngs) {
		mMarkers.clear();
		LatLng cenpt = new LatLng(30.0, 120.0);
		for (int i = 0; i < latlngs.size(); i ++){
			LatLng ll = latlngs.get(i);
			if (ll.latitude * ll.longitude == 0){
				continue;
			}
			BitmapDescriptor bd2 = BitmapDescriptorFactory.fromBitmap(createBitmap(mNames.get(i)));
			MarkerOptions ooA = new MarkerOptions().position(ll).icon(bd2)//.title("xxxxxxxxxx")//title 无效？？
					.zIndex(9).draggable(true);
//			TextOptions ooText = new TextOptions().position(ll).text(mNames.get(i)).zIndex(9);
			Marker m = (Marker) (mBaiduMap.addOverlay(ooA));
//			mBaiduMap.addOverlay(ooText);
			mMarkers.add(m);
			
//			Button button = new Button(getApplicationContext());
//			button.setBackgroundResource(R.drawable.popup);
//			button.setTextColor(getResources().getColor(R.color.black));
//			button.setText(mNames.get(i));
//			InfoWindow infoWindow = new InfoWindow(button, ll, -47);
//			mBaiduMap.showInfoWindow(infoWindow);
			cenpt = ll;//地图中心点
		}
		MapStatus ms = new MapStatus.Builder().target(cenpt).zoom(14).build();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(ms));

//		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
//			public void onMarkerDrag(Marker marker) {
//			}
//
//			public void onMarkerDragEnd(Marker marker) {
//				Toast.makeText(
//						BaiduMapAct.this,
//						"拖拽结束，新位置：" + marker.getPosition().latitude + ", "
//								+ marker.getPosition().longitude,
//						Toast.LENGTH_LONG).show();
//			}
//
//			public void onMarkerDragStart(Marker marker) {
//			}
//		});
	}
	
	private class GetLatLngThread extends Thread{
		@Override
		public void run() {
			mLatLngs.clear();
			mNames.clear();
			mMarkers.clear();
			String account = Utils.getStrPref(Utils.PREF_USERNAME, BaiduMapAct.this);
			List<YunNodeModel> nodes = new WS_API().GetFirstPageShort(account, 2);
			if (null != nodes){
				for (YunNodeModel y : nodes){
					LatLng ll = new LatLng(y.latitude, y.longitude);
					mLatLngs.add(ll);
					mNames.add(y.nodeStr);
				}
			}
			runOnUiThread( new Runnable() {
				public void run() {
					initOverlay(mLatLngs);
				}
			});
		}
	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.onDestroy();
		super.onDestroy();
		// 回收 bitmap 资源
		bd.recycle();
	}
	
	private Bitmap createBitmap(String letter) {
		int width = 180;
		int height = 180;
		Bitmap imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(imgTemp);
		Paint paint = new Paint(); // 建立画笔
		paint.setDither(true);
		paint.setFilterBitmap(true);
		
		Bitmap imgMarker = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_gcoding);
		
		Rect src = new Rect(0, 0, width, height);
		Rect dst = new Rect(0, 40, width, 40 + height);
		canvas.drawBitmap(imgMarker, src, dst, paint);

		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
//		textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度
		textPaint.setColor(Color.BLACK);
		// 调整字体在图片中的位置，此处根据分辨率来判断字体的大小了
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int scrHeigh = dm.heightPixels;
//		if (scrHeigh > 1280) {
			textPaint.setTextSize(32.0f);
			canvas.drawText(String.valueOf(letter), 0, 32, textPaint);
//			canvas.drawText(String.valueOf(letter), width / 2 - 35,
//					height / 3 - 26, textPaint);
//		} else {
//			textPaint.setTextSize(25.0f);
//			canvas.drawText(String.valueOf(letter), width / 2 - 27,
//					height / 3 - 20, textPaint);
//		}
		canvas.save();
		canvas.restore();
		BitmapDrawable pic = new BitmapDrawable(getResources(), imgTemp);

		return pic.getBitmap();
	}

}