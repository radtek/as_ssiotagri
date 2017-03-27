package com.ssiot.remote.yun.manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPProductBatchModel;
import com.ssiot.remote.data.model.ERPProductPlanModel;
import com.ssiot.remote.data.model.ERPTaskModel;
import com.ssiot.remote.yun.webapi.ERPDiseaseLog;
import com.ssiot.remote.yun.webapi.ERPFertilizer;
import com.ssiot.remote.yun.webapi.ERPGrowth;
import com.ssiot.remote.yun.webapi.ERPInputsLog;
import com.ssiot.remote.yun.webapi.ERPPesticide;
import com.ssiot.remote.yun.webapi.ProductBatch;
import com.ssiot.remote.yun.webapi.ProductPlan;
import com.ssiot.remote.yun.webapi.Task;
import com.ssiot.remote.yun.webapi.WS_InputsIn;
import com.ssiot.remote.yun.webapi.WS_InputsOut;
import com.ssiot.remote.yun.webapi.WS_ProductsIn;
import com.ssiot.remote.yun.webapi.WS_ProductsPack;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemListAct extends MyListActivity{
    private static final String tag = "ItemListAct";
    private String tableName = "";
    int userId = -1;
    List<Serializable> modelList = new ArrayList<Serializable>();
    Intent clickIntent;
    String clickIntentTag;
    Intent editNewIntent;
    ERPProductBatchModel mBatchModel;//只有针对批次的部分列表有这个值
    ImageButton imageButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tableName = getIntent().getStringExtra("whichtable");
        mBatchModel = (ERPProductBatchModel) getIntent().getSerializableExtra("erp_productbatch");
        if (null == tableName){
            Log.e(tag, "------tableName = null !!!!");
            return;
        }
        userId = Utils.getIntPref(Utils.PREF_USERID, this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetProductThread().start();
            }
        });
        
        setKListener(new KItmClickListner() {
            @Override
            public void onKItemClick(int position) {
            	Log.v(tag, "-------onKItemClick-" + position);
                if (!TextUtils.isEmpty(tableName) && clickIntent != null){
                    clickIntent.putExtra(clickIntentTag, modelList.get(position));
                    startActivity(clickIntent);
                }
            }
        });
        setActionBarText();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	new GetProductThread().start();
    }
    
    private void setActionBarText(){
        ActionBar actionBar = getSupportActionBar();
        if (tableName.equals("ERP_ProductBatch")){
            actionBar.setTitle("批次列表");
            clickIntent = new Intent(ItemListAct.this, BatchManageAct.class);//ProductBatchDetailAct
            clickIntentTag = "erp_productbatch";
            editNewIntent = new Intent(this, BatchEditAct.class);
        } else if (tableName.equals("ERP_ProductPlan")){
            actionBar.setTitle("方案列表");
            clickIntent = new Intent(ItemListAct.this, ProductPlanDetailAct.class);
            clickIntentTag = "erp_productplan";
            editNewIntent = new Intent(this, EditProductPlanAct.class);
        } else if (tableName.equals("ERP_Task")){
            actionBar.setTitle("任务模板列表");
//            clickIntent = new Intent(ItemListAct.this, TaskDetailAct.class);
//            clickIntentTag = "erp_productplan";
            editNewIntent = new Intent(this, EditTaskMouldAct.class);
        }
//        else if (tableName.equals("已激活的任务列表")){
//            actionBar.setTitle("已激活的任务列表");
//            clickIntent = new Intent(ItemListAct.this, ProductPlanDetailAct.class);
//            clickIntentTag = "erp_productplan";
//        }
        else if (tableName.equals("ERP_Fertilizer")){
            actionBar.setTitle("肥料记录");
            editNewIntent = new Intent(this, EditFertilizerAct.class);
        } else if (tableName.equals("ERP_Pesticides")){
            actionBar.setTitle("农药记录");
            editNewIntent = new Intent(this, EditPesticideAct.class);
        } else if (tableName.equals("ERP_InputsIn")){
            actionBar.setTitle("投入品入库");
            editNewIntent = new Intent(this, InputsInEditAct.class);
        } else if (tableName.equals("ERP_InputsOut")){
            actionBar.setTitle("投入品出库");
            editNewIntent = new Intent(this, InputsOutEditAct.class);
        } else if (tableName.equals("ERP_InputsLog")){
            actionBar.setTitle("使用记录");
            editNewIntent = new Intent(this, EditInputsLogAct.class);
        } else if (tableName.equals("ERP_Growth")){
            actionBar.setTitle("生长记录");
            editNewIntent = new Intent(this, EditGrowthAct.class);
        } else if (tableName.equals("ERP_DiseaseLog")){
            actionBar.setTitle("病情记录");
            editNewIntent = new Intent(this, EditDiseaseLogAct.class);
        } else if (tableName.equals("ERP_ProductsIn")){
            actionBar.setTitle("采收入库");
            if (null != mBatchModel && false == mBatchModel._isfinish){//本批次未采收才显示edit
            	editNewIntent = new Intent(this, ProductsInEditAct.class);
            }
        } else if (tableName.equals("ERP_ProductsPack")){
            actionBar.setTitle("产品包装");
            editNewIntent = new Intent(this, ProductsPackEditAct.class);
            
            clickIntent = new Intent(ItemListAct.this, ProductsPackDetailAct.class);
            clickIntentTag = "erp_productspack";
        }
        
        if (null != editNewIntent){
        	if (null != mBatchModel){
            	editNewIntent.putExtra("batchid", mBatchModel._id);
            	editNewIntent.putExtra("erp_productbatch", mBatchModel);
            }
            addEditButton();
        }
    }
    
    private void addEditButton(){
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.swipe_relativelayout);
        imageButton = new ImageButton(this);
        imageButton.setBackgroundResource(R.drawable.question_new_select);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rl.setMargins(0, 0, 50, 50);
        rootView.addView(imageButton, rl);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(tableName) && editNewIntent != null){
                    startActivity(editNewIntent);
                }
            }
        });
    }
    
    private class GetProductThread extends Thread{
        @Override
        public void run() {
            List list = null;
            if (null != mBatchModel){
            	List<ERPProductBatchModel> batches = new ProductBatch().GetProductBatch("ID=" + mBatchModel._id);//
            	if (null != batches && batches.size() > 0){
            		mBatchModel = batches.get(0);//在保存采收时 batch表会更新IsFinish，所以要重新查询
            	}
            }
            
            if (!TextUtils.isEmpty(tableName)){
                if (tableName.equals("ERP_ProductBatch")){
                    list = new ProductBatch().GetProductBatch(userId);
                } else if (tableName.equals("ERP_ProductPlan")){
                    list = new ProductPlan().GetProductPlan(userId);
                } else if (tableName.equals("ERP_Task")){
                    list = new Task().GetTasks(userId);
                } else if (tableName.equals("ERP_Fertilizer")){
                    list = new ERPFertilizer().GetFertilizer(userId);
                } else if (tableName.equals("ERP_Pesticides")){
                    list = new ERPPesticide().GetPesticide(userId);
                } else if (tableName.equals("ERP_InputsIn")){
                    list = new WS_InputsIn().GetInputsIn(userId);
                } else if (tableName.equals("ERP_InputsOut")){
                    list = new WS_InputsOut().GetInputsOut(userId);
                } else if (tableName.equals("ERP_InputsLog")){//一个批次下的记录列表
                	if (null != mBatchModel){
                		list = new ERPInputsLog().GetInputsLogByBatch(mBatchModel._id);
                	}
                } else if (tableName.equals("ERP_Growth")){
                	if (null != mBatchModel){
                		list = new ERPGrowth().GetGrowthByBatch(mBatchModel._id);
                	}
                } else if (tableName.equals("ERP_DiseaseLog")){
                	if (null != mBatchModel){
                		list = new ERPDiseaseLog().GetDiseaseLogByBatch(mBatchModel._id);
                	}
                } else if (tableName.equals("ERP_ProductsIn")){
                	if (null != mBatchModel){
                		list = new WS_ProductsIn().GetProductsIn("ProductBatchID=" + mBatchModel._id);
                		imageButton.setVisibility(mBatchModel._isfinish ? View.GONE : View.VISIBLE);
                	}
                } else if (tableName.equals("ERP_ProductsPack")){
                	if (null != mBatchModel){
                		list = new WS_ProductsPack().GetProductsPackByBatch(mBatchModel._id);
                	}
                }
            }
            
            if (null != list && list.size() > 0){
                mDatas.clear();
                List<CustomModel> customModels = new ArrayList<CustomModel>();
                for (int i = 0; i < list.size(); i ++){
                    try {
                        Object o = list.get(0);
                        if (o instanceof CustomModel.GetCustomShowInterface){
                            customModels.add(new CustomModel((CustomModel.GetCustomShowInterface) list.get(i)));
                        } else {
                            Log.e(tag, "------class type error !!!!");
                        }
                        
//                        if (list.get(i) instanceof ERPProductBatchModel){
//                            ERPProductBatchModel b = (ERPProductBatchModel) list.get(i);
//                            customModels.add(new CustomModel(b));
//                        } else if (list.get(i) instanceof ERPProductPlanModel){
//                            ERPProductPlanModel b = (ERPProductPlanModel) list.get(i);
//                            customModels.add(new CustomModel(b._name, "方案归属:"+b._owenerid));
//                        } else if (list.get(i) instanceof ERPTaskModel){
//                            ERPTaskModel b = (ERPTaskModel) list.get(i);
//                            customModels.add(new CustomModel(b._taskdetail, "阶段:"+b._stagetype));
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mDatas.addAll(customModels);
                modelList.clear();
                modelList.addAll(list);
            }
            sendRefreshInThread();
        }
    }
}