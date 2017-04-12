package com.ssiot.remote.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ssiot.agri.BuildConfig;
import com.ssiot.agri.R;
import com.ssiot.remote.SettingFrag;
import com.ssiot.remote.SsiotService;
import com.ssiot.remote.UpdateDialogActivity;
import com.ssiot.remote.UpdateManager;
import com.ssiot.remote.Utils;
import com.ssiot.remote.aliyun.MQTTRecvMsg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SsiotReceiver extends BroadcastReceiver{
    private static final String tag = "SsiotReceiverFish";
    public static final String ACTION_SSIOT_MSG = "com.ssiot.agri.SHOWMSG";
    static final String BOOTACTION = "android.intent.action.BOOT_COMPLETED";
    public static final String ACTION_SSIOT_V_GOT = "com.ssiot.agri.update.versiongot";
    public static final String ACTION_SSIOT_V_DOWNLOAD_PROGRESS = "com.ssiot.agri.update.downloadprogress";
    public static final String ACTION_SSIOT_V_DOWNLOAD_FINISH = "com.ssiot.agri.update.downfinish";
    public static final String ACTION_SSIOT_V_DOWNLOAD_ERROR = "com.ssiot.agri.update.error";
    public static final String ACTION_NOTIFICAION_DELETE = "com.ssiot.agri.notification.delete";
    
    public static final int NOTIFICATION_FLAG = 1; 
    Notification mProgressNoti;
    Notification.Builder notiBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MQTTRecvMsg.main();//TODO 测试阿里云mqtt
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}).start();
    	
        
        String action = intent.getAction();
        Log.v(tag, "-----onReceive-----" + action +" "+ intent);
        
        if (ACTION_SSIOT_MSG.equals(action)){
            
            String extraString = intent.getStringExtra("showmsg");
            Log.v(tag, "----onReceive----showmsg:" +extraString);
            Toast.makeText(context, extraString + "，请检查网络后重试。", Toast.LENGTH_SHORT).show();
        } else if (BOOTACTION.equals(action)){
            if (Utils.getBooleabPref(Utils.PREF_ALARM, context)){
                Intent myintent = new Intent(context, SsiotService.class);
                context.startService(myintent);
            }
        } else if (ACTION_SSIOT_V_GOT.equals(action)){
            intent.getIntExtra("updatestatus", -1);
            int remoteVer = intent.getIntExtra("remoteversion", -1);
            int curentV = intent.getIntExtra("currentversion", -1);
            if (remoteVer <= 0){//大多是网络问题
                Intent i = new Intent(SettingFrag.ACTION_SSIOT_UPDATE);
                i.putExtra("checkresult", 0);
                context.sendBroadcast(i);
            } else if (remoteVer > curentV){//remoteversion > curVersion
                
                HashMap<String, String> mVerInfoMap = (HashMap<String, String>) intent.getSerializableExtra("versionxmlmap");
                Intent userCheckDialog = new Intent(context, UpdateDialogActivity.class);
                userCheckDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                userCheckDialog.putExtra("versionxmlmap", mVerInfoMap);
                context.startActivity(userCheckDialog);
                
                Intent i = new Intent(SettingFrag.ACTION_SSIOT_UPDATE);
                i.putExtra("checkresult", 1);
                context.sendBroadcast(i);
            } else if (remoteVer == curentV){
                Intent i = new Intent(SettingFrag.ACTION_SSIOT_UPDATE);
                i.putExtra("checkresult", 2);
                context.sendBroadcast(i);
            } else {
                Toast.makeText(context, "本地版本高于服务器版本", Toast.LENGTH_SHORT).show();
            }
            
        } else if (ACTION_SSIOT_V_DOWNLOAD_PROGRESS.equals(action)){
            int pro = intent.getIntExtra("downloadprogress", 0);
            Log.v(tag, "----------ACTION_SSIOT_V_DOWNLOAD_PROGRESS"+pro + (null != mProgressNoti));
            if (null != notiBuilder){
                notiBuilder.setContentText("已下载：" + pro + "%");
                notiBuilder.build();
//                NotificationManager mnotiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                mProgressNoti.setLatestEventInfo(context, "正在更新", "已下载：" + pro + "%",
//                        PendingIntent.getActivity(context, -1, new Intent(""), 0));
//                mnotiManager.notify(UpdateManager.NOTIFICATION_FLAG, mProgressNoti);
            } else {
                notiBuilder = showNotification(context,pro);
            }
        } else if (ACTION_SSIOT_V_DOWNLOAD_FINISH.equals(action)){
            NotificationManager mnotiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mnotiManager.cancel(UpdateManager.NOTIFICATION_FLAG);
            installApk(context);
        } else if (ACTION_SSIOT_V_DOWNLOAD_ERROR.equals(action)){
            NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mManager.cancel(UpdateManager.NOTIFICATION_FLAG);
            Toast.makeText(context, "下载出现错误", Toast.LENGTH_LONG).show();
        } else if (ACTION_NOTIFICAION_DELETE.equals(action)){
        	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        	Editor e = sp.edit();
        	e.putString(Utils.PREF_LAST_OFFLINE, "");
        	e.commit();
        }
    }
    
    
    
    
    
    
    @SuppressLint("NewApi") //必须检查版本
    public Notification.Builder showNotification(Context c,int pro) {
        Notification.Builder builder = new Notification.Builder(c);
        // builder.setTicker(title);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("正在更新");
        builder.setContentText("已下载："+pro+"%");
        builder.setAutoCancel(false);
        builder.setProgress(100, pro, false);
        // builder.setContentIntent(PendingIntent.getActivity(c, 0, new
        // Intent(Intent.ACTION_DELETE), 0));

        // Notification noti = new Notification();//TODO
        RemoteViews remoteView = new RemoteViews(c.getPackageName(),
                R.layout.notification_download);
        remoteView.setProgressBar(R.id.noti_progress, 100, 20, false);
        remoteView.setImageViewResource(R.id.noti_image, R.drawable.ic_launcher);
        remoteView.setTextViewText(R.id.noti_text, "我的新通知");
        // builder.setContent(remoteView);
        Notification noti = builder.build();
        // noti.contentView = remoteView;
//            noti.flags |= Notification.FLAG_ONGOING_EVENT;

        NotificationManager mnotiManager = (NotificationManager) c
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mnotiManager.notify(NOTIFICATION_FLAG, noti);
        return builder;
    }
    
    public void installApk(Context context) {
        File apkfile = new File(UpdateManager.getSavePath(), UpdateManager.LOCAL_APK);
        if (!apkfile.exists()) {
            Toast.makeText(context, "未找到文件" + apkfile.getPath(), Toast.LENGTH_SHORT).show();
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String[] command = {"chmod", "777", apkfile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//android7.0的新限制
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".fileProvider", apkfile);
            i.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(i);
    }
    
    
}