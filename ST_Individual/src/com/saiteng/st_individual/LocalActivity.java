package com.saiteng.st_individual;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.EditText;

public class LocalActivity extends Activity{
	private EditText mlongitude,latitude;
	private Button mBtn_local;
	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState, persistentState);
		setContentView(R.layout.activity_dingwei);
		findViewById();
	}
	
	private void findViewById() {
		mlongitude = (EditText) findViewById(R.id.jingdu);
		latitude   = (EditText) findViewById(R.id.weidu);
		mBtn_local = (Button) findViewById(R.id.dingwei);
	}

}
