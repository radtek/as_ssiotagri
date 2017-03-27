package com.ssiot.remote.yun.detail;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mining.app.zxing.view.ViewfinderView;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.detail.sensors.FarmDetailSensorPagerAct;
import com.ssiot.remote.yun.detail.sensors.NodeChartDetailAct;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SensorsFragment extends DevicesFragment{
    private static final String tag = "SensorsFragment";
    
//    final SensorAdapter1 workingSensorsArray = new SensorAdapter1();
    private NodeAdapter adapter;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(tag, "----onCreateView----");
        
        return inflater.inflate(R.layout.activity_farm_monitor_detail_sensors_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        View parentBlock = view.findViewById(R.id.field_regular);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        super.onViewCreated(view, savedInstanceState);
        adapter = new NodeAdapter(mYunNodeModels);
        listView.setAdapter(adapter);
    }

    @Override
    int getTabID() {
        return R.id.tcagri_farm_detail_tab_sensors;
    }

    @Override
    void add(@NonNull DeviceBean paramDevice) {//TODO 考虑主线程和子线程
//        workingSensorsArray.Items.add(paramDevice);
        mHandler.sendEmptyMessage(MSG_NOTIFY);
//        try
//        {
//            if (paramDevice.isGroupDevice()) {
//                this.groupedSensorsArray.addAsync(paramDevice);
//                return;
//            }
//            if (paramDevice.isOffline()) {
//                this.offlineSensorsArray.addAsync(paramDevice);
//                continue;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//        }
//        if (paramDevice.isWarning()) {
//            this.warningSensorsArray.addAsync(paramDevice);
//        } else {
//            this.workingSensorsArray.addAsync(paramDevice);
//        }
    }

    @Override
    int totalDevices() {
//        if (isUIThread()) {
//          this.warningSensorsArray.notifyDataSetChanged();
//          this.offlineSensorsArray.notifyDataSetChanged();
//          this.groupedSensorsArray.notifyDataSetChanged();
//          this.workingSensorsArray.notifyDataSetChanged();
//        }
//        return this.warningSensorsArray.getCount() + this.offlineSensorsArray.getCount() + this.groupedSensorsArray.getCount() + this.workingSensorsArray.getCount();
        return 0;
    }
    
    @Override
    void updateUI() {
        // TODO Auto-generated method stub
        mHandler.sendEmptyMessage(MSG_NOTIFY);
    }
    
    
    
    private final class SensorAdapter1 extends BaseAdapter implements View.OnClickListener, AdapterView.OnItemClickListener{// implements View.OnClickListener, 
    // private DeviceTypeParams params;
        private final List<DeviceBean> Items;// = new ArrayList();
        private YunNodeModel mInYNModel;

        SensorAdapter1(List<DeviceBean> devices,YunNodeModel ynm) {
            Items = devices;
            mInYNModel = ynm;
        }

        @Override
        public int getCount() {
            return Items.size();
        }

        @Override
        public DeviceBean getItem(int position) {
            if (null == Items || position >= Items.size()) {
                return null;//有这种情况，最后一行的单个left item
            }
            return Items.get(position);
        }

        @Override
        public long getItemId(int position) {
            DeviceBean d = Items.get(position);
            return d.mDeviceTypeNo;// TODO
        }

        @Override
        public View getView(int position, View paramView, ViewGroup paramViewGroup) {
            ViewHolder localViewHolder;
            DeviceBean localDevice;
            if ((paramView.getTag() instanceof ViewHolder)) {
                localViewHolder = (ViewHolder) paramView.getTag();
            } else {
                localViewHolder = new ViewHolder(paramView);
                paramView.setOnClickListener(this);
            }
            localViewHolder.position = position;

            localDevice = getItem(position);
            if (localDevice == null) {
                paramView.setVisibility(View.GONE);
            } else {
                paramView.setVisibility(View.VISIBLE);
                localViewHolder.icon.setImageResource(localDevice.getIconRes());
                localViewHolder.name.setText(localDevice.mName + (localDevice.mChannel > 0 ? localDevice.mChannel : ""));
                localViewHolder.value.setText("" + localDevice.valueStr + localDevice.getUnit());//改成了valueStr
                
                // if (this.params == null)
                // this.params = new DeviceTypeParams(getActivity());
                // localViewHolder.value.setText(localDevice.getLastValue() +
                // ' ' + this.params.getUnit(localDevice.getDeviceType(),
                // false));
                // if ((localDevice.isOffline()) ||
                // (localDevice.isGroupDevice())){
                // localViewHolder.value.setVisibility(View.GONE);
                // }else{
                // localViewHolder.value.setVisibility(View.VISIBLE);
                // }
            }
            return paramView;
        }

        @Override
        public void onClick(View paramView) {
            if ((paramView.getTag() instanceof ViewHolder)) {
                ViewHolder localViewHolder = (ViewHolder) paramView.getTag();
                onItemClick(null, paramView, localViewHolder.position, getItemId(localViewHolder.position));
            }
        }
        
        @Override
        public void onItemClick(AdapterView<?> paramAdapterView, View
                paramView, int paramInt, long paramLong) {
            // listener.hideHint();
            DeviceBean localDevice = getItem(paramInt);
            if ((localDevice != null) && (paramView != null)) {
                Intent localIntent = new Intent(getActivity(), FarmDetailSensorPagerAct.class);
//                localIntent.putExtra("", localDevice)
//                        .putExtra("can change settings", canChangeDeviceSetting())
//                        .putExtra("can control devices", canControlDevice())
//                        .putExtra("can change settings", canChangeGroupDevice())
//                        .putExtra("can show statistics", canSeeDeviceStatictics());
                localIntent.putExtra("devicebean", localDevice);
                localIntent.putExtra("yunnodemodel", mInYNModel);
                startActivityForResult(localIntent, 0);
                Log.v(tag, "--------onItemClick--------" + paramInt + " " + localDevice.mName);
            }
        }

        private final class ViewHolder {
            final ImageView icon;
            final TextView name;
            int position;
            final TextView value;

            ViewHolder(View v) {
                icon = ((ImageView) v.findViewById(android.R.id.icon));
                name = ((TextView) v.findViewById(android.R.id.text2));
                value = ((TextView) v.findViewById(android.R.id.text1));
                v.setTag(this);
            }
        }
    }

  //一行的adapter
    private final class SensorsAdapter2 extends BaseAdapter {

        @NonNull
        private final SensorAdapter1 Items;

        @NonNull
        final View block;

        @Nullable
        final View empty;

        @NonNull
        private final DataSetObserver observer = new DataSetObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
                if (isEmpty()) {
                    if (null != block){
                        block.setVisibility(View.GONE);
                    }
                    if (empty != null) {
                        empty.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (null != block){
                        block.setVisibility(View.VISIBLE);
                    }
                    if (empty != null) {
                        empty.setVisibility(View.GONE);
                    }
                }
            }
        };

        SensorsAdapter2(@NonNull SensorAdapter1 paramView1, @Nullable View blockView, View emptyView) {
            block = blockView;
            empty = emptyView;
            Items = paramView1;
            Items.registerDataSetObserver(this.observer);
            observer.onChanged();
        }

        public int getCount() {
            return Items.getCount() / 2 + Items.getCount() % 2;
        }

        public Pair<DeviceBean, DeviceBean> getItem(int paramInt) {
            return Pair.create(Items.getItem(paramInt * 2), Items.getItem(1 + paramInt * 2));
        }

        public long getItemId(int paramInt) {
            return 0L;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
            ViewHolder localViewHolder;
            if ((paramView != null) && ((paramView.getTag() instanceof ViewHolder))) {
                localViewHolder = (ViewHolder) paramView.getTag();
            } else {
                paramView = LayoutInflater.from(paramViewGroup.getContext()).inflate(
                        R.layout.activity_farm_monitor_detail_sensors_item2, paramViewGroup, false);
                localViewHolder = new ViewHolder(paramView);
            }
            Items.getView(paramInt * 2, localViewHolder.leftItem, paramViewGroup);
            Items.getView(1 + paramInt * 2, localViewHolder.rightItem, paramViewGroup);

            if ((localViewHolder.leftItem.getVisibility() != View.VISIBLE)
                    || (localViewHolder.rightItem.getVisibility() != View.VISIBLE)) {
                localViewHolder.divider.setVisibility(View.GONE);
            } else {
                localViewHolder.divider.setVisibility(View.VISIBLE);
            }

            if ((localViewHolder.leftItem.getVisibility() == View.VISIBLE)
                    || (localViewHolder.rightItem.getVisibility() == View.VISIBLE)) {
                paramView.setVisibility(View.VISIBLE);
            } else {
                paramView.setVisibility(View.GONE);
            }
            return paramView;
        }

        private final class ViewHolder {
            final View divider;
            final View leftItem;
            final View rightItem;

            ViewHolder(View v) {
                this.divider = v.findViewById(R.id.divider);
                this.leftItem = v.findViewById(R.id.item_left);
                this.rightItem = v.findViewById(R.id.item_right);
                v.setTag(this);
            }
        }
    }
    
    private class NodeAdapter extends BaseAdapter{
        private List<YunNodeModel> mDatas;
        
        public NodeAdapter(List<YunNodeModel> datas){
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
            final YunNodeModel yunNode = mDatas.get(position);
            holder.mNodeTitleText.setText(yunNode.nodeStr);
            holder.mNodeStatus.setImageResource(isOnline(yunNode) ? R.drawable.online : R.drawable.offline);
            holder.mTimeView.setText(formatShowTime(yunNode.mLastTime));//TODO 二代节点显示的是数据的时间，3代节点？
            holder.mTimeView.setVisibility(View.VISIBLE);
            holder.mChartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), NodeChartDetailAct.class);
                    i.putExtra("yunnodemodel",yunNode);
                    startActivity(i);
                }
            });
            SensorAdapter1 adapter1 = new SensorAdapter1(yunNode.list, yunNode);
            holder.mList.setAdapter(new SensorsAdapter2(adapter1, null, null));
            return convertView;
        }
        
        private boolean isOnline(YunNodeModel m){
            if (null != m && m.list != null){
                for (int i = 0; i < m.list.size(); i ++){
                    DeviceBean d = m.list.get(i);
                    if (null != d.mTime && System.currentTimeMillis() - d.mTime.getTime() < (long)Utils.offlineSeconds * 1000){
                        return true;
                    }
                }
            }
            return false;
        }

        private String formatShowTime(Timestamp t){
            String str = Utils.formatTime(t);
            if (null != str && str.length() > 5){
                return str.substring(5, str.length());
            }
            return "";
        }
        
        private final class ViewHolder{
            TextView mNodeTitleText;
            ImageView mNodeStatus;
            TextView mTimeView;
            View mChartBtn;
            ListView mList;
            
            public ViewHolder(View v){
                mNodeTitleText = (TextView) v.findViewById(R.id.single_node_title);
                mNodeStatus = (ImageView) v.findViewById(R.id.single_node_status);
                mTimeView = (TextView) v.findViewById(R.id.single_node_time);
                mChartBtn = v.findViewById(R.id.single_node_chart);
                mList = (ListView) v.findViewById(R.id.single_node_list);
                v.setTag(this);
            }
        }
    }
    
}