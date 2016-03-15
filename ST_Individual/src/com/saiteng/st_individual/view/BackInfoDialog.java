package com.saiteng.st_individual.view;

import com.saiteng.st_individual.Config;
import com.saiteng.st_individual.MainActivity;
import com.saiteng.st_individual.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BackInfoDialog extends Dialog {
	private Activity activity;
	private String title1;
	private TextView textViewTitle;
	private Button mButtonOk, mButtonCancle;
	public BackInfoDialog(Context context,String title) {
		super(context, R.style.mpu_dialog);
		this.title1 = title;
		this.activity=(Activity)context;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean);

		textViewTitle = (TextView) findViewById(R.id.base_dialog_title);
		mButtonOk = (Button) findViewById(R.id.base_dialog_set_ok);
		mButtonCancle = (Button) findViewById(R.id.base_dialog_set_cancel);
		textViewTitle.setText(title1);
		
		mButtonCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				BackInfoDialog.this.dismiss();
			}
		});
		mButtonOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Config.mIsLogined=false;
				Config.mIsFristLogined=false;
				Config.ip=null;
				Config.port=null;
				Config.phoneNum=null;
				Config.loginInfo=null;
				Config.medit.clear();
				Config.medit.commit();
				BackInfoDialog.this.dismiss();
			}
		});
	}
}
