package com.ssiot.remote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.baidu.platform.comapi.map.v;
import com.ssiot.remote.data.model.CameraFileModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetImageThread extends Thread{
    private static final String tag = "remote.GetImageThread";
    public static final int MSG_GETFTPIMG_END = 2;//MoniNodeListFrag.MSG_GET_ONEIMAGE_END;
    String url = "";
    ImageView imageView;
    Handler uiHandler;
    private int maxwidth = 256;//不能是static的
    public GetImageThread(ImageView view, String urlString,Handler h){
        url = urlString;
        imageView = view;
        uiHandler = h;
    }
    
    public GetImageThread(ImageView view, String urlString,Handler h, int maxPixel){
        url = urlString;
        imageView = view;
        uiHandler = h;
        if (maxPixel > 0){
        	maxwidth = maxPixel;
        }
    }
    @Override
    public void run() {
        final Bitmap bitmap = getHttpBitmap(url);
//        Message message = uiHandler.obtainMessage(MoniNodeListFrag.MSG_GET_ONEIMAGE_END);//MoniNodeListFrag 与ControlNodeListFrag 必须都是2
//        message.obj = new ThumnailHolder(imageView, bitmap);
//        uiHandler.sendMessage(message);
        if (null != imageView.getTag() && imageView.getTag() instanceof CameraFileModel){
        	((CameraFileModel) imageView.getTag()).bitmap = bitmap;
        }
        imageView.post(new Runnable() {//gejingbo 20160818
			@Override
			public void run() {
				imageView.setImageBitmap(bitmap);
			}
		});
    }
    
    public Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            Log.v(tag, "----start getftpbitmap---" + url);
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            test();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(4000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.outWidth = 64;
//            options.outHeight = 64;
            bitmap = BitmapFactory.decodeStream(is);
//            bitmap = BitmapFactory.decodeStream(is, null, options);
            
            bitmap = resizeBitmap(bitmap, maxwidth, maxwidth);
            //关闭数据流
            is.close();
            String localFolder = "";
            int index = url.indexOf("cloud.ssiot.com");
            if (index >= 0){//本公司图片进行缓存
            	localFolder = url.substring(index + 15, url.length());
            	Log.v(tag, "----- save to localsdcard:" + url);
            	saveBitmap(bitmap, Environment.getExternalStorageDirectory() + "/"+SsiotConfig.CACHE_DIR +"/" + localFolder);
            } else {
            	Log.e(tag, "-----not save to localsdcard:" + url);
            	return bitmap;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
    
    private static void test(){//测试获取FTP目录下的文件列表，不可行！
    	Log.v(tag, "===========");
    	Log.v(tag, "===========");
//    	URL url;
//		try {
//			url = new URL("http://yushu.ssiot.com/YS1/");
//			
//			Log.v(tag, "===========" + url.getAuthority() + url.getFile() +" path:"+ url.getPath() + url.getRef());
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			Log.v(tag, "===========inputstream:" + conn.getInputStream());
//			Log.v(tag, "===========filenamemap" + conn.getFileNameMap().);
//			conn.disconnect();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}catch (IOException e) {
//			e.printStackTrace();
//		}
    }
    
    public void saveBitmap(Bitmap bm, String path) {
        path = path.replace("http://", "");
        File f = new File(path);
        f.getParentFile().mkdirs();
        if (f.exists()) {
            boolean b = f.delete();
            Log.v(tag, "------exists delete result :" + b + " " + path);
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    public Bitmap resizeBitmap(Bitmap drawable, int desireWidth,
            int desireHeight) {
        int width = drawable.getWidth();
        int height = drawable.getHeight();

        if (0 < width && 0 < height && desireWidth < width
                || desireHeight < height) {
            // Calculate scale
            float scale;
            if (width < height) {
                scale = (float) desireHeight / (float) height;
                if (desireWidth < width * scale) {
                    scale = (float) desireWidth / (float) width;
                }
            } else {
                scale = (float) desireWidth / (float) width;
            }

            // Draw resized image
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap bitmap = Bitmap.createBitmap(drawable, 0, 0, width, height,
                    matrix, true);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);

            drawable = bitmap;
        }
        return drawable;
    }
    
    public class ThumnailHolder{
        public ImageView imageView;
        public Bitmap bitmap;
        public ThumnailHolder(ImageView image,Bitmap b) {
            this.bitmap = b;
            this.imageView = image;
        }
    }
}
