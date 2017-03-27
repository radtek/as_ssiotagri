package com.demo.xclcharts.view;

import java.util.ArrayList;
import java.util.List;

import org.xclcharts.chart.DialChart;
import org.xclcharts.view.GraphicalView;

import com.ssiot.remote.data.model.view.SensorThresholdModel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

//从xcl-charts项目的github上copy而来
public class DialChart05View extends GraphicalView {
	private String TAG = "DialChart05View";
	private DialChart chart = new DialChart();
	private float mPercentage = 0.1f;

	float mP1 = 0.0f;
	float mP2 = 0.0f;
	
	private static final float OutterR = 1f;
	private static final float InnerR = 0.85f;

	public DialChart05View(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}

	public DialChart05View(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public DialChart05View(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		chartRender();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		chart.setChartRange(w, h);
	}

	public void chartRender() {
		try {
			// 设置标题背景
			chart.setApplyBackgroundColor(false);
//			chart.setBackgroundColor(Color.rgb(28, 129, 243));
			// 绘制边框
//			chart.showRoundBorder();
			// 设置当前百分比
			chart.getPointer().setPercentage(mPercentage);
			// 设置指针长度
			chart.getPointer().setLength(InnerR);
			// 增加轴
			addAxis();
			// ///////////////////////////////////////////////////////////
			addPointer();
			// 设置附加信息
			addAttrInfo();
			// ///////////////////////////////////////////////////////////
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	public void addAxis() {
		List<String> rlabels = new ArrayList<String>();
		int j = 0;
		for (int i = 0; i <= 150;) {
			if (0 == i || j == 4) {
				rlabels.add(Integer.toString(i));
				j = 0;
			} else {
				rlabels.add("");
				j++;
			}

			i += 5;
		}
		chart.addOuterTicksAxis(1f, rlabels);//外部的数字显示
//		chart.setTitle("dsafda");
//		chart.addSubtitle("dfasdfa");

		// 环形颜色轴
		List<Float> ringPercentage = new ArrayList<Float>();
		ringPercentage.add(0.33f);
		ringPercentage.add(0.33f);
		ringPercentage.add(1 - 2 * 0.33f);

		List<Integer> rcolor = new ArrayList<Integer>();
		rcolor.add(Color.rgb(133, 206, 130));
		rcolor.add(Color.rgb(252, 210, 9));
		rcolor.add(Color.rgb(229, 63, 56));
		chart.addStrokeRingAxis(1f, InnerR, ringPercentage, rcolor);//外圈大小，内圈大小，分段比例，分段颜色

		List<String> rlabels2 = new ArrayList<String>();
		for (int i = 0; i <= 8; i++) {
			rlabels2.add(Integer.toString(i) + "MM");
		}
		chart.addInnerTicksAxis(InnerR, rlabels2);//内部文字所在区域的圆圈比例,内部的文字。， 均匀全覆盖？？

		chart.getPlotAxis().get(1).getFillAxisPaint()
				.setColor(Color.argb(0, 28, 129, 243));//内部的颜色

		chart.getPlotAxis().get(0).hideAxisLine();//隐藏外圈的黑线
		chart.getPlotAxis().get(2).hideAxisLine();//隐藏内圈的黑线
		chart.getPlotAxis().get(0).getTickMarksPaint().setColor(Color.YELLOW);//外部的小线段颜色
		chart.getPlotAxis().get(2).getTickMarksPaint().setColor(Color.RED);//内
		chart.getPlotAxis().get(2).getTickLabelPaint().setColor(Color.RED);//内字颜色
		chart.getPlotAxis().get(2).getTickLabelPaint().setTextSize(24);//jingbo add this
	}

	private void addAttrInfo() {

	}

	public void addPointer() {

	}
	
	public void setData(SensorThresholdModel thresholdModel, float value){//add by jingbo 
		chart.clearAll();
		if (null == thresholdModel){
			chart.getPointer().setPercentage(0.5f);
			setNullAxis();
			return;
		}
		Log.v(TAG, thresholdModel.toString());
		mPercentage = (value-thresholdModel.min)/(thresholdModel.max - thresholdModel.min);
		chart.getPointer().setPercentage(mPercentage);
		
		if (thresholdModel.thresholdType == 1){//传感器是单向值的
			setUpperAxis(thresholdModel);
		} else if (thresholdModel.thresholdType == 2){
			setLowerAxis(thresholdModel);
		} else if (thresholdModel.thresholdType == 3){
			setUpperAndLowerAxis(thresholdModel);
		}
//		chart.getPlotAxis().get(0).hideAxisLine();//隐藏外圈的黑线
		chart.getPlotAxis().get(1).hideAxisLine();//隐藏内圈的黑线//IndexOutOfBoundsException
		chart.getPlotAxis().get(1).getTickLabelPaint().setTextSize(24);
	}
	
	private void setUpperAxis(SensorThresholdModel thresholdModel){
		List<Float> ringPercentage = new ArrayList<Float>();
		float all = thresholdModel.max - thresholdModel.min;
		ringPercentage.add((thresholdModel.upperwarnvalue-thresholdModel.min)/all);
		ringPercentage.add((thresholdModel.upperalertvalue-thresholdModel.upperwarnvalue)/all);
		ringPercentage.add((thresholdModel.max-thresholdModel.upperalertvalue)/all);

		List<Integer> rcolor = new ArrayList<Integer>();
		rcolor.add(Color.rgb(133, 206, 130));
		rcolor.add(Color.rgb(252, 210, 9));
		rcolor.add(Color.rgb(229, 63, 56));
		chart.addStrokeRingAxis(1f, InnerR, ringPercentage, rcolor);//外圈大小，内圈大小，分段比例，分段颜色
		
		List<String> rlabels2 = new ArrayList<String>();
		for (int i = 0; i <= 3; i++) {
			rlabels2.add(""+(thresholdModel.min + (thresholdModel.max-thresholdModel.min)/3*i));
		}
		chart.addInnerTicksAxis(InnerR, rlabels2);//内部文字所在区域的圆圈比例,内部的文字。， 均匀全覆盖？？
	}
	
	private void setLowerAxis(SensorThresholdModel thresholdModel){
		List<Float> ringPercentage = new ArrayList<Float>();
		float all = thresholdModel.max - thresholdModel.min;
		ringPercentage.add((thresholdModel.loweralertvalue-thresholdModel.min)/all);
		ringPercentage.add((thresholdModel.lowerwarnvalue-thresholdModel.loweralertvalue)/all);
		ringPercentage.add((thresholdModel.max-thresholdModel.lowerwarnvalue)/all);

		List<Integer> rcolor = new ArrayList<Integer>();
		rcolor.add(Color.rgb(229, 63, 56));
		rcolor.add(Color.rgb(252, 210, 9));
		rcolor.add(Color.rgb(133, 206, 130));
		chart.addStrokeRingAxis(1f, InnerR, ringPercentage, rcolor);//外圈大小，内圈大小，分段比例，分段颜色
		
		List<String> rlabels2 = new ArrayList<String>();
		for (int i = 0; i <= 3; i++) {
			rlabels2.add(""+(thresholdModel.min + (thresholdModel.max-thresholdModel.min)/3*i));
		}
		chart.addInnerTicksAxis(InnerR, rlabels2);//内部文字所在区域的圆圈比例,内部的文字。， 均匀全覆盖？？
	}
	
	
	
	private void setUpperAndLowerAxis(SensorThresholdModel thresholdModel){
		List<Float> ringPercentage = new ArrayList<Float>();
		float all = thresholdModel.max - thresholdModel.min;
		ringPercentage.add((thresholdModel.loweralertvalue-thresholdModel.min)/all);
		ringPercentage.add((thresholdModel.lowerwarnvalue-thresholdModel.loweralertvalue)/all);
		ringPercentage.add((thresholdModel.upperwarnvalue-thresholdModel.lowerwarnvalue)/all);
		ringPercentage.add((thresholdModel.upperalertvalue-thresholdModel.upperwarnvalue)/all);
		ringPercentage.add((thresholdModel.max-thresholdModel.upperalertvalue)/all);

		List<Integer> rcolor = new ArrayList<Integer>();
		rcolor.add(Color.rgb(229, 63, 56));
		rcolor.add(Color.rgb(252, 210, 9));
		rcolor.add(Color.rgb(133, 206, 130));
		rcolor.add(Color.rgb(252, 210, 9));
		rcolor.add(Color.rgb(229, 63, 56));
		chart.addStrokeRingAxis(1f, InnerR, ringPercentage, rcolor);//外圈大小，内圈大小，分段比例，分段颜色
		
		List<String> rlabels2 = new ArrayList<String>();
		for (int i = 0; i <= 3; i++) {
			rlabels2.add(""+(thresholdModel.min + (thresholdModel.max-thresholdModel.min)/3*i));
		}
		chart.addInnerTicksAxis(InnerR, rlabels2);//内部文字所在区域的圆圈比例,内部的文字。， 均匀全覆盖？？
	}
	
	private void setNullAxis(){
		List<Float> ringPercentage = new ArrayList<Float>();
		ringPercentage.add(1f);

		List<Integer> rcolor = new ArrayList<Integer>();
		rcolor.add(Color.rgb(133, 206, 130));
		chart.addStrokeRingAxis(1f, InnerR, ringPercentage, rcolor);//外圈大小，内圈大小，分段比例，分段颜色
		
		List<String> rlabels2 = new ArrayList<String>();
//		for (int i = 0; i <= 3; i++) {
//			rlabels2.add((thresholdModel.min + (thresholdModel.max-thresholdModel.min)/3*i) + "MM");
//		}
//		chart.addInnerTicksAxis(InnerR, rlabels2);//内部文字所在区域的圆圈比例,内部的文字。， 均匀全覆盖？？
	}
	

	public void setCurrentStatus(float percentage) {
		// 清理
		chart.clearAll();

		mPercentage = percentage;
		// 设置当前百分比
		chart.getPointer().setPercentage(mPercentage);
		addAxis();
		addPointer();
		addAttrInfo();
	}

	@Override
	public void render(Canvas canvas) {
		// TODO Auto-generated method stub
		try {
			chart.render(canvas);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

}
