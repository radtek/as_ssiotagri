package com.ssiot.remote.yun.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPProductTypeModel;
import com.ssiot.remote.data.model.ERPTaskModel;
import com.ssiot.remote.data.model.ERPTaskStageTypesModel;
import com.ssiot.remote.data.model.ERPTaskTypesModel;
import com.ssiot.remote.yun.webapi.ERPProductType;
import com.ssiot.remote.yun.webapi.Task;
import com.ssiot.remote.yun.webapi.TaskStageTypes;
import com.ssiot.remote.yun.webapi.TaskTypes;
import java.util.ArrayList;
import java.util.List;

public class EditTaskMouldAct extends HeadActivity{
    private static final String tag = "EditTaskMouldAct";
    
    private int userID;
    
    private TextView mCropTextView;
    EditText mEditTextContent;
    Spinner mStagePick;
    Spinner mTypePick;
//    TextView mTextViewStart;
//    TextView mTextViewEnd;
    ToggleButton mTogglePho;
    TextView mTextPho;
    ToggleButton mToggleLocation;
    TextView mTextLocation;
    ToggleButton mToggleFer;
    ToggleButton mTogglePesti;
    EditText mEditTextWorkLoad;
    
    private int mStageTypeID = 0;
    private int mTaskTypeID = 0;
    private ArrayList<String> stageArr = new ArrayList<String>();
    private ArrayList<String> taskTypeArr = new ArrayList<String>();
    List<ERPTaskStageTypesModel> mStageModels;
    List<ERPTaskTypesModel> mTypesModels;
    
    private int mCropID = -1;
	private String cropStr = "";
    private List<ERPProductTypeModel> mAllProductTypeModel = new ArrayList<ERPProductTypeModel>();
    
    private static final int MSG_ADD_END = 1;
    private static final int MSG_GETTYPES_END = 3;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_ADD_END:
                    finish();
                    break;
                case MSG_GETTYPES_END:
                    initStagePickSpinner(stageArr);//TODO
                    initTaskTypePickSpinner(taskTypeArr);
                    break;
                default:
                    break;
            }
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = Utils.getIntPref(Utils.PREF_USERID, this);
        hideActionBar();
        setContentView(R.layout.taskmould_new);
        initTitleBar();
        findViews();
        new GetStageAndTaskTypesThread().start();
    }
    
    private void findViews(){
    	mCropTextView = (TextView) findViewById(R.id.txt_croptype);
        mEditTextContent = (EditText) findViewById(R.id.editTextContent);
        mStagePick = (Spinner) findViewById(R.id.taskstage_pick);
        mTypePick = (Spinner) findViewById(R.id.tasktype_pick);
//        mTextViewStart = (TextView) findViewById(R.id.TextViewStart);
//        mTextViewEnd = (TextView) findViewById(R.id.TextViewEnd);
        mTogglePho = (ToggleButton) findViewById(R.id.toggleButtonPho);
        mToggleLocation = (ToggleButton) findViewById(R.id.toggleButtonLocation);
        mTextPho = (TextView) findViewById(R.id.textViewPho);
        mTextLocation = (TextView) findViewById(R.id.textViewLoc);
        mToggleFer = (ToggleButton) findViewById(R.id.toggleButtonFertilizer);
        mTogglePesti = (ToggleButton) findViewById(R.id.toggleButtonPesticide);
        mEditTextWorkLoad = (EditText) findViewById(R.id.editTextWorkLoad);
        
        mTogglePho.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTextPho.setText(isChecked ? "需要" : "不需要");
            }
        });
        mToggleLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTextLocation.setText(isChecked ? "需要" : "不需要");
            }
        });
        mToggleFer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((TextView) findViewById(R.id.textViewFertilizer)).setText(isChecked ? "需要" : "不需要");
            }
        });
        mTogglePesti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((TextView) findViewById(R.id.textViewPesticide)).setText(isChecked ? "需要" : "不需要");
            }
        });
        
        initStagePickSpinner(stageArr);
        initTaskTypePickSpinner(taskTypeArr);
    }
    
    public void ClickFunc(View v) {
        Log.v(tag, "--------ClickFunc-------" );
        switch (v.getId()) {
            case R.id.row_croptype:
                new GetAllCropsThread().start();
                break;
            default:
                break;
        }
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ERPTaskModel model = new ERPTaskModel();
                int userid = Utils.getIntPref(Utils.PREF_USERID, EditTaskMouldAct.this);
                model._id = 0;
                model._ownerid = userid;
                model._croptypeid = mCropID;
                model._stagetype = mStageTypeID;
                model._tasktype = mTaskTypeID;
                model._taskdetail = mEditTextContent.getText().toString();
                model._requirepic = mTogglePho.isChecked();
                model._requirelocaion = mToggleLocation.isChecked();
                String tables = (mToggleFer.isChecked() ? "ERPFertilizer" : "") + 
                        ((mToggleFer.isChecked()&& mTogglePesti.isChecked()) ? "," : "")
                            + (mTogglePesti.isChecked() ? "ERPPesticide" : "");
                model._requirefilltables = tables;
                if (!TextUtils.isEmpty(mEditTextWorkLoad.getText().toString())){
                	float load = Float.parseFloat(mEditTextWorkLoad.getText().toString());
                	model._workload = load;
                }
                if (!TextUtils.isEmpty(model._taskdetail) && model._stagetype != 0 && model._tasktype != 0
                		 && model._workload > 0 && model._croptypeid > 0){//mStartTimestamp != null && null != mEndTimestamp && 
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int ret = new Task().Save(model);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            } else {
                                Log.e(tag, "-----保存出错");
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(EditTaskMouldAct.this, "请填写完整", Toast.LENGTH_SHORT).show();
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
    
    private void initStagePickSpinner(ArrayList<String> arr){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arr);
        mStagePick.setAdapter(adapter);
        mStagePick.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    mStageTypeID = mStageModels.get(position - 1)._id;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    
    private void initTaskTypePickSpinner(ArrayList<String> arr){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arr);
        mTypePick.setAdapter(adapter);
        mTypePick.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    mTaskTypeID = mTypesModels.get(position - 1)._id;
                }
                ((TextView) view).setTextColor(getResources().getColor(R.color.black));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    
    private void showCropPickDialog(){
        if (mAllProductTypeModel.size() <= 0){
            Toast.makeText(this, "此用户无作物信息", Toast.LENGTH_SHORT).show();
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
        bui.setTitle("产品选择").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
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
    
    private class GetStageAndTaskTypesThread extends Thread{
        @Override
        public void run() {
            mStageModels = new TaskStageTypes().GetAllStages(userID);
            stageArr.clear();
            stageArr.add("请选择阶段");
            if (null != mStageModels){
                for (ERPTaskStageTypesModel m : mStageModels){
                    stageArr.add(m._name);
                }
            }
            
            mTypesModels = new TaskTypes().GetAllTaskTypes(userID);
            taskTypeArr.clear();
            taskTypeArr.add("请选择任务类型");
            if (null != mTypesModels){
                for (ERPTaskTypesModel m : mTypesModels){
                    taskTypeArr.add(m._name);
                }
            }
            mHandler.sendEmptyMessage(MSG_GETTYPES_END);
        }
    }
    
}