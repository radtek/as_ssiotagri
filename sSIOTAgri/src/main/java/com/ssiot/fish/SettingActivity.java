package com.ssiot.fish;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

import com.ssiot.agri.R;
import com.ssiot.remote.SettingFrag;
import com.ssiot.remote.SettingFrag.FSettingBtnClickListener;
import com.ssiot.remote.UpdateManager;
import com.ssiot.remote.Utils;

public class SettingActivity extends HeadActivity{
    private static final String tag = "SettingActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            SettingFrag mMainFragment = new SettingFrag();
            Log.v(tag, "---------------fragcount:"+getSupportFragmentManager().getBackStackEntryCount());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mMainFragment)
                    .commit();
        } else {
            savedInstanceState.remove("android:support:fragments");
            Log.v(tag, "---------------fragcount&&&&&&&&&&:"+getSupportFragmentManager().getBackStackEntryCount());
        }
    }
}