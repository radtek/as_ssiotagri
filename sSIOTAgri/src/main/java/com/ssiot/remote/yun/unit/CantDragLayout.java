package com.ssiot.remote.yun.unit;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

//原先想解决chart的滑动问题，可惜没用
public class CantDragLayout extends LinearLayout{
    private static final String tag = "CantDragLayout";
    
    public CantDragLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public CantDragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CantDragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(tag, "-----onTouchEvent-------");
        return super.onTouchEvent(event);
//        return true;//没用啊！
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.v(tag, "-----onInterceptTouchEvent-------");
        return super.onInterceptTouchEvent(ev);//不能复写，要继续往子view发
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.v(tag, "-----dispatchKeyEvent-------");
        return super.dispatchKeyEvent(event);
//        return true;
    }
    
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        Log.v(tag, "-----onScrollChanged-------");
        super.onScrollChanged(l, t, oldl, oldt);
    }
    
    
}