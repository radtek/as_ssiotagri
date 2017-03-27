package com.ssiot.remote.yun.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPInputsInModel;
import com.ssiot.remote.data.model.ERPUserInputsTypeModel;
import com.ssiot.remote.data.model.ERPWarehouseModel;
import com.ssiot.remote.yun.webapi.ERPWarehouse;
import com.ssiot.remote.yun.webapi.WS_InputsIn;
import com.ssiot.remote.yun.webapi.WS_UserInputsType;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InputsInEditAct extends HeadActivity{
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
    private TextView mBuildTime;
    
    private TextView mWarehouseView;
    private ERPWarehouseModel mWarehouseModel;
    private String warehouseText = "";
    
    TextView mTextWarehousePick;
    ToggleButton mToggleWarehouse;
    EditText mWarrantyEdit;
    EditText mWarrantyUnitEdit;
    EditText mAmountUnitEdit;
    EditText mPerAmountEdit;
    EditText mPerAmountUnitEdit;
    
    private String typeStr = "";
    
    private Timestamp mTimeStamp;
    private Timestamp mBuildTimeStamp;// 生产日期
    private List<ERPUserInputsTypeModel> mAllTypesModel = new ArrayList<ERPUserInputsTypeModel>();
    private List<ERPWarehouseModel> mAllWarehouseModel = new ArrayList<ERPWarehouseModel>();
    
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
        userId = Utils.getIntPref(Utils.PREF_MAIN_USERID, this);
        setContentView(R.layout.activity_product_new);
        initViews();
        initTitleBar();
        new getAllTypesThread().start();
        new getAllWarehouseThread().start();
    }
    
    private void initViews(){
    	mToggleWarehouse = (ToggleButton) findViewById(R.id.toggleButtonWarehouse);
        mTxtType = (TextView) findViewById(R.id.txt_type);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mAmountEdit = (EditText) findViewById(R.id.edit_amount);
//        mPriceEdit = (EditText) findViewById(R.id.edit_price);
        mSupplierDetailEdit = (EditText) findViewById(R.id.edit_detail);
        mWorkerEdit = (EditText) findViewById(R.id.edit_worker);
        mTxtTime = (TextView) findViewById(R.id.txt_time);
        mTextWarehousePick = (TextView) findViewById(R.id.textViewWarehousePick);
        mBuildTime = (TextView) findViewById(R.id.txt_buildtime);
        mWarehouseView = (TextView) findViewById(R.id.txt_warehouse);
        
        mWarrantyEdit = (EditText) findViewById(R.id.edit_warranty);
        mWarrantyUnitEdit = (EditText) findViewById(R.id.edit_warrantyunit);
        mAmountUnitEdit = (EditText) findViewById(R.id.edit_amountunit);
        mPerAmountEdit = (EditText) findViewById(R.id.edit_peramount);
        mPerAmountUnitEdit = (EditText) findViewById(R.id.edit_peramountunit);
        
        mToggleWarehouse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	mTextWarehousePick.setText(isChecked ? "需要新建库存" : "已有库存添加");
            	showNewWarehouse(isChecked);
            }
        });
        showNewWarehouse(mToggleWarehouse.isChecked());
    }
    
    private void showNewWarehouse(boolean isChecked){
    	int vis = isChecked ? View.VISIBLE : View.GONE;
    	TableRow warehouseRow = (TableRow) findViewById(R.id.row_warehouse);
    	TableRow row2 = (TableRow) findViewById(R.id.row_producttype);
    	TableRow row3 = (TableRow) findViewById(R.id.row_name);
    	TableRow row4 = (TableRow) findViewById(R.id.row_amountunit);
    	TableRow row5 = (TableRow) findViewById(R.id.row_peramount);
    	TableRow row6 = (TableRow) findViewById(R.id.row_peramountunit);
    	
    	warehouseRow.setVisibility(isChecked ? View.GONE : View.VISIBLE);
    	row2.setVisibility(vis);
    	row3.setVisibility(vis);
    	row4.setVisibility(vis);
    	row5.setVisibility(vis);
    	row6.setVisibility(vis);
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (mToggleWarehouse.isChecked()){//addd
            		final ERPInputsInModel model = new ERPInputsInModel();
                    model._id = 0;
                    model._userid = userId;
                    model._inputstypeid = mProductTypeID;
                    model._namedetail= mNameEdit.getText().toString();
                    try{
                        model._amount = Float.parseFloat(mAmountEdit.getText().toString());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    model._duetime = mTimeStamp;
                    model._suppliername = mSupplierDetailEdit.getText().toString();
                    model._relatedpeopleinfo = mWorkerEdit.getText().toString();
                    model._productdate = mBuildTimeStamp;
                    float f1 = 0;
                    try {
						f1 = Float.parseFloat(mWarrantyEdit.getText().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
                    float warranty = f1;
                    model._warranty = warranty;
                    String warrantyunit = mWarrantyUnitEdit.getText().toString();
                    model._warrantyunit = warrantyunit;
                    final String amountunit = mAmountUnitEdit.getText().toString();
                    float f2= 0;
                    try {
						f2 = Float.parseFloat(mPerAmountEdit.getText().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
                    final float peramount = f2;
                    final String peramountunit = mPerAmountUnitEdit.getText().toString();
                    if (model._amount > 0 && model._userid > 0 && null != mTimeStamp && mProductTypeID >= 0 
                    		&& !TextUtils.isEmpty(amountunit) && !TextUtils.isEmpty(warrantyunit) && !TextUtils.isEmpty(peramountunit) && peramount > 2){
                        new Thread(new Runnable() {
                            public void run() {
                                int ret = new WS_InputsIn().SaveAndAddWarehouse(model, amountunit, peramount, peramountunit);
                                if (ret > -1){
                                    mHandler.sendEmptyMessage(MSG_ADD_END);
                                }
                            }
                        }).start();
                    } else {
                        Toast.makeText(InputsInEditAct.this, "请填写完整", Toast.LENGTH_SHORT).show();
                    }
            	} else {//已有的 简单添加update
            		if (mWarehouseModel == null){
            			return;
            		}
            		final ERPInputsInModel model = new ERPInputsInModel();
                    model._id = 0;
                    model._userid = mWarehouseModel._userid;
                    model._inputstypeid = mWarehouseModel._userinputstypeid;
                    model._namedetail= mWarehouseModel._name;
                    try{
                        model._amount = Float.parseFloat(mAmountEdit.getText().toString());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    model._duetime = mTimeStamp;
                    model._suppliername = mSupplierDetailEdit.getText().toString();
                    model._relatedpeopleinfo = mWorkerEdit.getText().toString();
                    model._productdate = mBuildTimeStamp;
                    float f1 = 0;
                    try {
						f1 = Float.parseFloat(mWarrantyEdit.getText().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
                    float warranty = f1;
                    model._warranty = warranty;
                    String warrantyunit = mWarrantyUnitEdit.getText().toString();
                    model._warrantyunit = warrantyunit;
                    model._warehouseid = mWarehouseModel._id;
                    if (model._amount > 0 && model._userid > 0 && null != mTimeStamp && mProductTypeID >= 0 ){
                        new Thread(new Runnable() {
                            public void run() {
                                int ret = new WS_InputsIn().SaveAndUpdateWarehouse(model);
                                if (ret > -1){
                                    mHandler.sendEmptyMessage(MSG_ADD_END);
                                }
                            }
                        }).start();
                    } else {
                        Toast.makeText(InputsInEditAct.this, "请填写完整", Toast.LENGTH_SHORT).show();
                    }
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
        titleView.setText("入库");
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
            case R.id.row_warehouse:
            	showWarehousePickDialog();
            	break;
            case R.id.row_buildtime:
            	showBuildDatePickerDialog();
            	break;

            default:
                break;
        }
    }
    
    private void showTypePickDialog(){
        final String[] types = new String[mAllTypesModel.size()];
        for (int i = 0; i < mAllTypesModel.size(); i ++){
            types[i] = mAllTypesModel.get(i)._inputstypename;
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
    
    private void showWarehousePickDialog(){
        if (mAllWarehouseModel.size() <= 0){
            Toast.makeText(this, "此用户无仓库信息，需要新建库存", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[mAllWarehouseModel.size()];
        for (int i = 0; i < mAllWarehouseModel.size(); i ++){
            types[i] = mAllWarehouseModel.get(i)._name + " 投入品规格:" + mAllWarehouseModel.get(i)._peramount + mAllWarehouseModel.get(i)._peramountunit 
            		 + "/" + mAllWarehouseModel.get(i)._amountUnit;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	mWarehouseModel = mAllWarehouseModel.get(which);
                warehouseText = mAllWarehouseModel.get(which)._name;
            }
        });
        bui.setTitle("仓库中已有的投入品").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mWarehouseView.setText(warehouseText);
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
    
    private void showBuildDatePickerDialog(){
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dia_date_time_pick, null);
        final DatePicker dp = (DatePicker) view.findViewById(R.id.date_pick);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.time_pick);
        bui.setTitle("生产日期时间选择").setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = new Date(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
                mBuildTimeStamp = new Timestamp(d.getTime());
                String str = formatter.format(d);
                mBuildTime.setText("时间:"+str);
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
    
    private class getAllWarehouseThread extends Thread{
        @Override
        public void run() {
            List<ERPWarehouseModel> list = new ERPWarehouse().GetWarehouse(userId);
            if (null != list && list.size() > 0){
            	mAllWarehouseModel.clear();
            	mAllWarehouseModel.addAll(list);
//                mHandler.sendEmptyMessage(MSG_GET_TYPES_END);
            }
        }
    }
    
}