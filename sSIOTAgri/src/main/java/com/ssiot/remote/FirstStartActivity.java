
package com.ssiot.remote;

import com.ssiot.remote.data.model.UserModel;
import com.ssiot.remote.yun.webapi.WS_User;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.ssiot.fish.FishMainActivity;
import com.ssiot.agri.R;

public class FirstStartActivity extends Activity {
    private final String tag = "FirstStartActivity";
    private String username = "";
    private String password = "";
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        mPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        if (mPref != null) {
        	checkPrefVersion();
        	startMainUI();
        } else {
            Log.e(tag, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!mPref = null");
        }
        super.onResume();
    }

    private void checkPrefVersion(){//版本更新后，有些pref以前没有。。如userkey
        try {
            int appVerCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            int prefVerCode = Utils.getIntPref("versioncode", this);
//            String vername = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;//测试版的临时做法 TODO
//            String prefname = Utils.getStrPref("versioname", this);
            if (appVerCode != prefVerCode ){//|| !vername.equalsIgnoreCase(prefname)
                Editor e = mPref.edit();
                e.clear().commit();//清除整个pref
                e.putInt("versioncode", appVerCode);
                e.commit();
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    
    }

    private void startLoginUI() {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(600);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(FirstStartActivity.this, LoginActivity.class);
		        startActivity(intent);
		        finish();
			}
		}).start();
    }
    
    private void startMainUI() {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(600);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(FirstStartActivity.this, FishMainActivity.class);
		        startActivity(intent);
		        finish();
			}
		}).start();
    }

}
