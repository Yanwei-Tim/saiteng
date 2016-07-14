package com.saiteng.st_master.conn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.saiteng.st_master.Config;

import android.os.AsyncTask;
/**
 * 点击参数设置弹框的确认按钮时
 * 上传选定的参数到服务器端
 * */
public class SentParamTask extends AsyncTask<String, Void, String>{

	@Override
	protected String doInBackground(String... params) {
		String interval = params[0];
		String result=null;
		HttpGet get = new HttpGet(Config.url+"sendparam?phonenum="+Config.phonenum+"&interval="+interval);
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
