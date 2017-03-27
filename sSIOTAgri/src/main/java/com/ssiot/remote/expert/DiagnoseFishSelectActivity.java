package com.ssiot.remote.expert;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ssiot.remote.BrowserActivity;
import com.ssiot.fish.HeadActivity;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.FishTypeModel;
import com.ssiot.remote.yun.webapi.WS_Fish;
import com.ssiot.agri.R;

public class DiagnoseFishSelectActivity extends HeadActivity{
    private static final String tag = "DiagnoseFishSelectActivity";
//    String[] mItems;
//    Integer[] mIntItems;
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<Integer> nameIds = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_select);
//        mItems = getResources().getStringArray(R.array.fishes);
//        mIntItems = getResources().getIntArray(R.array.fishes_int);
        initList();
        new GetAllTypesThread().start();
    }
    
    private void initList(){
        ListView listView = (ListView) findViewById(R.id.fish_select_list);
        if (null != names){
        	listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent(DiagnoseFishSelectActivity.this, BrowserActivity.class);
//                i.putExtra("url", "http://www.adds.org.cn/SelfDiagnosis/GetContent/" + mIntItems[position]);
            	Intent i = new Intent(DiagnoseFishSelectActivity.this,SymptomAct.class);
            	i.putExtra("fishtypeid", nameIds.get(position));
                startActivity(i);
            }
        });
    }
    
    private class GetAllTypesThread extends Thread{
    	@Override
    	public void run() {
    		String account = Utils.getStrPref(Utils.PREF_USERNAME, DiagnoseFishSelectActivity.this);
    		List<FishTypeModel> types = new WS_Fish().GetAllCropTypes(account);
    		if (null != types && types.size() > 0){
    			for (int i = 0; i < types.size(); i ++){
    				names.add(types.get(i)._name);
    				nameIds.add(types.get(i)._id);
    			}
    		}
    		runOnUiThread(new Runnable() {
				public void run() {
					initList();
				}
			});
    	}
    }
    
}