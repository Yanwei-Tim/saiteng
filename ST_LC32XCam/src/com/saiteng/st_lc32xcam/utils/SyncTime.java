package com.saiteng.st_lc32xcam.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.text.format.DateFormat;

/**同步手机时间给设备*/
public class SyncTime extends AsyncTask<String, Void, String>{
	private String result=null;
	private String time=null;
	@SuppressWarnings("static-access")
	public SyncTime(){
		time = (String) new DateFormat().format("yyyy-MM-dd hh:mm:ss", Calendar.getInstance(Locale.CHINA));
	}

	@Override
	protected String doInBackground(String... params) {//"http://192.168.11.123/api/trio_tmr?val=2016-06-23 09:26:19"
		HttpGet get = new HttpGet(SmartCamDefine.SMARTCAM_URL+"/api/trio_tmr?val="+time);
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
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}

}
