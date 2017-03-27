package com.ssiot.remote.aliyun;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

import com.ssiot.agri.ContextUtilApp;
import com.ssiot.agri.R;
import com.ssiot.remote.SsiotService.NotiInfo;
import com.ssiot.remote.dblocal.LocalDBHelper;

public class MQTTRecvMsg {
	private static final String tag = "MQTTRecvMsg阿里云-农帮手";
	public static boolean running = false;
	public static MqttClient sampleClient = null;
	
    public static void main() throws IOException {//String[] args
    	if (running && null != sampleClient && sampleClient.isConnected()){
    		Log.v(tag, "---已订阅 且 sampleClient.isConnected！");
    		return;
    	}
    	running = true;
        /**
         * 设置MQTT的接入点，请根据应用所在环境选择合适的region，不支持跨Region访问
         */
        final String broker ="tcp://mqtt.ons.aliyun.com:1883";
        /**
         * 设置阿里云的AccessKey，用于鉴权
         */
        final String acessKey ="1PFRbguH6M34Vkob";
        /**
         * 设置阿里云的SecretKey，用于鉴权
         */
        final String secretKey ="pzl5IS4F1oZF2WDOCTidmGUzd1i4bk";//TODO 
        /**
         * 发消息使用的一级Topic，需要先在MQ控制台里申请
         */
        final String topic ="ssiotagritopic";
        /**
         * MQTT的ClientID，一般由2部分组成，ConsumerID@@@DeviceID
         * 其中ConsumerID在MQ控制台里申请
         * DeviceID由应用方设置，可能是设备编号等，需要唯一，否则服务端拒绝重复的ClientID连接
         */
        final String clientId ="CID_fisher@@@" + Secure.getString(ContextUtilApp.getInstance().getContentResolver(),Secure.ANDROID_ID + "ssiotagri");//不能重复
        String sign;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            final MqttConnectOptions connOpts = new MqttConnectOptions();
            Log.v(tag, "Connecting to broker: " + broker);
            /**
             * 计算签名，将签名作为MQTT的password。
             * 签名的计算方法，参考工具类MacSignature，第一个参数是ClientID的前半部分，即Producer ID或者Consumer ID
             * 第二个参数阿里云的SecretKey
             */
            sign = MacSignature.macSignature(clientId.split("@@@")[0], secretKey);
            connOpts.setUserName(acessKey);
            connOpts.setServerURIs(new String[] { broker });
            connOpts.setPassword(sign.toCharArray());
            connOpts.setCleanSession(false);
            connOpts.setKeepAliveInterval(100);
            sampleClient.setCallback(new MqttCallback() {
            	@Override
                public void connectionLost(Throwable throwable) {
            		Log.v(tag, "mqtt connection lost");
                    throwable.printStackTrace();
                }
            	@Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            		String txt = new String(mqttMessage.getPayload());
            		Log.v(tag, "messageArrived:" + topic + "------" + txt);
            		try {
            			MsgBean m = new MsgBean();
                		JSONObject jo = new JSONObject(txt);
                		m.title = jo.getString("title");
                		m.detail = jo.getString("content");
                		m.url = jo.getString("url");
                		insertNotiToLocalDB(m);
                		showNotification(ContextUtilApp.getInstance(), m);//TODO
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
            	@Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            		Log.v(tag, "deliveryComplete:" + iMqttDeliveryToken.getMessageId());
                }
            });
            sampleClient.connect(connOpts);
            /**
             * 设置订阅方订阅的Topic集合，此处遵循MQTT的订阅规则，可以是一级Topic，二级Topic或者是P2P消息，
             */
            final String p2ptopic = topic+"/p2p/";
            final String[] topicFilters=new String[]{topic, topic+"/notice/",p2ptopic};
            sampleClient.subscribe(topicFilters);
            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception me) {
            me.printStackTrace();
        }
    }
    
    private static void insertNotiToLocalDB(MsgBean msgModel){
        if (null == msgModel || msgModel.title == null){
            Log.e(tag, "----msg = null");
            return;
        }
        LocalDBHelper dbHelper = new LocalDBHelper(ContextUtilApp.getInstance());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TitleStr", msgModel.title);
        values.put("DetailStr", msgModel.detail);
        values.put("UrlStr", msgModel.url);
//        values.put("CreateTime", model._code);
    
        db.insert("msghistory", null, values);//nullcolumnhack is null or "id"?
        db.close();
        dbHelper.close();
    }
    
    @SuppressLint("NewApi") //必须检查版本
    private static Notification showNotification(Context c, MsgBean msgModel) {
        Notification.Builder builder = new Notification.Builder(c);
        // builder.setTicker(title);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(msgModel.title);
        builder.setContentText(msgModel.detail);
        Log.v(tag, "---notimsg:" + msgModel.title + msgModel.detail + msgModel.url);

        builder.setAutoCancel(true);
        if (!TextUtils.isEmpty(msgModel.url)){//点击打开Url
            builder.setContentIntent(PendingIntent.getActivity(c, 0, new Intent(c, MsgListAct.class), PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            Intent txtIntent = new Intent(c, TxtAct.class);
            txtIntent.putExtra("msg", msgModel.detail);
            builder.setContentIntent(PendingIntent.getActivity(c, 0, txtIntent, PendingIntent.FLAG_UPDATE_CURRENT));//TODO
        }

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
        noti.sound = Uri.parse("android.resource://" + c.getPackageName() + "/" +R.raw.beep);
        NotificationManager mnotiManager = (NotificationManager) c
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mnotiManager.notify(2000002, noti);
        return noti;
    }
}