package com.ssiot.remote.expert;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssiot.agri.R;
import com.ssiot.remote.GetImageThread;
import com.ssiot.remote.data.model.GoodsModel;

public class GoodsAdapter extends BaseAdapter{
	private List<GoodsModel> mDatas;
	LayoutInflater inflater;
	Handler mHandler;
	
	public GoodsAdapter(Context c,List<GoodsModel> data, Handler handler){
		mDatas = data;
		inflater = LayoutInflater.from(c);
		mHandler = handler;
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
		convertView = inflater.inflate(R.layout.itm_goods, null, false);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.goods_img);
		TextView nameView = (TextView) convertView.findViewById(R.id.goods_name);
		TextView detailView = (TextView) convertView.findViewById(R.id.goods_detail);
		TextView priceView = (TextView) convertView.findViewById(R.id.goods_price);
		GoodsModel m = mDatas.get(position);
		
		new GetImageThread(imageView, "http://cloud.ssiot.com/"+m._img, mHandler).start();//TODO 选个地址
		nameView.setText(m._name);
		detailView.setText(m._detail);
		priceView.setText("￥"+m._price);
		return convertView;
	}
}