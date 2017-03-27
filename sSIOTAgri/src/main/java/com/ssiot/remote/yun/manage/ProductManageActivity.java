package com.ssiot.remote.yun.manage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ssiot.remote.BrowserActivity;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.yun.CellModel;
import com.ssiot.remote.yun.ImageAdapter;
import com.ssiot.remote.yun.manage.task.TaskActivity;

import java.util.ArrayList;

public class ProductManageActivity extends HeadActivity{
    private static final String tag = "ProductManageActivity";
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
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"生产批次", "map_batchlist"));
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"生产方案", "map_batchlist"));
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"任务模板", "map_batchlist"));
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"激活的任务", "map_batchlist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"肥料记录", "map_batchlist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"农药记录", "map_batchlist"));
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"投入品入库", "map_batchlist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"投入品出库", "map_batchlist"));//TODO 暂时不做
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"投入品使用", "map_batchlist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"生长记录", "map_batchlist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"病情记录", "map_batchlist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"采收入库", "map_batchlist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"采收包装", "map_batchlist"));
        
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"入库", "map_in"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"出库", "map_out"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"出入库列表", "map_productlist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"鱼药", "map_fishdrug"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"鱼药列表", "map_fishdruglist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"饲料", "map_fishdruglist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"饲料列表", "map_fishdruglist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"鱼苗", "map_fishdruglist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"鱼苗列表", "map_fishdruglist"));
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
                if ("生产批次".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_ProductBatch");
                    startActivity(i);
                } else if ("生产方案".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_ProductPlan");
                    startActivity(i);
                } else if ("任务模板".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_Task");
                    startActivity(i);
                } else if ("激活的任务".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, TaskActivity.class);
                    startActivity(i);
                } else if ("肥料记录".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_Fertilizer");
                    startActivity(i);
                } else if ("农药记录".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_Pesticides");
                    startActivity(i);
                } else if ("投入品入库".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_InputsIn");
                    startActivity(i);
                } else if ("投入品出库".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_InputsOut");
                    startActivity(i);
                } else if ("投入品使用".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_InputsLog");
                    startActivity(i);
                } else if ("生长记录".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_Growth");
                    startActivity(i);
                } else if ("病情记录".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_DiseaseLog");
                    startActivity(i);
                } else if ("采收入库".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_ProductsIn");
                    startActivity(i);
                } else if ("采收包装".equals(model.itemText)){
                    Intent i = new Intent(ProductManageActivity.this, ItemListAct.class);
                    i.putExtra("whichtable", "ERP_ProductsPack");
                    startActivity(i);
                }
                
//                else if ("入库".equals(model.itemText)){
//                    Intent i = new Intent(ProductManageActivity.this, ProductEditActivity.class);
//                    i.putExtra("isproductin", true);
//                    startActivity(i);
//                } else if ("出库".equals(model.itemText)){
//                    Intent intent = new Intent(ProductManageActivity.this, ProductEditActivity.class);
//                    intent.putExtra("isproductin", false);
//                    startActivity(intent);
//                } else if ("出入库列表".equals(model.itemText)){
//                    Intent intent = new Intent(ProductManageActivity.this, ProductListActivity.class);
//                    intent.putExtra("whichtable", "FishProduction");
//                    startActivity(intent);
//                } else if ("鱼药".equals(model.itemText)){
//                    Intent intent = new Intent(ProductManageActivity.this, FishEditActivity.class);
//                    intent.putExtra("edittable", "FishDrug");
//                    startActivity(intent);
//                } else if ("鱼药列表".equals(model.itemText)){
//                    Intent intent = new Intent(ProductManageActivity.this, ProductListActivity.class);
//                    intent.putExtra("whichtable", "FishDrug");
//                    startActivity(intent);
//                } else if ("饲料".equals(model.itemText)){
//                    Intent intent = new Intent(ProductManageActivity.this, FishEditActivity.class);
//                    intent.putExtra("edittable", "FishFeed");
//                    startActivity(intent);
//                } else if ("饲料列表".equals(model.itemText)){
//                    Intent intent = new Intent(ProductManageActivity.this, ProductListActivity.class);
//                    intent.putExtra("whichtable", "FishFeed");
//                    startActivity(intent);
//                } else if ("鱼苗".equals(model.itemText)){
//                    Intent intent = new Intent(ProductManageActivity.this, FishEditActivity.class);
//                    intent.putExtra("edittable", "FishSmall");
//                    startActivity(intent);
//                } else if ("鱼苗列表".equals(model.itemText)){
//                    Intent intent = new Intent(ProductManageActivity.this, ProductListActivity.class);
//                    intent.putExtra("whichtable", "FishSmall");
//                    startActivity(intent);
//                }
            }
        }
    };
}