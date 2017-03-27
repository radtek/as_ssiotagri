package com.ssiot.remote.yun.manage;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPProductPlanModel;
import com.ssiot.remote.data.model.ERPProductTypeModel;
import com.ssiot.remote.yun.webapi.ERPProductType;
import com.ssiot.remote.yun.webapi.ProductPlan;

public class EditProductPlanAct extends HeadActivity{
	private static final String tag = "EditProductPlanAct";
	
	private TextView mCropTextView;
	private EditText mNameEdit;
	
	private int userId = 0;
	private int mCropID = -1;
	private String cropStr = "";
	ArrayList<Integer> taskIDs = new ArrayList<Integer>();
	
	private List<ERPProductTypeModel> mAllProductTypeModel = new ArrayList<ERPProductTypeModel>();
	
	private static final int MSG_ADD_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_ADD_END:
                    finish();
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
		setContentView(R.layout.activity_productplan_new);
		initViews();
		initTitleBar();
	}
	
	private void initViews(){
        mCropTextView = (TextView) findViewById(R.id.txt_croptype);
        mNameEdit = (EditText) findViewById(R.id.edit_batchname);
    }
	
	private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ERPProductPlanModel model = new ERPProductPlanModel();
                model._id = 0;
                model._owenerid = userId;
                model._name = mNameEdit.getText().toString();
                model._croptype = mCropID;
                String taskids = "";
//                JSONArray ja = new JSONArray();
                for (int i = 0; i < taskIDs.size(); i ++){
//                	JSONObject jo = new JSONObject();
//                	try {
//						jo.put("id", taskIDs.get(i).intValue());
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//                	ja.put(jo);
                	taskids += taskIDs.get(i).intValue() + ",";
                }
                if (taskids.endsWith(",")){
                	taskids = taskids.substring(0, taskids.length() - 1);
                }
                model._taskids = taskids;
                if (!TextUtils.isEmpty(model._name) && model._croptype > 0 && taskIDs.size() > 0){
                    new Thread(new Runnable() {
                        public void run() {
                            int ret = new ProductPlan().Save(model);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(EditProductPlanAct.this, "请填写完整", Toast.LENGTH_SHORT).show();
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
        titleView.setText("新建一个方案");
    }
	
	public void ClickFunc(View v) {
        Log.v(tag, "--------ClickFunc-------" );
        switch (v.getId()) {
            case R.id.row_croptype:
//                showCropPickDialog();
                new GetAllCropsThread().start();
                break;
            case R.id.row_tasks:
            	if (mCropID > 0){
            		Intent intent = new Intent(this, TaskPickAct.class);
                	intent.putExtra("croptypeid", mCropID);
                	startActivityForResult(intent, REQUEST_TASKS);
            	} else {
            		showToast("请先选择作物类别");
            	}
            	break;
//            case R.id.row_facilities:
//            	new getAllFacilitiesThread().start();
//            	break;
//            case R.id.row_plan:
//            	new getAllPlansThread().start();
//            	break;
//            case R.id.row_time1:
//                showDatePickerDialog1();
//                break;
//            case R.id.row_time2:
//            	showDatePickerDialog2();
//            	break;

            default:
                break;
        }
    }
	
	private static final int REQUEST_TASKS = 1;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_TASKS:
			if (RESULT_OK == resultCode){
				taskIDs = data.getIntegerArrayListExtra("extra_ids");
				((TextView) findViewById(R.id.txt_tasks)).setText("选择了" + taskIDs.size() + "个任务");
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void showCropPickDialog(){
        if (mAllProductTypeModel.size() <= 0){
            Toast.makeText(this, "无种类信息,请重试", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[mAllProductTypeModel.size()];
        for (int i = 0; i < mAllProductTypeModel.size(); i ++){
            types[i] = mAllProductTypeModel.get(i)._name;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCropID = mAllProductTypeModel.get(which)._id;
                cropStr = mAllProductTypeModel.get(which)._name;
            }
        });
        bui.setTitle("选择种类").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCropTextView.setText(cropStr);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
	
	private class GetAllCropsThread extends Thread{
        @Override
        public void run() {
            List<ERPProductTypeModel> list = new ERPProductType().GetProductType();
            if (null != list && list.size() > 0){
                mAllProductTypeModel.clear();
                mAllProductTypeModel.addAll(list);
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
}