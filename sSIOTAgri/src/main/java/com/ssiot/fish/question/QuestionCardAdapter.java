package com.ssiot.fish.question;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ssiot.agri.R;
import com.ssiot.fish.question.widget.QuestionCardView;
import com.ssiot.remote.data.model.QuestionModel;

import java.util.List;

public class QuestionCardAdapter extends RecyclerView.Adapter {
    private static final String TAG = "QuestionCardAdapter";
    private List<QuestionModel> list;
    private OnRecyclerViewListener onRecyclerViewListener;

    public QuestionCardAdapter(List<QuestionModel> lis) {
        list = lis;
    }
    
    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_card_view, null);
        View view = new QuestionCardView(viewGroup.getContext(), null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new QuestionBeanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        QuestionBeanViewHolder holder = (QuestionBeanViewHolder) viewHolder;
        QuestionModel questionModel = list.get(i);
//        holder.nameTv.setText(QuestionBean.getName());
//        holder.ageTv.setText(QuestionBean.getAge() + "岁");
        holder.fillData(questionModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected class QuestionBeanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public View rootView;
        public TextView nameTv;
        public TextView ageTv;
        
        public QuestionCardView mView;

        public QuestionBeanViewHolder(View itemView) {
            super(itemView);
            mView = (QuestionCardView) itemView;
//            nameTv = (TextView) itemView.findViewById(R.id.);
//            ageTv = (TextView) itemView.findViewById(R.id.recycler_view_test_item_QuestionBean_age_tv);
//            rootView = itemView.findViewById(R.id.recycler_view_test_item_QuestionBean_view);
//            rootView.setOnClickListener(this);
//            rootView.setOnLongClickListener(this);
        }
        
        public void fillData(QuestionModel model){
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