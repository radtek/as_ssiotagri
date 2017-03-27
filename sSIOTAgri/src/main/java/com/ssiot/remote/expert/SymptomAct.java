package com.ssiot.remote.expert;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.data.model.DiseaseModel;
import com.ssiot.remote.data.model.FishPartModel;
import com.ssiot.remote.data.model.SymptomModel;
import com.ssiot.remote.yun.webapi.WS_Fish;

public class SymptomAct extends HeadActivity{
	private static final String tag = "SymptomAct";
	
	private int mFishTypeID;
	List<FishPartModel> mPartModels = new ArrayList<FishPartModel>();
	
	GridView mPartGridView;
	ImageView mImageView;
	Button mDiagnoseBtn;
	FishPartAdapter mPartAdapter;
	
	List<DiseaseModel> mDiseases = new ArrayList<DiseaseModel>();
	
	private static final int MSG_GETPART_END = 1;
	private static final int MSG_DIAGNOSE_END = 2;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GETPART_END:
				mPartAdapter.notifyDataSetChanged();
				break;
			case MSG_DIAGNOSE_END:
				mDiagnoseBtn.setEnabled(true);
				if (null != mDiseases && mDiseases.size() > 0){
					showDiseaseListDialog();
				} else {
					showToast("未诊断出结果");
				}
//				dialog 还是activity
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFishTypeID = getIntent().getIntExtra("fishtypeid", -1);
		if (mFishTypeID < 1){
			showToast("未获取到种类信息，请重新选取种类信息。");
		}
		setContentView(R.layout.act_symptom);
		initViews();
		new GetPartThread().start();
	}
	
	private void initViews(){
		mPartGridView = (GridView) findViewById(R.id.part_grid);
		mImageView = (ImageView) findViewById(R.id.fish_img);
		mDiagnoseBtn = (Button) findViewById(R.id.diagnose_btn);
		mPartAdapter = new FishPartAdapter(this, mPartModels);
		mPartGridView.setAdapter(mPartAdapter);
		mPartGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showPickDialog(mPartModels.get(position));
			}
		});
		mDiagnoseBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDiagnoseBtn.setEnabled(false);
				new DiagnoseThread().start();
			}
		});
	}
	
	private void showPickDialog(final FishPartModel partModel){
		AlertDialog.Builder bui = new AlertDialog.Builder(this);
//        View view = getLayoutInflater().inflate(R.layout.dia_symptom_pick, null);
        final String[] types = new String[partModel._symptoms.size()];
        final boolean[] statusbools= new boolean[partModel._symptoms.size()];
        for (int i = 0; i < partModel._symptoms.size(); i ++){
        	types[i] = partModel._symptoms.get(i)._symptomtxt;
        	statusbools[i] = partModel._symptoms.get(i).isChecked;
        }
        bui.setTitle("选取症状");
        bui.setPositiveButton("确定", null);
        bui.setMultiChoiceItems(types,  statusbools, new DialogInterface.OnMultiChoiceClickListener() {
        	@Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        		partModel._symptoms.get(which).isChecked = isChecked;
        		mPartAdapter.notifyDataSetChanged();//选取的个数更新
            }
        });

//        bui.setTitle("选择").setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
	}
	
	private void showDiseaseListDialog(){
		AlertDialog.Builder bui = new AlertDialog.Builder(this);
		View view = getLayoutInflater().inflate(R.layout.dia_disease_list, null);
		ListView mDiseaseList = (ListView) view.findViewById(R.id.disease_list);
		mDiseaseList.setAdapter(new DiseaseListAdapter(this, mDiseases));
		mDiseaseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(SymptomAct.this, SolutionAct.class);
				intent.putExtra("fishdisease", mDiseases.get(position));
				startActivity(intent);
			}
		});
		bui.setTitle("诊断结果").setView(view).setPositiveButton("确定", null);
		Dialog d = bui.create();
		d.show();
//		d.setOnShowListener(new DialogInterface.OnShowListener() {
//			@Override
//			public void onShow(DialogInterface dialog) {
//				//ListView 的高度在dialog中不能很好适应
//			}
//		});
	}
	
	
	
	private class GetPartThread extends Thread{
		@Override
		public void run() {
			List<FishPartModel> list = new WS_Fish().GetSymptomByType(mFishTypeID);
			if (null != list && list.size() > 0){
				mPartModels.clear();
				mPartModels.addAll(list);
			}
			mHandler.sendEmptyMessage(MSG_GETPART_END);
		}
	}
	
	private class DiagnoseThread extends Thread{
		@Override
		public void run() {
			mDiseases.clear();
			String sylist = "";
			for (FishPartModel f : mPartModels){
				for (SymptomModel syM : f._symptoms){
					if (syM.isChecked){
						sylist += syM._id + ",";
					}
				}
			}
			if (!TextUtils.isEmpty(sylist)){
				sylist = sylist.substring(0, sylist.length() - 1);
				List<DiseaseModel> diseases = new WS_Fish().GetDieaseBySymptom(mFishTypeID, sylist);
				if (null != diseases){
					mDiseases.addAll(diseases);
				}
			}
			mHandler.sendEmptyMessage(MSG_DIAGNOSE_END);
		}
	}
}