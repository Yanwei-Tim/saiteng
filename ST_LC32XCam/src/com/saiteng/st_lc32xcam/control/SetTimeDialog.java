package com.saiteng.st_lc32xcam.control;

import com.example.st_lc32xcam.R;
import com.saiteng.st_lc32xcam.control.ControlCmdHelper.ControlCmdListener;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetTimeDialog extends Dialog implements android.view.View.OnClickListener{
	private ControlCmdHelper mControlCmdHelper = new ControlCmdHelper();
	private EditText mtext_time;
	private Button mbtn_PositiveButton,btn_NegativeButton;
	private Context mcontext;
	public SetTimeDialog(Context context) {
		super(context);
		this.mcontext = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settime_dialog);
		initView();
	}

	private void initView() {
		mtext_time =(EditText) findViewById(R.id.time_time);
		mbtn_PositiveButton = (Button) findViewById(R.id.time_PositiveButton);
		btn_NegativeButton = (Button) findViewById(R.id.time_NegativeButton);
		mbtn_PositiveButton.setOnClickListener(this);
		btn_NegativeButton.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		String time = mtext_time.getText().toString();
		if(v.getId()==R.id.time_PositiveButton){
			if(time!=null){
				mControlCmdHelper.sendCmd(ControlCmdHelper.CONTROL_CMD_SETRECORDTIME+time, new ControlCmdListener(){

					@Override
					public void onFailure(int type) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(Object obj) {
						TimeCmdInfo info  = (TimeCmdInfo) obj;
						if(info!=null&&info.getCode()==0){
							Toast.makeText(mcontext, "录像保存时间设置成功", Toast.LENGTH_SHORT).show();
							dismiss();
						}else if(info!=null&&info.getCode()==-2){
							Toast.makeText(mcontext, "时间格式错误", Toast.LENGTH_SHORT).show();
						}
						
					}
					
				}, TimeCmdInfo.class);
			}
			
		}else if(v.getId()==R.id.time_NegativeButton){
			this.dismiss();
		}
		
	}

}
