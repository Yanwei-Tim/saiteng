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

public class ChoceShakeValue extends Dialog{
	private RadioGroup radiogroup2;
	private Button btn_cancel;

	public ChoceShakeValue(Context context) {
		super(context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_shakevalue);
		initView();
	}

	private void initView() {
		radiogroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
		btn_cancel = (Button) findViewById(R.id.cancel1);
		if(Config.medumValue==11){
			radiogroup2.check(R.id.radio_small);
		}else if(Config.medumValue==15){
			radiogroup2.check(R.id.radio_middle);
		}else
			radiogroup2.check(R.id.radio_big);
		radiogroup2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
			  if(checkedId==R.id.radio_small){
				  Config.medumValue=11;
				  Config.madpterhandler.sendEmptyMessage(2);
			  }if(checkedId==R.id.radio_middle){
				  Config.medumValue=15;
				  Config.madpterhandler.sendEmptyMessage(3);
			  }if(checkedId==R.id.radio_big){
				  Config.medumValue=19;
				  Config.madpterhandler.sendEmptyMessage(4);
			  }
			  ChoceShakeValue.this.dismiss();
			}
		});
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ChoceShakeValue.this.dismiss();
				
			}
		});
		
	}

}
