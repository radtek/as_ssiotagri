package com.ssiot.fish;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.ssiot.fish.facility.GetLocationActivity;
import com.ssiot.fish.question.QuestionListActivity;
import com.ssiot.remote.BrowserActivity;
import com.ssiot.remote.FirstStartActivity;
import com.ssiot.remote.LoginActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.SettingFrag;
import com.ssiot.remote.SsiotService;
import com.ssiot.remote.UpdateManager;
import com.ssiot.remote.Utils;
import com.ssiot.remote.aliyun.MsgListAct;
import com.ssiot.remote.aliyun.TxtAct;
import com.ssiot.remote.expert.DiagnoseFishSelectActivity;
import com.ssiot.remote.expert.FacilityNodeListAct;
import com.ssiot.remote.expert.WaterAnalysisLauncherAct;
import com.ssiot.remote.expert.WaterColorDiagnoseAct;
import com.ssiot.remote.history.HistoryAct;
import com.ssiot.remote.myzxing.MipcaActivityCapture;
import com.ssiot.remote.weather.AliYunWeatherAct;
import com.ssiot.remote.weather.JuheWeatherAct;
import com.ssiot.remote.weather.WeatherUtils;
import com.ssiot.remote.yun.admin.DeviceRegistAct;
import com.ssiot.remote.yun.admin.FacilityEditAct;
import com.ssiot.remote.yun.admin.FacilityManageAct;
import com.ssiot.remote.yun.admin.LandEditAct;
import com.ssiot.remote.yun.monitor.DeviceBean;
import com.ssiot.remote.yun.monitor.MonitorAct;
import com.ssiot.remote.yun.monitor.TintedBitmapDrawable;
import com.ssiot.remote.yun.sta.StatisticsAct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class FishMainActivity extends HeadActivity{
    private static final String tag = "FishMainActivity";
    ArrayList<CellModel> cells;
    private SharedPreferences mPref;
    
    private UpdateManager mUpdateManager;
    private Notification mNoti;
    private Context mContext;
    
    TextView locationText;
    ImageView weatherImg;
    TextView weatherText;
    TextView airText;
    ImageView locationBtn;
    
    
    private static final int MSG_LOCATION_GOT = 1;
    private Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case MSG_LOCATION_GOT:
				BDLocation location = (BDLocation) msg.obj;
				new GetWeatherThread(""+location.getLatitude(), ""+location.getLongitude()).start();
				break;

			default:
				break;
			}
    	};
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        appInitCheck();
        mContext = this;
        hideActionBar();
        setContentView(R.layout.activity_home);
        startService(new Intent(this, SsiotService.class));
        initIconTitleBar();
        initWeatherBar();
        initMain();
        new GetBDLocationUtil().start();
        
        if (mPref.getBoolean(Utils.PREF_AUTOUPDATE, true) == true){
            mUpdateManager = new UpdateManager(this);
            mUpdateManager.startGetRemoteVer();
        }
        DeviceBean.updateunit();
		
		String account = Utils.getStrPref(Utils.PREF_USERNAME, this);
        if ("guizhouchaye".equals(account)){
			Utils.offlineSeconds = 3600 * 25;//贵州茶叶帐号离线设置为25小时
		}
    }
    
    private void initIconTitleBar(){
		initTitleLeft(R.id.title_bar_left);
		View addIcon = findViewById(R.id.title_bar_menu_icon);
		addIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopMenu(v);
			}
		});
	}
    
    private void initMain(){
    	LinearLayout linear1 = (LinearLayout) findViewById(R.id.linear_1);
    	LinearLayout linear2 = (LinearLayout) findViewById(R.id.linear_2);
    	LinearLayout linear3 = (LinearLayout) findViewById(R.id.linear_3);
    	LinearLayout linear4 = (LinearLayout) findViewById(R.id.linear_4);
    	addToLinearAndClick(linear1, R.drawable.cell_cekong,"管控中心");
    	addToLinearAndClick(linear1, R.drawable.cell_data,"数据中心");
    	addToLinearAndClick(linear1, R.drawable.cell_weather,"气象信息");
    	addToLinearAndClick(linear1, R.drawable.cell_weather,"天气预报");
    	addToLinearAndClick(linear2, R.drawable.cell_video,"视频监控");
    	addToLinearAndClick(linear2, R.drawable.cell_crop_diagnose,"病害诊断");
    	addToLinearAndClick(linear3, R.drawable.cell_shenchanguanli,"生产管理");
    	addToLinearAndClick(linear3, R.drawable.cell_landfacility_manage,"设施管理");
    	addToLinearAndClick(linear3, R.drawable.cell_trace,"溯源信息");
    	addToLinearAndClick(linear4, R.drawable.cell_help,"使用帮助");
    	addToLinearAndClick(linear4, R.drawable.cell_shichangdongtai,"消息记录");

        if ("gn".equalsIgnoreCase(mPref.getString(Utils.PREF_USERNAME, ""))){
        	addToLinearAndClick(linear3, R.drawable.cell_wuzijiaoyi,"灌南电商");
         }
    }
    
    private void initWeatherBar(){
    	locationText = (TextView) findViewById(R.id.txt_location);
    	weatherImg = (ImageView) findViewById(R.id.img_weather);
    	weatherText = (TextView) findViewById(R.id.txt_weather);
    	airText = (TextView) findViewById(R.id.txt_air);
    	locationBtn = (ImageView) findViewById(R.id.img_location_btn);
    	
    	weatherImg.setImageDrawable(new TintedBitmapDrawable(getResources(), 
    			R.drawable.notification_weather_sunny_small, R.color.black));
    	
    	locationBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(FishMainActivity.this, GetLocationActivity.class);//TODO
				startActivity(i);
			}
		});
    }
    

    
    private void startLoginUI() {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(FishMainActivity.this, LoginActivity.class);
		        startActivity(intent);
		        finish();
			}
		}).start();
    }
    
	public String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}
	
	private boolean isVisitorMode(){//游客登录有很多限制
		String pwd = Utils.getStrPref(Utils.PREF_PWD, this);
		if (TextUtils.isEmpty(pwd)){
			return true;
		}else {
			return false;
		}
	}

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        getMenuInflater().inflate(R.menu.main, menu);
//        getMenuInflater().inflate(R.menu.menu_f_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
////            case R.id.action_logout:
////                Intent loginIntent = new Intent(FishMainActivity.this, LoginActivity.class);
////                startActivity(loginIntent);
////                SsiotService.cancel = true;
////                stopService(new Intent(this, SsiotService.class));
////                finish();
////                Editor e = mPref.edit();
////                e.putString(Utils.PREF_PWD, "");
////                e.commit();
////                return true;
//            case R.id.action_frag_main_setting:
//            	if (isVisitorMode()){
//            		showToast("游客无权限查看此模块，请登录");
//            		startLoginUI();
//            		return super.onOptionsItemSelected(item);
//            	}
//                Intent intent = new Intent(FishMainActivity.this, SettingActivity.class);
//                startActivityForResult(intent, REQUEST_SETTING_OUT);
//                break;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    
    private static final int REQUEST_SETTING_OUT = 1;
    private static final int REQUEST_ADDDEVICE = 3;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case REQUEST_SETTING_OUT:
				if (resultCode == RESULT_OK) {// 用onActivityResult 目地是同时销毁两个activity
					Intent loginIntent = new Intent(FishMainActivity.this,
							LoginActivity.class);
					startActivity(loginIntent);
					SsiotService.cancel = true;
					stopService(new Intent(this, SsiotService.class));
					finish();
					Editor e = mPref.edit();
					e.putString(Utils.PREF_PWD, "");
					e.commit();
				}
				break;
			case REQUEST_ADDDEVICE:
				Intent i = new Intent(this, DeviceRegistAct.class);
				String qrStr = data.getStringExtra("qrcode");

                int beginIndex = qrStr.indexOf("=");
//                qrStr.indexOf("&");
                if (beginIndex > 0 && qrStr.indexOf("http://wap.farmer8.com/device.aspx?did=") >= 0) {
                    String serialno = qrStr.substring(beginIndex + 1, qrStr.length());
                    i.putExtra("serialno", serialno);
                    startActivity(i);
                } else {
                    Toast.makeText(this,"二维码不正确 " + qrStr,Toast.LENGTH_SHORT).show();
                }

				break;

			default:
				break;
			}
		}
    }
    
    private void showUpdateChoseDialog(HashMap<String, String> mVerMap){
        final HashMap<String, String> tmpMap = mVerMap;
        AlertDialog.Builder builder =new Builder(this);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        builder.setPositiveButton(R.string.soft_update_updatebtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNoti = mUpdateManager.showNotification(FishMainActivity.this);
//                        .setProgressBar(R.id.noti_progress, 100, 0, false);
                mUpdateManager.startDownLoad(tmpMap);
//                showDownloadDialog(tmpMap);
                dialog.dismiss();
                Editor e = mPref.edit();
                e.putBoolean(Utils.PREF_AUTOUPDATE, true);
                e.commit();
                Toast.makeText(FishMainActivity.this, "转向后台下载，可在通知栏中查看进度。", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.soft_update_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editor e = mPref.edit();
                e.putBoolean(Utils.PREF_AUTOUPDATE, false);
                e.commit();
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }
    
//    @Override
//    public void onFSettingBtnClick() {
//        if (mUpdateManager == null){
//            mUpdateManager = new UpdateManager(FishMainActivity.this, mHandler);
//        }
//        if (mPref.getBoolean(Utils.PREF_AUTOUPDATE, true) == false){
//            Editor editor = mPref.edit();
//            editor.putBoolean(Utils.PREF_AUTOUPDATE, true);
//            editor.commit();
//        }
//        mUpdateManager.startGetRemoteVer();
//    }
    
    private void appInitCheck(){
//    	mPref = PreferenceManager.getDefaultSharedPreferences(this);
    	checkPrefVersion();
    }
    
    private void checkPrefVersion(){//版本更新后，有些pref以前没有。。如userkey
        try {
            int appVerCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            int prefVerCode = Utils.getIntPref("versioncode", this);
//            String vername = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;//测试版的临时做法 TODO
//            String prefname = Utils.getStrPref("versioname", this);
            if (appVerCode != prefVerCode ){//|| !vername.equalsIgnoreCase(prefname)
                Editor e = mPref.edit();
                e.clear().commit();//清除整个pref
                e.putInt("versioncode", appVerCode);
                e.commit();
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void addToLinearAndClick(LinearLayout linear, int iconRes, String itemText){
        View paramView = getLayoutInflater().inflate(R.layout.layout_gridview_item, null);
//        HashMap localHashMap = (HashMap) this.localArrayList.get(paramInt);
        CellModel cellModel = new CellModel(iconRes, itemText, "");
        ImageView localImageView1 = (ImageView) paramView.findViewById(R.id.imageView_ItemImage);
        RelativeLayout localRelativeLayout = (RelativeLayout) paramView.findViewById(R.id.relativeLayout2);
        TextView localTextView = (TextView) paramView.findViewById(R.id.textView_ItemText);
        ViewGroup.LayoutParams localLayoutParams1 = localRelativeLayout.getLayoutParams();
        localLayoutParams1.height = (10 + HeadActivity.width / 7);
        localLayoutParams1.width = localLayoutParams1.height;
        localRelativeLayout.setLayoutParams(localLayoutParams1);
        ViewGroup.LayoutParams localLayoutParams2 = localImageView1.getLayoutParams();
        localLayoutParams2.height = (HeadActivity.width / 7);
        localLayoutParams2.width = localLayoutParams2.height;
        localImageView1.setLayoutParams(localLayoutParams2);
//        localImageView1.setImageResource(((Integer) localHashMap.get("itemImage")).intValue());
//        localTextView.setText((String) localHashMap.get("itemText"));
        localImageView1.setImageResource(cellModel.itemImage);
        localTextView.setText(cellModel.itemText);
        localTextView.setSingleLine(true);
        localTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        paramView.setOnClickListener(new IconClickListener(cellModel));
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(      
                LinearLayout.LayoutParams.WRAP_CONTENT,      
                LinearLayout.LayoutParams.WRAP_CONTENT      
        ); 
        llp.setMargins(30, 10, 30, 20);
        linear.addView(paramView, llp);
    }
    
    public class IconClickListener implements View.OnClickListener{
    	CellModel mCellModel;
    	private IconClickListener(CellModel c){
    		mCellModel = c;
    	}
    	@Override
    	public void onClick(View v) {
    		CellModel model = mCellModel;
    		if (isVisitorMode()){
        		showToast("请登录");
        		startLoginUI();
        		return;
        	}
            if ("管控中心".equals(model.itemText)){
                Intent intent = new Intent(mContext, MonitorAct.class);
                startActivity(intent);
            } else if ("视频监控".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, VideoListActivity.class);
                startActivity(intent);
            } else if ("数据中心".equals(model.itemText)){
            	Intent intent = new Intent(mContext, StatisticsAct.class);
            	startActivity(intent);
            } else if ("互动交流".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, QuestionListActivity.class);
                intent.putExtra("isexpertmode", false);
                startActivity(intent);
            } else if ("病害诊断".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, DiagnoseFishSelectActivity.class);
                startActivity(intent);
            } else if ("水色诊断".equals(model.itemText)){
            	Intent intent = new Intent(FishMainActivity.this, WaterColorDiagnoseAct.class);
            	startActivity(intent);
            } else if ("专家在线".equals(model.itemText)){//专家在线只是问题类别不同
                Intent intent = new Intent(FishMainActivity.this, QuestionListActivity.class);
                intent.putExtra("isexpertmode", true);
                startActivity(intent);
            } else if ("生产管理".equals(model.itemText)){//TODO 三代界面之后是这个生产管理界面
                Intent intent = new Intent(FishMainActivity.this, com.ssiot.remote.yun.manage.ProductManageActivity.class);
                startActivity(intent);
            } else if ("设施管理".equals(model.itemText)){
            	Intent intent = new Intent(FishMainActivity.this, FacilityManageAct.class);
            	startActivity(intent);
            } else if ("溯源信息".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, HistoryAct.class);
                startActivity(intent);
            } else if ("物资交易".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, BrowserActivity.class);
				intent.putExtra("url", "http://wapcart.fisher88.com");
                //intent.putExtra("url", "http://gn.ssiot.com/mobile2/index.html");
                startActivity(intent);
            } else if ("企业汇总".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, CompanyListActivity.class);
                startActivity(intent);
            } else if ("市场动态".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, MarketNewsActivity.class);
                startActivity(intent);
            } else if ("消息记录".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, MsgListAct.class);//MsgListAct
                startActivity(intent);
            } else if ("水质分析".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, WaterAnalysisLauncherAct.class);
                startActivity(intent);
            } else if ("气象信息".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, WeatherLaunchAct.class);
                startActivity(intent);
            } else if ("天气预报".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, AliYunWeatherAct.class);//JuheWeatherAct
                startActivity(intent);
            } else if ("技术资讯".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, ArticleListAct.class);
                startActivity(intent);
            } else if ("使用帮助".equals(model.itemText)){
                Intent intent = new Intent(FishMainActivity.this, BrowserActivity.class);
                InputStream is =getResources().openRawResource(R.raw.app_help);
                String url = "file:///android_asset/app_help.html";//文件的内容必须要是UTF-8的
                try {
					String str = inputStream2String(is);//中文乱码？？
//					intent.putExtra("msg", str);
//					startActivity(intent);
					intent.putExtra("url", url);
//					intent.putExtra("data", str);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
    	}
    }
    
    public class GetBDLocationUtil{
    	MyLocationListener mMyLocationListener = new MyLocationListener();
    	LocationClient mLocationClient = null;
    	public GetBDLocationUtil(){
    		
    	}
    	public void start() {
    		mLocationClient = new LocationClient(getApplicationContext());
            mLocationClient.setLocOption(setLocationClientOption());
            mLocationClient.registerLocationListener(mMyLocationListener);
            mLocationClient.start();
    	}
    	
    	public class MyLocationListener implements BDLocationListener {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location==null) {
                    return;
                }
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                LatLng latlng = new LatLng(lat, lng);
                //定位点地址信息做非空判断
                String strLocationProvince;//定位点的省份
                String strLocationCity;//定位点的城市
                String strLocationDistrict;//定位点的区县
                String strLocationStreet;//定位点的街道信息
                String strLocationStreetNumber;//定位点的街道号码
                String strLocationAddrStr;//定位点的详细地址(包括国家和以上省市区等信息)
                if ("".equals(location.getProvince())) {
                    strLocationProvince = "未知省";
                }else {
                    strLocationProvince = location.getProvince();
                }
                if ("".equals(location.getCity())) {
                    strLocationCity = "未知市";
                }else {
                    strLocationCity = location.getCity();
                }
                if ("".equals(location.getDistrict())) {
                    strLocationDistrict = "未知区";
                }else {
                    strLocationDistrict = location.getDistrict();
                }
                if ("".equals(location.getStreet())) {
                    strLocationStreet = "未知街道";
                }else {
                    strLocationStreet = location.getStreet();
                }
                if ("".equals(location.getStreetNumber())) {
                    strLocationStreetNumber = "";
                }else {
                    strLocationStreetNumber =location.getStreetNumber();
                }
                if ("".equals(location.getAddrStr())) {
                    strLocationAddrStr = "";
                }else {
                    strLocationAddrStr =location.getAddrStr();
                }
                //定位成功后对获取的数据依据需求自定义处理，这里只做log显示
                Log.d("tag", "latlng.lat="+lat);
                Log.d("tag", "latlng.lng="+lng);
                Log.d("tag", "strLocationProvince="+strLocationProvince);
                Log.d("tag", "strLocationCity="+strLocationCity);
                Log.d("tag", "strLocationDistrict="+strLocationDistrict);

                // 到此定位成功，没有必要反复定位
                // 应该停止客户端再发送定位请求
                if (mLocationClient.isStarted()) {
                    Log.d("tag", "mLocationClient.isStarted()==>mLocationClient.stop()");
                    mLocationClient.stop();
                }
                Message msg = new Message();
                msg.what = MSG_LOCATION_GOT;
                msg.obj = location;
                mHandler.sendMessage(msg);
            }
        }
    	
    	private LocationClientOption setLocationClientOption() {
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            option.setScanSpan(1000);//每隔1秒发起一次定位
            option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
            option.setOpenGps(true);//是否打开gps
            option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到该描述，不设置则在4G情况下会默认定位到“天安门广场”
            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要，不设置则拿不到定位点的省市区信息
            option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            /*可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            该参数若不设置，则在4G状态下，会出现定位失败，将直接定位到天安门广场
             */
            return option;
        }
    }
    
    private class GetWeatherThread extends Thread{
    	String lat = "";
    	String lng = "";
    	public GetWeatherThread(String latStr,String lngStr){
    		lat = latStr;
    		lng = lngStr;
    	}
    	@Override
    	public void run() {
    		int lastWeatherTime = Utils.getIntPref(Utils.PREF_WEATHER_TIME, FishMainActivity.this);
    		int now = (int) (System.currentTimeMillis()/1000);
    		final String str;
    		if ((now - lastWeatherTime) > 30 * 60){//半小时内则不更新20170220
    			str = WeatherUtils.get7DayWeatherFromAliYun(lat, lng);
    			if (!TextUtils.isEmpty(str)){
    				Editor e = mPref.edit();
        			e.putInt(Utils.PREF_WEATHER_TIME, now);
        			e.putString(Utils.PREF_WEATHER_DETAIL, str);
        			e.commit();
    			}
    		} else {
    			str = Utils.getStrPref(Utils.PREF_WEATHER_DETAIL, FishMainActivity.this);
    			if (TextUtils.isEmpty(str)){
    				Editor e = mPref.edit();
        			e.putInt(Utils.PREF_WEATHER_TIME, 0);
        			e.commit();
    			}
    		}
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						Log.v(tag, "weatherstr===="+str);
						JSONObject jo = new JSONObject(str);
						String cityStr = jo.getJSONObject("showapi_res_body").getJSONObject("cityInfo").getString("c3");
						String tempNight = jo.getJSONObject("showapi_res_body").getJSONObject("f1").getString("night_air_temperature");
						String tempDay = jo.getJSONObject("showapi_res_body").getJSONObject("f1").getString("day_air_temperature");
						String weatherDay = jo.getJSONObject("showapi_res_body").getJSONObject("f1").getString("day_weather");
						String weatherCode =  jo.getJSONObject("showapi_res_body").getJSONObject("f1").getString("day_weather_code");
						
						String airStr = jo.getJSONObject("showapi_res_body").getJSONObject("now").getString("aqi");//空气污染
						
						locationText.setText(cityStr + "   ");
						weatherImg.setImageDrawable(new TintedBitmapDrawable(getResources(),
                                WeatherUtils.GetSmallWeatherIcon(weatherCode), R.color.black));
//						weatherImg.setImageResource(getWeatherIcon(weatherCode));
						weatherText.setText("   "+ weatherDay + "   " + tempDay + "℃~" + tempNight + "℃   ");
						String score = "";
						try{
							int air = Integer.parseInt(airStr);
							if (air < 50){
								score = "优";
                                airText.setBackgroundResource(R.drawable.bk_blue);
							} else if (air > 100){
                                airText.setBackgroundResource(R.drawable.bk_red);
								score = "差";
							} else {
								score = "良";
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						airText.setText("空气" + score + " " + airStr);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
    	}
    }
    
    
    
    private void showPopMenu(View anchor){//TODO 
		View menuView = LayoutInflater.from(this).inflate(R.layout.pop_menu_home_act, null,false);
		View deviceV = menuView.findViewById(R.id.pop_home_add_device);
		View settingV = menuView.findViewById(R.id.pop_home_setting);
		
		final PopupWindow popupWindow = new PopupWindow(menuView, 
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		deviceV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isVisitorMode()){
            		showToast("游客无权限查看此模块，请登录");
            		startLoginUI();
				} else {
					Intent intent = new Intent(FishMainActivity.this, MipcaActivityCapture.class);
					startActivityForResult(intent, REQUEST_ADDDEVICE);
					popupWindow.dismiss();
				}
			}
		});
		settingV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isVisitorMode()){
            		showToast("游客无权限查看此模块，请登录");
            		startLoginUI();
            	} else {
	                Intent intent = new Intent(FishMainActivity.this, SettingActivity.class);
	                startActivityForResult(intent, REQUEST_SETTING_OUT);
            	}
				popupWindow.dismiss();
			}
		});
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.anim.roll_down);
		popupWindow.showAsDropDown(anchor, 0, 0);
	}
}
