package com.locate.location.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CloseBroadcastReceiver extends BroadcastReceiver{
	static final String ACTION = "android.intent.action.SCREEN_OFF"; 
	@Override
	public void onReceive(Context context, Intent intent) {
		 if (intent.getAction().equals(ACTION)){
			 
			 Log.i("close","SCREEN CLOSE");
		 }
		
	}

}
