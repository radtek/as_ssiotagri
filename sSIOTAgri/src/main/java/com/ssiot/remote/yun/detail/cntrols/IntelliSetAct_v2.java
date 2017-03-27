package com.ssiot.remote.yun.detail.cntrols;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.NodeModel;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.webapi.WS_API;
import com.ssiot.remote.yun.webapi.WS_TraceProject;

public class IntelliSetAct_v2 extends HeadActivity{
	private static final String tag = "IntelliSetAct_v2";
	
	DeviceBean deviceBean;
	YunNodeModel yModel;
	ArrayList<YunNodeModel> yModelsInFacility;
	
	Button mNextBtn;
	
	Spinner mSensorSpinner;
	
	private List<NodeModel> n2mList = new ArrayList<NodeModel>();
	private ListView mNodeListView;
	NodeListCheckAdapter mNodeAdapter;
	
	List<DeviceBean> mSVModels = new ArrayList<DeviceBean>();
	
	
	private String selectednodesStr = "";
    private int mSelectedIntervalTime = 5;//minutes
    private int mSelectedWorkTime = 5;
    private int mSelectedRelationType = -1;//同时满足条件，满足其中之一
    
    private final String[] typeDatas = {"同时满足条件","满足其中之一"};
    public static final String[] spinnerDatas = {"5分钟","10分钟","15分钟","20分钟","25分钟","30分钟","35分钟","40分钟","45分钟","50分钟","55分钟"};
    
    private TriRuleAdapter mElementAdapter;
    private ArrayList<TriRuleElementBean> mElementDatas = new ArrayList<TriRuleElementBean>();
    
    ArrayList<String> mSensorDatas = null;//null 表示还没开始获取进程结果
    private final String[] maxMinDatas = {"大于","小于","范围内"};
    
    private static final int MSG_GETNODES_END = 0;
    private static final int MSG_GETCOMSENSORS_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GETNODES_END:
                    mNodeAdapter.notifyDataSetChanged();
                    break;
                case MSG_GETCOMSENSORS_END:
                    if (mSensorDatas != null && null != mSensorSpinner){
                        ArrayAdapter<String> arr_adapter = new ArrayAdapter<String>(IntelliSetAct_v2.this,android.R.layout.simple_spinner_item,
                                mSensorDatas);
                        mSensorSpinner.setAdapter(arr_adapter);
                    }
                    break;

                default:
                    break;
            }
        };
    };
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceBean = (DeviceBean) getIntent().getSerializableExtra("devicebean");
		yModel = (YunNodeModel) getIntent().getSerializableExtra("yunnodemodel");
		yModelsInFacility = (ArrayList<YunNodeModel>) getIntent().getSerializableExtra("yunnodemodels");
		setContentView(R.layout.dia_ctr_trigger);
		
		new Thread(new Runnable() {//获取所有节点
            @Override
            public void run() {
            	String account = Utils.getStrPref(Utils.PREF_USERNAME, IntelliSetAct_v2.this);
                List<NodeModel> tmpList = new WS_TraceProject().GetAllNodes(account);
                n2mList.clear();
                n2mList.addAll(tmpList);
                mHandler.sendEmptyMessage(MSG_GETNODES_END);
            }
        }).start();
		
		mNodeAdapter = new NodeListCheckAdapter(this, n2mList);
		initViews();
	}
	
	public void ClickFunc(View v){
		Builder builder = new AlertDialog.Builder(this);
		ListView nodelistView = new ListView(this);
		nodelistView.setAdapter(mNodeAdapter);
		builder.setView(nodelistView);
		builder.setTitle("选择触发源节点").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setSelectedNodesAndRefreshSensorData();
				TextView t = (TextView) findViewById(R.id.txt_nodes);
            	t.setText(selectednodesStr);
			}
		});
		builder.create().show();
	}
	
	public void ShowIntervalPickDia(View v){
		Builder bui = new AlertDialog.Builder(this);
		bui.setSingleChoiceItems(spinnerDatas, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	Log.v(tag, "-----ShowIntervalPickDia--pick-which:" + which);
            	mSelectedIntervalTime = (1 + which) * 5;
            	TextView t = (TextView) findViewById(R.id.txt_interval);
            	t.setText(spinnerDatas[which]);
            }
        }).setTitle("间隔时间选择");
		bui.create().show();
	}
	
	public void ShowWorkTimePickDia(View v){
		Builder bui = new AlertDialog.Builder(this);
		bui.setSingleChoiceItems(spinnerDatas, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	Log.v(tag, "-----ShowWorkTimePickDia--pick-which:" + which);
            	mSelectedWorkTime = (1 + which) * 5;//minutes
            	TextView t = (TextView) findViewById(R.id.txt_work);
            	t.setText(spinnerDatas[which]);
            }
        }).setTitle("运行时间选择").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		bui.create().show();
	}
	
	public void ShowRelationPickDia(View v){
		Builder bui = new AlertDialog.Builder(this);
		bui.setSingleChoiceItems(typeDatas, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	Log.v(tag, "-----ShowRelationPickDia--pick-which:" + which);
            	mSelectedRelationType = which;
            	TextView t = (TextView) findViewById(R.id.txt_relation);
            	t.setText(typeDatas[which]);
            }
        }).setTitle("关系选择").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		bui.create().show();
	}
	
	private void initViews(){
//		initFirstPage();
//		initSecondPage();
		
		LinearLayout mTriRuleTitle = (LinearLayout) findViewById(R.id.tri_rule_title);
		ListView mElementList = (ListView) findViewById(R.id.tri_element_list);
		Button mFinishBtn = (Button) findViewById(R.id.tri_finish);
		
		int titlecolor = getResources().getColor(R.color.blue_2);
        mTriRuleTitle.findViewById(R.id.ele_name).setBackgroundColor(titlecolor);
        mTriRuleTitle.findViewById(R.id.ele_type).setBackgroundColor(titlecolor);
        mTriRuleTitle.findViewById(R.id.ele_num).setBackgroundColor(titlecolor);
        final ImageButton addBtn = (ImageButton) mTriRuleTitle.findViewById(R.id.tri_btn);
        addBtn.setBackgroundColor(titlecolor);
        addBtn.setImageResource(R.drawable.tri_rule_add);
//        final View anchorView = rootView;
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPopup(addBtn);
            }
        });
        
        mElementAdapter = new TriRuleAdapter(this,mElementDatas);
        mElementList.setAdapter(mElementAdapter);
        
        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedWorkTime >= mSelectedIntervalTime || TextUtils.isEmpty(selectednodesStr) || mSelectedRelationType < 0
                		|| null == mElementDatas || mElementDatas.size() == 0){
                	showToast("运行时间必须小于触发间隔,关系选择不能为空。阈值设置不能为空");
                    return;
                }
                final String condition = buildTriJSON(mSelectedIntervalTime,mSelectedWorkTime,typeDatas[mSelectedRelationType], mElementDatas);
                Log.v(tag, "----condition---"+ condition);
                final String nodeUnique = yModel.mNodeUnique;
                new Thread(new Runnable() {
					@Override
					public void run() {
						int ret = new WS_API().CtrRule_v2(nodeUnique, deviceBean.mChannel, 3, selectednodesStr,condition);
						sendToast(ret > 0 ? "成功" : "失败");
						if (ret > 0){
							finish();
						}
					}
				}).start();
            }
        });
	}
	
//	private void initFirstPage(){
//        mNodeListView = (ListView) findViewById(R.id.tri_node_list);
//        mNodeAdapter = new NodeListCheckAdapter(this, n2mList);
//        mNodeListView.setAdapter(mNodeAdapter);
//        mNextBtn = (Button) findViewById(R.id.tri_next);
//        final RelativeLayout part1 = (RelativeLayout) findViewById(R.id.part1);
//        final RelativeLayout part2 = (RelativeLayout) findViewById(R.id.part2);
//        
//        mNextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            	setSelectedNodesAndRefreshSensorData();
//            	part1.setVisibility(View.GONE);
//                part2.setVisibility(View.VISIBLE);
//            }
//        });
//    }
	
	private void setSelectedNodesAndRefreshSensorData(){
		selectednodesStr = "";
        ArrayList<NodeModel> checkedList = mNodeAdapter.getCheckedList();
        if (null == checkedList || checkedList.size() <= 0){
        	showToast("未选择节点");
            return;
        }
        for (int i = 0; i < checkedList.size(); i ++){
        	selectednodesStr += checkedList.get(i)._uniqueid + ",";
        }
        
        if (null != mSensorDatas){
            mSensorDatas.clear();//每次都需要清除一下
            mSensorDatas = null;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
            	for (int i = 0; i < yModelsInFacility.size(); i ++){
            		YunNodeModel y = yModelsInFacility.get(i);
            		if (y.nodeType == DeviceBean.TYPE_SENSOR){
            			for (int j = 0; j < y.list.size(); j ++){
            				DeviceBean tmpDevice = y.list.get(j);
            				if (!existSensorViewModel(tmpDevice.mDeviceTypeNo, tmpDevice.mChannel, mSVModels)){
            					mSVModels.add(tmpDevice);//获取一个抽象的传感器列表 此设计垃圾
            				}
            			}
            		}
            	}
                if (mSensorDatas == null){//null 代表获取进程没结束
                    mSensorDatas = new ArrayList<String>();
                }
                if (null != mSVModels){
                    for (int i = 0; i < mSVModels.size(); i ++){
                        mSensorDatas.add(mSVModels.get(i).mName + mSVModels.get(i).mChannel);
                    }
                }
                mHandler.sendEmptyMessage(MSG_GETCOMSENSORS_END);
            }
        }).start();
        
	}
	
	private boolean existSensorViewModel(int deviceType, int channel, List<DeviceBean> svms){
		for (int i = 0; i < svms.size(); i ++){
			if (deviceType == svms.get(i).mDeviceTypeNo && channel == svms.get(i).mChannel){
				return true;
			}
		}
		return false;
	}
	
	private void initSecondPage(){
//		Spinner mIntervalSpinner = (Spinner) findViewById(R.id.tri_interval_time);
//		Spinner mWorkTimeSpinner = (Spinner) findViewById(R.id.tri_working_time);
//		Spinner mRelationTypeSpinner = (Spinner) findViewById(R.id.tri_relation_type);
		LinearLayout mTriRuleTitle = (LinearLayout) findViewById(R.id.tri_rule_title);
		ListView mElementList = (ListView) findViewById(R.id.tri_element_list);
		Button mFinishBtn = (Button) findViewById(R.id.tri_finish);
        
//        ArrayAdapter<String> arr_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,ControlDetailHolderFrag.spinnerDatas);
//        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mIntervalSpinner.setAdapter(arr_adapter);
//        mIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mSelectedIntervalTime = (1 + position) * 5;//minutes
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
        //运行时间必须小于触发间隔 
//        mWorkTimeSpinner.setAdapter(arr_adapter);
//        mWorkTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mSelectedWorkTime = (1 + position) * 5;//minutes
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,typeDatas);
//        mRelationTypeSpinner.setAdapter(typeAdapter);
//        mRelationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mSelectedRelationType = position;//minutes
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
        
        int titlecolor = getResources().getColor(R.color.ssiotgreen);
        mTriRuleTitle.findViewById(R.id.ele_name).setBackgroundColor(titlecolor);
        mTriRuleTitle.findViewById(R.id.ele_type).setBackgroundColor(titlecolor);
        mTriRuleTitle.findViewById(R.id.ele_num).setBackgroundColor(titlecolor);
        final ImageButton addBtn = (ImageButton) mTriRuleTitle.findViewById(R.id.tri_btn);
        addBtn.setBackgroundColor(titlecolor);
        addBtn.setImageResource(R.drawable.tri_rule_add);
//        final View anchorView = rootView;
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPopup(addBtn);
            }
        });
        
        mElementAdapter = new TriRuleAdapter(this,mElementDatas);
        mElementList.setAdapter(mElementAdapter);
        
        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedWorkTime >= mSelectedIntervalTime){
                	showToast("运行时间必须小于触发间隔");
                    return;
                }
                final String condition = buildTriJSON(mSelectedIntervalTime,mSelectedWorkTime,typeDatas[mSelectedRelationType], mElementDatas);
                Log.v(tag, ""+ condition);
                final String nodeUnique = yModel.mNodeUnique;
                new Thread(new Runnable() {
					@Override
					public void run() {
						int ret = new WS_API().CtrRule_v2(nodeUnique, deviceBean.mChannel, 3, "collectuniqueids",condition);
						sendToast(ret > 0 ? "成功" : "失败");
					}
				}).start();
            }
        });
    }
	
	private void showAddPopup(View anchor){
        View popView = LayoutInflater.from(this).inflate(R.layout.tri_add_popup, null);
        
        mSensorSpinner = (Spinner) popView.findViewById(R.id.tri_pop_sensor_spinner);
        final EditText numEdit = (EditText) popView.findViewById(R.id.tri_maxmin_value_edit);
        final EditText numEdit2 = (EditText) popView.findViewById(R.id.tri_maxmin_value_edit_2);
        if (null != mSensorDatas){
            ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mSensorDatas);
            mSensorSpinner.setAdapter(sensorAdapter);
        } else {
            String[] pleaseWait = {"正在查找传感器"};
            ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,pleaseWait);
            mSensorSpinner.setAdapter(sensorAdapter);
        }
        
        final Spinner mMaxMinSpinner = (Spinner) popView.findViewById(R.id.tri_pop_maxmin_spinner);
        ArrayAdapter<String> maxminAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,maxMinDatas);
        mMaxMinSpinner.setAdapter(maxminAdapter);
        mMaxMinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 2){ 
                    numEdit2.setVisibility(View.VISIBLE);
                } else {
                    numEdit2.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        
        final PopupWindow popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        
        Button tri_pop_add = (Button) popView.findViewById(R.id.tri_pop_add);
        tri_pop_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceBean sModel = mSVModels.get(mSensorSpinner.getSelectedItemPosition());
                int maxMinPosition = mMaxMinSpinner.getSelectedItemPosition();
                String type = maxMinDatas[maxMinPosition];
                String value = numEdit.getText().toString();
                if (TextUtils.isEmpty(value)){
                	showToast("数值不能为空");
                    return;
                }
                String value2 = "";
                if (maxMinPosition == 2){
                    value2 = numEdit2.getText().toString();
                    if (TextUtils.isEmpty(value2)){
                    	showToast("数值不能为空");
                        return;
                    }
                    if (Float.parseFloat(value) >= Float.parseFloat(value2)){
                    	showToast("最小值不能大于最大值数值不能为空");
                        return;
                    }
                    value2 = "," + value2;
                }
                mElementDatas.add(new TriRuleElementBean(sModel.mName + sModel.mChannel, sModel.mDeviceTypeNo + "-" + sModel.mChannel, type, value + value2));
                mElementAdapter.notifyDataSetChanged();
                if (popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });
        
        popView.findViewById(R.id.tri_pop_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });
        
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_card_normal));
//        popupWindow.showAsDropDown(anchor);
        popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0);
    }
	
	private String buildTriJSON(int intervalMinutes, int runMinutes, String relationStr, ArrayList<TriRuleElementBean> elements){
        JSONObject jsRet = new JSONObject();
        
        JSONArray triDataArr = new JSONArray();
        for (int i = 0; i < elements.size(); i ++){
            JSONObject jo = new JSONObject();
            try {
                jo.put("ID", ""+(i +1));
                jo.put("Element", elements.get(i).sensorValue);
                jo.put("Type", elements.get(i).type);
                jo.put("Param", elements.get(i).valueNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            triDataArr.put(jo);
        }
        try {
            jsRet.put("MinInterval", ""+intervalMinutes);
            jsRet.put("Relation", relationStr);
            jsRet.put("RunTime", ""+runMinutes);
            jsRet.put("TriggerData", triDataArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsRet.toString();
    }
	
	
	
	
	
	
	public class NodeListCheckAdapter extends BaseAdapter{
        List<NodeModel> mDatas;
        private ArrayList<NodeModel> checkedList = new ArrayList<NodeModel>();
        private LayoutInflater mInflater;

        public NodeListCheckAdapter(Context c, List<NodeModel> n2ms){
            mDatas = n2ms;
            mInflater = LayoutInflater.from(c);
        }
        
        public ArrayList<NodeModel> getCheckedList(){
            return checkedList;
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
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.tri_check_item, null);
                holder.mTextView = (TextView) convertView.findViewById(R.id.tri_node_name);
                holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.tri_node_check);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final NodeModel m = mDatas.get(position);
            holder.mTextView.setText(m._nodeno + m._location);
            holder.mCheckBox.setChecked(checkedList.contains(m));
            final int positionFinal = position;
            holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        if (!checkedList.contains(m)){
                            checkedList.add(m);
                        }
                    } else {
                        if (checkedList.contains(m)){
                            checkedList.remove(m);
                        }
                    }
                }
            });
            return convertView;
        }
        
        private class ViewHolder{
            TextView mTextView;
            CheckBox mCheckBox;
        }
    }
	
	
	public class TriRuleAdapter extends BaseAdapter{
        private ArrayList<TriRuleElementBean> mDatas;
        private LayoutInflater mInflater;
        
        public TriRuleAdapter(Context c,ArrayList<TriRuleElementBean> d){
            mDatas = d;
            mInflater = LayoutInflater.from(c);
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
            ViewHold hold;
            if (null == convertView){
                hold = new ViewHold();
                convertView = mInflater.inflate(R.layout.tri_addrule_item, null);
                hold.sensorNameView = (TextView) convertView.findViewById(R.id.ele_name);
                hold.typeView = (TextView) convertView.findViewById(R.id.ele_type);
                hold.numView = (TextView) convertView.findViewById(R.id.ele_num);
                hold.mBtn = (ImageButton) convertView.findViewById(R.id.tri_btn);
                convertView.setTag(hold);
            } else {
                hold = (ViewHold) convertView.getTag();
            }
            final TriRuleElementBean bean = mDatas.get(position);
            hold.sensorNameView.setText(bean.sensorName);
            hold.typeView.setText(bean.type);
            hold.numView.setText(bean.valueNumber);
            hold.mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatas.remove(bean);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
        
        private class ViewHold {
            public TextView sensorNameView;
            public TextView typeView;
            public TextView numView;
            private ImageButton mBtn;
        }
    }
    
    public class TriRuleElementBean{// extends NodeView2Model{
        private String sensorName = "";
        private String sensorValue = "";
        private String type = "";//大于 小于 之间
        private String valueNumber = "";
        
        
        public TriRuleElementBean(String sensorName, String sensorValue,String type, String valueNumber){
            this.sensorName = sensorName;
            this.sensorValue = sensorValue;
            this.type = type;
            this.valueNumber = valueNumber;
        }
    }
}