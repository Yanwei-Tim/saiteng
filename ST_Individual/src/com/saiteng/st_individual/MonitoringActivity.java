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
	private ProgressDialog dialog;//��ʾ��
	public String[] arr=new String[10];
	//�ֻ���������loginActivity��ȡ��
	private String Path="http://192.168.0.59:8080/NA721/group?name=13764005641";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jiankong);
		mView_listview = (ListView) findViewById(R.id.jianlong_listview);
		context=MonitoringActivity.this;
		/**��ʾ��*/
		dialog = new ProgressDialog(this);
		dialog.setTitle("��ʾ��");
		dialog.setMessage("���ڼ�����...");
	
		new MyTask().execute(Path);
		
		//�������

		mView_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(context, "��ʼ��������", Toast.LENGTH_LONG).show();
				
			}
		});
		
	}
	/**
	 * MyTask�̳��̳߳�AsyncTask�������������������ݽ��������ݸ��µȲ���
	 * */
	 class MyTask extends AsyncTask<String, Void, String> {
		
		/**
		 * ��������ǰ��ʾdialog��
		 */
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}
		/**
		  * ��doInBackground�����У���һЩ������������Ⱥ�ʱ������
		  */
		@Override
		public String doInBackground(String... params) {
			return RequestData();
		}
		
		/**
		 * �ڸ÷����У���Ҫ����һЩ���ݵĴ������¡�
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
