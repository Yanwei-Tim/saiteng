package com.saiteng.st_master;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.saiteng.st_master.adapter.ManageAdapter;
import com.saiteng.st_master.fragments.BottomDanBingFragment;
import com.saiteng.st_master.fragments.BottomFragment;
import com.saiteng.st_master.fragments.BottonXinBiaoFragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

@SuppressWarnings("deprecation")
public class Menu_ManageActivity extends Activity{
	private ListView mView_menuManagelistView;
	private List<Map<String, Object>> data;
	private ManageAdapter manageadapter;
	public String[] arr;
	private Context context;
	private BottomDanBingFragment danbing;
	private BottonXinBiaoFragment XinBiao;
	private BottomFragment bottom;
	private boolean flag=true;
	private String[] msg_arr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_manage);
		context=Menu_ManageActivity.this;
		Config.mManagecontext=context;
		mView_menuManagelistView = (ListView) findViewById(R.id.menu_manage_listview);
	    new GroupnumTask().execute();
        setDefaultFragment(); 
        //根据点击不同的按钮弹出不同的底部菜单
	    Handler handler = new Handler(){
	    	@Override
	    	public void handleMessage(Message msg) {
	    		super.handleMessage(msg);
	    		 msg_arr=null;
	    		 msg_arr = msg.obj.toString().split(",");
	    		 danbing = new BottomDanBingFragment();
	    		 XinBiao = new BottonXinBiaoFragment();
	    	     Config.phonenum = msg_arr[1];
	    		 FragmentManager fm = getFragmentManager();  
    	        // 开启Fragment事务  
    	        FragmentTransaction transaction = fm.beginTransaction(); 
	    		if("2130837510".equals(msg_arr[0])){
		    	    transaction.replace(R.id.buttom_fragment, XinBiao);
	    		}else{
		    	    transaction.replace(R.id.buttom_fragment, danbing);
		    	   
	    		}
	    		 transaction.commit();
	    	}
	    };
	   Config.mhandler = handler;
	   //Listview的长按事件
	   mView_menuManagelistView.setOnItemLongClickListener(new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			return false;
		}
	});
	}
	
	// 设置默认的Fragment  
	private void setDefaultFragment() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		bottom = new BottomFragment();
		transaction.replace(R.id.buttom_fragment, bottom);
		transaction.commit();

	}


	//另起线程获得网络数据
	class GroupnumTask extends AsyncTask<String, Void, String>{
		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(String... params) {
			String result=null;
			HttpGet get = new HttpGet(Config.url+"group");
			HttpClient client = new DefaultHttpClient();
			StringBuilder builder = null;
			try {
				
				HttpResponse response = client.execute(get);
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					InputStream inputStream = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					builder = new StringBuilder();
					String s = null;
					for (s = reader.readLine(); s != null; s = reader.readLine()) {
						builder.append(s);
					}
					result = builder.toString();
				}else{
					result ="NetworkException";
				}
			} catch (Exception e) {
				e.printStackTrace();
				result="Exception";
				
			}
			return result;
		}
		/**
		 * 在该方法中，主要进行一些数据的处理，更新。
		 */
		@Override
		public void onPostExecute(String result) {
			if("NetworkException".equals(result)){
				
			}else if("Exception".equals(result)){
				
			}else{
				if(result!=null){
					arr=result.split(",");
					data=getData(arr);
					manageadapter = new ManageAdapter(context,data);
					mView_menuManagelistView.setAdapter(manageadapter);
				}
			}
			
		}
		//根据访问数据库得到的数据设置数据源
		private List<Map<String, Object>> getData(String[] arr1) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			 Map<String, Object> map ;
			 for(int i=0;i<arr.length;i++){
				 map= new HashMap<String, Object>();
				 String[] arr_group=null;
				 arr_group = arr[i].split("-");
				 if("0".equals(arr_group[2])){
					 map.put("image", R.drawable.danbing);
				 }else
					 map.put("image", R.drawable.xinbiao);
				 map.put("divicename", arr_group[0]);
				 map.put("divicenum", arr_group[1]);
				 list.add(map);
			 }
			return list;
		}
		
	}


	
		

}
