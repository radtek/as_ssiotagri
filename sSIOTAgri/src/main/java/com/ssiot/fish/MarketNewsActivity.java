package com.ssiot.fish;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ssiot.remote.BrowserActivity;
import com.ssiot.agri.R;

import java.util.ArrayList;

public class MarketNewsActivity extends HeadActivity{
    private static final String tag = "MarketNewsActivity";
    private GridView gridView1;
    ArrayList<CellModel> cells;
    private SharedPreferences mPref;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_main_fish);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        initGridView();
    }
    
    private void initGridView(){
        cells = new ArrayList<CellModel>();
        cells.add(new CellModel(R.drawable.cell_shuichanjiaoyi,"水产交易", "map_shuichanjiaoyi"));
        cells.add(new CellModel(R.drawable.cell_shuichanshangwu,"水产商务", "map_shuichanshangwu"));
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
                if ("水产交易".equals(model.itemText)){
                    Intent i = new Intent(MarketNewsActivity.this, BrowserActivity.class);
                    i.putExtra("url", "http://www.zgsc123.com");//中国水产网
                    startActivity(i);
                } else if ("水产商务".equals(model.itemText)){
                    Intent intent = new Intent(MarketNewsActivity.this, BrowserActivity.class);
                    intent.putExtra("url", "http://www.chinaaquatic.cn");//中国水产商务网
                    startActivity(intent);
                }
            }
        }
    };
}