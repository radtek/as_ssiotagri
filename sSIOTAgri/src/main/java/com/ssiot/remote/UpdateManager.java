package com.ssiot.remote;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import com.ssiot.agri.R;
import com.ssiot.remote.receiver.SsiotReceiver;

public class UpdateManager{
    private static final String tag = "UpdateManager-ssiotagri";
    public static final int NOTIFICATION_FLAG = 1; 
    private Context mContext;
    public static boolean updating = false;
    public static final String LOCAL_APK = "ssiotagri.apk";
    
    public UpdateManager(Context context){
        mContext = context;
    }
    
    public void startGetRemoteVer(){
        new GetRemoteVerThread().start();
    }
    
    public void startDownLoad(HashMap<String, String> hsMap){
        new DownloadApkThread(hsMap).start();
    }
    
    public void stopDownload(){
        cancelUpdate = true;
    }
    
    private class GetRemoteVerThread extends Thread{
        @Override
        public synchronized void run() {
            updating = true;
            try {
                HashMap<String, String> mHashMap;
                int curV = getCurVersionCode(mContext);
                URL url = new URL("http://www.ssiot.com/app/downloads/ssiotagriversion.xml");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream inStream = conn.getInputStream();
                mHashMap = Utils.parseXml(inStream);
                if (null != mHashMap){
                    int serviceCode = Integer.valueOf(mHashMap.get("version"));
//                    if (null != mAppHandler){
//                        Message m = mAppHandler.obtainMessage(MainActivity.MSG_GETVERSION_END);
//                        m.arg1 = serviceCode;
//                        m.arg2 = curV;
//                        m.obj = mHashMap;
//                        mAppHandler.sendMessage(m);
//                    }
                    Intent getVerEndIntent = new Intent(SsiotReceiver.ACTION_SSIOT_V_GOT);
                    getVerEndIntent.putExtra("remoteversion", serviceCode);
                    getVerEndIntent.putExtra("currentversion", curV);
                    getVerEndIntent.putExtra("versionxmlmap", mHashMap);
                    mContext.sendBroadcast(getVerEndIntent);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
//                Message m = mAppHandler.obtainMessage(MainActivity.MSG_GETVERSION_END);
//                m.arg1 = -1;
//                mAppHandler.sendMessage(m);
                Intent getVerEndIntent = new Intent(SsiotReceiver.ACTION_SSIOT_V_GOT);
                getVerEndIntent.putExtra("remoteversion", -1);
                mContext.sendBroadcast(getVerEndIntent);
            }
            updating = false;
            
        }
    }
    
    private int getCurVersionCode(Context c){
        int versionCode = 0;
        try {
            versionCode = c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionCode;
    }
    
    boolean cancelUpdate = false;
    private class DownloadApkThread extends Thread {
        private HashMap<String, String> mHashMap;
        
        
        public DownloadApkThread(HashMap<String, String> mVerMap){
            mHashMap = mVerMap;
            cancelUpdate = false;
        }
        
        @Override
        public void run() {
            updating = true;
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String mSavePath = getSavePath();
                    Log.v(tag, "--------------mSavePath" +mSavePath);
                    if (null == mHashMap){
                        throw new IOException("----HashMap is null!");
                    }
                    URL url = new URL(mHashMap.get("url"));
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();
                    File file = new File(mSavePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, LOCAL_APK);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    int progress = 0;
                    int progressnow = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progressnow = (int) (((float) count / length) * 100);
                        // 更新进度
                        if (progressnow != progress){
//                            Message m = mAppHandler.obtainMessage(MainActivity.MSG_DOWNLOADING_PREOGRESS);
//                            m.arg1 = progress;
//                            mAppHandler.sendMessage(m);
                            Intent intent = new Intent(SsiotReceiver.ACTION_SSIOT_V_DOWNLOAD_PROGRESS);
                            intent.putExtra("downloadprogress", progress);
                            mContext.sendBroadcast(intent);
                        }
                        progress = progressnow;
                        
                        if (numread <= 0) {
                            // 下载完成
//                            mAppHandler.sendEmptyMessage(MainActivity.MSG_DOWNLOAD_FINISH);
                            Intent intent = new Intent(SsiotReceiver.ACTION_SSIOT_V_DOWNLOAD_FINISH);
                            mContext.sendBroadcast(intent);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                    if (cancelUpdate && apkFile.exists()){
                        apkFile.delete();
//                        mAppHandler.sendEmptyMessage(MainActivity.MSG_DOWNLOAD_CANCEL);
                    }
                } else {
                    Log.v(tag, "!!!!!!!!!!!!!!!!! sdcard is not mounted");
//                    mAppHandler.sendEmptyMessage(MainActivity.MSG_SHOWERROR);
                    mContext.sendBroadcast(new Intent(SsiotReceiver.ACTION_SSIOT_V_DOWNLOAD_ERROR));
                }
            } catch (MalformedURLException e) {
//                showText = "URL错误";
//                mAppHandler.sendEmptyMessage(MainActivity.MSG_SHOWERROR);
                mContext.sendBroadcast(new Intent(SsiotReceiver.ACTION_SSIOT_V_DOWNLOAD_ERROR));
                e.printStackTrace();
            } catch (IOException e) {
//                showText = "IO错误";
//                mAppHandler.sendEmptyMessage(MainActivity.MSG_SHOWERROR);
                mContext.sendBroadcast(new Intent(SsiotReceiver.ACTION_SSIOT_V_DOWNLOAD_ERROR));
                e.printStackTrace();
            }
            // 取消下载对话框显示
//            if (null != mDownloadDialog && mDownloadDialog.isShowing()){
//                mDownloadDialog.dismiss();
//            }
            updating = false;
        }
        
        public void cancel(){
            cancelUpdate = true;
        }
    };
    
    public static String getSavePath(){
        String path = Environment.getExternalStorageDirectory() + "/" + SsiotConfig.CACHE_DIR+ "/";
        return path;
    }
    
    @SuppressLint("NewApi") //必须检查版本
    public Notification showNotification(Context c) {
        Notification.Builder builder = new Notification.Builder(c);
        // builder.setTicker(title);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("正在更新");
        builder.setContentText("已下载：0%");
        builder.setAutoCancel(false);
        builder.setProgress(100, 50, false);
        // builder.setContentIntent(PendingIntent.getActivity(c, 0, new
        // Intent(Intent.ACTION_DELETE), 0));

        // Notification noti = new Notification();
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
        return noti;
    }
    
    
//    // in Thread
//    public interface VersionListener{
//        public void onNewVersionFound(HashMap<String, String> h);
//    }
}