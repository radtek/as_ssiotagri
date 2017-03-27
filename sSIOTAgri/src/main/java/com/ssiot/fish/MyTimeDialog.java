package com.ssiot.fish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ssiot.agri.R;

public class MyTimeDialog extends AlertDialog{//TODO

    public MyTimeDialog(Context context) {
        super(context);
        View view = getLayoutInflater().inflate(R.layout.dia_date_time_pick, null);
        
        setTitle("时间选择");
        final DatePicker dp = (DatePicker) view.findViewById(R.id.date_pick);
        final TimePicker tp = (TimePicker) view.findViewById(R.id.time_pick);
//        
//        setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date d = new Date(dp.getYear()-1900, dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
//                mTimeStamp = new Timestamp(d.getTime());
//                String str = formatter.format(d);
//                mTxtTime.setText("时间:"+str);
//            }
//        }).setNegativeButton(android.R.string.cancel, null);
        
        setView(view);
//        setContentView(view);
    }
    
}