
package com.ssiot.remote.yun.unit;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> mList;

    public ViewPagerAdapter(ArrayList<View> paramArrayList) {
        mList = paramArrayList;
    }

    public void destroyItem(View paramView, int paramInt, Object paramObject) {
        ((ViewPager) paramView).removeView((View) this.mList.get(paramInt));
    }

    public void finishUpdate(View paramView) {
    }

    public int getCount() {
        return this.mList.size();
    }

    public Object instantiateItem(View paramView, int paramInt) {
        ((ViewPager) paramView).addView((View) this.mList.get(paramInt));
        return this.mList.get(paramInt);
    }

    public boolean isViewFromObject(View paramView, Object paramObject) {
        if (paramView == paramObject) {
            return true;
        } else {
            return false;
        }
    }

    public void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader) {
    }

    public Parcelable saveState() {
        return null;
    }

    public void startUpdate(View paramView) {
    }
}
