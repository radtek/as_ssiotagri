package com.ssiot.remote.weather;

import com.ssiot.agri.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DayWeatherView extends RelativeLayout{

	private TextView weekTextV;
	private ImageView imgVDay;
	private ImageView imgVNight;
	private TextView windOriTextV;
	private TextView windTextV;
	public DayWeatherView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
//		TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.future_weather_view_attrs);
//		String text1=ta.getString(R.styleable.future_weather_view_attrs_text1);
//		String text2=ta.getString(R.styleable.future_weather_view_attrs_text2);
//		String text3=ta.getString(R.styleable.future_weather_view_attrs_text3);
//		Drawable drawable=ta.getDrawable(R.styleable.future_weather_view_attrs_src); 
//		
//		tv1.setText(text1);
//		tv2.setText(text2);
//		tv3.setText(text3);
//		iv.setImageDrawable(drawable);
	}

	private void initView(Context context){
		View.inflate(context, R.layout.future_weather_view_ver, this);
		weekTextV = (TextView) findViewById(R.id.tv_week);
		imgVDay=(ImageView) findViewById(R.id.img_day);
		imgVNight=(ImageView) findViewById(R.id.img_night);
		windOriTextV=(TextView) findViewById(R.id.tv_windori);
		windTextV=(TextView) findViewById(R.id.tv_wind);
	}
	
	private void setWeekText(String str){
		weekTextV.setText(str);
	}
	
	private void setDayIcon(int resId){
		imgVDay.setImageResource(resId);
	}
	
	private void setNightIcon(int resId){
		imgVNight.setImageResource(resId);
	}
	
	private void setWindOri(String ori){
		windOriTextV.setText(ori);
	}
	
	private void setWind(String windlevel){
		windTextV.setText(windlevel);
	}
	

}
