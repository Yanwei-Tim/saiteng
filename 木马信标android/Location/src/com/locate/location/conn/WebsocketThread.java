package com.locate.location.conn;

import java.util.ArrayList;
import com.locate.location.services.Config;
import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

public class WebsocketThread extends Thread{
	final WebSocketConnection wsc = new WebSocketConnection();
	protected static final String TAG = "WebSocket";
	
	private ArrayList<String> list1;
	private String msg1="";
	private String Equipment="";
    
	public WebsocketThread(ArrayList<String> list) {
		if(list.size()!=0){
		this.list1 = list;
		for(int i=0;i<list1.size();i++){
			this.msg1=msg1+list1.get(i)+",";
		}
		this.Equipment = list.get(3).toString();
		}
	}
	
	public void run() {  
		
		try {
			wsc.connect(Config.mWebsock+Equipment, new WebSocketConnectionHandler(){

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
					System.out.println("onOpen");
					wsc.sendTextMessage(msg1);//发送信息到服务器端
					//int sleeptime = Config.msetTime
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					wsc.disconnect();
					
				}

				 // 文本消息事件  
				@Override
				public void onRawTextMessage(byte[] payload) {
					System.out.println("onRawTextMessage size="+payload.length);
				}

				/**
				  *根据服务器端下发的参数调整定位策略 
				  */
				@Override
				public void onTextMessage(String payload) {
					if("reset".equals(payload)){
						Config.mIsSetOption=true;
						Config.mIsSetOption=true;
						Config.mIsDestroy=false;
						Config.mIsDisconn=false;
						Config.mIsForceLocate=false;
						Config.mIsForceTrans=false;
						Config.mIsOpenGps=false;
						Config.mIsSetOption=false;
						Config.mBaiduGps=false;
					}
					if("disconn".equals(payload)){//断开与服务器的连接
						wsc.disconnect();//断开连接
					}
					if("forcetrans".equals(payload)){
						Config.mIsSetOption=true;
						Config.mIsForceTrans=true;//设置为强制传输，即在数据流量的情况下也传输数据到服务器
					}
					if("forceLocate".equals(payload)){
						Config.mIsOpenGps=true;//设置强制定位：需要目标手机获得root权限，强制开启gps服务（目前调整为默认gps服务是开启的，设置定位策略主动使用gps定位方式）
					}
					if("60".equals(payload)){
						Config.mIsSetOption=true;
						Config.msetTime = 1000*60;//设置定位间隔为1分钟
					}
					if("600".equals(payload)){
						Config.mIsSetOption=true;
						Config.msetTime = 1000*60*10;
					}
					if("3600".equals(payload)){
						Config.mIsSetOption=true;
						Config.msetTime = 1000*60*10;
					}
					if("20".equals(payload)){
						Config.mIsSetOption=true;
						Config.msetTime = 1000*20;
					}
					if("destroy".equals(payload)){
						Config.mIsDestroy=true;
					}
					System.out.println("onTextMessage"+payload);
					Log.i("TAG",payload);
				}
			});
		} catch (WebSocketException e) {
			e.printStackTrace();
		}
	}

}
