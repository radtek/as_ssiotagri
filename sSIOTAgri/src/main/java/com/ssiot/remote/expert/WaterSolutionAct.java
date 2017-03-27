package com.ssiot.remote.expert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.BrowserActivity;
import com.ssiot.remote.GetImageThread;
import com.ssiot.remote.data.model.GoodsModel;
import com.ssiot.remote.data.model.WaterDetailModel;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;
import com.ssiot.remote.yun.webapi.WS_Fish;
import com.ssiot.remote.yun.webapi.WS_WaterQuality;

public class WaterSolutionAct extends HeadActivity{
	private static final String tag = "WaterSolutionAct";
	
	private List<DeviceBean> abnormalDevices = new ArrayList<DeviceBean>();//不正常的device
	YunNodeModel yunNodeModel;
	RecyclerView recyclerView;//不正常的传感器
	private int selectedPostion = 0;
	
	ListView mGoodsView;
	List<GoodsModel> mGoodsModels = new ArrayList<GoodsModel>();
	GoodsAdapter mGoodsAdapter;
	
	private static final int MSG_GET_GOODS = 1;
	private static final int MSG_GET_WATERSOLUTION_END = 3;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GET_WATERSOLUTION_END:
				WaterDetailModel model = (WaterDetailModel) msg.obj;
				if (null != model){
					refreshSolutionAndGoods(model);
				}
				break;
			case GetImageThread.MSG_GETFTPIMG_END:
				break;
				
			case MSG_GET_GOODS:
				if (null != mGoodsAdapter) {
					mGoodsAdapter.notifyDataSetChanged();
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
		setContentView(R.layout.act_watersolution);
		getSupportActionBar().setTitle("渔管家-水质解决方案");
		yunNodeModel = (YunNodeModel) getIntent().getSerializableExtra("yunnodemodel");
		initViews();
	}
	
	private void initViews(){
		getabNormalDevices(yunNodeModel);
		TextView dateView = (TextView) findViewById(R.id.txt_date);
		TextView timeView = (TextView) findViewById(R.id.txt_time);
		TextView diseaseView = (TextView) findViewById(R.id.txt_diseasename);//这个是存鱼塘信息的//水塘名称
		TextView fishtypeView = (TextView) findViewById(R.id.txt_fishtype);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateTxt = formatter.format(new Date());
		dateView.setText(dateTxt);
		SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
		String timeTxt = formatter2.format(new Date());
		timeView.setText(timeTxt);
		diseaseView.setText(yunNodeModel.facilityStr);
		fishtypeView.setText("typeid:");//TODO 获取种类
		
		recyclerView = (RecyclerView) findViewById(R.id.recycler_view); 
		recyclerView.setHasFixedSize(true);
		int spanCount = 1; // 只显示一行  
	    LayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL);
	    recyclerView.setLayoutManager(layoutManager);
	    recyclerView.setAdapter(new HorizontalListViewAdapter(abnormalDevices));
	    
	    mGoodsView = (ListView) findViewById(R.id.goods_list);
		mGoodsAdapter = new GoodsAdapter(this,mGoodsModels,mHandler);
		mGoodsView.setAdapter(mGoodsAdapter);
		mGoodsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(WaterSolutionAct.this, BrowserActivity.class);
				intent.putExtra("url", "http://wapcart.fisher88.com/product.aspx?id=" + mGoodsModels.get(position)._id);
				startActivity(intent);
			}
		});
		
		if (abnormalDevices.size() > 0){//初始化时手动获取一下
			selectedPostion = 0;
			getSolution(abnormalDevices.get(0));
		}
	}
	
	private void refreshSolutionAndGoods(WaterDetailModel waterSolutionModel){
		TextView reasonView = (TextView) findViewById(R.id.txt_reason);
		TextView resultView = (TextView) findViewById(R.id.txt_result);
		TextView resolveView = (TextView) findViewById(R.id.txt_resolve);
		reasonView.setText(waterSolutionModel._causereason);
		resultView.setText(waterSolutionModel._resulttext);
		resolveView.setText(waterSolutionModel._resolve);
		
		setGoodsListHeight(mGoodsView, waterSolutionModel);
		mGoodsModels.clear();
		mGoodsAdapter.notifyDataSetChanged();
		new GetGoodsThread(waterSolutionModel._medicineIds).start();
	}
	
	private void setGoodsListHeight(ListView listView, WaterDetailModel waterSolutionModel){
		int size = 0;
		if (!TextUtils.isEmpty(waterSolutionModel._medicineIds)){
			String[] goodsids = waterSolutionModel._medicineIds.split(",");
			size = goodsids.length;
		}
		if (size > 2){
			size =2;
		}
		int height = (int) getResources().getDimension(R.dimen.mainItemHeigh) * size;
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = height;
		listView.setLayoutParams(params);
	}
	
	
	private List<DeviceBean> getabNormalDevices(YunNodeModel y){
		abnormalDevices.clear();
		for (DeviceBean d : yunNodeModel.list){
			if (d.thresholdModel != null){
				//
				boolean abnormal = false;
				boolean upperorlower = false;
				if (d.thresholdModel.thresholdType == 1){
					abnormal = d.value > d.thresholdModel.upperwarnvalue;
					upperorlower = true;
				} else if (d.thresholdModel.thresholdType == 2){
					abnormal = d.value < d.thresholdModel.lowerwarnvalue;
					upperorlower = false;
				} else if (d.thresholdModel.thresholdType == 3){
					abnormal = d.value > d.thresholdModel.upperwarnvalue || d.value < d.thresholdModel.lowerwarnvalue;
					upperorlower = d.value > d.thresholdModel.upperwarnvalue;
				}
				if (abnormal){
					abnormalDevices.add(d);
				}
			}
		}
		return abnormalDevices;
	}
	
	private void getSolution(DeviceBean d){
		boolean upperorlower = false;
		if (d.thresholdModel.thresholdType == 1){
			upperorlower = true;
		} else if (d.thresholdModel.thresholdType == 2){
			upperorlower = false;
		} else if (d.thresholdModel.thresholdType == 3){
			upperorlower = d.value > d.thresholdModel.upperwarnvalue;
		}
		new GetSolutionsThread(yunNodeModel.mFacilityID, d.mDeviceTypeNo, upperorlower).start();
	}
	
	public class HorizontalListViewAdapter extends RecyclerView.Adapter{
		private List<DeviceBean> deviceBeans;
		
		public HorizontalListViewAdapter(List<DeviceBean> devices){
			deviceBeans = devices;
		}

		@Override
		public int getItemCount() {
			return deviceBeans.size();
		}

		@Override
		public void onBindViewHolder(ViewHolder vHolder, final int i) {
			MyViewHolder holder = (MyViewHolder) vHolder;
			holder.txtView.setText(deviceBeans.get(i).mName + "异常");
			final TextView tView = holder.txtView;
			final DeviceBean d = deviceBeans.get(i);
			tView.setSelected(selectedPostion == i);
			holder.txtView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getSolution(d);
					
					selectedPostion = i;
					notifyDataSetChanged();
				}
			});
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
			View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itm_txt, viewGroup, false);
			return new MyViewHolder(view);
		}
		
		protected class MyViewHolder extends RecyclerView.ViewHolder{
			TextView txtView;
			public MyViewHolder(View v) {
				super(v);
				txtView = (TextView) v;
			}
		}
		
	}
	
	private class GetGoodsThread extends Thread{
		String idStrs;
		public GetGoodsThread(String ids){
			idStrs = ids;
		}
		@Override
		public void run() {
			mGoodsModels.clear();
			List<GoodsModel> list = new WS_Fish().GetMultiGoods(idStrs);
			if (null != list && list.size() > 0){
				mGoodsModels.addAll(list);
			}
			mHandler.sendEmptyMessage(MSG_GET_GOODS);
		}
	}
	
	public class GetSolutionsThread extends Thread{
		private int facilityid;
		private int senid;
		private boolean upperORlower;
		
		private GetSolutionsThread(int faid, int sen, boolean upperorlower){
			facilityid = faid;
			senid = sen;
			upperORlower = upperorlower;
		}
		@Override
		public void run() {
			WaterDetailModel model = new WS_WaterQuality().GetWaterDetailBySensor(facilityid, senid, upperORlower);
			Message msg = mHandler.obtainMessage(MSG_GET_WATERSOLUTION_END);
			msg.obj = model;
			mHandler.sendMessage(msg);
		}
	}
}