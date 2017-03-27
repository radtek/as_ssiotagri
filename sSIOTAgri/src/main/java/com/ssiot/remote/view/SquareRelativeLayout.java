package com.ssiot.remote.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SquareRelativeLayout extends RelativeLayout{
    private static final String tag = "SquareRelativeLayout";
    public SquareRelativeLayout(Context context) {
        super(context);
    }
 
    public SquareRelativeLayout(Context context,AttributeSet attr) {
        super(context,attr);
    }
 
    public SquareRelativeLayout(Context context,AttributeSet attr,int defStyle) {
        super(context,attr,defStyle);
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(getDefaultSize(0,widthMeasureSpec),getDefaultSize(0,heightMeasureSpec));
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);  
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);  
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);  
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);  
        // height is equal to  width
        Log.v(tag, "----before widthSize:"+ widthSize + " -----" + heightSize);
        if (widthSize * heightSize == 0){//TODO 这是特殊处理，why在gridview中高度0
            heightSize = widthSize = (widthSize + heightSize);
        } else {
            heightSize = widthSize = heightSize < widthSize ? widthSize : heightSize;//取大值 在gridview的item_image.xml中用到
        }
        Log.v(tag, "----after widthSize:"+ widthSize + " -----" + heightSize);
        
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}