package com.ssiot.remote.yun.detail.sensors;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ssiot.fish.HeadActivity;
import com.ssiot.agri.R;

//huiyun SenSet.java
public class AlarmRuleSetAct extends HeadActivity{
    
    String comStr;
    String typeStr;
    
    TextView compTextView;
    TextView comTextView;
    RadioButton smallButton;
    RadioButton bigButton;
    RadioGroup comGroup;
    TextView valTextView;
    TextView valScaleTextView;
    TextView startTextView;
    TextView endTextView;
    TextView unitTextView;
    ToggleButton startButton;
    EditText warnValTextView;
    
    int toggle = 1;
    String opStr;
    boolean isSmall;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
//        setContentView(R.layout.sen_set);//activity_device_setting_editor
        initView(getIntent().getExtras());
    }
    
    protected void initView(Bundle bundle) {
      setContentView(R.layout.sen_set);
      comStr = "大于";
//      typeParams = new DeviceTypeParams(mContext);
      compTextView = ((TextView)findViewById(R.id.textViewComp));
      comTextView = ((TextView)findViewById(R.id.textViewCombo));
//      tu = new TextUtils(comTextView);
      smallButton = ((RadioButton)findViewById(R.id.radioSmall));
      bigButton = ((RadioButton)findViewById(R.id.radioBig));
      comGroup = ((RadioGroup)findViewById(R.id.radioGroupCombo));
      comGroup.setOnCheckedChangeListener(filterChangeListener);
      valTextView = ((TextView)findViewById(R.id.TextViewVal));
      valScaleTextView = ((TextView)findViewById(R.id.textViewValScale));
      startTextView = ((TextView)findViewById(R.id.TextViewStart));
      endTextView = ((TextView)findViewById(R.id.TextViewEnd));
      unitTextView = ((TextView)findViewById(R.id.textViewUnit));
      startButton = ((ToggleButton)findViewById(R.id.toggleButtonSet));
      startButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean isChecked) {
          if (isChecked) {
            toggle = 1;
            compTextView.setText(R.string.sensorSetStartState);//已启用。当数据达到预警值时将发出预警
          } else {
              toggle = 0;
              compTextView.setText(getString(R.string.sensorSetStopState) + "\n");
          }
        }
      });
      warnValTextView = ((EditText)findViewById(R.id.textViewVa2));
    }
    
    RadioGroup.OnCheckedChangeListener filterChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int position) {
            switch (position) {
                case R.id.radioSmall:
                    comStr = smallButton.getText().toString();
                    opStr = "<";
                    isSmall = true;
                    break;
                case R.id.radioBig:
                    comStr = bigButton.getText().toString();
                    opStr = ">";
                    isSmall = false;
                    break;
                default:
                    break;
            }
            setComText();
        }
    };
    
    void setComText() {
      comTextView.setText(getCom());
//      tu.setHighlight_TEXT(this.comStr, mResources.getColor(2131099749));
    }
    
    private String getCom() {//当传感器%1$s%2$s预警%3$s时发出预警
        if (isSmall) {
            comStr = getString(R.string.opLess);
        } else {
            comStr = getString(R.string.opMore);
        }
        Object[] arrayOfObject = new Object[3];
        arrayOfObject[0] = typeStr;
        arrayOfObject[1] = comStr;
        arrayOfObject[2] = typeStr;
        return getString(R.string.msg_sensor_summary, arrayOfObject);
    }
}