package com.saiteng.gsm.BroadcastReceiver;


import java.util.List;

import com.saiteng.gsm.Config;
import com.saiteng.gsm.MainActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.AppTask;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSBroadcastReceiver extends BroadcastReceiver{
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		 Object[] pduses= (Object[])intent.getExtras().get("pdus");  
	        for(Object pdus: pduses){  
	            byte[] pdusmessage = (byte[])pdus;  
	            SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);  
	            String mobile = sms.getOriginatingAddress();//���Ͷ��ŵ��ֻ�����  
	            String content = sms.getMessageBody(); //�������� 
	            Message message = Config.mhandler.obtainMessage();
	            message.obj= content;
	            Config.mhandler.sendMessage(message);
	            if("000000".equals(content)){
	            	Intent GsmMain=new Intent(context,MainActivity.class); 
		            GsmMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            context.startActivity(GsmMain);
		            Toast.makeText(context, "�յ���������:"+content, Toast.LENGTH_LONG).show();
	            }
	            Toast.makeText(context, "�յ�����:"+content, Toast.LENGTH_LONG).show();
	        }
		
	}

	

	

	

	

	

	
}
