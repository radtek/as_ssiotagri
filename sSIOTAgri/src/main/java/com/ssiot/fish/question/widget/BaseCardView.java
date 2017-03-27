package com.ssiot.fish.question.widget;

import android.content.Context;
//import android.support.v7.widget.RecyclerView.LayoutParams;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ssiot.agri.R;

public abstract class BaseCardView extends FrameLayout {
    protected View aView;
    protected TextView bTextView;

    public BaseCardView(Context paramContext) {
        this(paramContext, null, 0);
    }

    public BaseCardView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public BaseCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        if ((paramAttributeSet == null) || (paramInt == 0)) {
            aView = LayoutInflater.from(paramContext).inflate(getLayoutId(), this);
            bTextView = ((TextView) finda(R.id.card_loading));
            a();
        }
        aView = LayoutInflater.from(paramContext).inflate(getLayoutId(), null);
    }

    public final View finda(int paramInt) {
        return aView.findViewById(paramInt);
    }

    protected abstract void a();

//    public void a(boolean paramBoolean1, boolean paramBoolean2) {
//        String str;
//        TextView localTextView2;
//        int i;
//        if (bTextView != null)
//        {
//            TextView localTextView1 = bTextView;
//            if (!(paramBoolean2))
//                break label49;
//            str = getRefreshNoMoreText();
//            localTextView1.setText(str);
//            localTextView2 = bTextView;
//            if (!(paramBoolean1))
//                break label58;
//            i = 0;
//        }
//        while (true)
//        {
//            while (true)
//            {
//                localTextView2.setVisibility(i);
//                return;
//                label49: str = getRefreshLoadingText();
//            }
//            label58: i = 8;
//        }
//    }

    public final View getInnerView() {
        return aView;
    }

    public final View getInnerViewForRecyclerView() {
//        RecyclerView.LayoutParams localLayoutParams = new RecyclerView.LayoutParams(-1, -2);
//        aView.setLayoutParams(localLayoutParams);
        return aView;
    }

    public abstract int getLayoutId();

    protected String getRefreshLoadingText() {
        return "加载更多...";
    }

    protected String getRefreshNoMoreText() {
        return "暂无更多";
    }

    public abstract void setInfo(Object paramObject);
}
