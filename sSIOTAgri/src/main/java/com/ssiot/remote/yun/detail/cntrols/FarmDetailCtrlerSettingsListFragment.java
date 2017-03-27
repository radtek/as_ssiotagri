package com.ssiot.remote.yun.detail.cntrols;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ControlActionInfoModel;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;

import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FarmDetailCtrlerSettingsListFragment extends Fragment{
    private static final String tag = "FarmDetailCtrlerSettingsListFragment";
    SwipeRefreshLayout refresh;
    ListView mListView;
    ImplementAdapter mAdapter;
    List<CtrlRuleBean> mDataList = new ArrayList<CtrlRuleBean>();;
    FloatingActionsMenu menuMultipleActions;
    private DeviceBean device;
    private YunNodeModel mYunNodeModel;
    ArrayList<YunNodeModel> yModelsInFacility;
    
    private static final int MSG_CONTROLRULE_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_CONTROLRULE_END:
                    refresh.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        };
    };
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        device = ((DeviceBean) getActivity().getIntent().getSerializableExtra("devicebean"));//TODO 是否有值 ？？ TODO TODO TODO TODO
        mYunNodeModel = ((YunNodeModel) getActivity().getIntent().getSerializableExtra("yunnodemodel"));
        yModelsInFacility = ((ArrayList<YunNodeModel>) getActivity().getIntent().getSerializableExtra("yunnodemodels"));
        if (TextUtils.isEmpty(mYunNodeModel.mNodeUnique)){
            Toast.makeText(getActivity(), "数据出现问题mNodeUnique = null", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_farm_monitor_detail_ctrler_settings_fragment, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mListView = (ListView) view.findViewById(android.R.id.list);
        super.onViewCreated(view, savedInstanceState);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                new GetControlRuleThread().start();//TODO 暂时没有规则控制
            }
        });
        mAdapter = new ImplementAdapter(mDataList);
        mListView.setAdapter(mAdapter);
        mListView.setDivider(null);
        mListView.setDividerHeight(8);
        
        initFloatActionMenu(view);
//        new GetControlRuleThread().start();
    }
    
    // FloatingActionMenu 开源项目 圆形浮出菜单
    private void initFloatActionMenu(View root){
        menuMultipleActions = (FloatingActionsMenu) root.findViewById(android.R.id.button1);
        View view1 = root.findViewById(android.R.id.button2);
        View view2 = root.findViewById(android.R.id.button3);
        View view3 = root.findViewById(R.id.action4);
        view2.setVisibility(View.GONE);//暂时把定时控制隐藏
        view1.setOnClickListener(floatActionListener);
        view2.setOnClickListener(floatActionListener);
        view3.setOnClickListener(floatActionListener);
    }
    
    private View.OnClickListener floatActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case android.R.id.button2:
                	if (mYunNodeModel.devver == 3){
                		Intent intent = new Intent(getActivity(), ControlSetAct.class);
                        intent.putExtra("devicebeans", new ArrayList<DeviceBean>());//关联的传感器 TODO
                        intent.putExtra("isloopmode", false);
                        startActivity(intent);
                	} else {
                		Intent intent = new Intent(getActivity(), IntelliSetAct_v2.class);//二代触发控制
                        intent.putExtra("devicebean", device);
                        intent.putExtra("yunnodemodel", mYunNodeModel);
                        intent.putExtra("yunnodemodels", yModelsInFacility);//一个设施下所有的yunnode
                        startActivity(intent);
                	}
                    
                    menuMultipleActions.collapse();//收起FloatingActionsMenu
                    break;
                case android.R.id.button3:
                    Intent intent2 = new Intent(getActivity(), ControlSetAct.class);
                    intent2.putExtra("devicebean", device);//关联的传感器 TODO
                    intent2.putExtra("yunnodemodel", mYunNodeModel);
                    intent2.putExtra("isloopmode", true);
                    startActivity(intent2);
                    menuMultipleActions.collapse();
                    break;
                case R.id.action4:
                	Intent intent = new Intent(getActivity(), EditCtrRuleBase_v3.class);
                	intent.putExtra("yunnodemodel", mYunNodeModel);
                	startActivity(intent);
                	menuMultipleActions.collapse();
                	break;
                default:
                    break;
            }
        }
    };
    
//    private class GetControlRuleThread extends Thread{
//        @Override
//        public void run() {
//            List<ControlActionInfoModel> models = new ControlActionInfo().GetModelList(" UniqueID='"+ mYunNodeModel.mNodeUnique+"'" +
//            		" and ControlCondition like '%\"Dev\":" +device.mChannel + ",%'" );//TODO
//            mDataList.clear();
//            if (null != models){
//                for (int i = 0; i < models.size(); i ++){
//                    ControlActionInfoModel m = models.get(i);
//                    CtrlRuleBean bean = new CtrlRuleBean();
//                    String cmd = m._controlcondition;
//                    bean.ruleType = m._controltype;//15 or 16
//                    if (parseCtrRuleJSON(bean, cmd)){
//                        mDataList.add(bean);
//                    }
//                }
//                mHandler.sendEmptyMessage(MSG_CONTROLRULE_END);
//            }
//        }
//    }
    
    private boolean parseCtrRuleJSON(CtrlRuleBean bean, String str){
        try {
            if (bean.ruleType == 15){
            	JSONObject jObject = new JSONObject(str);
            	int cmdType = jObject.getInt("Type");
                JSONArray ja = jObject.getJSONArray("Data");
                switch (cmdType){
                    case 3:
                        for (int i = 0; i < ja.length(); i ++){
                            JSONObject jo = ja.optJSONObject(i);
                            int dev = jo.getInt("Dev");
                            if (dev == device.mChannel){
                                bean.startTime = jo.getLong("st");
//                                bean.endTime = jo.getLong("ent");
                                bean.workTime = jo.getInt("rt");
                                return true;
                            }
                        }
                        break;
                    case 4:
                        for (int i = 0; i < ja.length(); i ++){
                            JSONObject jo = ja.optJSONObject(i);
                            int dev = jo.getInt("Dev");
                            if (dev == device.mChannel){
                                bean.startTime = jo.getLong("st");
                                bean.endTime = jo.getLong("ent");
                                bean.workTime = jo.getInt("stt");
                                bean.spandTime = jo.getInt("rt");
                                return true;
                            }
                        }
                        break;
                    case 5:
                        for (int i = 0; i < ja.length(); i ++){
                            JSONObject jo = ja.optJSONObject(i);
                            int dev = jo.getInt("Dev");
                            if (dev == device.mChannel){
                                bean.startTime = jo.getLong("st");
                                bean.endTime = jo.getLong("ent");
                                bean.workTime = jo.getInt("stt");
                                bean.spandTime = jo.getInt("rt");
                                bean.redundant = jo.getInt("wt");
                                return true;
                            }
                        }
                        break;
                }
            } else if (bean.ruleType == 16){
            	JSONObject jObject = new JSONObject(str);
            	int cmdType = jObject.getInt("Type");
                JSONArray ja = jObject.getJSONArray("Data");
            } else if (bean.ruleType == 3){//二代定时
            	JSONArray jTimeArray = new JSONArray(str);
            	JSONObject jo = jTimeArray.optJSONObject(0);
            	bean.startTime = Utils.getMyTimestamp(jo.getString("StartTime")).getTime();
            	bean.endTime = Utils.getMyTimestamp(jo.getString("EndTime")).getTime();
            } else if (bean.ruleType == 5){//二代循环
            	JSONArray jTimeArray = new JSONArray(str);
            	JSONObject jo = jTimeArray.optJSONObject(0);
            	bean.startTime = Utils.getMyTimestamp(jo.getString("StartTime")).getTime();
            	bean.endTime = Utils.getMyTimestamp(jo.getString("EndTime")).getTime();
            	bean.workTime = jo.getInt("OnceRunTime");
            	bean.spandTime = jo.getInt("IntervalTime");//这个格式很多不对
            } else if (bean.ruleType == 6){//二代触发
            	JSONObject jo = new JSONObject(str);
            	bean.startTime = Utils.getMyTimestamp(jo.getString("StartTime")).getTime();
            	bean.endTime = Utils.getMyTimestamp(jo.getString("EndTime")).getTime();
            	bean.workTime = jo.getInt("OnceRunTime");
            	bean.spandTime = jo.getInt("IntervalTime");
            	
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private class ImplementAdapter extends BaseAdapter{
        List<CtrlRuleBean> mData;
        
        public ImplementAdapter(List<CtrlRuleBean> data){
            mData = data;
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
            ViewHolder holder;
            if (null == convertView){
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_farm_monitor_detail_ctrler_settings_list_item, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            CtrlRuleBean bean = mData.get(position);
            holder.time_text.setText(convertTimeStr(bean));
            Log.v(tag, "-----getView-----position:"+position +" reduandant:"+ bean.redundant);
            if (bean.ruleType == 15){//loop
                setUpTimeRule(holder, bean);
            } else if (bean.ruleType == 16){//触发
                //TODO
            }
            return convertView;
        }
        
        private String convertTimeStr(CtrlRuleBean b){
            String str;
            if (b.startTime > 3600 * 24 * 1000){
                Timestamp s = new Timestamp(b.startTime * 1000);
                Timestamp e = new Timestamp(b.endTime * 1000);
                str = s.toString().substring(0, 16) + "-\n" + e.toString().substring(0, 16);
            } else {
                str = "" + (int) b.startTime / 3600 + ":" + (int) (b.startTime % 3600) / 60 + "-"
                        +(int) b.endTime / 3600 + ":" + (int) (b.endTime % 3600) / 60;
            }
            return str;
        }
        
        private void setUpTimeRule(ViewHolder holder, CtrlRuleBean timeRuleBean){
            Log.v(tag, "--------------reduandant:" + timeRuleBean.redundant);
            holder.state_image.setImageResource(R.drawable.ic_contorl_timing_on);//ic_contorl_timing_off
            holder.continuation_time.setText(String.valueOf(timeRuleBean.workTime));
            holder.interval_time.setText(String.valueOf(timeRuleBean.spandTime));
            if (timeRuleBean.redundant > 0){
                holder.control_loop.setVisibility(View.VISIBLE);
                holder.control_loop.setText(timeRuleBean.getWeekdaysText(getResources()));
            } else {
                holder.control_loop.setVisibility(View.GONE);
            }
            holder.controlStart.setVisibility(View.GONE);
            holder.controlStop.setVisibility(View.GONE);
            
            holder.switch_state.setVisibility(View.GONE);
            holder.switch_state_text.setVisibility(View.GONE);
        }
        
        private void setUpTriggerRule(ViewHolder holder,CtrlRuleBean triRuleBean){
        	
        }
        
        private void setUpTimeRule_v2(ViewHolder holder, CtrlRuleBean ruleBean){//二代定时 循环
        	holder.state_image.setImageResource(R.drawable.ic_contorl_timing_on);
        	holder.continuation_time.setText(String.valueOf(ruleBean.workTime));
        }
        
        private void setUpTriggerRule_v2(ViewHolder holder, CtrlRuleBean ruleBean){
        	
        }
    }
    
    private final class ViewHolder {
        final TextView continuation_time;
        final TextView controlStart;
        final TextView controlStop;
        final TextView control_loop;
        final TextView interval_time;
        final ImageView state_image;
        final CheckBox switch_state;
        final TextView switch_state_text;
        final TextView time_text;

        ViewHolder(View v) {
            state_image = ((ImageView) v.findViewById(R.id.image_auto_mode));
            time_text = ((TextView) v.findViewById(R.id.time_bucket));
            switch_state_text = ((TextView) v.findViewById(R.id.switch_state_text));
            switch_state = ((CheckBox) v.findViewById(R.id.switch_state));
            continuation_time = ((TextView) v.findViewById(R.id.continuation_time));
            control_loop = ((TextView) v.findViewById(R.id.control_loop));
            interval_time = ((TextView) v.findViewById(R.id.interval_time));
            controlStart = ((TextView) v.findViewById(R.id.ControlStart));
            controlStop = ((TextView) v.findViewById(R.id.ControlStop));
            v.setTag(this);
        }
        
        private CharSequence buildText(DeviceBean paramDevice, String paramString1, String paramString2, String paramString3) {
//          FarmDetailCtrlerSettingsListFragment.this.deviceTypeParams.setType(paramDevice.getDeviceType());
          StringBuilder localStringBuilder = new StringBuilder();
//          localStringBuilder.append("当").append(paramDevice.mName).append("的")
//              .append(FarmDetailCtrlerSettingsListFragment.this.deviceTypeParams.getDeviceTypeUnitName())
//              .append(paramString1).append(' ').append(paramString2)
//              .append(FarmDetailCtrlerSettingsListFragment.this.deviceTypeParams.getUnit(paramDevice.getDeviceType(), false))
//              .append("时").append(paramString3);
          return localStringBuilder;
        }
      
    }
}