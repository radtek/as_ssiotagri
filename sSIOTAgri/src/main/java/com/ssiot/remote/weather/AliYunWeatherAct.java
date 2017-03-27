package com.ssiot.remote.weather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.Text;
import com.ssiot.agri.R;
import com.ssiot.fish.FishMainActivity;
import com.ssiot.fish.HeadActivity;
import com.ssiot.remote.Utils;
import com.ssiot.remote.view.MoreLineView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AliYunWeatherAct extends HeadActivity{
	private static final String tag = "AliYunWeatherAct";
	String[] dayKeys = {"f1","f2","f3","f4","f5","f6"};//,"f7"

	boolean running = false;
	ImageView mRefreshView;
	TextView mLocationSelectView;
	TextView mCurTemp;
	TextView mTxtHigh;
	TextView mTxtLow;
	TextView mTxtTempDetail;
	TextView mCurWindOri;
	TextView mGetTime;
	LinearLayout mScrollContainer;
	LinearLayout mSevenContainer;
	WeatherChartView mChartView;
	SharedPreferences mPref;
	LayoutInflater inflater;// = LayoutInflater.from(this);

	private static final int MSG_GET_LOCATION_END = 1;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case MSG_GET_LOCATION_END:
					break;
				default:
					break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideActionBar();
		setContentView(R.layout.weather);
//		initState();
		setStatusBarAndTitleBarColor(R.color.weather_line_blue, 0);
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		inflater = LayoutInflater.from(this);
		initViews();
		startGet();
		animateRefreshView();
	}

	/*private void initState() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

			LinearLayout linear_bar = (LinearLayout) findViewById(R.id.ll_bar);
			linear_bar.setVisibility(View.VISIBLE);
			//获取到状态栏的高度
			int statusHeight = getStatusBarHeight();
			//动态的设置隐藏布局的高度
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linear_bar.getLayoutParams();
			params.height = statusHeight;
			linear_bar.setLayoutParams(params);
		}
	}*/
	
	private void initViews(){
		mRefreshView = (ImageView) findViewById(R.id.shuaxin);
		mLocationSelectView = (TextView) findViewById(R.id.liebiao);
		mCurTemp = (TextView) findViewById(R.id.cur_temp);
		mTxtHigh = (TextView) findViewById(R.id.temp_high);
		mTxtLow = (TextView) findViewById(R.id.temp_low);
		mTxtTempDetail = (TextView) findViewById(R.id.temp_detail);
		mCurWindOri = (TextView) findViewById(R.id.cur_wind_ori);
		mGetTime = (TextView) findViewById(R.id.get_time);
		mScrollContainer = (LinearLayout) findViewById(R.id.scroll_container);
		mSevenContainer = (LinearLayout) findViewById(R.id.sevenday_container);
		mChartView = (WeatherChartView) findViewById(R.id.chart_view);
		
		mRefreshView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!running) {
					SharedPreferences.Editor e = mPref.edit();
					e.putInt(Utils.PREF_WEATHER_TIME, 0);
					e.putString(Utils.PREF_WEATHER_DETAIL, "");
					e.putInt(Utils.PREF_WEATHER_TIME_HOUR, 0);
					e.putString(Utils.PREF_WEATHER_DETAIL_HOUR, "");
					e.apply();//先清除，确保下次是刷新的网络上的数据
					startGet();
					animateRefreshView();
				}
			}
		});
	}
	
	private void animateRefreshView(){
		final Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.loading_animation);
		mRefreshView.startAnimation(hyperspaceJumpAnimation);
		mRefreshView.postDelayed(new Runnable() {
			@Override
			public void run() {
//				hyperspaceJumpAnimation.cancel();
				mRefreshView.clearAnimation();
			}
		}, 3000);
	}

	private void parseJsonToUI(String strDay,String strHour){
		mRefreshView.clearAnimation();
		JSONObject jo = null;
		try {
			jo = new JSONObject(strDay);
			JSONObject bodyJson = jo.getJSONObject("showapi_res_body");
			String cityStr = bodyJson.getJSONObject("cityInfo").getString("c3");
			JSONObject f1Json = bodyJson.getJSONObject("f1");
			String tempNight = f1Json.getString("night_air_temperature");
			String tempDay = f1Json.getString("day_air_temperature");
			String weatherDay = f1Json.getString("day_weather");
			String weatherCode =  f1Json.getString("day_weather_code");

			JSONObject nowJson = bodyJson.getJSONObject("now");
			String airStr = nowJson.getString("aqi");//空气污染
			String nowTemp = nowJson.getString("temperature");
			String windOri = nowJson.getString("wind_direction");
			mLocationSelectView.setText(cityStr);
			mCurTemp.setText(nowTemp + "°");
			mTxtHigh.setText("高"+tempDay);
			mTxtLow.setText(" 低"+tempNight);
			mTxtTempDetail.setText(weatherDay);
			mCurWindOri.setText(windOri);
			String time = bodyJson.getString("time");//预报发布时间
			mGetTime.setText("发布时间："+getTimeStr(time));
			setSevenContainer(bodyJson);
			setHourScroller(strHour);
			setChart(bodyJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		running = false;
	}

	private void setHourScroller(String strHour){
		mScrollContainer.removeAllViews();
		try {
			JSONArray hourList = new JSONObject(strHour).getJSONObject("showapi_res_body").getJSONArray("hourList");
			for (int i = 0; i < hourList.length(); i ++){
				JSONObject jo = hourList.optJSONObject(i);
				View v = inflater.inflate(R.layout.itm_weather_hour, null, false);
				TextView hourText = (TextView) v.findViewById(R.id.hour_time);
				ImageView hourImg = (ImageView) v.findViewById(R.id.hour_icon);
				TextView hourTemp = (TextView) v.findViewById(R.id.hour_temp);
				hourText.setText(jo.getString("time").substring(8,10) + "时");
				hourImg.setImageResource(WeatherUtils.GetDayWeatherIcon(jo.getString("weather_code")));
				hourTemp.setText(jo.getString("temperature") + "°");
				mScrollContainer.addView(v);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.v(tag,"-----errorstrHour------"+strHour);
		}
	}

	private void setSevenContainer(JSONObject bodyJson){

		mSevenContainer.removeAllViews();
		for (int i = 0; i < dayKeys.length; i ++){
			try {
				JSONObject f = bodyJson.getJSONObject(dayKeys[i]);
				View v = inflater.inflate(R.layout.itm_weather_day, null, false);
				TextView weekTV = (TextView) v.findViewById(R.id.itm_weektxt);
				ImageView dayIcon = (ImageView) v.findViewById(R.id.itm_d_icon);
				ImageView nightIcon = (ImageView) v.findViewById(R.id.itm_n_icon);
				TextView windOriTV = (TextView) v.findViewById(R.id.itm_windori);
				TextView windTV = (TextView) v.findViewById(R.id.itm_wind);

				weekTV.setText("周"+ getWeekDayChinese(f.getString("weekday")));
				dayIcon.setImageResource(WeatherUtils.GetDayWeatherIcon(f.getString("day_weather_code")));
				nightIcon.setImageResource(WeatherUtils.GetNightWeatherIcon(f.getString("night_weather_code")));
				windOriTV.setText(f.getString("day_wind_direction"));
				windTV.setText(f.getString("day_wind_power"));//微风10m/h 风力编号
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
				if (i == 0){
					int color = Color.argb(16,0,0,0);
					v.setBackgroundColor(color);
				}
				mSevenContainer.addView(v,lp);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	private void setChart(JSONObject bodyJson){
		int[] dayTemps = new int[dayKeys.length];
		int[] nightTemps = new int[dayKeys.length];
		for (int i = 0; i < dayKeys.length; i ++){
			try {
				dayTemps[i] = bodyJson.getJSONObject(dayKeys[i]).getInt("day_air_temperature");
				nightTemps[i] = bodyJson.getJSONObject(dayKeys[i]).getInt("night_air_temperature");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		mChartView .setTempDay(dayTemps);
		mChartView .setTempNight(nightTemps);
		mChartView .invalidate();
	}

	private void startGet(){
		running = true;
		final LocationClient mLocationClient = new LocationClient(getApplicationContext());
		BDLocationListener bdlistener = new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (mLocationClient.isStarted()){
					mLocationClient.stop();//必须停止，要不然一直查询
				}
				new GetHourWeatherThread(""+location.getLatitude(), ""+location.getLongitude()).start();
			}
		};

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setScanSpan(1000);//每隔1秒发起一次定位
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		option.setOpenGps(true);//是否打开gps
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到该描述，不设置则在4G情况下会默认定位到“天安门广场”
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要，不设置则拿不到定位点的省市区信息
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setLocationNotify(true);
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(bdlistener);
		mLocationClient.start();
	}

	private class GetHourWeatherThread extends Thread{
		String lat = "";
		String lng = "";
		private GetHourWeatherThread(String latStr,String lngStr){
			lat = latStr;
			lng = lngStr;
		}

		@Override
		public void run() {
			String strDay = "";
			String strHour = "";
			if (isOutOfTime(Utils.PREF_WEATHER_TIME)){
				strDay = WeatherUtils.get7DayWeatherFromAliYun(lat, lng);
				if (!TextUtils.isEmpty(strDay)){
					SharedPreferences.Editor e = mPref.edit();
					e.putInt(Utils.PREF_WEATHER_TIME, (int) (System.currentTimeMillis()/1000));
					e.putString(Utils.PREF_WEATHER_DETAIL, strDay);
					e.apply();
				}
			} else {
				strDay = Utils.getStrPref(Utils.PREF_WEATHER_DETAIL, AliYunWeatherAct.this);
				if (TextUtils.isEmpty(strDay)){
					SharedPreferences.Editor e = mPref.edit();
					e.putInt(Utils.PREF_WEATHER_TIME, 0);
					e.apply();
				}
			}
			String areaid =getAreaidBy7DayJson(strDay);
			if (!TextUtils.isEmpty(areaid)){
				if (isOutOfTime(Utils.PREF_WEATHER_TIME_HOUR)){
					if (!TextUtils.isEmpty(areaid)){
						strHour = WeatherUtils.Get24WeatherFromAliYun("",areaid);
						if (!TextUtils.isEmpty(strHour)) {
							SharedPreferences.Editor e = mPref.edit();
							e.putInt(Utils.PREF_WEATHER_TIME_HOUR, (int) (System.currentTimeMillis() / 1000));
							e.putString(Utils.PREF_WEATHER_DETAIL_HOUR, strHour);
							e.apply();
						}
					}
				} else {
					strHour = Utils.getStrPref(Utils.PREF_WEATHER_DETAIL_HOUR,AliYunWeatherAct.this);
				}
			}
			final String str1 = strDay;
			final String str2 = strHour;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					parseJsonToUI(str1,str2);
				}
			});
		}
	}



	private boolean isOutOfTime(String timePref){
		int lastWeatherTime = Utils.getIntPref(timePref, AliYunWeatherAct.this);
		int now = (int) (System.currentTimeMillis()/1000);
		if ((now - lastWeatherTime) > 30 * 60){//超过半小时
			return true;
		} else {
			return false;
		}
	}

	private String getAreaidBy7DayJson(String jsonStr){
		try {
			JSONObject jo = new JSONObject(jsonStr);
			String areaid = jo.getJSONObject("showapi_res_body").getJSONObject("cityInfo").getString("c1");
			return areaid;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String getWeekDayChinese(String numStr){
		switch (numStr){
			case "1":
				return "一";
			case "2":
				return "二";
			case "3":
				return "三";
			case "4":
				return "四";
			case "5":
				return "五";
			case "6":
				return "六";
			case "7":
				return "日";
		}
		return "";
	}

	private String getTimeStr(String aliYunTimeStr){//20170316113000
		if (null != aliYunTimeStr && aliYunTimeStr.length() >=14 ){
			return aliYunTimeStr.substring(8,10) + ":" + aliYunTimeStr.substring(10,12);
		}
		return "";
	}

	
}