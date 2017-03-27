package com.ssiot.remote;

import java.util.ArrayList;

import com.ssiot.remote.yun.CellModel;
import com.ssiot.remote.yun.ImageAdapter;
import com.ssiot.remote.yun.expert.ExpertSystemAct;
import com.ssiot.remote.yun.history.ProductHistoryAct;
import com.ssiot.remote.yun.manage.ProductManageActivity;
import com.ssiot.remote.yun.monitor.MonitorAct;
import com.ssiot.remote.yun.sta.StatisticsAct;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.ssiot.agri.R;

public class ExpertFragment extends Fragment{
    public static final String tag = "ExpertFragment";
    private FExpertBtnClickListener mFExpertBtnClickListener;
    ArrayList<CellModel> cells = new ArrayList<CellModel>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFExpertBtnClickListener = (FExpertBtnClickListener) getActivity();
        setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_expert, container, false);
//        TextView fishView = (TextView) v.findViewById(R.id.diagnose_fish);
//        fishView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mFExpertBtnClickListener){
//                    mFExpertBtnClickListener.onFExpertBtnClick();
//                }
//            }
//        });
        
        View v = inflater.inflate(R.layout.activity_main_fish, container, false);
        initGridView(v);
        return v;
    }
    
    private void initGridView(View rootView){
    	GridView gridView1;
    	gridView1 = (GridView) rootView.findViewById(R.id.gridView1);
    	cells.add(new CellModel(R.drawable.cell_diagnosefish,"鱼病诊断", "map_fishdiagnose"));
    	ImageAdapter adapter = new ImageAdapter(getActivity(), cells);
        gridView1.setAdapter(adapter);
        gridView1.setSelector(new ColorDrawable(0));
        gridView1.setOnItemClickListener(gvAppListen);
    }
    
    AdapterView.OnItemClickListener gvAppListen = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (null != cells){
                CellModel model = cells.get(position);
                if ("鱼病诊断".equals(model.itemText)){
                	if (null != mFExpertBtnClickListener){
                        mFExpertBtnClickListener.onFExpertBtnClick();
                    }
                }
            }
        }
    };
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_expert, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Log.v(tag, "----------------action-settting");
//                break;
//
//            default:
//                break;
//        }
        return true;
    }
    
    public void setClickListener(FExpertBtnClickListener listen){
        mFExpertBtnClickListener = listen;
    }
    
    //回调接口，留给activity使用
    public interface FExpertBtnClickListener {  
        void onFExpertBtnClick();  
    }
}