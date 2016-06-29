package com.saiteng.st_forensics.view;

import com.saiteng.st_forensics.Config;
import com.saiteng.st_forensics.MainActivity;
import com.saiteng.st_forensics.R;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Comfir_Dialog extends Dialog{
	private Context context;
	private EditText txtPassword;
	private Button btn_ok,btn_cancel;
	private Editor edit;
	public Comfir_Dialog(Context context) {
		super(context);
		this.context = context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		btn_ok = (Button) findViewById(R.id.password_ok);
		btn_cancel = (Button) findViewById(R.id.password_cancel);
		txtPassword = (EditText) findViewById(R.id.password);
		SharedPreferences shared  = context.getSharedPreferences("lasthistory", Context.MODE_APPEND);
		edit = shared.edit();
		if(shared.getString("password", "")!=""){
			Config.password = shared.getString("password", "");
		}
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = txtPassword.getText().toString();
				if(Config.password.equals(password)){
					MainActivity.getHandler().sendEmptyMessage(1);
					Comfir_Dialog.this.dismiss();
					edit.putBoolean("Login",true);
				}else{
					edit.putBoolean("Login",false);
					Toast.makeText(context, "√‹¬Î¥ÌŒÛ£¨«Î÷ÿ–¬ ‰»Î", Toast.LENGTH_SHORT).show();
				}
				edit.commit();
			}
		});
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.getHandler().sendEmptyMessage(0);
				Comfir_Dialog.this.dismiss();
			}
		});
	}
}
