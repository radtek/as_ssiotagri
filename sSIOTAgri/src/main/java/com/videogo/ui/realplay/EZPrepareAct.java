package com.videogo.ui.realplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ssiot.agri.R;
import com.videogo.constant.IntentConsts;
import com.videogo.exception.BaseException;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.ui.util.SignUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class EZPrepareAct extends Activity{//备注：EZOpenSDK.jar中包含了AudioEngineSDK.jar org.eclipse.paho.client.mqttv3-1.0.2.jar PlayerSDK.jar
    private static final String tag = "EZPrepareAct";
    String deviceSerial = "";
    String verifyCode = "";
    EZCameraInfo ezCameraInfo = null;
    
    private static final int MSG_GET_END = 1;
    private static final int MSG_GET_FAIL = 2;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_END:
                    if (null != ezCameraInfo){
                        Intent intent = new Intent(EZPrepareAct.this, EZRealPlayActivity.class);
                        intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, ezCameraInfo);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EZPrepareAct.this, "未查找到此设备信息", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    break;
                case MSG_GET_FAIL:
                    Toast.makeText(EZPrepareAct.this, "登陆萤石云出错", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    break;
            }
        };
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {//TODO 从videolist start这个界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        deviceSerial = getIntent().getStringExtra("deviceserial");
        verifyCode = getIntent().getStringExtra("verifycode");
//        deviceSerial = "585915553";//585915553 TODO 
//        verifyCode = "";
        new GetTokenThread().start();
    }
    
    private class GetTokenThread extends Thread{
        @Override
        public void run() {
            String sendJsonStr = SignUtil.getGetAccessTokenSign("13951415610");
            Log.v(tag, "-----sendjsonstr:" + sendJsonStr);
            String jsonRet = test3(sendJsonStr);
            Log.v(tag, "--------jsonret:" + jsonRet);
            String token = parseTokenJson(jsonRet);
            if (!TextUtils.isEmpty(token)){
                EZOpenSDK.getInstance().setAccessToken(token);
                try {
                    if (!TextUtils.isEmpty(deviceSerial)){
                        ezCameraInfo = EZOpenSDK.getInstance().getCameraInfo(deviceSerial);
                    }
                } catch (BaseException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(MSG_GET_END);
            } else {
                mHandler.sendEmptyMessage(MSG_GET_FAIL);
            }
        }
    }
    
    private String test3(String jsonStr) {
        String httpUrl = "https://open.ys7.com/api/method";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(httpUrl);

        httppost.addHeader("Content-Type", "application/json");
//        httppost.addHeader("User-Agent", "imgfornote");

        try {
            httppost.setEntity(new StringEntity(jsonStr));

            HttpResponse response;
            response = httpclient.execute(httppost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                String str = EntityUtils.toString(response.getEntity());
                Log.v(tag, "-----test3 result:" + str);
                return str;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //{"result":{"data":{"accessToken":"at.afgvvd5ict597v808mscxkesbgtwrmbp-86q9tubc8u-1xfqzfo-ws4nexf8m","userId":"aaa1b78a88294cb3"},"code":"200","msg":"操作成功!"}}
    private String parseTokenJson(String jsonStr){
        try {
            JSONObject jo = new JSONObject(jsonStr);
            JSONObject joResult = jo.getJSONObject("result");
            JSONObject joData = joResult.getJSONObject("data");
            String code = joResult.getString("code");
            String msg = joResult.getString("msg");//操作成功！
            String token = joData.getString("accessToken");
            String userId = joData.getString("userId");
            return token;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}