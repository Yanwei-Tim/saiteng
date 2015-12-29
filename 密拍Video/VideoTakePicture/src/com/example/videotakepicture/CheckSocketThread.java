package com.example.videotakepicture;

import android.os.Message;
import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

public class CheckSocketThread extends Thread{
	final WebSocketConnection wsc = new WebSocketConnection();
	protected static final String TAG = "WebSocket";
	private String imei;
	public CheckSocketThread(String imei){
		this.imei=imei;
	}
	
	@Override
	public void run() {
		
		super.run();
		
		try {
			wsc.connect("ws://192.168.0.62:8080/ProvingWebsocket/regist?deviceEIMI="+imei,
					
					new WebSocketConnectionHandler(){
				
				// 二进制消息事件  
				@Override
				public void onBinaryMessage(byte[] payload) {
					System.out.println("onBinaryMessage size="+payload.length);
				}

				
				@Override
				public void onClose(int code, String reason) {
					super.onClose(code, reason);
					Log.i(TAG,"onClose reason="+reason);
				}

				
				@Override
				public void onOpen() {
					Config.IsConn=true;
					System.out.println("onOpen");
					wsc.sendTextMessage(imei);//发送信息到服务器端
					//wsc.disconnect();
					
				}

				 // 文本消息事件  
				@Override
				public void onRawTextMessage(byte[] payload) {
					
					System.out.println("onRawTextMessage size="+payload.length);
				}

				/**
				  *根据服务器的消息判断验证是否成功
				  */
				@Override
				public void onTextMessage(String payload) {
					if("0".equals(payload)){//验证成功
						 Message message = Config.mhandler.obtainMessage();
				         message.obj= "allow";
				         Config.mhandler.sendMessage(message);
					}else if("1".equals(payload)){//验证失败
						 Message message = Config.mhandler.obtainMessage();
				         message.obj= "forbidden";
				         Config.mhandler.sendMessage(message);
					}else{
						
					 }
					System.out.println("onTextMessage"+payload);
					Log.i("TAG",payload);
					wsc.disconnect();
				}
			});
		} catch (WebSocketException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!Config.IsConn){
			 Message message = Config.mhandler.obtainMessage();
	         message.obj= "serverDis";
	         Config.mhandler.sendMessage(message);
		}
	}

}
