package com.saiteng.st_master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.saiteng.st_master.adapter.ManageAdapter;
import com.saiteng.st_master.conn.ConnSocketServer;
import com.saiteng.st_master.fragments.BottomDanBingFragment;
import com.saiteng.st_master.fragments.BottomFragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
/**点击 设备管理 后展示的设备列表和默认的底部导航栏*/
public class Menu_ManageActivity extends Activity{
	private static ListView mView_menuManagelistView;
	private static List<Map<String, Object>> data;
	private static ManageAdapter manageadapter;
	private static Context context;
	private BottomDanBingFragment danbing;
	private BottomFragment bottom;
	private static ProgressDialog dialog;//提示框
	private String[] msg_arr;
	private static String[] arr_data;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_manage);
		context=Menu_ManageActivity.this;
		Config.mManagecontext=context;
		mView_menuManagelistView = (ListView) findViewById(R.id.menu_manage_listview);
		ConnSocketServer.sendOrder("[ST*"+Config.imei+"*GetDivice");
        setDefaultFragment(); 
        //根据点击不同的按钮弹出不同的底部菜单
	    Handler handler = new Handler(){
	    	@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				msg_arr = null;
				msg_arr = msg.obj.toString().split(",");
				danbing = new BottomDanBingFragment();
				Config.phonenum = msg_arr[1];
				FragmentManager fm = getFragmentManager();
				// 开启Fragment事务根据选择不同类型的设备切换底部的导航栏
				FragmentTransaction transaction = fm.beginTransaction();
				transaction.replace(R.id.buttom_fragment, danbing);
				transaction.commit();
			}
	    };
	   Config.mhandler = handler;
	   //Listview的长按事件
	   mView_menuManagelistView.setOnItemLongClickListener(new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			
			return false;
		}
	});
	}
	
	// 设置默认的Fragment 底部导航栏
	private void setDefaultFragment() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		bottom = new BottomFragment();
		transaction.replace(R.id.buttom_fragment, bottom);
		transaction.commit();
		dialog = new ProgressDialog(context);
		dialog.setTitle("提示！");
		dialog.setMessage("正在加载设备列表...");
		dialog.show();
	}
	
	
	public static void setDiviceData(String divicedata) {
		if (dialog != null) {
			dialog.dismiss();
		}
		if (context != null) {
			if (divicedata.length() < 20) {

			} else {
				arr_data = divicedata.substring(17, divicedata.length() - 1).split(",");
				data = getData(arr_data);
				manageadapter = new ManageAdapter(context, data);
				mView_menuManagelistView.setAdapter(manageadapter);
			}
		}

	}
	//根据访问数据库得到的数据设置数据源
	private static List<Map<String, Object>> getData(String[] arr1) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		if(arr1.length<2){
		}else{
			for (int i = 0; i < arr1.length; i++) {
				map = new HashMap<String, Object>();
				String[] arr_group = null;
				arr_group = arr1[i].split("#");
				map.put("image", R.drawable.xinbiao);
				map.put("divicename", arr_group[0]);
				map.put("divicenum", arr_group[1]);
				list.add(map);
			}
		}
		return list;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
