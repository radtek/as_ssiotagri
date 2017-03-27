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
import com.ssiot.remote.SsiotConfig;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPGrowthModel;
import com.ssiot.remote.data.model.ERPProductBatchModel;
import com.ssiot.remote.yun.manage.task.UploadFileBaseActivity;
import com.ssiot.remote.yun.webapi.ERPGrowth;
import com.ssiot.remote.yun.webapi.ERPPesticide;
import com.ssiot.remote.yun.webapi.ProductBatch;
import com.ssiot.remote.yun.webapi.WS_InputsOut;
import com.ssiot.remote.yun.webapi.WS_UserInputsType;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditGrowthAct extends UploadFileBaseActivity{
    private static final String tag = "EditGrowthAct";
    
    private int userId = 0;
    private int mProductBatchID = -1;
    private int typeid = -1;
    private int sexid;//01雌雄
    private String sexStr;
    private TextView mSexTextView;
    private TextView mBatchTextView;
    private EditText mLengthEdit;
    private TextView mTxtTime;
    
    private String batchText = "";
    private String mImgPath = "";
    private Dialog mWaitDialog;
    private static final String FTP_GROWTH_FOLDER = "/Upload/GrowthImage/";
    private static final String FTP_GROWTH_PATH = "cloud.ssiot.com" + FTP_GROWTH_FOLDER;
    
    private Timestamp mTimeStamp;
    private List<ERPProductBatchModel> mAllBatchsModel = new ArrayList<ERPProductBatchModel>();
    
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
        mProductBatchID = getIntent().getIntExtra("batchid", -1);
        setContentView(R.layout.act_growth_new);
        initViews();
        initTitleBar();
        new getAllBatchsThread().start();
    }
    
    private void initViews(){
        mBatchTextView = (TextView) findViewById(R.id.txt_productbatch);
        mSexTextView = (TextView) findViewById(R.id.txt_sex);
        mTxtTime = (TextView) findViewById(R.id.txt_time);
        mLengthEdit = (EditText) findViewById(R.id.edit_lenth);
    }
    
    private void initTitleBar(){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ERPGrowthModel model = new ERPGrowthModel();
                model._id = 0;
                model._batchid = mProductBatchID;
                float f = 0;
                try {
                    f = Float.parseFloat(mLengthEdit.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model._productlength = f;
                model._productweight = (float) calculateWeight(typeid, sexid, f);//公式计算，最好放在接口里 TODO 
                model._sex = sexid;
                model._createtime = mTimeStamp;
                model._image = mImgPath;
                if (model._productlength > 0 && model._batchid > 0 && null != mTimeStamp){
                    new Thread(new Runnable() {
                        public void run() {
                            int ret = new ERPGrowth().Save(model);
                            if (ret > 0){
                                mHandler.sendEmptyMessage(MSG_ADD_END);
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(EditGrowthAct.this, "请填写完整", Toast.LENGTH_SHORT).show();
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
        titleView.setText("生长记录");
    }
    
    public void ClickFunc(View v) {
        Log.v(tag, "--------ClickFunc-------" );
        switch (v.getId()) {
            case R.id.row_productbatch:
                showBatchPickDialog();
                break;
            case R.id.row_time:
                showDatePickerDialog();
                break;
            case R.id.row_sex:
            	showSexPickDialog();
            	break;
            case R.id.growth_img:
            	startCameraForResult();
            	break;

            default:
                break;
        }
    }
    
    public double calculateWeight(int type, int sexid, float length){
    	switch (type) {
		case 17://河蟹
			if (sexid == 0){
				return 6.007817 * 0.0001 * Math.pow(length * 10, 2.9725);//毫米-克 雌蟹：W=6.007817×10-4 L2.9725
			} else {
				return 1.399027 * 0.001 * Math.pow(length * 10, 2.7530);//雄蟹：W=1.399027×10-3 L2.7530			}
			}
		case 18:
			break;

		default:
			break;
		}
    	return 0;
    }
    
    private void showBatchPickDialog(){
        if (mAllBatchsModel.size() <= 0){
            Toast.makeText(this, "此用户无批次信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] types = new String[mAllBatchsModel.size()];
        for (int i = 0; i < mAllBatchsModel.size(); i ++){
            types[i] = mAllBatchsModel.get(i)._name + " 作物类型:" + mAllBatchsModel.get(i)._CropName;
        }
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProductBatchID = mAllBatchsModel.get(which)._id;
                typeid = mAllBatchsModel.get(which)._croptype;
                batchText = mAllBatchsModel.get(which)._name;
            }
        });
        bui.setTitle("批次").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBatchTextView.setText(batchText);
            }
        }).setNegativeButton(android.R.string.cancel, null);
        bui.create().show();
    }
    
    private void showSexPickDialog(){
        final String[] types = {"雌个体","雄个体"};
        AlertDialog.Builder bui = new AlertDialog.Builder(this);
        bui.setSingleChoiceItems(types, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sexid = which + 1;
                sexStr = types[which];
            }
        });
        bui.setTitle("选择雌雄").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSexTextView.setText(sexStr);
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
                    ImageView imageView = (ImageView) findViewById(R.id.growth_img);
                    imageView.setImageBitmap(bm);
                    saveImage(bm, spath);
                    mWaitDialog = Utils.createLoadingDialog(this, "上传中");
                    mWaitDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String remoteFileName = uploadimg(FTP_GROWTH_PATH, new File(spath), fileName);
                            mImgPath = FTP_GROWTH_FOLDER + remoteFileName;//20160826改为了全路径
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
    
    private class getAllBatchsThread extends Thread{
        @Override
        public void run() {
            List<ERPProductBatchModel> list = new ProductBatch().GetActiveProductBatch(userId);
            if (null != list && list.size() > 0){
                mAllBatchsModel.clear();
                mAllBatchsModel.addAll(list);
                mHandler.sendEmptyMessage(MSG_GET_TYPES_END);
            }
        }
    }
    
}