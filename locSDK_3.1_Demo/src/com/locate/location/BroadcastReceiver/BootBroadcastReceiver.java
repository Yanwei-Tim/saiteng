package com.locate.location.BroadcastReceiver;


import com.baidu.location.f;
import com.locate.location.services.LocateServices;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*
 * 监听开机自启动程序
 * **/
public class BootBroadcastReceiver  extends BroadcastReceiver{

	static final String ACTION = "android.intent.action.BOOT_COMPLETED";    
	  
	@Override    
	public void onReceive(Context context, Intent intent) {    
	 if (intent.getAction().equals(ACTION)){    
	   Intent locationIntent=new Intent(context,LocateServices.class);    
	  
	   locationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
	  
	   context.startService(locationIntent); 
	   Log.d("DEBUG", "开机自动服务自动启动...");
	 }    
	}    

}
