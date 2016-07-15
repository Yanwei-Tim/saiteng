package com.saiteng.st_master;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.saiteng.st_master.conn.ConnSocketServer;
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
import android.telephony.TelephonyManager;
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
	private static Handler handler;
	private SharedPreferences shared;
	private Editor edit;
	private boolean isCheck=true;
	private  String imei;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		MyApplication.getInstance().addActivity(this);
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
	    imei = tm.getDeviceId();
	    Config.imei = imei;
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
				}else if(msg.what==0){
					Toast.makeText(LoginActivity.this,"用户名或密码错误", Toast.LENGTH_LONG).show();
				}else if(msg.what==2){
					MyApplication.getInstance().serverexit(); 
					Toast.makeText(LoginActivity.this,"服务器关闭", Toast.LENGTH_LONG).show();
				}else if(msg.what==3){
					//Toast.makeText(LoginActivity.this,"连接错误，请检查服务器是否正常开启", Toast.LENGTH_SHORT).show();
					mbtn_login.setEnabled(false);
					mbtn_login.setText("连接错误");
					try {
						Thread.sleep(5000);
						new ConnSocketServer(imei).start();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else if(msg.what==4){
					mbtn_login.setEnabled(true);
					mbtn_login.setText("登录");;
				}
			}
		};
		new ConnSocketServer(imei).start();
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
		boolean last_checkbox = sharedPreferences.getBoolean("rememberpw", false);
		//填充上次记录的数据
		if(last_username!=null){
			medit_username.setText(last_username);
		}
		if(last_password!=null&&last_checkbox){
			medit_password.setText(last_password);
		}
		mbox_rememberpwd.setChecked(last_checkbox);
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
	    edit.putBoolean("rememberpw", isCheck);
	    if(isCheck){
	    	edit.putString("password", password);
	    }
	    edit.commit();
		switch(v.getId()){
		case R.id.login:
			if(!"".equals(username)&&!"".equals(password)){
				
				 ConnSocketServer.sendOrder("[ST*"+imei+"*Login,"+username+","+password+"]");
			}else{
				Toast.makeText(LoginActivity.this,"用户名或密码不能为空", Toast.LENGTH_LONG).show();
			}
		}	
	}
	
	public static Handler getHandler(){
		
		return handler;
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void ExieActivity(){
		finish();
	}
	
}
