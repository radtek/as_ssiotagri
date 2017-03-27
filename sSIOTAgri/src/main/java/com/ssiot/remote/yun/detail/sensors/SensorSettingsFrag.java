package com.ssiot.remote.yun.detail.sensors;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssiot.remote.BaseFragment;
import com.ssiot.agri.R;
import com.ssiot.remote.data.model.AlarmRuleBean;
import com.ssiot.remote.data.model.AlarmRuleModel;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.webapi.WS_API;

import net.i2p.android.ext.floatingactionbutton.AddFloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//预警frag
public class SensorSettingsFrag extends BaseFragment{
	YunNodeModel yModel = null;
	DeviceBean deviceBean = null;
	AlarmRuleModel alarmModel;
	
	EditText maxEdit;
	EditText minEdit;

	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
		};
	};
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		yModel = (YunNodeModel) getArguments().getSerializable("yunnodemodel");
		deviceBean = (DeviceBean) getArguments().getSerializable("devicebean");
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_farm_monitor_detail_sensor_settings_fragment_2, container, false);
        initViews_2(rootView);
        return rootView;
    }
    
    private void initViews_2(View v){
    	maxEdit = (EditText) v.findViewById(R.id.maxEdit);
    	minEdit = (EditText) v.findViewById(R.id.minEdit);
    	final Button saveBtn = (Button) v.findViewById(R.id.saveBtn);
    	saveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAlarmRule();
			}
		});
    	if (null != yModel && null != deviceBean){
    		new Thread(new Runnable() {
				@Override
				public void run() {
					alarmModel = new WS_API().GetAlarmRules_v2(yModel.mNodeUnique);
					saveBtn.post(new Runnable() {
						@Override
						public void run() {
							if (null != alarmModel){
								List<AlarmRuleBean> list = alarmModel.parseAlarmJSON(alarmModel._ruleStr);
								if (null != list){
									for (AlarmRuleBean b : list){
										if (deviceBean.mDeviceTypeNo == b.sensorType && deviceBean.mChannel == b.channel){
											if ("大于".equals(b.type)){
												maxEdit.setText(""+b.value);
											} else if ("小于".equals(b.type)){
												minEdit.setText(""+b.value);
											}
										}
									}
								}
							}
						}
					});
				}
			}).start();
    	}
    }
    
    private void saveAlarmRule(){
    	JSONArray jArray = new JSONArray();
    	if (null == alarmModel){
			alarmModel = new AlarmRuleModel();
			alarmModel._id = 0;
			alarmModel._uniqueID = yModel.mNodeUnique;
			alarmModel._relation = "满足其中之一";//TODO
		} else {
			try {
				JSONArray oriArray = new JSONArray(alarmModel._ruleStr);
				for (int i = 0; i < oriArray.length(); i ++){
					if (deviceBean.mDeviceTypeNo == oriArray.optJSONObject(i).getInt("sensorno") 
							&& deviceBean.mChannel == oriArray.optJSONObject(i).getInt("channel")){
						continue;
					}
					jArray.put(oriArray.optJSONObject(i));//同节点下的其他传感器的配置。
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		String maxStr = maxEdit.getText().toString();
		String minStr = minEdit.getText().toString();
		
		if (!TextUtils.isEmpty(maxStr)){
			float fMax = Float.parseFloat(maxStr);
			JSONObject jo = new JSONObject();
			try {
				jo.put("sensorno", deviceBean.mDeviceTypeNo);
				jo.put("channel", deviceBean.mChannel);
				jo.put("sensorname", deviceBean.mName);
				jo.put("type", "大于");
				jo.put("value", fMax);
				jArray.put(jo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (!TextUtils.isEmpty(minStr)){
			float fMin = Float.parseFloat(minStr);
			JSONObject jo = new JSONObject();
			try {
				jo.put("sensorno", deviceBean.mDeviceTypeNo);
				jo.put("channel", deviceBean.mChannel);
				jo.put("sensorname", deviceBean.mName);
				jo.put("type", "小于");
				jo.put("value", fMin);
				jArray.put(jo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		alarmModel._ruleStr = jArray.toString();//TODO
		new Thread(new Runnable() {
			@Override
			public void run() {
				int ret = new WS_API().SaveAlarmRule(alarmModel);
				showToastMSG(ret >= 0 ? "保存成功" : "保存失败");
			}
		}).start();
		
    }
    
    //-----------------------------------------------------
    
    private void initViews(View v){
        AddFloatingActionButton m = (AddFloatingActionButton) v.findViewById(android.R.id.button1);
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlarmRuleSetAct.class);
                startActivity(intent);
            }
        });
    }
    
    private final class ImplementationAdapter extends BaseAdapter{
        ArrayList<AlarmRuleModel> items;
        Context mContext;
        
        public ImplementationAdapter(Context c){
            mContext = c;
        }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_farm_monitor_detail_sensor_settings_item, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        }
        
        private final class ViewHolder {
            CheckBox enabled;
            ImageView iv;
            TextView sensorWarnEnabledText;
            TextView setPropertyValue;
            TextView setTime;

            ViewHolder(View v) {
                setPropertyValue = ((TextView) v.findViewById(2131296615));
                setTime = ((TextView) v.findViewById(2131296616));
                iv = ((ImageView) v.findViewById(2131296614));
                enabled = ((CheckBox) v.findViewById(2131296433));
                sensorWarnEnabledText = ((TextView) v.findViewById(2131296430));
                v.setTag(this);
            }
        }
        
    }
}