package com.ssiot.remote.yun.history;

import android.os.Bundle;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.TraceProfileModel;
import com.ssiot.remote.history.HistoryFragment;
import com.ssiot.remote.history.HistoryFragment.FHisBtnClickListener;

public class ProductHistoryAct extends HeadActivity implements FHisBtnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            HistoryFragment frag = new HistoryFragment();
            Bundle bundle = new Bundle();
            String userkey = Utils.getStrPref(Utils.PREF_USERKEY, this);
            bundle.putString("uniqueid", userkey);
            frag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, frag)
                    .commit();
        } else {
            savedInstanceState.remove("android:support:fragments");
        }
    }
    
    @Override
    public void onFHisBtnClick(TraceProfileModel m, boolean forceScan) {
//        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
//        HistoryDetailFragment hisDetailFragment = new HistoryDetailFragment();
//        mTransaction.replace(R.id.container, hisDetailFragment, MainActivity.TAG_HISTORY_DETAIL);
//        Bundle bundle2 = new Bundle();
////        bundle2.putString("title", bundle.getString("result"));
////        bundle2.putString("code", m._code);
//        hisDetailFragment.setArguments(bundle2);
//        hisDetailFragment.setModel(m);
//        mTransaction.addToBackStack(null);
//        mTransaction.commit();
        
    }
}