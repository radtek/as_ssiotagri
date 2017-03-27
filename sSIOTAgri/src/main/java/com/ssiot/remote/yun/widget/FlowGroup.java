//package com.ssiot.remote.yun.widget;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.View.MeasureSpec;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.ImageView.ScaleType;
////import com.xiaomi.smarthome.library.common.util.DisplayUtils;
//
//public class FlowGroup extends ViewGroup{
//  private boolean a = true;
//  private int b;
//  private int c;
//  private int d = 0;
//  private boolean e = true;
//  private TagMoreClickListener mTagMoreClickListener;
//
//  public FlowGroup(Context paramContext){
//    super(paramContext);
//    a();
//  }
//
//  public FlowGroup(Context paramContext, AttributeSet paramAttributeSet){
//    super(paramContext, paramAttributeSet);
//    a();
//  }
//
//  public FlowGroup(Context paramContext, AttributeSet paramAttributeSet, int paramInt){
//    super(paramContext, paramAttributeSet, paramInt);
//    a();
//  }
//
//  private boolean a(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5){
//    boolean bool = false;
//    if (paramInt1 < this.d){
//    	return false;
//    } else {
//    	if ((paramInt5 + (paramInt2 + paramInt4 + this.b) >= paramInt3) && (paramInt2 + paramInt4 >= paramInt3)){
//    		return true;
//    	}
//    }
//  }
//
//  void a(){
//    this.b = DisplayUtils.a(16.0F);
//    this.c = DisplayUtils.a(18.0F);
//  }
//
//  @Override
//  protected void onLayout(boolean changed, int l, int t, int r, int b){
//    int i = getChildCount();
//    if (i == 0);
//    int wid;
//    int k;
//    int m;
//    int n;
//    ImageView localImageView;
//    int i2;
//    View localView;
//    int width;
//    int height;
//    while (true)
//    {
//      return;
//      wid = r - l;
//      k = 0;
//      m = 0;
//      n = 1;
//      localImageView = (ImageView)getChildAt(i - 1);
//      localImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//      int i1 = localImageView.getMeasuredWidth();
//      i2 = 0;
//      if (i2 < i - 1)
//      {
//        localView = getChildAt(i2);
//        if (localView.getVisibility() == 8)
//          break label294;
//        width = localView.getMeasuredWidth();
//        height = localView.getMeasuredHeight();
//        if (!a(n, k, wid, width, i1))
//          break;
//      }
//      if (this.e)
//        break label277;
//      localImageView.setImageResource(2130837655);
//      label130: if (k + localImageView.getMeasuredWidth() > j){
//        k = 0;
//        m += getChildAt(0).getMeasuredHeight() + this.c;
//      }
//      localImageView.layout(k, m, k + localImageView.getMeasuredWidth(), m + getChildAt(0).getMeasuredHeight());
//      localImageView.setOnClickListener(new View.OnClickListener()
//      {
//        public void onClick(View paramAnonymousView)
//        {
//          if (FlowGroup.a(FlowGroup.this) != null)
//            FlowGroup.a(FlowGroup.this).a(paramAnonymousView, FlowGroup.b(FlowGroup.this));
//        }
//      });
//    }
//    int i3;
//    if (k + width > j)
//    {
//      n++;
//      k = 0;
//      i3 = m + (height + this.c);
//      label236: localView.layout(k, i3, k + width, height + i3);
//      k += width + this.b;
//    }
//    while (true)
//    {
//      i2++;
//      m = i3;
//      break;
//      label277: localImageView.setImageResource(2130837653);
//      break label130;
//      i3 = m;
//      break label236;
//      label294: i3 = m;
//    }
//  }
//
//  @Override
//  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
//    int size = View.MeasureSpec.getSize(widthMeasureSpec);
//    int count = getChildCount();
//    if (count == 0){
//      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//      return;
//    }
//    ((ImageView)getChildAt(count - 1)).measure(0, 0);
//    int k = 0;
//    int m = 1;
//    int n = 0;
//    int i1 = 0;
//    label51: int i2;
//    int i3;
//    if (k < count)
//    {
//      View localView = getChildAt(k);
//      if (localView.getVisibility() != 8)
//      {
//        localView.measure(0, 0);
//        i2 = localView.getMeasuredWidth();
//        i3 = getChildAt(0).getMeasuredHeight() + this.c;
//        if (i1 + i2 > size)
//          if ((m >= 3) && (!this.a))
//            if (k >= count)
//              break label208;
//      }
//    }
//    label208: for (boolean bool = false; ; bool = true)
//    {
//      this.e = bool;
//      this.d = m;
//      setMeasuredDimension(size, n + getChildAt(0).getMeasuredHeight() + DisplayUtils.a(26.0F));
//      break;
//      m += 1;
//      n += i3;
//      i1 = 0;
//      i1 += i2 + this.b;
//      k++;
//      break label51;
//    }
//  }
//
//  public void setExpand(boolean paramBoolean)
//  {
//    this.a = paramBoolean;
//    requestLayout();
//    invalidate();
//  }
//
//  public void setMoreClickListener(TagMoreClickListener paramTagMoreClickListener)
//  {
//	  mTagMoreClickListener = paramTagMoreClickListener;
//  }
//
//  public static abstract interface TagMoreClickListener {
//    public abstract void a(View paramView, boolean paramBoolean);
//  }
//}