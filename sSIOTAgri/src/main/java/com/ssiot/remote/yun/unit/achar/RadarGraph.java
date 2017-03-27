package com.ssiot.remote.yun.unit.achar;

import android.view.View;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.ssiot.agri.R;

import java.util.ArrayList;
import java.util.Iterator;

public class RadarGraph extends View {
  public static final int EXE = 1;
  public static final int WIND = 0;//jingbo
  protected float angle;
  int bgColor;
  protected int centerX;
  protected int centerY;
  private int[] colorsSel;
  protected int count;
  MoreLineText lineText;
  private Paint paint;
  private int point_radius = 5;
  protected Point[] pts;
  ArrayList<Radar> radars;
  protected int radius;//半径
  private int[] regionValues;
  private Region[] regions;
  private int regionwidth = 40;
  Resources resources;
  int showIndex;
  protected String[] titles;
  int type;
  private Paint valuePaint;
  private Path valuePath;
  private int valueRulingCount = 5;
  private Point[] value_pts;

  public RadarGraph(Context context) {
    super(context);
    init(context);
  }

  public RadarGraph(Context context, int mtype) {
    super(context);
    type = mtype;
    init(context);
  }

  public RadarGraph(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    init(context);
  }

  public RadarGraph(Context context, AttributeSet attributeSet, int defStyleAttr) {
    super(context, attributeSet, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    int j = 0;
    resources = context.getResources();
    initText(context);
    count = titles.length;
    angle = (360 / count);
    int[] colors = new int[3];
    colors[0] = resources.getColor(R.color.blue_2);//R.color.sta_line
    colors[1] = resources.getColor(R.color.sta_high);
    colors[2] = resources.getColor(R.color.sta_low);
    colorsSel = colors;
    paint = new Paint();
    valuePaint = new Paint();
    valuePaint.setAntiAlias(true);
    valuePaint.setSubpixelText(true);
    pts = new Point[count];
    value_pts = new Point[count];
    valuePath = new Path();
    int i = 0;
    while (i < count){
        pts[i] = new Point();
        value_pts[i] = new Point();
        ++i;
    }
    regionValues = new int[2 * count * valueRulingCount];//Region 代表屏幕上的一个区域
    regions = new Region[2 * count * valueRulingCount];
    while(j < regions.length){
        regions[j] = new Region();
        ++j;
    }
  }

  protected void drawTitleText(Canvas canvas, Paint paint, float paramFloat) {
      paint.setTextAlign(Paint.Align.CENTER);
    int i = 0;
    while (i < count){
        int x = pts[i].x;
        int y = pts[i].y;
        if (angle * i == 0F){
            y = (int)(y - paramFloat);
        } else if (angle * i == 180.0F){
            y = (int)(paramFloat + y);
        }
        
        if (angle*i > 0F && angle * i < 180.0F){
            x = (int)(paramFloat + x);
        } else if (angle * i > 180.0F && angle*i < 360F){
            x = (int)(x - paramFloat);
        }
        lineText = new MoreLineText(titles[i], x, y, 70, 80, paint);
        lineText.InitText();
        lineText.DrawText(canvas);
        ++i;
    }
  }

  protected void formatTextGravity(int paramInt, Paint paramPaint) {
    paramPaint.setTextAlign(Paint.Align.CENTER);
  }

  protected void formatValText(int paramInt, Paint paramPaint){
  }

  protected void formatValTextColor(Paint paramPaint) {
    paramPaint.setColor(resources.getColor(R.color.c333333));
  }

  int getBgColor(Resources resources) {
    return resources.getColor(R.color.bgColor);
  }

  public ArrayList<Radar> getRadars() {
    return radars;
  }

  public int getShowIndex() {
    return showIndex;
  }

  public final String[] getTitles() {
    return titles;
  }

  public final int getType() {
    return type;
  }

  protected void initText(Context context) {
    String[] arrayOfString = new String[8];
    arrayOfString[0] = "北风";
    arrayOfString[1] = "东北";
    arrayOfString[2] = "东风";
    arrayOfString[3] = "东南";
    arrayOfString[4] = "南风";
    arrayOfString[5] = "西南";
    arrayOfString[6] = "西风";
    arrayOfString[7] = "西北";
    titles = arrayOfString;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    float f;
    int colorIndex;
    Iterator iterator;
    int i3;
    Paint localPaint1 = new Paint();
    formatValTextColor(localPaint1);
    localPaint1.setStyle(Paint.Style.FILL_AND_STROKE);
    localPaint1.setStrokeWidth(1F);
    localPaint1.setTextSize(20.0F);
    localPaint1.setAntiAlias(true);
    localPaint1.setSubpixelText(true);
    Paint localPaint2 = new Paint();
    bgColor = getBgColor(resources);
    canvas.drawColor(bgColor);
    localPaint2.setColor(resources.getColor(R.color.c333333));
    localPaint2.setStyle(Paint.Style.STROKE);
    localPaint2.setStrokeWidth(1F);
    localPaint2.setAntiAlias(true);
    localPaint2.setSubpixelText(true);
    int i = centerX / 5;
    int radiusTmp = radius;
    int l = 0;
    while(l < count){
        canvas.drawCircle(centerX, centerY, radiusTmp, localPaint2);
        radiusTmp -= i;
        canvas.drawLine(centerX, centerY, pts[l].x, pts[l].y, localPaint1);
        ++l;
    }
    localPaint2.setStyle(Paint.Style.FILL_AND_STROKE);
    localPaint2.setTextSize(20.0F);
    drawTitleText(canvas, localPaint1, 2F * -localPaint2.getFontMetrics().ascent);//ascent baseline以上的height
    f = 0F;
    colorIndex = 0;
    
    canvas.drawText("0", centerX, centerY, localPaint1);
    
    if (radars != null){
        iterator = radars.iterator();
        if (iterator.hasNext()){
            Radar localRadar = (Radar)iterator.next();
            if (f < localRadar.getMaxVal())
              f = localRadar.getMaxVal() * 1.2F;//10.0F + localRadar.getMaxVal()
            for (int i2 = 0; i2 < count; i2 ++){
                value_pts[i2].x = (int)(centerX + (pts[i2].x - centerX) * localRadar.getValues()[i2] / f);
                value_pts[i2].y = (int)(centerY + (pts[i2].y - centerY) * localRadar.getValues()[i2] / f);
                if ((localRadar.getValues()[i2] == localRadar.getMaxVal()) && (localRadar.getMaxVal() != 0F)){
                  formatValText(i2, localPaint1);
                  canvas.drawText(String.valueOf(localRadar.getValues()[i2]), value_pts[i2].x, value_pts[i2].y, localPaint1);
                }
            }
            valuePath.reset();
            valuePaint.setAntiAlias(true);
            valuePaint.setColor(colorsSel[colorIndex]);
            valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            i3 = 0;
            valuePath.moveTo(value_pts[i3].x, value_pts[i3].y);
            for (i3 = 0; i3 < pts.length; i3 ++){
                canvas.drawCircle(value_pts[i3].x, value_pts[i3].y, point_radius, valuePaint);
                valuePath.lineTo(value_pts[i3].x, value_pts[i3].y);
            }
            valuePaint.setAlpha(150);
            canvas.drawPath(valuePath, valuePaint);
            ++colorIndex;
        }
      }
  }

  @Override
  public boolean onKeyLongPress(int paramInt, KeyEvent paramKeyEvent) {
    return super.onKeyLongPress(paramInt, paramKeyEvent);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldW, int oldH) {
    radius = (-60 + Math.min(h, w) / 2);
    centerX = (w / 2);
    centerY = setCenterY(h);
    for (int i = 0; i < count; i ++){
        pts[i].x = (centerX + (int)(radius * Math.sin(Math.toRadians(angle * i))));
        pts[i].y = (centerY - (int)(radius * Math.cos(Math.toRadians(angle * i))));
        for (int j = 1; j < 2 * valueRulingCount; j ++){
            int k = centerX + j * (pts[i].x - centerX) / 2 * valueRulingCount;
            int l = centerY + j * (pts[i].y - centerY) / 2 * valueRulingCount;
            regions[(-1 + j + 2 * i * valueRulingCount)].set(k - regionwidth / 2, l - regionwidth / 2, k + regionwidth / 2, l + regionwidth / 2);
            regionValues[(-1 + j + 2 * i * valueRulingCount)] = j;
        }
    }
    postInvalidate();
    super.onSizeChanged(w, h, oldW, oldH);
  }

  @Override
  public boolean onTouchEvent(MotionEvent motionEvent){
    motionEvent.getAction();
    float f1 = motionEvent.getX();
    float f2 = motionEvent.getY();
    switch (motionEvent.getAction()) {
        case MotionEvent.ACTION_UP:
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            break;
        case MotionEvent.ACTION_DOWN:
            int i = 0;
            while(i < regions.length){
                if ((regions[i].contains((int)f1, (int)f2))){
                    Log.e("MainMenuAct??--", "regionValues[i]:" + regionValues[i]);
                }
                i ++;
            }
            return true;
        default:
            return super.onTouchEvent(motionEvent);
    }
    return super.onTouchEvent(motionEvent);
  }

  protected int setCenterY(int paramInt) {
    return (paramInt / 2);
  }

  public void setRadars(ArrayList<Radar> paramArrayList) {
    if (radars != null)
      radars.clear();
    radars = paramArrayList;
  }

  public void setShowIndex(int paramInt) {
    showIndex = paramInt;
  }

  public final void setTitles(String[] paramArrayOfString) {
    titles = paramArrayOfString;
  }

  public final void setType(int paramInt) {
    type = paramInt;
  }
}