package com.ssiot.fish;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.VideoFragment;

public class VideoListActivity extends HeadActivity{
    private static final String tag = "VideoListActivity";
    private SharedPreferences mPref;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {//默认的savedInstanceState会存储一些数据，包括Fragment的实例
//          MainFragment mMainFragment = new MainFragment();
          Log.v(tag, "---------------fragcount:"+getSupportFragmentManager().getBackStackEntryCount());
//          getSupportFragmentManager().beginTransaction()
//                  .add(R.id.container, mMainFragment)
//                  .commit();
          
          if (!Utils.isNetworkConnected(this)){
              Toast.makeText(this, R.string.please_check_net, Toast.LENGTH_SHORT).show();
              return;
          }
          VideoFragment videoFragment = new VideoFragment();
//          mTransaction.replace(R.id.container, videoFragment, TAG_VIDEO);
          mTransaction.add(R.id.container, videoFragment);
          Bundle bundle = new Bundle();
          bundle.putString("title", "ttttteeeeesssst");
          videoFragment.setArguments(bundle);
//          mTransaction.addToBackStack(null);
          mTransaction.commit();
          
      } else {
          savedInstanceState.remove("android:support:fragments");//解决getactivity为空的问题？？
          Log.v(tag, "---------------fragcount&&&&&&&&&&:"+getSupportFragmentManager().getBackStackEntryCount());
      }
    }
}