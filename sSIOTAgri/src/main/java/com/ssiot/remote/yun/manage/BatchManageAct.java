package com.ssiot.remote.yun.manage;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ssiot.fish.CellModel;
import com.ssiot.fish.HeadActivity;
import com.ssiot.fish.ImageAdapter;
import com.ssiot.agri.R;
import com.ssiot.remote.data.model.ERPProductBatchModel;
import com.ssiot.remote.yun.manage.task.TaskActivity;

public class BatchManageAct extends HeadActivity{
	private GridView gridView1;
    ArrayList<CellModel> cells;
    private Context mContext;
    
    ERPProductBatchModel mBatchModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	mBatchModel = (ERPProductBatchModel) getIntent().getSerializableExtra("erp_productbatch");
    	if (mBatchModel == null){
    		showToast("数据出现问题,请重试");
    	}
    	mContext = this;
    	setContentView(R.layout.activity_main_fish);
    	gridView1 = (GridView) findViewById(R.id.gridView1);
        initGridView();
        getSupportActionBar().setTitle(mBatchModel._name);
    }
    
    private void initGridView(){
        cells = new ArrayList<CellModel>();
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"批次详细", "map_batchlist"));
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"投入品使用", "map_batchlist"));
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"生长记录", "map_batchlist"));
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"病情记录", "map_batchlist"));
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"采收入库", "map_batchlist"));
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"采收包装", "map_batchlist"));
//        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"本批次任务", "map_batchlist"));
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
                if ("批次详细".equals(model.itemText)){
                    Intent i = new Intent(mContext, ProductBatchDetailAct.class);
                    i.putExtra("erp_productbatch", mBatchModel);
                    startActivity(i);
                } else if ("投入品使用".equals(model.itemText)){
                    Intent i = new Intent(mContext, ItemListAct.class);
                    i.putExtra("erp_productbatch", mBatchModel);
                    i.putExtra("whichtable", "ERP_InputsLog");
                    startActivity(i);
                } else if ("生长记录".equals(model.itemText)){
                    Intent i = new Intent(mContext, ItemListAct.class);
                    i.putExtra("erp_productbatch", mBatchModel);
                    i.putExtra("whichtable", "ERP_Growth");
                    startActivity(i);
                } else if ("病情记录".equals(model.itemText)){
                    Intent i = new Intent(mContext, ItemListAct.class);
                    i.putExtra("erp_productbatch", mBatchModel);
                    i.putExtra("whichtable", "ERP_DiseaseLog");
                    startActivity(i);
                } else if ("采收入库".equals(model.itemText)){
                    Intent i = new Intent(mContext, ItemListAct.class);
                    i.putExtra("erp_productbatch", mBatchModel);
                    i.putExtra("whichtable", "ERP_ProductsIn");
                    startActivity(i);
                } else if ("采收包装".equals(model.itemText)){
                    Intent i = new Intent(mContext, ItemListAct.class);
                    i.putExtra("erp_productbatch", mBatchModel);
                    i.putExtra("whichtable", "ERP_ProductsPack");
                    startActivity(i);
                }
//                else if ("本批次任务".equals(model.itemText)){
//                    Intent i = new Intent(mContext, TaskActivity.class);
//                    i.putExtra("erp_productbatch", mBatchModel);
//                    startActivity(i);
//                }
            }
        }
    };
}