package com.ssiot.remote.yun;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.Toast;
import com.ssiot.remote.BrowserActivity;
import com.ssiot.fish.HeadActivity;
import com.ssiot.remote.LoginActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.SsiotService;
import com.ssiot.remote.UpdateManager;
import com.ssiot.remote.Utils;
import com.ssiot.remote.yun.expert.ExpertSystemAct;
import com.ssiot.remote.yun.history.ProductHistoryAct;
import com.ssiot.remote.yun.manage.ProductManageActivity;
import com.ssiot.remote.yun.monitor.MonitorAct;
import com.ssiot.remote.yun.sta.StatisticsAct;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainAct extends HeadActivity{
    private static final String tag = "FishMainActivity";
    private GridView gridView1;
    ArrayList<CellModel> cells;
    private SharedPreferences mPref;
    
    private UpdateManager mUpdaManager;
    private Notification mNoti;
    private Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {//TODO 记得 视频添加了个字段 枪机也用tcpport
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main_fish);
        startService(new Intent(this, SsiotService.class));
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        initGridView();
        
        if (mPref.getBoolean(Utils.PREF_AUTOUPDATE, true) == true){
            mUpdaManager = new UpdateManager(this);
            mUpdaManager.startGetRemoteVer();
        }
        new Thread(new Runnable() {
            
            @Override
            public void run() {
//                new MyWebService().plus(4f, 6f);
//                new MyWebService().func2(4);
                ArrayList<String> as = new ArrayList<String>();
                as.add("jiangsu");
                as.add("shanghai");
//                new MyWebService().func6();
            }
        }).start();
        
    }
    
//    private void testWebService2() {
//        try {
//            final String SERVER_URL = SERVICE_URL+ "";//"http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather"; // 定义需要获取的内容来源地址
//            HttpPost request = new HttpPost(SERVER_URL); // 根据内容来源地址创建一个Http请求
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("x", ""+4)); // 添加必须的参数
//            params.add(new BasicNameValuePair("y", ""+6)); // 添加必须的参数
//            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); // 设置参数的编码
//            HttpResponse httpResponse = new DefaultHttpClient().execute(request); // 发送请求并获取反馈
//
//            // 解析返回的内容
//            if (httpResponse.getStatusLine().getStatusCode() != 404) {
//                String result = EntityUtils.toString(httpResponse.getEntity());
//                Log.e(tag, "~~~~~~~~~~result:" + result);
//            } else {
//                Log.e(tag, "404!!!!!!!");
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    
    
    private static List<String> parseProvinceOrCity(SoapObject detail) {  
        ArrayList<String> result = new ArrayList<String>();  
        for (int i = 0; i < detail.getPropertyCount(); i++) {  
            // 解析出每个省份  
            result.add(detail.getProperty(i).toString().split(",")[0]);  
        }  
        return result;  
    }
    
    
    private void initGridView(){
        cells = new ArrayList<CellModel>();
        cells.add(new CellModel(R.drawable.cell_cekong,"监测预警", "map_zhengwu"));//测控中心
        cells.add(new CellModel(R.drawable.cell_data,"数据中心", "map_qiye"));//统计报表
        cells.add(new CellModel(R.drawable.cell_shichangdongtai,"溯源查询", "map_jstg"));//
        cells.add(new CellModel(R.drawable.cell_zhuanjiazaixian,"专家系统", "map_zhuisu"));//
//        cells.add(new CellModel(R.drawable.cell_yubingzhengduan,"鱼病诊断", "map_xinxi"));//
//        cells.add(new CellModel(R.drawable.cell_zhuanjiazaixian,"专家在线", "map_jinrong",CellModel.MODE_URL).setUrl("http://www.zhdcoop.com/"));
        cells.add(new CellModel(R.drawable.cell_shenchanguanli,"生产管理", "map_webbrow"));//
//        cells.add(new CellModel(R.drawable.cell_yuchangguanli,"渔场管理", "map_qiyezaixian"));//
//        cells.add(new CellModel(R.drawable.cell_wuzijiaoyi,"物资交易", "map_yztb"));//
//        cells.add(new CellModel(R.drawable.cell_hudongjiaoliu,"互动交流", "map_hudong"));
//        cells.add(new CellModel(R.drawable.cell_qiyehuizong,"企业汇总", "bdsj"));//
//        cells.add(new CellModel(R.drawable.cell_shichangdongtai,"市场动态", "map_yonghuzhinan"));//
//        cells.add(new CellModel(R.drawable.cell_zhengwuguanli,"更新", "map_findupdate"));
        if ("gn".equalsIgnoreCase(mPref.getString(Utils.PREF_USERNAME, ""))){
            cells.add(new CellModel(R.drawable.cell_wuzijiaoyi,"灌南电商", "map_gnshop"));
        }
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
//                switch (model.openType) {
//                    case CellModel.MODE_URL:
//                        Intent intent1 = new Intent(mContext, BrowserActivity.class);
//                        intent1.putExtra("url", model.urlString);
//                        startActivity(intent1);
//                        break;
//
//                    default:
//                        break;
//                }
                if ("监测预警".equals(model.itemText)){
                    Intent intent = new Intent(mContext, MonitorAct.class);
                    startActivity(intent);
                } else if ("数据中心".equals(model.itemText)){
                    Intent intent = new Intent(mContext, StatisticsAct.class);
                    startActivity(intent);
                } else if ("溯源查询".equals(model.itemText)){
                    Intent intent = new Intent(mContext, ProductHistoryAct.class);
                    startActivity(intent);
                } else if ("专家系统".equals(model.itemText)){
                    Intent intent = new Intent(mContext, ExpertSystemAct.class);
                    startActivity(intent);
                }//http://localhost:8080/SsiotWebserviceTest/services/WebService1  jingbo test
//                 else if ("鱼病诊断".equals(model.itemText)){
//                    Intent intent = new Intent(mContext, DiagnoseFishSelectActivity.class);
//                    startActivity(intent);
//                } else if ("渔场管理".equals(model.itemText)){
//                    Intent intent = new Intent(mContext, FishpondMainActivity.class);
//                    startActivity(intent);
//                } else if ("专家在线".equals(model.itemText)){//专家在线只是问题类别不同
//                    Intent intent = new Intent(mContext, QuestionListActivity.class);
//                    intent.putExtra("isexpertmode", true);
//                    startActivity(intent);
//                }
                else if ("生产管理".equals(model.itemText)){
                    Intent intent = new Intent(mContext, ProductManageActivity.class);
                    startActivity(intent);
                }
//                    else if ("任务中心".equals(model.itemText)){
//                    Intent intent = new Intent(mContext, TaskActivity.class);
//                    startActivity(intent);
//                } else if ("物资交易".equals(model.itemText)){
//                    Intent intent = new Intent(mContext, BrowserActivity.class);
//                    intent.putExtra("url", "http://gn.ssiot.com/mobile2/index.html");
//                    startActivity(intent);
//                } else if ("企业汇总".equals(model.itemText)){
//                    Intent intent = new Intent(mContext, CompanyListActivity.class);
//                    startActivity(intent);
//                } else if ("市场动态".equals(model.itemText)){
//                    Intent intent = new Intent(mContext, MarketNewsActivity.class);
//                    startActivity(intent);
//                }
                else if ("灌南电商".equals(model.itemText)){
                    Intent intent = new Intent(mContext, BrowserActivity.class);
                    intent.putExtra("url", "http://gn.ssiot.com/mobile");
                    startActivity(intent);
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.menu_f_main, menu);
        getMenuInflater().inflate(R.menu.menu_old_ui, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Intent loginIntent = new Intent(mContext, LoginActivity.class);
                startActivity(loginIntent);
                SsiotService.cancel = true;
                stopService(new Intent(this, SsiotService.class));
                finish();
                Editor e = mPref.edit();
                e.putString(Utils.PREF_PWD, "");
                e.commit();
                return true;
            case R.id.action_old_ui:
//            	Intent i = new Intent(mContext, MainActivity.class);
//            	startActivity(i);
            	break;
            case R.id.action_frag_main_setting:
                Intent intent = new Intent(mContext, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showUpdateChoseDialog(HashMap<String, String> mVerMap){
        final HashMap<String, String> tmpMap = mVerMap;
        AlertDialog.Builder builder =new Builder(this);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        builder.setPositiveButton(R.string.soft_update_updatebtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNoti = mUpdaManager.showNotification(mContext);
//                        .setProgressBar(R.id.noti_progress, 100, 0, false);
                mUpdaManager.startDownLoad(tmpMap);
//                showDownloadDialog(tmpMap);
                dialog.dismiss();
                Editor e = mPref.edit();
                e.putBoolean(Utils.PREF_AUTOUPDATE, true);
                e.commit();
                Toast.makeText(mContext, "转向后台下载，可在通知栏中查看进度。", Toast.LENGTH_SHORT).show();
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
//        if (mUpdaManager == null){
//            mUpdaManager = new UpdateManager(mContext, mHandler);
//        }
//        if (mPref.getBoolean(Utils.PREF_AUTOUPDATE, true) == false){
//            Editor editor = mPref.edit();
//            editor.putBoolean(Utils.PREF_AUTOUPDATE, true);
//            editor.commit();
//        }
//        mUpdaManager.startGetRemoteVer();
//    }
}
