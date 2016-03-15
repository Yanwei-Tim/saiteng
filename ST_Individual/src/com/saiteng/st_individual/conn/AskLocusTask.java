package com.saiteng.st_individual.conn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.saiteng.st_individual.Config;
import com.saiteng.st_individual.LocusDatilsActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
/**�������ݿ�õ����豸ĳ��ʱ���ڵ�������Ϣ���ڵ�ͼ���Թ켣����ʽչʾ*/
public class AskLocusTask extends AsyncTask<String, Void, String>{
	private String url=null;
	private ProgressDialog dialog;//��ʾ��
	/**
	  * ��������ǰ��ʾdialog��
	  */
	@Override
	public void onPreExecute() {
		super.onPreExecute();
		/**��ʾ��*/
		dialog = new ProgressDialog(Config.Locus_mcontext);
		dialog.setTitle("��ʾ��");
		dialog.setMessage("���ڼ�����...");
	}
	@Override
	protected String doInBackground(String... params) {
		this.url=params[0];
		return RequestData("http://"+Config.ip+":"+Config.port+"/NA721/locus?phonenum="+url);
	}
	
	@Override
	public void onPostExecute(String result) {
		dialog.dismiss();
		if("NetworkException".equals(result)){
			Toast.makeText(Config.Locus_mcontext, "�����쳣", Toast.LENGTH_LONG).show();
			
		}else if("Exception".equals(result)){
			Toast.makeText(Config.Locus_mcontext, "�����쳣", Toast.LENGTH_LONG).show();	
		}else if("null".equals(result)){
			Toast.makeText(Config.Locus_mcontext,"�豸"+url+"���޶�λ����",Toast.LENGTH_LONG).show();}
		else{
			String[] array = result.split(",");
			Intent intent = new Intent();
			intent.putExtra("details", array);
			intent.setClass(Config.Locus_mcontext, LocusDatilsActivity.class);
			Config.Locus_mcontext.startActivity(intent);
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
