package com.saiteng.st_individual;

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

import com.saiteng.st_individual.adapter.Jiankong_adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MonitoringActivity extends Activity{
	private ListView mView_listview;
	private Jiankong_adapter myAdapter;
	private List<Map<String, Object>> data;
	private Context context;
	private ProgressDialog dialog;//提示框
	public String[] arr=new String[10];
	//手机号码在在loginActivity中取得
	private String Path="http://192.168.0.59:8080/NA721/group?name=13764005641";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jiankong);
		mView_listview = (ListView) findViewById(R.id.jianlong_listview);
		context=MonitoringActivity.this;
		/**提示框*/
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示！");
		dialog.setMessage("正在加载中...");
	
		new MyTask().execute(Path);
		
		//填充数据

		mView_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(context, "开始跟踪设置", Toast.LENGTH_LONG).show();
				
			}
		});
		
	}
	/**
	 * MyTask继承线程池AsyncTask用来网络数据请求、数据解析、数据更新等操作
	 * */
	 class MyTask extends AsyncTask<String, Void, String> {
		
		/**
		 * 数据请求前显示dialog。
		 */
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}
		/**
		  * 在doInBackground方法中，做一些诸如网络请求等耗时操作。
		  */
		@Override
		public String doInBackground(String... params) {
			return RequestData();
		}
		
		/**
		 * 在该方法中，主要进行一些数据的处理，更新。
		 */
		@Override
		public void onPostExecute(String result) {
			if(result!=null){
				arr=result.split(",");
				data=getData(arr);
				myAdapter = new Jiankong_adapter(context,data);
				mView_listview.setAdapter(myAdapter);
				Toast.makeText(context,data+"",Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
		}		
	}
	public List<Map<String, Object>> getData(String[] arr) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		 Map<String, Object> map ;
		 String[] arr_group=null;
		 for(int i=0;i<arr.length;i++){
			arr_group=null;
			arr_group = arr[i].split("-");
			map= new HashMap<String, Object>();
			map.put("image", R.drawable.ic_launcher);
			map.put("phonenum", arr_group[1]);
			map.put("phonename", arr_group[0]);
			list.add(map);
		 }
		return list;
	}

	public String RequestData() {
		HttpGet get = new HttpGet(Path);
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

}
