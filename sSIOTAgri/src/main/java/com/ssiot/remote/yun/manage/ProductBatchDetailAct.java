package com.ssiot.remote.yun.manage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.data.model.ERPProductBatchModel;
import com.ssiot.remote.data.model.ERPProductPlanModel;
import com.ssiot.remote.data.model.ERPTaskInstanceModel;
import com.ssiot.remote.data.model.ERPTaskModel;
import com.ssiot.remote.yun.webapi.ProductPlan;
import com.ssiot.remote.yun.webapi.Task;
import com.ssiot.remote.yun.webapi.TaskInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductBatchDetailAct extends HeadActivity{
    private static final String tag = "ProductBatchDetailAct";
    
    private ERPProductBatchModel mModel;
    private List<ERPTaskModel> mTasks = new ArrayList<ERPTaskModel>();
    
    TextView mBatchTextView;
    TextView mInputsTextView;
    TextView mCropTextView;
    TextView mFacilityTextView;
    TextView mUserTextView;
    
    ListView mTaskListView;
    TaskListAdapter adapter;
    
    private static final int MSG_GET_END = 1;
    private static final int MSG_ENABLETASK_END = 2;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    if (null != mTasks && mTasks.size() > 0 && adapter != null){
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case MSG_ENABLETASK_END:
                	
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
        mModel = (ERPProductBatchModel) intent.getSerializableExtra("erp_productbatch");
        if (null == mModel){
            Log.e(tag, "--------mModel = null");
            Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
            return;
        }
        setContentView(R.layout.act_product_batch_detail);
        initViews();
        new GetBatchDetailThread().start();
    }
    
    private void initViews(){
        mBatchTextView = (TextView) findViewById(R.id.batch_name);
        mInputsTextView = (TextView) findViewById(R.id.inputsin_name);
        mCropTextView = (TextView) findViewById(R.id.croptype_name);
        mFacilityTextView = (TextView) findViewById(R.id.facility_name);
        mUserTextView = (TextView) findViewById(R.id.user_name);
//        mInputsTextView
        mTaskListView = (ListView) findViewById(R.id.task_list);
        adapter = new TaskListAdapter(mTasks, this, new TaskListAdapter.OnEnableTaskListener() {
			@Override
			public void onEnableTask(ERPTaskModel taskmodel) {
				Intent intent = new Intent(ProductBatchDetailAct.this, EnableTaskAct.class);
				intent.putExtra("taskid", taskmodel._id);
				intent.putExtra("batchid", mModel._id);
				intent.putExtra("taskstr", taskmodel._taskdetail);
				intent.putExtra("batchstr", mModel._name);
				startActivityForResult(intent, REQUEST_ENABLETASK);
			}
		});
        mTaskListView.setAdapter(adapter);
        setViewData();
    }
    
    private void setViewData(){
        mBatchTextView.setText(mModel._name != null ? mModel._name : "空");
//        mInputsTextView
        mCropTextView.setText(mModel._CropName != null ? mModel._CropName : "空");
        mFacilityTextView.setText(mModel._FacilityName != null ? mModel._FacilityName : "空");
        mUserTextView.setText(mModel._UserName != null ? mModel._UserName : "空");
    }
    
    private class GetBatchDetailThread extends Thread{
        @Override
        public void run() {
            ERPProductPlanModel planModel = new ProductPlan().GetProductPlanByID(mModel._planid);
            if (null != planModel){
            	List<Integer> ids = new ArrayList<Integer>();
                try {
                    String[] tmp = planModel._taskids.split(",");
                    for (int i = 0; i < tmp.length; i ++){
                    	ids.add(Integer.valueOf(tmp[i]));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                List<ERPTaskModel> tmpTasks;
                tmpTasks = new Task().GetTasksByIDs(ids);
                mTasks.clear();
                List<ERPTaskInstanceModel> enabledTasks = new TaskInstance().GetTaskInstance(" BatchID=" + mModel._id);
                
                if (null != tmpTasks){
                	for (ERPTaskModel m : tmpTasks){
                    	if (null != enabledTasks){
                    		for (ERPTaskInstanceModel taskins : enabledTasks){
                    			if (taskins._taskmouldid == m._id){
                    				m.enabled = true;
                    				break;
                    			}
                    		}
                    	}
                    }
                    mTasks.addAll(tmpTasks);
                }
                mHandler.sendEmptyMessage(MSG_GET_END);
            }
        }
    }
    
    private static final int REQUEST_ENABLETASK = 1;
    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data) {
    	Log.v(tag, "--------onActivityResult-----" + requestcode + " resultcode:"+resultcode);
    	switch (requestcode) {
		case REQUEST_ENABLETASK:
			if (RESULT_OK == resultcode){
				new GetBatchDetailThread().start();//激活后要刷新界面
			}
			break;

		default:
			break;
		}
    	super.onActivityResult(requestcode, resultcode, data);
    }
}