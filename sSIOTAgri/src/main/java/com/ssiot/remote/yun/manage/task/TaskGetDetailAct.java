
package com.ssiot.remote.yun.manage.task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;
import com.ssiot.remote.GetImageThread;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.ERPTaskInstanceModel;
import com.ssiot.remote.data.model.ERPTaskReportModel;
import com.ssiot.remote.data.model.UserModel;
import com.ssiot.remote.yun.webapi.TaskInstance;
import com.ssiot.remote.yun.webapi.TaskReport;
import com.ssiot.remote.yun.webapi.WS_User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskGetDetailAct extends HeadActivity {
    private static final String tag = "TaskGetDetailAct";
    View infoView;
    TableRow frameRow;
    TextView frameNameTextView;
    TextView sourceTextView;
    TextView turnTextView;
    TextView timeView;
    LinearLayout getLayout;
    TextView stateTextView;
    TextView senderTextView;
    TableLayout infoLayout;
    TextView startTimeTextView;
    TextView endTimeTextView;
    LinearLayout contentLayout;
    TextView contentTextView;
    TextView cTitleTextView;
    TextView reportTextView;
    ListView reportListView;

    ERPTaskInstanceModel taskModel;
    List<ERPTaskReportModel> reportList = new ArrayList<ERPTaskReportModel>();
    
    private static final int MSG_GET_TASK_INFO = 1;
    private static final int MSG_STATE_CHANGE_END = 3;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GET_TASK_INFO:
                    setvalue();
                    initTitleBar(taskModel._state);
                    break;
                case MSG_STATE_CHANGE_END:
                    if ((Boolean) msg.obj){
                        setvalue();
                        initTitleBar(2);
                    }
                    break;
                case GetImageThread.MSG_GETFTPIMG_END:
                    GetImageThread.ThumnailHolder thumb = (GetImageThread.ThumnailHolder) msg.obj;
                    thumb.imageView.setImageBitmap(thumb.bitmap);
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        taskModel = (ERPTaskInstanceModel) getIntent().getSerializableExtra("ERPTaskInstanceModel");
        initView();
//        setvalue();//after thread
        new GetTaskDetailThread(taskModel._id).start();
        
    }

    private void initView() {
        setContentView(R.layout.task_get_detail);
        // initTitleText();
        infoView = mInflater.inflate(R.layout.task_get_detail_top, null);
        // frameRow =
        // ((TableRow)infoView.findViewById(R.id.TableRowFrame));//任务园区
        // frameNameTextView =
        // ((TextView)infoView.findViewById(R.id.textViewFrameName));
//        sourceTextView = ((TextView) infoView.findViewById(R.id.textViewSource));// 任务来源
        // appendixLayout =
        // ((LinearLayout)infoView.findViewById(R.id.LinearLayoutAppendix));//附件
        // appendixRow =
        // ((TableRow)infoView.findViewById(R.id.tableRowAppendix));
        turnTextView = ((TextView) infoView.findViewById(R.id.textViewTurn));// 转发
        turnTextView.setOnClickListener(listener);
        // initRec(infoView);//处理接受者的ui ？
        timeView = ((TextView) infoView.findViewById(R.id.textViewDate));// 接收时间
        getLayout = ((LinearLayout) infoView.findViewById(R.id.LinearLayoutGet));// 确认收到
        getLayout.setOnClickListener(listener);
        stateTextView = ((TextView) infoView.findViewById(R.id.textViewState));//
        // moreLayout =
        // ((LinearLayout)infoView.findViewById(R.id.linearLayoutMore));//UI
        // 中重复的？
        senderTextView = ((TextView) infoView.findViewById(R.id.textViewSender));// 发起者
        // moreLayout.setOnClickListener(listener);
        infoLayout = ((TableLayout) infoView.findViewById(R.id.tableLayoutInfo));// 要手动隐藏的下半部
        startTimeTextView = ((TextView) infoView.findViewById(R.id.textViewStartTime));
        endTimeTextView = ((TextView) infoView.findViewById(R.id.textViewEndTime));
        // noTextView = ((TextView)infoView.findViewById(R.id.textViewNo));
        // phoTextView =
        // ((TextView)infoView.findViewById(R.id.textViewPho));//？？？？？
        // dividImageView =
        // ((ImageView)infoView.findViewById(R.id.ImageViewDivier));
        // initExe(infoView);
        // locationTextView =
        // ((TextView)infoView.findViewById(R.id.textViewLocation));
        // fertilizesTextView =
        // ((TextView)infoView.findViewById(R.id.textViewFertilizes));
        // spraysTextView =
        // ((TextView)infoView.findViewById(R.id.textViewSprays));
        // ackCycleTextView =
        // ((TextView)infoView.findViewById(R.id.textViewAckCycle));
        // scaleTextView =
        // ((TextView)infoView.findViewById(R.id.textViewExecScale));//汇报时间
        contentLayout = ((LinearLayout) infoView.findViewById(R.id.linearLayoutContent));// 即详细
        contentLayout.setOnClickListener(listener);
        contentTextView = ((TextView) infoView.findViewById(R.id.textViewContent));
        reportListView = ((ListView) findViewById(R.id.listViewReport));
        cTitleTextView = ((TextView) infoView.findViewById(R.id.textViewContentTitle));// 汇报
                                                                                       // 无汇报
        reportTextView = ((TextView) findViewById(R.id.textViewReport));// 写汇报按钮
        reportTextView.setOnClickListener(listener);

        reportListView.addHeaderView(infoView);
        findViewById(R.id.title_bar_right).setVisibility(View.GONE);
    }

    private void setvalue() {
        
        if (taskModel._state < 2 && isReceiver()){//状态是新建时才显示。只有接受者才能操作这个，
            getLayout.setVisibility(View.VISIBLE);
            timeView.setText("未接收");
        } else {
            getLayout.setVisibility(View.GONE);
            timeView.setText("已接收");
//            if (null != taskModel._receivedtime){
//                timeView.setText(/Utils.formatTime(taskModel._receivedtime));
//            } else {
//                Log.e(tag, "----------_receivedtime==null");
//            }
        }
        senderTextView.setText(taskModel._OwnerName);//发起者
//        sourceTextView.setText(""+taskModel._userid);//任务来源
        TextView mReciverView = (TextView) infoView.findViewById(R.id.textViewReFirstExe);
        mReciverView.setText(taskModel._parsedReceiverNames);//分配给 TODO
        startTimeTextView.setText(Utils.formatTime(taskModel._createtime));
//        endTimeTextView.setText(Utils.formatTime(taskModel._endtime));
        contentTextView.setText(taskModel._taskdetail);
        cTitleTextView.setText(reportList.size() > 0 ? "汇报" : "无汇报");
        
        TaskReportAdapter adapter = new TaskReportAdapter(TaskGetDetailAct.this, reportList, mHandler);
        reportListView.setAdapter(adapter);
    }
    
    private void initTitleBar(int state){
        TextView titleRight = (TextView) findViewById(R.id.title_bar_right);
        if (state < 2 || !isReceiver()){
            titleRight.setVisibility(View.GONE);
//            titleRight.setText("请queren");//
        } else {
            titleRight.setVisibility(View.VISIBLE);
            titleRight.setText("写汇报");
            titleRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TaskGetDetailAct.this, TaskReportNewAct.class);
                    intent.putExtra("ERPTaskInstanceModel", taskModel);
//                    intent.putExtra("taskid", taskModel._id);
                    startActivityForResult(intent, REQUEST_NEW_REPORT);
                }
            });
        }
        
        initTitleLeft(R.id.title_bar_left);
    }
    
    private boolean isReceiver(){
        int myuserid = Utils.getIntPref(Utils.PREF_USERID, this);
        return (","+taskModel._workerids + ",").contains(","+myuserid + ",");
    }
    
    private static final int REQUEST_NEW_REPORT = 1;
    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent intent) {
        switch (requestcode) {
            case REQUEST_NEW_REPORT:
                if (RESULT_OK == resultcode) {
                    new GetTaskDetailThread(taskModel._id).start();//
                }
                break;

            default:
                break;
        }
    }
    
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.LinearLayoutGet://确认 收到
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean ret = false;
                            if (taskModel._id > 0){
                                taskModel._state ++;
                                ret = new TaskInstance().Save(taskModel) > 0;
                            }
                            
                            List<ERPTaskInstanceModel> tasks = new TaskInstance().GetTaskInstance(taskModel._id);
                            if (null != tasks && tasks.size() > 0){
                                taskModel = tasks.get(0);
                            }
                            Message msg = mHandler.obtainMessage(MSG_STATE_CHANGE_END);
                            msg.obj = ret;
                            mHandler.sendMessage(msg);
                        }
                    }).start();
                    
                    break;

                default:
                    break;
            }
        }
    };
    
    private class GetTaskDetailThread extends Thread{
        private int id;
        public GetTaskDetailThread(int taskid){
            this.id = taskid;
        }
        @Override
        public void run() {
            List<ERPTaskInstanceModel> list = new TaskInstance().GetTaskInstance(id);
            if (null != list && list.size() > 0){
                taskModel = list.get(0);
                List<Integer> ids = taskModel.getReceiverUserIDsList();
                if (null != ids && ids.size() > 0){
                    List<UserModel> userlist = new WS_User().GetReceiverUserNames(ids);
                    String receiverNames = "";
                    if (null != userlist){
                        for (int i = 0; i < userlist.size(); i ++){
                            receiverNames += userlist.get(i)._username + " ";
                        }
                        taskModel._parsedReceiverNames = receiverNames;//任务接受者的名称
                    }
                }
                
                List<ERPTaskReportModel> reportLi = new TaskReport().GetTaskReports(taskModel._id);
                reportList.clear();
                if (null != reportLi){
                    reportList.addAll(reportLi);
                }
                mHandler.sendEmptyMessage(MSG_GET_TASK_INFO);
            }
            
        }
    }
}
