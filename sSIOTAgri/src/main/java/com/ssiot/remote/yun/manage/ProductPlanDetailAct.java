package com.ssiot.remote.yun.manage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPProductPlanModel;
import com.ssiot.remote.data.model.ERPTaskModel;
import com.ssiot.remote.yun.webapi.Task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductPlanDetailAct extends HeadActivity{
    private static final String tag = "ProductPlanDetailAct";
    private int userid;
    ERPProductPlanModel mPlanModel;
    private List<ERPTaskModel> mTasks = new ArrayList<ERPTaskModel>();
    
    TextView mPlanNameTextView;
    TextView mCropTypeTextView;
    TextView mOwnerTextView;
    private ListView mTaskListView;
    TaskListAdapter adapter;
    
    private static final int MSG_GET_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    if (null != mTasks && mTasks.size() > 0 && adapter != null){
                        adapter.notifyDataSetChanged();
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
        Intent intent = getIntent();
        mPlanModel = (ERPProductPlanModel) intent.getSerializableExtra("erp_productplan");
        userid = Utils.getIntPref(Utils.PREF_USERID, this);
        setContentView(R.layout.act_product_plan_detail);
        initViews();
        getSupportActionBar().setTitle("方案-详细");
        new GetTaskThread().start();
    }
    
    private void initViews(){
        mPlanNameTextView = (TextView) findViewById(R.id.plan_name);
        mCropTypeTextView = (TextView) findViewById(R.id.croptype_name);
        mOwnerTextView = (TextView) findViewById(R.id.owner_name);
        mTaskListView = (ListView) findViewById(R.id.task_list);
        adapter = new TaskListAdapter(mTasks, this);
        mTaskListView.setAdapter(adapter);
        initData();
    }
    
    private void initData(){
        if (null != mPlanModel){
            mPlanNameTextView.setText(mPlanModel._name);
            mOwnerTextView.setText(mPlanModel._owenerid == 0 ? "平台" : mPlanModel._UserName);
        }
    }
    
    private class GetTaskThread extends Thread{
        @Override
        public void run() {
            List<ERPTaskModel> tmpTasks;
            
            List<Integer> ids = new ArrayList<Integer>();
            try {
                String[] tmp = mPlanModel._taskids.split(",");
                for (int i = 0; i < tmp.length; i ++){
                	ids.add(Integer.valueOf(tmp[i]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            tmpTasks = new Task().GetTasksByIDs(ids);
            mTasks.clear();
            if (null != tmpTasks){
                mTasks.addAll(tmpTasks);
            }
            mHandler.sendEmptyMessage(MSG_GET_END);
        }
    }
}