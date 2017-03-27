package com.ssiot.remote.expert;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.AdapterView;
import com.ssiot.fish.CellModel;
import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.ImageAdapter;
import com.ssiot.agri.R;
import com.ssiot.remote.BrowserActivity;
import com.ssiot.remote.Utils;
import com.ssiot.remote.expert.FacilityNodeListAct;

import java.util.ArrayList;

public class WaterAnalysisLauncherAct extends HeadActivity{
    private static final String tag = "WaterAnalysisLauncherAct";
    private GridView gridView1;
    ArrayList<CellModel> cells;
    private Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main_fish);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        initGridView();
    }
    
    private void initGridView(){
        cells = new ArrayList<CellModel>();
        cells.add(new CellModel(R.drawable.cell_shichangdongtai,"人工录入", "map_zhengwu"));
        cells.add(new CellModel(R.drawable.cell_shichangdongtai,"分析结果", "map_zhengwu"));
        ImageAdapter adapter = new ImageAdapter(this, cells);
        gridView1.setAdapter(adapter);
        gridView1.setSelector(new ColorDrawable(0));
        gridView1.setOnItemClickListener(gvAppListen);
    }
    
    AdapterView.OnItemClickListener gvAppListen = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (null != cells){
                CellModel model = cells.get(position);
                if ("人工录入".equals(model.itemText)){
                    Intent intent = new Intent(mContext, FacilityNodeListAct.class);
                    startActivity(intent);
                } else if ("分析结果".equals(model.itemText)){
                    Intent intent = new Intent(mContext, BrowserActivity.class);
                    String account = Utils.getStrPref(Utils.PREF_USERNAME, mContext);
                    intent.putExtra("url", "http://wap.fisher88.com/index.aspx?strAccount=" + account);
                    startActivity(intent);
                }
            }
        }
    };
}
