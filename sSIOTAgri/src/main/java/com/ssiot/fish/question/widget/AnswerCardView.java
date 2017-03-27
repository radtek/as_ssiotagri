package com.ssiot.fish.question.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.ssiot.agri.R;
import com.ssiot.fish.question.PicAdapter;
import com.ssiot.fish.question.PicGridAdapter;
import com.ssiot.remote.GetImageThread;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.AnswerModel;
import com.ssiot.remote.data.model.QuestionModel;

import java.util.ArrayList;

public class AnswerCardView extends FrameLayout{
    private static final String tag = "AnswerCardView";
    private View aView;
    private ViewStub questionCardStub;
    private ViewStub countstub;
    
    TextView mUserNameView;
    TextView mContentTextView;
    TextView mTimeTextView;
    TextView mReplyAddrTextView;
    
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GetImageThread.MSG_GETFTPIMG_END:
                    GetImageThread.ThumnailHolder thumb = (GetImageThread.ThumnailHolder) msg.obj;
                    thumb.imageView.setImageBitmap(thumb.bitmap);
                    break;

                default:
                    break;
            }
        };
    };
    
    public AnswerCardView(Context context) {
        this(context,null);
        // TODO Auto-generated constructor stub
    }
    
    public AnswerCardView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        
    }

    public AnswerCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        if ((attrs == null) || (defStyle == 0)) {
            
        }
        aView = LayoutInflater.from(context).inflate(R.layout.reply_detail_extra_card_view, this);
        questionCardStub = (ViewStub) aView.findViewById(R.id.question_detail_card);
        countstub = (ViewStub) aView.findViewById(R.id.ask_card_replycount_root);
        if (questionCardStub == null){
            Log.e(tag, "!!!!!!!!!!!!!!!stub==null");//放在初始化时不会返回null，费解
        }
        mUserNameView = (TextView) aView.findViewById(R.id.reply_detail_card_username);
        mContentTextView = (TextView) aView.findViewById(R.id.reply_detail_card_content);
        mTimeTextView = (TextView) aView.findViewById(R.id.reply_detail_card_time);
        mReplyAddrTextView = (TextView) aView.findViewById(R.id.reply_card_address);
        if (mUserNameView == null){
            Log.e("ssssssssssss", "dddddddd3333333333333ddddddddd");
        }
    }
    
    public void fillData(AnswerModel model){
        mUserNameView.setText(model._username);
        mContentTextView.setText(model._contenttext);
        mTimeTextView.setText(Utils.formatTime(model._createTime));
        mReplyAddrTextView.setText(model._addr);
    }
    
    public void fillHeadData(QuestionModel qModel){//TODO TODO ??????
//        ViewStub stub = (ViewStub) aView.findViewById(R.id.question_detail_card);
//        if (stub == null){
            Log.e("ssssssssssss", "dddddddddddddddddddddddd");
//        }
//        stub.inflate();
        Log.e(tag, "----fillHeadData-----");
        questionCardStub.setVisibility(View.VISIBLE);
        countstub.setVisibility(View.VISIBLE);
        TextView titleView = (TextView) aView.findViewById(R.id.ask_card_title);
        TextView askTimeView = (TextView) aView.findViewById(R.id.ask_card_time);
        TextView usernameView = (TextView) aView.findViewById(R.id.question_detail_card_username);
        TextView contentTextView = (TextView) aView.findViewById(R.id.ask_card_content);
        TextView addrView = (TextView) aView.findViewById(R.id.ask_card_address);
        TextView countTextView = (TextView) aView.findViewById(R.id.ask_card_replycount_text);
        titleView.setText(qModel._title);
        askTimeView.setText(Utils.formatTime(qModel._createTime));
        if (!TextUtils.isEmpty(qModel._username)){
            usernameView.setText(""+qModel._username);
        }
        contentTextView.setText(qModel._contentText);
        countTextView.setText(""+qModel._replyCount);
        if (qModel._type == 2){//待专家回答问题
            TextView countLabel = (TextView) aView.findViewById(R.id.ask_card_replycount_label);
            countLabel.setText("个专家回答");
        }
        if (!TextUtils.isEmpty(qModel._addr)){
            addrView.setText(qModel._addr);
        }
        aView.findViewById(R.id.reply_detail_card).setVisibility(View.GONE);
        
        GridView gridView = (GridView) aView.findViewById(R.id.ask_card_pic);
        if (!TextUtils.isEmpty(qModel._picUrls)){
            gridView.setVisibility(View.VISIBLE);
            String[] paths = qModel._picUrls.split(",");
            if (null != paths){
                ArrayList<String> as = new ArrayList<String>();
                for (int i = 0; i < paths.length; i ++){
                    as.add(paths[i]);
                }
                gridView.setAdapter(new PicAdapter(getContext(), as, mHandler));
            }
        } else {
            gridView.setVisibility(View.GONE);
        }
    }
}