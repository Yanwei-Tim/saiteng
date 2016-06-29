package com.saiteng.st_forensics.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.saiteng.st_forensics.Config;
import com.saiteng.st_forensics.R;

public class ChangeCamera extends Dialog{
	private RadioGroup radiogroup;
	private Button btn_cancel,btn_ok;;
    private boolean mCreamer=false;
	private Editor edit;
	private SharedPreferences shared;
	private Context mcontext;
	
	public ChangeCamera(Context context) {
		super(context);
		this.mcontext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);
		shared  = mcontext.getSharedPreferences("lasthistory", Context.MODE_APPEND);
		edit = shared.edit();
		initView();
	}

	private void initView() {
		radiogroup = (RadioGroup)  findViewById(R.id.radioGroup1);
		radiogroup.check(R.id.radio_front);
		btn_cancel = (Button)      findViewById(R.id.cancel);
		btn_ok     = (Button)      findViewById(R.id.ok);
		radiogroup.check(R.id.radio_front);
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = radiogroup.getCheckedRadioButtonId();
				if (id==R.id.radio_front){
					Config.madpterhandler.sendEmptyMessage(1);
					Config.defaultcamera=false;
				}else {
					Config.madpterhandler.sendEmptyMessage(0);
					Config.defaultcamera=true;
				}
				ChangeCamera.this.dismiss();
			}
		});
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ChangeCamera.this.dismiss();
			}
		});
		radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.radio_front) {
					edit.putBoolean("camera", true);
				} else if (checkedId == R.id.radio_back) {
					edit.putBoolean("camera", false);
				}
			}
		});
	}
}
