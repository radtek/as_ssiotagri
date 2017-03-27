package com.ssiot.remote.yun.manage.task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPProductBatchModel;
//import com.ssiot.remote.data.business.TaskCenter;
import com.ssiot.remote.data.model.ERPTaskInstanceModel;
import com.ssiot.remote.data.model.ERPTaskStageTypesModel;
import com.ssiot.remote.data.model.ERPTaskTypesModel;
import com.ssiot.remote.yun.webapi.ProductBatch;
import com.ssiot.remote.yun.webapi.TaskInstance;
import com.ssiot.remote.yun.webapi.TaskStageTypes;
import com.ssiot.remote.yun.webapi.TaskTypes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskNewActivity extends HeadActivity{
    private static final String tag = "TaskNewActivity";
    
    private int userID;
    private TextView mBatchTextView;
    LinearLayout toUsersLinear;
    LinearLayout mContainerRE;
    EditText mEditTextContent;
    Spinner mStagePick;
    Spinner mTypePick;
    TextView mTextViewStart;
    TextView mTextViewEnd;
    ToggleButton mTogglePho;
    TextView mTextPho;
    ToggleButton mToggleLocation;
    TextView mTextLocation;
    ToggleButton mToggleFer;
    ToggleButton mTogglePesti;
    EditText mEditTextWorkLoad;
    
    private String batchText = "";
    private int mProductBatchID = -1;
    private List<ERPProductBatchModel> mAllBatchsModel = new ArrayList<ERPProductBatchModel>();
    private ArrayList<String> toUserNames = new ArrayList<String>();
    private ArrayList<Integer> toUserIDs = new ArrayList<Integer>();
    Timestamp mStartTimestamp;
    Timestamp mEndTimestamp;
    private int mStageTypeID = 0;
    private int mTaskTypeID = 0;
    private ArrayList<String> stageArr = new ArrayList<String>();
    private ArrayList<String> taskTypeArr = new ArrayList<String>();
    List<ERPTaskStageTypesModel> mStageModels;
    List<ERPTaskTypesModel> mTypesModels;
    
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
        setContentView(R.layout.task_new);
        initTitleBar();
        findViews();
        new GetStageAndTaskTypesThread().start();
        new getAllBatchsThread().start();
    }
    
    private void findViews(){
    	mBatchTextView = (TextView) findViewById(R.id.txt_productbatch);
        toUsersLinear = (LinearLayout) findViewById(R.id.linearLayoutRe);
        mContainerRE = (LinearLayout) findViewById(R.id.LinearLayoutcontainer);
        mEditTextContent = (EditText) findViewById(R.id.editTextContent);
        mStagePick = (Spinner) findViewById(R.id.taskstage_pick);
        mTypePick = (Spinner) findViewById(R.id.tasktype_pick);
        mTextViewStart = (TextView) findViewById(R.id.TextViewStart);
        mTextViewEnd = (TextView) findViewById(R.id.TextViewEnd);
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
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ERPTaskInstanceModel model = new ERPTaskInstanceModel();
                int userid = Utils.getIntPref(Utils.PREF_USERID, TaskNewActivity.this);
                model._id = 0;
                model._batchid = mProductBatchID;
                model._ownerid = userid;
                model._croptypeid = 0;//TODO
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
                model._workerids = buildToUsersJSON();
                model._userid = userid;
                model._img = "";
                model._state = 1;//新建
                if (!TextUtils.isEmpty(model._taskdetail) && model._stagetype != 0 && model._tasktype != 0
                		 && model._workload > 0 && !TextUtils.isEmpty(model._workerids) && mProductBatchID > 0){//mStartTimestamp != null && null != mEndTimestamp && 
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int ret = new TaskInstance().Save(model);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            } else {
                                Log.e(tag, "-----保存出错");
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(TaskNewActivity.this, "请填写完整", Toast.LENGTH_SHORT).show();
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
    
    private String buildToUsersJSON(){
        try {
            JSONArray jaArray = new JSONArray();
            for (int i =0; i < toUserIDs.size(); i ++){
                JSONObject jo = new JSONObject();
                jo.put("to", toUserIDs.get(i));
                jaArray.put(jo);
            }
            return jaArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public void ClickFunc(View v) {
        Log.v(tag, "--------ClickFunc-------" );
        switch (v.getId()) {
            case R.id.row_productbatch:
                showBatchPickDialog();
                break;

            default:
                break;
        }
    }
    
    private void showBatchPickDialog(){
        if (mAllBatchsModel.size() <= 0){
            Toast.makeText(this, "此用户无批次信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[mAllBatchsModel.size()];
        for (int i = 0; i < mAllBatchsModel.size(); i ++){
            types[i] = mAllBatchsModel.get(i)._name + " 作物类型:" + mAllBatchsModel.get(i)._CropName;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProductBatchID = mAllBatchsModel.get(which)._id;
//                mFacilitys = mAllBatchsModel.get(which)._facilityids;
                batchText = mAllBatchsModel.get(which)._name;
            }
        });
        bui.setTitle("批次").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBatchTextView.setText(batchText);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private class getAllBatchsThread extends Thread{
        @Override
        public void run() {
            List<ERPProductBatchModel> list = new ProductBatch().GetProductBatch(userID);
            if (null != list && list.size() > 0){
                mAllBatchsModel.clear();
                mAllBatchsModel.addAll(list);
//                mHandler.sendEmptyMessage(MSG_GET_BATCHES_END);
            }
        }
    }
    
    private final  int REQUEST_USERS = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_USERS:
                if (RESULT_OK == resultCode){
                    toUserNames.clear();
                    toUserIDs.clear();
                    mContainerRE.removeAllViews();
                    toUserNames = data.getStringArrayListExtra("extra_names");
                    toUserIDs = data.getIntegerArrayListExtra("extra_userids");
                    if (null != toUserNames){
                        for (int i = 0; i < toUserNames.size(); i ++){
//                            ContactView child = new ContactView(this);
//                            child.setText(toUserNames.get(i));
//                            mContainerRE.addView(child);
                            addRec(toUserNames.get(i), toUserIDs.get(i));
                        }
                    }
                }
                break;

            default:
                break;
        }
    }
    
    public void addRec(String name,int userid){
        ViewGroup contactView = (ViewGroup)LayoutInflater.from(this).inflate(R.layout.msg_new_item, this.mContainerRE, false);
        ((TextView)contactView.findViewById(R.id.TextViewTitle)).setText(name);
        View deleteView = contactView.findViewById(R.id.ImageButtonDelete);
        deleteView.setTag(R.id.tag_first, name);
        deleteView.setTag(R.id.tag_second, userid);
//        deleteView.setTag(R.id.tag_third, contactView);
        deleteView.setOnClickListener(this.deleteClickListener);
        mContainerRE.addView(contactView);
    }
    
    View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int userid = (Integer) v.getTag(R.id.tag_second);
            int index = -1;
            for (int i = 0; i < toUserIDs.size(); i ++){
                if (userid == toUserIDs.get(i)){
                    index = i;
                    break;
                }
            }
            if (index > -1){
                toUserIDs.remove(index);
                toUserNames.remove(index);
                mContainerRE.removeViewAt(index);
            }
        }
    };
    
    public void ClickAdd(View v){
        Intent userCheckIntent = new Intent(this, TaskReceiverAct.class);
        startActivityForResult(userCheckIntent, REQUEST_USERS);
    }
    
    public void ClickStartTime(View v){
        showDatePickerDialog(v);
    }
    
    public void ClickEndTime(View v){
        showDatePickerDialog(v);
    }
    
    private void showDatePickerDialog(final View v){
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dia_date_time_pick, null);
        final DatePicker dp = (DatePicker) view.findViewById(R.id.date_pick);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.time_pick);
        bui.setTitle("时间选择").setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = new Date(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
                String str = formatter.format(d);
                if (v.getId() == R.id.TableRowStartTime){
                    mStartTimestamp = new Timestamp(d.getTime());
                    ((TextView) findViewById(R.id.TextViewStart)).setText(str);
                } else {
                    mEndTimestamp = new Timestamp(d.getTime());
                    ((TextView) findViewById(R.id.TextViewEnd)).setText(str);
                }
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
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