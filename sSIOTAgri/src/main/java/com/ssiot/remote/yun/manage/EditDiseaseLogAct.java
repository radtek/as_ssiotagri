package com.ssiot.remote.yun.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.ssiot.remote.data.model.ERPDiseaseLogModel;
import com.ssiot.remote.data.model.ERPProductBatchModel;
import com.ssiot.remote.yun.webapi.ERPDiseaseLog;
import com.ssiot.remote.yun.webapi.ProductBatch;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditDiseaseLogAct extends HeadActivity{
    private static final String tag = "EditDiseaseLogAct";
    
    private int userId = 0;
    private int mProductBatchID = -1;
    private TextView mBatchTextView;
    private EditText mSymptomEdit;
    private TextView mTxtTime;
    private EditText mDiseaseEdit;
    private EditText mResolveEdit;
    
    private String batchText = "";
    
    private Timestamp mTimeStamp;
    private List<ERPProductBatchModel> mAllBatchsModel = new ArrayList<ERPProductBatchModel>();
    
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
        mProductBatchID = getIntent().getIntExtra("batchid", -1);
        setContentView(R.layout.act_diseaselog_new);
        initViews();
        initTitleBar();
        new getAllBatchsThread().start();
    }
    
    private void initViews(){
        mBatchTextView = (TextView) findViewById(R.id.txt_productbatch);
        mSymptomEdit = (EditText) findViewById(R.id.edit_symptom);
        mDiseaseEdit = (EditText) findViewById(R.id.edit_disease);
        mResolveEdit = (EditText) findViewById(R.id.edit_resolve);
        mTxtTime = (TextView) findViewById(R.id.txt_time);
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ERPDiseaseLogModel model = new ERPDiseaseLogModel();
                model._id = 0;
                model._batchid = mProductBatchID;
                model._time = mTimeStamp;
                model._symptom = mSymptomEdit.getText().toString();
                model._disease = mDiseaseEdit.getText().toString();
                model._resolve = mResolveEdit.getText().toString();
                if (model._batchid > 0 && null != mTimeStamp && !TextUtils.isEmpty(model._disease)){
                    new Thread(new Runnable() {
                        public void run() {
                            int ret = new ERPDiseaseLog().Save(model);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(EditDiseaseLogAct.this, "请填写完整", Toast.LENGTH_SHORT).show();
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
        titleView.setText("疾病记录");
    }
    
    public void ClickFunc(View v) {
        Log.v(tag, "--------ClickFunc-------" );
        switch (v.getId()) {
            case R.id.row_productbatch:
                showBatchPickDialog();
                break;
            case R.id.row_time:
                showDatePickerDialog();
                break;
            default:
                break;
        }
    }
    
//    public float calculateWeight(float length){
//    	
//    }
    
    private void showBatchPickDialog(){
        if (mAllBatchsModel.size() <= 0){
            Toast.makeText(this, "此用户无批次信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[mAllBatchsModel.size()];
        for (int i = 0; i < mAllBatchsModel.size(); i ++){
            types[i] = mAllBatchsModel.get(i)._name + " 类型:" + mAllBatchsModel.get(i)._CropName;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProductBatchID = mAllBatchsModel.get(which)._id;
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
    
    
    private void showDatePickerDialog(){
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dia_date_time_pick, null);
        final DatePicker dp = (DatePicker) view.findViewById(R.id.date_pick);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.time_pick);
        bui.setTitle("时间选择").setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = new Date(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
                mTimeStamp = new Timestamp(d.getTime());
                String str = formatter.format(d);
                mTxtTime.setText("时间:"+str);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private class getAllBatchsThread extends Thread{
        @Override
        public void run() {
            List<ERPProductBatchModel> list = new ProductBatch().GetActiveProductBatch(userId);
            if (null != list && list.size() > 0){
                mAllBatchsModel.clear();
                mAllBatchsModel.addAll(list);
                mHandler.sendEmptyMessage(MSG_GET_TYPES_END);
            }
        }
    }
}