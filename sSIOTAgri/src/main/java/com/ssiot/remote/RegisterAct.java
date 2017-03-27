package com.ssiot.remote;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.R.integer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.platform.comapi.map.r;
import com.ssiot.fish.HeadActivity;
import com.ssiot.remote.yun.webapi.WS_API;
import com.ssiot.remote.yun.webapi.WS_Register;
import com.ssiot.remote.yun.webapi.WS_User;
import com.ssiot.agri.R;

public class RegisterAct extends HeadActivity{
	private static final String tag = "RegisterAct";
	EditText mPhoneEdit;
	EditText mVerifyEdit;
	TextView mSmsBtn;
	EditText mPswdEdit;
	EditText mPswdEdit2;
	EditText mInviteEdit;
	CheckBox mLicenceBox;
	View mRegisterBtn;
	int timeCount = 60;
	
	private static final int MSG_SEND_SUCCESS = 1;
	private static final int MSG_SEND_FAIL_EXIST = 2;
	private static final int MSG_SEND_FAIL_ERROR = 3;
	private static final int MSG_TIME_COUNT = 4;
	private static final int MSG_REGISTER_OK = 5;
//	private static final int MSG_REGIST_END = 5;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SEND_SUCCESS:
				disableSendUI();
				break;
			case MSG_SEND_FAIL_EXIST:
				showToast("帐号已存在，请直接登陆");
				break;
			case MSG_SEND_FAIL_ERROR:
				showToast("未知问题, 请稍后再试");
				break;
			case MSG_TIME_COUNT:
				timeCount --;
				if (timeCount > 0){
					mHandler.removeMessages(MSG_TIME_COUNT);
					mSmsBtn.setText(timeCount + "秒后重试");
					mHandler.sendEmptyMessageDelayed(MSG_TIME_COUNT, 1000);
				} else {
					mSmsBtn.setEnabled(true);
					mSmsBtn.setText(R.string.register_get_verify);
				}
				break;
			case MSG_REGISTER_OK:
				showResultDialog(true);
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mobile_login_regesiter_layout);//act_register
		initViews();
		//TODO 忘记密码找回界面?
	}
	
	private void initViews(){
		mPhoneEdit = (EditText) findViewById(R.id.et_phone_num);
		mSmsBtn = (TextView)findViewById(R.id.tv_send_verify_code);
		mVerifyEdit = (EditText) findViewById(R.id.et_verify_code);
		mPswdEdit = (EditText) findViewById(R.id.et_register_password);
		mPswdEdit2 = (EditText) findViewById(R.id.et_register_password_2);
		mInviteEdit = (EditText) findViewById(R.id.et_register_invite);
		mRegisterBtn = findViewById(R.id.btn_submit);
		
		mSmsBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//先检查帐号是否存在
				// 发短信接口
				String phoneString = mPhoneEdit.getText().toString().trim();
				if (!TextUtils.isEmpty(phoneString) && phoneString.length() == 11){
					new CheckExistAndSendThread(phoneString).start();
				} else {
					showToast("请输入正确的11位手机号码");
				}
			}
		});
		
		mRegisterBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phoneStr = mPhoneEdit.getText().toString();
				String verifyStr = mVerifyEdit.getText().toString();
				String pwd = mPswdEdit.getText().toString();
				String pwd2 = mPswdEdit2.getText().toString();
				if (null == pwd || !pwd.equals(pwd2)){
					showToast("密码为空或者不一致");
					return;
				}
				if (!checkPwd(pwd)){
					return;
				}
				String inviteStr = mInviteEdit.getText().toString();
				// 注册接口
				if (!TextUtils.isEmpty(inviteStr)){
					int userid = checkInviteCodeUser(inviteStr);
					if (userid > 0){
						new RegisterThread(phoneStr, verifyStr, pwd, ""+userid).start();
					} else {
						showToast("邀请码错误");
					}
				} else {
					new RegisterThread(phoneStr, verifyStr, pwd, "").start();
				}
				
			}
		});
	}
	
	private boolean checkPwd(String pwd){
		if (null == pwd || pwd.length() < 6 || pwd.length() > 16){
			showToast("密码长度不符合要求");
			return false;
		}
		for (int i = 0; i < pwd.length(); i ++){
			char ch = pwd.charAt(i);
			if ((ch >= '0' && ch <= '9') ||
					(ch >= 'A' && ch <= 'Z') ||
					(ch >= 'a' && ch <= 'z')){
				return true;
			} else {
				showToast("只能是数字和字母");
				return false;
			}
		}
		return true;
	}
	
	private void disableSendUI(){
		mSmsBtn.setText("已经发送");
		mSmsBtn.setEnabled(false);
		timeCount = 60;
		mHandler.sendEmptyMessage(MSG_TIME_COUNT);
//		mSmsBtn.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				mSmsBtn.setEnabled(true);
//				mSmsBtn.setText(R.string.register_get_verify);
//			}
//		}, 60 * 1000);
	}
	
	private class CheckExistAndSendThread extends Thread{
		String phoneStr;
		private CheckExistAndSendThread(String phone){
			phoneStr = phone;
		}
		@Override
		public void run() {
			Log.v(tag, "=============" + phoneStr + getMD5(phoneStr));
			int ret = new WS_User().CheckAccountExist(phoneStr);
			if (ret == 0){
				int smsRet = new WS_User().SendSms(phoneStr, getMD5(phoneStr));
				if (smsRet == 1){
					mHandler.sendEmptyMessage(MSG_SEND_SUCCESS);
				} else {
					mHandler.sendEmptyMessage(MSG_SEND_FAIL_ERROR);
				}
			} else if (ret == 1){//帐号已存在
				mHandler.sendEmptyMessage(MSG_SEND_FAIL_EXIST);
			} else {
				mHandler.sendEmptyMessage(MSG_SEND_FAIL_ERROR);
			}
		}
	}
	
	private class RegisterThread extends Thread{
		String phonetmp;
		String verifyCodetmp;
		String pwdtmp;
		String inviteCodetmp;
		private RegisterThread(String phone, String verifyCode, String pwd, String inviteCode){
			phonetmp = phone;
			verifyCodetmp = verifyCode;
			pwdtmp = pwd;
			inviteCodetmp = inviteCode;
		}
		@Override
		public void run() {
			int ret = new WS_User().StartRegister(phonetmp, verifyCodetmp, pwdtmp, inviteCodetmp);
			if (ret > 0){
				mHandler.sendEmptyMessage(MSG_REGISTER_OK);
			} else if (ret == -3){
				sendToast("验证码超时");
			} else if (ret == -2){
				sendToast("邀请码不存在");
			} else if (ret == -1){
				sendToast("无验证码");
			} else {
				sendToast("请检查网络后重试");
			}
		}
	}
	
	private String getMD5(String str){
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] bs = md5.digest(str.getBytes());
	        StringBuilder sb = new StringBuilder(40);
	        for(byte x:bs) {
	            if((x & 0xff)>>4 == 0) {
	                sb.append("0").append(Integer.toHexString(x & 0xff));
	            } else {
	                sb.append(Integer.toHexString(x & 0xff));
	            }
	        }
	        return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private int checkInviteCodeUser(String inviteCode){
		if (!TextUtils.isEmpty(inviteCode) && inviteCode.length() == 8){
			String useridStr = inviteCode.substring(4, 8);
			int userid = Integer.parseInt(useridStr);
			int ii = Integer.parseInt(inviteCode.substring(0, 4));
			if (ii == (userid * 6789)%10000){//简单加密防止乱用
				return userid;
			}
		}
		return -1;
	}
	
	private void showResultDialog(final boolean ret){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setCancelable(false);
		builder.setMessage(ret ? "注册成功" : "注册失败");
		builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (ret){
					finish();
				} else {
					
				}
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
}