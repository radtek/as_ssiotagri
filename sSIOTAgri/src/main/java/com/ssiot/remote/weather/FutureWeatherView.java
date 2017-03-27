package com.ssiot.remote.weather;

import com.ssiot.agri.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FutureWeatherView extends RelativeLayout{

	private ImageView iv;
	private TextView tv1,tv2,tv3;
	public FutureWeatherView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.future_weather_view_attrs);
		String text1=ta.getString(R.styleable.future_weather_view_attrs_text1);
		String text2=ta.getString(R.styleable.future_weather_view_attrs_text2);
		String text3=ta.getString(R.styleable.future_weather_view_attrs_text3);
		Drawable drawable=ta.getDrawable(R.styleable.future_weather_view_attrs_src); 
		
		tv1.setText(text1);
		tv2.setText(text2);
		tv3.setText(text3);
		iv.setImageDrawable(drawable);
	}

	private void initView(Context context){
		View.inflate(context, R.layout.future_weather_view, this);
		iv=(ImageView) findViewById(R.id.future_info_Iv);
		tv1=(TextView) findViewById(R.id.future_date_Tv);
		tv2=(TextView) findViewById(R.id.future_day_tem_Tv);
		tv3=(TextView) findViewById(R.id.future_night_tem_Tv);
	}

}
