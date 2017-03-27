package com.ssiot.remote.yun.detail.cntrols;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ControlActionInfoModel;
import com.ssiot.remote.yun.WheelValAct;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.unit.CustomDialog;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class ControlSetAct extends HeadActivity{
    private static final String tag = "ControlSetAct";
    private boolean ISLOOPMODE = true;
    
    CustomDialog dialog;
    String[] menuOp;
    String[] datesItem;
    int toogle = 1;
    int toogleTime = 1;
    
    String[] deviceName;
    int[] deviceArray;
    DeviceBean device;
    YunNodeModel mYunNodeModel;
    ArrayList<DeviceBean> tmpDevices;
    int senStartDeviceId;
    int senStopDeviceId;
    
    ViewGroup setGroup;
    RadioButton intelliButton;
    RadioButton timeButton;
    
    View intelliView;
    TextView deviceStartTitleTextView;
    TextView deviceEndTitleTextView;
    ToggleButton startButton;
    TableRow timeErrorRowIni;
    TextView timeErrorTextView;
    TableRow startIniRow;
    TableRow endIniRow;
    TextView startIntTextView;
    TextView endIntTextView;
    TextView sen1TextView;
    TextView unit1TextView;
    TextView senOp1TextView;
    EditText va1EditText;
    TextView senOp2TextView;
    TextView sen2TextView;
    TextView unit2TextView;
    EditText va2EditText;
    TextView spandTextView;
    TextView timeTextView;
    TextView compTextView;
    TableRow spandRow;
    TableRow timeRow;
    
    View timeView;
    private TableRow spandTimeRow;
    private TextView spandTimeTextView;
    private TableRow startRow;
    private TableRow endRow;
    private ToggleButton startTimeButton;
    private TextView startTimeTextView;
    private RadioButton stopButton;
    private TextView compTimeTextView;
    TableRow timeErrorRow;
    TextView timeErrorTextViewTime;
    TextView endTimeTextView;
    TextView redundantTextView;
    TextView timeTimeButton;
    TextView timeTimeTextView;
    TableRow timeTimeRow;
    TableRow controlStateRow;
    RadioGroup controlStateGroup;
    ToggleButton toggleButtonCircle;//add by jingbo
    
    long m_StartTime;
    long m_EndTime;
    int m_WorkingTime = 2;//分钟
    int m_SpandTime = 15;
    ArrayList<Integer> m_CheckWeekVals = new ArrayList<Integer>();//checkVals
    
    LayoutInflater mInflater;
    
    private static final int MSG_SAVECONTROLACTIONINFO_OK = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
//                case WS_MQTT.MSG_MQTT_PUB_END:
//                    saveControlActionInfo((String) msg.obj);//保存规则到数据库的线程
//                    break;
//                case WS_MQTT.MSG_MQTT_PUB_FAIL:
//                    showToast("设置失败");
//                    break;
                case MSG_SAVECONTROLACTIONINFO_OK:
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
        Intent intent = getIntent();
        ISLOOPMODE = intent.getBooleanExtra("isloopmode", false);
        device = (DeviceBean) intent.getSerializableExtra("devicebean");
        mYunNodeModel = (YunNodeModel) intent.getSerializableExtra("yunnodemodel");
        tmpDevices = (ArrayList<DeviceBean>) intent.getSerializableExtra("devicebeans");
        mInflater = LayoutInflater.from(this);
        initView();
    }
    
    private void initView(){
        setContentView(R.layout.set_detail);
        datesItem = getResources().getStringArray(R.array.datesItem);
        menuOp = getResources().getStringArray(R.array.setOpMenu);
        initAllView();
        LinearLayout localLinearLayout = (LinearLayout)findViewById(R.id.linearLayoutSet);
        if (ISLOOPMODE){
            localLinearLayout.addView(timeView);
            intelliButton.setVisibility(View.GONE);
        } else {
            localLinearLayout.addView(intelliView);
            timeButton.setVisibility(View.GONE);
        }
    }
    
    private void initAllView(){
        setGroup = (RadioGroup) findViewById(R.id.RadioGroupSet);
        intelliButton = (RadioButton) findViewById(R.id.RadioButtonInt);
        timeButton = (RadioButton) findViewById(R.id.RadioButtonTime);
        intelliView = mInflater.inflate(R.layout.intelligent_set, null);
        deviceStartTitleTextView = ((TextView) intelliView.findViewById(R.id.TextViewDeviceStartTitle));//开始条件textview
        deviceEndTitleTextView = ((TextView) intelliView.findViewById(R.id.TextViewDeviceEndTitle));
        startButton = ((ToggleButton) intelliView.findViewById(R.id.toggleButtonSetIni));
        startButton.setOnCheckedChangeListener(toggleListener);
        timeErrorRowIni = ((TableRow) intelliView.findViewById(R.id.tableRowTimeErrorInte));//开始时间不能大于结束时间 
        timeErrorTextView = ((TextView)intelliView.findViewById(R.id.textViewStartError));//开始时间不能大于结束时间
        startIniRow = ((TableRow) intelliView.findViewById(R.id.TableRowStarIni));
        endIniRow = ((TableRow) intelliView.findViewById(R.id.TableRowEndIni));
        startIntTextView = ((TextView) intelliView.findViewById(R.id.TextViewStartIni));
        endIntTextView = ((TextView) intelliView.findViewById(R.id.TextViewEndIni));
        sen1TextView = ((TextView) intelliView.findViewById(R.id.TextViewSen1));//选择传感器
        sen1TextView.setOnClickListener(clickListener);
        unit1TextView = ((TextView) intelliView.findViewById(R.id.textViewVa1Unit));
        senOp1TextView = ((TextView) intelliView.findViewById(R.id.TextViewSenOp1));
        senOp1TextView.setOnClickListener(clickListener);
        va1EditText = ((EditText) intelliView.findViewById(R.id.textViewVa1));
        senOp2TextView = ((TextView) intelliView.findViewById(R.id.TextViewSenOp2));
        senOp2TextView.setOnClickListener(clickListener);
        sen2TextView = ((TextView)intelliView.findViewById(R.id.TextViewSen2));
        sen2TextView.setOnClickListener(clickListener);
        unit2TextView = ((TextView) intelliView.findViewById(R.id.textViewVa2Unit));
        va2EditText = ((EditText) intelliView.findViewById(R.id.textViewVa2));
        spandTextView = ((TextView) intelliView.findViewById(R.id.TextViewpandIni));
        timeTextView = ((TextView) intelliView.findViewById(R.id.TextViewTimeIni));
        compTextView = ((TextView) intelliView.findViewById(R.id.textViewIntComp));
        spandRow = ((TableRow) intelliView.findViewById(R.id.TableRowSpandIni));
        timeRow = ((TableRow) intelliView.findViewById(R.id.TableRowTimeIni));
        
        
        initTimeView();
        
        //--------------------------
        deviceShowOp(tmpDevices);
    }
    
    private void initTimeView(){//循环
        timeView = mInflater.inflate(R.layout.time_set, null);
        startTimeButton = ((ToggleButton) timeView.findViewById(R.id.toggleButtonSet));
        startTimeButton.setOnCheckedChangeListener(toggleListener);
        compTimeTextView = ((TextView) timeView.findViewById(R.id.textViewComp));
        timeErrorRow = ((TableRow) timeView.findViewById(R.id.tableRowTimeErrorTime));
        timeErrorTextViewTime = ((TextView) timeView.findViewById(R.id.textViewStartErrorTime));//开始时间不能大于结束时间
        startRow = ((TableRow) timeView.findViewById(R.id.TableRowStart));
        endRow = ((TableRow) timeView.findViewById(R.id.TableRowEnd));
        startTimeTextView = ((TextView) timeView.findViewById(R.id.TextViewStart));//开始时间
        endTimeTextView = ((TextView) timeView.findViewById(R.id.TextViewEnd));//结束时间
        spandTimeTextView = ((TextView) timeView.findViewById(R.id.TextViewpand));//每次时长 15
        redundantTextView = ((TextView) timeView.findViewById(R.id.TextViewRedundant));//循环周期 周一到周日
        timeTimeButton = ((RadioButton) timeView.findViewById(R.id.RadioButtonTime));
        timeTimeTextView = ((TextView) timeView.findViewById(R.id.TextViewTime));
        spandTimeRow = ((TableRow) timeView.findViewById(R.id.TableRowSpand));
        timeTimeRow = ((TableRow) timeView.findViewById(R.id.TableRowTime));
        controlStateRow = ((TableRow) timeView.findViewById(R.id.TableRowControlState));//控制状态
        toggleButtonCircle = (ToggleButton) timeView.findViewById(R.id.toggleButtonCircle);
//        controlStateGroup = ((RadioGroup) timeView.findViewById(R.id.RadioGroupControlState));
//        stopButton = ((RadioButton) timeView.findViewById(R.id.RadioButtonStop));
//        stopButton.setVisibility(View.GONE);
//        controlStateGroup.setOnCheckedChangeListener(controlStateChangeListener);
        setTimeViewVal();
    }
    
    private void setTimeViewVal(){
        toggleButtonCircle.setOnCheckedChangeListener(timeViewToggleListener);
    }
    
    public void onBackClick(View v){
        finish();
    }
    
    public void SubClick(View v){
        if (m_EndTime > (m_StartTime+1000)){
            String str = "";
            if (ISLOOPMODE){// 几种循环模式 TODO
                if (!toggleButtonCircle.isChecked() && m_StartTime > System.currentTimeMillis()+1000){//定时某时某刻开始执行一段时间后关闭
                    int workingSeconds = (int) (m_EndTime - m_StartTime)/1000;
                    str = "{\"Cmd\":1,\"Type\":3,\"Data\":[{\"Dev\":"+device.mChannel+",\"st\":"+(int) (m_StartTime/1000)+",\"rt\":"+workingSeconds+"}]}";
//                    new MQTT().pubMsg("B/" + mYunNodeModel.mNodeNo, str, mHandler);
                } else if ((m_CheckWeekVals == null || m_CheckWeekVals.size() == 0) && m_StartTime > System.currentTimeMillis()+1000){
                    if (m_SpandTime > m_WorkingTime && m_WorkingTime > 0){
                        str = "{\"Cmd\":1,\"Type\":4,\"Data\":[{\"Dev\":"+device.mChannel+",\"st\":"+(int) (m_StartTime/1000)+"," +
                                "\"rt\":"+m_SpandTime+",\"stt\":"+m_WorkingTime+",\"ent\":"+(int) (m_EndTime/1000)+"}]}";
//                        new MQTT().pubMsg("B/" + mYunNodeModel.mNodeNo, str, mHandler);
                    } else {
                        showToast("持续-间隔时间设置错误");
                    }
                } else if (m_CheckWeekVals != null && m_CheckWeekVals.size() > 0){
                    int weekFlag = 0;
                    for (int i = 0; i < m_CheckWeekVals.size(); i ++){
                        weekFlag += Math.pow(2, m_CheckWeekVals.get(i));
                    }
                    Timestamp start  = new Timestamp(m_StartTime);
                    int startInt = start.getHours() * 3600 + start.getMinutes() * 60;
                    Timestamp end  = new Timestamp(m_EndTime);
                    int endInt = end.getHours() * 3600 + end.getMinutes() * 60;//TODO 这个不是绝对时间
                    str = "{\"Cmd\":1,\"Type\":5,\"Data\":[{\"Dev\":"+device.mChannel+",\"st\":"+(int) startInt+"," +
                            "\"rt\":"+m_SpandTime+",\"stt\":"+m_WorkingTime+",\"ent\":"+(int) endInt+",\"wt\":"+weekFlag+"}]}";
//                    new MQTT().pubMsg("B/" + mYunNodeModel.mNodeNo, str, mHandler);
                }
            }
        } else {
            showToast("开始结束时间设置错误，请检查。");
        }
    }
    
    private void saveControlActionInfo(String conditionStr){//TODO 3代 2代
        final ControlActionInfoModel controlActionInfo = new ControlActionInfoModel();
        controlActionInfo._areaid = Utils.getIntPref(Utils.PREF_AREAID, this);;
        controlActionInfo._controlname = ISLOOPMODE ? "定时控制" : "触发控制";// (string)Session["controlActionName"];
        controlActionInfo._uniqueid = ""+mYunNodeModel.mNodeUnique;
        controlActionInfo._deviceno = device.mChannel;
        controlActionInfo._controltype = ISLOOPMODE ? 15 : 16;
        controlActionInfo._controlcondition = conditionStr;
        controlActionInfo._operatetime = new Timestamp(System.currentTimeMillis());
        controlActionInfo._statenow = 0;
        controlActionInfo._operate = "打开";
        new Thread(new Runnable() {
            @Override
            public void run() {//TODO
//                if (new ControlActionInfo().Add(controlActionInfo) > 0){
//                    mHandler.sendEmptyMessage(MSG_SAVECONTROLACTIONINFO_OK);
//                }
            }
        }).start();
        
    }
    
    public void StartTimeClick(View v){
//        showTimePickDialog(0);
        showValWheel(REQUEST_TIME_START);
    }
    
    public void EndTimeClick(View v) {
//        showTimePickDialog(1);
        showValWheel(REQUEST_TIME_END);
    }
    
    public void TimeClick(View v) {//持续时长
        showEditDialog(R.string.setTime);
    }
    
    public void SpandClick(View v) {//每次间隔
        showEditDialog(R.string.setSpand);
    }
    
    public void RedundantClick(View paramView) {
        showCheckList();
    }
    
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.TextViewSenOp1:
                    opMenu(senOp1TextView, sen1TextView.getText().toString() + "启动时机");
                    break;
                case R.id.TextViewSenOp2:
                    opMenu(senOp2TextView, sen2TextView.getText().toString() + "关闭时机");
                    break;
                case R.id.TextViewSen1:
                    formatVal(deviceStartTitleTextView, va1EditText, sen1TextView, unit1TextView);
                    break;
                case R.id.TextViewSen2:
                    formatVal(deviceEndTitleTextView, va2EditText, sen2TextView, unit2TextView);
                    break;
                default:
                    break;
            }
        }
    };
    
    CompoundButton.OnCheckedChangeListener timeViewToggleListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
//                findViewById(R.id.TableRowCircleOpen).setBackgroundResource(R.drawable.bg_optionlist_top_select);
                timeTimeRow.setVisibility(View.VISIBLE);
                spandTimeRow.setVisibility(View.VISIBLE);
                findViewById(R.id.TableRowRedundant).setVisibility(View.VISIBLE);
                
            } else {
//                findViewById(R.id.TableRowCircleOpen).setBackgroundResource(R.drawable.bg_optionlist_select);
                timeTimeRow.setVisibility(View.GONE);
                spandTimeRow.setVisibility(View.GONE);
                findViewById(R.id.TableRowRedundant).setVisibility(View.GONE);
            }
        }
    };
    
    CompoundButton.OnCheckedChangeListener toggleListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (compoundButton.getTag().toString().equals("Ini")){
                if (isChecked) {
                    toogle = 1;
                    toogleTime = 1;
                    compTextView.setText("已启用。");
                } else {
                    toogle = 0;
                    toogleTime = 0;
                    compTextView.setText("禁用。");
                }
            }
        }
    };

    private void opMenu(final TextView paramTextView, String paramString) {
        dialog = new CustomDialog(this);
        dialog.setTitle(paramString);
        dialog.setItems(menuOp);
        dialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> paramAdapterView, View view, int paramInt,
                    long paramLong) {
                paramTextView.setText(menuOp[paramInt]);
                dialog.cancel();
                if (paramInt == 0) {//TODO
                    paramTextView.setTag(">");
                } else {
                    paramTextView.setTag("<");
                }
            }
        });
        dialog.setBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    
    private void formatVal(final TextView txtView1, final EditText editText, final TextView senTxtView, final TextView unitTxtView) {
        if (deviceName.length > 1) {
            dialog = new CustomDialog(this);
            dialog.setTitle(getResources().getString(R.string.selSenDevice));// 选择传感器
            dialog.setItems(deviceName);
            dialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                    dialog.cancel();
                    if (senTxtView.getTag().toString().equals("start")) {
                        senStartDeviceId = deviceArray[position];
                    } else {
                        senStopDeviceId = deviceArray[position];
                    }
                    String str2 = "";
                    editText.setText("");
                    if (position == 0) {
                        unitTxtView.setVisibility(View.GONE);
                        senTxtView.setText(str2);
                        if (senTxtView.getText().toString().equals("")) {
                            senTxtView.setHint(getResources().getString(R.string.selSenDevice));// 选择传感器
                        }
                        txtView1.setTextColor(getResources().getColor(R.color.c777777));
                        txtView1.setTag(Integer.valueOf(0));
                    } else {
                        DeviceBean localDevice = (DeviceBean) tmpDevices.get(position - 1);
                        // initValFomatTag(editText, localDevice);
                        str2 = deviceName[position];
                        String strUnit = "TODO";
                        unitTxtView.setText(strUnit);
                        unitTxtView.setVisibility(View.VISIBLE);
                    }
                }
            });
            dialog.setBackClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.show();
            return;
        } else {
            showToast(R.string.noDataLocalSensorStr);// R.string.noDataFarmSensorStr
        }
    }
    
    private void showEditDialog(final int resId){
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        bui.setTitle(resId).setView(editText).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = editText.getText().toString();
                int i = 2;
                try {
                    i = Integer.parseInt(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (R.string.setTime == resId){
                    m_WorkingTime = i;
                    ((TextView) timeTimeTextView).setText(""+m_WorkingTime);
                } else if (R.string.setSpand == resId){
                    m_SpandTime = i;
                    ((TextView) spandTimeTextView).setText(""+m_SpandTime);
                }
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private void showCheckList() {
        dialog = new CustomDialog(this);
        dialog.setTitle("设置的重复日期");
        dialog.setCheckVals(m_CheckWeekVals);
        dialog.setShowDateGrid(true);
        dialog.setBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View paramAnonymousView) {
                dialog.dismiss();
            }
        });
        dialog.setSureClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View paramAnonymousView) {
                if (m_CheckWeekVals.size() > 0) {
                    String str = Utils.getStringForVals(m_CheckWeekVals);
                    // if (set != null)
                    // set.setExecWeekDates(str);
                    setRedundant(str);
                } else {
                    redundantTextView.setText("未设置");
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    
    private void setRedundant(String paramString) {
        String str2;
        if (paramString.equals("")) {
            str2 = "";
            return;
        }
        String[] arrayOfString = paramString.split(",");
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("周");
        int size = arrayOfString.length;
        for (int j = 0; j < size; j++) {
            int k = Integer.parseInt(arrayOfString[j]);
            if (!m_CheckWeekVals.contains(Integer.valueOf(k))){
                m_CheckWeekVals.add(Integer.valueOf(k));
            }
            localStringBuilder.append(datesItem[CustomDialog.getRepeatDateOffset(k)]);
            localStringBuilder.append(",");
        }
        String str1 = localStringBuilder.toString();
        str2 = str1.substring(0, -1 + str1.length());
        redundantTextView.setText(str2);
    }
    
    void deviceShowOp(ArrayList<DeviceBean> paramArrayList) {
        if (null != paramArrayList){
            int i = 1 + paramArrayList.size();
            deviceName = new String[i];
            deviceArray = new int[i];
            deviceName[0] = "(无)";
            deviceArray[0] = 0;
            int j = 1;
            for (int m = 0; m < paramArrayList.size(); m++) {
                deviceName[j] = paramArrayList.get(m).mName;
                deviceArray[j] = paramArrayList.get(m).mDeviceTypeNo;
                j++;
            }
        }
    }
    
    private static final int REQUEST_TIME_START = 1;
    private static final int REQUEST_TIME_END = 2;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(tag, "----onActivityResult----" + requestCode);//可能有bug
        switch (requestCode) {
            case REQUEST_TIME_START:
                if (resultCode == RESULT_OK){
                    m_StartTime = data.getLongExtra("timepoint", System.currentTimeMillis());
                    Date date = new Date(m_StartTime);
                    startTimeTextView.setText(""+date.getHours() + ":" + date.getMinutes());
                }
                break;
            case REQUEST_TIME_END:
                if (resultCode == RESULT_OK){
                    m_EndTime = data.getLongExtra("timepoint", System.currentTimeMillis());
                    Date date = new Date(m_EndTime);
                    endTimeTextView.setText(""+date.getHours() + ":" + date.getMinutes());
                }
                break;
            default:
                break;
        }
    }
    
    private void showValWheel(int requestid){
        Intent intent = new Intent(this, WheelValAct.class);
        startActivityForResult(intent, requestid);
    }
    
}