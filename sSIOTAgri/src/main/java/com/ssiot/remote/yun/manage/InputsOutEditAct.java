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
import com.ssiot.remote.data.model.ERPInputsOutModel;
import com.ssiot.remote.data.model.ERPUserInputsTypeModel;
import com.ssiot.remote.yun.webapi.WS_InputsOut;
import com.ssiot.remote.yun.webapi.WS_UserInputsType;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InputsOutEditAct extends HeadActivity{
    private static final String tag = "InputsEditActivity";
    
    private int userId = 0;
    private int mProductTypeID = -1;
    private TextView mTxtType;
    private EditText mNameEdit;
    private EditText mAmountEdit;
//    private EditText mPriceEdit;
    private EditText mSupplierDetailEdit;
    private EditText mWorkerEdit;
    private TextView mTxtTime;
    
    private String typeStr = "";
    
    private Timestamp mTimeStamp;
    private List<ERPUserInputsTypeModel> mAllTypesModel = new ArrayList<ERPUserInputsTypeModel>();
    
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
        setContentView(R.layout.activity_inputsout_new);
        initViews();
        initTitleBar();
        new getAllTypesThread().start();
    }
    
    private void initViews(){
        mTxtType = (TextView) findViewById(R.id.txt_type);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mAmountEdit = (EditText) findViewById(R.id.edit_amount);
//        mPriceEdit = (EditText) findViewById(R.id.edit_price);
        mSupplierDetailEdit = (EditText) findViewById(R.id.edit_detail);
        mWorkerEdit = (EditText) findViewById(R.id.edit_worker);
        mTxtTime = (TextView) findViewById(R.id.txt_time);
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ERPInputsOutModel model = new ERPInputsOutModel();
                model._id = 0;
                model._userid = userId;
                model._inputstypeid = mProductTypeID;
                model._namedetail= mNameEdit.getText().toString();
                try{
                    model._amount = Float.parseFloat(mAmountEdit.getText().toString());
                }catch(Exception e){
                    e.printStackTrace();
                }
                model._takingperson = mWorkerEdit.getText().toString();
                model._takingtime = mTimeStamp;
                if (!TextUtils.isEmpty(model._namedetail) && model._userid > 0 && null != mTimeStamp && mProductTypeID >= 0 ){
                    new Thread(new Runnable() {
                        public void run() {
                            int ret = new WS_InputsOut().Save(model);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(InputsOutEditAct.this, "请填写完整", Toast.LENGTH_SHORT).show();
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
        titleView.setText("出库");
    }
    
    public void ClickFunc(View v) {
        Log.v(tag, "--------ClickFunc-------" );
        switch (v.getId()) {
            case R.id.row_producttype:
                showTypePickDialog();
                break;
            case R.id.row_time:
                showDatePickerDialog();
                break;

            default:
                break;
        }
    }
    
    private void showTypePickDialog(){
        final String[] types = new String[mAllTypesModel.size()];
        for (int i = 0; i < mAllTypesModel.size(); i ++){
            types[i] = mAllTypesModel.get(i)._inputstypename + " 单位：" + mAllTypesModel.get(i)._inputsunit;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProductTypeID = mAllTypesModel.get(which)._id;
                typeStr = mAllTypesModel.get(which)._inputstypename;
            }
        });
        bui.setTitle("产品类别").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTxtType.setText(typeStr);
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
    
    private class getAllTypesThread extends Thread{
        @Override
        public void run() {
            List<ERPUserInputsTypeModel> list = new WS_UserInputsType().GetUserInputsType(userId);
            if (null != list && list.size() > 0){
                mAllTypesModel.clear();
                mAllTypesModel.addAll(list);
                mHandler.sendEmptyMessage(MSG_GET_TYPES_END);
            }
        }
    }
    
}