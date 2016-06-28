package com.locate.location;

import com.locate.location.services.LocateServices;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class mainActivity extends Activity {
	private Button   mStartBtn;
	private Context context;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mStartBtn = (Button)findViewById(R.id.StartBtn);
		mStartBtn.setText("正在连接");;
		context=mainActivity.this;
		Intent intent = new Intent(context,LocateServices.class); 
		startService(intent);
        finish();

	}
}