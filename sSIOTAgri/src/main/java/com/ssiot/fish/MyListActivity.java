package com.ssiot.fish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.FishDrugModel;
import com.ssiot.remote.data.model.FishFeedModel;
import com.ssiot.remote.data.model.FishProductionModel;
import com.ssiot.remote.data.model.FishSmallModel;
import com.ssiot.remote.data.model.QuestionModel;

import java.util.ArrayList;
import java.util.List;

//一个基础activity类  基于RecyclerView
public class MyListActivity extends HeadActivity{
    private static final String tag = "MyListActivity";
    
    public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter mAdapter;
    public List<CustomModel> mDatas = new ArrayList<CustomModel>();
    RecyclerView mRecyclerView;
    SharedPreferences mPref;
    public int userid = 0;
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
        setContentView(R.layout.activity_swiperefresh_list);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        userid = mPref.getInt(Utils.PREF_USERID, 0);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipelist_layout);
        mRecyclerView = ((RecyclerView)LayoutInflater.from(this).inflate(R.layout.vertical_recycler_view, null));
        swipeRefreshLayout.addView(mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager bLinearLayoutManager = new LinearLayoutManager(this);
        bLinearLayoutManager.setOrientation(1);
        mRecyclerView.setLayoutManager(bLinearLayoutManager);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        
        
        mAdapter = new CustomAdapter(mDatas);
        setAdapter(mAdapter);
    }
    
    public final void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
    }
    
    public void sendRefreshInThread(){
        mHandler.sendEmptyMessage(MSG_GET_END);
    }
    
    private class CustomAdapter extends RecyclerView.Adapter{
        private List<CustomModel> list;
        
        public CustomAdapter(List<CustomModel> lis){
            list = lis;
        }
        
        @Override
        public int getItemCount() {
            return list.size();
        }
        
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, null);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            CustomViewHolder holder = (CustomViewHolder) viewHolder;
            CustomModel model = list.get(i);
            holder.fillData(model);
        }
    }
    
    //一个通用的View
    protected class CustomViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        private TextView mTitleView;
        private TextView mContentView;
        CustomModel fishpond;
        public CustomViewHolder(View v) {
            super(v);
            mTitleView = (TextView) v.findViewById(R.id.txt_title);
            mContentView = (TextView) v.findViewById(R.id.txt_content);
            v.setOnClickListener(this);
        }
        
        public void fillData(CustomModel model){
            fishpond = model;
            mTitleView.setText(model.title);
            mContentView.setText(model.content);
        }

        @Override
        public void onClick(View v) {
            //TODO
        }
    }
    
    public class CustomModel{
        private String title;
        private String content;
        public CustomModel(Object object) throws Exception{
            if (null != object){
                if (object instanceof FishProductionModel){
                    title = ((FishProductionModel) object)._name;
                    content = (((FishProductionModel) object)._isProductIn ? "入库" : "出库") +"数量:" + ((FishProductionModel) object)._amount;
                } else if (object instanceof FishDrugModel){
                    title = ((FishDrugModel) object)._name;
                    content = "数量:" + ((FishDrugModel) object)._amount;
                } else if (object instanceof FishFeedModel){
                    title = ((FishFeedModel) object)._name;
                    content = "数量:" + ((FishDrugModel) object)._amount;
                } else if (object instanceof FishSmallModel){
                    title = ((FishSmallModel) object)._name;
                    content = "数量:" + ((FishSmallModel) object)._amount;
                } else {
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }
        }
    }
}