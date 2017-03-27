package com.ssiot.remote.backwork;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.ssiot.agri.R;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.view.NodeView2Model;
import com.ssiot.remote.receiver.SsiotReceiver;

public class OffLineListenerThread extends Thread{//TODO 版本升级 pref的问题
	private static final String tag = "OffLineListenerThread";
	private static final int NOTIFICATION_OFFLINE_ID = 9001;
    public boolean cancle = false;
    String userKey;
    Context mContext;
    public boolean running = false;
    
    public OffLineListenerThread(Context c){
    	mContext = c;
    	userKey = Utils.getStrPref(Utils.PREF_USERKEY, c);
    	Log.v(tag, "---OffLineListenerThread created----userKey:" + userKey);
    }
    
    @Override
    public synchronized void run() {
        running = true;
        while (!cancle){
        	try {
                Thread.sleep(4 * 1000);//延迟一会儿，防止程序都在同时执行
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.v(tag, "---------start check offline" + new Date().toString() + Utils.getBooleabPref(Utils.PREF_OFFLINE_NOTICE, mContext)
            		 + " net:" + Utils.isNetworkConnected(mContext));
            
            if (Utils.getBooleabPref(Utils.PREF_OFFLINE_NOTICE, mContext) && Utils.isNetworkConnected(mContext)){
                
                String offlineNodes = "";
                List<NodeView2Model> list = null;// TODO TODO new AjaxGetNodesDataByUserkey().GetAllNodesDataByUserkey(userKey);
                for (int i = 0; i < list.size(); i ++){
                	if ("离线".equals(list.get(i)._isonline)){
                		offlineNodes += list.get(i)._nodeno + ",";
                	}
                }
                
                if (!TextUtils.isEmpty(offlineNodes)){
                	if (offlineNodes.endsWith(",")){
                		offlineNodes = offlineNodes.substring(0, offlineNodes.length() - 1);
                		
                		String account = Utils.getStrPref(Utils.PREF_USERNAME, mContext);
                    	String prefNodes = Utils.getStrPref(account + Utils.PREF_OFFLINE_NOTICE, mContext);
                    	Log.v(tag, "-------prefNodes:" +prefNodes + " offlineNodes:"+offlineNodes);
                    	String toshow = containsInPref(offlineNodes, prefNodes);
                    	String mLastOfflineNodes = Utils.getStrPref(Utils.PREF_LAST_OFFLINE, mContext);
                    	Log.v(tag, "-------toshow:"+ toshow + " mLastOfflineNodes:"+mLastOfflineNodes);
                    	if (!TextUtils.isEmpty(toshow) && !toshow.equals(mLastOfflineNodes)){//防止重复提醒
                    		SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
                    		Editor e = mPref.edit();
                    		e.putString(Utils.PREF_LAST_OFFLINE, toshow);
                    		e.commit();
                    		showNotification(mContext, NOTIFICATION_OFFLINE_ID, toshow);
                    	}
                	}
                	
                }
                
            }
            try {
                Thread.sleep(30 * 60 * 1000);//20min执行一次  58 * 6 TODO
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        running = false;
    }
    
    private String containsInPref(String offlineNodes, String prefNodes){
    	String ret = "";
    	String[] prefNodesList = prefNodes.split(",");
    	String[] offlineList = offlineNodes.split(",");
    	for (int i = 0; i < prefNodesList.length; i ++){
    		if (!TextUtils.isEmpty(prefNodesList[i])){
    			for (int j = 0; j < offlineList.length; j ++){
    				if (offlineList[j].equals(prefNodesList[i])){
    					ret += prefNodesList[i] + ",";
    				}
    			}
    		}
    	}
    	if (ret.endsWith(",")){
    		ret = ret.substring(0, ret.length() - 1);
    	}
    	return ret;
    }
    
    public void cancle(){
        cancle = true;
    }
    
    
    
    
    
    @SuppressLint("NewApi") //必须检查版本
    private Notification showNotification(Context c, int notiId, String contentText) {
        Notification.Builder builder = new Notification.Builder(c);
        // builder.setTicker(title);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("节点掉线提醒" + Utils.formatTime(new Timestamp(System.currentTimeMillis())));
        builder.setContentText("节点号：" + contentText);

        builder.setAutoCancel(false);
//            builder.setContentIntent(PendingIntent.getActivity(c, 0, new Intent(c, FirstStartActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));//TODO

        Intent deleteIntent = new Intent();
        deleteIntent.setClass(mContext, SsiotReceiver.class);
        deleteIntent.setAction(SsiotReceiver.ACTION_NOTIFICAION_DELETE);
        builder.setDeleteIntent(PendingIntent.getBroadcast(mContext, 0, deleteIntent, 0));
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
        noti.sound = Uri.parse("android.resource://" + mContext.getPackageName() + "/" +R.raw.beep);
        NotificationManager mnotiManager = (NotificationManager) c
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mnotiManager.notify(notiId, noti);
//            if (!nearlyExists(unique, notiInfos)){
//
//                notiInfos.add(new NotiInfo(System.currentTimeMillis(), unique,contentTxt));
//            }
        return noti;
    }
}