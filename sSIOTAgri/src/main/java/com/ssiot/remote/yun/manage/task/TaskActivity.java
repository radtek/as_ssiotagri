package com.ssiot.remote.yun.manage.task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPTaskInstanceModel;
import com.ssiot.remote.data.model.TraceProfileModel;
import com.ssiot.remote.data.model.UserModel;
import com.ssiot.remote.yun.webapi.TaskInstance;
import com.ssiot.remote.yun.webapi.WS_User;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends HeadActivity{
    private static final String tag = "TaskActivity";
    RadioGroup rGroup;
    ViewPager pager;
    ImageButton mNewBtn;
    ArrayList<View> viewList = new ArrayList<View>();
    private TaskAdapter mGetTaskAdapter;
    private TaskAdapter mSendTaskAdapter;
    private List<ERPTaskInstanceModel> mGetTasks = new ArrayList<ERPTaskInstanceModel>();
    private List<ERPTaskInstanceModel> mSendTasks = new ArrayList<ERPTaskInstanceModel>();
    
    private static final int MSG_GET_END = 1;
    private static final int MSG_GET_NAME_END = 3;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    mGetTaskAdapter.notifyDataSetChanged();
                    mSendTaskAdapter.notifyDataSetChanged();
                    for (int i = 0; i < viewList.size(); i ++){
                        ((SwipeRefreshLayout) viewList.get(i).findViewById(R.id.swipelist_layout)).setRefreshing(false);
                    }
                    break;
                case MSG_GET_NAME_END:
                	NameViewHolder t = (NameViewHolder) msg.obj;
                	t.textView.setText("来自:"+t.str);
                	break;

                default:
                    break;
            }
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
        rGroup = (RadioGroup) findViewById(R.id.RadioGroupTask);
        pager = (ViewPager) findViewById(R.id.pagerMsg);
        mNewBtn = (ImageButton) findViewById(R.id.task_new);
        mNewBtn.setOnClickListener(newTaskListener);
        initRadioGroup();
        LayoutInflater inflater = LayoutInflater.from(this);
        initViewPager(inflater);
    }
    
    View.OnClickListener newTaskListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(TaskActivity.this, TaskNewActivity.class);
            startActivity(intent);
        }
    };
    
    private void initRadioGroup(){
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (null != pager) {
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    Log.v(tag, "----------onCheckedChanged----------" + checkedId + radioButton.isChecked());
                    if (radioButton.isChecked()) {
                        switch (radioButton.getId()) {
                            case R.id.RadioButtonGet:
                                pager.setCurrentItem(0, true);
                                break;
                            case R.id.RadioButtonSend:
                                pager.setCurrentItem(1, true);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        });
    }
    
    private void initViewPager(LayoutInflater inflater){
        viewList.clear();
        viewList.add(inflater.inflate(R.layout.activity_swiperefresh_list, null, false));
        viewList.add(inflater.inflate(R.layout.activity_swiperefresh_list, null, false));
        pager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                Log.v(tag, "----instantiateItem----" + position);
                View page = viewList.get(position);
                container.addView(page);
                fillPage(page, position);
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
                return viewList.size();
            }
        });
        pager.setOnPageChangeListener(new OnPageChangeListener() {
            
            @Override
            public void onPageSelected(int arg0) {
                ((RadioButton) rGroup.getChildAt(arg0)).setChecked(true);
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
    
    private void fillPage(View pageRoot, int index){
        if (index == 0){
            RecyclerView mRecyclerView = ((RecyclerView)LayoutInflater.from(this).inflate(R.layout.vertical_recycler_view, null));
            SwipeRefreshLayout page = (SwipeRefreshLayout) pageRoot.findViewById(R.id.swipelist_layout);
            ((SwipeRefreshLayout) page).addView(mRecyclerView);
            mRecyclerView.setHasFixedSize(true);//设置固定大小
            LinearLayoutManager bLinearLayoutManager = new LinearLayoutManager(this);
            bLinearLayoutManager.setOrientation(1);
            mRecyclerView.setLayoutManager(bLinearLayoutManager);//没有这部分会导致 computeVerticalScrollOffset空指针
            ((SwipeRefreshLayout) page).setSize(SwipeRefreshLayout.DEFAULT);
            mGetTaskAdapter = new TaskAdapter(mGetTasks);
            mRecyclerView.setAdapter(mGetTaskAdapter);
            ((SwipeRefreshLayout) page).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new GetTaskThread().start();
                }
            });
            
        } else {
            RecyclerView mRecyclerView = ((RecyclerView)LayoutInflater.from(this).inflate(R.layout.vertical_recycler_view, null));
            SwipeRefreshLayout page = (SwipeRefreshLayout) pageRoot.findViewById(R.id.swipelist_layout);
            ((SwipeRefreshLayout) page).addView(mRecyclerView);
            mRecyclerView.setHasFixedSize(true);//设置固定大小
            LinearLayoutManager bLinearLayoutManager = new LinearLayoutManager(this);
            bLinearLayoutManager.setOrientation(1);
            mRecyclerView.setLayoutManager(bLinearLayoutManager);
            ((SwipeRefreshLayout) page).setSize(SwipeRefreshLayout.DEFAULT);
            mSendTaskAdapter = new TaskAdapter(mSendTasks);
            mRecyclerView.setAdapter(mSendTaskAdapter);
            ((SwipeRefreshLayout) page).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new GetTaskThread().start();
                }
            });
        }
        new GetTaskThread().start();
    }
    
    private class GetTaskThread extends Thread{
        @Override
        public void run() {
            int userid = Utils.getIntPref(Utils.PREF_USERID, TaskActivity.this);
            String mUserID = "" + Utils.getIntPref(Utils.PREF_USERID, TaskActivity.this);
//            List<ERPTaskInstanceModel> list = new TaskCenter().GetModelViewList(" UserID=" + mUserID);//我下发的任务
            List<ERPTaskInstanceModel> list = new TaskInstance().GetMySendTaskInstances(userid);
            if (null != list && list.size() > 0){
                mSendTasks.clear();
                mSendTasks.addAll(list);
            }
//            List<ERPTaskInstanceModel> list2 = new TaskCenter().GetModelViewList(" ToUsersID like '%:" + mUserID + "}%'");//[{"to",129}]
            List<ERPTaskInstanceModel> list2 = new TaskInstance().GetMyReceiveTaskInstances(userid);
            if (null != list2 && list2.size() > 0){
                mGetTasks.clear();
                mGetTasks.addAll(list2);
            }
            //TODO 用户的名称
            mHandler.sendEmptyMessage(MSG_GET_END);
        }
    }
    
    private class TaskAdapter extends RecyclerView.Adapter{
        private List<ERPTaskInstanceModel> list;
    
        public TaskAdapter(List<ERPTaskInstanceModel> lis){
            list = lis;
        }
    
        @Override
        public int getItemCount() {
            return list.size();
        }
    
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            ERPTaskInstanceModel model = list.get(i);
            holder.fillData(model);
        }
    }
    
    protected class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleView;
        private TextView mContentView;
        ERPTaskInstanceModel mod;
        public MyViewHolder(View v) {
            super(v);
            mTitleView = (TextView) v.findViewById(R.id.txt_title);
            mContentView = (TextView) v.findViewById(R.id.txt_content);
            v.setOnClickListener(this);
        }

        public void fillData(ERPTaskInstanceModel model){
            mod = model;
//            mTitleView.setText("来自:"+model._OwnerName);
            getNameThreadStart(mTitleView, model._userid, mHandler);
            mContentView.setText(model._taskdetail);
            
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(TaskActivity.this, TaskGetDetailAct.class);
            intent.putExtra("ERPTaskInstanceModel", mod);
            startActivity(intent);
        }
    }
    
    public void getNameThreadStart(final TextView tView,final int userId, Handler handler){
        new Thread(new Runnable() {
			@Override
			public void run() {
				UserModel uModel = new WS_User().GetUserByID(userId);
				if (null != uModel){
					Message msg = mHandler.obtainMessage(MSG_GET_NAME_END);
					msg.obj = new NameViewHolder(tView, uModel._username);
					mHandler.sendMessage(msg);
				}
			}
		}).start();
    }
    
    public class NameViewHolder{
    	public TextView textView;
    	public String str;
    	
    	public NameViewHolder(TextView t, String s){
    		textView = t;
    		str = s;
    	}
    }
}