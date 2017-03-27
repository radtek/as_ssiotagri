package com.ssiot.remote.yun.admin;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.agri.R;
import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.facility.GetLocationActivity;
import com.ssiot.remote.data.model.FacilityModel;
import com.ssiot.remote.data.model.LandModel;
import com.ssiot.remote.yun.webapi.WS_Facility;
import com.ssiot.remote.Utils;

public class FacilityEditAct extends HeadActivity{
	private static final String tag = "FacilityEditAct";
	
	EditText nameView;
//	View addrView;
	TextView longtiView;
	TextView latiView;
	EditText sizeEditText;
	float x;
	float y;
	SharedPreferences mPref;
	
	ArrayList<LandModel> landModels;
	int mLandID;
	String mLandStr;
	TextView txt_land;
	
	String[] facilityTypes = {"玻璃温室","联栋温室","简易薄膜大棚","大田","鱼塘","循环水养殖"};//TODO
	int mFacilityTypeID = 1;
	String mFacilityTypeStr;
	TextView txt_faType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideActionBar();
		setContentView(R.layout.act_facility_edit);
		landModels = (ArrayList<LandModel>) getIntent().getSerializableExtra("landmodels");
		initViews();
	}
	
	private void initViews(){
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		txt_land = (TextView) findViewById(R.id.txt_land);
		txt_faType = (TextView) findViewById(R.id.txt_fa_type);
        nameView = (EditText) findViewById(R.id.edit_facilityname);
//        addrView = findViewById(R.id.edit_addr);
        longtiView = (TextView) findViewById(R.id.edit_lng);
        latiView = (TextView) findViewById(R.id.edit_lat);
        sizeEditText = (EditText) findViewById(R.id.edit_cover);
        View.OnClickListener cli = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacilityEditAct.this, GetLocationActivity.class);
                startActivityForResult(intent, REQUEST_LOCATION);
            }
        };
        findViewById(R.id.btn_map_lng).setOnClickListener(cli);
        findViewById(R.id.btn_map_lat).setOnClickListener(cli);
        initTitleBar();
	}
	
	public void ClickFunc(View v) {
		switch (v.getId()) {
		case R.id.row_land:
			showLandPickDialog();
			break;
		case R.id.row_fa_type:
			showFacilityTypePickDia();
			break;
		}
	}
	
	private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sizeStr = sizeEditText.getText().toString();
                final FacilityModel model = new FacilityModel();
                model._landid = mLandID;
                model._facilitiesname = ((EditText) nameView).getText().toString();
//                model._addr = ((EditText) addrView).getText().toString();
                try {
                    model._areacovered = Float.parseFloat(sizeStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model._parentid = mPref.getInt(Utils.PREF_USERID, 0);
                model._longitude = x;
                model._latitude = y;
                model._areaid = mPref.getInt(Utils.PREF_AREAID, 0);
                model._facilitiestypeid = mFacilityTypeID;
                if (!TextUtils.isEmpty(model._facilitiesname) && model._parentid > 0 && model._areaid > 0
                		 && model._landid > 0){
                    new Thread(new Runnable() {
                        public void run() {
                            int ret = new WS_Facility().SaveAgricultureFacility(model);
                            if (ret > 0){
                            	setResult(RESULT_OK);
                                finish();
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
//                onBackPressed();
                finish();
            }
        });
    }
	
	private void showLandPickDialog(){
        if (null == landModels || landModels.size() <= 0){
            Toast.makeText(this, "未查询到已有地块信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[landModels.size()];
        for (int i = 0; i < landModels.size(); i ++){
            types[i] = landModels.get(i)._name;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mLandID = landModels.get(which)._landid;
                mLandStr = landModels.get(which)._name;
            }
        });
        bui.setTitle("请选择").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	txt_land.setText(mLandStr);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
	
    private void showFacilityTypePickDia(){
//        if (null == landModels || landModels.size() <= 0){
//            Toast.makeText(this, "未查询到已有地块信息", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        final String[] types = new String[landModels.size()];
//        for (int i = 0; i < landModels.size(); i ++){
//            types[i] = landModels.get(i)._name;
//        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(facilityTypes, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFacilityTypeID = which + 1;
                mFacilityTypeStr = facilityTypes[which];
            }
        });
        bui.setTitle("请选择").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	txt_faType.setText(mFacilityTypeStr);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
	
	private static final int REQUEST_LOCATION = 1;
    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent intent) {
        switch (requestcode) {
            case REQUEST_LOCATION:
                if (RESULT_OK == resultcode) {
                    x = intent.getFloatExtra("resultx", 0);
                    y = intent.getFloatExtra("resulty", 0);
                    longtiView.setText("" + x);
                    latiView.setText(""+y);
                }
                break;

            default:
                break;
        }
    }
}