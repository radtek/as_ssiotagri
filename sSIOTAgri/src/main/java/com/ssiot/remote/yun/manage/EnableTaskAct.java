package com.ssiot.remote.yun.manage;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPTaskInstanceModel;
import com.ssiot.remote.yun.manage.task.TaskNewActivity;
import com.ssiot.remote.yun.manage.task.TaskReceiverAct;
import com.ssiot.remote.yun.webapi.TaskInstance;

public class EnableTaskAct extends HeadActivity{
	private static final String tag = "EnableTaskAct";
	
	int userid;
	int taskid;
	int batchid;
	String mTaskStr;
	String mBatchStr;
	
	LinearLayout toUsersLinear;
    LinearLayout mContainerRE;
    
    private ArrayList<String> toUserNames = new ArrayList<String>();
    private ArrayList<Integer> toUserIDs = new ArrayList<Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideActionBar();
		setContentView(R.layout.act_enable_task);
		userid = Utils.getIntPref(Utils.PREF_USERID, this);
		taskid = getIntent().getIntExtra("taskid", -1);
		batchid = getIntent().getIntExtra("batchid", -1);
		mTaskStr = getIntent().getStringExtra("taskstr");
		mBatchStr = getIntent().getStringExtra("batchstr");
		initViews();
	}
	
	private void initViews(){
		TextView batchView = (TextView) findViewById(R.id.batch_str);
		TextView taskView = (TextView) findViewById(R.id.task_str);
		batchView.setText("批次:" + mBatchStr);
		taskView.setText("任务:" + mTaskStr);
		toUsersLinear = (LinearLayout) findViewById(R.id.linearLayoutRe);
        mContainerRE = (LinearLayout) findViewById(R.id.LinearLayoutcontainer);
        initTitleBar();
	}
	
	private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	new Thread(new Runnable() {
					@Override
					public void run() {
						String workerids = buildToUsersStr();
						if (TextUtils.isEmpty(workerids)){
							sendToast("请选择任务接受者");
							return;
						}
						int ret = new TaskInstance().EnabelTaskToTaskInstance(taskid, batchid, workerids, userid);
						if (ret > 0){
							sendToast("任务激活成功");
							setResult(RESULT_OK);
							finish();
						} else {
							sendToast("任务激活失败");
						}
					}
				}).start();
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
	
	private String buildToUsersStr(){//不是用json格式了
		String str = "";
        try {
            for (int i =0; i < toUserIDs.size(); i ++){
            	str += ""+toUserIDs.get(i) + ",";
            }
            if (!TextUtils.isEmpty(str)){
            	str = str.substring(0, str.length() - 1);
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
	
	private final  int REQUEST_USERS = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_USERS:
                if (RESULT_OK == resultCode){
                    toUserNames.clear();
                    toUserIDs.clear();
                    mContainerRE.removeAllViews();
                    toUserNames = data.getStringArrayListExtra("extra_names");
                    toUserIDs = data.getIntegerArrayListExtra("extra_userids");
                    if (null != toUserNames){
                        for (int i = 0; i < toUserNames.size(); i ++){
//                            ContactView child = new ContactView(this);
//                            child.setText(toUserNames.get(i));
//                            mContainerRE.addView(child);
                            addRec(toUserNames.get(i), toUserIDs.get(i));
                        }
                    }
                }
                break;

            default:
                break;
        }
    }
    
    public void addRec(String name,int userid){
        ViewGroup contactView = (ViewGroup)LayoutInflater.from(this).inflate(R.layout.msg_new_item, this.mContainerRE, false);
        ((TextView)contactView.findViewById(R.id.TextViewTitle)).setText(name);
        View deleteView = contactView.findViewById(R.id.ImageButtonDelete);
        deleteView.setTag(R.id.tag_first, name);
        deleteView.setTag(R.id.tag_second, userid);
//        deleteView.setTag(R.id.tag_third, contactView);
        deleteView.setOnClickListener(this.deleteClickListener);
        mContainerRE.addView(contactView);
    }
    
    View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int userid = (Integer) v.getTag(R.id.tag_second);
            int index = -1;
            for (int i = 0; i < toUserIDs.size(); i ++){
                if (userid == toUserIDs.get(i)){
                    index = i;
                    break;
                }
            }
            if (index > -1){
                toUserIDs.remove(index);
                toUserNames.remove(index);
                mContainerRE.removeViewAt(index);
            }
        }
    };
    
    public void ClickAdd(View v){
        Intent userCheckIntent = new Intent(this, TaskReceiverAct.class);
        startActivityForResult(userCheckIntent, REQUEST_USERS);
    }
	
}