package com.ssiot.remote.yun.detail.sensors;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.Text;
import com.ssiot.agri.R;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;

import java.util.ArrayList;
import java.util.List;

public class NodeChartDetailAct extends Activity {
    YunNodeModel yModel;

    TextView mNodeTitle;
    GridView mGridView;//传感器实时数据
    RadioGroup mDayPickGroup;
    ListView mChartList;
    ChartListAdapter adapter;

    List<ChartListModel> mChartListModels = new ArrayList<ChartListModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        yModel = (YunNodeModel) getIntent().getSerializableExtra("yunnodemodel");
        if (null == yModel){
            Toast.makeText(this,"出现错误，请重试",Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.act_node_chart);
        initViews();
    }

    private void initViews(){
        mNodeTitle = (TextView) findViewById(R.id.node_title);
        mGridView = (GridView) findViewById(R.id.gridview);
        mDayPickGroup = (RadioGroup) findViewById(R.id.rg_daypick);
        mChartList = (ListView) findViewById(R.id.list_charts);

        getChartIndexDatas();
        adapter = new ChartListAdapter(mChartListModels, this);//
        mChartList.setAdapter(adapter);

        mNodeTitle.setText(yModel.nodeStr + "(" + yModel.mNodeNo + ")");
        initSensorGridView();
    }

    private void initSensorGridView(){
        if(null != yModel.list) {
            mGridView.setAdapter(new SensorAdapter(yModel.list));
        }
    }

    private void getChartIndexDatas(){
        mChartListModels.clear();
        if (null != yModel && yModel.list != null && yModel.list.size() > 0 && yModel.nodeType == DeviceBean.TYPE_SENSOR){
            for (DeviceBean d : yModel.list){
                ChartListModel m = new ChartListModel();
                m.nodeunique = yModel.mNodeUnique;
                m.deviceBean = d;
                mChartListModels.add(m);
            }
        }
    }

    private class SensorAdapter extends BaseAdapter{
        List<DeviceBean> list;
        LayoutInflater inflater;
        private  SensorAdapter(List<DeviceBean> list){
            this.list = list;
            inflater = LayoutInflater.from(NodeChartDetailAct.this);
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.itm_sensor_grid, null, false);
            ImageView imgView = (ImageView) convertView.findViewById(R.id.itm_grid_img);
            TextView nameView = (TextView) convertView.findViewById(R.id.itm_grid_sensorname);
            TextView valueView = (TextView) convertView.findViewById(R.id.itm_grid_sensorvalue);
            DeviceBean d = list.get(position);
            imgView.setImageResource(d.getIconRes());
            nameView.setText(d.mName);
            valueView.setText(d.valueStr + d.getUnit());
            return convertView;
        }
    }
}
