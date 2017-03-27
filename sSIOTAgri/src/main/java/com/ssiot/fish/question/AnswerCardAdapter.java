package com.ssiot.fish.question;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ssiot.agri.R;
import com.ssiot.fish.question.widget.AnswerCardView;
import com.ssiot.remote.data.model.AnswerModel;
import com.ssiot.remote.data.model.QuestionModel;

import java.util.List;

public class AnswerCardAdapter extends RecyclerView.Adapter {
    private static final String tag = "AnswerCardAdapter";
    private QuestionModel qModel;
    private List<AnswerModel> list;
    private OnRecyclerViewListener onRecyclerViewListener;

    public AnswerCardAdapter(List<AnswerModel> lis,QuestionModel qModel) {
        list = lis;
        this.qModel = qModel;
    }
    
    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_card_view, null);
        View view = new AnswerCardView(viewGroup.getContext(), null,0);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new AnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        Log.v(tag, "----onBindViewHolder------" + i);
        AnViewHolder holder = (AnViewHolder) viewHolder;
        if (i == 0){
            holder.fillHeadData(qModel);
        } else {
            AnswerModel answerModel = list.get(i-1);
            holder.fillData(answerModel);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    protected class AnViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public View rootView;
        public TextView nameTv;
        public TextView ageTv;
        
        public AnswerCardView mView;

        public AnViewHolder(View itemView) {
            super(itemView);
            mView = (AnswerCardView) itemView;
//            nameTv = (TextView) itemView.findViewById(R.id.);
//            ageTv = (TextView) itemView.findViewById(R.id.recycler_view_test_item_QuestionBean_age_tv);
//            rootView = itemView.findViewById(R.id.recycler_view_test_item_QuestionBean_view);
//            rootView.setOnClickListener(this);
//            rootView.setOnLongClickListener(this);
        }
        
        public void fillHeadData(QuestionModel qModel){
            mView.fillHeadData(qModel);
        }
        
        public void fillData(AnswerModel model){
            mView.fillData(model);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(this.getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (null != onRecyclerViewListener) {
                return onRecyclerViewListener.onItemLongClick(this.getPosition());
            }
            return false;
        }
    }

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }
}