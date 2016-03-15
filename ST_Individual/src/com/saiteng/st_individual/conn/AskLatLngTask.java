package com.saiteng.st_individual.conn;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.saiteng.st_individual.Config;
import com.saiteng.st_individual.TrackActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
/**监控功能请求该设备最新的坐标信息在地图上以定位点的形式展示*/
public class AskLatLngTask extends AsyncTask<String, Void, String>{
	private String url=null;
	
	/**
	  * 数据请求前显示dialog。
	  */
	@Override
	public void onPreExecute() {
		super.onPreExecute();
		/**提示框*/
		
	}
	/**
	  * 在doInBackground方法中，做一些诸如网络请求等耗时操作。
	  */
	@Override
	protected String doInBackground(String... params) {
		this.url=params[0];
		return RequestData("http://"+Config.ip+":"+Config.port+"/NA721/latLng?phonenum="+url);
	}
	
	/**
	 * 在该方法中，主要进行一些数据的处理，更新。
	 */
	@Override
	public void onPostExecute(String result) {
		
		if("NetworkException".equals(result)){
			Toast.makeText(Config.mcontext, "确认服务器开启", Toast.LENGTH_LONG).show();
			
		}else if("Exception".equals(result)){
			Toast.makeText(Config.mcontext, "网络异常", Toast.LENGTH_LONG).show();	
		}else if("null".equals(result)){
			Toast.makeText(Config.mcontext,"设备"+url+"暂无定位数据",Toast.LENGTH_LONG).show();}
		else{
			String[] array = result.split(",");
			Intent intent = new Intent();
			intent.putExtra("longitude", array[0]);
			intent.putExtra("latitude", array[1]);
			intent.setClass(Config.mcontext, TrackActivity.class);
			Config.mcontext.startActivity(intent);
		}
		
	}

	@SuppressWarnings("deprecation")
	private String RequestData(String path2) {
		String result=null;
		HttpGet get = new HttpGet(path2);
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

