package com.ssiot.remote.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnTouchModeChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.ssiot.agri.R;
import com.ssiot.remote.yun.widget.QQHorizontalScrollView;

public class JuheWeatherAct extends Activity  {
	private static final String tag = "JuheWeatherAct";

	private FutureWeatherView fv1,fv2,fv3,fv4,fv5;
	private LinearLayout linearLayout;
	private TextView tv1_1,tv1_2,tv1_3,city_Tv,info_Tv,temperature_Tv,data_Tv,moon_Tv,time_Tv;
	private TextView tv2_1,tv2_2,tv2_3;
	private TextView tv3_1,tv3_2,tv3_3;
	private TextView tv4_1,tv4_2,tv4_3;
	private TextView tv5_1,tv5_2,tv5_3;
	private ImageView iv1,iv2,iv3,iv4,iv5;
	private TextView today_info,ziwaixian,kongtiao,yundong,chuanyi;
	private TextView sousuo_Tv;
	private EditText sousuo_Et;
	private  String cityname="北京";
	
	
	private ImageView img_Iv,shuaxin,liebiao;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.juhe_weather_act);
		
		//侧滑实现
//		SlidingMenu menu=new SlidingMenu(this);
//		menu.setMode(SlidingMenu.LEFT);
//		//设置触摸屏幕的模式
//		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//		menu.setShadowWidthRes(R.dimen.shadow_width);
//		menu.setShadowDrawable(R.drawable.shadow);
//		
//		//设置滑动菜单视图的宽度
//		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//		//设置渐入渐出效果的值
//		menu.setFadeDegree(0.35f);
//		
//		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//		//为侧滑菜单设置布局
//		menu.setMenu(R.layout.leftmenu);
		
		init();
		
//		ViewUtils.inject(this);
//		getWeather(cityname);//TODO
		new GetCityThread().start();
		
//		shuaxin.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//				ObjectAnimator animator = ObjectAnimator.ofFloat(shuaxin, "rotation", 0f, 720f);  
//		        animator.setDuration(2000);  
//		        animator.start();
//				getWeather();
//				Toast.makeText(JuheWeatherAct.this, "刷新成功！", 0).show();
//			}
//		});
		
//		sousuo_Et=(EditText) findViewById(R.id.sousuo_Et);
//		sousuo_Tv=(TextView) findViewById(R.id.sousuo_Tv);
		
//		sousuo_Tv.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				cityname=sousuo_Et.getText().toString();
//				Log.v(tag, cityname);
//				getWeather();
//				
//			}
//		});
	}

	private void init() {
		final QQHorizontalScrollView my_sliding_ui = (QQHorizontalScrollView) findViewById(R.id.my_sliding_ui);
		
		fv1=(FutureWeatherView) findViewById(R.id.future_1_View);
		fv2=(FutureWeatherView) findViewById(R.id.future_2_View);
		fv3=(FutureWeatherView) findViewById(R.id.future_3_View);
		fv4=(FutureWeatherView) findViewById(R.id.future_4_View);
		fv5=(FutureWeatherView) findViewById(R.id.future_5_View);
		
		tv1_1=(TextView) fv1.findViewById(R.id.future_date_Tv);
		tv1_2=(TextView) fv1.findViewById(R.id.future_day_tem_Tv);
		tv1_3=(TextView) fv1.findViewById(R.id.future_night_tem_Tv);
		iv1=(ImageView) fv1.findViewById(R.id.future_info_Iv);
		
		tv2_1=(TextView) fv2.findViewById(R.id.future_date_Tv);
		tv2_2=(TextView) fv2.findViewById(R.id.future_day_tem_Tv);
		tv2_3=(TextView) fv2.findViewById(R.id.future_night_tem_Tv);
		iv2=(ImageView) fv2.findViewById(R.id.future_info_Iv);
		
		tv3_1=(TextView) fv3.findViewById(R.id.future_date_Tv);
		tv3_2=(TextView) fv3.findViewById(R.id.future_day_tem_Tv);
		tv3_3=(TextView) fv3.findViewById(R.id.future_night_tem_Tv);
		iv3=(ImageView) fv3.findViewById(R.id.future_info_Iv);
		
		tv4_1=(TextView) fv4.findViewById(R.id.future_date_Tv);
		tv4_2=(TextView) fv4.findViewById(R.id.future_day_tem_Tv);
		tv4_3=(TextView) fv4.findViewById(R.id.future_night_tem_Tv);
		iv4=(ImageView) fv4.findViewById(R.id.future_info_Iv);
		
		tv5_1=(TextView) fv5.findViewById(R.id.future_date_Tv);
		tv5_2=(TextView) fv5.findViewById(R.id.future_day_tem_Tv);
		tv5_3=(TextView) fv5.findViewById(R.id.future_night_tem_Tv);
		iv5=(ImageView) fv5.findViewById(R.id.future_info_Iv);
		
		city_Tv=(TextView) findViewById(R.id.city_Tv);
		info_Tv=(TextView) findViewById(R.id.info_Tv);
		temperature_Tv=(TextView) findViewById(R.id.temperature_Tv);
		data_Tv=(TextView) findViewById(R.id.data_Tv);
		moon_Tv=(TextView) findViewById(R.id.moon_Tv);
		time_Tv=(TextView) findViewById(R.id.time_Tv);
		img_Iv=(ImageView) findViewById(R.id.ima_Tv);
		linearLayout=(LinearLayout) findViewById(R.id.background);
		shuaxin=(ImageView) findViewById(R.id.shuaxin);
		liebiao = (ImageView) findViewById(R.id.liebiao);
		today_info=(TextView) findViewById(R.id.today_info);
		ziwaixian=(TextView) findViewById(R.id.ziwaixian);
		kongtiao=(TextView) findViewById(R.id.kongtiao);
		yundong=(TextView) findViewById(R.id.yundong);
		chuanyi=(TextView) findViewById(R.id.chuanyi);
		
		liebiao.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				my_sliding_ui.smoothScrollTo(0, 0);
			}
		});
	}
	
	private class GetCityThread extends Thread{
		@Override
		public void run() {
			getCityFromBaidu(getLocation());
		}
	}
	
	private Location getLocation(){
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度  
        criteria.setAltitudeRequired(false);//无海拔要求  
        criteria.setBearingRequired(false);//无方位要求  
        criteria.setCostAllowed(false);//允许产生资费  
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗  

        // 获取最佳服务对象  
        String provider = locationManager.getBestProvider(criteria,true);
        Location location = locationManager.getLastKnownLocation(provider);
        return location;
    }
	
	public void getWeather(final String cityStr){
		HttpUtils httpUtils=new HttpUtils(10000);
		String urlParam="http://op.juhe.cn/onebox/weather/query?cityname="+cityStr+
				"&key=6b686638aeaec6d3d2e3ee91fcb698c3";
		httpUtils.send(HttpMethod.GET, urlParam,new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.e(tag, "-----onFailure---------" + arg1);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Log.v(tag, "------"+arg0.result + cityStr);
				String message_json=arg0.result;
				
				Message msg=Message.obtain();
				msg.what=MSG_GET_JUHE_END;
				msg.obj=message_json;
				handler.sendMessage(msg);
//				Toast.makeText(JuheWeatherAct.this, message_json, 0).show();
			}
		});
		
	}
	
	private static final int MSG_GET_JUHE_END = 1;
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_GET_JUHE_END:
					Toast.makeText(JuheWeatherAct.this, " 查询成功", Toast.LENGTH_SHORT)
							.show();
					String message_json = (String) msg.obj;
					parserJson(message_json);
					break;
	
				default:
					break;
			}

			// Log.v(tag, time);
		}

		private void parserJson(String message_json) {
			try {
				JSONObject jsonObject = new JSONObject(message_json);
				JSONObject resultObject = jsonObject.getJSONObject("result");
				JSONObject dataObject = resultObject.getJSONObject("data");
				JSONObject realtimeObject = dataObject.getJSONObject("realtime");
				
				String updatatime=realtimeObject.getString("time");
				time_Tv.setText(updatatime+"\b发布");
				
				
				JSONObject cuurent_weatherObject=realtimeObject.getJSONObject("weather");//当前天气json
				String current_humidity=cuurent_weatherObject.getString("humidity");//当前湿度
				String current_img=cuurent_weatherObject.getString("img");//图片TODO
				
				String current_info=cuurent_weatherObject.getString("info");//当前天气
				int currentid = cuurent_weatherObject.getInt("img");
				info_Tv.setText(current_info);
				
				String current_temperature=cuurent_weatherObject.getString("temperature");//当前温度
				temperature_Tv.setText(current_temperature+"°C");
				
				String current_data=realtimeObject.getString("date");
				data_Tv.setText(current_data);
				
				String city_name=realtimeObject.getString("city_name");
				city_Tv.setText(city_name);
				
				String current_week=realtimeObject.getString("week");
				
				String current_moon=realtimeObject.getString("moon");
				moon_Tv.setText(current_moon);
				today_info.setText("今天：现在"+current_info+"。"+"当前气温"+current_temperature+"°C。");
				
				JSONObject lifeJsonObject=dataObject.getJSONObject("life");
				JSONObject infoJsonObject=lifeJsonObject.getJSONObject("info");
				
				JSONArray kongtiaoArray=infoJsonObject.getJSONArray("kongtiao");//？？？
				String kongtiao=kongtiaoArray.getString(1);
				JuheWeatherAct.this.kongtiao.setText(kongtiao);
				
				JSONArray yundongArray=infoJsonObject.getJSONArray("yundong");//运动
				String yundong=yundongArray.getString(1);
				JuheWeatherAct.this.yundong.setText(yundong);
				
				JSONArray ziwaixianArray=infoJsonObject.getJSONArray("ziwaixian");//紫外线
				String ziwaixian=ziwaixianArray.getString(1);
				JuheWeatherAct.this.ziwaixian.setText(ziwaixian);
				
				//感冒
				//洗车
				
				JSONArray chuanyiArray=infoJsonObject.getJSONArray("chuanyi");//穿衣
				String chuanyi=chuanyiArray.getString(1);
				JuheWeatherAct.this.chuanyi.setText(chuanyi);
				
				//-----------------------------------------------------------------------------
				JSONArray weatherArray=dataObject.getJSONArray("weather");//以下是预报，包括今天
				//--------------
				JSONObject wea_1Object=weatherArray.getJSONObject(1);
				String future_1_date=wea_1Object.getString("date");
				tv1_1.setText(future_1_date);
				
				JSONObject fu_1_infoJsonObject=wea_1Object.getJSONObject("info");
				JSONArray fu_1_nightArray=fu_1_infoJsonObject.getJSONArray("night");
				String fu_1_night_tem=fu_1_nightArray.getString(2);//夜温度
				tv1_3.setText(fu_1_night_tem);
				
				JSONArray fu_1_dayArray=fu_1_infoJsonObject.getJSONArray("day");
				int fu_1_id = fu_1_dayArray.getInt(0);
				String fu_1_day_info=fu_1_dayArray.getString(1);//天气
				String fu_1_day_tem=fu_1_dayArray.getString(2);//温度
				tv1_2.setText(fu_1_day_tem);
				//--------------
				JSONObject wea_2Object=weatherArray.getJSONObject(2);
				String future_2_date=wea_2Object.getString("date");
				tv2_1.setText(future_2_date);
				
				JSONObject fu_2_infoJsonObject=wea_2Object.getJSONObject("info");
				JSONArray fu_2_nightArray=fu_2_infoJsonObject.getJSONArray("night");
				String fu_2_night_tem=fu_2_nightArray.getString(2);
				tv2_3.setText(fu_2_night_tem);
				
				JSONArray fu_2_dayArray=fu_2_infoJsonObject.getJSONArray("day");
				int fu_2_id = fu_2_dayArray.getInt(0);
				String fu_2_day_info=fu_2_dayArray.getString(1);
				String fu_2_day_tem=fu_2_dayArray.getString(2);
				tv2_2.setText(fu_2_day_tem);
				//-----------
				JSONObject wea_3Object=weatherArray.getJSONObject(3);
				String future_3_date=wea_3Object.getString("date");
				tv3_1.setText(future_3_date);
				
				JSONObject fu_3_infoJsonObject=wea_3Object.getJSONObject("info");
				JSONArray fu_3_nightArray=fu_3_infoJsonObject.getJSONArray("night");
				String fu_3_night_tem=fu_3_nightArray.getString(2);
				tv3_3.setText(fu_3_night_tem);
				
				JSONArray fu_3_dayArray=fu_3_infoJsonObject.getJSONArray("day");
				int fu_3_id = fu_3_dayArray.getInt(0);
				String fu_3_day_info=fu_3_dayArray.getString(1);
				String fu_3_day_tem=fu_3_dayArray.getString(2);
				tv3_2.setText(fu_3_day_tem);
				//-----------
				JSONObject wea_4Object=weatherArray.getJSONObject(4);
				String future_4_date=wea_4Object.getString("date");
				tv4_1.setText(future_4_date);
				
				JSONObject fu_4_infoJsonObject=wea_4Object.getJSONObject("info");
				JSONArray fu_4_nightArray=fu_4_infoJsonObject.getJSONArray("night");
				String fu_4_night_tem=fu_4_nightArray.getString(2);
				tv4_3.setText(fu_4_night_tem);
				
				JSONArray fu_4_dayArray=fu_4_infoJsonObject.getJSONArray("day");
				int fu_4_id = fu_4_dayArray.getInt(0);
				String fu_4_day_info=fu_4_dayArray.getString(1);
				Log.v(tag, fu_4_day_info);
				String fu_4_day_tem=fu_4_dayArray.getString(2);
				tv4_2.setText(fu_4_day_tem);
				//-----------
//				JSONObject wea_5Object=weatherArray.getJSONObject(5);
//				String future_5_date=wea_5Object.getString("date");
//				tv5_1.setText(future_5_date);
//				
//				JSONObject fu_5_infoJsonObject=wea_5Object.getJSONObject("info");
//				JSONArray fu_5_nightArray=fu_5_infoJsonObject.getJSONArray("night");
//				String fu_5_night_tem=fu_5_nightArray.getString(2);
//				tv5_3.setText(fu_5_night_tem);
//				
//				JSONArray fu_5_dayArray=fu_5_infoJsonObject.getJSONArray("day");
//				int fu_5_id = fu_5_dayArray.getInt(0);
//				String fu_5_day_info=fu_5_dayArray.getString(1);
//				String fu_5_day_tem=fu_5_dayArray.getString(2);
//				tv5_2.setText(fu_5_day_tem);
				
//				setImg(current_info,fu_1_day_info,fu_2_day_info,fu_3_day_info,fu_4_day_info,
//						fu_5_day_info);
				setImg2(currentid,fu_1_id,fu_2_id,fu_3_id,fu_4_id);
				
				//还有预计降雨量和温度
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void setImg2(int currentid,int fu_1_id, int fu_2_id, int fu_3_id, int fu_4_id){
			img_Iv.setImageResource(IMGS_DAY[currentid]);
			
			iv1.setImageResource(IMGS_DAY[fu_1_id]);
			iv2.setImageResource(IMGS_DAY[fu_2_id]);
			iv3.setImageResource(IMGS_DAY[fu_3_id]);
			iv4.setImageResource(IMGS_DAY[fu_4_id]);
//			iv5.setImageResource(IMGS_DAY[fu_5_id]);
			int bkres = R.drawable.bg_sunny_day;
			if (currentid >= 1 && currentid <= 2){
				bkres = R.drawable.bg_na;
			} else if ((currentid >= 3 && currentid <= 12)
					|| (currentid >= 19 && currentid <= 25)){
				bkres = R.drawable.bg_moderate_rain_day;
			} else if (currentid >= 13 && currentid <= 18
					|| (currentid >= 26 && currentid <= 53)){
				bkres = R.drawable.bg_snow_day;
			}
			
			linearLayout.setBackgroundResource(bkres);
		}

		//设置当前天气图标和背景，，&设置预报天气图标
//		private void setImg(String current_info,String fu_1_day_info,String fu_2_day_info,
//				String fu_3_day_info,String fu_4_day_info,String fu_5_day_info) {
//			if(current_info.equals("小雨")||current_info.equals("阵雨")){
//				img_Iv.setImageResource(R.drawable.xiaoyu);
//				linearLayout.setBackgroundResource(R.drawable.xiayu_bg);
//			}else if(current_info.equals("大雨")||current_info.equals("大雨-暴雨")){
//				img_Iv.setImageResource(R.drawable.dayu);
//				linearLayout.setBackgroundResource(R.drawable.dayu_bg);
//			}else if(current_info.equals("多云")){
//				img_Iv.setImageResource(R.drawable.duoyun);
//				linearLayout.setBackgroundResource(R.drawable.duoyun_bg);
//			}else if(current_info.equals("阴")){
//				img_Iv.setImageResource(R.drawable.yin);
//				linearLayout.setBackgroundResource(R.drawable.yin_bg);
//			}else if(current_info.equals("晴")){
//				img_Iv.setImageResource(R.drawable.qing);
//				linearLayout.setBackgroundResource(R.drawable.qing_bg);
//			}else if(current_info.equals("雷阵雨")){
//				img_Iv.setImageResource(R.drawable.leizhenyu);
//				linearLayout.setBackgroundResource(R.drawable.leiyu_bg);
//			}else if(current_info.equals("中雨")||current_info.equals("小雨-中雨")){
//				img_Iv.setImageResource(R.drawable.zhongyu);
//				linearLayout.setBackgroundResource(R.drawable.dayu_bg);
//			}else if(current_info.equals("雾")){
//				img_Iv.setImageResource(R.drawable.wu);
//				linearLayout.setBackgroundResource(R.drawable.wu_bg);
//			}
//			
//			if(fu_1_day_info.equals("小雨")||fu_1_day_info.equals("阵雨")){
//				iv1.setImageResource(R.drawable.xiaoyu);
//			}else if(fu_1_day_info.equals("大雨")){
//				iv1.setImageResource(R.drawable.dayu);
//			}else if(fu_1_day_info.equals("多云")){
//				iv1.setImageResource(R.drawable.duoyun);
//			}else if(fu_1_day_info.equals("阴")){
//				iv1.setImageResource(R.drawable.yin);
//			}else if(fu_1_day_info.equals("晴")){
//				iv1.setImageResource(R.drawable.qing);
//			}else if(fu_1_day_info.equals("雷阵雨")||fu_1_day_info.equals("暴雨")){
//				iv1.setImageResource(R.drawable.leizhenyu);
//			}else if(fu_1_day_info.equals("中雨")||fu_1_day_info.equals("小雨-中雨")){
//				iv1.setImageResource(R.drawable.zhongyu);
//			}else if(fu_1_day_info.equals("雾")){
//				iv1.setImageResource(R.drawable.wu);
//			}
//			
//			if(fu_2_day_info.equals("小雨")||fu_2_day_info.equals("阵雨")){
//				iv2.setImageResource(R.drawable.xiaoyu);
//			}else if(fu_2_day_info.equals("大雨")){
//				iv2.setImageResource(R.drawable.dayu);
//			}else if(fu_2_day_info.equals("多云")){
//				iv2.setImageResource(R.drawable.duoyun);
//			}else if(fu_2_day_info.equals("阴")){
//				iv2.setImageResource(R.drawable.yin);
//			}else if(fu_2_day_info.equals("晴")){
//				iv2.setImageResource(R.drawable.qing);
//			}else if(fu_2_day_info.equals("雷阵雨")||fu_2_day_info.equals("暴雨")){
//				iv2.setImageResource(R.drawable.leizhenyu);
//			}else if(fu_2_day_info.equals("中雨")||fu_2_day_info.equals("小雨-中雨")){
//				iv2.setImageResource(R.drawable.zhongyu);
//			}else if(fu_2_day_info.equals("雾")){
//				iv2.setImageResource(R.drawable.wu);
//			}
//			
//			if(fu_3_day_info.equals("小雨")||fu_3_day_info.equals("阵雨")){
//				iv3.setImageResource(R.drawable.xiaoyu);
//			}else if(fu_3_day_info.equals("大雨")){
//				iv3.setImageResource(R.drawable.dayu);
//			}else if(fu_3_day_info.equals("多云")){
//				iv3.setImageResource(R.drawable.duoyun);
//			}else if(fu_3_day_info.equals("阴")){
//				iv3.setImageResource(R.drawable.yin);
//			}else if(fu_3_day_info.equals("晴")){
//				iv3.setImageResource(R.drawable.qing);
//			}else if(fu_3_day_info.equals("雷阵雨")||fu_3_day_info.equals("暴雨")){
//				iv3.setImageResource(R.drawable.leizhenyu);
//			}else if(fu_3_day_info.equals("中雨")||fu_3_day_info.equals("小雨-中雨")){
//				iv3.setImageResource(R.drawable.zhongyu);
//			}else if(fu_3_day_info.equals("雾")){
//				iv3.setImageResource(R.drawable.wu);
//			}
//			
//			if(fu_4_day_info.equals("小雨")||fu_4_day_info.equals("阵雨")){
//				iv4.setImageResource(R.drawable.xiaoyu);
//			}else if(fu_4_day_info.equals("大雨")){
//				iv4.setImageResource(R.drawable.dayu);
//			}else if(fu_4_day_info.equals("多云")){
//				iv4.setImageResource(R.drawable.duoyun);
//			}else if(fu_4_day_info.equals("阴")){
//				iv4.setImageResource(R.drawable.yin);
//			}else if(fu_4_day_info.equals("晴")){
//				iv4.setImageResource(R.drawable.qing);
//			}else if(fu_4_day_info.equals("雷阵雨")||fu_4_day_info.equals("暴雨")){
//				iv4.setImageResource(R.drawable.leizhenyu);
//			}else if(fu_4_day_info.equals("中雨")||fu_4_day_info.equals("小雨-中雨")){
//				iv4.setImageResource(R.drawable.zhongyu);
//			}else if(fu_4_day_info.equals("雾")){
//				iv4.setImageResource(R.drawable.wu);
//			}
//			
//			if(fu_5_day_info.equals("小雨")||fu_5_day_info.equals("阵雨")){
//				iv5.setImageResource(R.drawable.xiaoyu);
//			}else if(fu_5_day_info.equals("大雨")){
//				iv5.setImageResource(R.drawable.dayu);
//			}else if(fu_5_day_info.equals("多云")){
//				iv5.setImageResource(R.drawable.duoyun);
//			}else if(fu_5_day_info.equals("阴")){
//				iv5.setImageResource(R.drawable.yin);
//			}else if(fu_5_day_info.equals("晴")){
//				iv5.setImageResource(R.drawable.qing);
//			}else if(fu_5_day_info.equals("雷阵雨")||fu_5_day_info.equals("暴雨")){
//				iv5.setImageResource(R.drawable.leizhenyu);
//			}else if(fu_5_day_info.equals("中雨")||fu_5_day_info.equals("小雨-中雨")){
//				iv5.setImageResource(R.drawable.zhongyu);
//			}else if(fu_5_day_info.equals("雾")){
//				iv5.setImageResource(R.drawable.wu);
//			}
//		};
	};
	
	private static final int[] IMGS_DAY = new int[33];
	static{
		IMGS_DAY[0] = R.drawable.we_00;
		IMGS_DAY[1] = R.drawable.we_01;
		IMGS_DAY[2] = R.drawable.we_02;
		IMGS_DAY[3] = R.drawable.we_03;
		IMGS_DAY[4] = R.drawable.we_04;
		IMGS_DAY[5] = R.drawable.we_05;
		IMGS_DAY[6] = R.drawable.we_06;
		IMGS_DAY[7] = R.drawable.we_07;
		IMGS_DAY[8] = R.drawable.we_08;
		IMGS_DAY[9] = R.drawable.we_09;
		IMGS_DAY[10] = R.drawable.we_10;
		IMGS_DAY[11] = R.drawable.we_11;
		IMGS_DAY[12] = R.drawable.we_12;
		IMGS_DAY[13] = R.drawable.we_13;
		IMGS_DAY[14] = R.drawable.we_14;
		IMGS_DAY[15] = R.drawable.we_15;
		IMGS_DAY[16] = R.drawable.we_16;
		IMGS_DAY[17] = R.drawable.we_17;
		IMGS_DAY[18] = R.drawable.we_18;
		IMGS_DAY[19] = R.drawable.we_19;
		IMGS_DAY[20] = R.drawable.we_20;
		IMGS_DAY[21] = R.drawable.we_21;
		IMGS_DAY[22] = R.drawable.we_22;
		IMGS_DAY[23] = R.drawable.we_23;
		IMGS_DAY[24] = R.drawable.we_24;
		IMGS_DAY[25] = R.drawable.we_25;
		IMGS_DAY[26] = R.drawable.we_26;
		IMGS_DAY[27] = R.drawable.we_27;
		IMGS_DAY[28] = R.drawable.we_28;
		IMGS_DAY[29] = R.drawable.we_29;
		IMGS_DAY[30] = R.drawable.we_30;
		IMGS_DAY[31] = R.drawable.we_31;
		IMGS_DAY[32] = R.drawable.we_53;
	}
	
	private static final int[] IMGS_N = new int[33];
	static{
		IMGS_N[0] = R.drawable.we_n_00;
		IMGS_N[1] = R.drawable.we_n_01;
		IMGS_N[2] = R.drawable.we_n_02;
		IMGS_N[3] = R.drawable.we_n_03;
		IMGS_N[4] = R.drawable.we_n_04;
		IMGS_N[5] = R.drawable.we_n_05;
		IMGS_N[6] = R.drawable.we_n_06;
		IMGS_N[7] = R.drawable.we_n_07;
		IMGS_N[8] = R.drawable.we_n_08;
		IMGS_N[9] = R.drawable.we_n_09;
		IMGS_N[10] = R.drawable.we_n_10;
		IMGS_N[11] = R.drawable.we_n_11;
		IMGS_N[12] = R.drawable.we_n_12;
		IMGS_N[13] = R.drawable.we_n_13;
		IMGS_N[14] = R.drawable.we_n_14;
		IMGS_N[15] = R.drawable.we_n_15;
		IMGS_N[16] = R.drawable.we_n_16;
		IMGS_N[17] = R.drawable.we_n_17;
		IMGS_N[18] = R.drawable.we_n_18;
		IMGS_N[19] = R.drawable.we_n_19;
		IMGS_N[20] = R.drawable.we_n_20;
		IMGS_N[21] = R.drawable.we_n_21;
		IMGS_N[22] = R.drawable.we_n_22;
		IMGS_N[23] = R.drawable.we_n_23;
		IMGS_N[24] = R.drawable.we_n_24;
		IMGS_N[25] = R.drawable.we_n_25;
		IMGS_N[26] = R.drawable.we_n_26;
		IMGS_N[27] = R.drawable.we_n_27;
		IMGS_N[28] = R.drawable.we_n_28;
		IMGS_N[29] = R.drawable.we_n_29;
		IMGS_N[30] = R.drawable.we_n_30;
		IMGS_N[31] = R.drawable.we_n_31;
		IMGS_N[32] = R.drawable.we_n_53;
	}
	
	private String getCityFromBaidu(Location lo){
      try {
          //此百度的ak类别是选择的服务端
          URL url = new URL("http://api.map.baidu.com/geocoder/v2/?ak=" +
                  "nX6MZuMH44lfg5jHQ3smcchQ&callback=renderReverse&location=" + lo.getLatitude()
                  + "," + lo.getLongitude() + "&output=json&pois=0");
          HttpGet httpRequest = new HttpGet(url.toString());
          HttpClient httpclient = new DefaultHttpClient();
          // 请求HttpClient，取得HttpResponse
          HttpResponse httpResponse = httpclient.execute(httpRequest);
          // 请求成功
          if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
              // 取得返回的字符串
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				Log.v(tag,strResult + " " + lo.getLongitude() + " " + lo.getLatitude());
				String cityStr = parseCityJSONStr(strResult);
				cityname = cityStr;
				getWeather(cityname);
				return cityStr;
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String parseCityJSONStr(String str){
        String str2 = str;
        str2 = str2.substring(str2.indexOf("{"), str2.lastIndexOf("}")+1);
        Log.v(tag,  "----parseLocationJSONStr----"+str2);
        try {
            JSONObject jo = new JSONObject(str2);
            if (jo.getInt("status") == 0){
                JSONObject jResult = jo.getJSONObject("result");
                JSONObject adrJson = jResult.getJSONObject("addressComponent");
//                return adrJson.getString("district");//"province":"浙江省","city":"杭州市","district":"富阳市"
                //百度的地址和聚合数据的地址不一样，例如 海安县  海安
                return parseJuheCityStr(adrJson.getString("district"));
            } else {
                Log.e(tag, "error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "北京";
    }
	
	private String parseJuheCityStr(String baiduCity){//模糊city名称转换为聚合精确名称
		String str ="";
		try {
			InputStreamReader inputReader;
			inputReader = new InputStreamReader(getResources().getAssets().open("citylist.txt"));
			BufferedReader bufReader = new BufferedReader(inputReader);
	        String line="";
	        while((line = bufReader.readLine()) != null){
	            str += line;
	        }
	        
	        //-----------------
	        JSONObject jo = new JSONObject(str);
	        JSONArray cityArray = jo.getJSONArray("result");
	        for (int i = 0; i < cityArray.length(); i ++){
	        	String juhecity = cityArray.optJSONObject(i).getString("district");
	        	if (juhecity.indexOf(baiduCity) == 0 || baiduCity.indexOf(juhecity) == 0){
	        		return juhecity;
	        	}
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}catch (JSONException e) {
			e.printStackTrace();
		}
		
        
		return "北京";
	}
}