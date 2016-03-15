package com.saiteng.st_master;

import com.baidu.mapapi.SDKInitializer;
import com.saiteng.st_master.view.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
	private Button mBtn_locate,mBtn_gengzong;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		context = MainActivity.this;
		initView();
	}

	private void initView() {
		mBtn_gengzong = (Button) findViewById(R.id.genzong_btn);
		mBtn_locate   = (Button) findViewById(R.id.main_location_btn);
		mBtn_gengzong.setOnClickListener(this);
		mBtn_locate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.genzong_btn://点击跟踪按钮
			Config.mlocationClient.start();
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, MenuActivity.class);
			MainActivity.this.startActivity(intent);
			break;
		case R.id.main_location_btn://点击定位按钮
			
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			Utils.ExitDialog(context, "确定退出？");
		}
		return true;
	}
}
