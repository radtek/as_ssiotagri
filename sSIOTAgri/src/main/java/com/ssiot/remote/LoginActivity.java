/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ssiot.remote;

import java.util.List;
import java.util.Properties;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import android.support.v7.app.ActionBarActivity;

import com.ssiot.fish.FishMainActivity;
import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.data.model.UserModel;
import com.ssiot.remote.yun.MainAct;
import com.ssiot.remote.yun.webapi.WS_User;
public class LoginActivity extends HeadActivity {
    private static final String tag = "LoginActivity";
    private EditText logEditText;
    private EditText pwdEditText;
    private CheckBox checkbox;
    private Button logButton;
    private TextView registerBtn;
    private String account = "";
    private String password = "";
    private String uniqueID = "";
    private Dialog mWaitDialog;
    SharedPreferences mPref;

    private final int MSG_LOGIN_RETURN = 1;
    private final int MSG_LOGIN_TIMEOUT = 2;
    private final int MSG_LOGIN_CON_FAIL = 3;
    private final int MSG_TOAST = 4;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (null != mWaitDialog){
                mWaitDialog.dismiss();
            }
            switch (msg.what) {
                case MSG_LOGIN_RETURN://登陆成功
                    removeMessages(MSG_LOGIN_TIMEOUT);
                    if (!TextUtils.isEmpty(uniqueID)) {
                        SharedPreferences mPref = PreferenceManager
                                .getDefaultSharedPreferences(LoginActivity.this);
                        if (mPref != null) {
                            Editor editor = mPref.edit();
                            editor.putString(Utils.PREF_USERNAME, account);
                            editor.putString(Utils.PREF_PWD, password);
                            editor.putString("rememberPWD", checkbox.isChecked() ? "yes" : "no");
                            editor.commit();
                        }
                        Intent intent = new Intent(LoginActivity.this, FishMainActivity.class);
                        startActivity(intent);
                        
                        Intent myintent = new Intent(LoginActivity.this, SsiotService.class);
                        startService(myintent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "用户名或密码有误",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_LOGIN_TIMEOUT:
                    Toast.makeText(getApplicationContext(), "登陆失败",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MSG_LOGIN_CON_FAIL:
                    Toast.makeText(getApplicationContext(), "连接服务器失败",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MSG_TOAST:
                	String str = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), str,
                            Toast.LENGTH_SHORT).show();
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
        
        setContentView(R.layout.activity_login);
        View loginHolder = findViewById(R.id.login_content);
        Animation anim = AnimationUtils.loadAnimation(this,
                R.anim.translate_up);
        loginHolder.startAnimation(anim);//上滑动画
        /*
         * LayoutInflater layout=this.getLayoutInflater(); View
         * view=layout.inflate(R.layout.activity_main,null);
         */
        logEditText = (EditText) findViewById(R.id.logEditText);
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);
        checkbox = (CheckBox) findViewById(R.id.checkBox1);
        logButton = (Button) findViewById(R.id.logButton);
        registerBtn = (TextView) findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterAct.class);
				startActivity(intent);
			}
		});
//        registerBtn.setVisibility(View.GONE);
        TextView visitorBtn = (TextView) findViewById(R.id.visitor_btn);
        visitorBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, FishMainActivity.class);
				startActivity(intent);
				finish();
			}
		});
        
        
        if (mPref != null) {
        	checkPrefVersion();
        	
            String pro_username = mPref.getString(Utils.PREF_USERNAME, "");
            String pro_password = mPref.getString(Utils.PREF_PWD, "");
            String pro_isRemember = mPref.getString("rememberPWD", "");
            if (!TextUtils.isEmpty(pro_username)) {
                loadUserInfo(pro_username, pro_password, pro_isRemember);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != logEditText){
            logEditText.requestFocus();
        }
        logButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	account = logEditText.getText().toString();
                password = pwdEditText.getText().toString();
                String isRemember;

                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "用户名和密码不能为空", Toast.LENGTH_LONG)
                            .show();
                } else {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                    builder.setMessage("正在连接服务器...");
//                    mWaitDialog = builder.create();
                    mWaitDialog = createLoadingDialog(LoginActivity.this, "正在登陆");
                    mWaitDialog.show();
                    mHandler.sendEmptyMessageDelayed(MSG_LOGIN_TIMEOUT, 8000);
                    new Thread(new getUniqueID()).start();
                }
            }
        });

    }

    /*
     * Ԥ�����û���Ϣ
     * @param name �û���
     * @param password ����
     * @param isRemember �Ƿ��ס����
     */
    private void loadUserInfo(String name, String password, String isRemember) {
        if (isRemember.contains("yes")) {
            logEditText.setText(name);
            pwdEditText.setText(password);
            checkbox.setChecked(true);
        } else if (!TextUtils.isEmpty(name)) {
            logEditText.setText(name);
        }
    }

    private class getUniqueID implements Runnable {
        @Override
        public void run() {
            try {
                if (Utils.isNetworkConnected(LoginActivity.this)){
                    UserModel uModel = new WS_User().GetUserByPsw(account, password);
                    if (uModel != null){
                    	uModel._userpassword = password;
                    	uniqueID = uModel._uniqueid;
                    	int mainuserid = new WS_User().getMainUserId(uModel._userid);
                    	savePrefs(uModel, mainuserid);
                        mHandler.sendEmptyMessage(MSG_LOGIN_RETURN);
                        return;
                    } else {
                    	sendToast("用户名或密码错误");
                    	return;
                    }
                } else {
                	sendToast("请检查网络");
                }
            } catch (Exception e) {
            	sendToast("出现异常，请重试");
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void sendToast(String msg){
    	Message m = mHandler.obtainMessage(MSG_TOAST);
    	m.obj = msg;
    	mHandler.sendMessage(m);
    }
    
    private void savePrefs(UserModel model, int mainuserid){
        Editor e = mPref.edit();
        e.putInt(Utils.PREF_USERID, model._userid);
        e.putString(Utils.PREF_USERNAME, model._account);
        e.putString(Utils.PREF_PWD, model._userpassword);
        e.putString(Utils.PREF_USERKEY, model._uniqueid);
        e.putString(Utils.PREF_USERNAMETEXT, model._username);
        e.putInt(Utils.PREF_AREAID, model._areaid);
//        e.putString(Utils.PREF_ADDR, model._address);
//        e.putString(Utils.PREF_AVATAR, model._avatar);
        e.putInt(Utils.PREF_PARENTID, model._parentid);
        e.putInt(Utils.PREF_GROUPID, model._usergroupid);
        e.putInt(Utils.PREF_USERTYPE, model._type);
        e.putInt(Utils.PREF_USERDEVICETYPE, model._devicetype);
        e.putInt(Utils.PREF_MAIN_USERID, mainuserid);
        e.commit();
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
    
    public Dialog createLoadingDialog(Context context, String msg) {  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view  
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        // main.xml中的ImageView  
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);  
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字  
        // 加载动画  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                context, R.anim.loading_animation);  
        // 使用ImageView显示动画  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
        tipTextView.setText(msg);// 设置加载信息  
  
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog  
  
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消  
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局  
        return loadingDialog;  
    }
}
