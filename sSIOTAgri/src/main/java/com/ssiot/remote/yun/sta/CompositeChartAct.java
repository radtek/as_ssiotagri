package com.ssiot.remote.yun.sta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.agri.R;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.unit.XYStringHolder;
import com.ssiot.remote.yun.webapi.WS_API;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CompositeChartAct extends Activity {
    private static final String tag = "";
    Context mContext;
    ArrayList<YunNodeModel> mYunModels;
    LinearLayout mChartContainer;
    Spinner mTableSpinner;
    Spinner mNodeSpinner;
    TextView mTime1Text;
    TextView mTime2Text;
    ListView mSensorListView;
    SensorAdapter mSensorAdapter;
    View mConfirmBtn;

    int mTableIndex = 2;//tableindex 1 十分钟 2 小时 3 天 4 月
    String mNodeUnique;
    int mStartTime1;
    int mStartTime2;

    XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();//创建数据层
    XYMultipleSeriesRenderer mMulRenderer = new XYMultipleSeriesRenderer();//创建你需要的图表最下面的图层
    GraphicalView mChartView = null;

    int[] colorResList = {R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4,
            R.color.color_5, R.color.color_6,};
    int[] drawableResList = {R.drawable.bg_circle_sensor_color1, R.drawable.bg_circle_sensor_color2,
            R.drawable.bg_circle_sensor_color3, R.drawable.bg_circle_sensor_color4,
            R.drawable.bg_circle_sensor_color5, R.drawable.bg_circle_sensor_color6};

    private static final int MSG_GET_END = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_GET_END:
                    List<XYStringHolder> datas = (List<XYStringHolder>) msg.obj;
                    Log.v(tag, "-----查询到数据个数" + datas.size());
                    if (null != datas){//msg.arg2表示是左边的曲线还是右边的曲线
                        addLine(mDataset, mMulRenderer, datas, msg.arg1, msg.arg2);
                        mChartView.repaint();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mYunModels = (ArrayList<YunNodeModel>) getIntent().getSerializableExtra("yunnodemodels");//已经全是sensor了
        mContext = this;
        if (null == mYunModels || mYunModels.size() == 0){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            return;
        }
        setContentView(R.layout.act_composite_chart);
        initViews();
    }

    private void initViews(){
        mTableSpinner = (Spinner) findViewById(R.id.spinner_table);
        mNodeSpinner = (Spinner) findViewById(R.id.spinner_node);
        mTime1Text = (TextView) findViewById(R.id.txt_time1);
        mTime2Text = (TextView) findViewById(R.id.txt_time2);
        mSensorListView = (ListView) findViewById(R.id.list_sensor);
        mConfirmBtn = findViewById(R.id.btn_confirm);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStartTime1 == 0 && mStartTime2 == 0){
                    Toast.makeText(CompositeChartAct.this,"未选择开始时间",Toast.LENGTH_SHORT).show();
                }
                final List<DeviceBean> selectedDevices = mSensorAdapter.getSelectedDevices();
                if (null == selectedDevices || selectedDevices.size() == 0){
                    Toast.makeText(CompositeChartAct.this,"未选择传感器",Toast.LENGTH_SHORT).show();
                }
                updateChartSetting();
                new GetDataThread(selectedDevices).start();
            }
        });

        initTableSpinner();
        initNodeSpinner();
        initTimeSelect(mTime1Text, true);
        initTimeSelect(mTime2Text, false);
        mChartContainer = (LinearLayout) findViewById(R.id.chart_container);
        initChartView(mChartContainer);
    }

    private class GetDataThread extends Thread{
        List<DeviceBean> selectedDevices;
        public GetDataThread(List<DeviceBean> sDevices){
            selectedDevices = sDevices;
        }
        @Override
        public void run() {
            int endTime1 = mStartTime1;
            int endTime2 = mStartTime2;
            if (mTableIndex == 2){//小时表
                endTime1 = mStartTime1 + 24 * 3600;
                endTime2 = mStartTime2 + 24 * 3600;
            } else if (mTableIndex == 3){//天表
                endTime1 = mStartTime1 + 30 * 24 * 3600;
                endTime2 = mStartTime2 + 30 * 24 *3600;
            } else if (mTableIndex == 4){
                endTime1 = mStartTime1 + 365 * 24 * 3600;
                endTime2 = mStartTime2 + 365 * 24 * 3600;
            }

            Log.v(tag, "~~~~~~~~~~" + mStartTime1 + "~~~" + mStartTime2);

            if (null != mSensorAdapter){
                for (DeviceBean d : selectedDevices){
                    if (mStartTime1 != 0) {
                        List<XYStringHolder> list;
                        if (mTableIndex == 2 && (System.currentTimeMillis()/1000)-mStartTime1 < 7 * 24 * 3600){
                            list = new WS_API().GetSensorHisData(mNodeUnique, mStartTime1, endTime1,
                                    mTableIndex, d.mDeviceTypeNo, d.mChannel);
                        } else {
                            list = new WS_API().GetSensorHisData_His(mNodeUnique, mStartTime1, endTime1,
                                    mTableIndex, d.mDeviceTypeNo, d.mChannel);
                        }
                        Message msg = mHandler.obtainMessage(MSG_GET_END);
                        msg.obj = list;
                        msg.arg1 = d.ColorRes;
                        msg.arg2 = 1;
                        mHandler.sendMessage(msg);
                    }
                    if (mStartTime2 != 0){
//                        List<XYStringHolder> list = new WS_API().GetSensorHisData(mNodeUnique,mStartTime2,endTime2,
//                                mTableIndex,d.mDeviceTypeNo,d.mChannel);
                        List<XYStringHolder> list;
                        if (mTableIndex == 2 && (System.currentTimeMillis()/1000)-mStartTime2 < 7 * 24 * 3600){
                            list = new WS_API().GetSensorHisData(mNodeUnique, mStartTime2, endTime2,
                                    mTableIndex, d.mDeviceTypeNo, d.mChannel);
                        } else {
                            list = new WS_API().GetSensorHisData_His(mNodeUnique, mStartTime2, endTime2,
                                    mTableIndex, d.mDeviceTypeNo, d.mChannel);
                        }
                        Message msg = mHandler.obtainMessage(MSG_GET_END);
                        msg.obj = list;
                        msg.arg1 = d.ColorRes;
                        msg.arg2 = 2;
                        mHandler.sendMessage(msg);
                    }

                }
            }
        }
    }

    private void initTableSpinner(){
        String[] mItems = {"一天内", "一月内", "一年内"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.my_spinner_itm_white, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTableSpinner .setAdapter(adapter);
        mTableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                mTableIndex = pos + 2;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mTableSpinner.setSelection(0, true);
    }

    private void initNodeSpinner(){
        String[] mItems = new String[mYunModels.size()];
        for (int i = 0; i < mYunModels.size(); i ++){
            mItems[i] = mYunModels.get(i).nodeStr;
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.my_spinner_itm_white, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNodeSpinner .setAdapter(adapter);
        mNodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
//                mNodeIndex = pos;
                mNodeUnique = mYunModels.get(pos).mNodeUnique;
                initSensorList(mYunModels.get(pos).list);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mNodeSpinner.setSelection(0, true);
    }

    private void initSensorList(List<DeviceBean> devices){
        mSensorAdapter = new SensorAdapter(devices);
        mSensorListView.setAdapter(mSensorAdapter);
    }

    private void initTimeSelect(final TextView tV, final boolean isFirst){
        tV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder bui = new AlertDialog.Builder(CompositeChartAct.this);
                final DatePicker datePicker = new DatePicker(CompositeChartAct.this);
                bui.setView(datePicker);
                bui.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date d = new Date(datePicker.getYear()-1900, datePicker.getMonth(),datePicker.getDayOfMonth());
                        if (isFirst){
                            mStartTime1 = (int) (d.getTime()/1000);
                        } else {
                            mStartTime2 = (int) (d.getTime()/1000);
                        }
                        tV.setText(d.toString());
                    }
                });
                bui.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                bui.create().show();
            }
        });
    }

    private class SensorAdapter extends BaseAdapter{
        List<DeviceBean> mDatas;
        boolean[] statusList;
        LayoutInflater inflater;

        private SensorAdapter(List<DeviceBean> datas){
            mDatas = datas;
            inflater = LayoutInflater.from(CompositeChartAct.this);
            statusList = new boolean[datas.size()];
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public DeviceBean getItem(int position) {
            DeviceBean d = mDatas.get(position);
            d.ColorRes = colorResList[position%colorResList.length];
            return d;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.itm_sensor_pick, null, false);
            ImageView statusImg = (ImageView) convertView.findViewById(R.id.itm_sensor_checkstatus);
            ImageView iconImg = (ImageView) convertView.findViewById(R.id.itm_sensor_img);
            TextView txt = (TextView) convertView.findViewById(R.id.itm_sensor_txt);

            DeviceBean d = mDatas.get(position);
            statusImg.setBackgroundResource(drawableResList[position%drawableResList.length]);
//            statusImg.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_circle_sensor_color1));
            statusImg.setImageResource(statusList[position] ? R.drawable.selected : 0);
            iconImg.setImageResource(d.getIconRes());
            txt.setText(d.mName);

            final int pos = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toogleStatus(pos);
                }
            });

            return convertView;

        }

        private void toogleStatus(int position){
            statusList[position] = !statusList[position];
            notifyDataSetChanged();
        }

        private ArrayList<DeviceBean> getSelectedDevices(){
            ArrayList<DeviceBean> list = new ArrayList<>();
            for (int i = 0; i <  statusList.length; i ++){
                if (statusList[i]){
                    list.add(getItem(i));
                }
            }
            return list;
        }
    }

    //---------------------------------------------------------------------------
    private void initChartView(LinearLayout chartContainer) {
        XYSeries mCurrentSeries;//创建具体的数据层
        XYSeriesRenderer mCurrentRenderer;//创建你需要在图层上显示的具体内容的图层

        mMulRenderer.setPointSize(8f);// achartengine的bug 这个要在chartview生成之前设置！！ 最好是大多设置都设好
        mChartView = ChartFactory.getLineChartView(mContext, mDataset, mMulRenderer);

        // enable the chart click events
        mMulRenderer.setClickEnabled(true);// 是否可点击
        mMulRenderer.setSelectableBuffer(20);// 点击区域大小  设置点的缓冲半径值(在某点附近点击时,多大范围内都算点击这个点)
        chartContainer.addView(mChartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        //----------------------
        Log.v(tag, "----initChartData-----");
        float textSize = mContext.getResources().getDimension(R.dimen.chartLableTextSize);
        mMulRenderer.setApplyBackgroundColor(true);// 设置是否显示背景颜色
        mMulRenderer.setBackgroundColor(Color.TRANSPARENT);// 设置背景颜色
        mMulRenderer.setMarginsColor(Color.TRANSPARENT);//设置周边背景色
        mMulRenderer.setAxisTitleTextSize(16);//Axis代表轴
        mMulRenderer.setChartTitleTextSize(0);//设置图表标题字体大小,可以设置0是把标题隐藏掉
        mMulRenderer.setLabelsTextSize(textSize);//15
        mMulRenderer.setLegendTextSize(textSize);
        mMulRenderer.setMargins(new int[] { 30, 55, 45 ,10});//20, 30, 15, 0
        mMulRenderer.setZoomButtonsVisible(false);//一个大bug？？？？会drawbitmap空指针
        mMulRenderer.setPointSize(1.0f); // 设置图表上显示点的大小 ??
        mMulRenderer.setLabelsColor(Color.RED);//// 设置坐标颜色
        mMulRenderer.setAxesColor(mContext.getResources().getColor(R.color.DarkGreen));//设置坐标轴颜色？？
        mMulRenderer.setXLabels(0);//copy huiyun ??//设置X轴显示的刻度标签的个数
        mMulRenderer.setYLabels(10);
        mMulRenderer.setXLabelsColor(mContext.getResources().getColor(R.color.c333333));
        mMulRenderer.setYLabelsColor(0, mContext.getResources().getColor(R.color.c333333));
        mMulRenderer.setXLabelsAlign(Paint.Align.CENTER);//设置刻度线与X轴之间的相对位置
        mMulRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mMulRenderer.setShowLegend(false);//设置是否显示图例.//是否显示图例说明
        mMulRenderer.setShowGrid(true);//设置是否显示网格
        mMulRenderer.setGridColor(mContext.getResources().getColor(R.color.help_button_view));
        mMulRenderer.setChartTitleTextSize(1f+textSize);
        mMulRenderer.setAxisTitleTextSize(textSize);
        mMulRenderer.setPanEnabled(true, false);//设置是否允许拖动
        //mMulRenderer.setPointSize(4.0f);
//        mMulRenderer.setXLabelsAngle(-60f);//设置x轴显示的倾斜度

        //mChartView.setBackgroundColor(Color.RED);
        //mChartView.invalidate();
        mChartView.repaint();
    }

    private void addLine(XYMultipleSeriesDataset dataSet, XYMultipleSeriesRenderer mulRender,
                         List<XYStringHolder> datas, int colorRes, int lineMark){
        XYSeries mCurrentSeries = new XYSeries("类似于sheet1");//创建具体的数据层
        dataSet.addSeries(mCurrentSeries);
        XYSeriesRenderer renderer = new XYSeriesRenderer();//创建你需要在图层上显示的具体内容的图层
        mulRender.addSeriesRenderer(renderer);

        renderer.setPointStyle(PointStyle.POINT);///折线点的样式//设置为point就没有点了，但
        renderer.setPointStrokeWidth(1f);//折线点的大小 ???错
        if (colorRes > 0){
            renderer.setColor(mContext.getResources().getColor(colorRes));
        } else {
            renderer.setColor(mContext.getResources().getColor(R.color.blue_2));
        }
        if (lineMark == 2){
            renderer.setPointStyle(PointStyle.CIRCLE);
        }
        renderer.setFillPoints(true);// 点的形状,是空心的还是实心的
        renderer.setLineWidth(3.0f);//折线宽度
        renderer.setDisplayChartValues(true);//设置显示折线的点对应的值
        renderer.setDisplayChartValuesDistance(5);//折线点的值距离折线点的距离
        renderer.setChartValuesSpacing(20.0f);
        renderer.setChartValuesTextSize(15);

        DecimalFormat df1 = new DecimalFormat(getLongestNumFormat(datas));
        renderer.setChartValuesFormat(df1);//值的显示小数点后位置
        mCurrentSeries.clear();
        if (null == datas || datas.size() == 0){
            Toast.makeText(mContext, "曲线图-无数据", Toast.LENGTH_SHORT).show();
            return;
        }
        int size = datas.size();
        int xLabelZone = size/6 > 0 ? size/6 : 1;
        Log.v(tag,"=================chart size =" + size);
        for (int i = 0; i < size; i ++){
            mCurrentSeries.add(i + 1, datas.get(size - i -1).yData);
            if (i % xLabelZone == xLabelZone/2){
//                mulRender.addTextLabel(i+1, formatXString2(datas.get(size - i - 1).xString));
            }

        }

//        double max = findMax(datas);
//        double min = findMin(datas);
//        mulRender.setYAxisMax(max + (max - min)/10);
//        mulRender.setYAxisMin(min - (max-min)/20);
//        mulRender.setXAxisMin(0);
//        mulRender.setXAxisMax(size);
    }

    private void updateChartSetting(){
        mDataset.clear();
        mMulRenderer.removeAllRenderers();
        if (mTableIndex == 2) {
            mMulRenderer.setXLabels(24);
        } else if (mTableIndex == 3){
            mMulRenderer.setXLabels(30);
        } else if (mTableIndex == 4){
            mMulRenderer.setXLabels(12);
        }
    }

    private double findMax(List<XYStringHolder> datas){
        double max = 0;
        if (null != datas && datas.size() > 0){
            max = datas.get(0).yData;
            for (XYStringHolder h : datas){
                if (max < h.yData){
                    max = h.yData;
                }
            }
        }
        return max;
    }

    private double findMin(List<XYStringHolder> datas){
        double min = 0;
        if (null != datas && datas.size() > 0){
            min = datas.get(0).yData;
            for (XYStringHolder h : datas){
                if (min > h.yData){
                    min = h.yData;
                }
            }
        }
        return min;
    }

    private String getLongestNumFormat(List<XYStringHolder> list){
        int longest = 0;
        if (null != list){
            for (XYStringHolder t : list){
                String str = ""+t.yData;
                int tmp = str.length() - str.indexOf(".") - 1;
                if (longest < tmp){
                    longest = tmp;
                }
            }
        }
        if (longest > 0){
            String formatStr = "0.";
            for (int i = 0; i < longest; i ++){
                formatStr += "0";
            }
            Log.v(tag, "----------getLongestNumFormat--------" + formatStr);
            return formatStr;
        }
        return "";
    }

    private String formatXString2(String str){
        Timestamp t = Timestamp.valueOf(str);
//        if (tableName.equals(TABLETEN)){
//            return ""+t.getHours() + ":" +t.getMinutes();
//        } else if (tableName.equals(TABLEHOUR)){
//            return ""+t.getHours() + "时";
//        } else if (tableName.equals(TABLEDAY)){
//            return ""+t.getDate() +"日";
//        } else if (tableName.equals(TABLEMONTH)){
//            return ""+t.getMonth() + "月";
//        } else if (tableName.equals(TABLEYEAR)){
//            return "" + (t.getYear() + 1900) + "年";
//        }
        return ""+t.getDate() +"日\n"  + t.getHours() + "时";
    }



}
