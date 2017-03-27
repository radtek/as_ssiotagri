package com.ssiot.remote.yun.manage;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.BrowserActivity;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPProductBatchModel;
import com.ssiot.remote.data.model.ERPProductsPackModel;
import com.ssiot.remote.yun.webapi.ProductBatch;
import com.ssiot.remote.yun.webapi.WS_ProductsPack;

public class ProductsPackDetailAct extends HeadActivity{
	private static final String tag = "ProductPackDetailAct";
	
	private ERPProductsPackModel mPackModel;
	TextView mBatchName;
	TextView mPackName;
	TextView mTypeView;
	TextView mFormatName;//规格
	TextView mDateName;
	
	TextView prodesc_name;
	TextView comdesc_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_product_pack_detail);
		mPackModel = (ERPProductsPackModel) getIntent().getSerializableExtra("erp_productspack");
		initViews();
		new GetBatchNameThread().start();
	}
	
	private void initViews(){
		mBatchName = (TextView) findViewById(R.id.batch_name);
		mPackName = (TextView) findViewById(R.id.productspack_name);
		mTypeView = (TextView) findViewById(R.id.croptype_name);
		mFormatName = (TextView) findViewById(R.id.format_name);
		mDateName = (TextView) findViewById(R.id.date_name);
		prodesc_name = (TextView) findViewById(R.id.prodesc_name);
		comdesc_name = (TextView) findViewById(R.id.comdesc_name);
		Button prodesc = (Button) findViewById(R.id.prodesc_edit);
		Button comdesc = (Button) findViewById(R.id.comdesc_edit);
		prodesc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDia(prodesc_name, "Prodesc");
			}
		});
		comdesc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDia(comdesc_name, "ProComDesc");
			}
		});
		
		mPackName.setText(mPackModel._name);
		mFormatName.setText(mPackModel._packunit + "/" + mPackModel._packtypeid);//5斤/盒
		mDateName.setText(Utils.formatTime(mPackModel._createtime));
		prodesc_name.setText(mPackModel._prodesc);
		Log.v(tag, "-------------mPackModel:" + mPackModel._comdesc);
		comdesc_name.setText(mPackModel._comdesc);
		
		TextView traceprofilecode_name = (TextView) findViewById(R.id.traceprofilecode_name);
		traceprofilecode_name.setText(mPackModel._qrcode);
		Button viewTrace = (Button) findViewById(R.id.trace_view);
		viewTrace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ProductsPackDetailAct.this, BrowserActivity.class);
				i.putExtra("url", "http://t.ssiot.com/?p=" + mPackModel._qrcode);
				startActivity(i);
			}
		});
	}
	
	private void showDia(final TextView tView, final String key){
		AlertDialog.Builder bui = new AlertDialog.Builder(this);
		final EditText edit = new EditText(this);
		bui.setView(edit);
		bui.setTitle("修改内容").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	final String str = edit.getText().toString();
            	tView.setText(str);
            	new Thread(new Runnable() {
					@Override
					public void run() {
						int ret = new WS_ProductsPack().Update(mPackModel._id, key, str);
						sendToast(ret > 0 ? "成功" : "失败");
					}
				}).start();
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
	}
	
	private class GetBatchNameThread extends Thread{
		@Override
		public void run() {
			final List<ERPProductBatchModel> list = new ProductBatch().GetProductBatch("ID=(select ProductBatchID from cms2016.dbo.ERP_ProductsIn where ID="+ mPackModel._productsinid +")");
			if (null != list && list.size() > 0){
				runOnUiThread(new Runnable() {
					public void run() {
						mBatchName.setText(list.get(0)._name);
						mTypeView.setText(list.get(0)._CropName);
					}
				});
				
			}
		}
	}
}