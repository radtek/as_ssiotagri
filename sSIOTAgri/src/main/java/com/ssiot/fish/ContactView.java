package com.ssiot.fish;

import com.ssiot.agri.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactView extends LinearLayout{
    TextView mTextView;
    ImageView mView;
    
    public ContactView(Context context) {
        super(context);
//        View view = LayoutInflater.from(context).inflate(R.layout.control_node_view, null);
        mTextView = new TextView(context);
        mTextView.setGravity(Gravity.CENTER_VERTICAL);
        mTextView.setBackgroundResource(R.drawable.bg_location_item_normal);
        mView = new ImageView(context);
        addView(mTextView);
        addView(mView);
    }
    
    public ContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ContactView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
    
    public void setText(String text){
        mTextView.setText(text);
    }
    
}