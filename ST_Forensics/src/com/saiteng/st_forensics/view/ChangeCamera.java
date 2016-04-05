package com.saiteng.st_forensics.view;

import com.saiteng.st_forensics.Config;
import com.saiteng.st_forensics.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ChangeCamera extends Dialog{
	private RadioGroup radiogroup;
	private Button btn_cancel,btn_ok;;
    private boolean mCreamer=false;
	public ChangeCamera(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);
		initView();
	}

	private void initView() {
		radiogroup = (RadioGroup)  findViewById(R.id.radioGroup1);
	
		btn_cancel = (Button)      findViewById(R.id.cancel);
		btn_ok     = (Button)      findViewById(R.id.ok);
		if(Config.defaultcamera){
		    radiogroup.check(R.id.radio_front);
		}else{
		    radiogroup.check(R.id.radio_back);
		}
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Config.defaultcamera=mCreamer;
				if(mCreamer){
					Config.madpterhandler.sendEmptyMessage(1);
				}else{
					Config.madpterhandler.sendEmptyMessage(0);
						
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
				if(checkedId==R.id.radio_front){
					mCreamer=true;
				}else if(checkedId==R.id.radio_back){
					mCreamer=false;
					
				}
			}
		});
	}
}
