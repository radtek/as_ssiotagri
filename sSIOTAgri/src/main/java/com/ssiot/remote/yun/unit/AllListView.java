
package com.ssiot.remote.yun.unit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

//ListView全部展开
public class AllListView extends ListView {

    public AllListView(Context context) {
        super(context);
    }

    public AllListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AllListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
