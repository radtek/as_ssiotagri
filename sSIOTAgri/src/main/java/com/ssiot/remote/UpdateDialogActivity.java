package com.ssiot.remote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.agri.R;
import com.ssiot.remote.receiver.SsiotReceiver;

import java.util.HashMap;

public class UpdateDialogActivity extends Activity{//只能是Activity ！！
    private static final String tag = "UpdateDialogActivity";
    private SharedPreferences mPref;
    View sureView;
    View cancelView;

    public static final String TAG_VERSIONINFO = "versionInfo";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.dialog_update_chose);
        final HashMap<String, String> versionxml = (HashMap<String, String>) getIntent().getSerializableExtra("versionxmlmap");
        TextView txt_detail = (TextView) findViewById(R.id.txt_detail);
        String versionInfo = versionxml.get(TAG_VERSIONINFO);
        if (!TextUtils.isEmpty(versionInfo)){
            txt_detail.setText(versionInfo);
        }
        sureView = findViewById(R.id.buttonSure);
        cancelView = findViewById(R.id.buttonBack);
        sureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent(SsiotReceiver.ACTION_SSIOT_V_DOWNLOAD_PROGRESS));
                new UpdateManager(UpdateDialogActivity.this).startDownLoad(versionxml);
//                mNoti = mUpdaManager.showNotification(UpdateDialogActivity.this);
//                mUpdaManager.startDownLoad(tmpMap);
                Editor e = mPref.edit();
                e.putBoolean(Utils.PREF_AUTOUPDATE, true);
                e.commit();
                Toast.makeText(UpdateDialogActivity.this, "转向后台下载，可在通知栏中查看进度。", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        
        cancelView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Editor e = mPref.edit();
                e.putBoolean(Utils.PREF_AUTOUPDATE, false);
                e.commit();
                finish();
            }
        });
    }
    
}