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

import com.saiteng.st_individual.adapter.Locus_adapter;
import com.saiteng.st_individual.conn.AskLocusTask;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

public class LocusActivity extends Activity implements OnClickListener{
	private ListView mListView_guiji;
	private ImageView mImage1;
	private Context context;
	private Locus_adapter locusadapter;
	private RelativeLayout relativeLayout1;
	private boolean bTransfer = true;
	private ProgressDialog dialog;//��ʾ��
	private PreViewPopwindow preViewPopwindow;
	private List<Map<String, Object>> data;
	public String[] arr;
	private String Path_Gps = "http://"+Config.ip+":"+Config.port+"/NA721/group?phonenum="+Config.phoneNum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guiji);
		context=LocusActivity.this;
		Config.Locus_mcontext=context;
		mListView_guiji = (ListView) findViewById(R.id.guiji_listview);
		//����������
		mImage1 = (ImageView) findViewById(R.id.jiankong_btn);
		mImage1.setOnClickListener(this);
		relativeLayout1 = (RelativeLayout)findViewById(R.id.jiankong_layout);
		
		
		
		new LocusTask().execute();
		
		mListView_guiji.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				 TextView content=(TextView) view.findViewById(R.id.phoneNum_guiji);
				 new AskLocusTask().execute(content.getText().toString());
				// Toast.makeText(context,content.getText().toString(),Toast.LENGTH_LONG).show();
			}
		});
	}
	
	/**
	 * LocusTask�̳��̳߳�AsyncTask�������������������ݽ��������ݸ��µȲ���
	 * */
	
	 class LocusTask extends AsyncTask<String, Void, String> {
		   /**
			 * ��������ǰ��ʾdialog��
			 */
			@Override
			public void onPreExecute() {
				super.onPreExecute();
				
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
				if("NetworkException".equals(result)){
					Toast.makeText(context, "�����쳣", Toast.LENGTH_LONG).show();
					finish();
				}else if("Exception".equals(result)){
					Toast.makeText(context, "�����쳣", Toast.LENGTH_LONG).show();
					finish();
				}else{
					arr=result.split(",");
					data=getData(arr);
					locusadapter = new Locus_adapter(context,data);
					mListView_guiji.setAdapter(locusadapter);
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
					map.put("image", R.drawable.folder);
					map.put("phonenum", arr_group[1]);
					list.add(map);
				 }
				return list;
			}
			
			/**������ж�С���ڵĳ�Ա��Ϣ*/
			@SuppressWarnings("deprecation")
			private String RequestData() {
				String result=null;
				HttpGet get = new HttpGet(Path_Gps);
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
				relativeLayout1.getLocationOnScreen(location);
				preViewPopwindow.showAtLocation(relativeLayout1, Gravity.NO_GRAVITY,
						location[0] - preViewPopwindow.getWidth(), 0);
				bTransfer = false;
			}else{
				preViewPopwindow.dismiss();
				bTransfer = true;
			}
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}


