package com.saiteng.st_individual;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{
	private EditText mUserName;
	private EditText mPassWord;
	private Button mBtn_login;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_in);
		mBtn_login = (Button) findViewById(R.id.login);
		mUserName = (EditText) findViewById(R.id.username);
		mPassWord = (EditText) findViewById(R.id.password);
		
		mBtn_login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.login){
			String name = mUserName.getText().toString();
			String pass = mPassWord.getText().toString();
			if("admin".equals(name)&&"000000".equals(pass)){
				 Intent intent=new Intent();
				 intent.setClass(this, MainActivity.class);
				 startActivity(intent);
			}else{
				Toast.makeText(LoginActivity.this, "用户名或密码错误，请重新登录", Toast.LENGTH_LONG).show();
			}
		}
		
	}

}
