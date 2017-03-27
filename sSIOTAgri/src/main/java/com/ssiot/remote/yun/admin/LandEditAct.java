package com.ssiot.remote.yun.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ssiot.agri.R;
import com.ssiot.fish.HeadActivity;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.LandModel;
import com.ssiot.remote.yun.webapi.WS_Land;

public class LandEditAct extends HeadActivity{
	private static final String tag = "LandEditAct";
	EditText edit_landname;
	SharedPreferences mPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideActionBar();
		setContentView(R.layout.act_land_edit);
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		initViews();
	}
	
	private void initViews(){
		edit_landname = (EditText) findViewById(R.id.edit_landname);
		initTitleBar();
	}
	
	private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LandModel model = new LandModel();
                model._name = edit_landname.getText().toString();
                model._areaid = mPref.getInt(Utils.PREF_AREAID, 0);
                if (!TextUtils.isEmpty(model._name) && model._areaid > 0){
                    new Thread(new Runnable() {
                        public void run() {
                            int ret = new WS_Land().Save(0, model._areaid, model._name, "");
                            if (ret > 0){
                            	setResult(RESULT_OK);
                                finish();
                            } else {
                            	sendToast("保存失败");
                            }
                        }
                    }).start();
                } else {
                	showToast("请填写完整");
                }
            }
        });
        TextView titleLeft = (TextView) findViewById(R.id.title_bar_left);
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}