package com.ssiot.fish;

import java.lang.reflect.Field;
import com.ssiot.agri.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

//此方法废弃！！！！ application的theme会覆盖下面的导致You need to use a Theme.AppCompat theme
public class NoActionBarActivity extends Activity{
	private boolean windowTranslucent = false;
	
	private static final int MSG_TOAST = 898989;
    private Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case MSG_TOAST:
				String str = (String) msg.obj;
				showToast(str);
				break;

			default:
				break;
			}
    		
    	};
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//安卓自定义状态栏颜色以与APP风格保持一致
            windowTranslucent = true;
//            //透明状态栏  
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
//            //透明导航栏  
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); 
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);    
            tintManager.setStatusBarTintEnabled(true);    
            tintManager.setStatusBarTintResource(R.color.DarkGreen);//通知栏所需颜色  
        }
	}
	
	@Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (windowTranslucent){
            View rootView = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setPadding(0, getStatusBarHeight(), 0, 0);
        }
    }
	
	private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
	
	@TargetApi(19)     
    private void setTranslucentStatus(boolean on) {    
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        //当你把窗口flag设置成 FLAG_TRANSLUCENT_STATUS后，你的应用所占的屏幕扩大到全屏，
        //但是最顶上会有背景透明的状态栏，它的文字可能会盖着你的应用的标题栏，你可以手动将你的app显示的内容向下错出一个状态栏的高度，这样就能完成适配了。
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;    
        if (on) {    
            winParams.flags |= bits;    
        } else {    
            winParams.flags &= ~bits;    
        }    
        win.setAttributes(winParams);   
    }
	
	
	public void showToast(int strRes){
        Toast.makeText(this, strRes, Toast.LENGTH_SHORT).show();
    }
    
    public void showToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    
    public void sendToast(String str){
    	Message msg = mHandler.obtainMessage(MSG_TOAST);
    	msg.obj = str;
    	mHandler.sendMessage(msg);
    }
}