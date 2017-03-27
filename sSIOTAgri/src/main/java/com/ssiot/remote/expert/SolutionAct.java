package com.ssiot.remote.expert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Browser;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.BrowserActivity;
import com.ssiot.remote.GetImageThread;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.DiseaseModel;
import com.ssiot.remote.data.model.GoodsModel;
import com.ssiot.remote.yun.webapi.WS_Fish;

public class SolutionAct extends HeadActivity{
	private static final String tag = "SolutionAct";
	DiseaseModel mDiseaseModel;
	ListView mGoodsView;
	List<GoodsModel> mGoodsModels = new ArrayList<GoodsModel>();
	GoodsAdapter mGoodsAdapter;
	private static final int MSG_GET_GOODS = 1;
	private static final int MSG_GETIMG = GetImageThread.MSG_GETFTPIMG_END;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GET_GOODS:
				if (null != mGoodsAdapter){
					mGoodsAdapter.notifyDataSetChanged();
				}
				break;

			case MSG_GETIMG:
				GetImageThread.ThumnailHolder thumb = (GetImageThread.ThumnailHolder) msg.obj;
				if (null != thumb.bitmap){
					thumb.imageView.setImageBitmap(thumb.bitmap);
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
		setContentView(R.layout.act_solution);
		getSupportActionBar().setTitle("渔管家-鱼病解决方案");
		mDiseaseModel = (DiseaseModel) getIntent().getSerializableExtra("fishdisease");
		if (null == mDiseaseModel){
			showToast("传输数据出错，请重新诊断");
		} else {
			initViews();
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				mDiseaseModel = new WS_Fish().GetDiease(mDiseaseModel._id);
				runOnUiThread(new Runnable() {
					public void run() {
						if (null != mDiseaseModel){
							initViews();
						}
					}
				});
			}
		}).start();
		
	}
	
	private void initViews(){
		TextView dateView = (TextView) findViewById(R.id.txt_date);
		TextView timeView = (TextView) findViewById(R.id.txt_time);
		TextView diseaseView = (TextView) findViewById(R.id.txt_diseasename);
		TextView fishtypeView = (TextView) findViewById(R.id.txt_fishtype);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateTxt = formatter.format(new Date());
		dateView.setText(dateTxt);
		SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
		String timeTxt = formatter2.format(new Date());
		timeView.setText(timeTxt);
		diseaseView.setText(mDiseaseModel._name);
		fishtypeView.setText(mDiseaseModel._fishname);//TODO 获取种类
		
		TextView reasonView = (TextView) findViewById(R.id.txt_reason);
		TextView resultView = (TextView) findViewById(R.id.txt_result);
		TextView resolveView = (TextView) findViewById(R.id.txt_resolve);
		reasonView.setText(mDiseaseModel._causereason);
		resultView.setText(mDiseaseModel._resulttext);
		resolveView.setText(mDiseaseModel._resolve);
		
		mGoodsView = (ListView) findViewById(R.id.goods_list);
		setGoodsListHeight(mGoodsView);
		mGoodsAdapter = new GoodsAdapter(this,mGoodsModels,mHandler);
		mGoodsView.setAdapter(mGoodsAdapter);
		mGoodsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int userid = Utils.getIntPref(Utils.PREF_USERID, SolutionAct.this);
				Intent intent = new Intent(SolutionAct.this, BrowserActivity.class);
				intent.putExtra("url", "http://wapcart.fisher88.com/product.aspx?id=" + mGoodsModels.get(position)._id + "&userid=" + userid);
				startActivity(intent);
			}
		});
		new GetGoodsThread().start();
	}
	
	private void setGoodsListHeight(ListView listView){
		int size = 0;
		if (!TextUtils.isEmpty(mDiseaseModel._medicineIds)){
			String[] goodsids = mDiseaseModel._medicineIds.split(",");
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
	
	private class GetGoodsThread extends Thread{
		@Override
		public void run() {
			mGoodsModels.clear();
			List<GoodsModel> list = new WS_Fish().GetMultiGoods(mDiseaseModel._medicineIds);
			if (null != list && list.size() > 0){
				mGoodsModels.addAll(list);
			}
			mHandler.sendEmptyMessage(MSG_GET_GOODS);
		}
	}
}