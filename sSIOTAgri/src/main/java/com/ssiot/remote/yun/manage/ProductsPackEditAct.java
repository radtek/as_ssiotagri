package com.ssiot.remote.yun.manage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.SsiotConfig;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.CompanyModel;
import com.ssiot.remote.data.model.ERPProductsInModel;
import com.ssiot.remote.data.model.ERPProductsPackModel;
import com.ssiot.remote.data.model.ERPProductBatchModel;
import com.ssiot.remote.data.model.NodeModel;
import com.ssiot.remote.yun.manage.task.UploadFileBaseActivity;
import com.ssiot.remote.yun.webapi.ERPFertilizer;
import com.ssiot.remote.yun.webapi.ProductBatch;
import com.ssiot.remote.yun.webapi.WS_InputsOut;
import com.ssiot.remote.yun.webapi.WS_ProductsIn;
import com.ssiot.remote.yun.webapi.WS_ProductsPack;
import com.ssiot.remote.yun.webapi.WS_TraceProject;
import com.ssiot.remote.yun.webapi.WS_UserInputsType;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductsPackEditAct extends UploadFileBaseActivity{
    private static final String tag = "EditFertilizerAct";
    
    private int userId = 0;
    int  mProductBatchID = -1;
    private int mProductInID = -1;
    private TextView mProductsInTextView;
    private EditText mNameEdit;
    private EditText mPackTypeEdit;
    private EditText mUnitEdit;
    private TextView mTxtTime;
    
    private String productsInText = "";
//    private String mTracCropStr = "";
    private String mCompanyStr = "";
    private String mNodeStr = "";
    
//    private int mTraceCropID = -1;
    private int mCompanyID = -1;
    private int mNodeNo = -1;
    private String mImgPath = "";
    private Timestamp mTimeStamp;
    private List<ERPProductsInModel> mAllProInModel = new ArrayList<ERPProductsInModel>();
    
    private List<CompanyModel> mCompanys = new ArrayList<CompanyModel>();
    private List<NodeModel> mNodes = new ArrayList<NodeModel>();
    
    private Dialog mWaitDialog;
    private static final String FTP_TRACEPROFILE_FOLDER = "/Upload/ProImage/";
    private static final String FTP_TRACEPROFILE_PATH = "cloud.ssiot.com" + FTP_TRACEPROFILE_FOLDER;
    
    private static final int MSG_ADD_END = 1;
    private static final int MSG_GET_TYPES_END = 3;
    private static final int MSG_FTPUPLOAD_END = 4;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_ADD_END:
                    finish();
                    break;
                case MSG_GET_TYPES_END:
                    break;
                case MSG_FTPUPLOAD_END:
                	break;
                default:
                    break;
            }
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        userId = Utils.getIntPref(Utils.PREF_USERID, this);
        mProductBatchID = getIntent().getIntExtra("batchid", -1);//此批次的此采收批次已经固定了
        setContentView(R.layout.activity_productspack_new);
        initViews();
        initTitleBar();
        new getAllProductsInThread().start();
        new getAllCompanysThread().start();
        new getAllNodesThread().start();
    }
    
    private void initViews(){
    	mProductsInTextView = (TextView) findViewById(R.id.txt_productsIn);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mPackTypeEdit = (EditText) findViewById(R.id.edit_packtype);
        mUnitEdit = (EditText) findViewById(R.id.edit_unit);
        mTxtTime = (TextView) findViewById(R.id.txt_time);
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	final String account = Utils.getStrPref(Utils.PREF_USERNAME, ProductsPackEditAct.this);
                final ERPProductsPackModel model = new ERPProductsPackModel();
                model._id = 0;
                model._productsinid = mProductInID;
                model._name = mNameEdit.getText().toString();
                model._packtypeid = mPackTypeEdit.getText().toString();//袋，捆，箱
                model._packunit = mUnitEdit.getText().toString();
                model._createtime = mTimeStamp;
                model._proimg = mImgPath;
                if (!TextUtils.isEmpty(model._name) && model._productsinid > 0 && null != mTimeStamp
                		 && mNodeNo > 0){//&& mCompanyID > 0
                    new Thread(new Runnable() {
                        public void run() {
//                            int ret = new WS_TraceProject().ProductsPackSave(model, mImgPath, mCompanyID, account, mNodeNo);
                        	int ret = new WS_ProductsPack().Save(model, mNodeNo, mCompanyID, account);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            } else {
                            	sendToast("保存失败");
                            }
                        }
                    }).start();
                } else {
                	sendToast("请填写完整");
                }
            }
        });
        TextView titleLeft = (TextView) findViewById(R.id.title_bar_left);
        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBackPressed();
                finish();
            }
        });
        
        TextView titleView = (TextView) findViewById(R.id.title_bar_title);
        titleView.setText("产品包装");
    }
    
    public void ClickFunc(View v) {
        Log.v(tag, "--------ClickFunc-------" );
        switch (v.getId()) {
            case R.id.row_productsIn:
                showProInPickDialog();
                break;
            case R.id.row_time:
                showDatePickerDialog();
                break;
            case R.id.trace_img:
            	startCameraForResult();
            	break;
//            case R.id.row_tracecroptype:
//            	showTraceCropPickDialog();
//            	break;
            case R.id.row_company:
            	showCompanyPickDialog();
            	break;
            case R.id.row_node:
            	showNodePickDialog();
            	break;

            default:
                break;
        }
    }
    
    
    private void startCameraForResult(){
    	Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }
    
    private void startGalleryForResult(){//TODO 
    	Intent intent = new Intent();    
        intent.setType("image/*");    
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMG);
    }
    
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_IMG = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.v(tag, "----onActivityResult------requestCode:" + requestCode);
    	if (resultCode == RESULT_OK){
    		if (requestCode == REQUEST_CAMERA){
    			Bundle bundle = data.getExtras();
                Bitmap bm = (Bitmap) bundle.get("data");
                if (bm != null){
                    bm.recycle();
                }
                bm = (Bitmap) data.getExtras().get("data");
                if(bm != null){
                    final String fileName = System.currentTimeMillis() + ".jpg";
                    final String spath = Environment.getExternalStorageDirectory()+ "/"+SsiotConfig.CACHE_DIR +"/"+
                            "tmp/" + fileName;
                    ImageView imageView = (ImageView) findViewById(R.id.trace_img);
                    imageView.setImageBitmap(bm);
                    saveImage(bm, spath);
                    mWaitDialog = Utils.createLoadingDialog(this, "上传中");
                    mWaitDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String remoteFileName = uploadimg(FTP_TRACEPROFILE_PATH, new File(spath), fileName);
                            mImgPath = "http://"+FTP_TRACEPROFILE_PATH + remoteFileName;//全路径
                            Message m = mHandler.obtainMessage(MSG_FTPUPLOAD_END);
                            m.obj = remoteFileName;
                            mHandler.sendMessage(m);
                            if (null != mWaitDialog && mWaitDialog.isShowing()){
                            	mWaitDialog.dismiss();
                            }
                        }
                    }).start();
                }
    		} else if (requestCode == REQUEST_IMG){
    			Uri uri = data.getData();
                Log.e("uri", uri.toString());
                ContentResolver cr = this.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
    		}
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void showProInPickDialog(){
        if (mAllProInModel.size() <= 0){
            Toast.makeText(this, "此用户无采收信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[mAllProInModel.size()];
        for (int i = 0; i < mAllProInModel.size(); i ++){
            types[i] = mAllProInModel.get(i)._ProductBatchName + " 采收时间:" + mAllProInModel.get(i)._createtime;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	mProductInID = mAllProInModel.get(which)._id;
            	productsInText = mAllProInModel.get(which)._ProductBatchName;
            }
        });
        bui.setTitle("采收批次").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProductsInTextView.setText(productsInText);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private void showDatePickerDialog(){
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dia_date_time_pick, null);
        final DatePicker dp = (DatePicker) view.findViewById(R.id.date_pick);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.time_pick);
        bui.setTitle("时间选择").setView(view).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = new Date(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
                mTimeStamp = new Timestamp(d.getTime());
                String str = formatter.format(d);
                mTxtTime.setText("时间:"+str);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
//    private void showTraceCropPickDialog(){
//        if (mTraceCropTypes.size() <= 0){
//            Toast.makeText(this, "此用户无溯源产品类型信息", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        final String[] types = new String[mTraceCropTypes.size()];
//        for (int i = 0; i < mTraceCropTypes.size(); i ++){
//            types[i] = mTraceCropTypes.get(i)._name;
//        }
//        AlertDialog.Builder bui = new AlertDialog.Builder(this);
//        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            	mTraceCropID = mTraceCropTypes.get(which)._id;
//            	mTracCropStr = mTraceCropTypes.get(which)._name;
//            }
//        });
//        bui.setTitle("溯源产品类型").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            	TextView traceCropText = (TextView) findViewById(R.id.txt_tracecrop);
//            	traceCropText.setText(mTracCropStr);
//            }
//        }).setNegativeButton(android.R.string.cancel, null);
//        bui.create().show();
//    }
    
    private void showCompanyPickDialog(){
        if (mCompanys.size() <= 0){
            Toast.makeText(this, "无公司信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[mCompanys.size()];
        for (int i = 0; i < mCompanys.size(); i ++){
            types[i] = mCompanys.get(i)._name;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	mCompanyID = mCompanys.get(which)._id;
            	mCompanyStr = mCompanys.get(which)._name;
            }
        });
        bui.setTitle("公司").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	TextView t = (TextView) findViewById(R.id.txt_company);
            	t.setText(mCompanyStr);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private void showNodePickDialog(){
        if (mNodes.size() <= 0){
            Toast.makeText(this, "无节点信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[mNodes.size()];
        for (int i = 0; i < mNodes.size(); i ++){
            types[i] = mNodes.get(i)._location;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	mNodeNo = mNodes.get(which)._nodeno;
            	mNodeStr = mNodes.get(which)._location;
            }
        });
        bui.setTitle("节点选择").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	TextView t = (TextView) findViewById(R.id.txt_node);
            	t.setText(mNodeStr);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private class getAllProductsInThread extends Thread{
        @Override
        public void run() {
        	List<ERPProductsInModel> list = new WS_ProductsIn().GetProductsIn("ProductBatchID=" + mProductBatchID);
        	if (null != list && list.size() > 0){
        		mProductInID = list.get(0)._id;
        	} else {
        		sendToast("异常，请重试");
        	}
//            List<ERPProductsInModel> list = new WS_ProductsIn().GetProductsIn(userId);
//            if (null != list && list.size() > 0){
//                mAllProInModel.clear();
//                mAllProInModel.addAll(list);
//                mHandler.sendEmptyMessage(MSG_GET_TYPES_END);
//            }
        }
    }
    
//    private class getAllTraceCropsThread extends Thread{
//    	@Override
//    	public void run() {
//    		String account = Utils.getStrPref(Utils.PREF_USERNAME, ProductsPackEditAct.this);
//    		List<PlantCropTypesModel> list = new WS_TraceProject().GetProCropTypes(account);
//    		if (null != list && list.size() > 0){
//    			mTraceCropTypes.clear();
//    			mTraceCropTypes.addAll(list);
//    		}
//    	}
//    }
    
    private class getAllCompanysThread extends Thread{
    	@Override
    	public void run() {
    		String account = Utils.getStrPref(Utils.PREF_USERNAME, ProductsPackEditAct.this);
    		List<CompanyModel> list = new WS_TraceProject().GetAllCompanys(account);
    		if (null != list && list.size() > 0){
    			mCompanys.clear();
    			mCompanys.addAll(list);
    		}
    	}
    }
    
    private class getAllNodesThread extends Thread{
    	@Override
    	public void run() {
    		String account = Utils.getStrPref(Utils.PREF_USERNAME, ProductsPackEditAct.this);
    		List<NodeModel> list = new WS_TraceProject().GetAllNodes(account);
    		if (null != list && list.size() > 0){
    			mNodes.clear();
    			mNodes.addAll(list);
    		}
    	}
    }
    
}