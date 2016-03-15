package com.saiteng.st_master;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Menu_ChangepwdActivity extends Activity implements OnClickListener{
	private TextView mView_title;
	private Button mbtn_confirm;
	private EditText medit_oldpwd,medit_newpwd,medit_confirmpwd;
	private String oldpwd,newpwd,confirmpwd;
	private Context context;
	private Handler handler=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_changepwd);
		context = Menu_ChangepwdActivity.this;
		initView();
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1){
					Toast.makeText(context, "修改密码成功", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(context, "修改密码失败", Toast.LENGTH_LONG).show();
				}
			}
		};
		
	}
	private void initView() {
		mView_title      = (TextView) findViewById(R.id.action_bar_preview_txt);
		mbtn_confirm     = (Button) findViewById(R.id.confirm_change);
		medit_oldpwd     = (EditText) findViewById(R.id.old_pwd);
		medit_newpwd     = (EditText) findViewById(R.id.new_pwd);
		medit_confirmpwd = (EditText) findViewById(R.id.confirm_pwd);
		mView_title.setText("修改登录密码");
		mbtn_confirm.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		
		oldpwd = medit_oldpwd.getText().toString();
		newpwd = medit_newpwd.getText().toString();
		confirmpwd = medit_confirmpwd.getText().toString();
		switch(v.getId()){
		case R.id.confirm_change :
			if(!"".equals(oldpwd)&&!"".equals(newpwd)&&!"".equals(confirmpwd)){
			    new ChangepwdTak().execute();;
			}else if(!oldpwd.equals(Config.pwd)){
				Toast.makeText(context, "输入的旧密码不正确", Toast.LENGTH_LONG).show();
			}else if(!newpwd.equals(confirmpwd)){
				Toast.makeText(context,"输入的新密码和确认密码不一致", Toast.LENGTH_LONG).show();
			}else if(oldpwd==newpwd){
				Toast.makeText(context,"新旧密码一样", Toast.LENGTH_LONG).show();
			}
			break;
		}
		
	}
   class ChangepwdTak extends AsyncTask<String, Void, String>{

	@Override
	protected String doInBackground(String... params) {
		String result=null;
		HttpGet get = new HttpGet(Config.url+"changepwd?newpwd="+newpwd+"&username="+Config.username);
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
		if("1".equals(result)){
			Message msg = handler.obtainMessage();
			msg.what = 1;
			handler.sendMessage(msg);
		}else{
			Message msg = handler.obtainMessage();
			msg.what = 0;
			handler.sendMessage(msg);
		}
	}
	   
   }
}
