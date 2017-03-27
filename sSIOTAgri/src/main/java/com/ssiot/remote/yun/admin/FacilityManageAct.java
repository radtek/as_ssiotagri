package com.ssiot.remote.yun.admin;

import java.util.ArrayList;

import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.agri.R;
import com.ssiot.fish.HeadActivity;
import com.ssiot.remote.Utils;
import com.ssiot.remote.data.model.FacilityModel;
import com.ssiot.remote.data.model.LandModel;
import com.ssiot.remote.myzxing.MipcaActivityCapture;
import com.ssiot.remote.yun.webapi.WS_Facility;
import com.ssiot.remote.yun.webapi.WS_Land;

public class FacilityManageAct extends HeadActivity{
	private static final String tag = "FacilityManageAct";
	ExpandableListView expandablelistview;  
	BuddyAdapter adapter;
	ArrayList<LandModel> groupArr = new ArrayList<LandModel>();
	
    private static final int MSG_GOT = 1;
    private Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case MSG_GOT:
				ArrayList<LandModel> list = (ArrayList<LandModel>) msg.obj;
				groupArr.clear();
				groupArr.addAll(list);
				adapter.refresh();
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
		setContentView(R.layout.act_facility_manage);
		initIconTitleBar();
		initExpandView();
		getLandsAndFacilitiesStart();
	}
	
	private void initIconTitleBar(){
		initTitleLeft(R.id.title_bar_left);
		View addIcon = findViewById(R.id.title_bar_menu_icon);
		addIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopMenu(v);
			}
		});
	}
	
	private void initExpandView(){
		expandablelistview= (ExpandableListView) findViewById(R.id.expandableListView);  
        adapter = new BuddyAdapter(this,groupArr);  
        expandablelistview.setAdapter(adapter);
        //分组展开  
        expandablelistview.setOnGroupExpandListener(new OnGroupExpandListener(){
            public void onGroupExpand(int groupPosition) {
            }
        });  
        //分组关闭  
        expandablelistview.setOnGroupCollapseListener(new OnGroupCollapseListener(){  
            public void onGroupCollapse(int groupPosition) {
            }  
        });  
        //子项单击  
        expandablelistview.setOnChildClickListener(new OnChildClickListener(){  
            public boolean onChildClick(ExpandableListView arg0, View view,  
                    int groupPosition, int childPosition, long arg4) {
            	FacilityModel f = (FacilityModel) view.getTag();
            	Intent intent = new Intent(FacilityManageAct.this,DeviceTagEditorAct.class);
            	intent.putExtra("facilitymodel", f);
            	startActivity(intent);
                return true;
            }
        });
        expandablelistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        	@Override
        	public boolean onItemLongClick(AdapterView<?> parent, View view,
        			int position, long id) {
        		Object object = view.getTag();
        		if (object instanceof FacilityModel){
        			FacilityModel f = (FacilityModel) object;
//        			showToast("" + f._facilitiesname);
//        			view.showContextMenu();
        			showMenuDia(object);
        		} else if (object instanceof LandModel){
        			LandModel l = (LandModel) object;
//        			showToast(""+l._name);
//        			view.showContextMenu();
        			showMenuDia(object);
        		}
        		return true;
        	}
		});
	}
	
	private void showMenuDia(final Object modelObject){
		AlertDialog.Builder bui = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(R.layout.menu_delete_modify, null);
		View deleteBtn = view.findViewById(R.id.itm_delete);
		View modifyBtn = view.findViewById(R.id.itm_modify);
		
		bui.setView(view);
		bui.setTitle("选择操作");
		final AlertDialog dia = bui.create();
		deleteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (modelObject instanceof FacilityModel){
					FacilityModel f = (FacilityModel) modelObject;
					new DeleteFacilityThread(f._facilitiesid).start();
				} else if (modelObject instanceof LandModel){
					LandModel l = (LandModel) modelObject;
					new DeleteLandThread(l).start();
				}
				dia.dismiss();
			}
		});
		modifyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showToast("开发中");
				dia.dismiss();
			}
		});
		dia.show();
	}
	
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//			ContextMenuInfo menuInfo) {
//		menu.setHeaderTitle("选择操作");
//		menu.add(0, 1, 0, "删除");
//		menu.add(0, 2, 0, "修改");
//		super.onCreateContextMenu(menu, v, menuInfo);
//	}
	
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		Object object = item.getActionView().getTag();
//		switch (item.getItemId()) {
//		case 1:
//			if (object instanceof FacilityModel){
//				FacilityModel f = (FacilityModel) object;
//				new DeleteFacilityThread(f._facilitiesid).start();
//			} else if (object instanceof LandModel){
//				LandModel l = (LandModel) object;
//				new DeleteLandThread(l).start();
//			}
//			break;
//		case 2:
//			showToast("开发中");
//			break;
//
//		default:
//			break;
//		}
//		return super.onContextItemSelected(item);
//	}
	
	private class DeleteFacilityThread extends Thread{
		int id = 0;
		private DeleteFacilityThread(int id){
			this.id = id;
		}
		
		@Override
		public void run() {
			int ret = new WS_Facility().DeleteAgricultureFacilities(id);
			if (ret <= 0){
				sendToast("失败");
			} else {
				getLandsAndFacilitiesStart();
			}
		}
	}
	
	private class DeleteLandThread extends Thread{
		LandModel lm;
		private DeleteLandThread(LandModel lm){
			this.lm = lm;
		}
		@Override
		public void run() {
			for (int i = 0; i < lm.facilities.size(); i ++){
				int ret = new WS_Facility().DeleteAgricultureFacilities(lm.facilities.get(i)._facilitiesid);
				if (ret <= 0 ){
					sendToast("删除失败");
				}
			}
			int ret2 = new WS_Land().Delete(lm._landid);
			if (ret2 <= 0){
				sendToast("删除失败");
			} else {
				getLandsAndFacilitiesStart();
			}
		}
	}
	
//	private void showMenuDialog(){
//		String[] items = {"删除","修改"};
//		AlertDialog.Builder bui = new AlertDialog.Builder(this);
//		ArrayAdapter localArrayAdapter = new ArrayAdapter(this,
//				 R.layout.dialog_list_item, R.id.textViewContent, items);
//		bui.setAdapter(localArrayAdapter, new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				
//			}
//		});
//        bui.setTitle("请选择").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            	txt_land.setText(mLandStr);
//            }
//        }).setNegativeButton(android.R.string.cancel, null);
//        bui.create().show();
//	}
	
	private void getLandsAndFacilitiesStart(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				String account = Utils.getStrPref(Utils.PREF_USERNAME, FacilityManageAct.this);
				ArrayList<LandModel> lands = new WS_Land().GetLandsByAccount(account);
				String landids = "";
				if (null != lands){
					for (LandModel m : lands){
						landids += m._landid + ",";
					}
					if (landids.endsWith(",")){
						landids = landids.substring(0,landids.length()-1);
					}
				}
				ArrayList<FacilityModel> facilities = new WS_Facility().GetAgricultureFacilities("landid in ("+landids+")");
				if (null != lands){
					for (int i = 0; i < lands.size(); i ++){
						LandModel landModel = lands.get(i);
						if (null != facilities){
							for (FacilityModel f : facilities){
								if (landModel._landid == f._landid){
									landModel.facilities.add(f);
								}
							}
						}
					}
				}
				Message msg = new Message();
				msg.what = MSG_GOT;
				msg.obj = lands;
				mHandler.sendMessage(msg);
			}
		}).start();
	}
	
	public class BuddyAdapter extends BaseExpandableListAdapter {     
	    private ArrayList<LandModel> group;   
	    private Context context;  
	    LayoutInflater inflater;
	    
	    public BuddyAdapter(Context context,ArrayList<LandModel> group){  
	        this.context=context;
	        inflater = LayoutInflater.from(context);  
	        this.group=group;
	    }
	    @Override
	    public Object getChild(int groupPosition, int childPosition) {
	    	return group.get(groupPosition).getFacility(childPosition);
	    }
	  
	    @Override
	    public long getChildId(int groupPosition, int childPosition) {
	        return childPosition;
	    }
	  
	    @Override
	    public View getChildView(int groupPosition, int childPosition, boolean arg2, View convertView,  
	            ViewGroup arg4) {  
	        convertView = inflater.inflate(R.layout.expandlist_itm_child, null);  
	        TextView nickTextView=(TextView) convertView.findViewById(R.id.buddy_listview_child_title);  
	        FacilityModel fModel = group.get(groupPosition).getFacility(childPosition);
	        nickTextView.setText(fModel._facilitiesname);
	        TextView detailTextView = (TextView) convertView.findViewById(R.id.buddy_listview_child_trends);
	        detailTextView.setText("面积:"+fModel._areacovered + fModel._areacoveredunit + 
	        		" 坐标:" + fModel._longitude + "," + fModel._latitude);
	        convertView.setTag(fModel);
	        return convertView;
	    }  
	  
	    @Override
	    public int getChildrenCount(int groupPosition) {  
	        return group.get(groupPosition).facilities.size();  
	    }  
	  
	    @Override
	    public Object getGroup(int groupPosition) {  
	        return group.get(groupPosition);  
	    }  
	  
	    @Override
	    public int getGroupCount() {  
	        return group.size();  
	    }  
	  
	    @Override
	    public long getGroupId(int groupPosition) {  
	        return groupPosition;  
	    }
	  
	    @Override
	    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup arg3) {  
	        convertView = inflater.inflate(R.layout.expandlist_itm_parent, null);  
	        TextView groupNameTextView=(TextView) convertView.findViewById(R.id.buddy_listview_group_name);
	        TextView numTextView = (TextView) convertView.findViewById(R.id.buddy_listview_group_num);
	        LandModel lm = group.get(groupPosition);
	        groupNameTextView.setText(lm._name);
	        numTextView.setText("[" + lm.facilities.size() + "]");
	        ImageView image = (ImageView) convertView.findViewById(R.id.buddy_listview_image);  
	        //更换展开分组图片  
	        if(isExpanded){
	            image.setImageResource(R.drawable.arrow_t_down);  
	        } else {
	        	image.setImageResource(R.drawable.arrow_t_right); 
	        }
	        convertView.setTag(lm);
	        return convertView;  
	    }  
	  
	    @Override
	    public boolean hasStableIds() {  
	        return true;  
	    }  
	    // 子选项是否可以选择    
	    @Override
	    public boolean isChildSelectable(int arg0, int arg1) {  
	        // TODO Auto-generated method stub  
	        return true;
	    }
	    
	    public void refresh(){
	    	notifyDataSetChanged();
	    }
	}
	
	private void showPopMenu(View anchor){//TODO 
		View menuView = LayoutInflater.from(this).inflate(R.layout.pop_menu_add, null,false);
		View landV = menuView.findViewById(R.id.pop_add_land);
		View facilityV = menuView.findViewById(R.id.pop_add_facility);
//		View deviceV = menuView.findViewById(R.id.pop_add_device);
		
		final PopupWindow popupWindow = new PopupWindow(menuView, 
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		landV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FacilityManageAct.this, LandEditAct.class);
				startActivityForResult(intent, REQUEST_ADDLAND);
				popupWindow.dismiss();
			}
		});
		facilityV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FacilityManageAct.this, FacilityEditAct.class);
				intent.putExtra("landmodels", groupArr);
				startActivityForResult(intent, REQUEST_ADDFACILITY);
				popupWindow.dismiss();
			}
		});
//		deviceV.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(FacilityManageAct.this, MipcaActivityCapture.class);
//				startActivityForResult(intent, REQUEST_ADDDEVICE);
//				popupWindow.dismiss();
//			}
//		});
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.anim.roll_down);
		popupWindow.showAsDropDown(anchor, 0, 0);
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.menu_landfacility, menu);
//		return true;
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.action_popmenu:
//			showPopMenu(null);
////			Intent intent = new Intent(this, LandEditAct.class);
////			startActivityForResult(intent, REQUEST_ADDLAND);
//			break;
////		case R.id.action_add_facility:
////			Intent i = new Intent(this, FacilityEditAct.class);
////			i.putExtra("landmodels", groupArr);
////			startActivityForResult(i, REQUEST_ADDFACILITY);
////			break;
//		default:
//			break;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	private static final int REQUEST_ADDLAND = 1;
	private static final int REQUEST_ADDFACILITY = 2;
	private static final int REQUEST_ADDDEVICE = 3;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode){
			switch (requestCode) {
			case REQUEST_ADDLAND:
				getLandsAndFacilitiesStart();
				break;
			case REQUEST_ADDFACILITY:
				getLandsAndFacilitiesStart();
				break;
			case REQUEST_ADDDEVICE:
				Intent i = new Intent(this, DeviceRegistAct.class);
				String serialno = data.getStringExtra("qrcode");
				i.putExtra("serialno", serialno);
				startActivity(i);
				break;
	
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}