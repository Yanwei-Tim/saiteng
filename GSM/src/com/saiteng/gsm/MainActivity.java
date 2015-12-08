package com.saiteng.gsm;

import android.telephony.SmsMessage;
import android.telephony.gsm.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.saiteng.gsm.BroadcastReceiver.SMSBroadcastReceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button mBtn_SendMessage,mBtn_status;
	
	private EditText mEdit_PhoneNum,mEdit_MsgContent;
	
	private TextView mView_status;
	
	private Spinner mSpi_MsgContent;
	
	private IntentFilter intentFilter;
   
	private SMSBroadcastReceiver smsBroadcastReceiver;
	
	private String phone,contextTrue;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mBtn_SendMessage =(Button) findViewById(R.id.send_sms_button);
		
		mEdit_PhoneNum =(EditText) findViewById(R.id.phone_number_editText);
		
		mView_status = (TextView) findViewById(R.id.text_status);
		
		mBtn_status=(Button) findViewById(R.id.button_status);
		
		mSpi_MsgContent=(Spinner) findViewById(R.id.sms_content_editText);
		
		mBtn_SendMessage.setOnClickListener(listener);
		
		 intentFilter=new IntentFilter();
	       
		 intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
	       
		 smsBroadcastReceiver=new SMSBroadcastReceiver();
	       
		 //动态注册接收短信广播
	      registerReceiver(smsBroadcastReceiver, intentFilter);
	      
	      //根据接收的短信内容更新ui。
	      Handler handler = new Handler() {
	  	    @SuppressWarnings("deprecation")
			@Override
	  	    public void handleMessage(Message msg) {
	  	        super.handleMessage(msg);
	  	        String srt = msg.obj.toString();
	  	        mView_status.setVisibility(View.VISIBLE);
            	mBtn_status.setVisibility(View.VISIBLE);
	  	        if("BB00055".equals(srt)){
	  	        	int color = getResources().getColor(R.color.recording);
	  	        	mBtn_status.setBackgroundColor(color);
	  	        	mBtn_status.setText("正在监听");
	  	        	
	  	        }else if("BB00155".equals(srt)){
	  	        	int color = getResources().getColor(R.color.stoping);
	  	        	mBtn_status.setBackgroundColor(color);
	  	        	mBtn_status.setText("监听停止");
	  	     
	  	        }else if("BB01055".equals(srt)){//接收到打开成功信息，更新UI操作
	  	        	int color = getResources().getColor(R.color.opening);
	  	        	mBtn_status.setBackgroundColor(color);
	  	        	mBtn_status.setText("服务打开成功");
	  	    	   
	  	       }else if("BB10055".equals(srt)){//接收到关闭信息，更新UI操作
	  	    	    int color = getResources().getColor(R.color.closeing);
	  	        	mBtn_status.setBackgroundColor(color);
	  	        	mBtn_status.setText("服务关闭成功");
	  	    	   
	  	       }else if("BB10155".equals(srt)){//接收到开始录像信息，更新UI操作
	  	    	    int color = getResources().getColor(R.color.recording);
	  	        	mBtn_status.setBackgroundColor(color);
	  	        	mBtn_status.setText("开始录音");
	  	    	   
	  	       }else if("BB11055".equals(srt)){//接收到停止录像信息，更新UI操作
	  	    	    int color = getResources().getColor(R.color.stoping);
	  	        	mBtn_status.setBackgroundColor(color);
	  	        	mBtn_status.setText("停止录音");
	  	       }
	  	    }
	  	 
	  	};
	  	
	  	Config.mhandler=handler;//全局变量赋值
	}
	private OnClickListener listener = new OnClickListener(){  
        @Override  
        public void onClick(View v) {  
             phone = mEdit_PhoneNum.getText().toString();
            if("".equals(phone)||isMobile(phone)){//验证电话号码的格式
            	 
            	Toast.makeText(getApplicationContext(), "请正确的电话号码", Toast.LENGTH_SHORT).show();
            	
            	return;
            }
            String context = mSpi_MsgContent.getSelectedItem().toString();//获取下拉选项的文本内容
            
            contextTrue=getSelectContext(context);//根据文本内容设置发送指令形式
            if(!"AA10055".equals(contextTrue)){//判断是不是发送停止录像信息
	            //根据发送的内容更新ui
	            if(Config.mStatusListen){
	            	
	            	mView_status.setVisibility(View.VISIBLE);
	            	
	            	mBtn_status.setVisibility(View.VISIBLE);
	            	
	            	mBtn_status.setText("未检测到状态");
	            	
	            }else if(Config.mIsOpen){
	            	mView_status.setVisibility(View.GONE);
	            	mBtn_status.setVisibility(View.GONE);
	            }else if(Config.mIsStartRec){
	            	mView_status.setVisibility(View.GONE);
	            }else if(Config.mIsStopRec){
	            	mView_status.setVisibility(View.GONE);
	            }
	            SmsManager manager = SmsManager.getDefault();  
	          
	            ArrayList<String> list = manager.divideMessage(contextTrue);  //因为一条短信有字数限制，因此要将长短信拆分  
	         
	            for(String text:list){  
	           
	            	manager.sendTextMessage(phone, null, text, null, null);  
	            }  
	           
            
             }else {//当发送停止录像信息时必须做提示处理
            	    if(Config.mIsStopRec){
            	    mView_status.setVisibility(View.GONE);
	            	new AlertDialog.Builder(MainActivity.this).setTitle("系统提示").setMessage("确定要停止录音吗").setPositiveButton("确定",new DialogInterface.OnClickListener()
	            	
	            	{//添加确定按钮  
	            		@Override  
	            		public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件  
	            			
	            			 SmsManager manager = SmsManager.getDefault();  
	           	          
	         	            ArrayList<String> list = manager.divideMessage(contextTrue);  //因为一条短信有字数限制，因此要将长短信拆分  
	         	         
	         	            for(String text:list){  
	         	           
	         	            	manager.sendTextMessage(phone, null, text, null, null);  
	         	            }  
	                     }  
	                 }).setNegativeButton("返回",new DialogInterface.OnClickListener()
	               
	                 {//添加返回按钮  
	                @Override  
	                public void onClick(DialogInterface dialog, int which) {//返回按钮响应事件 
	                     }  
	              
	                 }).show();//在按键响应事件中显示此对话框 
	            }
            } 
        }

	};

	protected String getSelectContext(String str) {
		String context=null;
	    if("打开".equals(str)){
	    	context="AA00055";
	    	Config.mIsOpen=true;
	    	Config.mStatusListen=false;
	    }else if("关闭".equals(str)){
	    	context="AA00155";
	    	Config.mIsClose=true;
	    }else if("开始录音".equals(str)){
	    	context="AA01055";
	    	Config.mIsStartRec=true;
	    }else if("停止录音".equals(str)){
	    	context="AA10055";
	    	Config.mIsStopRec=true;
	    }else if("状态监测".equals(str)){
	    	context="AA10155";
	    	Config.mStatusListen=true;
	    }
		return context;
	}

	protected boolean isMobile(String phone) {
		Pattern pattern = Pattern.compile("1[0-9]{10}");
		Matcher matcher = pattern.matcher(phone);
		if (matcher.matches()) {
			return false;
		} else {
			return true;
		}
	}
	
	

}
