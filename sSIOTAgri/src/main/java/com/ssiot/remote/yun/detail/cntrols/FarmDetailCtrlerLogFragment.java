package com.ssiot.remote.yun.detail.cntrols;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.agri.R;
import com.ssiot.remote.data.model.ControlLogModel;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.webapi.WS_API;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FarmDetailCtrlerLogFragment extends Fragment{
    private SwipeRefreshLayout refresh;
    private ListView list;
    private ControlLogAdapter mAdapter;
    private DeviceBean device;
    private YunNodeModel mYunNodeModel;
    private List<ControlLogModel> mLogs = new ArrayList<ControlLogModel>();
    
    private static final int MSG_GET_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    refresh.setRefreshing(false);
                    if (null != mAdapter){
                        mAdapter.notifyDataSetChanged();
                    }
                    break;

                default:
                    break;
            }
        };
    };
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        device = ((DeviceBean) getActivity().getIntent().getSerializableExtra("devicebean"));
        mYunNodeModel = ((YunNodeModel) getActivity().getIntent().getSerializableExtra("yunnodemodel"));
        if (TextUtils.isEmpty(mYunNodeModel.mNodeUnique) || device == null || mYunNodeModel == null){
            Toast.makeText(getActivity(), "数据出现问题mNodeUnique = null", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_farm_monitor_detail_ctrler_logs_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        list = (ListView) view.findViewById(android.R.id.list);
        super.onViewCreated(view, savedInstanceState);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                new GetLogThread().start();
            }
        });
        list.addHeaderView(getLayoutInflater(savedInstanceState).inflate(
                R.layout.activity_farm_monitor_detail_ctrler_logs_list_header_item, list, false));
        list.setDivider(null);
        list.setDividerHeight(0);
        mAdapter = new ControlLogAdapter(getActivity(), mLogs);
        list.setAdapter(mAdapter);
        new GetLogThread().start();
    }
    
    private class GetLogThread extends Thread{
        @Override
        public void run() {//TODO pagelist一次显示10个   改成接口 TODO
//            List<ControlLogModel> data = null;//new ControlLog().GetModelPageList(" UniqueID='" + mYunNodeModel.mNodeUnique + "' and DeviceNo=" + device.mChannel);
            List<ControlLogModel> data = new WS_API().GetControlLogs(mYunNodeModel.mNodeUnique, device.mChannel, 10, 1);
            mLogs.clear();
            if (null != data){
                for (ControlLogModel m : data){
                    if (m._starttype == 1 || m._starttype == 2 || m._starttype == 3 || m._starttype == 5 || m._starttype == 6){
                        mLogs.add(m);
                    }
                }
                mHandler.sendEmptyMessage(MSG_GET_END);
            }
        }
    }
    
    public class ControlLogAdapter extends BaseAdapter{
        LayoutInflater inflater;
        List<ControlLogModel> mData;
        
        public ControlLogAdapter(Context c, List<ControlLogModel> logs){
            inflater = LayoutInflater.from(c);
            mData = logs;
        }
        
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold holder;
            if (null == convertView){
                holder = new ViewHold();
                convertView = inflater.inflate(R.layout.activity_farm_monitor_detail_ctrler_logs_list_item, parent, false);
                holder.time_HourMinute = (TextView) convertView.findViewById(R.id.time_HourMinute);
                holder.time_YearMonthDay = (TextView) convertView.findViewById(R.id.time_YearMonthDay);
                holder.Timing = (TextView) convertView.findViewById(R.id.Timing);
                holder.CtrlerMode = (TextView) convertView.findViewById(R.id.CtrlerMode);
                convertView.setTag(holder);
            } else {
                holder = (ViewHold) convertView.getTag();
            }
            ControlLogModel log = mData.get(position);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yy/MM/dd");
            holder.time_HourMinute.setText(sdf.format(log._createtime));
            holder.time_YearMonthDay.setText(sdf2.format(log._createtime));
            if (log._runtime > 0){
            	holder.Timing.setVisibility(View.VISIBLE);
            	holder.Timing.setText(log._runtime + "秒");
            } else {
            	holder.Timing.setVisibility(View.GONE);
            }
            
            switch (log._starttype) {
                case 1:
                    holder.CtrlerMode.setText("立即开启");
                    break;
                case 2:
                	holder.CtrlerMode.setText("立即关闭");
                	break;
                case 3:
                case 5:
                    holder.CtrlerMode.setText("定时开启");
                    break;
                case 6:
                    holder.CtrlerMode.setText("触发控制");
                    break;

                default:
                    break;
            }
            
            return convertView;
        }
        
        private class ViewHold{
            TextView time_HourMinute;
            TextView time_YearMonthDay;
            TextView Timing;
            TextView CtrlerMode;
        }
        
    }
    
}