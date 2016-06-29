package com.saiteng.st_forensics;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassWord extends Activity{
	private EditText txt_new,txt_confirm;
	private Button btn_ok,btn_cancel;
	private static SharedPreferences shared;
	private Editor edit;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changepassword);
		context = ChangePassWord.this;
		initView();
	}
	
	public static SharedPreferences getShared(){
		
		return shared;
		
	}

	private void initView() {
		//txt_old = (EditText) findViewById(R.id.old_password);
		txt_new = (EditText) findViewById(R.id.new_password);
		txt_confirm = (EditText) findViewById(R.id.confirm_password);
		btn_ok = (Button) findViewById(R.id.ok_change);
		btn_cancel = (Button) findViewById(R.id.cancel_change);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//String old = txt_old.getText().toString();
				String newp = txt_new.getText().toString();
				String con = txt_confirm.getText().toString();
				shared = getSharedPreferences("lasthistory", Context.MODE_APPEND);
				edit = shared.edit();
				if("".equals(newp)||"".equals(con)){
					
					Toast.makeText(ChangePassWord.this, "填写内容不能为空", Toast.LENGTH_SHORT).show();
				
				}
				else if(!newp.equals(con)){
					
					Toast.makeText(ChangePassWord.this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
				
				}
				else{
					Config.password = newp;
					edit.putString("password", Config.password);
					edit.commit();
					Toast.makeText(ChangePassWord.this, "密码修改成功", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
