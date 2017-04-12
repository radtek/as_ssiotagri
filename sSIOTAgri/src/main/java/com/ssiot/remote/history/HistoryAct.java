package com.ssiot.remote.history;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ssiot.agri.R;
import com.ssiot.fish.HeadActivity;

public class HistoryAct extends HeadActivity {
    private static final String tag = "HistoryAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            HistoryFragment frag = new HistoryFragment();
            Log.v(tag, "---------------fragcount:"+getSupportFragmentManager().getBackStackEntryCount());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, frag)
                    .commit();
        } else {
            savedInstanceState.remove("android:support:fragments");
            Log.v(tag, "---------------fragcount&&&&&&&&&&:"+getSupportFragmentManager().getBackStackEntryCount());
        }
    }
}
