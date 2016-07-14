package com.saiteng.st_master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.saiteng.st_master.adapter.ManageAdapter;
import com.saiteng.st_master.conn.ConnSocketServer;
import com.saiteng.st_master.fragments.BottomGenzongFragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

public class Menu_TrackActivity extends Activity{
	private static Context context;
	private static ListView genzong_listView;
	private static List<Map<String, Object>> data;
	private static ManageAdapter manageadapter;
	public String[] arr;
	private BottomGenzongFragment XinBiao;
	private String[] msg_arr;
	private static ProgressDialog dialog;//提示框
	private static String[] arr_data;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_genzong);
		context=Menu_TrackActivity.this;
		Config.mManagecontext=context;
		genzong_listView = (ListView) findViewById(R.id.genzong_listview);
		ConnSocketServer.sendOrder("[ST*"+Config.imei+"*GetDivice");
		 //根据点击不同的按钮弹出不同的底部菜单
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				msg_arr = null;
				msg_arr = msg.obj.toString().split(",");
				Config.phonenum = msg_arr[1];
				FragmentManager fm = getFragmentManager();
				// 开启Fragment事务
				FragmentTransaction transaction = fm.beginTransaction();
				XinBiao = new BottomGenzongFragment();
				transaction.replace(R.id.genzong_fragment, XinBiao);
				transaction.commit();
			}
		};
	    Config.mhandler = handler;
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
				arr_data = divicedata.substring(17, divicedata.length()-1).split(",");
				data = getData(arr_data);
				manageadapter = new ManageAdapter(context, data);
				genzong_listView.setAdapter(manageadapter);
			}
		}

	}
		//根据访问数据库得到的数据设置数据源
	private static List<Map<String, Object>> getData(String[] arr1) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		if (arr1.length<2) {

		} else {
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
}
