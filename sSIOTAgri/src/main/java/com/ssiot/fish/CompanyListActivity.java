package com.ssiot.fish;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
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
import com.ssiot.remote.data.model.IOTCompanyModel;
import com.ssiot.remote.yun.webapi.WS_Fish;
import com.ssiot.remote.yun.webapi.WS_TraceProject;

import java.util.ArrayList;
import java.util.List;

public class CompanyListActivity extends HeadActivity{
    private static final String tag = "FishpondListActivity";
    
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter mAdapter;
    List<IOTCompanyModel> mDatas = new ArrayList<IOTCompanyModel>();
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
        setContentView(R.layout.activity_swiperefresh_list);
        
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipelist_layout);
        mRecyclerView = ((RecyclerView)LayoutInflater.from(this).inflate(R.layout.vertical_recycler_view, null));
        Log.v(tag, "----------------------");
        swipeRefreshLayout.addView(mRecyclerView);
        Log.v(tag, "------------------1");
        mRecyclerView.setHasFixedSize(true);
        Log.v(tag, "------------------2");
        LinearLayoutManager bLinearLayoutManager = new LinearLayoutManager(this);
        bLinearLayoutManager.setOrientation(1);
        mRecyclerView.setLayoutManager(bLinearLayoutManager);
        mRecyclerView.addItemDecoration(new ItemDivider(CompanyListActivity.this, R.drawable.myshape));
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetCompanyThread().start();
            }
        });
        mAdapter = new MyListAdapter(mDatas);
        setAdapter(mAdapter);
        
        new GetCompanyThread().start();
    }
    
    public final void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
    }
    
    private class GetCompanyThread extends Thread{
        @Override
        public void run() {
//            List<IOTCompanyModel> list = new IOTCompany().GetModelList(" 1=1");
            List<IOTCompanyModel> list = new WS_TraceProject().GetCompanys(1, 999, "1=1");
            if (null != list && list.size() > 0){
                mDatas.clear();
                mDatas.addAll(list);
            }
            mHandler.sendEmptyMessage(MSG_GET_END);
        }
    }
    
    private class MyListAdapter extends RecyclerView.Adapter{
        private List<IOTCompanyModel> list;
        
        public MyListAdapter(List<IOTCompanyModel> lis){
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
            IOTCompanyModel fishpondModel = list.get(i);
            holder.fillData(fishpondModel);
        }
    }
    
    protected class MyViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        private TextView mTitleView;
        private TextView mContentView;
        IOTCompanyModel mod;
        public MyViewHolder(View v) {
            super(v);
            mTitleView = (TextView) v.findViewById(R.id.txt_title);
            mContentView = (TextView) v.findViewById(R.id.txt_content);
            v.setOnClickListener(this);
        }
        
        public void fillData(IOTCompanyModel model){
            mod = model;
            mTitleView.setText(model._name);
            mContentView.setText(model._companyContent);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(CompanyListActivity.this, CompanyDetailActivity.class);
            intent.putExtra("company", mod);
            startActivity(intent);
        }
    }
    
    
    public class ItemDivider extends ItemDecoration {

        private Drawable mDrawable;

        public ItemDivider(Context context, int resId) {
            //在这里我们传入作为Divider的Drawable对象
            mDrawable = context.getResources().getDrawable(resId);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                //以下计算主要用来确定绘制的位置
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDrawable.getIntrinsicHeight();
                mDrawable.setBounds(left, top, right, bottom);
                mDrawable.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, int position, RecyclerView parent) {
            outRect.set(0, 0, 0, mDrawable.getIntrinsicWidth());
        }
    }
}