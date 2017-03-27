
package com.ssiot.remote.yun.unit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ssiot.agri.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class CustomDialog extends AlertDialog implements View.OnClickListener {
    String[] Items;
    TextView alertMsg;
    public View.OnClickListener backClickListener;
    ArrayList<Integer> checkVals;
    EditText contentEditText;
    String contentHint;
    Context context;
    int[] DATEVALS;
    String highLight;
    boolean isShowDateGrid;
    GridView itemCheckGridView;
    AdapterView.OnItemClickListener itemClickListener;
    ListView itemListView;
    String msg;
    CheckArrayAdapter rDateAdapter;
    public View.OnClickListener sureClickListener;
    int tag;
    String title = "";

    public CustomDialog(Context paramContext) {
        super(paramContext, R.style.MyDialogStyle);
        context = paramContext;
    }

    public static int getRepeatDateOffset(int paramInt) {
        return paramInt;//?
    }

    private void initView() {
        contentEditText = ((EditText) findViewById(R.id.editTextContent));
        itemCheckGridView = ((GridView) findViewById(R.id.gridViewCheckItem));
        itemCheckGridView.setSelector(new ColorDrawable(context.getResources().getColor(
                R.color.transparent)));
        itemListView = ((ListView) findViewById(R.id.listViewItem));
        itemListView.setSelector(new ColorDrawable(context.getResources().getColor(
                R.color.transparent)));
        TextView titleView = (TextView) findViewById(R.id.textViewTitle);
        LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.LinearLayoutButton);
        TextView textViewDivider = (TextView) findViewById(R.id.TextViewDividerH);
        Button btnBack = (Button) findViewById(R.id.buttonBack);
        Button btnSure = (Button) findViewById(R.id.buttonSure);
        if (getContentHint() == null) {
            contentEditText.setVisibility(View.GONE);

            //显示listview
            if (getItems() == null) {
                itemListView.setVisibility(View.GONE);
            } else {
                ArrayAdapter localArrayAdapter = new ArrayAdapter(context,
                        R.layout.dialog_list_item, R.id.textViewContent, getItems());
                itemListView.setAdapter(localArrayAdapter);
                if (getItemClickListener() != null) {
                    itemListView.setOnItemClickListener(getItemClickListener());
                }
                itemListView.setVisibility(View.VISIBLE);
            }

            //显示gridview
            if (isShowDateGrid) {
                DATEVALS = context.getResources().getIntArray(R.array.dateVals);
                setItems(context.getResources().getStringArray(R.array.dates));
                rDateAdapter = new CheckArrayAdapter(context, R.layout.custom_grid_check_item,
                        getItems());
                itemCheckGridView.setAdapter(rDateAdapter);
                itemCheckGridView.setNumColumns(4);
                if (getItemClickListener() != null) {
                    itemCheckGridView.setOnItemClickListener(getItemClickListener());
                }
                itemCheckGridView.setVisibility(View.VISIBLE);
            } else {
                itemCheckGridView.setVisibility(View.GONE);
            }
        }
        titleView.setText(getTitle());
        alertMsg = ((TextView) findViewById(R.id.textViewMessage));
        if (getMsg() == null) {
            alertMsg.setVisibility(View.GONE);
        }  else {
            alertMsg.setText(getMsg());
        }

        if (getBackClickListener() == null) {
            btnBack.setVisibility(View.GONE);
        } else {
            btnBack.setOnClickListener(backClickListener);
        }

        if (getSureClickListener() == null) {
            btnSure.setVisibility(View.GONE);
        } else {
            btnSure.setOnClickListener(sureClickListener);
        }

        if ((btnBack.getVisibility() == View.GONE) && (btnSure.getVisibility() == View.GONE)) {
            textViewDivider.setVisibility(View.GONE);
            localLinearLayout.setVisibility(View.GONE);
        }
        if ((btnBack.getVisibility() == View.VISIBLE) && (btnSure.getVisibility() == View.GONE)) {
            btnBack.setText("返回");
        }

        contentEditText.setHint(getContentHint());
        
        // KeyUnit.KeyOpen();
    }

    public TextView getAlertMsg() {
        return alertMsg;
    }

    public View.OnClickListener getBackClickListener() {
        return backClickListener;
    }

    public ArrayList<Integer> getCheckVals()
    {
        return checkVals;
    }

    public EditText getContentEditText() {
        return contentEditText;
    }

    public String getContentHint() {
        return contentHint;
    }

    public String getHighLight() {
        return highLight;
    }

    public AdapterView.OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public String[] getItems() {
        return Items;
    }

    public String getMsg() {
        return msg;
    }

    public View.OnClickListener getSureClickListener() {
        return sureClickListener;
    }

    public int getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public CheckArrayAdapter getrDateAdapter() {
        return rDateAdapter;
    }

    public boolean isShowDateGrid() {
        return isShowDateGrid;
    }

    public void onBackPressed() {
        super.onBackPressed();
        // if (MainMenuAct.updateType == 2) {
        // MainMenuAct.initData();
        // ((MainAct)context).finish();
        // }
    }

    @Override
    public void onClick(View paramView) {
        dismiss();
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.my_dialog);
        initView();
    }

    public void setBackClickListener(View.OnClickListener paramOnClickListener) {
        backClickListener = paramOnClickListener;
    }

    public void setCheckVals(ArrayList<Integer> paramArrayList) {
        checkVals = paramArrayList;
    }

    public void setCheckVals(boolean[] paramArrayOfBoolean) {
        ArrayList localArrayList = new ArrayList(paramArrayOfBoolean.length);
        for (int i = 0;; i++) {
            if (i >= paramArrayOfBoolean.length) {
                setCheckVals(localArrayList);
                return;
            }
            if (paramArrayOfBoolean[i]) {// jingbo
                localArrayList.add(Integer.valueOf((i + 1) % 7));
            }
        }
    }

    public void setContentHint(String paramString) {
        contentHint = paramString;
    }

    public void setHighLight(String paramString) {
        highLight = paramString;
    }

    public void setItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener) {
        itemClickListener = paramOnItemClickListener;
    }

    public void setItems(String[] paramArrayOfString) {
        Items = paramArrayOfString;
    }

    public void setMsg(String paramString) {
        msg = paramString;
    }

    public void setShowDateGrid(boolean paramBoolean) {
        isShowDateGrid = paramBoolean;
    }

    public void setSureClickListener(View.OnClickListener paramOnClickListener) {
        sureClickListener = paramOnClickListener;
    }

    public void setTag(int paramInt) {
        tag = paramInt;
    }

    public void setTitle(String paramString) {
        title = paramString;
    }

    public class CheckArrayAdapter extends ArrayAdapter<String> {
        ArrayList<Boolean> checkStateList;
        ViewHolder holder = null;
        private LayoutInflater mInflater;
        Resources mResources;
        int mResurceLayout;
        CheckBox tmpBox;

        public CheckArrayAdapter(Context c, int layoutId, String[] arg4) {
            super(c, layoutId, arg4);
            mResurceLayout = layoutId;
            mResources = c.getResources();
            mInflater = LayoutInflater.from(c);
            checkStateList = new ArrayList<Boolean>();
            Iterator iterator;
            for (int i = 0; i < arg4.length; i++) {
                checkStateList.add(Boolean.valueOf(false));
            }
            if (null != checkVals) {
                iterator = checkVals.iterator();
                while (iterator.hasNext()) {
                    int j = getRepeatDateOffset(((Integer) iterator.next()).intValue());
                    checkStateList.set(j, Boolean.valueOf(true));// jingbo 这是一个实例？？
                }
            }
        }

        private void setBtnStyle(Button btn, boolean checked) {
            if (checked) {
                btn.setBackgroundResource(R.drawable.green_btn_select);
                btn.setTextColor(mResources.getColor(R.color.white));
                btn.setTag(Integer.valueOf(1));
            } else {
                btn.setBackgroundResource(R.drawable.bg_optionlist_select);
                btn.setTextColor(mResources.getColor(R.color.titleColor));
                btn.setTag(Integer.valueOf(0));
            }
        }

        public ArrayList<Boolean> getCheckStateList() {
            return checkStateList;
        }

        @Override
        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
            if (paramView == null) {
                holder = new ViewHolder();
                paramView = mInflater.inflate(mResurceLayout, null);
                holder.rDateButton = ((Button) paramView.findViewById(R.id.buttonDate));
                holder.rDateButton.setOnClickListener(checkClickListener);
                paramView.setTag(holder);
            } else {
                holder = ((ViewHolder) paramView.getTag());
            }
            holder.rDateButton.setId(paramInt);
            holder.rDateButton.setText(((String) getItem(paramInt)).toString());
            setBtnStyle(holder.rDateButton, ((Boolean) checkStateList.get(paramInt)).booleanValue());
            return paramView;
        }

        public final class ViewHolder {
            public Button rDateButton;

            public ViewHolder() {
            }
        }

        View.OnClickListener checkClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {//TODO
                Log.v("CustomDialog", "----onClick---checkVals:" + checkVals.toString() + " checkStateList:" + checkStateList.toString());
                int j = 0;
                Button localButton = (Button) view;
                j = DATEVALS[view.getId()];
                if (view.getTag().toString().equals("1")) {
                    setBtnStyle(localButton, false);
//                    Collections.sort(checkVals);
                    if (checkVals.contains(Integer.valueOf(j))) {
                        checkStateList.set(view.getId(), Boolean.valueOf(false));
                        int k = getIntIndex(checkVals, j);
                        checkVals.remove(k);
                    }
                } else {
                    if (!checkVals.contains(Integer.valueOf(j))) {
                        checkStateList.set(view.getId(), Boolean.valueOf(true));
                        checkVals.add(Integer.valueOf(j));
                        setBtnStyle(localButton, true);
                    }
                }
                Log.v("CustomDialog", "----onClickend---checkVals:" + checkVals.toString() + " checkStateList:" + checkStateList.toString());
            }
        };

    }

    public static int getIntIndex(ArrayList<Integer> paramArrayList, int paramInt) {
        int i = paramArrayList.size();
        for (int j = 0;; j++) {
            if (j >= i)
                j = -1;
            while (((Integer) paramArrayList.get(j)).intValue() == paramInt)
                return j;
        }
    }
}
