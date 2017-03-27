package com.ssiot.remote.yun.manage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPTaskModel;
import com.ssiot.remote.yun.webapi.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskPickAct extends HeadActivity{
    int cropid = -1;
	
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter mAdapter;
    List<ERPTaskModel> mDatas = new ArrayList<ERPTaskModel>();
    List<TaskBean> mStatusList = new ArrayList<TaskBean>();
    
    RecyclerView mRecyclerView;
    private static final int MSG_GET_END = 1;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
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
        setContentView(R.layout.activity_contacts);
        cropid = getIntent().getIntExtra("croptypeid", -1);
        
        findViewById(R.id.RadioButtonFarm).setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipelist_layout);
        mRecyclerView = ((RecyclerView)LayoutInflater.from(this).inflate(R.layout.vertical_recycler_view, null));
        swipeRefreshLayout.addView(mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager bLinearLayoutManager = new LinearLayoutManager(this);
        bLinearLayoutManager.setOrientation(1);
        mRecyclerView.setLayoutManager(bLinearLayoutManager);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetTasksThread().start();
            }
        });
        mAdapter = new MyAdapter(mDatas);
        mRecyclerView.setAdapter(mAdapter);
        
        initTitleBar();
        new GetTasksThread().start();
        
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
//                ArrayList<String> nameArray = new ArrayList<String>();
                ArrayList<Integer> idArray = new ArrayList<Integer>();
                for (int i = 0; i < mStatusList.size(); i ++){
                    TaskBean bean = mStatusList.get(i);
                    if (bean.isSelected){
//                        nameArray.add(bean.name);
                        idArray.add(bean.id);
                    }
                }
                intent.putIntegerArrayListExtra("extra_ids", idArray);
                setResult(RESULT_OK, intent);
                finish();
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
    
    private class GetTasksThread extends Thread{
        @Override
        public void run() {
            int parentid = Utils.getIntPref(Utils.PREF_PARENTID, TaskPickAct.this);
            int customerUserRootID = 0;
            if (parentid == 12){//是单位主帐号
                customerUserRootID = Utils.getIntPref(Utils.PREF_USERID, TaskPickAct.this);
            } else {//是子帐号
                customerUserRootID = parentid;
            }
            
            List<ERPTaskModel> users = new Task().GetTasksByCropID(customerUserRootID, cropid);
            if (null != users && users.size() > 0){
                mDatas.clear();
                mDatas.addAll(users);
                mStatusList.clear();
                for (int k =0; k < mDatas.size(); k ++){
                    ERPTaskModel model = mDatas.get(k);
                    mStatusList.add(new TaskBean(model._id, model._taskdetail,false));
                }
            }
            mHandler.sendEmptyMessage(MSG_GET_END);
        }
    }
    
    private class MyAdapter extends RecyclerView.Adapter{
        private List<ERPTaskModel> list;
        
        public MyAdapter(List<ERPTaskModel> lis){
            list = lis;
        }
        
        @Override
        public int getItemCount() {
            return list.size();
        }
        
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_task_check, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            ERPTaskModel model = list.get(i);
            boolean b = mStatusList.get(i).isSelected;
            holder.fillData(model,b);
        }
    }
    
    protected class MyViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        private ImageView imageViewIcon;
        private TextView mTitleView;
        private CheckBox mCkBox;
        ERPTaskModel mModel;
        public MyViewHolder(View v) {
            super(v);
            imageViewIcon = (ImageView) v.findViewById(R.id.imageViewIcon);
            mTitleView = (TextView) v.findViewById(R.id.textViewTask);
            mTitleView.setMaxLines(4);
            mCkBox = (CheckBox) v.findViewById(R.id.checkBoxSel);
            mCkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                
                @Override                
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mStatusList.get(getPosition()).isSelected = isChecked;
                }
            });
            v.setOnClickListener(this);
        }
        
        public void fillData(ERPTaskModel model,boolean isChecked){
            mModel = model;
            mTitleView.setText(model._taskdetail);
            
            mCkBox.setChecked(isChecked);
        }

        @Override
        public void onClick(View v) {
        }
        
    }
    
    public class TaskBean{
        public int id = 0;
        public String name = "";
        public boolean isSelected = false;
        
        public TaskBean(int uid, String nme, boolean isSelec){
            id = uid;
            name = nme;
            isSelected = isSelec;
        }
    }
}