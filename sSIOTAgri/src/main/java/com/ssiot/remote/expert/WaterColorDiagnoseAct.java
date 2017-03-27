package com.ssiot.remote.expert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.GetImageThread;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.CameraFileModel;
import com.ssiot.remote.data.model.VLCVideoInfoModel;
import com.ssiot.remote.data.model.WaterColorDiagnoseModel;
import com.ssiot.remote.yun.webapi.WS_API;
import com.ssiot.remote.yun.webapi.WS_Fish;

public class WaterColorDiagnoseAct extends HeadActivity{
	private static final String tag = "WaterColorDiagnoseAct";
	ListView mListView;
	private List<WaterColorDiagnoseModel> mColorDatas = new ArrayList<WaterColorDiagnoseModel>();
	WaterColorAdapter mAdapter;
	
	List<VLCVideoInfoModel> mCameras = new ArrayList<VLCVideoInfoModel>();
	VLCVideoInfoModel selectedCamera;
	
	ViewPager mPager;
	private List<ImageView> mImageViewList = new ArrayList<ImageView>();
	private List<CameraFileModel> imgList = new ArrayList<CameraFileModel>();
	TextView timeText;
	
	Button mCameraPickBtn;
	Button mColorPickBtn;
	ImageView mColorImgView;
	Button mCauseBtn;
	Button mResultBtn;
	Button mResolveBtn;
	
	int selectedColorID = -1;
	WaterColorDiagnoseModel selectedColorModel;
	
	private static final int MSG_FILELIST_GET = 1;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_FILELIST_GET:
				mPager.clearOnPageChangeListeners();
				mPager.setAdapter(null);
				initViewPager();
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_watercolordiagnose);
		initViews();
		new GetAllIPCThread().start();
	}
	
	private void initViews(){
		mCameraPickBtn = (Button) findViewById(R.id.camera_pick);
		mPager = (ViewPager) findViewById(R.id.pic_pager);
		timeText = (TextView) findViewById(R.id.time_text);
		mCameraPickBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showCameraPickDia();
			}
		});
		initViewPager();
		mColorPickBtn = (Button) findViewById(R.id.btn_colorpick);
		mColorImgView = (ImageView) findViewById(R.id.color_img);
		mCauseBtn = (Button) findViewById(R.id.btn_cause);
		mResultBtn = (Button) findViewById(R.id.btn_result);
		mResolveBtn = (Button) findViewById(R.id.btn_resolve);
		
		mColorPickBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showColorPickDia();
			}
		});
		mCauseBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedColorID >= 0 && mColorDatas.size() > selectedColorID){
					showMsgDia("原因", selectedColorModel._causereason);
				}
			}
		});
		mResultBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedColorID >= 0 && mColorDatas.size() > selectedColorID){
					showMsgDia("结果", selectedColorModel._resulttext);
				}
			}
		});
		mResolveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				if (selectedColorID >= 0 && mColorDatas.size() > selectedColorID){
//					showMsgDia("解决方案", selectedColorModel._resolve);
//				}
				if (selectedColorID >= 0 && !TextUtils.isEmpty(selectedColorModel._resolve)){
					Intent intent = new Intent(WaterColorDiagnoseAct.this, WaterColorSolutionAct.class);
					intent.putExtra("watercolormodel", selectedColorModel);
					startActivity(intent);
				}
			}
		});
		
//		mListView = (ListView) findViewById(R.id.color_list);
//		mAdapter = new WaterColorAdapter(mDatas);
//		mListView.setAdapter(mAdapter);
		new GetSimpleWaterColorsThread().start();
	}
	
	private void initViewPager(){
		mPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                Log.v(tag, "----instantiateItem----" + position);
                ImageView page = new ImageView(WaterColorDiagnoseAct.this);//mImageViewList.get(position);
//                page.setImageBitmap(bm);
                
                if (imgList.get(position).bitmap != null){
                	page.setImageBitmap(imgList.get(position).bitmap);
                } else {
                	page.setTag(imgList.get(position));//为了Thread里保存文件用的。
                	new GetImageThread(page, imgList.get(position)._url, null, 512).start();
                }
                page.setScaleType(ImageView.ScaleType.FIT_XY);
                container.addView(page);
                mImageViewList.add(page);
//                fillPage(page, position);
                
                return page;
            }
    
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(container.getChildAt(position));
            }
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }
    
            @Override
            public int getCount() {
                return imgList.size();
            }
        });
		mPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
            	timeText.setText(Utils.formatTime(imgList.get(arg0)._time));
            }
    
            @Override
            public void onPageScrolled(int arg0, float argfloat, int arg2) {
//                Log.v(tag, "----onPageScrolled----" +arg0+"float:"+ argfloat + " arg2:" + arg2);
//                View localView = rGroup.getChildAt(arg0);//rGroup.findViewById());
//                ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams) indicater.getLayoutParams();
//                localMarginLayoutParams.width = (localView.getRight() - localView.getLeft());
//                localMarginLayoutParams.leftMargin = ((int)(argfloat * localMarginLayoutParams.width) + localView.getLeft());
//                indicater.setLayoutParams(localMarginLayoutParams);
//                indicater.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
	}

	private void showColorPickDia(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		mListView = new ListView(this);
//		mListView.setAdapter(new WaterColorAdapter(mColorDatas));
//		builder.setView(mListView);
		final String[] types = new String[mColorDatas.size()];
        for (int i = 0; i < mColorDatas.size(); i ++){
            types[i] = mColorDatas.get(i)._name;
        }
		builder.setTitle("选择一个水色").setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
			@Override
            public void onClick(DialogInterface dialog, int which) {
				selectedColorID = mColorDatas.get(which)._id;
				selectedColorModel = mColorDatas.get(which);
//                mProductTypeID = mAllTypesModel.get(which)._id;
//                typeStr = mAllTypesModel.get(which)._inputstypename;
				mColorPickBtn.setText("选取一种水色(" + mColorDatas.get(which)._name + ")");
				new GetImageThread(mColorImgView, "http://cloud.ssiot.com/" + mColorDatas.get(which)._img, mHandler).start();
				new GetDetailWaterColorDiagnoseThread(selectedColorID).start();
            }
        });
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
	
	private void showCameraPickDia(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (null == mCameras || mCameras.size() == 0){
			showToast("无摄像头");
			return;
		}
//		mListView = new ListView(this);
//		mListView.setAdapter(new WaterColorAdapter(mColorDatas));
//		builder.setView(mListView);
		final String[] types = new String[mCameras.size()];
        for (int i = 0; i < mCameras.size(); i ++){
            types[i] = mCameras.get(i)._address;
        }
		builder.setTitle("选择一个摄像头").setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
			@Override
            public void onClick(DialogInterface dialog, int which) {
//				selectedColorID = mCameras.get(which)._id;
				selectedCamera = mCameras.get(which);
//                mProductTypeID = mAllTypesModel.get(which)._id;
//                typeStr = mAllTypesModel.get(which)._inputstypename;
				mCameraPickBtn.setText("选取一个摄像头(" + mCameras.get(which)._address + ")");
				new GetCameraImgsThread(selectedCamera._vlcvideoinfoid).start();
            }
        });
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
	
	private void showMsgDia(String title,String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title).setMessage(msg);
		builder.create().show();
	}
	
	private class GetSimpleWaterColorsThread extends Thread{
		@Override
		public void run() {
			mColorDatas.clear();
			List<WaterColorDiagnoseModel> list = new WS_Fish().GetAllWaterColors();
			if (null != list){
				mColorDatas.addAll(list);
			}
		}
	}
	
	private class GetDetailWaterColorDiagnoseThread extends Thread{
		int id = -1;
		public GetDetailWaterColorDiagnoseThread(int i){
			id = i;
		}
		@Override
		public void run() {
			WaterColorDiagnoseModel m = new WS_Fish().GetWaterColorResolve(id);
			if (null != m){
				selectedColorModel = m;
			}
		}
	}
	
	private class GetAllIPCThread extends Thread{
		@Override
		public void run() {
			mCameras.clear();
			int userid = Utils.getIntPref(Utils.PREF_USERID, WaterColorDiagnoseAct.this);
			List<VLCVideoInfoModel> list = new WS_API().GetAllIPC(userid);
			if (null != list){
				mCameras.addAll(list);
			}
		}
	}
	
	private class GetCameraImgsThread extends Thread{
		int vlcID = 0;
		public GetCameraImgsThread(int cameraid){
			vlcID = cameraid;
		}
		@Override
		public void run() {
			List<CameraFileModel> files = new WS_API().GetCameraFiles(20, vlcID);
			imgList.clear();
			if (null != files){
				imgList.addAll(files);
			}
			if (null == imgList || imgList.size() == 0){
				sendToast("摄像头无截图图片");
			}
			mHandler.sendEmptyMessage(MSG_FILELIST_GET);
//			userid-cameralist-选择id-imglist
		}
	}
	
	private class WaterColorAdapter extends BaseAdapter{
		private List<WaterColorDiagnoseModel> datas;
		LayoutInflater inflater;
		private WaterColorAdapter(List<WaterColorDiagnoseModel> colors){
			datas = colors;
			inflater = LayoutInflater.from(WaterColorDiagnoseAct.this);
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (null == convertView){
				convertView = inflater.inflate(R.layout.itm_watercolor, null, false);
				holder = new ViewHolder();
				holder.imgView = (ImageView) convertView.findViewById(R.id.water_img);
				holder.txtView = (TextView) convertView.findViewById(R.id.color_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			WaterColorDiagnoseModel m = datas.get(position);
			new GetImageThread(holder.imgView, "http://fisher.ssiot.com/zpchtml/goodsimg/"+m._img, null);//TODO
			holder.txtView.setText(m._name);
			return convertView;
		}
		
		private class ViewHolder{
			ImageView imgView;
			TextView txtView;
		}
	}
	
}