package com.ssiot.fish;

import java.util.ArrayList;
import java.util.List;

import com.ssiot.agri.R;
import com.ssiot.remote.aliyun.TxtAct;
import com.ssiot.remote.data.model.ArticleModel;
import com.ssiot.remote.yun.NewsAct;
import com.ssiot.remote.yun.webapi.WS_Other;
import com.ssiot.remote.yun.widget.QQHorizontalScrollView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ArticleListAct extends HeadActivity{
	QQHorizontalScrollView slideView;
	ListView mArticleListView;
	MyAdapter adapter;
	List<ArticleModel> articles = new ArrayList<ArticleModel>();
	LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = LayoutInflater.from(this);
		setContentView(R.layout.slide_layout);
		initViews();
	}
	
	private void initViews(){
		mArticleListView = (ListView) findViewById(R.id.list_articles);
		adapter = new MyAdapter();
		mArticleListView.setAdapter(adapter);
		mArticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(ArticleListAct.this, NewsAct.class);
				i.putExtra("articleid", articles.get(position)._id);
				startActivity(i);
			}
			
		});
		new GetArticlesThread().start();
	}
	
	private class MyAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return articles.size();
		}

		@Override
		public Object getItem(int position) {
			return articles.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHold viewHold;
			if (null == convertView){
				convertView = inflater.inflate(R.layout.list_item, null, false);
				viewHold = new ViewHold();
				viewHold.mTitleView = (TextView) convertView.findViewById(R.id.txt_title);
				viewHold.mContentView = (TextView) convertView.findViewById(R.id.txt_content);
				convertView.setTag(viewHold);
			} else {
				viewHold = (ViewHold) convertView.getTag();
			}
			ArticleModel a = articles.get(position);
			viewHold.mTitleView.setText(a._title);
			viewHold.mContentView.setText(a._description);
			return convertView;
		}
		
		private class ViewHold{
			TextView mTitleView;
			TextView mContentView;
		}
		
	}
	
	private class GetArticlesThread extends Thread{
		@Override
		public void run() {
			articles.clear();
			List<ArticleModel> list = new WS_Other().GetArticles(1, 10, "1=1");//ArticleTypeCode=0801 TODO
			if (null != list && list.size() > 0){
				articles.addAll(list);
			}
			runOnUiThread(new Runnable() {
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
		}
	}
}