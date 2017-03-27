package com.ssiot.remote.yun.detail.sensors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.Text;
import com.ssiot.agri.R;
import com.ssiot.remote.yun.unit.XYStringHolder;
import com.ssiot.remote.yun.webapi.WS_API;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class ChartListAdapter extends BaseAdapter{
    private static final String tag = "ChartListAdapter";
    List<ChartListModel> mDatas;// = new ArrayList<XYStringHolder>();
    LayoutInflater inflater;
    Context mContext;

//    private static final int MSG_GET_END = 1;
//    private Handler mhander = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case MSG_GET_END:
//                    break;
//            }
//        }
//    };

    public ChartListAdapter(List data, Context c){
        mDatas = data;
        mContext = c;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewBatch vBatch;
        convertView = inflater.inflate(R.layout.itm_listchart, null, false);
        vBatch = new ViewBatch();
        vBatch.img_sensor = (ImageView) convertView.findViewById(R.id.img_sensor);
        vBatch.txt_sensorname = (TextView) convertView.findViewById(R.id.txt_sensorname);
        vBatch.txt_nowdata = (TextView) convertView.findViewById(R.id.txt_nowdata);
        vBatch.txt_today_high = (TextView) convertView.findViewById(R.id.txt_today_high);
        vBatch.txt_today_low = (TextView) convertView.findViewById(R.id.txt_today_low);
        vBatch.txt_yesterday_aver = (TextView) convertView.findViewById(R.id.txt_yesterday_aver);
        vBatch.txt_yesterday_high = (TextView) convertView.findViewById(R.id.txt_yesterday_high);
        vBatch.txt_yesterday_low = (TextView) convertView.findViewById(R.id.txt_yesterday_low);
        vBatch.txt_yesyes_aver = (TextView) convertView.findViewById(R.id.txt_yesyes_aver);
        vBatch.txt_yesyes_high = (TextView) convertView.findViewById(R.id.txt_yesyes_high);
        vBatch.txt_yesyes_low = (TextView) convertView.findViewById(R.id.txt_yesyes_low);
        vBatch.chart_container = (LinearLayout) convertView.findViewById(R.id.chart_container);
        vBatch.txt_sensorname_chart = (TextView) convertView.findViewById(R.id.txt_sensorname_chart);
        vBatch.txt_timezone = (TextView) convertView.findViewById(R.id.txt_timezone);

        ChartListModel m = mDatas.get(position);
        vBatch.img_sensor.setImageResource(m.deviceBean.getIconRes());
        vBatch.txt_sensorname.setText(m.deviceBean.mName);
        vBatch.txt_sensorname_chart.setText(m.deviceBean.mName);
        vBatch.txt_nowdata.setText(""+m.deviceBean.valueStr + m.deviceBean.getUnit());
        vBatch.txt_timezone.setText("近三天的数据");
        if (!m.GOT){
            new GetXYThread(vBatch, m).start();
        } else {
            updateUIData(vBatch, m);
        }
        return convertView;
    }

    private void updateUIData(ViewBatch vBatch, ChartListModel m){
        vBatch.chart_container.removeAllViews();
        initChartView(vBatch.chart_container, m.datas, m.deviceBean.mDeviceTypeNo);
        vBatch.txt_today_high.setText(""+m.todayHigh);
        vBatch.txt_today_low.setText(""+m.todayLow);
        vBatch.txt_yesterday_aver.setText("平均:"+m.yesAver);
        vBatch.txt_yesterday_high.setText(""+m.yesHigh);
        vBatch.txt_yesterday_low.setText(""+m.yesLow);
        vBatch.txt_yesyes_aver.setText("平均:"+m.yesyesAver);
        vBatch.txt_yesyes_high.setText(""+m.yesyesHigh);
        vBatch.txt_yesyes_low.setText(""+m.yesyesLow);
    }

    private class GetXYThread extends Thread{
        ViewBatch vBatch;
        ChartListModel model;
        public GetXYThread(final ViewBatch vBatch, ChartListModel model){
            this.vBatch = vBatch;
            this.model = model;
        }
        @Override
        public void run() {
            int endTime = (int) (System.currentTimeMillis()/1000);
            int startTime = endTime - 3 * 24 * 3600;
            final List<XYStringHolder> list = new WS_API().GetSensorHisData(model.nodeunique,startTime,endTime,2,
                    model.deviceBean.mDeviceTypeNo,model.deviceBean.mChannel);
            model.datas = list;
            model.GOT = true;
            model.calculateAllData();

            vBatch.chart_container.post(new Runnable() {
                @Override
                public void run() {
                    if (null != list && list.size() > 0) {
                        updateUIData(vBatch,model);
                    }
                }
            });
        }
    }


    private void initChartView(LinearLayout chartContainer, List<XYStringHolder> datas, int sensorno) {
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();//创建数据层
        XYMultipleSeriesRenderer mMulRenderer = new XYMultipleSeriesRenderer();//创建你需要的图表最下面的图层
        XYSeries mCurrentSeries;//创建具体的数据层
        XYSeriesRenderer mCurrentRenderer;//创建你需要在图层上显示的具体内容的图层
        GraphicalView mChartView = null;
        if (null == mChartView) {
            // 有时候项目中开发,需要在界面的某一块展示视图,这时候我们可以通过getLineChartView得到一个GraphicalView类型的视图
            // (这边就不要需要在AndroidManifest.xml加上<activity
            // android:name="org.achartengine.GraphicalActivity" />.)
            mMulRenderer.setPointSize(8f);// achartengine的bug 这个要在chartview生成之前设置！！ 最好是大多设置都设好
            if (sensorno == 1281 || sensorno == 772){//光照度 or 雨量
                mChartView = ChartFactory.getBarChartView(mContext, mDataset, mMulRenderer, BarChart.Type.DEFAULT);
                mMulRenderer.setBarSpacing(2f);
            } else {
                mChartView = ChartFactory.getLineChartView(mContext, mDataset, mMulRenderer);
            }

            // enable the chart click events
            mMulRenderer.setClickEnabled(true);// 是否可点击
            mMulRenderer.setSelectableBuffer(20);// 点击区域大小  设置点的缓冲半径值(在某点附近点击时,多大范围内都算点击这个点)
            mChartView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
//                    if (seriesSelection == null) {
//                        Log.e(tag, "No chart element");
//                    } else {
////                        Toast.makeText(getActivity(),
////                                "第几条线 " + seriesSelection.getSeriesIndex()
////                                        + " 第几个点 " + seriesSelection.getPointIndex()
////                                        + "  X=" + seriesSelection.getXValue()
////                                        + ", Y="
////                                        + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
//                    }
                }
            });
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
            mMulRenderer.setXLabels(10);//copy huiyun ??//设置X轴显示的刻度标签的个数
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

            XYSeries series = new XYSeries("类似于sheet1");//创建具体的数据层
            mDataset.addSeries(series);
            mCurrentSeries = series;
            // create a new renderer for the new series
            XYSeriesRenderer renderer = new XYSeriesRenderer();//创建你需要在图层上显示的具体内容的图层
            mMulRenderer.addSeriesRenderer(renderer);

            renderer.setPointStyle(PointStyle.POINT);///折线点的样式//设置为point就没有点了，但
            renderer.setPointStrokeWidth(1f);//折线点的大小 ???错
            renderer.setColor(mContext.getResources().getColor(R.color.blue_2));//R.color.sta_line
            renderer.setFillPoints(true);// 点的形状,是空心的还是实心的
            renderer.setLineWidth(3.0f);//折线宽度
            renderer.setDisplayChartValues(false);//设置显示折线的点对应的值
//            renderer.setDisplayChartValuesDistance(5);//折线点的值距离折线点的距离
//            renderer.setChartValuesSpacing(20.0f);
//            renderer.setChartValuesTextSize(textSize);

            mChartView.repaint();
            mCurrentRenderer = renderer;
//        mChartView.repaint();
//        for (int i = 0; i < 17; i ++){//this is test
//            Random ra = new Random();
//            mCurrentSeries.add(i+1, ra.nextInt(10));//添加数据,一般都是for循环数据不断操作这一步添加的
//            mMulRenderer.addTextLabel(i+1, "x轴"+(i+1));
//            //x轴不显示原先的数字
//        }
            mMulRenderer.setXLabels(0);//设置X轴显示的刻度标签的个数 如果想要在X轴显示自定义的标签，那么首先要设置renderer.setXLabels(0);  如果不设置为0，那么所设置的Labels会与原X坐标轴labels重叠
            mChartView.repaint();

            Log.v(tag, "----initChartData----- end");
            if (null != mChartView){
                mCurrentSeries.clear();
                if (null == datas || datas.size() == 0){
                    Toast.makeText(mContext, "曲线图-近期无数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                int size = datas.size();
                int xLabelZone = size/6 > 0 ? size/6 : 1;
                Log.v(tag,"=================chart size =" + size);
                for (int i = 0; i < size; i ++){
                    mCurrentSeries.add(i + 1, datas.get(size - i -1).yData);
                    if (i % xLabelZone == xLabelZone/2){
                        mMulRenderer.addTextLabel(i+1, formatXString2(datas.get(size - i - 1).xString));
                    }

                }

                DecimalFormat df1 = new DecimalFormat(getLongestNumFormat(datas));
                mCurrentRenderer.setChartValuesFormat(df1);//值的显示小数点后位置
                double max = findMax(datas);
                double min = findMin(datas);
                mMulRenderer.setYAxisMax(max + (max - min)/10);
                mMulRenderer.setYAxisMin(min - (max-min)/20);
                mMulRenderer.setXAxisMin(0);
                mMulRenderer.setXAxisMax(size);
//                mChartView.repaint();
            }
        } else {
            Log.w(tag, "----initChartView--already have chartview");
            mChartView.setBackgroundColor(Color.RED);
            mChartView.invalidate();
        }
        mChartView.repaint();
    }


    private void initChartData(){

    }

//    private void refreshChart(List<XYStringHolder> datas){
//        if (null != mChartView){
//            mCurrentSeries.clear();
//            if (null == datas || datas.size() == 0){
//                Toast.makeText(getActivity(), "曲线图-近期无数据", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            int size = datas.size();
//            for (int i = 0; i < datas.size(); i ++){
//                mCurrentSeries.add(i + 1, mDatas.get(size - i -1).yData);
//                mMulRenderer.addTextLabel(i+1, formatXString(datas.get(size - i - 1).xString));
//            }
//            DecimalFormat df1 = new DecimalFormat(getLongestNumFormat(datas));
//            mCurrentRenderer.setChartValuesFormat(df1);//值的显示小数点后位置
//            double max = findMax(datas);
//            double min = findMin(datas);
//            mMulRenderer.setYAxisMax(max + (max - min)/10);
//            mMulRenderer.setYAxisMin(min - (max-min)/20);
//            mMulRenderer.setXAxisMin(0);
//            mMulRenderer.setXAxisMax(10);
//            mChartView.repaint();
//        }
//    }

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

    private class ViewBatch {
        ImageView img_sensor;
        TextView txt_sensorname;
        TextView txt_nowdata;
        TextView txt_yesterday_aver;
        TextView txt_yesyes_aver;
        TextView txt_today_high;
        TextView txt_yesterday_high;
        TextView txt_yesyes_high;
        TextView txt_today_low;
        TextView txt_yesterday_low;
        TextView txt_yesyes_low;
        TextView txt_sensorname_chart;
        LinearLayout chart_container;
        TextView txt_timezone;

    }

}
