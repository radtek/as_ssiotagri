package com.ssiot.remote.yun.manage.task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
//import com.ssiot.remote.data.business.User;
//import com.ssiot.remote.data.business.UserGroup;
import com.ssiot.remote.data.model.UserGroupModel;
import com.ssiot.remote.data.model.UserModel;
import com.ssiot.remote.yun.webapi.UserGroup;
import com.ssiot.remote.yun.webapi.WS_User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TaskReceiverAct extends HeadActivity{
	private static final String tag = "TaskReceiverAct";
    
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter mAdapter;
    List<UserModel> mDatas = new ArrayList<UserModel>();
    List<ContactBean> mStatusList = new ArrayList<ContactBean>();
    
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
                new GetUsersThread().start();
            }
        });
        mAdapter = new MyAdapter(mDatas);
        mRecyclerView.setAdapter(mAdapter);
        
        initTitleBar();
        new GetUsersThread().start();
        
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ArrayList<String> nameArray = new ArrayList<String>();
                ArrayList<Integer> useridArray = new ArrayList<Integer>();
                for (int i = 0; i < mStatusList.size(); i ++){
                    ContactBean bean = mStatusList.get(i);
                    if (bean.isSelected){
                        nameArray.add(bean.name);
                        useridArray.add(bean.userid);
                    }
                }
                intent.putStringArrayListExtra("extra_names", nameArray);
                intent.putIntegerArrayListExtra("extra_userids", useridArray);
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
    
    private class GetUsersThread extends Thread{
        @Override
        public void run() {
            int parentid = Utils.getIntPref(Utils.PREF_MAIN_USERID, TaskReceiverAct.this);
            Log.v(tag, "--------parentid:" + parentid);
            int customerUserRootID = 0;
            if (parentid == 12){//是单位主帐号
                customerUserRootID = Utils.getIntPref(Utils.PREF_USERID, TaskReceiverAct.this);
            } else {//是子帐号
                customerUserRootID = parentid;
            }
            
            List<UserModel> users = new WS_User().GetAllGroupUsersByID(customerUserRootID);
            if (null != users && users.size() > 0){
                mDatas.clear();
                mDatas.addAll(users);
                mStatusList.clear();
                for (int k =0; k < mDatas.size(); k ++){
                    UserModel model = mDatas.get(k);
                    mStatusList.add(new ContactBean(model._userid, model._username,false));
                }
            }
            mHandler.sendEmptyMessage(MSG_GET_END);
        }
    }
    
    private class MyAdapter extends RecyclerView.Adapter{
        private List<UserModel> list;
        
        public MyAdapter(List<UserModel> lis){
            list = lis;
        }
        
        @Override
        public int getItemCount() {
            return list.size();
        }
        
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_check, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            UserModel model = list.get(i);
            boolean b = mStatusList.get(i).isSelected;
            holder.fillData(model,b);
        }
    }
    
    protected class MyViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        private ImageView imageViewIcon;
        private TextView mTitleView;
        private CheckBox mCkBox;
        UserModel mModel;
        public MyViewHolder(View v) {
            super(v);
            imageViewIcon = (ImageView) v.findViewById(R.id.imageViewIcon);
            mTitleView = (TextView) v.findViewById(R.id.textViewOrg);
            mCkBox = (CheckBox) v.findViewById(R.id.checkBoxSel);
            mCkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                
                @Override                
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mStatusList.get(getPosition()).isSelected = isChecked;
                }
            });
            v.setOnClickListener(this);
        }
        
        public void fillData(UserModel model,boolean isChecked){
            mModel = model;
            mTitleView.setText(model._username);
            
            mCkBox.setChecked(isChecked);
        }

        @Override
        public void onClick(View v) {
        }
        
    }
    
    public class ContactBean{
        public int userid = 0;
        public String name = "";
        public boolean isSelected = false;
        
        public ContactBean(int uid, String nme, boolean isSelec){
            userid = uid;
            name = nme;
            isSelected = isSelec;
        }
    }
}