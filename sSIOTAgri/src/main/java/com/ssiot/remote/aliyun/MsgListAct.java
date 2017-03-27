package com.ssiot.remote.aliyun;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ssiot.agri.R;
import com.ssiot.remote.BrowserActivity;
import com.ssiot.fish.HeadActivity;
import com.ssiot.remote.Utils;
import com.ssiot.remote.dblocal.LocalDBHelper;

public class MsgListAct extends HeadActivity{
	private static final String tag = "MsgListAct";
	ListView mListView;
	LocalDBHelper dbHelper;
	List<MsgBean> mMsgs = new ArrayList<MsgBean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_list);
		dbHelper = new LocalDBHelper(this);
		initViews();
	}
	
	@Override
	protected void onDestroy() {
		dbHelper.close();
		super.onDestroy();
	}
	
	private void initViews(){
		mListView = (ListView) findViewById(R.id.noti_his);
		mMsgs.clear();
		List<MsgBean> list = queryFromLocalDB();
		if (list != null){
			mMsgs.addAll(list);
		}
		InfoAdapter mAdapter = new InfoAdapter(mMsgs);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!TextUtils.isEmpty(mMsgs.get(position).url)){
					Intent intent = new Intent(MsgListAct.this, BrowserActivity.class);
					intent.putExtra("url", mMsgs.get(position).url);
					startActivity(intent);
				}
			}
		});
	}
	
//	private void insertNotiToLocalDB(String msg){
//        if (null == msg){
//            Log.e(tag, "----msg = null");
//            return;
//        }
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("DetailStr", msg);
////        values.put("CreateTime", model._code);
//    
//        db.insert("msghistory", null, values);//nullcolumnhack is null or "id"?
//        db.close();
//    }
	
	private List<MsgBean> queryFromLocalDB(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("msghistory", null, null, null, null, null, "id asc");
        int idIndex = cursor.getColumnIndex("id");
        int titleIndex = cursor.getColumnIndex("TitleStr");
        int strIndex = cursor.getColumnIndex("DetailStr");
        int urlIndex = cursor.getColumnIndex("UrlStr");
        int timeIndex = cursor.getColumnIndex("CreateTime");
        List<MsgBean> hisList = new ArrayList<MsgBean>();
        for (cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext()) {
        	MsgBean hisBean = new MsgBean();
        	hisBean.id = cursor.getInt(idIndex);
        	hisBean.title = cursor.getString(titleIndex);
            hisBean.detail = cursor.getString(strIndex);
            hisBean.url = cursor.getString(urlIndex);
            String timeStr = cursor.getString(timeIndex);
            hisBean.timeStr = timeStr;
            hisList.add(hisBean);
        }
        cursor.close();
        db.close();
        return hisList;
    }
	
	private class InfoAdapter extends BaseAdapter{
		private List<MsgBean> msgs;
		LayoutInflater inflater;
		
		private InfoAdapter(List<MsgBean> notis){
			msgs = notis;
			inflater = LayoutInflater.from(MsgListAct.this);
		}
		@Override
		public int getCount() {
			return msgs.size();
		}

		@Override
		public Object getItem(int position) {
			return msgs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.itm_title_content, null, false);
			TextView titleView = (TextView) convertView.findViewById(R.id.txt_title);
			TextView contentView = (TextView) convertView.findViewById(R.id.txt_content);
			TextView timeView = (TextView) convertView.findViewById(R.id.txt_time);
			MsgBean m = msgs.get(position);
			titleView.setText(m.title + (TextUtils.isEmpty(m.url) ? "" : "(详细)"));
			contentView.setText(m.detail);
			timeView.setText(m.timeStr);
			return convertView;
		}
	}
	
}