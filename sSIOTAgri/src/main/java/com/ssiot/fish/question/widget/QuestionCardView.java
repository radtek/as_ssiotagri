
package com.ssiot.fish.question.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ssiot.agri.R;
import com.ssiot.fish.question.AlbumActivity;
import com.ssiot.fish.question.QuestionDetailListActivity;
import com.ssiot.fish.question.QuestionNewActivity;
import com.ssiot.remote.GetImageThread;
import com.ssiot.remote.GetImageThread.ThumnailHolder;
import com.ssiot.remote.data.model.QuestionModel;

public class QuestionCardView extends BaseCardView
        implements View.OnClickListener {
    private static final String tag = "QuestionCardView";
    private View cView;// 关注作物
    private View dView;
    private QuestionModel eQuestionModel;// i类
    private View fCardView;// f
    private View gMarginView;
    private TextView hAskTitleTextView;// h
    private TextView iContentTextView;// i
    private ImageView jPicImageView;
    private View kPicRootView;
    private View lPicMoreView;
    private TextView mCountTextView;
    private TextView nAddrView;// n
    private View cainaView;// o
    private View guanfangView;// p
    private View tuijianView;// q
    // private com.satan.peacantdoctor.question.b.f r;
    private View sEventCardView;
    private TextView tEventCardTitle;
    private TextView uEventCardContent;
    private View vEvCardPicRootV;
    private ImageView wEvCardPicImageV;
    private View xEvCarPicMore;
    private View yCardRefresh;

    // private b z;
    
    private static final int MSG_GETIMG_END = GetImageThread.MSG_GETFTPIMG_END;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GETIMG_END:
                    ThumnailHolder thumb = (ThumnailHolder) msg.obj;
                    thumb.imageView.setImageBitmap(thumb.bitmap);
                    break;

                default:
                    break;
            }
        };
    };

    public QuestionCardView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public QuestionCardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    // public QuestionCardView(Context paramContext, b paramb) {
    // super(paramContext);
    // setIQuestionDelCallBack(paramb);
    // }

    @Override
    protected void a() {
        cView = finda(R.id.ask_card_guanzhu_zuowu);// 0x fb =163
        dView = finda(R.id.ask_card_guanzhu_zuowu_line);
        cView.setOnClickListener(this);
        fCardView = finda(R.id.question_card_view);
        nAddrView = ((TextView) finda(R.id.ask_card_address));
        hAskTitleTextView = ((TextView) finda(R.id.ask_card_title));
        iContentTextView = ((TextView) finda(R.id.ask_card_content));
        kPicRootView = finda(R.id.ask_card_pic_root);
        lPicMoreView = finda(R.id.ask_card_pic_more);
        jPicImageView = ((ImageView) finda(R.id.ask_card_pic));
        // jPicImageView.setOnClickListener(new g(this));// TODO ????
        mCountTextView = ((TextView) finda(R.id.ask_card_replycount_text));
        gMarginView = finda(R.id.ask_card_first_margin);
        guanfangView = finda(R.id.ask_card_guanfang);
        cainaView = finda(R.id.ask_card_caina);
        tuijianView = finda(R.id.ask_card_tuijian);

        sEventCardView = finda(R.id.event_card_view);
        tEventCardTitle = ((TextView) finda(R.id.event_card_title));
        uEventCardContent = ((TextView) finda(R.id.event_card_content));
        vEvCardPicRootV = finda(R.id.event_card_pic_root);
        wEvCardPicImageV = ((ImageView) finda(R.id.event_card_pic));
        xEvCarPicMore = finda(R.id.event_card_pic_more);
        yCardRefresh = finda(R.id.card_refresh);
        yCardRefresh.setOnClickListener(this);
        fCardView.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.question_card_view;
    }

    public QuestionModel getQuestionModel() {
        return eQuestionModel;
    }
    
    public void fillData(QuestionModel modelData){
        eQuestionModel = modelData;
        hAskTitleTextView.setText(modelData._title.toString());
        iContentTextView.setText(modelData._contentText);
        mCountTextView.setText(""+modelData._replyCount);
        if (modelData._type == 2){
            guanfangView.setVisibility(View.VISIBLE);
        } else {
            guanfangView.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(modelData._addr)){
            nAddrView.setText(modelData._addr);
        }
        if (TextUtils.isEmpty(modelData._picUrls)){
            kPicRootView.setVisibility(View.GONE);
        } else {
            kPicRootView.setVisibility(View.VISIBLE);
            String[] pics = modelData._picUrls.split(",");
            if (null != pics && pics.length > 0){
                new GetImageThread(jPicImageView, "http://"+QuestionNewActivity.FTP_QUESTION_PATH+pics[0], mHandler).start();
            }
        }
//        setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        Log.v(tag, "----onclick--" + view.getId());
        switch (view.getId()) {
            case R.id.event_card_pic:
                Intent localIntent5 = new Intent();
                localIntent5.setClass(getContext(), AlbumActivity.class);
                // localIntent5.putExtra("BUNDLE_PICS", this.r.a());
                localIntent5.putExtra("BUNDLE_POSITION", 0);
                getContext().startActivity(localIntent5);
                break;
            case R.id.ask_card_pic:
                Intent localIntent4 = new Intent();
                localIntent4.setClass(getContext(), AlbumActivity.class);
                // localIntent4.putExtra("BUNDLE_PICS", this.e.f());
                
                localIntent4.putExtra("BUNDLE_POSITION", 0);
                getContext().startActivity(localIntent4);
                break;
            case R.id.question_card_view:
                Intent detailIntent = new Intent();
                // detailIntent.putExtra("qid", this.e.d);
                detailIntent.putExtra("questionmodel", eQuestionModel);
                detailIntent.setClass(getContext(), QuestionDetailListActivity.class);
                getContext().startActivity(detailIntent);
                break;
            case R.id.event_card_view:
                Intent localIntent3 = new Intent();
                // localIntent3.putExtra("BUNDLE_COMMON_WEBVIEW_URL", this.r.f);
                localIntent3.putExtra("BUNDLE_COMMON_WEBVIEW_SHOWTITLE", true);
                // localIntent3.putExtra("BUNDLE_COMMON_WEBVIEW_TITLE",
                // this.r.d);
                // localIntent3.putExtra("BUNDLE_COMMON_WEBVIEW_CONTENT",
                // this.r.e);
                // if (this.r.b.size() > 0){
                // localIntent3.putExtra("BUNDLE_COMMON_WEBVIEW_IMAGE",
                // (String)this.r.b.get(0));
                // }
                // localIntent3.setClass(getContext(),
                // CommonWebViewActivity.class);
                // getContext().startActivity(localIntent3);
                break;
            case R.id.ask_card_guanzhu_zuowu:
                // Intent localIntent2 = new Intent();
                // localIntent2.setClass(getContext(), UserCropActivity.class);
                // getContext().startActivity(localIntent2);
                break;
            default:
                break;
        }
    }

    @Override
    public void setInfo(Object paramObject) {
        // TODO Auto-generated method stub

    }

}
