package com.saiteng.st_individual;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.saiteng.st_individual.adapter.Jiankong_adapter;
import com.saiteng.st_individual.conn.AskLatLngTask;
import com.saiteng.st_individual.view.PreViewPopwindow;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MonitoringActivity extends Activity implements OnClickListener{
	private ListView mView_listview;
	private Jiankong_adapter myAdapter;
	private List<Map<String, Object>> data;
	private Context context;
	private ProgressDialog dialog;//��ʾ��
	public String[] arr;
	private ImageView mImage;
	private RelativeLayout relativeLayout;
	private boolean bTransfer = true;
	private PreViewPopwindow preViewPopwindow;
	private String Path_Gps = "http://"+Config.ip+":"+Config.port+"/NA721/group?phonenum="+Config.phoneNum;
	//�ֻ���������loginActivity��ȡ��
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jiankong);
		mView_listview = (ListView) findViewById(R.id.jianlong_listview);
		context=MonitoringActivity.this;
		Config.mcontext=context;
		//����������
		mImage = (ImageView) findViewById(R.id.jiankong_btn);
		mImage.setOnClickListener(this);
		relativeLayout = (RelativeLayout)findViewById(R.id.jiankong_layout);
		
		/**��ʾ��*/
		dialog = new ProgressDialog(this);
		dialog.setTitle("��ʾ��");
		dialog.setMessage("���ڼ�����...");
	
		new MyTask().execute();
		
		//�������

		mView_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            TextView content=(TextView) view.findViewById(R.id.phoneNum);
				new AskLatLngTask().execute(content.getText().toString());
				
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
			dialog.dismiss();
			if("NetworkException".equals(result)){
				Toast.makeText(context, "�����쳣", Toast.LENGTH_LONG).show();
				finish();
			}else if("Exception".equals(result)){
				Toast.makeText(context, "�����쳣", Toast.LENGTH_LONG).show();
				finish();
			}else{
				if(result!=null){
					arr=result.split(",");
					data=getData(arr);
					myAdapter = new Jiankong_adapter(context,data);
					mView_listview.setAdapter(myAdapter);
				}
			}
		}		
	}
	public List<Map<String, Object>> getData(String[] arr) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		 Map<String, Object> map ;
		 String[] arr_group=null;
		 for(int i=0;i<arr.length;i++){
			arr_group = null;
			arr_group = arr[i].split("-");
			map= new HashMap<String, Object>();
			map.put("image", R.drawable.ic_launcher);
			map.put("phonenum", arr_group[1]);
			map.put("phonename", arr_group[0]);
			list.add(map);
		 }
		return list;
	}
	@SuppressWarnings("deprecation")
	public String RequestData() {
		String result=null;
		HttpGet get = new HttpGet(Path_Gps);
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
	@Override
	public void onClick(View v) {
       if (bTransfer) {
			
			preViewPopwindow = new PreViewPopwindow(context);
			preViewPopwindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					bTransfer = true;
				}
			});
			int[] location = new int[2];
			relativeLayout.getLocationOnScreen(location);
			preViewPopwindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY,
					location[0] - preViewPopwindow.getWidth(), 0);
			bTransfer = false;
		}else{
			preViewPopwindow.dismiss();
			bTransfer = true;
		}
		
	}
	public void exitSystem() {
		Config.mIsLogined=false;
		Config.mIsFristLogined=false;
		Config.ip=null;
		Config.port=null;
		Config.phoneNum=null;
		Config.loginInfo=null;
		Config.medit.clear();
		Config.medit.commit();
		finish();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}
