//package com.ssiot.remote.yun.widget;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.Resources;
//import android.graphics.Point;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Build.VERSION;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.Display;
//import android.view.Window;
//import android.view.WindowManager;
////import com.xiaomi.smarthome.application.SHApplication;
////import com.xiaomi.smarthome.library.common.ApiHelper;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//
//public class DisplayUtils{
//  static final String a = DisplayUtils.class.getSimpleName();
//
//  public static int a(float paramFloat){
//    return b(SHApplication.g(), paramFloat);
//  }
//
//  public static int a(Context paramContext, float paramFloat)
//  {
//    return (int)(0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().density);
//  }
//
//  public static Point a(Activity paramActivity)
//  {
//    Point localPoint = new Point();
//    if (ApiHelper.e)
//      paramActivity.getWindowManager().getDefaultDisplay().getSize(localPoint);
//    while (true)
//    {
//      return localPoint;
//      Display localDisplay = paramActivity.getWindowManager().getDefaultDisplay();
//      localPoint.x = localDisplay.getWidth();
//      localPoint.y = localDisplay.getHeight();
//    }
//  }
//
//  public static BitmapDrawable a(Context paramContext)
//  {
//    try
//    {
//      Class localClass = Class.forName("miui.content.res.ThemeResources");
//      Class[] arrayOfClass = new Class[1];
//      arrayOfClass[0] = Context.class;
//      Method localMethod = localClass.getDeclaredMethod("getLockWallpaperCache", arrayOfClass);
//      Object[] arrayOfObject = new Object[1];
//      arrayOfObject[0] = paramContext;
//      localBitmapDrawable = (BitmapDrawable)localMethod.invoke(localClass, arrayOfObject);
//      return localBitmapDrawable;
//    }
//    catch (Exception localException)
//    {
//      while (true)
//        BitmapDrawable localBitmapDrawable = null;
//    }
//  }
//
//  public static void a(Context paramContext, int paramInt1, int paramInt2)
//  {
//    if (paramContext == null);
//    while (true)
//    {
//      return;
//      if ((paramContext instanceof Activity))
//      {
//        Activity localActivity = (Activity)paramContext;
//        Log.d(a, "OverridePending:Activity=" + localActivity);
//        localActivity.overridePendingTransition(paramInt1, paramInt2);
//      }
//    }
//  }
//
//  public static void a(Window paramWindow)
//  {
//    if (Build.VERSION.SDK_INT < 19);
//    while (true)
//    {
//      return;
//      try
//      {
//        Class localClass1 = paramWindow.getClass();
//        Class localClass2 = Class.forName("android.view.MiuiWindowManager$LayoutParams");
//        int i = localClass2.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT").getInt(localClass2);
//        int j = localClass2.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE").getInt(localClass2);
//        Class[] arrayOfClass = new Class[2];
//        arrayOfClass[0] = Integer.TYPE;
//        arrayOfClass[1] = Integer.TYPE;
//        Method localMethod = localClass1.getMethod("setExtraFlags", arrayOfClass);
//        Object[] arrayOfObject = new Object[2];
//        arrayOfObject[0] = Integer.valueOf(i);
//        arrayOfObject[1] = Integer.valueOf(j | i);
//        localMethod.invoke(paramWindow, arrayOfObject);
//        paramWindow.addFlags(67108864);
//      }
//      catch (Exception localException)
//      {
//      }
//    }
//  }
//
//  public static int b(Context paramContext, float paramFloat)
//  {
//    return (int)(0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().density);
//  }
//}