package com.saiteng.st_forensics.view;

import com.saiteng.st_forensics.Config;
import com.saiteng.st_forensics.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class RecordTime extends Dialog{
   private Spinner txt_redordtime;
   private Button btn_recoedok,btn_recordcancel;
	public RecordTime(Context context) {
		super(context);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_recordtime);
		initView();
	}
	private void initView() {
		txt_redordtime   = (Spinner)  findViewById(R.id.recordtime_txt);
		btn_recoedok     = (Button)   findViewById(R.id.recordtime_ok);
		btn_recordcancel = (Button)   findViewById(R.id.recordtime_cancel);
		txt_redordtime.setSelection(1);
		btn_recoedok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String time = txt_redordtime.getSelectedItem().toString();
				if("5分钟".equals(time)){
					Config.recordtime=5;
					Config.madpterhandler.sendEmptyMessage(5);
				}if("15分钟".equals(time)){
					Config.recordtime=15;
					Config.madpterhandler.sendEmptyMessage(6);
				}if("30分钟".equals(time)){
					Config.recordtime=30;
					Config.madpterhandler.sendEmptyMessage(7);
				}if("1小时".equals(time)){
					Config.recordtime=60;
					Config.madpterhandler.sendEmptyMessage(8);
				}
				
				RecordTime.this.dismiss();
			}
		});
		btn_recordcancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RecordTime.this.dismiss();
			}
		});
	}

}
