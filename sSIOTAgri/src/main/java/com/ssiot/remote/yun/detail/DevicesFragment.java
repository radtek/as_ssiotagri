
package com.ssiot.remote.yun.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssiot.remote.BaseFragment;
import com.ssiot.agri.R;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.YunNodeModel;

import java.util.ArrayList;
import java.util.List;

abstract class DevicesFragment extends BaseFragment {// TODO
    private static final String tag = "DevicesFragment";
    private SwipeRefreshLayout refresh;
    public List<YunNodeModel> mYunNodeModels = new ArrayList<YunNodeModel>();//TODO 按home后activity被销毁时怎么重建整个app
    
    @Override
    public void onActivityCreated(Bundle paramBundle) {
        super.onActivityCreated(paramBundle);
        Log.v(tag, "----onActivityCreated----");
        Activity localFragmentActivity = getActivity();
        if (((localFragmentActivity instanceof SwipeRefreshLayout.OnRefreshListener))
                && (refresh != null)){
            refresh.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) localFragmentActivity);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent paramIntent){//TODO 不知是否有用
        super.onActivityResult(requestCode, resultCode, paramIntent);
        if ((requestCode == 3) && (resultCode == 3)) {
            Activity localFragmentActivity = getActivity();
            if ((localFragmentActivity instanceof SwipeRefreshLayout.OnRefreshListener))
                ((SwipeRefreshLayout.OnRefreshListener) localFragmentActivity).onRefresh();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        if (refresh == null) {
            Log.e(tag, "!!!!!!!onViewCreated--------SwipeRefreshLayout = null");
            refresh = new SwipeRefreshLayout(getActivity());
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    abstract int getTabID();
    abstract void add(@NonNull DeviceBean paramDevice);
    abstract int totalDevices();
    abstract void updateUI();

    static final boolean isUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public final void setRefreshing(boolean paramBoolean) {
        if (refresh != null) {
            refresh.setRefreshing(paramBoolean);
        } else {
            Log.e(tag, "!!!!!!!setRefreshing--------SwipeRefreshLayout = null" + getTabID());//说明还未创建就手动刷新
        }
    }
    
    public void setData(List<YunNodeModel> list){
        mYunNodeModels = list;
    }
}
