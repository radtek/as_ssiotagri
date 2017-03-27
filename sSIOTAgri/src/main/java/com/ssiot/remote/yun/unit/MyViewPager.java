
package com.ssiot.remote.yun.unit;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ssiot.remote.yun.detail.sensors.SensorLineChartFrag;

public class MyViewPager extends ViewPager {
    private static final String tag = "MyViewPager";
    
    public MyViewPager(Context paramContext) {
        super(paramContext);
    }

    public MyViewPager(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }
    
    //解决嵌套了chartView的滑动问题
    @Override
    protected boolean canScroll(View arg0, boolean arg1, int arg2, int arg3, int arg4) {
//        Log.v(tag, "~~~~canScroll~~~~" + arg1 + " " + arg2 + " " + arg3 + " " + arg4);
//        boolean b = true;
        PagerAdapter localPagerAdapter = getAdapter();
        int pages = localPagerAdapter.getCount();
        for (int j = 0; j < pages; j ++){
            Fragment localFragment = ((FragmentPagerAdapter)localPagerAdapter).getItem(j);
            if (localPagerAdapter.isViewFromObject(arg0, localFragment) && localFragment instanceof SensorLineChartFrag){
                return ((SensorLineChartFrag) localFragment).canScroll(arg3, arg4);
            }
        }
        
        return super.canScroll(arg0, arg1, arg2, arg3, arg4);
    }
}
