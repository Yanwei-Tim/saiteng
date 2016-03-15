package com.saiteng.st_individual.conn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.saiteng.st_individual.Config;
import com.saiteng.st_individual.DrawActivity;
import com.saiteng.st_individual.LocusDatilsActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class AskDetailsTask extends AsyncTask<String, Void, String>{
	private String url=null;
	/**
	  * 数据请求前显示dialog。
	  */
	@Override
	public void onPreExecute() {
		super.onPreExecute();

	}
	/**
	  * 在doInBackground方法中，做一些诸如网络请求等耗时操作。
	  */
	@Override
	protected String doInBackground(String... params) {
		this.url=params[0];
		
		return RequestData("http://"+Config.ip+":"+Config.port+"/NA721/locusdetails?time="+url.substring(0, 14));
	}
	@Override
	public void onPostExecute(String result) {
		if("NetworkException".equals(result)){
			Toast.makeText(Config.Locusdetails_mcontext, "网络异常", Toast.LENGTH_LONG).show();
			
		}else if("Exception".equals(result)){
			Toast.makeText(Config.Locusdetails_mcontext, "网络异常", Toast.LENGTH_LONG).show();	
		}else if("null".equals(result)){
			Toast.makeText(Config.mcontext,"设备"+url+"暂无定位数据",Toast.LENGTH_LONG).show();}
		else{
			String[] array = result.split(",");
			if(array.length==1){
				Toast.makeText(Config.Locusdetails_mcontext,"设备"+url+"暂无轨迹数据",Toast.LENGTH_LONG).show();
			}else{
				Intent intent = new Intent();
				intent.putExtra("showdata", array);
				intent.setClass(Config.Locusdetails_mcontext, DrawActivity.class);
				Config.Locusdetails_mcontext.startActivity(intent);
			}
			
		}
	}
	
	@SuppressWarnings("deprecation")
	private String RequestData(String path) {
		String result=null;
		HttpGet get = new HttpGet(path);
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
