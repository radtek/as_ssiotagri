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
import com.ssiot.remote.data.model.ERPPesticideModel;
import com.ssiot.remote.data.model.ERPProductBatchModel;
import com.ssiot.remote.yun.webapi.ERPPesticide;
import com.ssiot.remote.yun.webapi.ProductBatch;
import com.ssiot.remote.yun.webapi.WS_InputsOut;
import com.ssiot.remote.yun.webapi.WS_UserInputsType;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditPesticideAct extends HeadActivity{
    private static final String tag = "InputsEditActivity";
    
    private int userId = 0;
    private int mProductBatchID = -1;
    private TextView mBatchTextView;
    private EditText mUseDateEdit;
    private EditText mNameEdit;
    private EditText mAmountEdit;
    private EditText mBrandEdit;
    private EditText mSupplierDetailEdit;
    private EditText mUnitEdit;
    private EditText mTypeEdit;
    private TextView mTxtTime;
    
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
        setContentView(R.layout.activity_productlog_new);
        initViews();
        initTitleBar();
        new getAllBatchsThread().start();
    }
    
    private void initViews(){
        mBatchTextView = (TextView) findViewById(R.id.txt_productbatch);
        mUseDateEdit = (EditText) findViewById(R.id.edit_usedate);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mAmountEdit = (EditText) findViewById(R.id.edit_amount);
        mBrandEdit = (EditText) findViewById(R.id.edit_brand);
        mSupplierDetailEdit = (EditText) findViewById(R.id.edit_supplier);
        mUnitEdit = (EditText) findViewById(R.id.edit_unit);
        mTxtTime = (TextView) findViewById(R.id.txt_time);
        mTypeEdit = (EditText) findViewById(R.id.edit_type);
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ERPPesticideModel model = new ERPPesticideModel();
                model._id = 0;
                model._productbatchid = mProductBatchID;
                model._usedate = mUseDateEdit.getText().toString();
                model._name = mNameEdit.getText().toString();
                float f = 0;
                try {
                    f = Float.parseFloat(mAmountEdit.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model._dosage = f;
                model._brand = mBrandEdit.getText().toString();
                model._suppliername = mSupplierDetailEdit.getText().toString();
                model._unit = mUnitEdit.getText().toString();
                model._usedatetime = mTimeStamp;
                model._type = mTypeEdit.getText().toString();
                if (!TextUtils.isEmpty(model._name) && model._productbatchid > 0 && null != mTimeStamp){
                    new Thread(new Runnable() {
                        public void run() {
                            int ret = new ERPPesticide().Save(model);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(EditPesticideAct.this, "请填写完整", Toast.LENGTH_SHORT).show();
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
        titleView.setText("农药");
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
            List<ERPProductBatchModel> list = new ProductBatch().GetProductBatch(userId);
            if (null != list && list.size() > 0){
                mAllBatchsModel.clear();
                mAllBatchsModel.addAll(list);
                mHandler.sendEmptyMessage(MSG_GET_TYPES_END);
            }
        }
    }
    
}