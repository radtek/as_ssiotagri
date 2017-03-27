package com.ssiot.remote.expert;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ManualAnalysisModel;
import com.ssiot.remote.data.model.ManualDataModel;
import com.ssiot.remote.data.model.SensorModel;
import com.ssiot.remote.yun.webapi.WS_Fish;

public class ManualDataEditAct extends HeadActivity{
	private static final String tag = "ManualDataEditAct";
	int facilityid = -1;
	ListView mListView;
	EditText mAnalysisEdit;
	ManualDataListAdapter mAdapter;
	List<ManualDataModel> datas = new ArrayList<ManualDataModel>();
	Context mContext;
	Timestamp mTimestamp;
	TextView titleRight;
	View footerView;
	
	List<SensorModel> mAllSensorsModel = new ArrayList<SensorModel>();
	
	private static final int MSG_ADD_END = 1;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ADD_END:
				finish();
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideActionBar();
		setContentView(R.layout.act_manualdata_edit);
		mContext = this;
		facilityid = getIntent().getIntExtra("facilityid", -1);
		mTimestamp = (Timestamp) getIntent().getSerializableExtra("time");//是否可序列化
		if (null == mTimestamp){//新建一个
			mTimestamp = new Timestamp(System.currentTimeMillis());
			mTimestamp = formatTimeToHour(mTimestamp);//整小时
		}
		initViews();
		
		initTitleBar();
		new GetAllManualSensorsThread().start();
		new GetEnableThread().start();//距离上次太近则不能建立
	}
	
	private void initViews(){
		mAnalysisEdit = (EditText) findViewById(R.id.analysis_edit);
		mListView = (ListView) findViewById(R.id.data_list);
		mAdapter = new ManualDataListAdapter(datas);
		final View footView = LayoutInflater.from(this).inflate(R.layout.footer_add, null, false);
		footerView = footView;
		footerView.setEnabled(false);
		footView.findViewById(R.id.manualdata_add).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				showSensorPickDia();
//				showAddPopup(footerView);
				showPop(footView);
			}
		});
		mListView.addFooterView(footerView);
		mListView.setAdapter(mAdapter);
	}
	
	private Timestamp formatTimeToHour(Timestamp time){
		return new Timestamp(time.getYear(), time.getMonth(), time.getDate(), time.getHours(), 0, 0, 0);
	}
	
	private void initTitleBar(){
        titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setEnabled(false);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	final ManualAnalysisModel m = new ManualAnalysisModel();
				m._collectiontime = mTimestamp;
				m._facilityid = facilityid;
				m._facilitytype = "渔业";
				m._analysis = mAnalysisEdit.getText().toString();
				if (m._facilityid > 0 && !TextUtils.isEmpty(m._analysis) && datas.size() > 0){
					new Thread(new Runnable() {
						@Override
						public void run() {
							int ret = new WS_Fish().SaveWaterManualAnalysis(m);
							if (ret > 0){
								mHandler.sendEmptyMessage(MSG_ADD_END);
							}
						}
	            		
	            	}).start();
				} else {
					showToast("请填写完整");
				}
            	
//                model._id = 0;
//                model._inputsinid = 0;//
//                model._userid = userId;
//                if (!TextUtils.isEmpty(model._name) && model._croptype > 0 && null != mTimeStamp1 && null != mTimeStamp2 && 
//                		!TextUtils.isEmpty(model._facilityids)){
//                    new Thread(new Runnable() {
//                        public void run() {
//                            int ret = new ProductBatch().Save(model);
//                            if (ret > 0){
//                                mHandler.sendEmptyMessage(MSG_ADD_END);
//                            }
//                        }
//                    }).start();
//                } else {
//                    Toast.makeText(mContext, "请填写完整", Toast.LENGTH_SHORT).show();
//                }
            	
            }
        });
        TextView titleLeft = (TextView) findViewById(R.id.title_bar_left);
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}
	
//	private void showSensorPickDia(){
//		SensorModel smodel;
//		final String[] types = new String[mAllSensorsModel.size()];
//        for (int i = 0; i < mAllSensorsModel.size(); i ++){
//            types[i] = mAllSensorsModel.get(i)._sensorname;
//        }
//		AlertDialog.Builder bui = new AlertDialog.Builder(this);
//        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////            	smodel = mAllSensorsModel.get(which);
//            }
//        });
//        bui.setTitle("选择一个传感器").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            	ManualDataModel m = new ManualDataModel();
//            	m._sensorno = mAllSensorsModel.get(which)._sensorno;
//            	m._sensorname = mAllSensorsModel.get(which)._sensorname;
//                datas.add(m);
//            }
//        }).setNegativeButton(android.R.string.cancel, null);
//        bui.create().show();
//	}
	
	private void showAddPopup(View anchor){
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_maunal_sensor, null);

        final Spinner mSensorSpinner = (Spinner) popView.findViewById(R.id.pop_sensor_spinner);
        final EditText numEdit = (EditText) popView.findViewById(R.id.value_edit);
        Button tri_pop_add = (Button) popView.findViewById(R.id.pop_add);
        Button tri_pop_cancel = (Button) popView.findViewById(R.id.pop_cancel);

        final String[] mSensorDatas = new String[mAllSensorsModel.size()];
        for (int i = 0; i < mAllSensorsModel.size(); i ++){
        	mSensorDatas[i] = mAllSensorsModel.get(i)._sensorname;
        }
        if (null != mSensorDatas){
            ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mSensorDatas);
            mSensorSpinner.setAdapter(sensorAdapter);
        } else {
            String[] pleaseWait = {"正在查找传感器"};
            ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,pleaseWait);
            mSensorSpinner.setAdapter(sensorAdapter);
        }

        final PopupWindow popupWindow = new PopupWindow(popView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.ModePopupList);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
        });


        tri_pop_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorModel sModel = mAllSensorsModel.get(mSensorSpinner.getSelectedItemPosition());
                String value = numEdit.getText().toString();
                if (TextUtils.isEmpty(value)){
                    Toast.makeText(mContext, "数值不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ManualDataModel manDataModel = new ManualDataModel();
                manDataModel._facilityid = facilityid;
                manDataModel._data = Float.parseFloat(value);
                manDataModel._sensorno = sModel._sensorno;
                manDataModel._collectiontime = mTimestamp;
                manDataModel._sensorname = sModel._shortname;
                datas.add(manDataModel);
                new Thread(new Runnable() {
					public void run() {
						if (manDataModel._facilityid > 0 && manDataModel._sensorno > 0 && null != manDataModel._collectiontime){
							int ret = new WS_Fish().SaveManualData(manDataModel);
							sendToast(ret > 0 ? "保存成功" : "失败");
						}
					}
				}).start();
                mAdapter.notifyDataSetChanged();
                if (popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
          }
      });

      tri_pop_cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (popupWindow.isShowing()){
                  popupWindow.dismiss();
              }
          }
      });

//      popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.ssiot_green));
//      popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0);
      popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_card_normal));
      popupWindow.showAsDropDown(anchor, anchor.getWidth(), 0);
  }
	
	private void showPop(View anchor){
		View popView = LayoutInflater.from(this).inflate(R.layout.pop_manualdata_edit, null,false);
		final Spinner mSensorSpinner = (Spinner) popView.findViewById(R.id.pop_sensor_spinner);
        final EditText numEdit = (EditText) popView.findViewById(R.id.value_edit);
        TextView popadd = (TextView) popView.findViewById(R.id.pop_add);

        final String[] mSensorDatas = new String[mAllSensorsModel.size()];
        for (int i = 0; i < mAllSensorsModel.size(); i ++){
        	mSensorDatas[i] = mAllSensorsModel.get(i)._sensorname;
        }
        if (null != mSensorDatas){
            ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mSensorDatas);
            mSensorSpinner.setAdapter(sensorAdapter);
        } else {
            String[] pleaseWait = {"正在查找传感器"};
            ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,pleaseWait);
            mSensorSpinner.setAdapter(sensorAdapter);
        }
        
        final PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        
        popadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensorModel sModel = mAllSensorsModel.get(mSensorSpinner.getSelectedItemPosition());
                String value = numEdit.getText().toString();
                if (TextUtils.isEmpty(value)){
                    Toast.makeText(mContext, "数值不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ManualDataModel manDataModel = new ManualDataModel();
                manDataModel._facilityid = facilityid;
                manDataModel._data = Float.parseFloat(value);
                manDataModel._sensorno = sModel._sensorno;
                manDataModel._collectiontime = mTimestamp;
                manDataModel._sensorname = sModel._shortname;
                datas.add(manDataModel);
                new Thread(new Runnable() {
					public void run() {
						if (manDataModel._facilityid > 0 && manDataModel._sensorno > 0 && null != manDataModel._collectiontime){
							int ret = new WS_Fish().SaveManualData(manDataModel);
							sendToast(ret > 0 ? "保存成功" : "失败");
						}
					}
				}).start();
                mAdapter.notifyDataSetChanged();
                if (popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
          }
        });
        
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.anim.roll_down);
        if (anchor != null) {
        	popupWindow.setWidth(anchor.getWidth());
        	popupWindow.showAsDropDown(anchor, 0, 0); 
        } else {
            View localView = findViewById(android.R.id.content);
            popupWindow.showAtLocation(localView, Gravity.BOTTOM, 0, 0); 
        }
	}
	
	private class ManualDataListAdapter extends BaseAdapter{
		private List<ManualDataModel> mDatas;
		private LayoutInflater mInflater;
		
		private ManualDataListAdapter(List<ManualDataModel> d){
			mDatas = d;
			mInflater = LayoutInflater.from(ManualDataEditAct.this);
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
				convertView = mInflater.inflate(R.layout.itm_data, null);
				hold.mNameTxtView = (TextView) convertView.findViewById(R.id.sensor_name);
				hold.mDataView = (EditText) convertView.findViewById(R.id.edit_data);
				hold.mDeleteView = convertView.findViewById(R.id.btn_delete);
				convertView.setTag(hold);
			} else {
				hold = (ViewHold) convertView.getTag();
			}
			final ManualDataModel m = mDatas.get(position);
			hold.mNameTxtView.setText(m._sensorname);
			hold.mDataView.setText(""+m._data);
			hold.mDeleteView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mDatas.remove(m);
					//数据remove
					notifyDataSetChanged();
				}
			});
			return convertView;
		}
		private class ViewHold{
			private TextView mNameTxtView;
			private EditText mDataView;
			private View mDeleteView;
		}
	}
	
	private class GetAllManualSensorsThread extends Thread{
		@Override
		public void run() {
			mAllSensorsModel.clear();
			List<SensorModel> list = new WS_Fish().GetManualSensor();//添加Sensor表的查询接口，  iot_WaterManualAnalysis iot_ManualData
			if (null != list){
				mAllSensorsModel.addAll(list);
			}
		}
	}
	
	private class GetEnableThread extends Thread{
		@Override
		public void run() {
			Timestamp t = new WS_Fish().GetManualDataLastTime(facilityid);
			if (t != null && ((mTimestamp.getTime() - t.getTime()) < 3600 * 1000)){
				sendToast("距离上次录入时间太近则不能建立");
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						titleRight.setEnabled(true);
						footerView.setEnabled(true);
					}
				});
			}
		}
	}
	
	private class GetManualDataThread extends Thread{
		@Override
		public void run() {
			
		}
	}
}