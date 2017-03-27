package com.ssiot.remote;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.ssiot.remote.backwork.OffLineListenerThread;
import com.ssiot.remote.data.model.AlarmRuleModel;
import com.ssiot.remote.data.model.LiveDataModel;
import com.ssiot.remote.yun.webapi.WS_API;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.ssiot.agri.R;

public class SsiotService extends Service{
    private static final String tag = "SsiotServiceFish";
    private static int NOTIFICATION_ID = 101;
//    private List<ControlActionInfoModel> actionInfoList = new ArrayList<ControlActionInfoModel>();
    private List<LiveDataModel> newestDataList = new ArrayList<LiveDataModel>();
    public static boolean cancel = false;
    private Object lock = new Object();
    SharedPreferences mPref;
    private LiveDataBackWorker mWorker;
    private OffLineListenerThread mOffLineWorker;
    List<AlarmRuleModel> mAlarmModels;
    private List<NotiInfo> notiInfos = new ArrayList<SsiotService.NotiInfo>();
//    List<SensorModel> sensor_dic;
    
    //BUG 小米手机熄屏后4分钟左右，getalarm连接不上网络，即使亮屏后也不行。 
    //小米手机--设置--其他高级设置--电源和性能--神隐模式 默认是标准,在屏保后4分钟左右会限制后台应用的网络功能
    private static final int MSG_RUN_ALARM = 1;
    private Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case MSG_RUN_ALARM:
				new LiveDataBackWorker_2().start();
				
				mHandler.removeMessages(MSG_RUN_ALARM);
				Message m = mHandler.obtainMessage(MSG_RUN_ALARM);
		        mHandler.sendMessageDelayed(m, 60 * 1000);
				break;

			default:
				break;
			}
    	};
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.v(tag, "-------------onCreate---------------");
        mWorker = new LiveDataBackWorker();
        mOffLineWorker = new OffLineListenerThread(this);
    }
    
    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.v(tag, "-------------onStart---------------");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(tag, "-------------onStartCommand---------------");
        cancel = false;
//        if (!mWorker.isAlive() && isLogedIn() && Utils.getBooleabPref(Utils.PREF_ALARM, this)){
////        	mWorker = new LiveDataBackWorker();//this is to resolve (IllegalThreadStateException: Thread already started)
//            mWorker.start();//TODO
//        }
        mHandler.removeMessages(MSG_RUN_ALARM);
        Message msg = mHandler.obtainMessage(MSG_RUN_ALARM);
        mHandler.sendMessageDelayed(msg, 60 * 1000);
        
        mOffLineWorker.cancle = false;
        if (!mOffLineWorker.isAlive() && isLogedIn() && Utils.getBooleabPref(Utils.PREF_OFFLINE_NOTICE, this)
                && !mOffLineWorker.running){
        	mOffLineWorker.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    
    private class LiveDataBackWorker extends Thread{
        @Override
        public void run() {
            cancel = false;
            while (!cancel) {
            	Log.v(tag, "^^^^^^^^^^^^^^^^LiveDataBackWorker^^^" + Utils.formatTime(new Timestamp(System.currentTimeMillis())));
                synchronized (lock) {
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (false == mPref.getBoolean("alarm", true)){//TODO
                        Log.v(tag, "-----alarm set false-----" + new Date().toString());
                        continue;
                    }
                    if (!Utils.isNetworkConnected(SsiotService.this)){
                        Log.e(tag, "-----no net-----" + new Date().toString());
                        continue;
                    }
                    
//                    if (null == sensor_dic || sensor_dic.size() == 0){
//                        sensor_dic = new Sensor().GetModelList("1=1");
//                    }
                    
                    String account = Utils.getStrPref(Utils.PREF_USERNAME, getApplicationContext());
                    HashMap<String, String> msgs = new WS_API().GetAlarming_v2(account);
                    Log.v(tag, "-------------------LiveDataBackWorker working alrmingStr:---"+msgs.toString());
                    if (null != msgs && msgs.size() > 0){
                    	Iterator iter = msgs.entrySet().iterator();
                    	while(iter.hasNext()){//一个节点发一条notificaition
                    		Map.Entry entry = (Map.Entry) iter.next();
                    		showNotification(getApplicationContext(),(String)entry.getKey(), (String)entry.getValue());
                    	}
                    }
                }
            }
        }
    }
    
    private class LiveDataBackWorker_2 extends Thread{
        @Override
        public void run() {
        	Log.v(tag, "^^^^^^^^^^^^^^^^LiveDataBackWorker2^^^" + Utils.formatTime(new Timestamp(System.currentTimeMillis())));
            synchronized (lock) {
                if (false == mPref.getBoolean("alarm", true)){//TODO
                    Log.v(tag, "-----alarm set false-----" + new Date().toString());
                    return;
                }
                if (!Utils.isNetworkConnected(SsiotService.this)){
                    Log.e(tag, "-----no net-----" + new Date().toString());
                    return;
                }
                
//                if (null == sensor_dic || sensor_dic.size() == 0){
//                    sensor_dic = new Sensor().GetModelList("1=1");
//                	sensor_dic = new WS_Fish().
//                }
                
                String account = Utils.getStrPref(Utils.PREF_USERNAME, getApplicationContext());
                HashMap<String, String> msgs = new WS_API().GetAlarming_v2(account);
                Log.v(tag, "-------------------LiveDataBackWorker working alrmingStr:---"+msgs.toString());
                if (null != msgs && msgs.size() > 0){
                	Iterator iter = msgs.entrySet().iterator();
                	while(iter.hasNext()){//一个节点发一条notificaition
                		Map.Entry entry = (Map.Entry) iter.next();
                		showNotification(getApplicationContext(),(String)entry.getKey(), (String)entry.getValue());
                	}
                }
            }
        }
    }

    @SuppressLint("NewApi") //必须检查版本 限制最低4.0 就不要检查版本了20170315
    private Notification showNotification(Context c, String unique, String contentTxt) {
        Notification.Builder builder = new Notification.Builder(c);
        // builder.setTicker(title);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setWhen(System.currentTimeMillis());
        if (null != unique){
            builder.setContentTitle("预警节点:" + unique);
            builder.setContentText("监测值：" + contentTxt);
            Log.v(tag, "---notimsg:" + contentTxt);
        }

        builder.setAutoCancel(true);
//            builder.setContentIntent(PendingIntent.getActivity(c, 0, new Intent(c, FirstStartActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));//TODO

        // Notification noti = new Notification();
            /*RemoteViews remoteView = new RemoteViews(c.getPackageName(),
                    R.layout.notification_download);
            remoteView.setProgressBar(R.id.noti_progress, 100, 20, false);
            remoteView.setImageViewResource(R.id.noti_image, R.drawable.ic_launcher);
            remoteView.setTextViewText(R.id.noti_text, "我的新通知");*/
        // builder.setContent(remoteView);
        Notification noti = builder.build();
        // noti.contentView = remoteView;
//            noti.flags |= Notification.FLAG_ONGOING_EVENT;
        if (Utils.getBooleabPref(Utils.PREF_NOTI_SOUND, this)){
            noti.sound = Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.beep);
        }
        NotificationManager mnotiManager = (NotificationManager) c
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (!nearlyExists(unique, notiInfos)){
            mnotiManager.notify(Integer.parseInt(unique), noti);
            notiInfos.add(new NotiInfo(System.currentTimeMillis(), unique,contentTxt));
        }
        return noti;
    }
    
    private boolean nearlyExists(String unique, List<NotiInfo> notis){//作用  30分钟内相同的节点不再发出noti
        for (int j = 0; j < notis.size(); j ++){//删除老的记录
            if (System.currentTimeMillis() - notis.get(j).time > 30 * 60 * 1000){//30分钟前的数据 
                notis.remove(j);
                j --;
            }
        }
        if (null != notis){
            for (int i = 0; i < notis.size(); i ++){
                if ((System.currentTimeMillis() - notis.get(i).time < 30 * 60 * 1000) && unique.equals(notis.get(i).nodeUnique)){
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isLogedIn(){
        if (mPref == null){
            return false;
        }
        String pwd = mPref.getString(Utils.PREF_PWD, "");
        return !TextUtils.isEmpty(pwd);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(tag, "-------------onDestroy--------restart-------" + !cancel);
        if (!cancel){//特殊情况下ondestroy的才重启
            Intent localIntent = new Intent();  
            localIntent.setClass(this, SsiotService.class);
            startService(localIntent);
        }
        //方法2:如果想让服务不被杀死（也即无动作不改变），在XML的<application段后面加上 android:persistent="true"就行了。
    }
    
    public class NotiInfo{
        public long time;
        public String nodeUnique;
        public String dataStr;
        public NotiInfo(long timeMills, String uni,String datas){
            time = timeMills;
            nodeUnique = uni;
            dataStr = datas;
        }
    }
    
}