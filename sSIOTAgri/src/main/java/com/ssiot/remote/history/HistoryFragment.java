package com.ssiot.remote.history;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.remote.BaseFragment;
import com.ssiot.remote.BrowserActivity;
import com.ssiot.remote.GetImageThread;
import com.ssiot.remote.Utils;
import com.ssiot.remote.GetImageThread.ThumnailHolder;
import com.ssiot.agri.R;
import com.ssiot.remote.data.model.ERPProductsPackModel;
import com.ssiot.remote.data.model.TraceProfileModel;
import com.ssiot.remote.myzxing.MipcaActivityCapture;
import com.ssiot.remote.yun.webapi.WS_ProductsPack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends BaseFragment{
    public static final String tag = "HisFragment";
    private static final String URL_START = "http://t.ssiot.com/?p=";
    private ListView mListView;
    private ViewPager pager;
    ArrayList<View> viewList = new ArrayList<View>();
    RadioGroup rGroup;
    View indicater;
    private TraceAdapter mAllAdapter;
    ArrayList<ERPProductsPackModel> mAllTraces = new ArrayList<>();
    private String userKey;
    
    private static final int MSG_GET_SINGLE_END = 1;
    private static final int MSG_QRIMG = GetImageThread.MSG_GETFTPIMG_END;
    private static final int MSG_NOTIFY_HIS = 4;
    private static final int MSG_GET_ALL_END = 3;
    
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (!isVisible()){
                Log.e(tag, "-----not visible: msg:" + msg.what);
                return;
            }
            switch (msg.what) {
                case MSG_GET_SINGLE_END:
                    break;
                case MSG_NOTIFY_HIS:
                    break;
                case MSG_GET_ALL_END:
                    mAllAdapter.notifyDataSetChanged();
                    break;
                case MSG_QRIMG:
                    ThumnailHolder thumb = (ThumnailHolder) msg.obj;
                    int pix = thumb.imageView.getWidth();
                    RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(pix, pix);//把二维码寬高最大化
                    thumb.imageView.setLayoutParams(rl);
                    thumb.imageView.setImageBitmap(thumb.bitmap);
                    break;

                default:
                    break;
            }
        };
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userKey = Utils.getStrPref(Utils.PREF_USERKEY,getActivity());
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(tag, "----onCreateView----");
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ImageButton cameraButton = (ImageButton) rootView.findViewById(R.id.startcamera);
        pager = (ViewPager) rootView.findViewById(R.id.pager);
        rGroup = (RadioGroup) rootView.findViewById(R.id.tabs);
        rGroup.setVisibility(View.GONE);
        indicater = (View) rootView.findViewById(R.id.indicator);
        initRadioGroup();
        initViewPager(inflater);
        setHisListTitleColor(rootView);
        mListView = (ListView) rootView.findViewById(R.id.his_his_list);
        
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, REQUEST_CODE_F_SCAN);
            }
        });

//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mFHisBtnClickListener && null != mEditText){
//                    final String code = mEditText.getText().toString();
//                    startSearch(code);
//                    if (TextUtils.isEmpty(code)){
//                        Toast.makeText(getActivity(), R.string.procode_not_found, Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
////                            TraceProfileModel m = new AjaxHistory().getTraceProfile(code);
////                            if (null != m){
////                                startBrowserAndSave(code, m);
////                            } else {
////                                showToastMSG(getResources().getString(R.string.procode_not_found));
////                            }
//                        }
//                    }).start();
//                }
//            }
//        });

        return rootView;
    }
    
    private void initRadioGroup(){
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (null != pager) {
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    Log.v(tag, "----------onCheckedChanged----------" + checkedId + radioButton.isChecked());
                    if (radioButton.isChecked()) {
                        switch (radioButton.getId()) {
                            case R.id.radio0:
                                pager.setCurrentItem(0, true);
                                break;
                            case R.id.radio1:
                                pager.setCurrentItem(1, true);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        });
    }
    
    private void initViewPager(LayoutInflater inflater){
        viewList.clear();
        viewList.add(inflater.inflate(R.layout.list_container, null, false));
        //viewList.add(inflater.inflate(R.layout.list_container, null, false));
        pager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View page = viewList.get(position);
                container.addView(page);
//                fillPagerViewDataFromList(page, listDatas, position);
                fillPage(page, position);
                return page;
            }
            
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(container.getChildAt(position));
            }
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }
            
            @Override
            public int getCount() {
                return viewList.size();
            }
        });
        pager.setOnPageChangeListener(new OnPageChangeListener() {
            
            @Override
            public void onPageSelected(int arg0) {
                ((RadioButton) rGroup.getChildAt(arg0)).setChecked(true);
            }
            
            @Override
            public void onPageScrolled(int arg0, float argfloat, int arg2) {
//                Log.v(tag, "----onPageScrolled----" +arg0+"float:"+ argfloat + " arg2:" + arg2);
                View localView = rGroup.getChildAt(arg0);//rGroup.findViewById());
                ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams) indicater.getLayoutParams();
                localMarginLayoutParams.width = (localView.getRight() - localView.getLeft());
                localMarginLayoutParams.leftMargin = ((int)(argfloat * localMarginLayoutParams.width) + localView.getLeft());
                indicater.setLayoutParams(localMarginLayoutParams);
                indicater.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
    
    private void fillPage(View page, int index){
        if (index == 0){
            ListView mAllListView = (ListView) page.findViewById(R.id.mylist);
            mAllAdapter = new TraceAdapter(getActivity(),mAllTraces);
            mAllListView.setAdapter(mAllAdapter);
            mAllListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ERPProductsPackModel model = mAllTraces.get(position);
                    String code = model._qrcode;
                    startBrowser(code);
                }
            });
            new GetUserTraceProfiles().start();
        } else {
        }
    }
    
    private void startBrowser(String code){
        Intent brower = new Intent(getActivity(), BrowserActivity.class);
        brower.putExtra("url", URL_START + code);
        startActivity(brower);
    }
    
    private class GetUserTraceProfiles extends Thread{
        @Override
        public void run() {
            sendShowMyDlg("正在获取溯源信息...");
            mAllTraces.clear();
//            int userid = Utils.getIntPref(Utils.PREF_USERID, getActivity());
            int mainuserid = Utils.getIntPref(Utils.PREF_MAIN_USERID, getActivity());
            List<ERPProductsPackModel> list = new WS_ProductsPack().GetProductsPack(mainuserid);
            List<ERPProductsPackModel> list2 = new WS_ProductsPack().GetProductsPackVirtual(mainuserid);

            if (null != list && list.size() > 0){
                mAllTraces.addAll(list);
            }
            if (null != list2){
                mAllTraces.addAll(list2);
            }
            sendDismissDlg();
            mHandler.sendEmptyMessage(MSG_GET_ALL_END);
        }
    }
    
    private void startSearch(String code){
        sendShowMyDlg("正在获取溯源信息...");
        new GetTraceProfileThread(code).start();
        View view = getActivity().getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
    private void setHisListTitleColor(View rootView){
        View v = rootView.findViewById(R.id.his_his_title);
        v.findViewById(R.id.his_itm_name).setBackgroundColor(getResources().getColor(R.color.ssiotgreen));
        v.findViewById(R.id.his_itm_batch).setBackgroundColor(getResources().getColor(R.color.ssiotgreen));
        v.findViewById(R.id.his_itm_time).setBackgroundColor(getResources().getColor(R.color.ssiotgreen));
        v.setVisibility(View.GONE);
    }
    
    private class GetTraceProfileThread extends Thread{
        String code;
        public GetTraceProfileThread(String code){
            this.code = code;
        }
        @Override
        public void run() {
//            mModel = new AjaxHistory().getTraceProfile(code);
//            insertToLocalDB(mModel);
//            sendDismissDlg();
//            mHandler.sendEmptyMessage(MSG_GET_SINGLE_END);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    private static final int REQUEST_CODE_F_SCAN = 1;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(tag, "------onActivityResult----" + requestCode +resultCode);
        switch (requestCode) {
            case REQUEST_CODE_F_SCAN:
                if (resultCode == Activity.RESULT_OK){
                    Bundle bundle = data.getExtras();
                    String qrcodeStr = bundle.getString("result");
                    Log.v(tag,"二维码："+qrcodeStr);
                    int index = qrcodeStr.indexOf(URL_START);
                    if (index > -1){
                        //qrcodeStr = qrcodeStr.substring(URL_START.length(), qrcodeStr.length());
                        Intent brower = new Intent(getActivity(), BrowserActivity.class);
                        brower.putExtra("url", qrcodeStr);
                        startActivity(brower);
                    } else {
                        Toast.makeText(getActivity(), "错误的溯源码", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }
    
    @Override
    public boolean canGoback(){
        return false;
    }
    
    @Override
    public void onMyBackPressed(){//add by jingbo
    }

    //回调接口，留给activity使用
    public interface FHisBtnClickListener {  
        void onFHisBtnClick(TraceProfileModel m, boolean forceScan);  
    }
    
    

    private void showQRDialog(final String imgUrl){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout square = (RelativeLayout) inflater.inflate(R.layout.dia_qrcode, null);
        final ImageView imageView = (ImageView) square.findViewById(R.id.qr_imageview);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new GetImageThread(imageView, "http://cloud.ssiot.com/" + imgUrl, mHandler).start();
            }
        }).start();
        builder.setView(square);
        builder.create().show();
    }
    
    private class TraceAdapter extends BaseAdapter{
        private LayoutInflater mInflater;
        private List<ERPProductsPackModel> mDatas;
        public TraceAdapter(Context c, List<ERPProductsPackModel> mdatas){
            mInflater = LayoutInflater.from(c);
            mDatas = mdatas;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView){
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.his_list_item, null);
                holder.his_itm_name = (TextView) convertView.findViewById(R.id.his_itm_name);
                holder.his_itm_batch = (TextView) convertView.findViewById(R.id.his_itm_batch);
                holder.his_itm_time = (TextView) convertView.findViewById(R.id.his_itm_time);
                holder.his_itm_qr = (ImageView) convertView.findViewById(R.id.his_itm_qr);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ERPProductsPackModel m = mDatas.get(position);
            holder.his_itm_name.setText("品名："+m._name);
            holder.his_itm_batch.setText("批次:"+m._probatchno);
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd HH:mm");
            holder.his_itm_time.setText("日期:"+ sdf.format(m._createtime));
            holder.his_itm_qr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showQRDialog(m._qrimgurl);
                }
            });
            return convertView;
        }
        
        private class ViewHolder{
            TextView his_itm_name;
            TextView his_itm_batch;
            TextView his_itm_time;
            ImageView his_itm_qr;
        }
    }
    
}