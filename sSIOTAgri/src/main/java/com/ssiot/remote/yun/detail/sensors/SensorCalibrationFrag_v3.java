package com.ssiot.remote.yun.detail.sensors;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.remote.BaseFragment;
import com.ssiot.agri.R;
import com.ssiot.remote.data.model.SettingModel;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.webapi.WS_API;
import com.ssiot.remote.yun.webapi.WS_MQTT;

//校准frag 三代产品
public class SensorCalibrationFrag_v3 extends BaseFragment{
    YunNodeModel mYunNodeModel;
    DeviceBean mDeviceBean;
    
    ListView mListView;
    PointAdapter mAdapter;
    private List<MyPoint> mPoints = new ArrayList<MyPoint>();
    
    Button pointAdd;
    Button pointSend;
    
    String address = "";
    String caliTxt = "";
    
    private static final int MSG_GET_END = 1;
    private static final int MSG_SEND_END = 2;
    private static final int MSG_MQTT_GET = WS_MQTT.MSG_MQTT_GET;
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                	if (null != mAdapter){
                		mAdapter.notifyDataSetChanged();
                	}
                    break;
                case MSG_SEND_END:
                    boolean result = (Boolean) msg.obj;
                    Toast.makeText(getActivity(), result ? "保存成功" : "保存失败", Toast.LENGTH_SHORT).show();
                    if (result){
                        new GetExistJiaoZhunThread().start();
                    }
                    break;
                case MSG_MQTT_GET:
                	String str = (String) msg.obj;
//					try {
//						String topic = str.substring(0,str.indexOf("###"));
//						String mqttmsg = str.substring(str.indexOf("###") + 3, str.length());
//						JSONObject jo = new JSONObject(mqttmsg);
//						if (jo.getInt("status") == 0){
////							showToastMSG("校准保存成功");
//							new Thread(new Runnable() {
//								@Override
//								public void run() {
//									boolean b = new WS_API().SetCali_v3(mYunNodeModel.mNodeNo, mDeviceBean.mDeviceTypeNo, mDeviceBean.mChannel, address, caliTxt) > 0;
//						            Message m = mHandler.obtainMessage(MSG_SEND_END);
//						            m.obj = b;
//						            mHandler.sendMessage(m);
//								}
//							}).start();
//						} else {
//							showToastMSG("校准保存至设备失败");
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//						showToastMSG("校准保存返回值" + str);
//					}
                	showToastMSG(str);
                	break;

                default:
                    break;
            }
        };
    };
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle b = getArguments();
        mYunNodeModel = (YunNodeModel) b.getSerializable("yunnodemodel");
        mDeviceBean = (DeviceBean) b.getSerializable("devicebean");
        if (null == mYunNodeModel || null == mDeviceBean){
            Toast.makeText(getActivity(), "参数错误", Toast.LENGTH_SHORT).show();
        }
        super.onActivityCreated(savedInstanceState);
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_farm_monitor_detail_sensor_cali_fragment_v3, container, false);
        initViews(rootView);
        return rootView;
    }
    
    private void initViews(View rootView){
    	mListView = (ListView) rootView.findViewById(R.id.point_list);
    	mAdapter = new PointAdapter(mPoints);
    	mListView.setAdapter(mAdapter);
    	
    	pointAdd = (Button) rootView.findViewById(R.id.point_add);
    	pointAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPoints.add(new MyPoint());
				mAdapter.notifyDataSetChanged();
			}
		});
    	
    	pointSend = (Button) rootView.findViewById(R.id.cali_send);
    	pointSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new SendbdThread().start();
			}
		});
        new GetExistJiaoZhunThread().start();
    }
    
    private class GetExistJiaoZhunThread extends Thread{
        @Override
        public void run() {
            if (null == mYunNodeModel || null == mDeviceBean){
                return;
            }
            String caliText = new WS_API().GetCali_v3(""+mYunNodeModel.mNodeNo, mDeviceBean.mDeviceTypeNo, mDeviceBean.mChannel);
            if (!TextUtils.isEmpty(caliText)){
            	mPoints.clear();
            	mPoints.addAll(parsePoints(caliText));
            	mHandler.sendEmptyMessage(MSG_GET_END);
            }
        }
    }
    
    private List<MyPoint> parsePoints(String str){
    	List<MyPoint> ps = new ArrayList<MyPoint>();
    	try {
			JSONObject jo = new JSONObject(str);
			JSONArray ja = jo.getJSONArray("cali");
			for (int i = 0; i< ja.length(); i ++){
				ps.add(new MyPoint(ja.getJSONObject(i).getDouble("x"), ja.getJSONObject(i).getDouble("y")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return ps;
    }
    
    private class SendbdThread extends Thread{
        @Override
        public void run() {
        	if (mPoints.size() < 2){
        		showToastMSG("至少设置两个点");
        		return;
        	}
            address = new WS_API().getSensorAddress(mYunNodeModel.mNodeNo, mDeviceBean.mDeviceTypeNo, mDeviceBean.mChannel);
            JSONObject caliJSON = new JSONObject();
            JSONArray pointArray = new JSONArray();
            try {
	            for (int i = 0; i < mPoints.size(); i ++){
	            	MyPoint p = mPoints.get(i);
	            	JSONObject jo = new JSONObject();
					jo.put("x", p.x);
					jo.put("y", p.y);
					pointArray.put(jo);
	            }
	            caliJSON.put("addr", address);
	            caliJSON.put("cali", pointArray);
            } catch (JSONException e) {
				e.printStackTrace();
			}
//            caliTxt = caliJSON.toString();
            caliTxt = pointArray.toString();
            
            int ret =new WS_MQTT().SetNodeCalibration(mYunNodeModel.mNodeUnique, mDeviceBean.mDeviceTypeNo, mDeviceBean.mChannel, caliTxt);
            Message m = mHandler.obtainMessage(MSG_MQTT_GET);
        	m.obj = (ret >= 0 ? "操作成功" : "操作失败");
        	mHandler.sendMessage(m);
//            new MQTT().subMsg("v1/n/" + mYunNodeModel.mNodeUnique + "/cali/ack", mHandler);
//            new MQTT().pubMsg("v1/n/" + mYunNodeModel.mNodeUnique + "/cali", caliTxt);
            //通过hander接受到之后再保存到数据库 mqtt中间件做好了
            
        }
    }
    
    private class PointAdapter extends BaseAdapter{
    	private List<MyPoint> points;
    	private LayoutInflater mInflater;
    	public PointAdapter(List<MyPoint> pos){
    		points = pos;
    		mInflater = LayoutInflater.from(getActivity());
    	}
    	
		@Override
		public int getCount() {
			return points.size();
		}

		@Override
		public Object getItem(int position) {
			return points.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.point_edit, null, false);
			EditText xEdit = (EditText) convertView.findViewById(R.id.xpoint);
			EditText yEdit = (EditText) convertView.findViewById(R.id.ypoint);
			Button delete = (Button) convertView.findViewById(R.id.point_delete);
			MyPoint point = points.get(position);
			xEdit.setText(""+point.x);
			yEdit.setText(""+point.y);
			xEdit.addTextChangedListener(new TxtWatcher(point, true));
			yEdit.addTextChangedListener(new TxtWatcher(point, false));
			delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mPoints.remove(position);
					mAdapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}
    }
    
    public class TxtWatcher implements TextWatcher{
    	MyPoint point;
    	private boolean isx = false;
    	private TxtWatcher (MyPoint po, boolean isxx){
    		point = po;
    		isx = isxx;
    	}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			float f = 0;
			try {
				if (!TextUtils.isEmpty(s.toString())){
					f = Float.parseFloat(s.toString());
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			if (isx){
				point.x = f;
			} else {
				point.y = f;
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			
		}
    }
    
    private class MyPoint{
    	private boolean init = false;
    	float x;
    	float y;
    	
    	public MyPoint(){
    		init = false;
    	}
    	
    	public MyPoint(float xx, float yy){
    		x = xx;
    		y = yy;
    		init = true;
    	}
    	
    	public MyPoint(double xx, double yy){
    		x = (float) xx;
    		y = (float) yy;
    		init = true;
    	}
    }
    
    
    
}