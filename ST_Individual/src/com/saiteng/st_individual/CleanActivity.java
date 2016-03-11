package com.saiteng.st_individual;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CleanActivity extends Activity implements OnClickListener{
   private Button mBtn_Ok,mBtn_Cannel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean);
		mBtn_Ok = (Button) findViewById(R.id.base_dialog_set_ok);
		mBtn_Cannel = (Button) findViewById(R.id.base_dialog_set_cancel);
		mBtn_Ok.setOnClickListener(this);
		mBtn_Cannel.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.base_dialog_set_ok:
			
			break;
		case R.id.base_dialog_set_cancel:
			
			break;
		}
		
	}
}
