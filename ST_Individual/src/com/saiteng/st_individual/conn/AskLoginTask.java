package com.saiteng.st_individual.conn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.saiteng.st_individual.Config;

import android.os.AsyncTask;
import android.os.Message;
import android.widget.Toast;

public class AskLoginTask extends AsyncTask<String, Void, String>{
	private String ip;
	private String port;
	private String url;

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... params) {
		String result=null;
		this.ip=params[0];
		this.port=params[1];
		url="http://"+ip+":"+port+"/NA721/login";
		HttpGet get = new HttpGet(url);
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
	public void onPostExecute(String result) {
		if("NetworkException".equals(result)){
			Toast.makeText(Config.Login_mcontext, "确认服务器开启", Toast.LENGTH_LONG).show();
			
		}else if("Exception".equals(result)){
			Toast.makeText(Config.Login_mcontext, "确认ip和端口正确", Toast.LENGTH_LONG).show();
			
		}else if("loginsuccess".equals(result)){
			 Message message = Config.mhandler.obtainMessage();
	         message.obj= "loginsuccess";
	         Config.mhandler.sendMessage(message);
		}
	}

}
