package com.ssiot.remote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import com.baidu.platform.comapi.map.B;
import com.baidu.platform.comapi.map.r;
import com.ssiot.fish.FishMainActivity;
import com.ssiot.remote.myzxing.MipcaActivityCapture;
import com.ssiot.remote.yun.webapi.WS_User;
import com.ssiot.agri.R;

public class SettingFrag extends BaseFragment{
    public static final String tag = "SettingFragment";
    private FSettingBtnClickListener mFSettingBtnClickListener;
    
    private TextView mVerStatusView;
    HashMap<String, String> mHashMap;
    private SharedPreferences mPref;
    
    public static final String ACTION_SSIOT_UPDATE = "com.ssiot.remote.update";
    BroadcastReceiver updateBroadcastReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            int checkRet = intent.getIntExtra("checkresult", -1);
            Log.v(tag, "----------updateBroadcastReceiver---" + checkRet);
            switch (checkRet) {
                case 0:
                    mVerStatusView.setText("未获取到最新版本信息");
                    break;
                case 1:
                    mVerStatusView.setText("有新版本");
                    break;
                case 2:
                    mVerStatusView.setText("已是最新");
                    break;

                default:
                    break;
            }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().registerReceiver(updateBroadcastReceiver, new IntentFilter(ACTION_SSIOT_UPDATE));
        mPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_setting_2, container, false);//20161014改动了UI
        
        TextView userNameView = (TextView) v.findViewById(R.id.setting_username);
        userNameView.setText(Utils.getStrPref(Utils.PREF_USERNAMETEXT, getActivity()));
        
        View bindBtn = (RelativeLayout) v.findViewById(R.id.setting_itm_addnode);
        bindBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), MipcaActivityCapture.class);//BindNodeAct
				startActivityForResult(i, REQEST_QR);
			}
		});
        initAppInfo(v);
        
        CheckBox cb = (CheckBox) v.findViewById(R.id.alarm_switch);
        cb.setChecked(mPref.getBoolean(Utils.PREF_ALARM, true));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (null != mPref){
                    Editor e = mPref.edit();
                    e.putBoolean(Utils.PREF_ALARM, isChecked);
                    e.commit();
                    if (isChecked){
                    	startBackService();
                    }
                }
            }
        });
        
        CheckBox cbOffLine = (CheckBox) v.findViewById(R.id.offline_switch);
        cbOffLine.setChecked(mPref.getBoolean(Utils.PREF_OFFLINE_NOTICE, false));
        cbOffLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (null != mPref){
                    Editor e = mPref.edit();
                    e.putBoolean(Utils.PREF_OFFLINE_NOTICE, isChecked);
                    e.putString(Utils.PREF_LAST_OFFLINE, "");//每次清除一下
                    e.commit();
                    if (isChecked){
                    	startBackService();
                    }
                }
            }
        });
        RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.offline_relativelayout);
        rl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),NodePickerAct.class);
				startActivity(intent);
			}
		});
        
        CheckBox cbSound = (CheckBox) v.findViewById(R.id.sound_switch);
        cbSound.setChecked(mPref.getBoolean(Utils.PREF_NOTI_SOUND, false));
        cbSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (null != mPref){
                    Editor e = mPref.edit();
                    e.putBoolean(Utils.PREF_NOTI_SOUND, isChecked);
                    e.commit();
                }
            }
        });
        
        initLogOutBtn(v);
        
        return v;
    }
    
    private void initAppInfo(View rootView){
    	TextView mVersionTextView = (TextView) rootView.findViewById(R.id.app_version);
        mVerStatusView = (TextView) rootView.findViewById(R.id.app_version_status);
        String text = getActivity().getResources().getString(R.string.app_name) + getCurVersionName(getActivity());
        mVersionTextView.setText(text);
        View b = (View) rootView.findViewById(R.id.checkupdate);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isNetworkConnected(getActivity())){
                    Toast.makeText(getActivity(), R.string.please_check_net, Toast.LENGTH_SHORT).show();
                }
                mVerStatusView.setText("");
                if (UpdateManager.updating){
                    Toast.makeText(getActivity(), "更新正在运行,请等待。", Toast.LENGTH_SHORT).show();
                } else {
//                    if (null != mFSettingBtnClickListener){
//                        mFSettingBtnClickListener.onFSettingBtnClick();
//                    }
                    UpdateManager mUpdaManager = new UpdateManager(getActivity());
                    if (mPref.getBoolean(Utils.PREF_AUTOUPDATE, true) == false){
                        Editor editor = mPref.edit();
                        editor.putBoolean(Utils.PREF_AUTOUPDATE, true);
                        editor.commit();
                    }
                    mUpdaManager.startGetRemoteVer();
                }
            }
        });
    }
    
    private void initLogOutBtn(View rootView){
    	View v = rootView.findViewById(R.id.logout);
    	v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Context context = getActivity();
				AlertDialog.Builder builder = new Builder(context);
				builder.setTitle("确认退出？").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getActivity().setResult(Activity.RESULT_OK);
						getActivity().finish();
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				builder.create().show();
			}
		});
    }
    
    private void startBackService(){
    	Intent localIntent = new Intent();  
        localIntent.setClass(getActivity(), SsiotService.class);
        getActivity().startService(localIntent);
    }
    
    private static final int REQEST_QR = 1;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == REQEST_QR){
    		if (resultCode == Activity.RESULT_OK){
    			final String account = Utils.getStrPref(Utils.PREF_USERNAME, getActivity());
    			final String qrString = data.getStringExtra("");
    			new Thread(new Runnable() {
					@Override
					public void run() {
						int ret = new WS_User().UserBindNode(account, qrString);//TODO 等待的UI 
						if (ret <= 0 ){
							showToastMSG("操作失败:" + qrString);
						} else {
							showToastMSG("操作成功:" + qrString);
						}
					}
				});
    			
    		}
    	}
    }
    
    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(updateBroadcastReceiver);
        super.onDestroyView();
    }
    
    private String getCurVersionName(Context c){
        String versionName = "";
        try {
            versionName = c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionName;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.Setting, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Log.v(tag, "----------------action-settting");
//                break;
//
//            default:
//                break;
//        }
        return true;
    }
    
    public void setClickListener(FSettingBtnClickListener listen){
        mFSettingBtnClickListener = listen;
    }
    
    //回调接口，留给activity使用
    public interface FSettingBtnClickListener {  
        void onFSettingBtnClick();  
    }
}