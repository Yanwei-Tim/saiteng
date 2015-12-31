package com.saiteng.smartlisten;

import com.saiteng.smartlisten.service.MPUListenService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent mMPUServiceIntent = new Intent(this, MPUListenService.class);
		startService(mMPUServiceIntent);
	}

}
