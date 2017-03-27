package com.ssiot.remote.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

public class SquareImageView extends ImageView{
    public SquareImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(getDefaultSize(0,widthMeasureSpec),getDefaultSize(0,heightMeasureSpec));
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);  
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);  
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);  
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);  
        // height is equal to  width
        heightSize = widthSize = heightSize < widthSize ? widthSize : heightSize;
        
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
}