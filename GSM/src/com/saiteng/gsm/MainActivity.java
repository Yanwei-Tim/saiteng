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
	       
		 //��̬ע����ն��Ź㲥
	      registerReceiver(smsBroadcastReceiver, intentFilter);
	      
	      //���ݽ��յĶ������ݸ���ui��
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
	  	        	mBtn_status.setText("���ڼ���");
	  	        	
	  	        }else if("BB00155".equals(srt)){
	  	        	int color = getResources().getColor(R.color.stoping);
	  	        	mBtn_status.setBackgroundColor(color);
	  	        	mBtn_status.setText("����ֹͣ");
	  	     
	  	        }else if("BB01055".equals(srt)){//���յ��򿪳ɹ���Ϣ������UI����
	  	        	int color = getResources().getColor(R.color.opening);
	  	        	mBtn_status.setBackgroundColor(color);
	  	        	mBtn_status.setText("����򿪳ɹ�");
	  	    	   
	  	       }else if("BB10055".equals(srt)){//���յ��ر���Ϣ������UI����
	  	    	    int color = getResources().getColor(R.color.closeing);
	  	        	mBtn_status.setBackgroundColor(color);
	  	        	mBtn_status.setText("����رճɹ�");
	  	    	   
	  	       }else if("BB10155".equals(srt)){//���յ���ʼ¼����Ϣ������UI����
	  	    	    int color = getResources().getColor(R.color.recording);
	  	        	mBtn_status.setBackgroundColor(color);
	  	        	mBtn_status.setText("��ʼ¼��");
	  	    	   
	  	       }else if("BB11055".equals(srt)){//���յ�ֹͣ¼����Ϣ������UI����
	  	    	    int color = getResources().getColor(R.color.stoping);
	  	        	mBtn_status.setBackgroundColor(color);
	  	        	mBtn_status.setText("ֹͣ¼��");
	  	       }
	  	    }
	  	 
	  	};
	  	
	  	Config.mhandler=handler;//ȫ�ֱ�����ֵ
	}
	private OnClickListener listener = new OnClickListener(){  
        @Override  
        public void onClick(View v) {  
             phone = mEdit_PhoneNum.getText().toString();
            if("".equals(phone)||isMobile(phone)){//��֤�绰����ĸ�ʽ
            	 
            	Toast.makeText(getApplicationContext(), "����ȷ�ĵ绰����", Toast.LENGTH_SHORT).show();
            	
            	return;
            }
            String context = mSpi_MsgContent.getSelectedItem().toString();//��ȡ����ѡ����ı�����
            
            contextTrue=getSelectContext(context);//�����ı��������÷���ָ����ʽ
            if(!"AA10055".equals(contextTrue)){//�ж��ǲ��Ƿ���ֹͣ¼����Ϣ
	            //���ݷ��͵����ݸ���ui
	            if(Config.mStatusListen){
	            	
	            	mView_status.setVisibility(View.VISIBLE);
	            	
	            	mBtn_status.setVisibility(View.VISIBLE);
	            	
	            	mBtn_status.setText("δ��⵽״̬");
	            	
	            }else if(Config.mIsOpen){
	            	mView_status.setVisibility(View.GONE);
	            	mBtn_status.setVisibility(View.GONE);
	            }else if(Config.mIsStartRec){
	            	mView_status.setVisibility(View.GONE);
	            }else if(Config.mIsStopRec){
	            	mView_status.setVisibility(View.GONE);
	            }
	            SmsManager manager = SmsManager.getDefault();  
	          
	            ArrayList<String> list = manager.divideMessage(contextTrue);  //��Ϊһ���������������ƣ����Ҫ�������Ų��  
	         
	            for(String text:list){  
	           
	            	manager.sendTextMessage(phone, null, text, null, null);  
	            }  
	           
            
             }else {//������ֹͣ¼����Ϣʱ��������ʾ����
            	    if(Config.mIsStopRec){
            	    mView_status.setVisibility(View.GONE);
	            	new AlertDialog.Builder(MainActivity.this).setTitle("ϵͳ��ʾ").setMessage("ȷ��Ҫֹͣ¼����").setPositiveButton("ȷ��",new DialogInterface.OnClickListener()
	            	
	            	{//���ȷ����ť  
	            		@Override  
	            		public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�  
	            			
	            			 SmsManager manager = SmsManager.getDefault();  
	           	          
	         	            ArrayList<String> list = manager.divideMessage(contextTrue);  //��Ϊһ���������������ƣ����Ҫ�������Ų��  
	         	         
	         	            for(String text:list){  
	         	           
	         	            	manager.sendTextMessage(phone, null, text, null, null);  
	         	            }  
	                     }  
	                 }).setNegativeButton("����",new DialogInterface.OnClickListener()
	               
	                 {//��ӷ��ذ�ť  
	                @Override  
	                public void onClick(DialogInterface dialog, int which) {//���ذ�ť��Ӧ�¼� 
	                     }  
	              
	                 }).show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի��� 
	            }
            } 
        }

	};

	protected String getSelectContext(String str) {
		String context=null;
	    if("��".equals(str)){
	    	context="AA00055";
	    	Config.mIsOpen=true;
	    	Config.mStatusListen=false;
	    }else if("�ر�".equals(str)){
	    	context="AA00155";
	    	Config.mIsClose=true;
	    }else if("��ʼ¼��".equals(str)){
	    	context="AA01055";
	    	Config.mIsStartRec=true;
	    }else if("ֹͣ¼��".equals(str)){
	    	context="AA10055";
	    	Config.mIsStopRec=true;
	    }else if("״̬���".equals(str)){
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
