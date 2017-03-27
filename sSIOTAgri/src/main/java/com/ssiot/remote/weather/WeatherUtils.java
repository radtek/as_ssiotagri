package com.ssiot.remote.weather;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;
import android.util.Log;

import com.ssiot.agri.R;

public class WeatherUtils{
	private static final String tag = "WeatherUtils";
	
	private static final String WEATHER_HOST = "http://ali-weather.showapi.com";
	
	
	public static String get7DayWeatherFromAliYun(String lat, String lng){//GPS经纬度坐标查询7天预报详情
    	if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng)){
    		Log.e(tag,"未获取到位置信息");
    		return "";
    	}
    	String host = WEATHER_HOST + "/gps-to-weather";
    	String url = host + "?" + "from=5&lat=" + lat + "&lng=" + lng + "&need3HourForcast=0&needAlarm=1&needHourData=0&needIndex=1&needMoreDay=1";
    	return GetAliYunWeaHttpGet(url);
    }
	
	
	
//	public static String GetWeekWeatherFromAliYun(String area,String areaid){//id或地名查询7天
//		String url = WEATHER_HOST + "/area-to-weather?area=" + area + "&areaid=" + areaid
//				+"need3HourForcast=0&needAlarm=0&needHourData=0&needIndex=0&needMoreDay=0";
//		return GetAliYunWeaHttpGet(url);
//	}
	
	public static String Get24WeatherFromAliYun(String area,String areaid){//id或地名查询24小时预报
		String url = WEATHER_HOST + "/hour24?area=" + area + "&areaid=" + areaid;
		return GetAliYunWeaHttpGet(url);
	}

	private static String GetAliYunWeaHttpGet(String url){
		HttpGet httpGet = new HttpGet(url);
    	httpGet.addHeader("Authorization", "APPCODE d407a63eecfb43279c4db92e7b7d0629");
    	try {
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200){
				String result = EntityUtils.
						toString(httpResponse.getEntity());
				Log.v(tag, "==========!!!!!!!!!!!" + result);
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return "";
	}

	public static int GetSmallWeatherIcon(String weatherCode){
		int res = 0;
		switch (weatherCode) {
			case "00"://晴
				return R.drawable.notification_weather_sunny_small;
			case "01"://多云
				return R.drawable.notification_weather_mostly_cloudy_small;
			case "02"://阴
				return R.drawable.notification_weather_cloudy_small;
			case "03"://阵雨
				return R.drawable.notification_weather_shower_small;
			case "04"://雷阵雨
			case "05"://雷阵雨伴有冰雹
				return R.drawable.notification_weather_thunderstorms_small;
			case "06"://雨夹雪
				return R.drawable.notification_weather_sleet_small;
			case "07"://小雨
				return R.drawable.notification_weather_drizzle_small;
			case "08"://中雨
			case "09"://大雨
			case "10"://暴雨
			case "11"://大暴雨
			case "12"://特大暴雨
			case "19"://冻雨
			case "21"://小到中雨
			case "22"://中到大雨
			case "23"://大到暴雨
			case "24"://暴雨到大暴雨
			case "25"://大暴雨到特大暴雨
				return R.drawable.notification_weather_heavy_rain_small;
			case "13"://阵雪
				return R.drawable.notification_weather_snow_shower_small;
			case "14"://小雪
				return R.drawable.notification_weather_little_snow_small;
			case "15"://中雪
			case "16"://大雪
			case "17"://暴雪
			case "26"://小到中雪
			case "27"://中到大雪
			case "28"://大到暴雪
				return R.drawable.notification_weather_heavy_snow_small;
			case "18"://雾
				return R.drawable.notification_weather_fog_small;
			case "20"://沙尘暴
			case "29"://浮尘
			case "30"://扬沙
			case "31"://强沙尘暴
			case "53"://霾
				return R.drawable.notification_weather_sandstorm_small;
			//
			//			return R.drawable.notification_weather_tornado_small;//
			default:
				break;
		}
		return R.drawable.notification_weather_error_small;
	}

	public static int GetDayWeatherIcon(String weatherCode){
		int res = 0;
		switch (weatherCode) {
			case "00"://晴
				return R.drawable.we_00;
			case "01"://多云
				return R.drawable.we_01;
			case "02"://阴
				return R.drawable.we_02;
			case "03"://阵雨
				return R.drawable.we_03;
			case "04"://雷阵雨
				return R.drawable.we_04;
			case "05"://雷阵雨伴有冰雹
				return R.drawable.we_05;
			case "06"://雨夹雪
				return R.drawable.we_06;
			case "07"://小雨
				return R.drawable.we_07;
			case "08"://中雨
				return R.drawable.we_08;
			case "09"://大雨
				return R.drawable.we_09;
			case "10"://暴雨
				return R.drawable.we_10;
			case "11"://大暴雨
				return R.drawable.we_11;
			case "12"://特大暴雨
				return R.drawable.we_12;
			case "13"://冻雨
				return R.drawable.we_13;
			case "14"://小到中雨
				return R.drawable.we_14;
			case "15"://
				return R.drawable.we_15;
			case "16"://
				return R.drawable.we_16;
			case "17":
				return R.drawable.we_17;
			case "18":
				return R.drawable.we_18;
			case "19":
				return R.drawable.we_19;
			case "20":
				return R.drawable.we_20;
			case "21":
				return R.drawable.we_21;
			case "22":
				return R.drawable.we_22;
			case "23":
				return R.drawable.we_23;
			case "24":
				return R.drawable.we_24;
			case "25":
				return R.drawable.we_25;
			case "26":
				return R.drawable.we_26;
			case "27":
				return R.drawable.we_27;
			case "28":
				return R.drawable.we_28;
			case "29":
				return R.drawable.we_29;
			case "30":
				return R.drawable.we_30;
			case "31":
				return R.drawable.we_31;
			case "53":
				return R.drawable.we_53;
			default:
				break;
		}
		return R.drawable.we_00;
	}

	public static int GetNightWeatherIcon(String weatherCode){
		int res = 0;
		switch (weatherCode) {
			case "00"://晴
				return R.drawable.we_n_00;
			case "01"://多云
				return R.drawable.we_n_01;
			case "02"://阴
				return R.drawable.we_n_02;
			case "03"://阵雨
				return R.drawable.we_n_03;
			case "04"://雷阵雨
				return R.drawable.we_n_04;
			case "05"://雷阵雨伴有冰雹
				return R.drawable.we_n_05;
			case "06"://雨夹雪
				return R.drawable.we_n_06;
			case "07"://小雨
				return R.drawable.we_n_07;
			case "08"://中雨
				return R.drawable.we_n_08;
			case "09"://大雨
				return R.drawable.we_n_09;
			case "10"://暴雨
				return R.drawable.we_n_10;
			case "11"://大暴雨
				return R.drawable.we_n_11;
			case "12"://特大暴雨
				return R.drawable.we_n_12;
			case "13"://冻雨
				return R.drawable.we_n_13;
			case "14"://小到中雨
				return R.drawable.we_n_14;
			case "15"://
				return R.drawable.we_n_15;
			case "16"://
				return R.drawable.we_n_16;
			case "17":
				return R.drawable.we_n_17;
			case "18":
				return R.drawable.we_n_18;
			case "19":
				return R.drawable.we_n_19;
			case "20":
				return R.drawable.we_n_20;
			case "21":
				return R.drawable.we_n_21;
			case "22":
				return R.drawable.we_n_22;
			case "23":
				return R.drawable.we_n_23;
			case "24":
				return R.drawable.we_n_24;
			case "25":
				return R.drawable.we_n_25;
			case "26":
				return R.drawable.we_n_26;
			case "27":
				return R.drawable.we_n_27;
			case "28":
				return R.drawable.we_n_28;
			case "29":
				return R.drawable.we_n_29;
			case "30":
				return R.drawable.we_n_30;
			case "31":
				return R.drawable.we_n_31;
			case "53":
				return R.drawable.we_n_53;
			default:
				break;
		}
		return R.drawable.we_n_00;
	}
}