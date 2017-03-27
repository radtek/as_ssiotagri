package com.ssiot.remote.yun.expert;

import android.content.Intent;
import android.os.Bundle;
import com.ssiot.remote.ExpertFragment;
import com.ssiot.remote.ExpertFragment.FExpertBtnClickListener;
import com.ssiot.remote.expert.DiagnoseFishSelectActivity;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;

public class ExpertSystemAct extends HeadActivity implements FExpertBtnClickListener{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            ExpertFragment frag = new ExpertFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, frag)
                    .commit();
        } else {
            savedInstanceState.remove("android:support:fragments");
        }
    }
    
    @Override
    public void onFExpertBtnClick() {
        Intent intent = new Intent(ExpertSystemAct.this, DiagnoseFishSelectActivity.class);
        startActivity(intent);
    }
}