package com.ssiot.fish.question.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class PicModelGridView extends GridView{
//    private final int hMeasureSpec;
    
    public PicModelGridView(Context context) {
        this(context, null, 0);
    }
    
    public PicModelGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PicModelGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        hMeasureSpec = MeasureSpec.makeMeasureSpec(120, MeasureSpec.EXACTLY);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);//高度自适应http://www.eoeandroid.com/thread-914121-1-2.html?_dsign=e96b2d24
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
    
    
    
}