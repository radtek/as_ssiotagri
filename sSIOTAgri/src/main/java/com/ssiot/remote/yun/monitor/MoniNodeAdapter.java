package com.ssiot.remote.yun.monitor;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import com.ssiot.agri.R;

//注意list嵌套list的问题！！！ 这是一个特殊的adapter，以node为item。地块和设施都是item中的标题
public class MoniNodeAdapter extends RecyclerView.Adapter{
    private static final String tag = "MoniNodeAdapter";
    private List<YunNodeModel> list;
    private Context mContext;
    private LayoutInflater mInflater;
    private Resources mResources;
    View deviceView;
    MyItemClickListener mListener;

    public MoniNodeAdapter(Context c,List<YunNodeModel> lis,MyItemClickListener listener){
        list = lis;
        mContext = c;
        mInflater = LayoutInflater.from(mContext);
        mResources = mContext.getResources();
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.node_item_yun3, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
//        Log.v(tag, "----------onBindViewHolder-----------" + i);
        MyViewHolder holder = (MyViewHolder) viewHolder;
        YunNodeModel model = list.get(i);
        long time1 = System.currentTimeMillis();
        if (model.mLandID <= 0){
            model.landVis = false;
            model.facilityVis = false;
        }
        for (int j = 0; j < i; j ++){//控制列表的第一个地块 设施才显示，  landid<=0代表无设施信息 即旧设备
            if (model.mLandID <= 0 || list.get(j).mLandID == model.mLandID){//(list.get(j).mLandID <= 0 || list.get(j).mLandID == model.mLandID) BUG 20160817
                model.landVis = false;
            }
            if (model.mFacilityID <= 0 || list.get(j).mFacilityID == model.mFacilityID){//(list.get(j).mFacilityID <= 0 || list.get(j).mFacilityID == model.mFacilityID)
                model.facilityVis = false;
                break;
            }
        }
//        Log.v(tag, "------count visible cost time" + i + "  " + (System.currentTimeMillis()-time1));
        
        holder.fillData(model);
    }
    
    
    protected class MyViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        private TextView mLandView;
        private TextView mFacilityView;
        private TextView mNodeTitleView;
        private LinearLayout mIconLayout;
        YunNodeModel mModel;
        public MyViewHolder(View v) {
            super(v);
            mLandView = (TextView) v.findViewById(R.id.land_text);
            mFacilityView = (TextView) v.findViewById(R.id.facility_text);
            mNodeTitleView = (TextView) v.findViewById(R.id.node_title);
            mIconLayout = (LinearLayout) v.findViewById(R.id.monitorLinearLayout);
            v.setOnClickListener(this);
            mIconLayout.setOnClickListener(this);//ScrollView click的分配？？TODO in a better way
        }

        public void fillData(YunNodeModel model){
            mModel = model;
            if (model.landVis){
                mLandView.setVisibility(View.VISIBLE);
                mLandView.setText(model.landStr);
            } else {
                mLandView.setVisibility(View.GONE);
            }
            if (model.facilityVis){
                mFacilityView.setVisibility(View.VISIBLE);
                mFacilityView.setText(model.facilityStr);
            } else {
                mFacilityView.setVisibility(View.GONE);
            }
            
            mNodeTitleView.setText(model.nodeStr + "(" + model.mNodeNo+")");//TODO test
            mIconLayout.removeAllViews();
            if (null != model.list){
                for (int i = 0; i < model.list.size(); i ++){
                    DeviceBean d = (DeviceBean) model.list.get(i);
                    View localdeviceView = setImgBgState(d, DeviceBean.getIconRes(d.mType, d.mDeviceTypeNo));//TODO TODO
                    mIconLayout.addView(localdeviceView);
                }
            }
        }

        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(mContext, FarmDetailPagerActivity.class);
//            intent.putExtra("yunnodemodel", mModel);
//            mContext.startActivity(intent);
            if (null != mListener){
                mListener.onMyItemClicked(mModel);
            }
        }
    }
    
    private View setImgBgState(DeviceBean paramDevice, int resId) {
        deviceView = mInflater.inflate(R.layout.monitor_list_device_item, null);
        ImageView imageView = ((ImageView) deviceView.findViewById(R.id.imageViewPho));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        if (paramDevice.getContactStatus() == 1 && paramDevice.getContactStatus() == 0) {//    在线 TODO
//            if ((paramDevice.getRunStatus() == 6) || (paramDevice.getRunStatus() == 3) || (paramDevice.getRunStatus() == 8)) {
//                imageView.setBackgroundResource(R.drawable.icbg_device_warning_actived);//黄色
//                imageView.setImageDrawable(new TintedBitmapDrawable(this.mResources, resId, R.color.white));
//            } else {
//                imageView.setBackgroundResource(R.drawable.icbg_device_connect_actived);//圆圈线 绿色
//                imageView.setImageDrawable(new TintedBitmapDrawable(this.mResources, resId, R.color.device_connect_actived));//绿色
//            }
//        } else {
//            imageView.setBackgroundResource(R.drawable.icbg_device_offline_actived);//粉色
//            imageView.setImageDrawable(new TintedBitmapDrawable(this.mResources, resId, R.color.white));
//        }
//        Log.v(tag, "-----setImgBgState---" + paramDevice.mChannel + " " + paramDevice.status);
        switch (paramDevice.getContactStatus()) {
            case -1://离线
                imageView.setBackgroundResource(R.drawable.icbg_device_offline_actived);//粉色
                imageView.setImageDrawable(new TintedBitmapDrawable(this.mResources, resId, R.color.device_offline_actived));
                break;
            case 0://在线，在线关闭
                imageView.setBackgroundResource(R.drawable.icbg_device_connect_actived);//圆圈线 绿色
                imageView.setImageDrawable(new TintedBitmapDrawable(this.mResources, resId, R.color.device_connect_actived));//绿色
                break;
            case 1://运行中
                imageView.setBackgroundResource(R.drawable.icbg_device_working_actived);//圆圈线 蓝色？？
                imageView.setImageDrawable(new TintedBitmapDrawable(this.mResources, resId, R.color.device_working_actived));//蓝色
                break;
            case 2://报警
                imageView.setBackgroundResource(R.drawable.icbg_device_warning_actived);//黄色
                imageView.setImageDrawable(new TintedBitmapDrawable(this.mResources, resId, R.color.white));
                break;

            default:
                break;
        }
        ((TextView) deviceView.findViewById(R.id.deviceName)).setText(paramDevice.mName);
        return this.deviceView;
    }
    
    public interface MyItemClickListener{
        void onMyItemClicked(YunNodeModel yunModel);
    }
}