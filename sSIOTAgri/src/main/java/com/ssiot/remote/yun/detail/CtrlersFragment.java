package com.ssiot.remote.yun.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ssiot.agri.R;
import com.ssiot.remote.yun.WheelValAct;
import com.ssiot.remote.yun.detail.cntrols.FarmDetailCtrlerPagerActivity;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.TintedBitmapDrawable;
import com.ssiot.remote.yun.monitor.YunNodeModel;

import java.util.ArrayList;
import java.util.List;

public class CtrlersFragment extends DevicesFragment{
    private static final String tag = "CtrlersFragment";
    final ArrayList<DeviceBean> devices = new ArrayList();
    CtrNodeAdapter adapter;// = new CtrNodeAdapter(mYunNodeModels);
    
    private static final int MSG_NOTIFY = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_NOTIFY:
                    if (null != adapter){
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e(tag, "----adapter = null !!!");
                    }
                    
                    break;

                default:
                    break;
            }
        };
    };
    
    @Override
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        return paramLayoutInflater.inflate(R.layout.activity_farm_monitor_detail_ctrlers_fragment, paramViewGroup, false);//TODO 暂时layout
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ListView localListView = (ListView) view.findViewById(android.R.id.list);
        super.onViewCreated(view, savedInstanceState);
//        View localView1 = getActivity().getLayoutInflater().inflate(R.layout.activity_farm_monitor_detail_ctrlers_list_header, localListView, false);
//        View localView2 = localView1.findViewById(android.R.id.icon2);
//        this.footerCreate = localView2;
//        localView2.setOnClickListener(this.groupedAdapter);
//        View localView3 = localView1.findViewById(android.R.id.empty);
//        this.footerEmpty = localView3;
//        localView3.setOnClickListener(this.groupedAdapter);
//        LinearLayoutForListView localLinearLayoutForListView = (LinearLayoutForListView)localView1.findViewById(2131296415);
//        this.footerList = localLinearLayoutForListView;
//        localLinearLayoutForListView.setAdapter(this.groupedAdapter);
//        localLinearLayoutForListView.setUseDevider(false);
//        localListView.addHeaderView(getLayoutInflater(paramBundle).inflate(2130903247, localListView, false));
//        localListView.addFooterView(localView1);
        adapter = new CtrNodeAdapter(mYunNodeModels);
        localListView.setAdapter(adapter);
        localListView.setDividerHeight(0);
//        this.observer.onChanged();
//        updateCreateIcon(localView2);
//        updateEmptyText((TextView)localView3);
    }

    @Override
    int getTabID() {
        return R.id.tcagri_farm_detail_tab_cntrols;
    }

    @Override
    void add(@NonNull DeviceBean paramDevice) {
//        if (paramDevice.isSensorDevice())
//            if (!paramDevice.isGroupDevice());
//          while (true)
//          {
//            return;
//            this.allSensorDevices.add(paramDevice);
//            continue;
//            if (paramDevice.isGroupDevice())
//              this.groupedAdapter.add(paramDevice);
//            else
//              this.adapter.add(paramDevice);
//          }
    }

    @Override
    int totalDevices() {
//        int i = this.groupedAdapter.getCount();
//        Iterator localIterator = this.devices.iterator();
//        while (true)
//        {
//          if (!localIterator.hasNext())
//            return i;
//          i += ((CntrolDeviceAdapter)localIterator.next()).getCount();
//        }
        return 0;
    }
    
    @Override
    void updateUI() {
        // TODO Auto-generated method stub
    	mHandler.sendEmptyMessage(MSG_NOTIFY);
    }
    
    private static final int REQUEST_TIME = 1;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent paramIntent) {
        Log.v(tag, "----onActivityResult----" + requestCode);//可能有bug
        if (requestCode == REQUEST_TIME){
        	if (Activity.RESULT_OK == resultCode){
        		//TODO 
        	}
        }
    }
    
    private class CtrDeviceAdapter extends BaseAdapter{
        private List<DeviceBean> mDeviceBeans;
        private YunNodeModel nodeModel;
        
        public CtrDeviceAdapter(List<DeviceBean> datas, YunNodeModel node){
            mDeviceBeans = datas;
            nodeModel = node;
        }

        @Override
        public int getCount() {
            return mDeviceBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return mDeviceBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView){
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.activity_farm_monitor_detail_ctr_item, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final DeviceBean device = mDeviceBeans.get(position);
//            holder.mDeviceImg.setImageResource(R.drawable.ic_section_default);
            holder.mDeviceImg.setImageDrawable(new TintedBitmapDrawable(getResources(), R.drawable.ic_section_default, R.color.device_connect_actived));
            holder.mDeviceText.setText(device.mName);
            if (device.getContactStatus() == -1){
                holder.mDeviceButton.setImageResource(R.drawable.ic_field_disconnect);
            } else if (device.getContactStatus() == 0){
                holder.mDeviceButton.setImageResource(R.drawable.ic_control_start);
            } else {
                holder.mDeviceButton.setImageResource(R.drawable.ic_control_stop);
            }
            
            holder.mDeviceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (device.getContactStatus() == 0){
                        Intent intent = new Intent(getActivity(), WheelValAct.class);
//                        startActivityForResult(intent, REQUEST_TIME);//TODO
                    } else {
                        //TODO
                    }
                    
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FarmDetailCtrlerPagerActivity.class);
                    intent.putExtra("devicebean", device);
                    intent.putExtra("yunnodemodel", nodeModel);
                    startActivity(intent);
                }
            });
            return convertView;
        }
        
        private final class ViewHolder{
            ImageView mDeviceImg;
            TextView mDeviceText;
            ImageView mDeviceButton;
            
            public ViewHolder(View v){
                mDeviceImg = (ImageView) v.findViewById(R.id.ctr_device_img);
                mDeviceText = (TextView) v.findViewById(R.id.ctr_device_txt);
                mDeviceButton = (ImageView) v.findViewById(R.id.ctr_device_button);
                v.setTag(this);
            }
        }
    }
    
    private class CtrNodeAdapter extends BaseAdapter{
        private List<YunNodeModel> mDatas;
        
        public CtrNodeAdapter(List<YunNodeModel> datas){
            mDatas = datas;
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
            ViewHolder holder;
            if (null == convertView){
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.activity_farm_monitor_detail_sensors_item0, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            YunNodeModel yunNode = mDatas.get(position);
            holder.mNodeTitleText.setText(yunNode.nodeStr);
            holder.mNodeStatus.setImageResource(isOnline(yunNode) ? R.drawable.online : R.drawable.offline);
            CtrDeviceAdapter adapter1 = new CtrDeviceAdapter(yunNode.list, yunNode);
            holder.mList.setAdapter(adapter1);
            return convertView;
        }
        
        private boolean isOnline(YunNodeModel y){
            if (null != y && y.list != null){
                for (DeviceBean d : y.list){
                    if (d.getContactStatus() >= 0){
                        return true;
                    }
                }
            }
            return false;
        }
        
        private final class ViewHolder{
            TextView mNodeTitleText;
            ImageView mNodeStatus;
            ListView mList;
            
            public ViewHolder(View v){
                mNodeTitleText = (TextView) v.findViewById(R.id.single_node_title);
                mNodeStatus = (ImageView) v.findViewById(R.id.single_node_status);
                mList = (ListView) v.findViewById(R.id.single_node_list);
                v.setTag(this);
            }
        }
    }
    
}