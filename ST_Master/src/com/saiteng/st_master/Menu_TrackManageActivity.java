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

import com.saiteng.st_master.adapter.ManageTrackAdapter;
import com.saiteng.st_master.fragments.BottomTrackFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Menu_TrackManageActivity extends Activity{
	private ListView mlistView_trackmanage;
	public String[] arr;
	private Context context;
	private List<Map<String, Object>> data;
	private BottomTrackFragment mTrack;
	private ManageTrackAdapter mTrackAdapter;
	private String time_Track=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trackmanage);
		context = Menu_TrackManageActivity.this;
		mlistView_trackmanage = (ListView) findViewById(R.id.track_manage);
		new TrackDataTask().execute();
		mlistView_trackmanage.setOnItemClickListener(new OnItemClickListener() {
           /**ListView的item监听事件，点击item启动一个activity
            * 进行轨迹播放*/
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        TextView time = (TextView) view.findViewById(R.id.track_manage_txt);
		        Config.tacketime= time.getText().toString();
		        Intent intent = new Intent();
		        intent.putExtra("timedata",Config.tacketime);
		        intent.setClass(context, Trackplayback.class);
		        context.startActivity(intent);
				Log.i("Menu_TrackManageActivity", Config.tacketime);
			}
		});
		 //根据cheeckbox的勾选与否弹出底部菜单
	    Handler handler = new Handler(){
	    	public void handleMessage(Message msg) {
	    		super.handleMessage(msg);
	    			
	    			FragmentManager fm = getFragmentManager(); 
	    			 // 开启Fragment事务  切换底部的菜单栏
	    	        FragmentTransaction transaction = fm.beginTransaction();
	    			if("true".equals(msg.obj.toString())){
	    				mTrack = new BottomTrackFragment(context,Config.tacketime);
	    				transaction.replace(R.id.track_fragment, mTrack);
	    		    }else
	    		    	transaction.hide(mTrack);
	    			transaction.commit();
	    	}
	    };
	   Config.mTrackContext =handler;
		
	}
	class TrackDataTask extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			String result=null;
			HttpGet get = new HttpGet(Config.url+"locus?phonenum="+Config.phonenum);
			HttpClient client = new DefaultHttpClient();
			StringBuilder builder = null;
			try {
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {
					InputStream inputStream = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inputStream));
					builder = new StringBuilder();
					String s = null;
					for (s = reader.readLine(); s != null; s = reader.readLine()) {
						builder.append(s);
					}
					result=builder.toString();
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
			if("".equals(result)){
				Toast.makeText(Menu_TrackManageActivity.this, "设备暂无轨迹数据", Toast.LENGTH_SHORT).show();
			}else if(result!=null){
				arr = result.split(",");
				data = getData(arr);
				mTrackAdapter = new ManageTrackAdapter(context,data);
				mlistView_trackmanage.setAdapter(mTrackAdapter);
				Log.i("Menu_TrackManageActivity", result);
			}
		}
		private List<Map<String, Object>> getData(String[] arr) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String, Object> map ;
			for(int i=0;i<arr.length;i++){
				map = new HashMap<String, Object>();
				map.put("image", R.drawable.file);
				map.put("title",arr[i]);
				list.add(map);
			}
			return list;
		}
		
		
	}
}
