package com.ssiot.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.ssiot.fish.HeadActivity;

public class HttpAct extends HeadActivity {
	private DefaultHttpClient httpClient;
    private HttpPost httpPost;
    private HttpEntity httpEntity;
    private HttpResponse httpResponse;
    public static String PHPSESSID = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				httppost();
				
			}
		}).start();
		
	}
	
	private void httppost() {
		ArrayList pairs = new ArrayList();
		pairs.add(new BasicNameValuePair("q", "top"));

		try {
			// 2.编码键值对来符合 URL 规范
			UrlEncodedFormEntity encodedEntity = new UrlEncodedFormEntity(pairs);

			// 3.创建一个 HTTP Post 的请求对象
			HttpPost post = new HttpPost("http://wapcart.fisher88.com");//http://dict.youdao.com/search

			// 4.放入编码过的键值对，最终得到类似如下 Get 请求的形式：
			// http://dict.youdao.com/search?q=top
			post.setEntity(encodedEntity);

			// 5.得到一个默认的 HTTP 客户端来执行上面的 Post 请求
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(post);

			// 6.得到 HTTP 应答
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();

			// 最好设定为最多可能收到的字节数，以减少动态变化时的性能损耗
			ByteArrayBuffer bArray = new ByteArrayBuffer(1024);
			byte[] ba = new byte[1024];
			int i = 0;

			// 7.从 HTTP 应答中读取收到的字节数据
			while (-1 != (i = is.read(ba))) {
				bArray.append(ba, 0, i); // i 为读出的实际字节个数
			}

			// 8.根据页面实际的编码格式将字节数组转化成相应的字符串对象(注：这里写死了为 utf-8 )
			final String strHtmlPage = EncodingUtils.getString(bArray.toByteArray(),
					"utf-8");

			// 用 TextView 显示 HTML 文档
//			TextView tv = new TextView(this);
//			// 加上如下语句，TextView 就可以拖动看到屏幕显示不完的内容了
//			tv.setMovementMethod(new ScrollingMovementMethod());
//			tv.setText(strHtmlPage);
//			setContentView(tv);

			// 或用 WebView 显示解释后的网页
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					WebView wv = new WebView(HttpAct.this);
					wv.loadData(strHtmlPage, "text/html", "utf-8");
					setContentView(wv);
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
//	public String executeRequest(String path, List<NameValuePair> params) {
//        String ret = "none";
//        try {
//            this.httpPost = new HttpPost(BASEPATH + path);
//            httpEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//            httpPost.setEntity(httpEntity);
//            //第一次一般是还未被赋值，若有值则将SessionId发给服务器
//            if(null != PHPSESSID){
//                httpPost.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
//            }            
//            httpClient = new DefaultHttpClient();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        try {
//            httpResponse = httpClient.execute(httpPost);
//            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                HttpEntity entity = httpResponse.getEntity();
//                ret = EntityUtils.toString(entity);
//                CookieStore mCookieStore = httpClient.getCookieStore();
//                List<Cookie> cookies = mCookieStore.getCookies();
//                for (int i = 0; i < cookies.size(); i++) {
//                    //这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
//                    if ("PHPSESSID".equals(cookies.get(i).getName())) {
//                        PHPSESSID = cookies.get(i).getValue();
//                        break;
//                    }
//                }
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return ret;
//    }
	
	
	
	
	
	
	CookieManager cookieManager;
	public String sendPost(String url, String username, String password) {
		  CookieSyncManager.createInstance(this);
		  // 每次登录操作的时候先清除cookie
		  removeAllCookie();
		  // 根据url获得HttpPost对象
		  HttpPost httpRequest = new HttpPost(url);
		  // 取得默认的HttpClient
		  DefaultHttpClient httpclient = new DefaultHttpClient();
		  String strResult = null;
		  // NameValuePair实现请求参数的封装
		  List<NameValuePair> params = new ArrayList<NameValuePair>();
		  params.add(new BasicNameValuePair("_tk", "codefrom"));
		  params.add(new BasicNameValuePair("name", username));
		  params.add(new BasicNameValuePair("pass", password));
		  httpRequest.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		  httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		  httpRequest.addHeader("Origin", "http://www.codefrom.com");
		  httpRequest.addHeader("Referer", "http://www.codefrom.com/login");
		  httpRequest.addHeader("X-Requested-With", "XMLHttpRequest");
		  try {
		      // 添加请求参数到请求对象
		      httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		      // 获得响应对象
		      HttpResponse httpResponse = httpclient.execute(httpRequest);
		      // 判断是否请求成功
		      if (httpResponse.getStatusLine().getStatusCode() == 200) {
		          // 获得响应返回Json格式数据
		          strResult = EntityUtils.toString(httpResponse.getEntity());
		          // 取得Cookie
		          CookieStore mCookieStore = httpclient.getCookieStore();
		          List<Cookie> cookies = mCookieStore.getCookies();
		          if (cookies.isEmpty()) {
		              System.out.println("Cookies为空");
		          } else {
		              for (int i = 0; i < cookies.size(); i++) {
		                  // 保存cookie
		                  Cookie cookie = cookies.get(i);
		                  Log.d("Cookie", cookies.get(i).getName() + "=" + cookies.get(i).getValue());
		                  cookieManager = CookieManager.getInstance();
		                  String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
		                  cookieManager.setCookie("http://www.codefrom.com/", cookieString);
		              }
		          }
		          return strResult;
		      } else {
		          strResult = "错误响应:" + httpResponse.getStatusLine().toString();
		      }
		  } catch (ClientProtocolException e) {
		      strResult = "错误响应:" + e.getMessage().toString();
		      e.printStackTrace();
		      return strResult;
		  } catch (IOException e) {
		      strResult = "错误响应:" + e.getMessage().toString();
		      e.printStackTrace();
		      return strResult;
		  } catch (Exception e) {
		      strResult = "错误响应:" + e.getMessage().toString();
		      e.printStackTrace();
		      return strResult;
		  }
		  return strResult;
		}
	
	private void removeAllCookie() {
		cookieManager = CookieManager.getInstance();
		  cookieManager.removeAllCookie();
		  CookieSyncManager.getInstance().sync();  
		}
	
	
	
//	urlConnection.setRequestProperty("Cookie", "JSESSIONID="+JSESSIONID);//附带上session信息http://bbs.csdn.net/topics/390896202/
	
//	获得了session id后，怎么再添加到我们的POST或者GET请求里面呢，
//	HttpPost httpPost = new HttpPost(访问地址);   
//	httpPost.setHeader("Cookie", "JSESSIONID=" + 我们在静态变量里存放的SessionId);   
//	HttpResponse httpResponse = httpclient.execute(httpPost);  
//	
//	HttpGet request = new HttpGet(url+"?"+Params);   
//    request.setHeader("Cookie",Sessionid); 
}