package com.saiteng.st_master;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.saiteng.st_master.view.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{
	private EditText medit_username,medit_password;
	private Button mbtn_login;
	private Context context;
	private CheckBox mbox_rememberpwd;
	private String username,password;
	private ProgressDialog dialog;//提示框
	private Handler handler;
	private SharedPreferences shared;
	private Editor edit;
	private boolean isCheck=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		context = LoginActivity.this;
		initView();
		shared = getSharedPreferences("lasthistory", Context.MODE_APPEND);
	    edit = shared.edit();
		 handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==1){
					Intent intent = new Intent();
					intent.setClass(context,MainActivity.class);
					context.startActivity(intent);
				}else{
					Toast.makeText(LoginActivity.this,"登录失败", Toast.LENGTH_LONG).show();
				}
			}
			
		};
		
	}
	private void initView() {
		mbtn_login       = (Button) findViewById(R.id.login);
		medit_username   = (EditText) findViewById(R.id.login_username);
		medit_password   = (EditText) findViewById(R.id.login_password);
		mbox_rememberpwd = (CheckBox) findViewById(R.id.remember_pwd);
		SharedPreferences sharedPreferences = getSharedPreferences(
				"lasthistory", Context.MODE_APPEND);
		String last_username=sharedPreferences.getString("username", "");
		String last_password=sharedPreferences.getString("password", "");
		if(last_username!=null){
			medit_username.setText(last_username);
		}
		if(last_password!=null){
			medit_password.setText(last_password);
		}
		mbtn_login.setOnClickListener(this);
		mbox_rememberpwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					isCheck=true;
				}else{
					isCheck=false;
				}
				
			}
		});
		
	}
	@Override
	public void onClick(View v) {
		username = medit_username.getText().toString();
		password = medit_password.getText().toString();
		Config.username = username;
	    Config.pwd      = password;
	    edit.putString("username", username);
	    if(isCheck){
	    	edit.putString("password", password);
	    }
	    edit.commit();
		switch(v.getId()){
		case R.id.login:
			if(!"".equals(username)&&!"".equals(password)){
				new LoginTask().execute();
			}else{
				Toast.makeText(LoginActivity.this,"用户名或密码不能为空", Toast.LENGTH_LONG).show();
			}
		}	
	}
   class LoginTask extends AsyncTask<String, Void, String>{
	   /**
		  * 数据请求前显示dialog。
		  */
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			/**提示框*/
			dialog = new ProgressDialog(context);
			dialog.setTitle("提示！");
			dialog.setMessage("正在登录...");
			dialog.show();
		}
	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... params) {
		String result=null;
		HttpGet get = new HttpGet(Config.url+"loginservice?username="+username+"&password="+password);
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
		dialog.dismiss();
		if("1".equals(result)){
			Message msg=handler.obtainMessage();
			msg.what=1;
			handler.sendMessage(msg);
		}else{
			Message msg=handler.obtainMessage();
			msg.what=0;
			handler.sendMessage(msg);
		}
	}
	   
   }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}
