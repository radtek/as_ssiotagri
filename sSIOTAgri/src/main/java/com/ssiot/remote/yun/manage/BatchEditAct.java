package com.ssiot.remote.yun.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.AgricultureFacilityModel;
import com.ssiot.remote.data.model.ERPProductTypeModel;
import com.ssiot.remote.data.model.ERPProductBatchModel;
import com.ssiot.remote.data.model.ERPProductPlanModel;
import com.ssiot.remote.yun.webapi.ERPProductType;
import com.ssiot.remote.yun.webapi.ProductBatch;
import com.ssiot.remote.yun.webapi.ProductPlan;
import com.ssiot.remote.yun.webapi.WS_API;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BatchEditAct extends HeadActivity{
    private static final String tag = "BatchEditAct";
    
    private int userId = 0;
    private int mCropID = -1;
    private String mFacilityIDs = "";
    private int mPlanID = -1;
    
    private TextView mCropTextView;
    private EditText mNameEdit;
    private TextView mFacilitiesTextView;
    private TextView mPlanTextView;
    private TextView mTxtTime1;
    private TextView mTxtTime2;
    
    private String cropStr = "";
    private String facilityStr = "";
    private String planStr = "";
    
    private Timestamp mTimeStamp1;
    private Timestamp mTimeStamp2;
    private List<ERPProductTypeModel> mAllCropsModel = new ArrayList<ERPProductTypeModel>();
    private List<AgricultureFacilityModel> mAllFacilitiesModel = new ArrayList<AgricultureFacilityModel>();
    boolean[] statusbools;
    private List<ERPProductPlanModel> mAllPlansModel = new ArrayList<ERPProductPlanModel>();
    
    private static final int MSG_ADD_END = 1;
    private static final int MSG_GET_TYPES_END = 3;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_ADD_END:
                    finish();
                    break;
                case MSG_GET_TYPES_END:
                    break;
                default:
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        userId = Utils.getIntPref(Utils.PREF_USERID, this);
        setContentView(R.layout.activity_batch_new);
        initViews();
        initTitleBar();
    }
    
    private void initViews(){
        mCropTextView = (TextView) findViewById(R.id.txt_croptype);
        mNameEdit = (EditText) findViewById(R.id.edit_batchname);
        mFacilitiesTextView = (TextView) findViewById(R.id.txt_facilites);
        mPlanTextView = (TextView) findViewById(R.id.txt_plan);
        mTxtTime1 = (TextView) findViewById(R.id.txt_time1);
        mTxtTime2 = (TextView) findViewById(R.id.txt_time2);
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ERPProductBatchModel model = new ERPProductBatchModel();
                model._id = 0;
                model._inputsinid = 0;//
                model._name = mNameEdit.getText().toString();
                model._croptype = mCropID;
                model._facilityids = mFacilityIDs;
                model._planid = mPlanID;
                model._expectstart = mTimeStamp1;
                model._expectend = mTimeStamp2;
                model._userid = userId;
                if (!TextUtils.isEmpty(model._name) && model._croptype > 0 && null != mTimeStamp1 && null != mTimeStamp2 && 
                		!TextUtils.isEmpty(model._facilityids)){
                    new Thread(new Runnable() {
                        public void run() {
                            int ret = new ProductBatch().Save(model);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(BatchEditAct.this, "请填写完整", Toast.LENGTH_SHORT).show();
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
        
        TextView titleView = (TextView) findViewById(R.id.title_bar_title);
        titleView.setText("新建一个批次");
    }
    
    public void ClickFunc(View v) {
        Log.v(tag, "--------ClickFunc-------" );
        switch (v.getId()) {
            case R.id.row_croptype:
//                showCropPickDialog();
                new GetAllCropsThread().start();
                break;
            case R.id.row_facilities:
            	new getAllFacilitiesThread().start();
            	break;
            case R.id.row_plan:
            	new getAllPlansThread().start();
            	break;
            case R.id.row_time1:
                showDatePickerDialog1();
                break;
            case R.id.row_time2:
            	showDatePickerDialog2();
            	break;

            default:
                break;
        }
    }
    
    private void showCropPickDialog(){
        if (mAllCropsModel.size() <= 0){
            Toast.makeText(this, "未查询到产品种类信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[mAllCropsModel.size()];
        for (int i = 0; i < mAllCropsModel.size(); i ++){
            types[i] = mAllCropsModel.get(i)._name;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCropID = mAllCropsModel.get(which)._id;
                cropStr = mAllCropsModel.get(which)._name;
            }
        });
        bui.setTitle("作物").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCropTextView.setText(cropStr);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private void showFacilityPickDialog(){
        if (mAllFacilitiesModel.size() <= 0){
            Toast.makeText(this, "此用户无设施信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[mAllFacilitiesModel.size()];
        for (int i = 0; i < mAllFacilitiesModel.size(); i ++){
            types[i] = mAllFacilitiesModel.get(i)._name;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setMultiChoiceItems(types,  statusbools, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				statusbools[which] = isChecked;
				mFacilityIDs = buildFacilityIDs();
                
			}
		});
        bui.setTitle("设施").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFacilitiesTextView.setText(facilityStr);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private String buildFacilityIDs(){
//    	JSONArray ja = new JSONArray();
    	String fids = "";
    	facilityStr = "";
    	for (int i = 0; i < statusbools.length; i ++){
    		if (statusbools[i]){
    			facilityStr += mAllFacilitiesModel.get(i)._name + ",";
    			fids += mAllFacilitiesModel.get(i)._id + ",";
//    			JSONObject jo = new JSONObject();
//    			try {
//					jo.put("id", mAllFacilitiesModel.get(i)._id);
//					ja.put(jo);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
    		}
    	}
    	if (facilityStr.endsWith(",")){
    		facilityStr = facilityStr.substring(0, facilityStr.length() - 1);
    	}
    	if (fids.endsWith(",")){
    		fids = fids.substring(0, fids.length() - 1);
    	}
    	return fids;
    }
    
    private void showPlanPickDialog(){
        if (mAllPlansModel.size() <= 0){
            Toast.makeText(this, "此用户无方案信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[mAllPlansModel.size()];
        for (int i = 0; i < mAllPlansModel.size(); i ++){
            types[i] = mAllPlansModel.get(i)._name;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPlanID = mAllPlansModel.get(which)._id;
                planStr = mAllPlansModel.get(which)._name;
            }
        });
        bui.setTitle("方案").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPlanTextView.setText(planStr);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private void showDatePickerDialog1(){
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dia_date_time_pick, null);
        final DatePicker dp = (DatePicker) view.findViewById(R.id.date_pick);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.time_pick);
        bui.setTitle("时间选择").setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = new Date(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
                mTimeStamp1 = new Timestamp(d.getTime());
                String str = formatter.format(d);
                mTxtTime1.setText("时间:"+str);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private void showDatePickerDialog2(){
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dia_date_time_pick, null);
        final DatePicker dp = (DatePicker) view.findViewById(R.id.date_pick);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.time_pick);
        bui.setTitle("时间选择").setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = new Date(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
                mTimeStamp2 = new Timestamp(d.getTime());
                String str = formatter.format(d);
                mTxtTime2.setText("时间:"+str);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private class GetAllCropsThread extends Thread{
        @Override
        public void run() {
            List<ERPProductTypeModel> list = new ERPProductType().GetProductType();//暂时渔管家
            if (null != list && list.size() > 0){
                mAllCropsModel.clear();
                mAllCropsModel.addAll(list);
                runOnUiThread(new Runnable() {
					public void run() {
						showCropPickDialog();
					}
				});
            } else {
            	runOnUiThread(new Runnable() {
					public void run() {
						showToast("出现异常");
					}
				});
            }
        }
    }
    
    private class getAllFacilitiesThread extends Thread{
        @Override
        public void run() {
        	int areaid = Utils.getIntPref(Utils.PREF_AREAID, BatchEditAct.this);
//            List<AgricultureFacilityModel> list = new FisheriesFacility().GetModelList(" AreaID in (" +areaid +")");//TODO 渔业  webapi
            List<AgricultureFacilityModel> list = new WS_API().GetFacilitiesByUser(userId);
            if (null != list && list.size() > 0){
                mAllFacilitiesModel.clear();
                mAllFacilitiesModel.addAll(list);
                statusbools = new boolean[list.size()];
                for (int i = 0; i < mAllFacilitiesModel.size(); i ++){
                	statusbools[i] = false;
                }
                runOnUiThread(new Runnable() {
					public void run() {
						showFacilityPickDialog();
					}
				});
            } else {
            	runOnUiThread(new Runnable() {
					public void run() {
						showToast("出现异常");
					}
				});
            }
        }
    }
    
    private class getAllPlansThread extends Thread{
        @Override
        public void run() {
            List<ERPProductPlanModel> list = new ProductPlan().GetProductPlan(userId);//"(OwnerID=0 or OwnerID="+userId+ ") and ");
            if (null != list && list.size() > 0){
                mAllPlansModel.clear();
                mAllPlansModel.addAll(list);
                runOnUiThread(new Runnable() {
					public void run() {
						showPlanPickDialog();
					}
				});
            } else {
            	runOnUiThread(new Runnable() {
					public void run() {
						showToast("出现异常");
					}
				});
            }
        }
    }
    
}