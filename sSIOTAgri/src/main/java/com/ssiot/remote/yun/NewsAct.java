package com.ssiot.remote.yun;

import java.net.URL;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.ssiot.agri.R;
import com.ssiot.fish.HeadActivity;
import com.ssiot.remote.yun.webapi.WS_Other;

public class NewsAct extends HeadActivity{
	private static final String tag = "NewsAct";
	int articleid = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String str = getIntent().getStringExtra("msg");
		setContentView(R.layout.act_txt);
		articleid = getIntent().getIntExtra("articleid", 0);
		final TextView t = (TextView) findViewById(R.id.txt);
//		t.setText(str);
//		if (null != str && str.startsWith("<html>")){
//			startShowHtml(t, str);
//		}
		startShowHtml(t, str);
	}
	
	private void startShowHtml(final TextView t, final String html){
		t.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		final ImageGetter imgGetter = new Html.ImageGetter() {
			public Drawable getDrawable(String source) {
				Log.v(tag, "--------getDrawable:source:" + source);
				Drawable drawable = null;
				URL url;
				try {
					url = new URL(source);
					drawable = Drawable.createFromStream(url.openStream(), ""); // 获取网路图片
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 2,
						drawable.getIntrinsicHeight() * 2);
				return drawable;
			}
		};
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String str = new WS_Other().GetArticleContent(articleid);
				if (TextUtils.isEmpty(str)){
					return;
				}
				final Spanned spanned = Html.fromHtml(str, imgGetter, null);
				runOnUiThread(new Runnable() {
					public void run() {
						t.setText(spanned);
					}
				});
			}
		}).start();
	}
	
	
	
//OnDownloadListener onDownloadListener=new OnDownloadListener() {
//        
//        //下载进度
//        public void onDownloadUpdate(DownLoadUtils manager, int percent) {
//            // TODO Auto-generated method stub
//            Log.i("DEBUG", percent+"");
//        }
//        
//        //下载失败
//        public void onDownloadError(DownLoadUtils manager, Exception e) {
//            // TODO Auto-generated method stub
//            
//        }
//        
//        //开始下载
//        public void onDownloadConnect(DownLoadUtils manager) {
//            // TODO Auto-generated method stub
//            Log.i("DEBUG", "Start  //////");
//        }
//        
//        //完成下载
//        public void onDownloadComplete(DownLoadUtils manager, Object result) {
//            // TODO Auto-generated method stub
//            Log.i("DEBUG", result.toString());
//            //替换sTExt的值，就是把图片的网络路径换成本地SD卡图片路径(最早想法，可以不需要这样做了)
//            //sText.replace(result.toString(), path+String.valueOf(result.hashCode()));
//            //再一次赋值给Textview
//            tView.setText(Html.fromHtml(sText, imageGetter, null));
//        }
//    };
}