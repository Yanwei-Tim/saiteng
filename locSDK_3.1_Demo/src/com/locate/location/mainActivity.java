package com.locate.location;

import com.locate.location.services.LocateServices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

public class mainActivity extends Activity{
	private Button   mStartBtn;
	private boolean  mIsStart,flag;
	private Context content;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		content = mainActivity.this;
		mStartBtn = (Button)findViewById(R.id.StartBtn);
		mStartBtn.setText("Á¬½Ó");
		mIsStart = true;
		mStartBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				   Intent intent = new Intent(content,LocateServices.class); 
				   startService(intent); 

			}
		});

	}
		
	

	
		
	

	
}