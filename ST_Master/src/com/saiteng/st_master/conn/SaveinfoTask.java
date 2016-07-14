package com.saiteng.st_master.conn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.saiteng.st_master.Config;
import com.saiteng.st_master.view.ST_InfoDialog;

import android.os.AsyncTask;
import android.os.Message;
import android.widget.Toast;

public class SaveinfoTask extends AsyncTask<String, Void, String>{
    private String info=null;
	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... params) {
		this.info = params[0];
		String result=null;
		HttpGet get = new HttpGet(Config.url+"insert?info="+info);
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
	public void onPostExecute(String result) {
		if("NetworkException".equals(result)){
			Toast.makeText(Config.mManagecontext, "确认服务器正常运行", Toast.LENGTH_SHORT).show();
			Config.InfoDialog.dismiss();
		}else if("Exception".equals(result)){
			Toast.makeText(Config.mManagecontext, "确认ip和端口正确或者服务器正常运行", Toast.LENGTH_SHORT).show();
			Config.InfoDialog.dismiss();
		}else if("addsuccess".equals(result)){
	         Toast.makeText(Config.mManagecontext, "添加成功", Toast.LENGTH_SHORT).show();
	         Config.InfoDialog.dismiss();
		}else{
			Toast.makeText(Config.mManagecontext, "添加失败，请确认该设备正常登录或开启", Toast.LENGTH_SHORT).show();
			Config.InfoDialog.dismiss();
		}
	}

}
