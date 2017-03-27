package com.ssiot.remote.yun.sta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.yun.detail.sensors.SensorLineChartFrag;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.unit.SeekBarPressure;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StaDetailAct extends HeadActivity{
    private static final String tag = "StaDetailAct";
    
    ArrayList<YunNodeModel> mYunNodeModels;
    DeviceBean mDeviceBean;
    ArrayList<YunNodeModel> mContainsYModels = new ArrayList<YunNodeModel>();
    
    protected PopupWindow pop;
    protected boolean isShowMatchWidth = false;
    TextView textViewTime;
    LinearLayout dateLayout;
    SeekBarPressure seekBar;
    Spinner nodeSpinner;
    Spinner senSpinner;
    View filterView;
    int defaultTable = 2;
    String mTableName = SensorLineChartFrag.tableList[defaultTable];
    TextView startTextView;
    TextView endTextView;
    String startTime = "";
    String endTime = "";
    Timestamp startTimestamp;
    Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());
    String mSelectNodeUnique = "";
    String mSelectColumnStr = "";
    TextView textViewUnit;
    int currentChannel = 0;
    
//    public ImageView imageButtonFilter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        mYunNodeModels = (ArrayList<YunNodeModel>) getIntent().getSerializableExtra("yunnodemodels");
        mDeviceBean = (DeviceBean) getIntent().getSerializableExtra("devicebean");
        currentChannel = mDeviceBean.mChannel;
        initViews();
    }
    
    private void initViews(){
        setContentView(R.layout.sta_detail_new);
        findViewById(R.id.graphLinearLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.LinearLayoutRadar).setVisibility(View.GONE);
//        findViewById(R.id.buttonFilter).setOnClickListener(cli);
//        imageButtonFilter = (ImageView) findViewById(R.id.imageButtonFilter);
        
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        dateLayout = (LinearLayout) findViewById(R.id.LinearLayoutDate);
        dateLayout.setOnClickListener(cli);
        textViewUnit = (TextView) findViewById(R.id.textViewUnit);
        textViewUnit.setText(mDeviceBean.getUnit());
        seekBar = (SeekBarPressure) findViewById(R.id.seek);
        seekBar.setProgressLowInt(defaultTable);
        seekBar.setOnSeekBarChangeListener(new SeekBarPressure.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBarPressure seekBar, double progressLow, double progressHigh,
                    int mprogressLow, int mprogressHigh, double max, double min) {
                Log.v(tag, "----------progressLow" + progressLow + "-----mprogressLow-int" + mprogressLow);
                mTableName = SensorLineChartFrag.tableList[mprogressLow];
            }
        });
        initSpinners();
        initFilterView();
        textViewTime.setText(getTimeStr(startTimestamp) + " 到 " + getTimeStr(endTimestamp));
        initTitleBar();
    }
    
    private void initSpinners(){
        nodeSpinner = (Spinner) findViewById(R.id.nodeSpinner);
        senSpinner = (Spinner) findViewById(R.id.senSpinner);
        ArrayList<String> nodeArr = new ArrayList<String>();
        mContainsYModels.clear();
        if (null != mYunNodeModels && mYunNodeModels.size() > 0){
            for (YunNodeModel y : mYunNodeModels){
                if (containsThisSensor(y, mDeviceBean.mDeviceTypeNo)){
                    if (!TextUtils.isEmpty(y.mNodeUnique)){
                        mContainsYModels.add(y);
                        nodeArr.add("("+y.mNodeNo+")"+y.nodeStr);
                    } else {
                        Log.e(tag, "!!!!!!mNodeUnique = " + y.mNodeUnique);
                    }
                }
            }
        } else {
            showToast("无包含此种传感器的节点");
        }
        Log.v(tag, "-------------------------nodearr:" + nodeArr.toString());
        ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,nodeArr);
        nodeSpinner.setAdapter(arrAdapter);
        mSelectNodeUnique = mContainsYModels.get(0).mNodeUnique;
        mSelectColumnStr = mDeviceBean.mName;//?
        nodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                YunNodeModel tmpY = mContainsYModels.get(position);
                mSelectNodeUnique = tmpY.mNodeUnique;
                final ArrayList<String> senArr = new ArrayList<String>();
                final ArrayList<Integer> channelList = new ArrayList<Integer>();
                if (tmpY != null && tmpY.list != null){
                    for (DeviceBean de : tmpY.list){
                        if(de.mDeviceTypeNo == mDeviceBean.mDeviceTypeNo){
                            senArr.add(de.mName + (de.mChannel > 0 ? de.mChannel : ""));
                            channelList.add(Integer.valueOf(de.mChannel));
                        }
                    }
                }
                ArrayAdapter<String> senAdapter = new ArrayAdapter<String>(StaDetailAct.this,android.R.layout.simple_spinner_item,senArr);
                senSpinner.setAdapter(senAdapter);
                Log.v(tag, "----------OnItemSelectnode---------------senarr:" + senArr.toString());
//                mSelectColumnStr = senArr.get(0);//在此处初始化没用！
                senSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                            long id) {
                        mSelectColumnStr = senArr.get(position);
                        currentChannel = channelList.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        
    }
    
    private void initTitleBar(){
        initTitleLeft(R.id.title_bar_left);
        TextView titleView = (TextView) findViewById(R.id.title_bar_title);
        titleView.setText(mDeviceBean.mName);
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(cli);
    }
    
    private boolean containsThisSensor(YunNodeModel y, int senType){
        if (y.nodeType == DeviceBean.TYPE_SENSOR){
            if (null != y.list && y.list.size() > 0){
                for (DeviceBean d : y.list){
                    if (senType == d.mDeviceTypeNo){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void initFilterView(){
        filterView = LayoutInflater.from(this).inflate(R.layout.filter_date, null,false);
        startTextView = (TextView) filterView.findViewById(R.id.startTextView);
        endTextView = (TextView) filterView.findViewById(R.id.endTextView);
        if (SensorLineChartFrag.TABLETEN.equals(mTableName)){
            startTimestamp = new Timestamp(System.currentTimeMillis()+ -2 * 3600 * 1000);
        } else if (SensorLineChartFrag.TABLEHOUR.equals(mTableName)){
            startTimestamp = new Timestamp(System.currentTimeMillis()+ -24 * 3600 * 1000);
        } else if (SensorLineChartFrag.TABLEDAY.equals(mTableName)){
            startTimestamp = new Timestamp(System.currentTimeMillis()+ -15 * 24 * 3600 * 1000);
        } else if (SensorLineChartFrag.TABLEMONTH.equals(mTableName)){
            startTimestamp = new Timestamp(System.currentTimeMillis()+ -365 * 24 * 3600 * 1000);
        } else if (SensorLineChartFrag.TABLEYEAR.equals(mTableName)){
            startTimestamp = new Timestamp(System.currentTimeMillis()+ -10 * 365 * 24 * 3600 * 1000);
        }
        startTime = getTimeStr(startTimestamp);
        endTime = getTimeStr(endTimestamp);
        if (null != startTimestamp){
            startTextView.setText(getTimeStr(startTimestamp));
        }
        if (null != endTimestamp){
            endTextView.setText(getTimeStr(endTimestamp));
        }
    }
    
    protected int getTableIndex(String tableName){//在ws_Api里传入的是index
    	if (mTableName.equals(SensorLineChartFrag.TABLETEN)){
    		return 1;
    	} else if (mTableName.equals(SensorLineChartFrag.TABLEHOUR)){
    		return 2;
    	} else if (mTableName.equals(SensorLineChartFrag.TABLEDAY)){
    		return 3;
    	} else if (mTableName.equals(SensorLineChartFrag.TABLEMONTH)){
    		return 4;
    	} else {
    		return 3;
    	}
    }
    
    public void StartTime(View v){
//        Intent intent = new Intent(this, TimePickDiaAct.class);
//        startActivityForResult(intent, REQUEST_START_TIME);
        createTimePickDialog(0).show();
    }
    
    public void EndTime(View v){
//        Intent intent = new Intent(this, TimePickDiaAct.class);
//        startActivityForResult(intent, REQUEST_END_TIME);
        createTimePickDialog(1).show();
    }
    
    public void Sub(View v){//popup的sub
        filterOp();
    }
    
    private Dialog createTimePickDialog(final int startOrEnd){
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dia_date_time_pick, null);
        final DatePicker dp = (DatePicker) view.findViewById(R.id.date_pick);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.time_pick);
        setDatePickLimit(dp);
        bui.setTitle("时间选择").setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
//                Date d = new Date(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
                Timestamp t = new Timestamp(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute(), 
                        0, 0);
                String str = formatter.format(t);
                
                if (startOrEnd == 0){
                    startTime = str;
                    startTextView.setText(startTime);
                    startTimestamp = t;// new Timestamp(d.getTime());
                } else {
                    endTime = str;
                    endTextView.setText(endTime);
                    endTimestamp = t;//new Timestamp(d.getTime());
                }
            }
        }).setNegativeButton(android.R.string.cancel, null);
        return bui.create();
    }
    
    private void setDatePickLimit(DatePicker datePicker){
    	Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);  
        int month = calendar.get(Calendar.MONTH);  
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, month, day, new OnDateChangedListener() {  
        	  
            @Override  
            public void onDateChanged(DatePicker view, int year,  
                    int monthOfYear, int dayOfMonth) {  

                if (isDateAfter(view)) {  
                    Calendar mCalendar = Calendar.getInstance();  
                    view.init(mCalendar.get(Calendar.YEAR),  
                            mCalendar.get(Calendar.MONTH),  
                            mCalendar.get(Calendar.DAY_OF_MONTH), this);  
                }  
            }  

            private boolean isDateAfter(DatePicker tempView) {
                Calendar mCalendar = Calendar.getInstance();  
                Calendar tempCalendar = Calendar.getInstance();  
                tempCalendar.set(tempView.getYear(), tempView.getMonth(),  
                        tempView.getDayOfMonth(), 0, 0, 0); 
                if (tempCalendar.before(mCalendar)){
                	return false;
                } else {
                	return true;
                }
            }  
        });
    }
    
    private String getTimeStr(Timestamp t){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String str = formatter.format(t);
        return str;
    }
    
    private void filterOp() {
      if (!(startTextView.getText().toString().contains(":"))){
          startTime = startTextView.getText().toString() + " 00:00:00";
      }
      if (!(endTextView.getText().toString().contains(":"))){
          endTime = endTextView.getText().toString() + " 23:59:59";
      }
      textViewTime.setText(startTime + " 到 " + endTime);
//      dataOp();
      pop.dismiss();
    }
    
    View.OnClickListener cli = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.buttonFilter:
//                    
//                    break;
                case R.id.LinearLayoutDate:
                    showFilter(dateLayout, isShowMatchWidth);
                    break;
                case R.id.title_bar_right:
                    onClickSearch();
                    break;
                default:
                    break;
            }
        }
    };
    
    public boolean onClickSearch(){
    	boolean ret = true;
    	if (SensorLineChartFrag.TABLETEN.equals(mTableName)){
//            startTimestamp = new Timestamp(System.currentTimeMillis()+ -2 * 3600 * 1000);//2小时 = 12条数据
            if (endTimestamp.getTime() - startTimestamp.getTime() > (long) 50 * 10 * 60 * 1000){//50条数据
            	ret = false;
            }
        } else if (SensorLineChartFrag.TABLEHOUR.equals(mTableName)){
//            startTimestamp = new Timestamp(System.currentTimeMillis()+ -24 * 3600 * 1000);//24小时
            if (endTimestamp.getTime() - startTimestamp.getTime() > (long) 50 * 3600 * 1000){//50条数据
            	ret = false;
            }
        } else if (SensorLineChartFrag.TABLEDAY.equals(mTableName)){
//            startTimestamp = new Timestamp(System.currentTimeMillis()+ -15 * 24 * 3600 * 1000);//15天
            if (endTimestamp.getTime() - startTimestamp.getTime() > (long) 50 * 24 * 3600 * 1000){//50条数据
            	ret = false;
            }
        } else if (SensorLineChartFrag.TABLEMONTH.equals(mTableName)){
//            startTimestamp = new Timestamp(System.currentTimeMillis()+ -365 * 24 * 3600 * 1000);//12个月
        	if (endTimestamp.getTime() - startTimestamp.getTime() > (long) 50 * 30 * 24 * 3600 * 1000){//50条数据
            	ret = false;
            }
        }
    	if (ret == false){
    		showToast("查询范围太大，请缩小查询范围。");
    	}
    	return ret;
    }
    
    protected void showFilter(View anchor, boolean matchWidth) {
        Log.v(tag, "-------------showFilter------maW:" + matchWidth);
//        pop = new PopupWindow(this);
//        pop.setContentView(filterView);
        pop = new PopupWindow(filterView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setAnimationStyle(R.anim.roll_down);
        if (anchor != null) {
            pop.setWidth(anchor.getWidth());
            pop.showAsDropDown(anchor, 0, 0);
        } else {
            View localView = findViewById(android.R.id.content);
            pop.showAtLocation(localView, Gravity.BOTTOM, 0, 0);
        }
    }
    
}