package com.saiteng.st_lc32xcam.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.st_lc32xcam.MainActivity;
import com.example.st_lc32xcam.MyApplication;

import android.os.AsyncTask;

public class RecordingSDCard extends AsyncTask<String, Void, String>{
	private String VideoName =null;
	private String result=null;
	public  RecordingSDCard() {
		long time = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date d1 = new Date(time);
		VideoName = "Video"+format.format(d1);
	}

	@Override
	protected String doInBackground(String... params) {
		HttpGet get = new HttpGet(SmartCamDefine.SMARTCAM_URL+"/api/trio_rec?val="+VideoName);
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
	public void onPostExecute(String  result) {
//		if(result.length()>100){
//			String newresult =result.substring(result.indexOf("{")+1, result.indexOf("}"));
//			// "code": 0, "value": "Stop record"    "code": -10, "value": "sdcard exist" 
//			String[] arr = newresult.split(",")[0].split(":");
//			String[] arr1 = newresult.split(",")[1].split(":");
//			arr1[1].length();
//			if(arr1[1].contains("Stop record")){
//				MainActivity.getUpdateHandler().sendEmptyMessage(1);
//			}else if(arr1[1].contains("sdcard exist")){
//				MainActivity.getUpdateHandler().sendEmptyMessage(2);
//			}else {
//				MainActivity.getUpdateHandler().sendEmptyMessage(0);
//			}
//			
//		}else{
//			
//		}
		
	}
}
