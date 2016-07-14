package com.saiteng.st_master.view;

import com.saiteng.st_master.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;


public class Shake_Dialog extends Dialog{
	public Shake_Dialog(Context context) {
		super(context);
		this.context = context;
	}
	private Context context;
	private Button btn_shakeOn,btn_shakeOff;
	private RadioButton btn_radioOn,btn_radioOff;
    private boolean ischeck,nocheck;
    private Handler handle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_shake);
		initView();
	    handle = new Handler(){
		   @Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
		}
		};
	}
	private void initView() {
		btn_shakeOn  =  (Button) findViewById(R.id.btn_shakeon);//确认
		btn_shakeOff = (Button) findViewById(R.id.btn_shakeoff);//取消
		btn_radioOn  =  (RadioButton) findViewById(R.id.shakeon);
		btn_radioOff = (RadioButton) findViewById(R.id.shakeoff);
		btn_radioOn.setChecked(true);
		ischeck=true;
		btn_radioOn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					if(nocheck){
						btn_radioOff.setChecked(false);
					}
					ischeck=true;
					
				}
			}
		});
		btn_radioOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				   //取消btn_radioOn的选中状态
					if(isChecked){
						if(ischeck){
							btn_radioOn.setChecked(false);
					    }
						nocheck=true;	
				}
				
			}
		});
		btn_shakeOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
		btn_shakeOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Shake_Dialog.this.dismiss();
				
			}
		});
	}

}
