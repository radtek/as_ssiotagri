package com.ssiot.remote.yun.unit;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.ssiot.agri.R;

import java.lang.reflect.Field;

public class MyNumberPicker extends NumberPicker {
    private static final String tag = "MyNumberPicker";
    private int mTxtColorRes = R.color.DarkGreen;
    
    public MyNumberPicker(Context context) {
        super(context);
    }
 
    public MyNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyNumberPicker);
        mTxtColorRes = ta.getResourceId(R.styleable.MyNumberPicker_mytextcolor, R.color.DarkGreen);
        Log.v(tag, "----mTxtColorRes----" + mTxtColorRes);
        ta.recycle();
    }
 
    public MyNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyNumberPicker, defStyleAttr, 0);
        mTxtColorRes = ta.getResourceId(R.styleable.MyNumberPicker_mytextcolor, R.color.DarkGreen);
        Log.v(tag, "----mTxtColorRes_2----" + mTxtColorRes);
        ta.recycle();
    }
 
    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }
 
    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }
 
    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }
 
    public void updateView(View view) {
        if (view instanceof TextView) {
            Log.e(tag, "----updateView-mTxtColorRes = "+mTxtColorRes);//非常奇怪了，颜色怎么改动都是0？
            //这里修改字体的属性
            ((TextView) view).setTextColor(getResources().getColor(R.color.white));
            ((TextView) view).setTextSize(35);
        }
    }
    
    public void updateAll(){//TODO 无效啊 ，上下没变 且滑动后还是一样 
        int count = getChildCount();
        for (int i = 0; i < count; i ++){
            updateView(getChildAt(i));
        }
    }
    
    //NumberPicker分割线颜色 通过反射拿到mSelectionDivider属性
    private void setNumberPickerDividerColor(NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, new ColorDrawable(this.getResources().getColor(R.color.DarkGreen)));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
 
}